package ucd.rubicon.utils;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import core.CallbackObject;
import core.PeisJavaMT;
import core.PeisTuple;
import core.PeisJavaInterface.PeisTupleCallback;
import core.PeisJavaMT.ENCODING;

import net.tinyos.packet.PacketSource;
import net.tinyos.util.Messenger;

/**
 * Wraps an existing packet source, and stores every packet read/written to/from the source to a binary log file
 * somewhere in the file system
 *  
 * @author Mauro Dragone <mauro.dragone@ucd.ie>
 *
 */
public class FilePacketForwardingSource extends ForwardingPacketSource {

	protected String filePath;
	protected DataOutputStream out = null;
	
	public FilePacketForwardingSource(PacketSource innerSource, String filePath) {
		super(innerSource);
		this.filePath = filePath;
	}
	
	@Override
	public void open(Messenger arg0) throws IOException {		
		super.open(arg0);
		
		out = new DataOutputStream(new FileOutputStream(filePath));		
	}
	
	@Override
	public void close() throws IOException {
		out.flush();
		out.close();
		out = null;
		super.close();
	}

	protected void addToLog(byte[] packet, boolean isRead) throws IOException {
		long timeStamp = System.currentTimeMillis(); // TODO: Do I need more precision?
		out.writeLong(timeStamp);
		out.writeBoolean(isRead); 
		out.writeInt(packet.length); // 
		out.write(packet);
	}
	
	@Override
	public void forwardReadPacket(byte[] packet) throws IOException {
		addToLog(packet, true);
	}


	@Override
	public void forwardWritePacket(byte[] packet) throws IOException {
		addToLog(packet, false);
	}


}
