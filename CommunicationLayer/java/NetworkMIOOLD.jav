//package gui;

import java.io.*;
import java.sql.*;
import net.tinyos.packet.*;
import net.tinyos.util.*;
import net.tinyos.message.*;
import java.util.Timer;
import java.util.TimerTask;

public class Network implements MessageListener {
	//island address definitions
	static final int BROADCAST_ISLAND = 0xFFFF;
	static final int LOCAL_ISLAND = 0xFFFE;
	static final int PROXY_PEIS_ID = 0xFFFD;
	//mote address definitions
	static final int LOCAL_MOTE = 0xFFFE;
	static final int NULL_MOTE = 0xFFFD;
	
	static final int AM_SERIALTEMP = 1;
	static final int AM_SERIALHUMID = 2;
	static final int AM_SERIALLIGHT = 3;
	static final int AM_SERIALRSSI = 4;
	static final int AM_SERIALDATA = 5;
	static final int AM_TEMP_MSG = 6;
	static final int AM_HUMID_MSG = 7;
	static final int AM_LIGHT_MSG = 8;
	static final int AM_RSSI_MSG = 9;
	static final int AM_DATA_MSG = 10;
	static final int AM_COMMAND_MSG = 11;
	static final int AM_SERIAL_DATA_MSG = 12;
	static final int AM_SYN_DATA_MSG = 13;
	static final int AM_JOIN_MSG = 14;
	static final int AM_JOIN_REPLY_MSG = 15;
	static final int AM_SERIAL_JOINED_MSG = 16;
	static final int AM_NETID_CMD_MSG = 17;
    static final int AM_BROADCAST_CMD_MSG = 18;
	static final int AM_SYN_CREATE_CMD_MSG = 19;
	static final int AM_LEARNING_MODULE_CMD_MSG = 20;
	static final int AM_LEARNING_MODULE_FLOAT_CMD_MSG = 21;
	static final int AM_PACKETIZED_MATRIX_MSG = 22;
	static final int AM_LEARNING_NOTIFICATION_MSG = 23;
	static final int AM_LEARNING_MODULE_DOWN_MSG = 24;
	static final int AM_LEARNING_OUTPUT_MSG = 25;
    static final int AM_SERIAL_OUTPUT_MSG = 26;
    static final int AM_CONNLESS_MSG = 27;
    static final int AM_NET_MSG = 28;
	
	static final int SYN_CHANNEL = 1;
	static final int STREAM = 2;
	static final int CONNLESS = 3;
	static final int COMP_MNGMNT = 4;
	static final int MOCKUP = 5;
	static final int LEARNING = 6;
    static final int TRANSPORT_MD = 7;
    static final int APPLICATION_MD = 8;
	static final int RUBICON_ACK = 9;
	static final int CONTROL_LAYER = 10;
	
	static final short READ_TEMP = 1;
	static final short READ_HUMID = 2;
	static final short READ_LIGHT = 3;
	static final short READ_RSSI = 4;
	static final short READ_DATA = 5;
	static final short READ_DATA_RANDOM_WAIT = 6;
	static final short READ_LOG = 7;
	static final short READ_PERIODICALLY = 8;
	static final short DEMO_SYN = 9;	
	
	static final int RANDOM = 1;
	static final int FIXED = 2; 
	static final int LOG = 3;
	static final int PERIODIC = 4;
	static final int SYN = 5;
	
	static final int WAIT_TIME = 18; //default wating time for read_data with fixed delay. milliseconds
	static final int FREQ = 100; //the sampling rate for periodc sampling
	static final int SYN_TICK = 500; //the tick of the synaptic computation
	
	private int[] values = new int[5];
	private Timer timer = new Timer();
	private boolean first = true;
	
	private MoteIF moteIF;
	//TO UPDATE: initialize this variable in some smart way
	private islandID = 11;
    
	public Network(MoteIF moteIF) {
		this.moteIF = moteIF;
		this.moteIF.registerListener(new NetMsg(), this);
		//this.moteIF.registerListener(new SerialJoinedMsg(), this);
	}
	
	
	private void generateDemoCmd() {
		AppMsg amsg = new AppMsg();
		NetMsg nmsg = new NetMsg();
		
		byte[] payload = new byte[100];
		
		//initialize the app_msg_t
		amsg.set_trans_id(CONNLESS);
		amsg.set_app_id(CONTROL_LAYER);
		amsg.set_payload(payload);
		
		
		nmsg.set_payload(amsg)
	}
	
	
	//notification of the reception of a message from PEIS
	public void GWReceive(Message m, int size) {
		//TO UPDATE: this should be the received message from PEIS
        NetMsg recnetmsg = (NetMsg)m;
        int dest = recnetmsg.get_dest_devid();
		
		//check if the message is for the PC/Robot or for a mote
		if(dest == NULL_MOTE) {
            System.out.println("Message for me, handling it...");
            //TO UPDATE: signal to the upper layer the reception of the message
            //TO UPDATE: send ack
        }
        
        else {
            System.out.println("Message for mote " + dest + ", forwarding it...");
			
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
	
//	
//	//this is the method to send the command to the sink over the serial
//	//you should prepare the message to be sent and call moteIF.send(0, your_message);
//	public void sendCommand(int modality, int delay) {
//		
//        //TO UPDATE: this should be the received message from PEIS
//        NetMsg recnetmsg = new NetMsg();
//        int dest = recnetmsg.get_dest_devid();
//        
//		//check if the message is for the PC/Robot or for a mote
//		if(dest == NULL_MOTE) {
//            System.out.println("Message for me, handling it...");
//            //TO UPDATE: signal to the upper layer the reception of the message
//            //TO UPDATE: send ack
//        }
//        
//        else {
//            System.out.println("Message for mote " + dest + ", forwarding it...");
//        
//            try{
//                moteIF.send(0, recnetmsg); //0 is the moteID of the sink
//            }
//            catch (IOException exception) {
//                System.err.println("Exception thrown when sending packets. Exiting.");
//                System.err.println(exception);
//            }
//        }
//	}
	
	
	byte[] getRightPayload(NetMsg netmsg) {
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
        
		switch(type) {
			case AM_NET_MSG:
				NetMsg netmsg = (NetMsg)message;
                int dest = netmsg.get_dest_pid();
                System.out.println("Received a data msg from island " + netmsg.get_src_pid() + ", and mote " + netmsg.get_src_devid());
                
                //case of the join msg
                if(dest == PROXY_PEIS_ID) {
                    System.out.println("Message for me, handling it...");
					
					byte[] msg = getRightPayload(netmsg);
					
					switch(msg[0]) {
						case AM_SERIAL_JOINED_MSG:
						SerialJoinedMsg smsg = new SerialJoinedMsg(msg);
						System.out.println("------  Mote Descriptor received ------"); 
						System.out.println("mote_type: " + smsg.get_mote_type() + "\ntransducers: " + smsg.get_transducers() +
										   "\nactuators: " + smsg.get_actuators());
						break;
					}
					
					
					
                    //TO UPDATE: signal to the upper layer the reception of the message
                    //TO UPDATE: send ack
                }
                
				//the message is for the current base station
				else if(dest == islandID) {
					System.out.println("Message for me, handling it...");
                    //TO UPDATE: signal to the upper layer the reception of the message
                    //TO UPDATE: send ack
				}
				
                else {
                    System.out.println("Message for island " + dest + ", forwarding it...");
                    //TO UPDATE: send the message by means of PEIS
                    //GWSend(dest, netmsg);
                }
				break;
                
//			case AM_SERIAL_JOINED_MSG:
//				SerialJoinedMsg joinedMsg = (SerialJoinedMsg)message;
//				
//				System.out.println("Mote " + joinedMsg.get_mote_addr() + " from island " + 
//								   joinedMsg.get_island_addr() + " joined with type:" +
//								   joinedMsg.get_mote_type() + ", transducers: " +
//								   joinedMsg.get_transducers() + " and actuators: "+
//								   joinedMsg.get_actuators());
//				break;
                
		}
	}
    
	
	private static void usage() {
		System.err.println("usage: SerialReader [-comm <source>]");
	}
	
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
		
		PhoenixSource phoenix;
		
		if (source == null) {
			phoenix = BuildSource.makePhoenix(PrintStreamMessenger.err);
		}
		else {
			phoenix = BuildSource.makePhoenix(source, PrintStreamMessenger.err);
		}
		
		MoteIF mif = new MoteIF(phoenix);
		Network client = new Network(mif);
		System.out.println("Started with source: " + source);
        
		//serial.sendPackets();
	}
}
