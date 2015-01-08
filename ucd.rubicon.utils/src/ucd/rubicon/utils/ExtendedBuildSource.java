// $Id: BuildSource.java,v 1.4 2006/12/12 18:23:00 vlahan Exp $

/*									tab:4
 * "Copyright (c) 2000-2003 The Regents of the University  of California.  
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for any purpose, without fee, and without written agreement is
 * hereby granted, provided that the above copyright notice, the following
 * two paragraphs and the author appear in all copies of this software.
 * 
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 * DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING OUT
 * OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF THE UNIVERSITY OF
 * CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE.  THE SOFTWARE PROVIDED HEREUNDER IS
 * ON AN "AS IS" BASIS, AND THE UNIVERSITY OF CALIFORNIA HAS NO OBLIGATION TO
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS."
 *
 * Copyright (c) 2002-2003 Intel Corporation
 * All rights reserved.
 *
 * This file is distributed under the terms in the attached INTEL-LICENSE     
 * file. If you do not find these files, copies can be found by writing to
 * Intel Research Berkeley, 2150 Shattuck Avenue, Suite 1300, Berkeley, CA, 
 * 94704.  Attention:  Intel License Inquiry.
 */
package ucd.rubicon.utils;

import java.io.IOException;
import java.util.StringTokenizer;

import net.tinyos.packet.BuildSource;
import net.tinyos.packet.ExtendedPhoenixSource;
import net.tinyos.packet.PacketSource;
import net.tinyos.packet.PhoenixSource;
import net.tinyos.util.*;

/**
 * This class opens and extends the default BuildSource class
 * in the TinyOS Java SDK, by supporting other packet sources, i.e. based on Peis and Log forwarding sources 
 * and by allowing to define chains between these new sources and standard TinyOS packet sources.
 *
 * @author Mauro Dragone <mauro.dragone@ucd.ie>
 *
 */
public class ExtendedBuildSource extends BuildSource {
	
	public static String PEIS_PREFIX = "peis@";
	public static String FILE_PREFIX = "file@";
	public static String COLON = ":";
	public static String AT = "@";
	public static String CHAIN = "+";
	

    /**
     * Make the specified packet source
     *     
     * @param name Name of the packet source, or null for "sf@localhost:9002"
     * @return The packet source, or null if it could not be made
     * 
     *      * It extends the makePacketSource of the standard BuildSource class by supporting Peis and Log forwarding sources and by allowing to
     * define chains between these new sources and standard TinyOS sources.
     * 
     * Examples:
     *     source+peis@Name (e.g. "serial@dev/ttyUSB0:telosb+peis@WSN0") creates  a source (specified by the parameter source)
     *        if the a peis@Name source is included (after the symbol ">>"), the access to the first source is re-exported to Peis
     *        , using a tuple with root key Name, and with the same peis-id used by the current process
     *     
     *     peis@[peis-id:]Name creates a source that reads/writes packets to/from an already existing source with a Peis interface
     *        the source may or not include the peis-id of the process that exports the peis source. 
     *        If the peis-id is not included, it will try to connect to first peis source it finds with the name specified,
     *        starting from the local peis-id.
     *           
     *     source+log@filename (e.g. serial@dev/ttyUSB0:telosb+peis@log1.pkt) to log packets read from the first source (i.e. the serial port) to the (binary) file log1.pkt
     *     
     *     log@filename (e.g. log@log1.pkt) to read packets from a previously recorded log file (i.e. log1.pkt). The packets are replayed
     *        and notified to the application using the source by respecting the same timing with which they
     *        were recorded. The log source will only read packets and ignore requests to write packets. 
     *
     *     serial@/dev/ttyUSB0+log@log2.pkt+peis@WSN-Floor1 reads/write packets from/to the serial port,
     *        logs every packet to the log2.pkt file and exports the source via the Peis tuple with root key WSN-Floor1
     *        						   
     *     serial@dev/ttyUSB0+sf@localhost:9002 will raise an exception, as source chaining is only supported to the new Peis and Log sources
     * 
     */
    public static PacketSource makeExtendedPacketSource(String name) {
    	//System.out.println("In makeExtendedPacketSource");
    	StringTokenizer tokenizer = new StringTokenizer(name, CHAIN);
    	PacketSource source[] = new PacketSource[tokenizer.countTokens()];
    	
    	String sourceName = tokenizer.nextToken(); // first source 
    	//System.out.println("  first source name is ("+sourceName+")");
    	source[0] = ExtendedBuildSource.makeSinkPacketSource(sourceName); // must be a sink
    	
    	// all the next sources (if any) must subclass the ForwardingPacketSource class
    	for (int i=1; i<source.length; i++) {    		
    		sourceName = tokenizer.nextToken();
    		//System.out.println("  "+i+"]  source name is ("+sourceName+")");
    		source[i] = ExtendedBuildSource.makeForwardingPacketSource(source[i-1], sourceName);
    		if (source[i]==null) { // failure, close everything open 
    			for (int j=0; j<i; j++) {
    				try {
						source[j].close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    			return null;
    		}
    	}
    	
    	return source[source.length-1]; // return the last, outer source

    }
    
    /**
     * Creates a sink packet source
     * @param name of the source, i.e. peis@[peis-id:]Name or file:pathtoexistingfilelog
     * @return the sink packet source
     */
    public static PacketSource makeSinkPacketSource(String name) {
    	//System.out.println("In makeSinkPacketSource ("+name+")");
    	if (name.startsWith(PEIS_PREFIX)) { // peis@[peis-id:]Name
    		//System.out.println("  Peis source");
    		StringTokenizer tokenizer = new StringTokenizer(name.substring(PEIS_PREFIX.length()));
    		String key = tokenizer.nextToken(COLON);
    		//System.out.println(" -peis key is "+key);	
    		String strPeisID = null; 
    		if (tokenizer.hasMoreTokens()) {
    			strPeisID = key;
    			key = tokenizer.nextToken();
    		}
    		//System.out.println(" -peis id is "+strPeisID);
    		if (strPeisID == null) return new PeisPacketSource(key);
    		else return new PeisPacketSource(key, Integer.parseInt(strPeisID));
    	} else if (name.startsWith(FILE_PREFIX)) {
    		//System.out.println(" File source");
    		StringTokenizer tokenizer = new StringTokenizer(name);
    		tokenizer.nextToken(AT);
    		String filePath = tokenizer.nextToken();
    		//System.out.println(" File path is ("+filePath+")");
    		return new FilePacketSource(filePath);
    	} else {
    		return BuildSource.makePacketSource(name);
    	}
    }
    
    /**
     * Creates a forwarding packet source that wraps an existing source and forward packets read and transmitted
     * by the inner source over a different streaming protocol
     * @param innerSource the inner source
     * @param name the name of the forwarding source, e.g. peis@[peis-id:]Name or file@pathtonewlogfile
     * @return the forwarding packet source
     */
    public static ForwardingPacketSource makeForwardingPacketSource(PacketSource innerSource, String name) {
    	if (name.startsWith(PEIS_PREFIX)) { // peis@[peis-id:]Name
    		return new PeisPacketForwardingSource(innerSource, name.substring(PEIS_PREFIX.length()));
    	} else if (name.startsWith(FILE_PREFIX)) { // file@pathtonewlogfile
    		return new FilePacketForwardingSource(innerSource, name.substring(FILE_PREFIX.length()));
    	} 
    	return null;
    }
    		
    	
    /**
     * Make a new PhoenixSource over a specified PacketSource
     * Note that a PhoenixSource must be started (<code>start</code> method)
     * before use, and that resurrection is off by default (the default error
     * calls System.exit).
     * @param source The packet-source to use (not null)
     * @param messages Where to send status messages (null for no messages)
     * @return The new PhoenixSource
     */
    public static PhoenixSource makePhoenix(PacketSource source, Messenger messages) {
    	return new ExtendedPhoenixSource(source, messages);
    }

    /**
     * Make a new PhoenixSource over a specified PacketSource
     * Note that a PhoenixSource must be started (<code>start</code> method)
     * before use, and that resurrection is off by default (the default error
     * calls System.exit).
     * @param name The packet-source to use, specified with a packet-source
     *   string
     * @param messages Where to send status messages (null for no messages)
     * @return The new PhoenixSource, or null if name is an invalid source
     */
    public static PhoenixSource makePhoenix(String name, Messenger messages) {
    	PacketSource source = makeExtendedPacketSource(name);
    	if (source == null) {
    		return null;
    	}
    	return new ExtendedPhoenixSource(source, messages);
    }
   

}
