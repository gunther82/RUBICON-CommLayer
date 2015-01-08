/**
 * 
 */
package ucd.rubicon.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * Control command message used to send the CMD_READ_PERIODICALLY instruction to a device
 * 
 * @author Mauro Dragone - UCD (<a href="mailto:mauro.dragone@ucd.ie">Contact</a>)
 * @version %I%, %G%
 * 
 */
public class RubiconMessageCtrlActuateCmd extends RubiconMessage {
	
	protected short actuator;
	
	protected int value;
	
	/**
	 * @param sender
	 * @param sensorsBitmask the bitmsk of the sensors (see {@link RubiconSensorTypes}) to instruct
	 * @param period the period (in milliseconds) to which the device must send sensor updates
	 */
	public RubiconMessageCtrlActuateCmd(RubiconAddress sender, short actuator, int value) {
		super(sender);
		this.actuator = actuator;
		this.value = value;
	}

	public short getActuator() {
		return actuator;
	}

	public void setActuator(short actuator) {
		this.actuator = actuator;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
