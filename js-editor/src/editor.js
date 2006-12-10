
/*

This code is (c)2006 Tim Becker (tim.becker@kuriositaet.de) and provided
with absolutely no gurantees. Feel free to do whatever you please with
it, though I'd appreciate it if you give me some credit, provide
feeback, bugfixes, ideas ...


Special thanks to "Locutus of Borg" for help
with IE and scriptlet support.

*/

var JavaScriptEditorConfig = {
	html : '<form> <textarea id="__input" cols="80" rows="15"></textarea> <input type="button" onClick="JavaScriptEditor.evaluateCode()" value="eval" onFocus="returnCursor()" ><input type="button" onClick="erase(JavaScriptEditor.input)" value="clear"><br/> <textarea id="__console" cols="80" rows="10"></textarea><input type="button" onClick="erase(JavaScriptEditor.console)" value="clear" ><br/> <textarea id="__errors" cols="80" rows="5"></textarea><input type="button" onClick="erase(JavaScriptEditor.errors)" value="clear" ></br> </form>' ,
	div_id : "javascript_editor"
}



var JavaScriptEditor = new function () {
	
	/*
		References to the three textareas
	*/
	this.input
	this.console
	this.errors



	
	/**
		finds a div named "javascript_editor" and adds the
		"editor" defined in __html, above to it.
		
		Finally it attaches the newly initialized instance as
		a property JavaScriptEditor named "editor"
	*/
	this.init= function () {
		var editorDiv = document.getElementById(JavaScriptEditorConfig.div_id)

		// create an editor div at the end of the page if none
		// exists.
		if (!editorDiv) {
			editorDiv = document.createElement('DIV'); 
			editorDiv.id = JavaScriptEditorConfig.div_id; 
			document.body.appendChild(editorDiv);	
		}
		
		//alert (divName)
		editorDiv.innerHTML = JavaScriptEditorConfig.html

		this.input = document.getElementById("__input")// forms[formName].input
		if (navigator.appName=="Netscape"){
			this.input.onkeydown=function (e) {   
				// this function gets attached to the
				// textarea `input` the events of
				// which it's handling, so refering to `this` references the 
				// textarea and NOT the editor....
				var start	= this.selectionStart
				var end		= this.selectionEnd

				if ( e.ctrlKey ) {
					if (e.keyCode == 13 || e.keyCode == 77) {
						// return (CR) should be ascii 13, but mac firefox(2.0)
						// returns 77 here, not sure why, possibly ^M ??
						// anyhow, Ctl-Enter should execute the code in the editor.
						JavaScriptEditor.evaluateCode();
						return;
					}   
				}

				// Stupid crutch, can't figure out how to
				// keep browser from tabbing to the next form element.
				// handled by returning focus to the input-field when the
				// next element (eval-button)'s onFocus event is triggered.

				if (  e.keyCode == 9 ) {
					var val = this.value
					val = val.substring(0,start) + "\t" +val.substring(end,val.length)
					this.value = val
					
					window.setTimeout("returnCursor(%d)".fmt(start+1), 10)
				}


			} // handleTab / input onkeydown
		}
		this.console = document.getElementById("__console")//forms[formName].console
		this.errors = document.getElementById("__errors")//forms[formName].errors
		
	} // init

	

	/*********************************************************
	* OUTPUT functions.   
	********************************************************/   

	// append the string `txt` to the textarea `area`
	this.print = function(txt, area) {
		tmpTxt = area.value
		tmpTxt += "\n"+txt
		area.value = tmpTxt   
	}

	
	this.evaluateCode = function () {
		try {
			erase(this.errors)
			erase(this.console)
			eval (this.input.value)
		} catch (e) {
			printErr (e)   
		}
	}


}// editor

// This is a li
if (window.autoinit != undefined && window.autoinit == true) {
	JavaScriptEditor.init()	
	JavaScriptEditor.input.focus()	
}

// INTERAL BOONDOGGLE FUNCTION.	
function returnCursor (nextPos) {
	// purely a cludge to return the cursor to the original position
	// after tab gets hit in the input area.
	if (navigator.appName != "Netscape") return
	JavaScriptEditor.input.focus()
	JavaScriptEditor.input.setSelectionRange(nextPos, nextPos)
}

function erase (area) {
	area.value=""
}




// print formated string to output area.
function printf () {
	arr = []
	for (var i = 1; i!=arguments.length; ++i) {
		arr[arr.length]=arguments[i]
	}
	JavaScriptEditor.print(sprintf(arguments[0], arr), JavaScriptEditor.console)
}


/**
	Functions provided for the utility of the user of the editor. 
*/

function print (txt) {
	JavaScriptEditor.print(txt, JavaScriptEditor.console)
}

function printErr (txt) {
	JavaScriptEditor.print(txt, JavaScriptEditor.errors)
}

function dumpProperties (obj) {
	print(obj)
	for (var p in obj){
		print(p+" = "+ obj[p])
	}			
}

function load (script_uri) {
	try {
		req = new Request(script_uri)
		req.process()
		eval(req.responseText)
	} catch (e){
		printErr(e)		
	}
}

/**
	Utility Code, this comes from other projects and is provided for
	convenience, see:
	
	
	http://www.kuriositaet.de/javascript/jsxmlrpc.html
	for more information about `Request`

	and

	http://blog.kuriositaet.de/?p=176
	for `sprintf`
*/

function getXMLHttpRequest () {
 var xmlhttp = false;
 var arr = [
 function(){return new XMLHttpRequest();},
 function(){return new ActiveXObject("Microsoft.XMLHTTP");},
 function(){return new ActiveXObject("Msxml2.XMLHTTP");}
 ]
  for (var i=0; i!=arr.length; ++i) {
 try { 
 xmlhttp = arr[i]()
 break
 } catch (e){} 
 }
 return xmlhttp;
}


function checkRequestStatus (request) {
  if (request.status != 200 && request.status != 0) {
 exception = request.status.toString() + " : "
 exception += request.statusText ? request.statusText : "Network error"
 exception.errCode = request.status
 throw exception
 }
}

function readyStateChangeFunc (request, lambda) {
 return function () {
 if (request.readyState != Request.COMPLETED) return
 try {
 checkRequestStatus(request)
 lambda(request) 
 } catch (e) { request.onnetworkerror(e) }
 }
}

function getOnreadystatechangeCallback (req){
 return function () {
 arr = ["readyState", "responseBody", "responseStream", "responseText", "responseXML", "status", "statusText"]
 for (var i=0; i!= arr.length; ++i) {
 try {
 req[arr[i]]=req._request[arr[i]] 
 } catch (e){} 
 }
 if (req.onreadystatechange){
 req.onreadystatechange()
 }
 }
}




function Request (url, content, callback) {
 this.url = url
 this.content = content
 this.callback = callback
 this.contentType = "text/xml"

 this.requestMethod = null
 
 
 this.readyState = 0
 this.responseBody = null
 this.responseStream = null
 this.responseText = null
 this.responseXML = null
 this.status = null
 this.statusText = null

 this._request = getXMLHttpRequest()
 this._request.onreadystatechange = getOnreadystatechangeCallback(this)
 this.abort = function () {
 return this._request.abort()
 }
 this.getAllResponseHeaders = function () {
 return this._request.getAllResponseHeaders()
 }
 this.getResponseHeader = function (str) {
 return this._request.getResponseHeader(str) 
 }
 this.open = function (method, url, async, user, password) {
 if (typeof(async)=="undefined") async = true
 if (typeof(user)=="undefined") user = null
 if (typeof(password)=="undefined") password = null
 return this._request.open(method, url, async, user, password) 
 }
 this.send = function (content) {
 if (typeof(content)=="undefined") content = "" 
 var tmp = this._request.send(content) 
 
 }
 this.setRequestHeader = function (name, value) {
 return this._request.setRequestHeader(name, value) 
 }

 this.copyAttributes = function () {
 arr = ["readyState", "responseBody", "responseStream", "responseText", "responseXML", "status", "statusText"]
 for (var i=0; i!= arr.length; ++i) {
 try {
 this[arr[i]]=this._request[arr[i]] 
 } catch (e){} 
 }

 }
  

 this.process = function () {
 if (callback){
 this._request.onnetworkerror = this.onnetworkerror
 this._request.onreadystatechange = readyStateChangeFunc (this._request, callback)
 }
 var async = callback ? true : false
 if (!this.requestMethod) {
 if (!this.content){
 this.requestMethod="GET" 
 } else {
 this.requestMethod="POST" 
 }
 }
 this.open (this.requestMethod, url, async)
 this.setRequestHeader("Content-Length", content!=null?content.length:0)
 if (content!=null)
 this.setRequestHeader("Content-Type",this.contentType);
 this.send (content)
 if (async) return

 this.copyAttributes()
 checkRequestStatus(this) 
 }
 this.onnetworkerror = function (e) {
 alert(e)
 }
}

Request.UNINITIALIZED = 0
Request.LOADING = 1
Request.LOADED = 2
Request.INTERACTIVE = 3
Request.COMPLETED = 4 

function sprintf () {
        // variable attributes
        if (!arguments || arguments.length < 1) {
        return "" 
        }
        if (arguments[1] && arguments[1] instanceof Array ) {
        arguments=[arguments[0]].concat(arguments[1])
        }
      
        var fmt = arguments[0]
        var output = ""
        var current_arg = 1
        for (var pos=0; pos < fmt.length; ++pos) {
        if (fmt.charAt(pos) != '%') {
            output += fmt.charAt(pos)
            continue
        }

        switch (fmt.charAt(++pos)) {
            case '%':
            output += '%'
            break
            case 'o': //octal
            output += '0'+arguments[current_arg++].toString(8)
            break
            case 'x': //hex
            output += '0x'+arguments[current_arg++].toString(16)
            break
            case 'b': //binary
               output += '0b'+arguments[current_arg++].toString(2)
               break
            default :
            output += arguments[current_arg++].toString()
           }
        }//for
        return output
    }//sprintf
    
String.prototype.fmt = function () {
    arr = []
    for (var i=0; i!=arguments.length; ++i) {
        arr[length]=arguments[i]
    }
    return sprintf(this, arr)
} 


