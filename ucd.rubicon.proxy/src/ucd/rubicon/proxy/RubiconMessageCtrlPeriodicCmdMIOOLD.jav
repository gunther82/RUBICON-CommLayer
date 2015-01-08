/**
 * 
 */
package ucd.rubicon.proxy;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import ucd.rubicon.network.RubiconAddress;
import ucd.rubicon.network.RubiconMessage;
import ucd.rubicon.network.RubiconSensorTypes;


/**
 * Control command message used to send the CMD_READ_PERIODICALLY instruction to a device
 * 
 * @author Mauro Dragone - UCD (<a href="mailto:mauro.dragone@ucd.ie">Contact</a>)
 * @version %I%, %G%
 * 
 */
public class RubiconMessageCtrlPeriodicCmd extends RubiconMessage {
	
	public RubiconMessageCtrlPeriodicCmd(RubiconMessage message) {
		super(message.getSender(), (byte)RubiconMessageCtrlTypesEnum.CMD_READ_PERIODICALLY.getCmdType(), message.getUserObject());
	}
	
	/**
	 * @param sender
	 * @param sensorsBitmask the bitmsk of the sensors (see {@link RubiconSensorTypes}) to instruct
	 * @param period the period (in milliseconds) to which the device must send sensor updates
	 */
	public RubiconMessageCtrlPeriodicCmd(RubiconAddress sender, short sensorsBitmask, short period) {
		super(sender, (byte)RubiconMessageCtrlTypesEnum.CMD_READ_PERIODICALLY.getCmdType());  	
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeByte(3);
			dos.writeByte(5);
			dos.writeByte(messageType);
			dos.writeShort(sensorsBitmask);
			dos.writeShort(period);
			dos.flush();
			userObject = bos.toByteArray();
		} catch (IOException e) {
			// TODO LOG
			e.printStackTrace();
		}
	}			
}
