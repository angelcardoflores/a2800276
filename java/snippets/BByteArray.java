package com.brokat.usr.util;
/**
	Self-resizing array of bytes. 
	Avoids the overhead (casting, unecessary functionality) of vector.

	@author tim.becker@pago.de
*/
public class BByteArray {
	/**	
		default initial size of the (internal) array
	*/
	private static final int DEFAULT = 20;

	/**
		internal storage
	*/
	
	private byte [] bytes;

	/**
		current position in the internal storage
	*/
	private int pos;

	/**
		contructs a variable array with the default size
	*/
	public BByteArray () {
		this.bytes= new byte[DEFAULT];	
	}

	/**
		initializes an array with the indicated initial size
	*/
	public BByteArray (int size) {
		bytes = new byte [size];
	}
	
	/**
		append the passed byte array to the end of the internal
		storage
	*/
	public void append (byte [] insert) {
		ensureCapacity(insert.length);
		System.arraycopy (insert, 0, bytes, pos, insert.length);
		pos +=insert.length;
	}
	/**
		append the passed by to the storage	
	*/
	public void append (byte b) {
		ensureCapacity(1);
		bytes[pos++]=b;
	}
	
	public void append (byte [] bytes, int offset, int length) {
		if (offset+length>bytes.length || offset < 0 || length < 0)
			throw new ArrayIndexOutOfBoundsException ("Not a valid offset or length. arr.length: "+bytes.length+" offset: "+offset+" length: "+length);
		if (length == 0)
			return;
		byte [] tmp = new byte [offset+length];
		System.arraycopy(bytes, offset, tmp, 0, length);
		append (tmp);
	}
	
	/**
		create a byte array from the inserted data.
	*/
	public byte [] toArray () {
		byte [] retArr = new byte [pos];
		System.arraycopy(bytes, 0, retArr, 0, pos);
		return retArr;
	}

	public byte get (int index) {
		if (!(index<size()))
			throw new ArrayIndexOutOfBoundsException();
		return bytes [index];
	}

	public byte getLast () {
		return get (size()-1);	
	}

	public byte getFirst () {
		return get (0);	
	}

	/**
		returns the current size of the content.
	*/
	public int size () {
		return pos;	
	}
	
	/**
		makes sure that there is at least room for
		<code>size</code> further elements in the internal array
		and "grows" it if necessary.
	*/
	private void ensureCapacity (int size) {
		int capacity = bytes.length - pos;
		if (capacity<size) 
			resize (bytes.length+size);		
	}
	
	/**
		"grows" the array to a new length of <code>len</code>.
	*/
	private void resize (int len) {
		int newLen = max(len,(int)(bytes.length*1.5));
		byte [] newArr = new byte[newLen];
		System.arraycopy (bytes,0,newArr,0,pos);
		bytes= newArr;
	}
	/**
		utility to return the larger of the two provided
		parameters.
	*/
	private int max (int i, int j) {
		return i>j?i:j;	
	}
		
}

