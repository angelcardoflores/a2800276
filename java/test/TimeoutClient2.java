import java.io.*;
import java.net.*;

public class TimeoutClient2 {

	String host;
	int port;
	Socket socket;
	InputStream is;
	OutputStream os;

	TimeoutClient2 (String [] args) throws IOException, NumberFormatException {
		this.host = args [0];
		this.port = Integer.parseInt (args[1]);
		

	}

	public void go () {
		try {
			socket = new Socket (host, port);
			socket.setSoTimeout (5000);
			dump (socket);
			is = socket.getInputStream();
			os = socket.getOutputStream();
			int i = is.read();

		}catch (IOException ioe) {
			ioe.printStackTrace();	
		}finally {
			try {

				is.close();
				os.close();
				socket.close();
				
			}catch (Throwable t) {
				t.printStackTrace ();

			}
		}
	}

	static void dump (Socket socket) {
		try {
			System.out.println ("LocalPort "+socket.getLocalPort());
			System.out.println ("Port "+socket.getPort());
			System.out.println ("ReceiveBufferSize "+socket.getReceiveBufferSize());
			System.out.println ("SendBufferSize "+socket.getSendBufferSize());
			System.out.println ("SoLinger "+socket.getSoLinger());
			System.out.println ("SoTimeout "+socket.getSoTimeout());
			System.out.println ("TcpNoDelay "+socket.getTcpNoDelay());
		}catch (SocketException se) {
			se.printStackTrace();
			System.exit(0);
		}
	}
	
	static void usage () {
			
	}

	public static void main (String [] args) {
		if (args.length != 2) {
			usage ();	
		}
		TimeoutClient2 client = null;

		try {
			client = new TimeoutClient2 (args);
		} catch (Throwable t) {
			t.printStackTrace ();
			usage ();
		}

//		Watch watch = new Watch (client, Thread.currentThread());
//		watch.start ();
		client.go();
	}



}
