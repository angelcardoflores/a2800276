package de.kuriositaet.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Random;
import java.util.Set;

public class Test {

	public static void main(String[] args) throws IOException, Throwable {
		Selector s1 = Selector.open();
		Selector s2 = Selector.open();

		System.out.println(s1 == s2);

		SocketChannel sc = SocketChannel.open();
		sc.configureBlocking(false);
		sc.connect(new InetSocketAddress("127.0.0.1", 12345));
		//sc.finishConnect();
		
		System.out.println("Connected1:" + sc.isConnected());
		SelectionKey key = sc.register(s1, SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT);

		byte[] b = new byte[10];
		Random rand = new Random();
		ByteBuffer buf = ByteBuffer.allocate(10);
		//System.out.println(buf.hasRemaining());
		
		/*for (int i = 0; i != 10; ++i) {

			rand.nextBytes(b);
			buf.clear();
			buf.put(b);
			buf.flip();
			System.out.println("Connected:" + sc.isConnected());
			System.out.println("Wrote:" + sc.write(buf));

		}*/
		int count = 0;
		while (true) {
			int i = s1.select(1000);
			System.out.println("Selected:"+i);
			if (i==0) {
				//Thread.sleep(100);
			}
			Set<SelectionKey> keys = s1.selectedKeys();
			System.out.println(keys.size());
			for (SelectionKey k : keys) {
				keys.remove(k);
				SocketChannel sc1 = (SocketChannel) key.channel();
				if ((k.readyOps() & SelectionKey.OP_CONNECT) == SelectionKey.OP_CONNECT) {
					if (sc1.isConnectionPending()){
						System.out.println("here");
						k.interestOps(SelectionKey.OP_WRITE);
						//sc1.finishConnect();
					}
						//
				} else if (k.isWritable()) {
					System.out.println("!" + (sc == sc1));
					for (int j = 0; j != 1000000; ++j) {
						buf.clear();	
						rand.nextBytes(b);
						buf.put(b);
						buf.flip();
						int num =0;
						System.out.println("Connected:" + sc1.isConnected());
						System.out.println("Writeable:" + k.isWritable());
						System.out.println("Valid    :" + k.isValid());
						num = sc1.write(buf);
						System.out.println("Remaining:" + buf.hasRemaining());
						System.out.println("Wrote:"+ num);

					}

				}
			}
			
			if (count++ == 50) {
				break;
			}

		}
		
		System.out.println("FIN.");

	}
}
