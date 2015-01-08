package ucd.rubicon.proxy;

import java.util.Properties;

import ucd.rubicon.gateway.RubiconGateway;
import ucd.rubicon.network.RubiconAddress;
import ucd.rubicon.network.RubiconMessage;
import ucd.rubicon.network.RubiconMessageJoin;
import ucd.rubicon.network.RubiconMessageListener;

import com.sun.jna.Pointer;

import core.CallbackObject;
import core.PeisJavaMT;
import core.PeisTuple;
import core.WrongPeisKernelVersionException;
import core.PeisJavaInterface.PeisSubscriberHandle;
import core.PeisJavaInterface.PeisTupleCallback;
import core.PeisJavaMT.ENCODING;

public class RubiconProxy implements RubiconMessageListener, Runnable {

	protected static String PROPERTIES_FILE = "proxy.properties";
	protected static String PROPERTY_CMD_REFRESH_PERIOD = "proxy.cmd_refresh_period";
	protected static String PROPERTY_KEEPALIVE_WAITING_PERIOD = "proxy.keepalive_waiting_period";
	protected int CMD_REFRESH_PERIOD = 5; // [seconds]
	protected int KEEPALIVE_WAITING_PERIOD = 10; // [seconds]
			
	protected Properties properties;
	protected DeviceRepository deviceRepository;
	protected Thread thread;
	protected RubiconGateway gateway;
	
	public RubiconProxy() {
		gateway = new RubiconGateway();		
		deviceRepository = new DeviceRepository();
		properties = new Properties(); 
		try {
		    properties.load(RubiconGateway.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
		} 
		catch (Exception ex) {
			System.out.println("RUBICON Proxy: Missing property file ("+PROPERTIES_FILE+") for RUBICON Gateway, it should be on the classpath.");
			System.exit(-1);
		}
		
		thread = new Thread(this);
		gateway.registerListener(this); // Mauro, before or after???
		thread.start();
	}
	
	@Override
	public void handleMessageReceived(RubiconMessage message,
			RubiconAddress destination) {
		
		if (message instanceof RubiconMessageJoin ) {
			System.out.println("RUBICON Proxy: Received message join, adding it to the repository");
			deviceRepository.addDevice((RubiconMessageJoin)message);
		} else {
			System.out.println("RUBICON Proxy: Received other message, checking type:"+message.getMessageTypeType());
			if (message.getMessageTypeType() == RubiconMessageCtrlTypesEnum.CMD_UPDATE.getCmdType()) {
				System.out.println("RUBICON Proxy: Received CtrlUpdate message");				
				deviceRepository.updateNode(new RubiconMessageCtrlUpdate(message));
			}
		}
		
		deviceRepository.print();
	}

	public static void main(String[] args) {		
				
		try {
			System.out.println("RUBICON Proxy: Initializing PeisJavaMT...");
			PeisJavaMT.peisjava_initialize(args);
			
			System.out.println("PeisID is "+ PeisJavaMT.peisjava_peisid());
			
			//RubiconGateway gateway = new RubiconGateway();
			RubiconProxy proxy = new RubiconProxy();			
								
		} catch (WrongPeisKernelVersionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}		
	}

	@Override
	public void run() {
//		while (true) {
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			short sensorsBitmask = (short)3;
			short period = 1000;
			RubiconAddress sender = new RubiconAddress(PeisJavaMT.peisjava_peisid(), 0xFFFD);
			RubiconAddress address = new RubiconAddress(PeisJavaMT.peisjava_peisid(), 1);
			RubiconMessageCtrlPeriodicCmd cmd = new RubiconMessageCtrlPeriodicCmd(
				address, sensorsBitmask, period);
			try {
				gateway.send(cmd,  address);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}		
	}
	
}
