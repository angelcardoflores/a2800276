
import java.net.*;
import java.io.*;

public class TimeoutClient {
	
	String host;
	int port;
	String urlStr;
	URL url;

	HttpURLConnection connection = null;
	BufferedReader inReader = null;
	
	boolean done;

	static String sendData = "123456789";
	

	public synchronized void done () {
		this.done = true;	
	}

	public synchronized boolean getDone () {
		return this.done;	
	}

	public void disconnect () {
		if (connection!=null) {
			System.out.println ("Disconnecting");	
			connection.disconnect ();
			try {
				inReader.close ();
			} catch (Throwable t) {
				 t.printStackTrace();	
			}
		}
	}

	public TimeoutClient (String [] args) throws IOException{
		this.host = args [0];
		this.port = Integer.parseInt (args[1]);
		this.urlStr = "http://"+host+":"+port;
		url = new URL (urlStr);
		
	}

	public void go () {
		DataOutputStream outStream = null;
		try {

			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput (true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Length", new Integer(sendData.length()).toString());
			connection.setRequestProperty ("Content-type", "text/html");
			connection.setRequestProperty ("User-Agent", "Pago HTTP cartridge");

			outStream = new DataOutputStream(connection.getOutputStream());

			outStream.writeBytes(sendData);
			System.out.println (1);
			InputStream is = connection.getInputStream();
			System.out.println (2);
			inReader = new BufferedReader(new InputStreamReader(is));
			String result;

			System.out.println (3);

			if ((result = inReader.readLine()) != null) {
				System.out.println (result);
			}

		} catch (IOException ioe) {
			ioe.printStackTrace ();
			System.exit(0);
		}finally {
			try {
				if (outStream!=null)outStream.close();
				if (inReader!=null)inReader.close();
			} catch (IOException ioe) {
				System.err.println ("Error closing Streams!");
				ioe.printStackTrace();
			}
			connection.disconnect();	
		}
		

	}
	
	static void usage () {
		System.err.println ("usage: [jre] TimeoutClient inetAddr port");
		System.exit (0);
	}

	public static void main (String [] args) {
		if (args.length != 2) {
			usage ();	
		}
		TimeoutClient client = null;

		try {
			client = new TimeoutClient (args);
		} catch (Throwable t) {
			t.printStackTrace ();
			usage ();
		}

		Watch watch = new Watch (client, Thread.currentThread());
		watch.start ();
		client.go();
	}

	
}
class Watch extends Thread {
	TimeoutClient client;
	Thread thread;
	
	Watch (TimeoutClient client, Thread thread) {
		this.client = client;	
		this.thread = thread;
	}

	public void run () {		

		synchronized (this) {
			try {
				System.out.println ("Starting to wait");
				this.wait (5000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				System.exit (0);
			}
			System.out.println ("time's up");
			if (!client.getDone()) {
				thread.destroy();
				client.disconnect();	
			}

		}
	}

}
