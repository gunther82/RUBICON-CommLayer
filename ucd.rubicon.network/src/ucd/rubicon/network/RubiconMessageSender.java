package ucd.rubicon.network;

/**
 * Interface used to send a message within RUBICON via the Rubicon Gateway
 *  
 * @author Mauro Dragone - UCD (<a href="mailto:mauro.dragone@ucd.ie">Contact</a>)
 * 
 */
public interface RubiconMessageSender {
	 
	/* 
	 * Forwards a given {@Link RubiconMessage} to a {@Link RubiconAddress}
	 * @param message the message to send
	 * @param destination the {@Link RubiconAddress} of the intended destination
	 * @Exception if the destination is unreachable or an error occurred during data transmission
	 */			
	public void send(RubiconMessage message, RubiconAddress destination) throws Exception;

}
