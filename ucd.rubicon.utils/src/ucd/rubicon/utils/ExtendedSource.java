package ucd.rubicon.utils;

import net.tinyos.message.MoteIF;
import net.tinyos.packet.BuildSource;
import net.tinyos.packet.PhoenixSource;
import net.tinyos.util.PrintStreamMessenger;

/**
 * @author Mauro Dragone <mauro.dragone@ucd.ie>
 *
 */
public class ExtendedSource {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		PhoenixSource phoenix;
		
		if (args.length==0) {
			System.out.println("ExtendedSource was called without argument, using the default packet source returned by BuildSource");
			phoenix = BuildSource.makePhoenix(PrintStreamMessenger.err);
		} else {
			System.out.println("ExtendedSource connecting to source: "+args[0]);
			phoenix = ExtendedBuildSource.makePhoenix(args[0], PrintStreamMessenger.err);
		}
		
		MoteIF moteIF = new MoteIF(phoenix);
		//moteIF.registerListener(new NetMsg(), this);

	}

}
