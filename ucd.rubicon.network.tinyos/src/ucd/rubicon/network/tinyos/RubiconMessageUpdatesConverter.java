
package ucd.rubicon.network.tinyos;

import java.util.Properties;
import java.util.Set;

import ucd.rubicon.network.DeviceUpdate;
import ucd.rubicon.network.RubiconAddress;
import ucd.rubicon.network.RubiconMessageUpdates;
import ucd.rubicon.network.SensorUpdate;
import cnr.rubicon.migFiles.UpdatesMsg;

/**
 * Control command message used to send the CMD_UPDATE instruction from a device to update Rubicon about the value of a device (sensor or actuator)
 * 
 * @author Mauro Dragone - UCD (<a href="mailto:mauro.dragone@ucd.ie">Contact</a>)
 * @version %I%, %G%
 * 
 */
public class RubiconMessageUpdatesConverter extends RubiconMessageConverterTinyOS<UpdatesMsg, RubiconMessageUpdates> {

	@Override
	public UpdatesMsg getNetworkFormat(
			RubiconMessageUpdates rubiconMessage,
			RubiconAddress sender,
			RubiconAddress destination) throws Exception {
		throw new Exception("Not supported"); // this is a one-way message from network to Rubicon
	}

	@Override
	public RubiconMessageUpdates getRubiconFormat(
			UpdatesMsg networkFormat,
			RubiconAddress sender,
			RubiconAddress destination) throws Exception {

		System.out.println("RubiconMessageUpdatesConverter::getRubiconFormat ");
		DeviceUpdate deviceUpdate = new DeviceUpdate();
		
		int[] reads = networkFormat.get_updates();
		//for(int i = 0; i < networkFormat.get_output_size(); i++) {
		int i=0; 
		while (i< networkFormat.get_output_size()) { 
			int sensorType = reads[i++];
						
			Properties resources = TypeConverter.getResources(sensorType, (short)0);
			Set<String> setSensorNames = resources.stringPropertyNames();
			for (String sensorName: setSensorNames) {
				int sensorValue = reads[i++];
				SensorUpdate sensorUpdate = new SensorUpdate(resources.getProperty(sensorName), sensorName, sensorValue);
				System.out.println("adding sensor updateo "+sensorName+" value:"+sensorValue);
				deviceUpdate.add(sensorUpdate);
				//break;
			}
		}
			
		return new RubiconMessageUpdates(sender, deviceUpdate);

	}

	@Override
	public Class<UpdatesMsg> getClassNetworkFormat() {
		return UpdatesMsg.class;
	}

	@Override
	public Class<RubiconMessageUpdates> getClassRubiconFormat() {
		return RubiconMessageUpdates.class;
	}

	
}
