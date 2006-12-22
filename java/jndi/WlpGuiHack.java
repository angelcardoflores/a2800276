package jndi;

import utils.*;
import java.util.*;
import javax.naming.*;

import com.atos.poseidon.base.usermgmt.*;
import com.atos.poseidon.base.usermgmt.data.*;

public class WlpGuiHack {
	
	private static void usage () {
		String usage = 	"usage: [jre] jndi.DumpContext \n"+
				"\t -initialFactory <java.naming.factory.initial>\n"+
				"\t -url <java.naming.provider.url>\n"+
				"\t -username <userName>";
		System.err.println (usage);
		System.exit(1);
	}
	
	private static String initFac;
	private static String url;
	private static String name;
	
	public static void main (String [] args) {
		CmdLine cmd = new CmdLine (args);
		initFac = cmd.get("-initialFactory");
		url = cmd.get ("-url");
		name = cmd.get ("-userName");
		if (initFac == null || url == null || name == null)
			usage();

		Hashtable ht = new Hashtable();
		ht.put(Context.INITIAL_CONTEXT_FACTORY,initFac);
		ht.put(Context.PROVIDER_URL,url);
		try {
			Context ctx=new InitialContext(ht);
			System.out.println (ctx.getNameInNamespace());
			Object obj = ctx.lookup ("com_atos_poseidon_base_usermgmt_LogonHome_EO");
			
			LogonRequest request = new LogonRequest();
			request.setAliasName(name);
			request.setMandatorName("----");
			request.setPassword("atosccr");

			Logon lo = (Logon)obj;
			LogonReply reply = lo.logon(request);

			Vector v = lo.getUsers("%").getVector();
			for (Iterator it = v.iterator(); it.hasNext();) {
				System.out.println(it.next());	
			}
			

			System.out.println("state: "+reply.getState());
			System.out.println("uid: "+reply.getBaseUser().getUserID().toString());
			System.out.println("mand: "+reply.getBaseUser().getMandatorID().toString());
			System.out.println("lang: "+reply.getBaseUser().getLanguageID());
			System.out.println("desc: "+reply.getBaseUser().getDescription());
			System.out.println("perm: "+reply.getBaseUser().getPermissionGroups());
		}
		catch (Throwable t) {
			t.printStackTrace();	
		}

	}
}
