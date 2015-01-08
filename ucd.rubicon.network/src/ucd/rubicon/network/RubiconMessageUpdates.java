/**
 * 
 */
package ucd.rubicon.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Control command message used to send the CMD_UPDATE instruction from a device to update Rubicon about the value of a device (sensor or actuator)
 * 
 * @author Mauro Dragone - UCD (<a href="mailto:mauro.dragone@ucd.ie">Contact</a>)
 * @version %I%, %G%
 * 
 */
public class RubiconMessageUpdates extends RubiconMessage {
		
	protected DeviceUpdate update;
	
	public RubiconMessageUpdates(RubiconAddress sender, DeviceUpdate update) {
		super(sender);
		this.update = update;
	}

	public DeviceUpdate getUpdate() {
		return update;
	}

	public void setUpdate(DeviceUpdate update) {
		this.update = update;
	}
		
}
