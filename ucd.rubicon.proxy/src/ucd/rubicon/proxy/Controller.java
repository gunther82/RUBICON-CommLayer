package ucd.rubicon.proxy;

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import core.CallbackObject;
import core.PeisJavaMT;
import core.PeisTuple;
import core.PeisTupleResultSet;
import core.PeisJavaInterface.PeisSubscriberHandle;
import core.PeisJavaMT.FLAG;

public class Controller {

	public static String COMMAND_KEY = "command";
	public static String PARAMETER_KEY = "parameter";
	protected String actuatorName = null;
	protected Hashtable mapping = new Hashtable();
	protected String name = null;
	
	protected PeisSubscriberHandle hndlRequest;
	protected PeisSubscriberHandle hndlParameter;
	protected CallbackObject callback;

	static protected ExecutorService executor = Executors.newCachedThreadPool();
	
	public Controller(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void parseFromString(String str) throws Exception {
		StringTokenizer st = new StringTokenizer(str, ",");
		if (st.countTokens()<1) {
			throw new Exception("Invalid controller description, expected controller_<name>=<actuator-name>(,<parameter-value>:<actuator-value>)+");
		}
		//setRubiconType(Integer.parseInt(st.nextToken()));
		setActuatorName(st.nextToken());
		while (st.hasMoreTokens()) {
			StringTokenizer st2 = new StringTokenizer(st.nextToken(), ":");
			mapping.put(st2.nextToken(), st2.nextToken());			
		}
		
		System.out.println("Controller "+str+" mapping:"+mapping);
	}

	public String getActuatorName() {
		return actuatorName;
	}

	public void setActuatorName(String actuatorName) {
		this.actuatorName = actuatorName;
	}
	
	public void processRequest(final PeisTuple tuple) {		
		String command = tuple.getStringData();
		if (!command.equals("ON")) return;
		String key = tuple.getKey();
		String subkeys[] = key.split("\\.");
		String parKey = subkeys[0]+"."+subkeys[1]+".parameter";
		System.out.println("parKey is ("+parKey+") owner is "+tuple.owner);
		PeisSubscriberHandle subscription = PeisJavaMT.peisjava_subscribe(tuple.owner, parKey);
		String parameter = PeisJavaMT.peisjava_getStringTuple(tuple.owner, parKey);
		PeisJavaMT.peisjava_unsubscribe(subscription);
		String value = (String)mapping.get(parameter);
	  
		String keyCommand = RubiconProxy.PROXY+"."+this.getActuatorName()+".command";
		System.out.println("Controller setting "+keyCommand + " to " + value);
		PeisJavaMT.peisjava_setStringTuple(keyCommand, value);
	}

	public void processRequest2(final PeisTuple tuple) {	
		String command = tuple.getStringData();
		System.out.println("in processRequest2, command = "+ command);
		if (!command.equals("ON")) return;
		String subkeys[] = tuple.getKey().split("\\.");
		String keyStar = subkeys[0]+"."+subkeys[1]+"." + subkeys[2] + ".*";
		System.out.println("in processRequest2, peisjava_getTuples("+tuple.owner+","+keyStar);
		PeisTupleResultSet result = PeisJavaMT.peisjava_getTuples(tuple.owner, keyStar, FLAG.BLOCKING);
		PeisTuple res = null;
		
		System.out.println("Sto leggendo le tuple del planner");
		while ((res = result.next()) != null) {							
			String data = res.getStringData();							
			String key = res.getKey(); 
			System.out.println("tuple" + key+"="+data);
			if (key.endsWith("parameters")) {
				String value = (String)mapping.get(data);
				System.out.println("parameters = "+value);
				String keyCommand = RubiconProxy.PROXY+"."+this.getActuatorName()+".command";
				String keyState = subkeys[0]+"."+subkeys[1]+"."+subkeys[2]+".state";
				System.out.println("Controller setting "+keyCommand + " to " + value);
				PeisJavaMT.peisjava_setStringTuple(keyCommand, value);
				//TODO: should wait for the sensor to move before setting state to complete
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Controller setting "+keyState + " to COMPLETED");
				PeisJavaMT.peisjava_setRemoteStringTuple(tuple.owner, keyState, "COMPLETED");
				
				break;
			}
		}
	}
	
	public void start() {
		// TODO test with -1 later
		int id = 995;
		
		System.out.println("Controller starting name="+this.getName());
		hndlRequest = PeisJavaMT.peisjava_subscribe(id, this.getName()+".*.*");
		//hndlParameter = PeisJavaMT.peisjava_subscribe(id, this.getName()+".*.");
		System.out.println("Controller subscribing "+this.getName()+".*."+COMMAND_KEY);
	
		/*
		ControllerBlind.12.state
		ControllerBlind.12.command
		ControllerBlind.12.parameters
		etc

		When pulling up the blind, one of these sets will be

		...
		ControllerBlind.12.command = ON
		ControllerBlind.12.parameters = UP
		*/
		
		System.out.println("Proxy controller registering callback to tuple: "+getName()+".*." +  COMMAND_KEY);
		/*callback =*/ PeisJavaMT.peisjava_registerTupleCallback(
			-1,
			getName()+".*." + COMMAND_KEY,
			new CallbackObject() {
				@Override
				public void callback(final PeisTuple tuple) {
					try {
						System.out.println("Controller callback"+ tuple);
						if (tuple.getStringData() != null && !tuple.getStringData().equals("") && !tuple.getStringData().equals("()")) {
							
							executor.submit(
									new Callable() {
										public Object call() {
											try {
												processRequest2(tuple);
						    				} catch(Throwable t) {
						    					t.printStackTrace();
						    					//TODO log
						    				}
											return null;					    					 
										}
									});

						}
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			} //End CallbackObject definition
		);

	}
	
	public void stop() {
		
	}
	
}
