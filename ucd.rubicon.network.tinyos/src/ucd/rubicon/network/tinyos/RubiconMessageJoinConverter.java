/**
 * 
 */
package ucd.rubicon.network.tinyos;

import ucd.rubicon.network.RubiconAddress;
import ucd.rubicon.network.RubiconMessageJoin;
import cnr.rubicon.migFiles.SerialJoinedMsg;


/**
 * Control command message used by a device to send to join an island
 * Contains information about device's sensing and actuating capabilities
 * 
 * @author Mauro Dragone - UCD (<a href="mailto:mauro.dragone@ucd.ie">Contact</a>)
 * @version %I%, %G%
 * 
 */
public class RubiconMessageJoinConverter extends RubiconMessageConverterTinyOS<SerialJoinedMsg, RubiconMessageJoin> {


	@Override
	public SerialJoinedMsg getNetworkFormat(
			RubiconMessageJoin rubiconMessage,
			RubiconAddress sender,
			RubiconAddress destination) throws Exception {
		
		throw new Exception("Not supported"); // this is a one-way message from network to Rubicon
		
	}

	@Override
	public RubiconMessageJoin getRubiconFormat(
			SerialJoinedMsg networkMessage,
			RubiconAddress sender,
			RubiconAddress destination) {

		System.out.println("RubiconMessageJoinConverter::getRubiconFormat ");
		RubiconMessageJoin msg = new RubiconMessageJoin(sender);
		short actuatorsMask = networkMessage.get_actuators();
		int transducersMask = networkMessage.get_transducers();
		msg.setResources(TypeConverter.getResources(transducersMask, actuatorsMask));
		msg.setAddr(networkMessage.get_mote_addr());
		msg.setIsland_addr((short)networkMessage.get_island_addr());
		
		return msg;
	}

	@Override
	public Class<SerialJoinedMsg> getClassNetworkFormat() {
		return SerialJoinedMsg.class;
	}

	@Override
	public Class<RubiconMessageJoin> getClassRubiconFormat() {
		return RubiconMessageJoin.class;
	}
	

		
	
}
