/**
 * 
 */
package ucd.rubicon.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;


/**
 * Control command message used to send the CMD_READ_PERIODICALLY instruction to a device
 * 
 * @author Mauro Dragone - UCD (<a href="mailto:mauro.dragone@ucd.ie">Contact</a>)
 * @version %I%, %G%
 * 
 */
public class RubiconMessageCtrlPeriodicCmd extends RubiconMessage {
	
	protected short sensorBitmask;
	protected short period;
	
	/**
	 * @param sender
	 * @param sensorsBitmask the bitmsk of the sensors (see {@link RubiconSensorTypes}) to instruct
	 * @param period the period (in milliseconds) to which the device must send sensor updates
	 */
	public RubiconMessageCtrlPeriodicCmd(RubiconAddress sender, Properties resources, short period) {
		super(sender);
		this.sensorBitmask = 0;//sensorsBitmask; // for the moment send the update of all sensors installed on the device
		this.period = period;				
	}

	public short getSensorBitmask() {
		return sensorBitmask;
	}

	public void setSensorBitmask(short sensorBitmask) {
		this.sensorBitmask = sensorBitmask;
	}

	public short getPeriod() {
		return period;
	}

	public void setPeriod(short period) {
		this.period = period;
	}			
}
