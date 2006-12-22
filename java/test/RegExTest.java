
import gnu.regexp.*;
public class RegExTest {

	public static void main (String [] args) {
		try {
			RE re = new RE (".*include.*\\.\\..*\\.tws");
			String str = ("\t\tinclude ../tws/blabla/blub.tws");
			REMatch match = re.getMatch (str);
			System.out.println ("a"+match.substituteInto("$1"));
			System.out.println ("b"+match);
			re = new RE (".*include.*(\\.\\..*\\.tws)");
			match = re.getMatch (str);
			System.out.println ("c"+match);
			System.out.println ("d"+match.substituteInto("$1"));
			str = "define CertDBDatabaseXPayGateway     \"Oracle\"     // e.g. Sybase, Oracle, Solid";
			re = new RE ("define\\s*(\\w+)\\s*\"(\\w+)\"");
			match = re.getMatch (str);
			System.out.println (str);
			System.out.println ("Var: "+ match.substituteInto("$1") );
			System.out.println ("Val: "+ match.substituteInto("$2") );
			//re = new RE (".*include.*(\\.\\..*\\.tws)");
			re = new RE ("\\.");
			str = ("com.brokat.bla.blub");
			System.out.println (re.substituteAll(str, "/"));

		}
		catch (REException ree) {
			ree.printStackTrace();	
		}

	}

}
