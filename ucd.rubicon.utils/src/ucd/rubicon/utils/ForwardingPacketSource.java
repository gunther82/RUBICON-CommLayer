package ucd.rubicon.utils;

import java.io.IOException;

import net.tinyos.packet.PacketSource;
import net.tinyos.util.Messenger;

/** 
 * Defines a packet source that can be chained to other packet sources
 *  
 * Sub-classes can override the methods of this class, but must call their base implementation
 * in order to assure that each call is correctly forwarded to inner sources that may be chained with them
 *   
 * @author Mauro Dragone <mauro.dragone@ucd.ie>
 *
 */
public abstract class ForwardingPacketSource implements PacketSource {

	protected PacketSource innerSource = null; 
	
	public ForwardingPacketSource(PacketSource innerSource) {
		super();
		this.innerSource = innerSource;
	}
	
	@Override
	public void close() throws IOException {
		if (innerSource != null) {
			innerSource.close();
		}		
	}

	@Override
	public void open(Messenger arg0) throws IOException {
		if (innerSource != null) {
			innerSource.open(arg0);
		}				
	}

	@Override
	public final byte[] readPacket() throws IOException {
		if (innerSource != null) {
			byte[] packet = innerSource.readPacket();
			forwardReadPacket(packet);
			return packet;
		}
		return null;
	}

	@Override
	public final boolean writePacket(byte[] arg0) throws IOException {
		if (innerSource != null) {
			innerSource.writePacket(arg0);
			forwardWritePacket(arg0);
		}
		return false;
	}
		
	@Override
	public String getName() {
		return getClass().getSimpleName();
	}
	
	public abstract void forwardReadPacket(byte[] packet) throws IOException;

	public abstract void forwardWritePacket(byte[] packet) throws IOException;

}
