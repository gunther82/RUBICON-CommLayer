package ucd.rubicon.proxy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Hashtable;

import core.PeisJavaInterface.PeisSubscriberHandle;
import core.CallbackObject;
import core.PeisJavaInterface.PeisTupleCallback;
import core.PeisJavaMT;
import core.PeisTuple;

public class PeisLog {
	protected int	 owner;
	protected String keys[];
	protected PeisSubscriberHandle handleTupleToLogs[];
	protected PeisSubscriberHandle handleLog;
	protected PeisTupleCallback callbacks[];
	protected boolean isLogging = false;
	protected Hashtable mapWriters = new Hashtable();
	protected long startTime = 0;
	
	public static String LOG = "LOG";
	
	protected synchronized void start() throws IOException {

		startTime = PeisJavaMT.peisjava_gettime();
		for (int t=0; t< keys.length; t++) {
						
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(keys[t]+".tuple")));
			mapWriters.put(keys[t], bw);			
			
			handleTupleToLogs[t] =  PeisJavaMT.peisjava_subscribe(owner, keys[t]);
			
						
			callbacks[t] =	
				PeisJavaMT.peisjava_registerTupleCallback(
					owner,
					keys[t],
					new CallbackObject() {
					@Override
					public void callback(final PeisTuple tuple) {
						if (tuple.getStringData() != null && !tuple.getStringData().equals("") && !tuple.getStringData().equals("()")) {
							write(tuple.getKey(), tuple.getStringData());
						}
					}
				}						
			);
		}

		isLogging =  true;
	}
	
	protected void stop() {
		isLogging = false;
		for (int t=0; t< keys.length; t++) {
			//PeisJavaMT.peisjava_unsubscribe(handleTupleToLogs[t]);
			//PeisJavaMT.peisjava_unregisterTupleCallback(callbacks[t]);
			try {
				BufferedWriter writer = (BufferedWriter) mapWriters.get(keys[t]);
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    	        
		}
	}

	protected void write(String key, String str) {
		if (!isLogging) return;
		System.out.println("Logging key "+key+":"+str);
		long time = PeisJavaMT.peisjava_gettime() - startTime;
		BufferedWriter writer = (BufferedWriter)mapWriters.get(key);
		try {
			System.out.println("writer.write("+str+")");	
			writer.write(str);
			writer.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PeisLog(int owner, String keys[]) {
		super();
		this.owner = owner;
		this.keys = new String[keys.length];
		for (int i=0; i<keys.length; i++) {
			this.keys[i] = new String(keys[i]);
		}
		
		// To Ctrl Log:
		// START  
		//
		PeisJavaMT.peisjava_setStringTuple(LOG, "OFF");
		handleLog = PeisJavaMT.peisjava_subscribe(PeisJavaMT.peisjava_peisid(), LOG);
		handleTupleToLogs =  new PeisSubscriberHandle[keys.length];
		callbacks = new PeisTupleCallback[keys.length];
		
		PeisJavaMT.peisjava_registerTupleCallback(
				PeisJavaMT.peisjava_peisid(),
				LOG,
				new CallbackObject() {
					@Override
					public void callback(final PeisTuple tuple) {
						System.out.println("PeisLog callback, LOG tuple is "+ tuple);
						if (tuple.getStringData() != null && !tuple.getStringData().equals("") && !tuple.getStringData().equals("()")) {
							final String cmd = tuple.getStringData();
							if (cmd.equalsIgnoreCase("ON")) {
								try {
									start();
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (UnsupportedEncodingException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else if (cmd.equalsIgnoreCase("OFF")) {
								stop();
							}
						}
					}
				}				
		);

	}

}
