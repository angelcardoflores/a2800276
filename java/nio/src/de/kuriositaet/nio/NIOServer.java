package de.kuriositaet.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class NIOServer {
	
	private Selector selector;

	public NIOServer () throws IOException {
		this.selector = Selector.open();
	}
	
	public SelectionKey listenToPort (int port) throws IOException {
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ServerSocket socket = ssc.socket();
		socket.bind( new InetSocketAddress(port) );
		return ssc.register(this.selector, SelectionKey.OP_ACCEPT);
	}
	
	
	
	
}
