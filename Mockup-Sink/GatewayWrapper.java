/**
 * 
 */
package learningnetwork;

import java.io.IOException;

import main.LearningLayer;
import net.tinyos.message.Message;
import net.tinyos.message.MessageListener;
import net.tinyos.message.MoteIF;
import net.tinyos.packet.BuildSource;
import net.tinyos.packet.PhoenixSource;
import net.tinyos.util.PrintStreamMessenger;

/**
 * @author bacciu
 *
 */
public class GatewayWrapper implements GatewayInterface,MessageListener {

	static final int SINK_ADDR = 0;
	static final int MAX_SEQ = 255;
	static final int PKT_SIZE = 109;
	static final int MATRIX_PAYLOAD = 100;
    static final int FLOAT_ELEMENTS = MATRIX_PAYLOAD/4;
	static final int AM_LEARNING_NOTIFICATION_MSG = 20;
	static int seqNum;
	
	private LNMessageListenerInterface ln;
	private MoteIF moteIF; //<Standard MoteIF object
	private	PhoenixSource phoenix;
	//NOTICE: if you use Windows or OSX update the following line according to the serial interface
	private String source = "/dev/ttyUSB0:telosb";

	private int tot_pkts;
	private int cur_pkt;
	private int matrix_len;
	short[] orig_matrix;
    float[] orig_float_matrix;
	short matrix_seq;
	short next_pkt_ack;
	boolean last_pkt;
	
	LearningModuleCmdMsg matrix_msg;
	LearningModuleFloatCmdMsg float_matrix_msg;

	
	public GatewayWrapper() {
		super();	
		
		seqNum = 0;
		phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
		//Init the MoteIF object and register the listener to the learning network message
		moteIF = new MoteIF(phoenix);
		
		System.out.println("Gateway Wrapper started.");
		LearningLayer.display_message(LearningLayer.LOW_PRIORITY, "[GATEWAY Wrapper]: Gateway Wrapper started.");
		
		//register the moteIF as listener for the incoming messages 
		this.moteIF.registerListener(new LearningNotificationMsg(), this);
		this.moteIF.registerListener(new LearningModuleDownMsg(), this);
		this.moteIF.registerListener(new LearningOutputMsg(), this);			
	}
	
	// Registers the Learning Network Wrapper that receives the LN messages
	public void registerListener(LNMessageListenerInterface l){
		this.ln = l;		
	}
	

	private short nextSeqNumber() {
		seqNum = (seqNum + 1)%MAX_SEQ;
		return (short) seqNum;
	}
	
	/* (non-Javadoc)
	 * @see learningnetwork.GatewayInterface#send(generics.LNMessages, int, int)
	 */
	@Override
	public int send(byte type, int nodeID, int netID) {
		
		//Message Packing (netid_cmd_msg)
		NetIdCmdMsg msg = new NetIdCmdMsg();	
		msg.set_type(type);
		msg.set_dest_mote_addr(get_device_address(nodeID));
		msg.set_dest_island_addr(get_island_address(nodeID));
		msg.set_netId(netID);
		short seq = nextSeqNumber();
		msg.set_seq_num(seq);
		
		//Send the message		
		try{
			LearningLayer.display_message(LearningLayer.LOW_PRIORITY, "[GATEWAY Wrapper]: Sending a message");  
			//now we assume that the wrapper is connected to the sink, so we send to the sink
			moteIF.send(SINK_ADDR, msg); 
		}
		catch (IOException ioe) {
			LearningLayer.display_message(LearningLayer.HIGH_PRIORITY, "[GATEWAY Wrapper] Warning: Got IOException sending message: "+ioe);
			ioe.printStackTrace();
		}
		
		return seq;
	}

	/* (non-Javadoc)
	 * @see learningnetwork.GatewayInterface#send(generics.LNMessages, int)
	 */
	@Override
	public int send(byte type, int arg) {

		//Message Packing (broadcast_cmd_msg)
		BroadcastCmdMsg msg = new BroadcastCmdMsg();	
		msg.set_type(type);
		msg.set_clock(arg);
		short seq = nextSeqNumber();
		msg.set_seq_num(seq);		
		
		//Send the message		
		try{
			LearningLayer.display_message(LearningLayer.LOW_PRIORITY, "[GATEWAY Wrapper]: Sending a message");  
			//now we assume that the wrapper is connected to the sink, so we send to the sink
			moteIF.send(SINK_ADDR, msg); 
		}
		catch (IOException ioe) {
			LearningLayer.display_message(LearningLayer.HIGH_PRIORITY, "[GATEWAY Wrapper] Warning: Got IOException sending message: "+ioe);
			ioe.printStackTrace();
		}
		
		return seq;
	}

	/* (non-Javadoc)
	 * @see learningnetwork.GatewayInterface#send(generics.LNMessages, int, int, int[], int, int, int[], byte)
	 */
	@Override
	public int send(byte type, int sourceNodeID, int sourceNetID,
			int[] sourceNeurID, int destNodeID, int destNetID,
			int[] destNeurID, byte params) {

		//Message Packing (syn_create_cmd_msg)
		SynCreateCmdMsg msg = new SynCreateCmdMsg();	
		msg.set_type(type);
		msg.set_src_mote_addr(get_device_address(sourceNodeID));
		msg.set_src_island_addr(get_island_address(sourceNodeID));
		msg.set_src_netId(sourceNetID);				
		msg.set_src_neurons(sourceNeurID); //Check types
        msg.set_src_neurons_size((short) sourceNeurID.length);
		msg.set_dest_mote_addr(get_device_address(destNodeID));
		msg.set_dest_island_addr(get_island_address(destNodeID));
		msg.set_dest_netId(destNetID);
		msg.set_dest_neurons(destNeurID); //Check types
        msg.set_dest_neurons_size((short) destNeurID.length);
		short seq = nextSeqNumber();
		msg.set_seq_num(seq);
        msg.set_params(params);
        

		//Send the message		
		try{
			LearningLayer.display_message(LearningLayer.LOW_PRIORITY, "[GATEWAY Wrapper]: Sending a message");  
			//now we assume that the wrapper is connected to the sink, so we send to the sink
			moteIF.send(SINK_ADDR, msg); 
		}
		catch (IOException ioe) {
			LearningLayer.display_message(LearningLayer.HIGH_PRIORITY, "[GATEWAY Wrapper] Warning: Got IOException sending message: "+ioe);
			ioe.printStackTrace();
		}
		
		return seq;
	}

	private void packetizeMatrix() {
		//actual size of the matrix being sent (except the last packet it is always MATRIX_PAYLOAD)
		short len;
		//portion of the matrix to be sent in this iteration
		short[] matrix_unit;
		
		//there is still a portion of the matrix to be sent
		if (cur_pkt < tot_pkts) {
			//check if this is the last packet to be sent
			if (cur_pkt == (tot_pkts - 1)) {
				//send only the remaining offset
				len = (short) (matrix_len % MATRIX_PAYLOAD);
				matrix_unit = new short[len];
				//copy the portion of the matrix from the original matrix to the packet
				for (int i = (cur_pkt*MATRIX_PAYLOAD); i < ((cur_pkt*MATRIX_PAYLOAD)+len); i++) {
					//subtract the offset, since we have to copy in a new array from its beginning
					matrix_unit[i-(cur_pkt*MATRIX_PAYLOAD)] = orig_matrix[i];
				}
				last_pkt = true;
			}
			
			else {//other packets to be sent after this
				len = MATRIX_PAYLOAD;
				matrix_unit = new short[len];
				//copy the portion of the matrix from the original matrix to the packet
				for (int i = (cur_pkt*MATRIX_PAYLOAD); i < ((cur_pkt+1)*MATRIX_PAYLOAD); i++) {
					//subtract the offset, since we have to copy in a new array from its beginning
					matrix_unit[i-(cur_pkt*MATRIX_PAYLOAD)] = orig_matrix[i];
				}
			}
			
			//Message Packing (netid_cmd_msg)
			matrix_msg.set_matrix_size(len);
			matrix_msg.set_weights(matrix_unit);
			
			//generate next seq number and store it in next_pkt_ack to check that all packets are received
			next_pkt_ack = nextSeqNumber();
			matrix_msg.set_seq_num(next_pkt_ack);
			
			
			//Send the message		
			try{
				LearningLayer.display_message(LearningLayer.LOW_PRIORITY, "[GATEWAY Wrapper]: Sending a message");  
				//now we assume that the wrapper is connected to the sink, so we send to the sink
				moteIF.send(SINK_ADDR, matrix_msg); 
			}
			catch (IOException ioe) {
				LearningLayer.display_message(LearningLayer.HIGH_PRIORITY, "[GATEWAY Wrapper] Warning: Got IOException sending message: "+ioe);
				ioe.printStackTrace();
			}
		}
		
		else {
			LearningLayer.display_message(LearningLayer.LOW_PRIORITY, "[GATEWAY Wrapper]: packetizeMatrix has been called, but matrix should have already been sent.");  
		}
	}
    
    
    private void packetizeFloatMatrix() {
		//actual size of the matrix being sent (except the last packet it is always MATRIX_PAYLOAD)
		short len;
		//portion of the matrix to be sent in this iteration
		float[] matrix_unit;
		
		//there is still a portion of the matrix to be sent
		if (cur_pkt < tot_pkts) {
			//check if this is the last packet to be sent
			if (cur_pkt == (tot_pkts - 1)) {
				//send only the remaining offset
				len = (short) (matrix_len % FLOAT_ELEMENTS);
				matrix_unit = new float[len];
				//copy the portion of the matrix from the original matrix to the packet
				for (int i = (cur_pkt*FLOAT_ELEMENTS); i < ((cur_pkt*FLOAT_ELEMENTS)+len); i++) {
					//subtract the offset, since we have to copy in a new array from its beginning
					matrix_unit[i-(cur_pkt*FLOAT_ELEMENTS)] = orig_matrix[i];
				}
				last_pkt = true;
			}
			
			else {//other packets to be sent after this
				len = FLOAT_ELEMENTS;
				matrix_unit = new float[len];
				//copy the portion of the matrix from the original matrix to the packet
				for (int i = (cur_pkt*FLOAT_ELEMENTS); i < ((cur_pkt+1)*FLOAT_ELEMENTS); i++) {
					//subtract the offset, since we have to copy in a new array from its beginning
					matrix_unit[i-(cur_pkt*FLOAT_ELEMENTS)] = orig_matrix[i];
				}
			}
			
			//Message Packing (netid_cmd_msg)
			float_matrix_msg.set_matrix_size(len);
			float_matrix_msg.set_weights(matrix_unit);
			
			//generate next seq number and store it in next_pkt_ack to check that all packets are received
			next_pkt_ack = nextSeqNumber();
			float_matrix_msg.set_seq_num(next_pkt_ack);
			
			
			//Send the message		
			try{
				LearningLayer.display_message(LearningLayer.LOW_PRIORITY, "[GATEWAY Wrapper]: Sending a message");  
				//now we assume that the wrapper is connected to the sink, so we send to the sink
				moteIF.send(SINK_ADDR, float_matrix_msg); 
			}
			catch (IOException ioe) {
				LearningLayer.display_message(LearningLayer.HIGH_PRIORITY, "[GATEWAY Wrapper] Warning: Got IOException sending message: "+ioe);
				ioe.printStackTrace();
			}
		}
		
		else {
			LearningLayer.display_message(LearningLayer.LOW_PRIORITY, "[GATEWAY Wrapper]: packetizeMatrix has been called, but matrix should have already been sent.");  
		}
	}
    
    
	
	/* (non-Javadoc)
	 * @see learningnetwork.GatewayInterface#send(generics.LNMessages, int, int, byte[])
	 */
	@Override
	public int send(byte type, int nodeID, int netID, short[] weights) {		
		//total number of packets to be sent
		tot_pkts = (weights.length / MATRIX_PAYLOAD) + 1;
		cur_pkt = 0;
		
		//set the sequence number of the whole matrix to be returned to LN
		matrix_seq = nextSeqNumber();
		
		//set info about the message
		//Message Packing (netid_cmd_msg)
		matrix_msg = new LearningModuleCmdMsg();	
		matrix_msg.set_type(type);
		matrix_msg.set_dest_island_addr(get_island_address(nodeID));
		matrix_msg.set_dest_mote_addr(get_device_address(nodeID));
		matrix_msg.set_netId(netID);
		
		//store the original matrix and its length
		matrix_len = weights.length;
		orig_matrix = new short[matrix_len];
		
		for (int i = 0; i<matrix_len; i++) {
			orig_matrix[i] = weights[i];
		}
		
		//set last_pkt to false
		last_pkt = false;
		
		//start protocol for packetizing the matrix
		packetizeMatrix();
		
		return matrix_seq;
	}
    
    
    /* (non-Javadoc)
	 * @see learningnetwork.GatewayInterface#send(generics.LNMessages, int, int, byte[])
	 */
	@Override
	public int send(byte type, int nodeID, int netID, float[] weights) {		
		//total number of packets to be sent
		tot_pkts = (weights.length / (MATRIX_PAYLOAD/4)) + 1;
		cur_pkt = 0;
		
		//set the sequence number of the whole matrix to be returned to LN
		matrix_seq = nextSeqNumber();
		
		//set info about the message
		//Message Packing (netid_cmd_msg)
		float_matrix_msg = new LearningModuleFloatCmdMsg();	
		float_matrix_msg.set_type(type);
		float_matrix_msg.set_dest_island_addr(get_island_address(nodeID));
		float_matrix_msg.set_dest_mote_addr(get_device_address(nodeID));
		float_matrix_msg.set_netId(netID);
		
		//store the original matrix and its length
		matrix_len = weights.length;
		orig_float_matrix = new float[matrix_len];
		
		for (int i = 0; i<matrix_len; i++) {
			orig_float_matrix[i] = weights[i];
		}
		
		//set last_pkt to false
		last_pkt = false;
		
		//start protocol for packetizing the matrix
		packetizeFloatMatrix();
		
		return matrix_seq;
	}
    

	/* (non-Javadoc)
	 * @see net.tinyos.message.MessageListener#messageReceived(int, net.tinyos.message.Message)
	 */
	@Override
	public void messageReceived(int arg0, Message arg1) {		
		int type = arg1.amType();
		
		//in case of ack of the packetized communication for sending the matrix, 
		//we have to continue the transmissions
		if(type == AM_LEARNING_NOTIFICATION_MSG) {
			LearningNotificationMsg notmsg = (LearningNotificationMsg)arg1;
			
			//check if it is the ack for a packetize msg
			if (notmsg.get_seq_num() == next_pkt_ack) {
				//check if the ack was successfull
				if (notmsg.get_ack() == 1) {
					//check if this was the last packet
					if (!last_pkt) {
						cur_pkt++;
						packetizeMatrix();
					}
					
					else {//finished sending the matrix, notify the LN
						//set the ack seq_number to the seq_number returned when the send has been called and notify the LN
						notmsg.set_seq_num(matrix_seq);
						arg1 = (Message)notmsg;
						ln.messageReceived(arg0,arg1);
					}
				}
				else {
					//resend the current portion of the matrix
					packetizeMatrix();
				}

			}
			//else forward the message to LN
			else {
				ln.messageReceived(arg0,arg1);
			}

		}
		//otherwise we forward the message
		else {
			ln.messageReceived(arg0,arg1);
		}
		
	}
	
    //------------ Private methods for translating device and island addresses --------------------
    
    private int get_device_address(int nodeID) {
        //TODO: Device address extraction
        return (short) nodeID;
    }
    
    private short get_island_address(int nodeID) {
        //TODO: Island address extraction
        return (short) nodeID;
    }
    
    public static int get_nodeID(int nodeAddr, short islandAddr) {
        //TODO: nodeID generation from island and node address
        return (int) nodeAddr;
    }
}
