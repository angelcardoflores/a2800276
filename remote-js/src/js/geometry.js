/**
*	usage: g = new Geometry(x0,y0,x1,y1)
*/
var Geometry = function (x0,y0,x1,y1) {
	this.geom = [x0,y0,x1,y1]

	function getArr () {
		return this.geom
	}
	function toString () {
		return "["+this.geom[0]+","+this.geom[1]+","+this.geom[2]+","+this.geom[3]+"]"
	}
}

var CanvasGeometry = function () {
	// these three arrays describe the page elements
	// we wish to gather geometry information about.
	this.idNames=[]
	this.classNames=[]
	this.tagNames=[]
	
	/* returns a hash containing the following information
	// TODO determine further relevant information.
	// 	{
			package:
			time:
			geometry {
				canvas: geometry
				for each id	-> #idName: geometry
				for each class	-> .className: [geometry, ...]	
				for each tag	-> tagName: [geometry, ...]
			}
		

		}

		geometry := [x0,y0,x1,y1]
	*/
	this.getGeometry = function () {
		var retVal = {}
		retVal["userAgent"] 		= navigator.userAgent

		var geometry = {}	
		
		geometry["canvas"] = new Geometry(0,0,window.innerWidth, window.innerHeight)
		
			
		for_in (getGeometryByIdNames(this.idNames),
	 		function(idName, geometryObj) {
				geometry[idName]=geometryObj
			})
		for_in (getGeometriesByClassNames(this.classNames), 
			function(className, geometryArr) {
				geometry[className]=geometryArr	
			})
		for_in (getGeometriesByTagNames(this.tagNames), 
			function(tagName, geometryArr) {
				geometry[tagName]=geometryArr	
			})

		retVal["geometry"]=geometry
		return retVal

	}

	this.toString = function () {
		
	}

} // End CanvasGeometry

/*
	Retrieve the on screen location of elements with id values
	correspoding to those listed in the passed `arr` array
	parameter.

	returns: 
	a hash 
		key = id in css notation (e.g. '#id_name') 
		value = geometry of element ([[x1,x2,y1,y2]] )

	NB: the geometry value of the element is always an array 
	containing a single geometry element to keep it consistant with
	the other methods.
*/
function getGeometryByIdNames ( idNamesArr ) {
	var results={} 
	idNamesArr.each(function (element) {
		var geometry = getGeometryOfElement(document.getElementById(element))
		if (geometry)
			results["#"+element]=geometry

	})
	return results
}

/*
	returns:
		{
			(for each className in classNameArr)
			.className: [geometry, ...]
		}
*/
function getGeometriesByClassNames ( classNamesArr ) {
	var getElementsByClassName = function (className){
		// there is no getElementsByClassName method in javascript
		// so we have to roll our own...

		// brute force, get ALL elements and see what class they belong to...
		var list = document.getElementsByTagName('*')
		var elements = []
		
		//  while `list` returned by getElementsByTagName BEHAVES
		// just like an Array, it's not an Array, so it doesn't support
		// the `each` method we added, an apparently it's a some weird
		// mutant that doesnt'
		// To top it off, it's not possible (or I'm too stupid) to add
		// functions to it's prototype.

		for (var i=0; i!=list.length;++i) {
			if (list[i].className==className){
				elements.push(list[i])
			}
		}
		return elements
	}
	return getGeometryByWhatever (classNamesArr, "#", getElementsByClassName)
}

function getGeometriesByTagNames ( arr ) {
	var lambda = function (tn){return document.getElementsByTagName(tn)}
	return getGeometryByWhatever (arr, "", lambda)
}

function getGeometryByWhatever (selectorArr, prefix, lambda) {
	var results={}
	selectorArr.each (function (selectorName){
		var geometries	= []
		var elements 	= lambda(selectorName)

		elements.each ( function (element) {
			geometries.push(getGeometryOfElement(element))
		} )
		results[prefix+selectorName]=geometries
	})	
	return results
}


/*
	retrieve a coordinates of the element

	returns:
		geometry of the element: [x1,y1,x2,y2]
*/
function getGeometryOfElement ( elem ) {
	if (!elem)
		return;
		
	/* return new Geometry(
		elem.offsetLeft, elem.offsetTop,
		elem.offsetLeft+elem.offsetWidth, elem.offsetTop+elem.offsetHeight
	)
	*/
	return [
		elem.offsetLeft, elem.offsetTop,
		elem.offsetLeft+elem.offsetWidth, elem.offsetTop+elem.offsetHeight

	]	
			
}


