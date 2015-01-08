package ucd.rubicon.utils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import net.tinyos.packet.PacketSource;
import net.tinyos.util.Messenger;


/**
 * Read packets from an existing binary log file
 * @author Mauro Dragone <mauro.dragone@ucd.ie>
 *
 */
public class FilePacketSource implements PacketSource {

	protected String filePath;
	protected DataInputStream in = null;
	protected Long currentTime = null;

	// TODO: add a speed parameter to allow firing out data at different speed (default normal speed, i.e. as it 
	// was recorded, also enabling maximum speed, i.e. without artificial delay in readPacket
	public FilePacketSource(String filePath) {
		super();
		this.filePath = filePath;
	}
	
	@Override
	public void open(Messenger arg0) throws IOException {
		// TODO, probably should attach arg0 to a tuple or another (local) log
		in = new DataInputStream(new FileInputStream(filePath));
	}

	@Override
	public void close() throws IOException {
		in.close();
		in = null;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}


	@Override
	public byte[] readPacket() throws IOException {
		// read packet, timestamp, filter out write packet (for the moment), wait and returns
		long timeStamp = 0;
		int  packetLength = 0;
		boolean isReadPacket = false;
		byte[] packet = null;
		while (in.available()>0) {
			timeStamp = in.readLong();
			isReadPacket = in.readBoolean();
			packetLength = in.readInt();
			if (isReadPacket) break;
			in.skip(packetLength);
		}
		
		if (in.available()>0 && (packetLength>0) && isReadPacket) {
			packet = new byte[packetLength];
			in.read(packet, 0, packetLength);
			if (currentTime != null) {
				try {
					Thread.sleep(timeStamp-currentTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			currentTime = timeStamp;
		}

		if (packet == null) {
			try {
				System.out.println("FilePacketSource reached the EOF, sleeping from now on ...");
				synchronized(this) {
				    while (true) {
				        this.wait();
				    }
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return packet;
	}

	@Override
	public boolean writePacket(byte[] packet) throws IOException {
		//TODO, for the moment ignore this
		return true;
	}


}
