package ucd.rubicon.network.salad;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;


import ucd.rubicon.network.DeviceUpdate;
import ucd.rubicon.network.RubiconAddress;
import ucd.rubicon.network.RubiconMessage;
import ucd.rubicon.network.RubiconMessageCtrlActuateCmd;
import ucd.rubicon.network.RubiconMessageUpdates;
import ucd.rubicon.network.RubiconMessageJoin;
import ucd.rubicon.network.RubiconNetwork;
import ucd.rubicon.network.SensorUpdate;

/**
 * Provides a {@Link RubiconNetwork} implementation to interface with TECNALIA's SALAD middleware
 * @author rubicon
 *
 */
public class RubiconNetworkSalad extends RubiconNetwork implements Runnable {
			
	// default value for network parameters
	public static final String SALAD_HOST = "salad_host";
	public static final String SALAD_PORT = "salad_port";
	public static final String RUBICON_PORT = "rubicon_port";
	public static final String LOCALHOST = "localhost";
	public static final String SOCKET_ACCEPT_TIMEOUT_MILLISECONDS = "socket_accept_timeout_milliseconds";
	public static final String SOCKET_BUFFER_SIZE_BYTES = "socket_buffer_size_bytes";
	public static final String SOCKET_INIT_RETRY_MILLISECONDS = "socket_init_retry_milliseconds";
	public static final String ID_PREFIX = "device_";
	
	protected Thread  thread = null;
	protected String  strHostSalad = LOCALHOST;
	protected int nSaladPort = 9000;
	protected int nRubiconPort = 9100;
	protected int socketAcceptTimeout = 1000;
	protected int socketInitRetryDelay = 1000;
	protected int socketBufferSize = 2048;
	
	protected DatagramSocket serverSocket = null;
	protected DatagramSocket clientSocket = null;
	
	//Receiving buffer creation
	protected byte[] buf = null;
	protected DatagramPacket dataPacket = null;
	protected byte[] sendData = null;
	protected DatagramPacket sendPacket;
	protected InetAddress IPAddress;
	
	protected boolean bIsRunning = false;
	
	protected Map<Integer, DeviceDescription> mapDevices = new HashMap<Integer, DeviceDescription>();

	
	public void parseProperties() {		
		// get the SALAD_HOST from the property, if null, it leaves the default value
		String s = getProperties().getProperty(SALAD_HOST);
		if (s!=null) {
			strHostSalad = s;
		}
		
		// get the SALAD_PORT from the property, if null, it leaves the default value
		s = getProperties().getProperty(SALAD_PORT);		
		if (s!=null) {
			nSaladPort = Integer.parseInt(s);
		}
		
		// get the RUBICON_PORT from the property, if null, it leaves the default value
		s = getProperties().getProperty(RUBICON_PORT);		
		if (s!=null) {
			nRubiconPort = Integer.parseInt(s);
		}
		
		// get the SOCKET_ACCEPT_TIMEOUT_MILLISECONDS from the property, if null, it leaves the default value
		s = getProperties().getProperty(SOCKET_ACCEPT_TIMEOUT_MILLISECONDS);		
		if (s!=null) {
			socketAcceptTimeout = Integer.parseInt(s);
		}
				
		// get the SOCKET_ACCEPT_TIMEOUT_MILLISECONDS from the property, if null, it leaves the default value
		s = getProperties().getProperty(SOCKET_INIT_RETRY_MILLISECONDS);
		if (s!=null) {
			socketInitRetryDelay = Integer.parseInt(s);
		}

		// get the RUBICON_PORT from the property, if null, it leaves the default value
		s = getProperties().getProperty(SOCKET_BUFFER_SIZE_BYTES);
		getProperties().remove(SOCKET_BUFFER_SIZE_BYTES);
		if (s!=null) {
			socketBufferSize = Integer.parseInt(s);
		}
	}
	
	public void parseDevices() {	
		//System.out.println("Parsing devices");
		Enumeration e = getProperties().keys();
		String s;
		while (e.hasMoreElements()) {
			String key 	 = (String)e.nextElement();
			String value = getProperties().getProperty(key);
			//System.out.println("key("+key+")="+value);
			if (key.startsWith(ID_PREFIX)) {
				s = key.substring(ID_PREFIX.length());
				int rubiconDeviceId = Integer.parseInt(s);
				DeviceDescription deviceDescription = new DeviceDescription(rubiconDeviceId);
				//System.out.println("device id("+rubiconDeviceId+")");
				try {
					deviceDescription.parseFromString(value);
					mapDevices.put(rubiconDeviceId, deviceDescription);
					
					RubiconAddress sender = new RubiconAddress(getIslandID(), this.getNetworkName(), rubiconDeviceId);
					RubiconAddress proxy = new RubiconAddress(getIslandID(), 0);
					RubiconMessageJoin rubiconMessage = new RubiconMessageJoin(sender);
					Properties resources = new Properties();
					resources.put(deviceDescription.getSymbolicName(), deviceDescription.getRubiconType());
					rubiconMessage.setResources(resources);
					rubiconMessage.setName(deviceDescription.getSymbolicName());
					rubiconMessage.setAddr(rubiconDeviceId);					
					rubiconMessage.setIsland_addr((short)getIslandID());
					
					sendToGateway(rubiconMessage, proxy);
					
				} catch (Exception e1) {
					System.out.println(e1.getMessage());
				}				
			}
		}		
	}

	
	public void initServerSocket() {
		System.out.println("Initializing server socket ...");
		//Create receiving buffer
		buf = new byte[socketBufferSize];
		dataPacket = new DatagramPacket(buf, buf.length);		
		boolean retry = true;
		while (retry) {
			try 
			{	
				serverSocket = new DatagramSocket(nRubiconPort);
				retry=false;
			} catch (IOException e) {
				System.out.println("Could not listen on port " + nRubiconPort + "/UDP:" + e.getMessage());
				System.out.println("Retrying in "+socketInitRetryDelay +" ms...");
				try {
					Thread.sleep(socketInitRetryDelay);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void initClientSocket() {
		System.out.println("Initializing client socket ...");
		sendData = new byte[socketBufferSize];
		boolean retry = true;
		while (retry) {
			try{
				clientSocket = new DatagramSocket();
				IPAddress = InetAddress.getByName(strHostSalad);
				System.out.println("Host IP Address is "+IPAddress.getHostAddress());
				sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, nSaladPort);
				retry = false;
			}		
			catch(IOException e){
				System.out.println("Could not create the client socket /UDP to communicate with the Salad middleware: "+e.getMessage());
				System.out.println("Retrying in "+socketInitRetryDelay +" ms...");
				try {
					Thread.sleep(socketInitRetryDelay);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}			
		}
	}

	public void init() throws Exception {
		
		System.out.println("Initializing RubiconNetworkSalad...");
		
		parseProperties();
		
		System.out.println(SALAD_HOST+"="+strHostSalad);
		System.out.println(SALAD_PORT+"="+nSaladPort);
		System.out.println(RUBICON_PORT+"="+nRubiconPort);
		System.out.println(SOCKET_ACCEPT_TIMEOUT_MILLISECONDS+"="+socketAcceptTimeout);
		System.out.println(SOCKET_BUFFER_SIZE_BYTES+"="+socketBufferSize);		
		System.out.println(SOCKET_INIT_RETRY_MILLISECONDS+"="+socketInitRetryDelay);
						
		//parseDevices();
		// launch listening thread
		System.out.println("Launching receiving thread...");
		thread = new Thread(this);
		thread.start();		
	}
		

	protected synchronized void stop() {
		bIsRunning = false;		
	}
	
	protected synchronized boolean isRunning() {
		return bIsRunning;
	}
	
	/**
	 * This method is called by {@Link RubiconGateway} when a message is received by this network
	 */
	@Override
	public void handleMessageReceived(RubiconMessage message, RubiconAddress destination) {
		System.out.println("RubiconNetworkSalad.handleMessageReceived");
		System.out.println(message.toString());
		if (destination.getDeviceId()<=0) {
			System.out.println("Invalid device id ("+destination.getDeviceId()+")! Request ignored.");
			return;
		}

		if (message instanceof RubiconMessageCtrlActuateCmd) {
			RubiconMessageCtrlActuateCmd actMsg = (RubiconMessageCtrlActuateCmd)message;
			DeviceDescription device = mapDevices.get(destination.getDeviceId());
			if (device == null) {
				System.out.println("Unrecognized device id ("+destination.getDeviceId()+")! Request ignored.");
				return;				
			}
			if (device.getActuatorSaladId()==null) {
				System.out.println("Device id ("+destination.getDeviceId()+") is not an actuator! Request ignored.");
				return;
			}

			String command = "#SALAD#2:"+device.getActuatorSaladId()+":"+actMsg.getValue()+":0#END#";
			send(command);
		}
	}
	

	protected void send(String message) {		
		System.out.println("Sending command("+message+")");
		try{
			sendData = message.getBytes();
			sendPacket.setData(sendData);
			clientSocket.send(sendPacket);			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}		
	}
	
	public void run() {		
	
		parseDevices();
		
		initServerSocket();
		initClientSocket();
		
		//send("#SALAD#0:2:2:0#END#"); // ask to send the list, TODO temporary
		send("#SALAD#0:1:2:0#END#"); // ask to start transmission, TODO temporary
		
		bIsRunning = true;
		while (isRunning()) {
			//Waiting for data
			try {
				serverSocket.receive(dataPacket);
				//Get received data
				String dataStr = new String(dataPacket.getData(), 0, dataPacket.getLength());
				//System.out.println("Received the following from SALAD:");
				//System.out.println(dataStr);
				
				if (dataStr.startsWith("#SALADST#")) {
					StringTokenizer st0 = new StringTokenizer(dataStr, "#");
					st0.nextToken();
					StringTokenizer st = new StringTokenizer(st0.nextToken(), ":");
					//st.nextElement(); // skip the first element
					while (st.hasMoreElements()) {
						String singleUpdate = st.nextToken();
						StringTokenizer st2 = new StringTokenizer(singleUpdate, ";");
						int deviceId = Integer.parseInt(st2.nextToken());
						//System.out.println("deviceId="+deviceId);
						String nameUpdate = "unknown";
						DeviceDescription deviceDescripton = mapDevices.get(deviceId);
						if (deviceDescripton != null) {
							nameUpdate = deviceDescripton.getSymbolicName();
						}
						
						double doubleValue = Double.parseDouble(st2.nextToken());
						DeviceUpdate update = new DeviceUpdate();
						if (doubleValue == Math.floor(doubleValue)) {
							update.add(new SensorUpdate(nameUpdate, (int)doubleValue));
						} else {
							update.add(new SensorUpdate(nameUpdate, doubleValue));
						}
						RubiconAddress sender = new RubiconAddress(getIslandID(), this.getNetworkName(), deviceId);
						RubiconAddress proxy = new RubiconAddress(getIslandID(), 0);
						RubiconMessageUpdates rubiconMessage = new RubiconMessageUpdates(sender, update);
						sendToGateway(rubiconMessage, proxy);
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				serverSocket.close();
				initServerSocket();
			}
		}
		
		send("#SALAD#0:0:2:0#END#"); // ask to stop transmission, TODO temporary
		serverSocket.close();
		clientSocket.close();
	}

	public static void main(String[] args) {
		
		try {
			DatagramSocket serverSocket = new DatagramSocket(9103);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*
		DatagramSocket clientSocket = null;
		try {
			clientSocket = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		InetAddress inetAddress = null;
		try {
			inetAddress = InetAddress.getByName("192.168.1.142");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("Host IP Address is "+inetAddress.getHostAddress());
		byte[] sendData = new byte[2048];
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, inetAddress, 9000);
		
		String message="#SALAD#0:1:2:0#END#";
		System.out.println("Sending command("+message+")");
		try{
			sendData = message.getBytes();
			sendPacket.setData(sendData);
			clientSocket.send(sendPacket);			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
				
		*/
		
		/*
		try {
			DatagramSocket serverSocket = new DatagramSocket(9100);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		/*
		System.out.println("Test RubiconNetworkSalad interface to RUBICON Peis Proxy");
		RubiconNetworkSalad networkSalad = new RubiconNetworkSalad();
		
		Properties properties = new Properties();
		
		properties.put(SALAD_HOST, "192.168.1.142");
		properties.put(SOCKET_ACCEPT_TIMEOUT_MILLISECONDS, "10000");
		properties.put(SOCKET_INIT_RETRY_MILLISECONDS, "5000");
		
		//properties.put(SALAD_PORT, "9000");
		//properties.put(RUBICON_PORT, nRubiconPort);
		//properties.put(SOCKET_ACCEPT_TIMEOUT_MILLISECONDS, socketAcceptTimeout);
		//properties.put(SOCKET_BUFFER_SIZE_BYTES, socketBufferSize);		
		//properties.put(SOCKET_INIT_RETRY_MILLISECONDS, socketInitRetryDelay);
		System.out.println("Properties is "+properties);
		
		try {
			networkSalad._init(1, null, properties);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}
}
