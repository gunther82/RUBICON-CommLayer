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
public class PeisPacketForwardingSource extends ForwardingPacketSource {

	protected String rootKey;
	
	public String DOT = ".";
	public String READ = "read";
	public String WRITE = "write";
	
	protected PeisTupleCallback writeCallback = null;
	
	public PeisPacketForwardingSource(PacketSource innerSource, String rootKey) {
		super(innerSource);
		this.rootKey = rootKey;
	}
	
	@Override
	public void open(Messenger arg0) throws IOException {		
		super.open(arg0);
		
		// Creates the write tuple and publish them with a dummy pre-set value to advertise this TinyOS packet forwarding service
		byte empty[] = new byte[1];
		
		if (PeisJavaMT.peisjava_peisid()<0) {
			System.out.println("PEIS was not initialized, initializing...");
			try {
				PeisJavaMT.peisjava_initialize(null);
			} catch (WrongPeisKernelVersionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		PeisJavaMT.peisjava_setTuple(rootKey+DOT+WRITE, empty, PeisPacketSource.ENCODING, ENCODING.BINARY);

		// Installs a callback to the write tuple
		writeCallback = PeisJavaMT.peisjava_registerTupleCallback(
				PeisJavaMT.peisjava_peisid(),
				rootKey+DOT+WRITE,				  
				new CallbackObject() {
					@Override
					public void callback(PeisTuple tuple) {
						try {
							writePacket(tuple.getByteData());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
					}
				}
		);

	}


	@Override
	public void close() throws IOException {		
		PeisJavaMT.peisjava_deleteTuple(PeisJavaMT.peisjava_peisid(), rootKey+DOT+READ);
		PeisJavaMT.peisjava_deleteTuple(PeisJavaMT.peisjava_peisid(), rootKey+DOT+WRITE);
		if (writeCallback != null) {
			PeisJavaMT.peisjava_unregisterTupleCallback(writeCallback);
			writeCallback = null;
		}
		super.close();
	}

	@Override
	public void forwardReadPacket(byte[] packet) throws IOException {
		PeisJavaMT.peisjava_setTuple(rootKey+DOT+READ, packet, PeisPacketSource.ENCODING, ENCODING.BINARY);
	}


	@Override
	public void forwardWritePacket(byte[] packet) throws IOException {
		// do nothing
	}


}
