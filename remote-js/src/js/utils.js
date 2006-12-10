
/**
 * apply function `func` to each element (not key) 
 * of the hash `hash`
 */
function for_in (hash, func) {
	for (var i in hash) {
		func(i,hash[i])	
	}
}

if (!Array.prototype.push){
	Array.prototype.push = function (elem) {
		this[this.length]=elem	
	}
}


if (!Array.prototype.each) {
	Array.prototype.each = function (lambda) {
		for (var i=0; i!=this.length; ++i) {
			lambda(this[i])
		}
	}

}

var _hex = "0123456789abcdef"
function to_hex (i, numDigits) {
	var hex = ""
	while (i) {
		hex = (_hex[i & 15]) + hex
		i >>= 4
	}
	while (hex.length < numDigits) {
		hex = '0'+hex
	}
	return hex
}

var Base64 = function () {}

Base64.encode = function (bytes){
	var i1, i2, i3
		var output = "";

	var BASE64STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="

	for (var i=0; i<bytes.length;){
		i1 = bytes[i++]
		output += BASE64STR[i1 >> 2]

		i2 = bytes[i++]
		if (i2!=undefined) {
			output += BASE64STR[ ((i1 & 0x03) << 4) | (i2 >> 4) ]
		} else {
			output += BASE64STR[ ((i1 & 0x03) << 4) ]
			output += "=="
			break
		}

		i3 = bytes[i++]
		if (i3!=undefined) {
			output += BASE64STR[ ((i2 & 0x0f) << 2) | (i3 >> 6) ]
				output += BASE64STR[ i3 & 0x3f]
		} else {
			output += BASE64STR[ ((i2 & 0x0f) << 2)]
				output += "="    
		}
	} //for
	return output;

}
