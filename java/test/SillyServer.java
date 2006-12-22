
import java.net.*;
import java.io.*;
/**
	Test Client Timeout behaviour. Accept connections
	and wait.
*/
public class SillyServer {
	
	ServerSocket server;
	Socket socket;
	int port;

	public SillyServer (int port) {
		this.port = port;
		try {
			server = new ServerSocket (port);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit (0);
		}
	}

	void acceptAndWait () {
		try {
			System.out.println ("Starting to wait for connection on port: "+port);
			socket = server.accept();
			System.out.println ("Accepted Connection.");
			System.out.println (server);
			System.out.println (socket);
			startWaiting ();
		}catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit (0);
		}finally {
			System.out.println ("Cleaning up.");
			System.out.flush();
			try {
				socket.close();
				server.close();
			} catch (Throwable t) {
				System.err.println ("Caught exception trying to close socket.");	
				t.printStackTrace();
			}
		}
	}

	void startWaiting () {
		while (true) {
			try {
				System.out.println ("zZZzzZZzZZzZZzzzZZzzZZ");
				Thread.sleep (5000);	
			} catch (InterruptedException ie) {
				ie.printStackTrace ();
				System.exit(0);
			}
		}	
	}
	
	static void usage () {
		System.err.println ("usage: [jre] SillyServer portNumber");	
		System.exit (0);
	}
	
	public static void main (String [] args) {
		if (args.length != 1) {
			usage();	
		}	
		int port = 0;
		
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			usage ();	
		}
		
		SillyServer silly = new SillyServer (port);
		silly.acceptAndWait();
	}

	

	
}
