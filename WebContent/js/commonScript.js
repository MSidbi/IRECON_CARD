var upp = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
var low = "abcdefghijklmnopqrstuvwxyz";
var num = "-0123456789.";
var ip = "0123456789.";
var spl = "/";
var seprator="| 	";



function setValueType(textbox,type) {
	
	if(type=="numeric") {
		
		var str= num;
		if(str.indexOf(String.fromCharCode(event.keyCode))==-1) {
			
			return event.returnValue= false;
		}
	}
		
	if(type=="filepath") {
			
			var str= upp+low+spl+":" ;
			if(str.indexOf(String.fromCharCode(event.keyCode))==-1) {
				
				return event.returnValue= false;
			}
		}
	if(type=="file") {
			
			var str= upp+low+"_" ;
			if(str.indexOf(String.fromCharCode(event.keyCode))==-1) {
				
				return event.returnValue= false;
			}
		}
	if(type=="ip") {
			
			var str= ip ;
			if(str.indexOf(String.fromCharCode(event.keyCode))==-1) {
				
				return event.returnValue= false;
			}
		}
	if(type=="seperator") {
			
			var str= seprator ;
			if(str.indexOf(String.fromCharCode(event.keyCode))==-1) {
				
				return event.returnValue= false;
			}
		}
	if(type=="search") {
		
		var str= upp+low+num ;
		if(str.indexOf(String.fromCharCode(event.keyCode))==-1) {
			
			return event.returnValue= false;
		}
	}
	if(type=="pattern") {
		
		var str= upp+low+num+"-|/\_%!^" ;
		if(str.indexOf(String.fromCharCode(event.keyCode))==-1) {
			
			return event.returnValue= false;
		}
	}
		
	}

	
