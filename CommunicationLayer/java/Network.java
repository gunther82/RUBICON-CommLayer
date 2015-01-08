//package CommunicationLayer;

//package gui;

import java.io.*;
import net.tinyos.packet.*;
import net.tinyos.util.*;
import net.tinyos.message.*;

import java.util.Properties;
import java.util.Arrays;
import java.lang.Math;

import ucd.rubicon.network.RubiconAddress;
import ucd.rubicon.network.RubiconMessage;
import ucd.rubicon.network.RubiconMessageJoin;
import ucd.rubicon.network.RubiconMessageSender;
import ucd.rubicon.network.RubiconNetwork;

public class Network extends RubiconNetwork implements MessageListener {
	
	public static String NETWORK_SOURCE = "network.source"; 
	
	private MoteIF moteIF;
	//TO UPDATE: initialize this variable in some smart way
	//Mauro: l'id dell'island e' in RubiconNetwork (la abstract class implementata da questa classe)
	private int islandID = 11;
    	
	private ReceiveNetworkListener receiveNetworkListener;
	
	
	/*********** START CODE CLAUDIO: test with Davide TO BE REMOVED ******************/
	private ReceiveSerialJoinMsgListener receiveSerialJoinMsgListener;
	
	//set the Listener for the event reception of a Learning message
    public void setOnReceiveSerialJoinMessage(ReceiveSerialJoinMsgListener listener) {
    	receiveSerialJoinMsgListener = listener;
    }
    /*********** END CODE CLAUDIO: test with Davide TO BE REMOVED ******************/
	
    
    
	// Allows the user to set a Listener and react to the event
    public void setOnReceiveNetworkListener(ReceiveNetworkListener listener) {
    	receiveNetworkListener = listener;
    }
	
    
    /*********** START CODE CLAUDIO: check if this solution is good ******************/
    private static Network netw;
    
    public static synchronized Network getNetwork() {
    	if(netw == null) {
    		netw = new Network();
    	}
    	return netw;
    }
    /*********** END CODE CLAUDIO: check if this solution is good ******************/
    
    
    public Network() {
        this.init();
    }
    
	public void init() {
				
		//String source = getProperties().getProperty(NETWORK_SOURCE);
		String source = "serial@/dev/tty.usbserial-MFTC66QF:telosb";
		if (source == null) {
			System.out.println("Network: missing property "+ NETWORK_SOURCE + " from properties file");
			//TODO: Log, I cannot initialize this network
			return;
		}
	
		System.out.println("Network: initializing with source " + source);
		
		PhoenixSource phoenix;
		
		if (source == null) {
			phoenix = BuildSource.makePhoenix(PrintStreamMessenger.err);
		}
		else {
			phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
		}
		
		moteIF = new MoteIF(phoenix);
		moteIF.registerListener(new NetMsg(), this);
		
		//this.moteIF.registerListener(new SerialJoinedMsg(), this);
		System.out.println("Network: Started with source: " + source + " and islandID = " + islandID);
		
	}
	
	//generate a sequence number for a message to be sent
	private int getSeqNum() {
		int seq = (int)(Math.random()*0xFFFF);
		return seq;
	}
	
	
    //Send the given message to the specified destination
    //Actually this command simply copy the message in the out buffer, and post the task for sending the message
	public synchronized int send(RubiconAddress dest, byte[] payload, short nbytes, boolean reliable) {
		//generate the sequence number
		int seq = getSeqNum();
		//set the size of the NetMsg
        //short len = (short)(nbytes + Definitions.NETWORK_HEADER);
		
		//check if the message is from the Synaptic Channel component
        if(dest.getPeisId() == Definitions.LOCAL_ISLAND) {
            //replace the macro LOCAL_ISLAND with the actual local island address
        	dest = new RubiconAddress(islandID, dest.getDeviceId());
        }
		
		//instantiate the NetMsg and fill it, with network header and payload
		NetMsg msg = new NetMsg();
		msg.set_dest_pid(dest.getPeisId());
		msg.set_dest_devid(dest.getDeviceId());
		msg.set_src_pid(islandID);
		msg.set_src_devid(Definitions.NULL_MOTE);
		if(reliable) {
            msg.set_reliable((byte)1);
        }
        else {
            msg.set_reliable((byte)0);
        }
		msg.set_seq_num(seq);
		msg.set_nbytes(nbytes);
		msg.set_payload(payload);
        
        //TO UPDATE: check the BROADCAST
        //check if the message is for the local island
        if(dest.getPeisId() == islandID) {
            //send the message to the SINK over the serial
            try{
				//forward the message destinated to a mote, over the serial to the SINK
                moteIF.send(Definitions.SINK, msg);
            }
            catch (IOException exception) {
                System.err.println("Exception thrown when sending packets. Exiting.");
                System.err.println(exception);
            }
        }
        else {
            //forward to gateway
        }
		
		return seq;
	}
	
	
	//notification of the reception of a message from PEIS
	public void GWReceive(Message m, int size) {
//		DemoMsg d = new DemoMsg();
//		d.set_msg_type((short)0);
//		d.set_sensorsBitmask(3);
//		d.set_period(1000);
//		
//		byte[] bytes = d.dataGet();
//		System.out.println("byte array is:" + Arrays.toString(bytes));
		
		
		//create the NetMsg
		NetMsg recnetmsg = (NetMsg)m;
		//System.out.println("GWReceive: peisID: " + recnetmsg.get_dest_pid() + ", devid: " + recnetmsg.get_dest_devid());
//		recnetmsg.set_src_pid(recnetmsg.get_dest_pid());
//		recnetmsg.set_src_devid(NULL_MOTE);
		recnetmsg.set_dest_pid(11);
//		recnetmsg.set_dest_devid(1);
		recnetmsg.set_reliable((byte)0);
		recnetmsg.set_seq_num(5);
		recnetmsg.set_nbytes((short)size);
//		recnetmsg.set_trans_id((short)CONNLESS);
//		recnetmsg.set_app_id((short)MOCKUP);
		
		//CompleteMsg recnetmsg = demoMsg();
		
		
		System.out.println("GWReceive: received payload is:" + Arrays.toString(recnetmsg.get_payload()) + " and the size is: " + size);
		
		int dest = recnetmsg.get_dest_devid();
		
		//check if the message is for the PC/Robot or for a mote
		if(dest == Definitions.NULL_MOTE) {
            System.out.println("GWReceive: Message for me, handling it...");
            //TO UPDATE: signal to the upper layer the reception of the message
            //TO UPDATE: send ack
        }
        
        else {
            System.out.println("GWReceive: Message for mote " + dest + ", forwarding it...");
			
            try{
				//forward the message destinated to a mote, over the serial to the SINK
                moteIF.send(0, recnetmsg); //0 is the moteID of the sink
            }
            catch (IOException exception) {
                System.err.println("Exception thrown when sending packets. Exiting.");
                System.err.println(exception);
            }
        }
	}
	
	
	
	byte[] getNetMsgPayload(NetMsg netmsg) {
		//get payload and size from msg
		byte[] payload = netmsg.get_payload();
		int size = netmsg.get_nbytes();
		//getting the actual message contained in the payload with the right size
		byte[] msg = new byte[size];
		for(int i = 0; i < size; i++) {
			msg[i] = payload[i];
		}
		
		return msg;
	}
	
	
	//method for handling the message coming from the serial
	//you can put the values in a log file and analyze them later
	public void messageReceived(int to, Message message) {
		int type = message.amType();
		
		System.out.println("[Network.java]: messageReceived invoked with type = " + type);
		
		switch(type) {
			case Definitions.AM_NET_MSG:
				//cast the message to a NetMsg
				NetMsg netmsg = (NetMsg)message;
				//get dest pid and payload
                int dest = netmsg.get_dest_pid();
				byte[] payload = getNetMsgPayload(netmsg);
				
                System.out.println("Network.messageReceived(): Received a AM_NET_MSG from island " + netmsg.get_src_pid() + 
                		", and mote " + netmsg.get_src_devid() + "for island " + netmsg.get_dest_pid() + 
                		" and devid " + netmsg.get_dest_devid() + " and size = " + netmsg.get_nbytes());
                
//                //special case of the join msg: for test only. 
//				//This case should be in the last "esle" branch of this method
//                if(dest == Definitions.PROXY_PEIS_ID) {
//                    System.out.println("dest == PROXY_PEIS_ID...");
//					
//					switch(payload[0]) {
//						case Definitions.AM_SERIAL_JOINED_MSG:
//						SerialJoinedMsg smsg = new SerialJoinedMsg(payload);
//						System.out.println("------  Mote Descriptor received ------"); 
//						System.out.println("mote_type: " + smsg.get_mote_type() + "\ntransducers: " + smsg.get_transducers() +
//										   "\nactuators: " + smsg.get_actuators());
//							
//						
//						/*********** START CODE CLAUDIO: test with Davide TO BE REMOVED ******************/
//						//forward SerialJoinedMsg to learning
//						receiveSerialJoinMsgListener.onReceiveSerialJoinMessage(smsg);
//						
//						/*********** END CODE CLAUDIO: test with Davide TO BE REMOVED ******************/
//						
//						//Mauro: create a RubiconMessageJoin and pass it to the gateway
//						RubiconAddress destination = new RubiconAddress(dest, netmsg.get_dest_devid()); 
//	    				RubiconAddress from = new RubiconAddress(netmsg.get_src_pid(), netmsg.get_src_devid()); 
//	    				RubiconMessageJoin rmsg = new RubiconMessageJoin(from);	
//						
//						//Fill its fields
//	    				rmsg.actuators = smsg.get_actuators(); //short
//	    				rmsg.transducers = smsg.get_transducers(); //int
//	    				// Mauro: ho dovuto mettere il cast to short qui
//	    				rmsg.island_addr = (short)smsg.get_island_addr(); //short
//	    				rmsg.addr = smsg.get_mote_addr(); //int
//	    				rmsg.type = smsg.get_mote_type(); //int
//						
//	    				if (this.getGateway()!=null) { 
//	    					try {
//	    						getGateway().send(rmsg, destination);
//	    					} catch (Exception e) {
//	    						// TODO Auto-generated catch block
//	    						// TODO: Log
//	    						e.printStackTrace();
//	    					}		
//	    				}
//					}
//														
//                    //TO UPDATE: signal to the upper layer the reception of the message
//                    //TO UPDATE: send ack
//                }
                
				//the message is for the current base station
				if(dest == islandID) {
					/*********** START CODE CLAUDIO: test with Davide TO BE REMOVED ******************/
					if(payload[2] == Definitions.AM_SERIAL_JOINED_MSG) {
						SerialJoinedMsg smsg = new SerialJoinedMsg(new AppMsg(payload).get_payload());
//						System.out.println("------  Mote Descriptor received ------"); 
//						System.out.println("mote_type: " + smsg.get_mote_type() + "\ntransducers: " + smsg.get_transducers() +
//								"\nactuators: " + smsg.get_actuators());
						//forward SerialJoinedMsg to learning
						if(receiveSerialJoinMsgListener != null) {
							receiveSerialJoinMsgListener.onReceiveSerialJoinMessage(smsg);
						}
					}
//					/*********** END CODE CLAUDIO: test with Davide TO BE REMOVED ******************/
//					
//					else {
						//System.out.println("messageReceived: Message for me, handling it...");
						//TO UPDATE: send ack
						//signal to the upper layer the reception of the message
						if(receiveNetworkListener!=null) {
							//get the src r_addr
							RubiconAddress sender = new RubiconAddress(netmsg.get_src_pid(), netmsg.get_src_devid());
							//signal the reception of the message to the TransportMD through the listener
                            System.out.println("Signaling onReceiveNetworkMessage");
							receiveNetworkListener.onReceiveNetworkMessage(sender, payload, netmsg.get_nbytes(), netmsg.get_reliable(), netmsg.get_seq_num());
						}
                        else {
                            System.out.println("receiveNetworkListener e' NULL");
                        }
//					}
				}	
				
                else if (this.getGateway()!=null) { 
                    System.out.println("Network.messageReceived(): Message for island " + dest + ", forwarding it...");
                    //TO UPDATE: send the message by means of PEIS
                    //GWSend(dest, netmsg);
                    // Mauro: the following is what you need to send a message to the gateway
					int size_payload = netmsg.get_nbytes() - 2;
					
					//debug code, it simply prints the app payload, excluded the two bytes APP_ID and TRANS_ID
					System.out.println("Network.messageReceived(): the payload is:" + Arrays.toString(payload) + " and size = " + size_payload);
					byte[] app_payload = new byte[size_payload];
					for(int i = 0; i < size_payload; i++) {
						app_payload[i] = payload[i+2];
					}
					
//					DemoDataMsg ddm = new DemoDataMsg(app_payload);
//					int light = ddm.get_light();
//					int temp = ddm.get_temp();
//					int humid = ddm.get_humid();
//					System.out.println("Network: DemoDataMsg received: light = " + light + ", temp = " + temp + ", humid = " + humid);
					
    				RubiconAddress destination = new RubiconAddress(dest, netmsg.get_dest_devid()); 
    				RubiconAddress from = new RubiconAddress(netmsg.get_src_pid(), netmsg.get_src_devid()); 
    				RubiconMessage msg = new RubiconMessage(from, payload[2], payload);
    				try {
						getGateway().send(msg, destination);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						// TODO: Log
						e.printStackTrace();
					}
                }
                
				break;
		}
	}
    
	
	private static void usage() {
		System.err.println("usage: SerialReader [-comm <source>]");
	}

	// Mauro: this now is only for testing,
	// the network object is created and initialized from the RubiconGateway
	public static void main(String[] args) throws Exception {
		
		String source = null;
		if (args.length == 2) {
			if (!args[0].equals("-comm")) {
				usage();
				System.exit(1);
			}
			source = args[1];
		}
		else if (args.length != 0) {
			usage();
			System.exit(1);
		}
		

		// Mauro: crea una network for testing purpose without gateway
		Network network = new Network();
		Properties properties = new Properties();
		properties.put(NETWORK_SOURCE, source);
		
		// initialize the network (it will call this class init method
		// to start the source
		network._init(11, null, properties);
		//serial.sendPackets();
	}


	/**
	 * Mauro: Called by the gateway when it receives a message for this network
	 */
	@Override
	public void handleMessageReceived(RubiconMessage message, RubiconAddress destination) {	
		// Mauro: penso che e' questo da fare per creare un NetMsg a partire dal payload in message
		NetMsg msg = new NetMsg();
		//get the sender from the RubiconMessage
		RubiconAddress sender = message.getSender();
		//get the payload from the RubiconMessage 
		byte[] payload = message.toEmbeddedFormat();
		if (payload == null) {
			System.out.println("Qualcosa non quadra, questo messaggio non va' considerato qui!");
			return;
		}
		System.out.println("NETWORK: handleMessageReceived: payload is:" + Arrays.toString(payload));
		//set destination and source fileds in the NetMsg
		msg.set_dest_pid(destination.getPeisId());
		msg.set_dest_devid(destination.getDeviceId());
		msg.set_src_pid(sender.getPeisId());
		msg.set_src_devid(sender.getDeviceId());
		//System.out.println("handleMessageReceived: peisID: " + destination.getPeisId() + ", devid: " + destination.getDeviceId());
		//set the payload in the NetMsg
		msg.set_payload(payload);
		GWReceive(msg, payload.length);
	}

		
}
