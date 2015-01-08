package ucd.rubicon.network;

import java.io.Serializable;

/**
 * Describes the Rubicon address used to identify a communication end-point (device, software component) within a multi-island RUBICON system
 *  
 * @author Mauro Dragone - UCD (<a href="mailto:mauro.dragone@ucd.ie">Contact</a>)
 *
 */
public class RubiconAddress implements Serializable {

	protected static String PROXY = "proxy"; 
	/**
	 * The Peis id of the RUBICON gateway that manages the island
	 * TODO: Mauro: should this not be a short?
	 */
	int peisId;   
	
	/**
	 * The id of the communication end-point (e.g. either a physical or a virtual/software device)
	 */
	int deviceId;    
	
	/**
	 * The identifier of the network originating this message
	 */
	String networkName;    

	/**
	 * Defines a new address
	 * @param peisId The Peis id of the RUBICON Gateway that manages the island 
	 * @param deviceId The id of the communication end-point (e.g. either a physical or a virtual/software device)
	 */
	public RubiconAddress(int peisId, String networkName, int deviceId) {
		this.peisId = peisId;
		this.deviceId = deviceId;
		this.networkName = networkName;
	}
		
	
	/**
	 * Defines a new address without specifying the deviceId [set to 0]
	 * @param peisId The Peis id of the RUBICON Gateway that manages the island
	 */
	public RubiconAddress(int peisId, int deviceId) {
		this(peisId, PROXY, 0);
	}

	/**
	 * 
	 * @return The Peis id of the RUBICON Gateway that manages the island
	 */
	public int getPeisId() {
		return peisId;
	}
	
	/**
	 * 
	 * @return The id of the communication end-point (e.g. either a physical or a virtual/software device)
	 */
	public int getDeviceId() {
		return deviceId;
	}
	
	public int hashCode() {
		return peisId*Short.MAX_VALUE + deviceId;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof RubiconAddress)) return false;
		RubiconAddress a = (RubiconAddress)obj;
		return (peisId==a.peisId && deviceId==a.deviceId);
	}
	
	public String toString() {
		return ""+peisId+":"+deviceId;
	}


	public String getNetworkName() {
		return networkName;
	}


	//public void setNetworkName(String networkName) {
	//	this.networkName = networkName;
	//}

	
}
