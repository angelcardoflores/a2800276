package externalEditor;

import java.net.*;
import java.io.*;

public class Server {
	public static void main (String [] args) {
		ServerSocket serv = null;
		Socket sock = null;
		try {
			int port = 12345;
			serv = new ServerSocket(12345);
			System.out.println ("created socket on port: "+port);
			sock = serv.accept();
			System.out.println ("accepted connection: "+sock);
			final BufferedReader buf = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			BufferedReader inStream = new BufferedReader(new InputStreamReader(System.in));
			BufferedWriter writ = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			System.out.println ("connection accepted");
			Thread readerThread = new Thread () {
				public void run () {
					String read = null;
					try {
					while ((read = buf.readLine())!=null) {
						System.out.println ("Read: "+read);
						try {
							System.out.println("parsed: "+Message.parseMessage(read));
						} catch (Throwable t){
							t.printStackTrace();
						}
					}} catch (Throwable t){t.printStackTrace();}
				}
			};
			readerThread.start();
			
			String tmp = null;		
			while (true) {
				System.out.print ("cmd? ");
				tmp = inStream.readLine();
				try {
					Message msg = Message.parseMessage(tmp);
				}catch (RuntimeException e) {
					System.out.println(e.getMessage());
					continue;
				}	
				tmp += "\n";
				System.out.println("Writing: "+tmp);
				writ.write(tmp, 0, tmp.length());
				writ.flush();
				Thread.sleep(250);
			}
				
			
				
		
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
			sock.close();
			serv.close();
			}catch (Throwable tt) {}	

		}	
	}
}
