/**
 * 
 */
package ucd.rubicon.network;

import java.util.HashMap;
import java.util.Properties;


/**
 * Specification of the devices sensing and actuator capabilities, as well as their basic network information, including
 * island and node address.
 * 
 * @author Davide Bacciu - UNIPI (<a href="mailto:bacciu@di.unipi.it">Contact</a>)
 * @version %I%, %G%
 * 
 * @see DeviceRepository
 */
public class RubiconMessageJoin extends RubiconMessage {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RubiconMessageJoin(RubiconAddress sender) {
		super(sender);
	}
	
	/**
	 * Map of resources (sensors or actuators names to type)
	 */
	protected Properties resources = new Properties();
	
	/**
	 * Network address of the (WSN) island hosting the device
	 */
	protected short island_addr;
	
	/**
	 * Network address of the device
	 */
	protected int addr;
	
	/**
	 * Device type (currently unused)
	 */
	protected int type;	
	
	/**
	 * Device name
	 */
	protected String name;

	
	public short getIsland_addr() {
		return island_addr;
	}
	
	public void setIsland_addr(short island_addr) {
		this.island_addr = island_addr;
	}
	
	public int getAddr() {
		return addr;
	}
	
	public void setAddr(int addr) {
		this.addr = addr;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
			
	public void setResources(Properties resources) {
		this.resources.clear();
		this.resources.putAll(resources);
	}
	
	public Properties getResources() {
		return resources;
	}
	
}
