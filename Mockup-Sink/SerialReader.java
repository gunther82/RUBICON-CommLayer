//package gui;

import java.io.*;
import java.sql.*;
import net.tinyos.packet.*;
import net.tinyos.util.*;
import net.tinyos.message.*;
import java.util.Timer;
import java.util.TimerTask;

public class SerialReader implements MessageListener {
	
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
	
	class MaxRSSI extends TimerTask {
		public void run() {
			first = true;
			int max = 0;
			int id = 0;
			
			for (int i = 0; i<values.length; i++) {
				if (values[i] > max) {
					max = values[i];
					id = i;
				}
			}
			
			for (int i = 0; i<values.length; i++) {
				values[i] = 0;
			}
			
			System.out.println("\n\n/****************************/\nI'm nearest to sensor " + 
							   id +" with rssi value of " + max + "\n/****************************/\n\n");
		}
	}
	
	public SerialReader(MoteIF moteIF) {
		this.moteIF = moteIF;
		this.moteIF.registerListener(new SerialDataMsg(), this);
		this.moteIF.registerListener(new SerialJoinedMsg(), this);
	}
	
	//this is the method to send the command to the sink over the serial
	//you should prepare the message to be sent and call moteIF.send(0, your_message);
	public void sendCommand(int modality, int delay) {
		CommandMsg comm = new CommandMsg();
		comm.set_delay(delay);
		switch (modality) {
			case RANDOM:
				//comm.set_type(READ_DATA_RANDOM_WAIT);
				comm.set_type(READ_DATA_RANDOM_WAIT);
				break;
			case FIXED:
				comm.set_type(READ_DATA);
				break;
			case LOG:
				comm.set_type(READ_LOG);
				break;
			case PERIODIC:
				comm.set_type(READ_PERIODICALLY);
				break;
			case SYN:
				comm.set_type(DEMO_SYN);
				break;

	
			default:
				break;
		}
		
		
		try{
			moteIF.send(0, comm); //0 is the moteID of the sink
		}
		catch (IOException exception) {
			System.err.println("Exception thrown when sending packets. Exiting.");
			System.err.println(exception);
		}
	}
	
	
	//method for handling the message coming from the serial
	//you can put the values in a log file and analyze them later
	public void messageReceived(int to, Message message) {
		int type = message.amType();

		switch(type) {
			case AM_SERIAL_DATA_MSG:
				SerialDataMsg datamsg = (SerialDataMsg)message;
				if(first) {
					first = false;
					timer.schedule(new MaxRSSI(), (values.length+1)*20);
				}
				values[datamsg.get_src_mote_addr()] = datamsg.get_rssi();
				
				double temp = datamsg.get_temp() * 0.01 - 40.0;
				
				System.out.println("Received a data msg from island " + datamsg.get_src_island_addr() + 
								   ", mote " + datamsg.get_src_mote_addr() + " with values:" +
								   "\n\tcounter: " + datamsg.get_counter() + 
								   "\n\ttemp: " + temp +
								   "\n\thumid: " + datamsg.get_humid() +
								   "\n\tlight: " + datamsg.get_light() +
								   "\n\trssi: " + datamsg.get_rssi());
				break;
			case AM_SERIAL_JOINED_MSG:
				SerialJoinedMsg joinedMsg = (SerialJoinedMsg)message;
				
				System.out.println("Mote " + joinedMsg.get_mote_addr() + " from island " + 
								   joinedMsg.get_island_addr() + " joined with type:" +
								   joinedMsg.get_mote_type() + ", transducers: " +
								   joinedMsg.get_transducers() + " and actuators: "+
								   joinedMsg.get_actuators());
				break;

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
		SerialReader client = new SerialReader(mif);
		System.out.println("Started with source: " + source);

		String str = new String();
		InputStreamReader reader = null;
		BufferedReader myInput = null;
		int modality = FIXED;
		boolean firstSet = false;
		boolean log = false;
		boolean requestMode = false;
		boolean ondemand = false;
		int delay = WAIT_TIME;
		
		try{
            reader = new InputStreamReader (System.in);
            myInput = new BufferedReader (reader);
            
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }

		System.out.println("\n\n/*************** MENU ****************/\nChoose the action:\n" +
						   "- press '1' for periodically sampling (at a default rate of 10Hz);\n"+
						   "- press '2' for on demand sampling (a sample each time you press enter);\n"+
						   "- press 'l' for getting the local flash log;\n"+
						   "- press 's' for starting the demo of Synaptic Channels;");
		
		
		while (!requestMode) {
			try{
				str = myInput.readLine();
				if (str.equalsIgnoreCase("1")) {
					System.out.println("You set the periodically modality...");
					modality = PERIODIC;
					delay = FREQ; //in this case the delay is the sampling rate
					requestMode = true;
					client.sendCommand(modality, delay);
				}
				else if(str.equalsIgnoreCase("l")) {
					System.out.println("You selected the get_log option");
					modality = LOG;						
					//firstSet = true;
					//log = true;
					requestMode = true;
					client.sendCommand(modality, delay);
				}
				else if(str.equalsIgnoreCase("2")) {
					System.out.println("You set the on demand modality.");
					requestMode = true;
					ondemand = true;
				}
				else if(str.equalsIgnoreCase("s")) {
					System.out.println("Starting Synaptic Channel demo...");
					modality = SYN;
					delay = SYN_TICK;
					//firstSet = true;
					//log = true;
					requestMode = true;
					client.sendCommand(modality, delay);
				}
				else {
					System.out.println("Wrong press, please choose '1', '2', 'l' or 's'.");
				}
			}
			catch (Exception e){
				System.err.println("Error: " + e.getMessage());
			}
		}
		
		while(true) {
			if (ondemand) {
				System.out.println("\nChoose the delay modality:\n" +
								   "1. press 'r' for random wait;\n2. press 'f' for fixed delay (node_id);");
				try{
					str = myInput.readLine();
					while (!firstSet) {
						if (str.equalsIgnoreCase("r")) {
							System.out.println("You set the modality to RANDOM");
							modality = RANDOM;
							firstSet = true;
						}
						else if(str.equalsIgnoreCase("f")) {
							System.out.println("You set the modality to FIXED");
							modality = FIXED;
							firstSet = true;
						}
						
						else {
							System.out.println("Wrong press, please choose 'r' or 'f'.");
							break;
						}
					}
					
					if (firstSet && !log) {
						System.out.println("Press enter to activate a read task, or 'r' or 'f' to change the delay modality: ");
						if (str.equalsIgnoreCase("r")) {
							System.out.println("---->You set the modality to RANDOM");
							modality = RANDOM;
						}
						else if(str.equalsIgnoreCase("f")) {
							System.out.println("---->You set the modality to FIXED");
							modality = FIXED;
						}
						else {
							System.out.println("You put: "+str);
						}
						
						client.sendCommand(modality, delay);
					}
				}
				catch (Exception e){
					System.err.println("Error: " + e.getMessage());
				}
			}
		}
		
		//serial.sendPackets();
	}
}
