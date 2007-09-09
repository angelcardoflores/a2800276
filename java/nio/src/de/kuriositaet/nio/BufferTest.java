package de.kuriositaet.nio;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class BufferTest {

	static void printBuffer (ByteBuffer buf) {
		System.out.println("Capacity: "+buf.capacity());
		System.out.println("Position: "+buf.position());
		System.out.println("Limit: "   +buf.limit());
		System.out.println("Offset: "   +buf.arrayOffset());
		System.out.println("--");
	}
	
	public static void main (String [] args) {
		
		ByteBuffer buf = ByteBuffer.allocate(8);
		printBuffer(buf);
		buf.put((byte)0x0b);
		printBuffer(buf);
		buf.flip();
		printBuffer(buf);
		buf.clear();
		printBuffer(buf);
		for (int i = 0; i!= 8; ++i) {
			
			if (i==4) {
				buf.mark();		
			}
			buf.put((byte)0xab);
		}
		printBuffer(buf);
		buf.reset();
		printBuffer(buf);
		System.out.println(buf.get() == (byte)0xab);
		buf.clear();
		printBuffer(buf);
		buf.position(4);
		System.out.println(buf.get() == (byte)0xab);
		buf.clear();
		//buf.reset();
		printBuffer(buf);
		
		byte [] arr = new byte[1024];
		
		buf = ByteBuffer.wrap(arr, 100, 8);
		printBuffer(buf);
		
		
		ByteBuffer buf2 = buf.slice();
		printBuffer(buf2);
		
		
		System.out.println("len:  "+buf2.array().length);
		System.out.println("ident:"+(buf2.array()==buf.array()) );

		ByteBuffer buf2b = buf2.duplicate();
		
		System.out.println("len:  "+buf2b.array().length);
		System.out.println("ident:"+(buf2.array()==buf2b.array()) );
		
		printBuffer (buf2);
		printBuffer (buf2b);
		
		ByteBuffer buf3 = buf.allocateDirect(1024);
		System.out.println(buf3.isDirect());
		
		long time = System.currentTimeMillis();
		ByteBuffer buf4 = null;
		for (int i =0; i!= 100; ++i) {
			buf4 = buf.allocateDirect(4096);
		}
		System.out.println("Direct: "+(System.currentTimeMillis()-time) );
		
		for (int i =0; i!= 100; ++i) {
			buf4 = buf.allocate(4096);
		}
		System.out.println("Normal: "+(System.currentTimeMillis()-time) );
	}
}
