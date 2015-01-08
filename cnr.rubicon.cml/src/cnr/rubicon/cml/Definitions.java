package cnr.rubicon.cml;

//
//  Definitions.java
//  RUBICON src
//
//  Created by Claudio Vairo on 17/05/13.
//
//


public final class Definitions {
    
    public static final int SINK = 1;
    
    //island address definitions
    public static final int BROADCAST_ISLAND = 0xFFFF;
    public static final int LOCAL_ISLAND = 0xFFFE;
    public static final int PROXY_PEIS_ID = 0xFFFD;
    //mote address definitions
    public static final int AM_BROADCAST_ADDR = 0xFFFF;
    public static final int LOCAL_MOTE = 0xFFFE;
    public static final int NULL_MOTE = 0xFFFD;
    
    //list of transducers -> to use as bitmask
    public static final int LIGHT = 0x01;
    public static final int TEMP = 0x02;
    public static final int ACCEL_X = 0x04;
    public static final int ACCEL_Y = 0x08;
    public static final int PIR = 0x10;
    public static final int MIC = 0x20;
    public static final int HUMID = 0x40;
    public static final int MAGNETIC = 0x80;
    public static final int RSSI1 = 0x100;
    public static final int RSSI2 = 0x200;
    public static final int RSSI3 = 0x400;
    public static final int RSSI4 = 0x800;
    public static final int RSSI5 = 0x1000;
    public static final int RSSI6 = 0x1001; // Mauro: wait a second, these value overlaps, why?
    public static final int RSSI7 = 0x1002;
    public static final int RSSI8 = 0x1003;
    public static final int RSSI9 = 0x1004;
    public static final int RSSI10 = 0x1005;
    
    //list of actuators bitmask
    public static final short LED = 0x01;
    public static final short OPEN = 0x02;
    
    //ack definitions
    public static final short REQ_ACK = 1;
    public static final short NOT_ACK = 0;
    public static final short ACK_SUCCESS = 0;
    
    //size of payloads and headers for all layers
    public static final short NETWORK_HEADER = 12;
    public static final short NETWORK_PAYLOAD = 102;
    public static final short APP_PAYLOAD = 100;
    public static final short NUM_UPDATES = 49;
    
    //size of incoming and outgoing radio messages buffer
    public static final short RADIO_BUFF_SIZE = 4;
    //size of incoming serial messages buffer
    public static final short SERIAL_BUFF_SIZE = 2;
    //maximum number of motes per island
    public static final short MAX_NUM_MOTES = 20;
    
    
    //AM_TYPES
    public static final short AM_SERIALTEMP = 1;
    public static final short AM_SERIALHUMID = 2;
    public static final short AM_SERIALLIGHT = 3;
    public static final short AM_SERIALRSSI = 4;
    public static final short AM_SERIALDATA = 5;
    public static final short AM_TEMP_MSG = 6;
    public static final short AM_HUMID_MSG = 7;
    public static final short AM_LIGHT_MSG = 8;
    public static final short AM_RSSI_MSG = 9;
    public static final short AM_DATA_MSG = 10;
    public static final short AM_COMMAND_MSG = 11;
    public static final short AM_SERIAL_DATA_MSG = 12;
    public static final short AM_SYN_DATA_MSG = 13;
    public static final short AM_JOIN_MSG = 14;
    public static final short AM_JOIN_REPLY_MSG = 15;
    public static final short AM_SERIAL_JOINED_MSG = 16;
    public static final short AM_NETID_CMD_MSG = 17;
    public static final short AM_BROADCAST_CMD_MSG = 18;
    public static final short AM_SYN_CREATE_CMD_MSG = 19;
    public static final short AM_LEARNING_MODULE_CMD_MSG = 20;
    public static final short AM_LEARNING_MODULE_FLOAT_CMD_MSG = 21;
    public static final short AM_PACKETIZED_MATRIX_MSG = 22;
    public static final short AM_LEARNING_NOTIFICATION_MSG = 23;
    public static final short AM_LEARNING_MODULE_DOWN_MSG = 24;
    public static final short AM_LEARNING_OUTPUT_MSG = 25;
    public static final short AM_SERIAL_OUTPUT_MSG = 26;
    public static final short AM_CONNLESS_MSG = 27;
    public static final short AM_NET_MSG = 28;
    public static final short AM_ACK_JOIN_MSG = 29;
    public static final short AM_APP_MSG = 30;
    public static final short AM_R_ADDR = 31;
    public static final short AM_DEMO_MSG = 32;
    public static final short AM_COMPLETE_MSG = 33;
    public static final short AM_DEMO_DATA_MSG = 34;
    public static final short AM_UPDATES_MSG = 35;
    public static final short AM_READ_UPDATES_MSG = 36;
    public static final short AM_CONFIG_MSG = 37;
    
    //Components
    public static final short SYN_CHANNEL = 1;
    public static final short STREAM = 2;
    public static final short CONNLESS = 3;
    public static final short COMP_MNGMNT = 4;
    public static final short MOCKUP = 5;
    public static final short LEARNING = 6;
    public static final short TRANSPORT_MD = 7;
    public static final short APPLICATION_MD = 8;
    public static final short RUBICON_ACK = 9;
    public static final short CONTROL_LAYER = 10;
    public static final short ESN_DATA = 11;
    
    //Commands
    public static final short READ_TEMP = 1;
    public static final short READ_HUMID = 2;
    public static final short READ_LIGHT = 3;
    public static final short READ_RSSI = 4;
    public static final short READ_DATA = 5;
    public static final short READ_DATA_RANDOM_WAIT = 6;
    public static final short READ_LOG = 7;
    public static final short READ_PERIODICALLY = 8;
    public static final short DEMO_SYN = 9;
    public static final short SET_ISLAND_ID = 10;
    
    public static final short ACTIVATE_FORW_COMP = 1;
	public static final short STOP_FORW_COMP = 2;
	public static final short ACTIVATE_LEARN_MODULE = 3;
	public static final short STOP_LEARN_MODULE = 4;
	public static final short RESET_LEARN_MODULE = 5;
	public static final short CONNECT_NODE = 6;
    public static final short DISCONNECT_NODE = 7;
	public static final short SET_CLOCK = 8;
	public static final short SYN_CONN_OUT = 9;
	public static final short SYN_CONN_IN = 10;
	public static final short SYN_CONN_LOCAL = 11;
	public static final short UPLOAD_MODULE_WIN = 12;
	public static final short DOWNLOAD_MODULE_WIN = 13;
	public static final short ACTIVATE_FEAT_SEL = 14;
	public static final short STOP_FEAT_SEL = 15;
	public static final short UPLOAD_MODULE_WA = 16;
	public static final short DOWNLOAD_MODULE_WA = 17;
	public static final short UPLOAD_MODULE_WB = 18;
	public static final short DOWNLOAD_MODULE_WB = 19;
    public static final short UPLOAD_MODULE_WOUT = 20;
	public static final short DOWNLOAD_MODULE_WOUT = 21;
    
    //OLD
    static final int RANDOM = 1;
    static final int FIXED = 2;
    static final int LOG = 3;
    static final int PERIODIC = 4;
    static final int SYN = 5;
    
    static final int WAIT_TIME = 18; //default wating time for read_data with fixed delay. milliseconds
    static final int FREQ = 100; //the sampling rate for periodc sampling
    static final int SYN_TICK = 500; //the tick of the synaptic computation
    
    
    /**
     * Conversion function that transforms a device and island address into a unique device identifier.
     * 
     * @param nodeAddr Network address of the device
     * @param islandAddr Network address of the island
     * @return The unique identifier of the device
     */
//    public static long get_nodeID(int nodeAddr, int islandAddr) {
//        //TODO: nodeID generation from island and node address
//        int pid_bitmask = 0xFF00;
//        int devid_bitmask = 0x00FF;
//        long nodeId = ((islandAddr<<16) & pid_bitmask) + (nodeAddr & devid_bitmask);
//    	return nodeId;
//    }
    
    public static int get_nodeID(int nodeAddr, int islandAddr) {
    	//TODO: nodeID generation from island and node address
    	int pid_bitmask = 0xFFFF0000;
    	int devid_bitmask = 0x0000FFFF;
    	int dev = (nodeAddr & devid_bitmask);
    	int pid = ((islandAddr<<16) & pid_bitmask);
    	int nodeId = dev | pid;
    	return nodeId;
   }
    
    //------------ public methods for translating device and island addresses --------------------
    
    public static int get_device_address(int nodeID) {
        //TODO: Island address extraction
    	int devid_bitmask = 0x0000FFFF;
        return (int)(nodeID & devid_bitmask);
    }
    
    public static int get_island_address(int nodeID) {
        //TODO: Device address extraction
    	int pid_bitmask = 0xFFFF0000;
    	int pid = nodeID & pid_bitmask;
        return (int) (pid>>16);
    }
}
