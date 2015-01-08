package ucd.rubicon.utils;

import java.io.IOException;

import core.CallbackObject;
import core.PeisJavaMT;
import core.PeisTuple;
import core.WrongPeisKernelVersionException;
import core.PeisJavaInterface.PeisTupleCallback;
import core.PeisJavaMT.ENCODING;

import net.tinyos.packet.PacketSource;
import net.tinyos.util.Messenger;

/**
 * Wraps an existing packet source, and allows other Peis components to interact with the same source
 * @author Mauro Dragone <mauro.dragone@ucd.ie>
 *
 */
public class PeisPacketSource implements PacketSource {

	protected String rootKey;
	protected int peisID = 0;
	protected PeisTupleCallback readCallback = null;
	
	public static String DOT = ".";
	public static String READ = "read";
	public static String WRITE = "write";	
	public static String ENCODING = "application/rubicon/tinyos";
	
	protected PeisTupleCallback writeCallback = null;
	
	public PeisPacketSource(String rootKey, int peisID) {
		super();
		this.rootKey = rootKey;
		this.peisID = peisID;
	}

	public PeisPacketSource(String rootKey) {
		this(rootKey, -1);
	}
	
	@Override
	public void close() throws IOException {	
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void open(Messenger arg0) throws IOException {
		// TODO, probably should attach arg0 to a tuple or another (local) log
		
		if (PeisJavaMT.peisjava_peisid()<0) {
			System.out.println("PEIS was not initialized, initializing...");
			try {
				PeisJavaMT.peisjava_initialize(null);
			} catch (WrongPeisKernelVersionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}

		// if peisID was not specified, subscribes to get the first update to find out the owner
		if (getPeisID() <0) {
			PeisJavaMT.peisjava_subscribe(-1, rootKey+DOT+READ);
			
			System.out.println("PeisPacketSource::open(), callback to "+rootKey+DOT+READ);
			readCallback = PeisJavaMT.peisjava_registerTupleCallback(
				-1,
				rootKey+DOT+READ,
				new CallbackObject() {
					@Override
					public void callback(final PeisTuple tuple) {
						//System.out.println("Called, owner is "+ tuple.owner);
						setPeisID(tuple.owner);
					}
				}
			);
			
		}
	}

	protected synchronized int getPeisID() {
		return peisID;
	}
	
	protected synchronized void setPeisID(int peisID) {
		if (peisID <0 || readCallback!=null) { 
			this.peisID = peisID;		
			System.out.println(getName()+" setting source's peis-id to "+peisID);
			if (readCallback != null) {
				System.out.println(getName()+" peisjava_unregisterTupleCallback");
				// TODO: should also unsubscribe 
				//PeisJavaMT.peisjava_unregisterTupleCallback(readCallback);
				readCallback = null;
			}		
			this.notify();
		}
	}

	@Override
	public byte[] readPacket() throws IOException {
		
		if (getPeisID()<0) {
			// I still don't know the owner, better wait for the first update
			System.out.println(getName()+" readPacket, peisID is -1, waiting...");
			synchronized (this) {
				try {
					this.wait();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}
		}
		
		return PeisJavaMT.peisjava_getByteTuple(getPeisID(), rootKey+DOT+READ, PeisJavaMT.FLAG.BLOCKING, PeisJavaMT.FLAG.FILTER_OLD);
	}

	@Override
	public boolean writePacket(byte[] packet) throws IOException {
		if (getPeisID() == PeisJavaMT.peisjava_peisid()) {
			// write to the local tuple
			PeisJavaMT.peisjava_setTuple(rootKey+DOT+WRITE, packet, ENCODING, PeisJavaMT.ENCODING.BINARY);
		} else {
			// write to the remote tuple
			PeisJavaMT.peisjava_setRemoteTuple(peisID, rootKey+DOT+WRITE, packet, ENCODING, PeisJavaMT.ENCODING.BINARY);
		}
		
		return true;
	}


}
