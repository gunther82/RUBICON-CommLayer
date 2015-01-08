package net.tinyos.packet;

import net.tinyos.packet.PacketSource;
import net.tinyos.packet.PhoenixSource;
import net.tinyos.util.Messenger;

/**
 * Extends the standard TinyOS PhoenixSource to export a public constructor so that it
 * can be created from ExtendedBuildSource
 * @author Mauro Dragone <mauro.dragone@ucd.ie>
 *
 */
public class ExtendedPhoenixSource extends PhoenixSource {
	
	 /**
     * Public constructor
     */
    public ExtendedPhoenixSource(PacketSource source, Messenger messages) {
    	super(source, messages);
    }

}
