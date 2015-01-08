package ucd.rubicon.network;

public interface RubiconMessageConverter<NT, RT extends RubiconMessage> {
		
	/**
	 * Translate a Rubicon Message into a network specific representation
	 * @param rubiconMessage
	 * @throws Exception if the operation is not supported
	 */
	public NT getNetworkFormat(RT rubiconMessage, RubiconAddress sender, RubiconAddress destination) throws Exception;
	
	/**
	 * Translate a network representation of a Rubicon message into a Rubicon Message
	 * @param rubiconMessage
	 * @throws Exception if the operation is not supported
	 */	
	public RT getRubiconFormat(NT networkFormat, RubiconAddress sender, RubiconAddress destination) throws Exception;
	
	
	 /**
     * Method returns class of Network Format
     *
     * @return Class<NT>
     */
    public Class<NT> getClassNetworkFormat();
    
    /**
     * Method returns class of Rubicon Format
     *
     * @return Class<RT>
     */
    public Class<RT> getClassRubiconFormat();
	
}
