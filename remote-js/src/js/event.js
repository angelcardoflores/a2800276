
/*
	TODO:
		* session handling: generate new cookie(?) each time recording starts?
		* handle geometry changes: onscroll, onresize, ... ?
		* flush event buffer every n secs regardless how many events have been captured. 
	
	Possible optimizations:	
		* use 1/100 sec accuracy instead of milliseconds, and bundle events
		* capture only every nth movement event, up/down are always important
*/

var MOUSE_MOVE	= 0x01;		   // just a standard move event, could be 0x00 if we run out of room ...
var MOUSE_DOWN	= MOUSE_MOVE << 1  // mouse down event
var MOUSE_UP	= MOUSE_DOWN << 1  // mouse up event
var TIME_RESET	= MOUSE_UP   << 1  // 18 bit time counter overflowed, starting over at zero 
var GEOMETRY	= TIME_RESET << 1  // geometry of the screen has changed, will send new geom per xmlrpc
var BYE		= GEOMETRY   << 1  // signalises end of connection.


var MouseEvent = function (type, x, y) {
	this.x=x
	this.y=y
	this.type=type
	this.getTime = function () {
		var time = new Date().getTime()-MouseEvent.startTime
		if (time>0x3FFFF) {
			time 			-= 0x3FFFF
			this.type 		|= TIME_RESET
			MouseEvent.startTime 	+= 0x3FFFF
		}
		return time

	}
	this.time=this.getTime();

	this.toNetworkBytes = function () {
		// wire protocol is:
		// 6 bits type, 18 bits time, 12 bits x, 12 bits y = 6 bytes, 3 chars

		var networkBytes = []

		// six bits of type in the first 6 bits of the first byte, followed by the
		// first 2 bites of time.
		var _time = (this.time & 0x3ffff)
		networkBytes.push( ( (this.type & 0x3F) << 2) | ( _time >> 16) )
		// next 8 bits of time
		_time &= 0xffff
		networkBytes.push ( _time >> 8)
		_time &= 0xff
		networkBytes.push ( _time )

		var _x = this.x & 0xfff
		var _y = this.y & 0xfff

		//first 8 bits of x
		networkBytes.push( _x >> 4 )
		// last 4 bits of x and first 4 bits of y
		_x &= 0x0f
		networkBytes.push( (_x << 4) | (_y >> 8) )
		networkBytes.push( _y & 0xff )
		return networkBytes
	} // toNetworkBytes


	
}

/**
	class variables for configuration
	
	storeHowMany 	= // number of events that we before before sending them
	// storeEveryNth = // only store every nth mouse movement to save
	// timeout	= // flush buffer after this many msecs even if storeHowMany 
			  //events haven't been captured.
*/
var MouseRecorder = function () {

	var store 	= [] 	// array that buffers the events
	var packetNum	= 0	// count of the current packet
	var lastFlush	= 0	// last time the buffered packets were sent to the server
	//var startTime = 0 	// time recording started
	
	

	this.startRecording = function () { 
		prevTime=new Date().getTime()
		//window.captureEvents(Event.MOUSEMOVE|Event.MOUSEDOWN|Event.MOUSEUP);
		
		MouseEvent.startTime 	= new Date().getTime()
		
		window.onmousemove	= this.record
		window.onmousedown	= this.recordMouseDown
		window.onmouseup	= this.recordMouseUp
		

	}

	this.stopRecording = function () {
		
		window.onmousemove	= null
		window.onmousedown	= null
		window.onmouseup	= null
		
		doFlush()
	}

	this.getEvents = function () {
		return store
	}
	
	
	this.record = function (e) {
		store.push(new MouseEvent(MOUSE_MOVE, e.pageX, e.pageY))
		flush()
	}

	this.recordMouseDown = function (e) {
		store.push(new MouseEvent(MOUSE_DOWN, e.pageX, e.pageY)) 
		flush()
	}

	this.recordMouseUp = function (e) {
		store.push(new MouseEvent(MOUSE_UP, e.pageX, e.pageY)) 
		flush()
	}

	var flush = function () {
		if (store.length >= MouseRecorder.storeHowMany) {
			doFlush()
		}
	}
	var doFlush = function () {
		// copy and reset the packet buffer
		var internalStore = store
		store = []
		
		// mark this transer
		lastFlush = new Date().getTime()
		
		// construct data to send to server.
		
		var str = ""
		str += packetNum++ + ":" 
		//str += lastFlush + ":"
		
		var messagePackets = []
		internalStore.each (function (e) { 
			messagePackets = messagePackets.concat(e.toNetworkBytes())
		})

		str+=Base64.encode(messagePackets)
	
		doNothing = function () {}
//TODO
		req = new Request("http://localhost:2000/dump", str, doNothing)
		req.process()
		 		
	
		print (str)	
		
	}

}

MouseRecorder.storeHowMany=60
