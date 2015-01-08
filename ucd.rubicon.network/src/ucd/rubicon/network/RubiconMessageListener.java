package ucd.rubicon.network;

/**
 * Interface used to receive a message callback from the Gateway
 * 
 * @author Mauro Dragone - UCD (<a href="mailto:mauro.dragone@ucd.ie">Contact</a>)
 * 
 *
 */
public interface RubiconMessageListener {
	
	/**
	 * Message handlers must override this method to handle incoming messages
	 * @param message the {@Link RubiconMessage} just received
	 * @param destination the {@Link RubiconAddress} of the intended destination
	 */
	public void handleMessageReceived(RubiconMessage message, RubiconAddress destination);  
}
