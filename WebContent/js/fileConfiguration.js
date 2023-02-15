
function setclassValue(e) {
	
	if(e.checked) {
		
		document.getElementById("classify_flag").value="Y"
		
	}else {
		
		document.getElementById("classify_flag").value="N"
	}
	
}

function setKnockValue(e) {
	
	if(e.checked) {
		
		document.getElementById("knock_offFlag").value="Y"
		
	}else {
		
		document.getElementById("knock_offFlag").value="N"
	}
	
}

function setprev_tbl(e) {
	
if(e.checked) {
		
		document.getElementById("prev_tblFlag").value="Y";
		document.getElementById("trprevtable").style.display="";
		document.getElementById("trhdr").style.display="none";
		
	}else {
		
		document.getElementById("prev_tblFlag").value="N";
		document.getElementById("trprevtable").style.display="none";
		document.getElementById("trhdr").style.display="";
			
	}
	
}





function crtcols() {
	
	
	var colsize =document.getElementById("colsize").value;
	var hdrtbl = document.getElementById("hdrtbl");
	
	if(colsize<=0) {
		
		alert("Please enter valid column number")
		return false;
		
	} else {
		var count = hdrtbl.rows.length
		try{
			for(var j =1;j<=count;j++ )
			{
				hdrtbl.removeChild(hdrtbl.lastChild);
				
				
			}
			
		}catch (e) {
			
		}
		
		document.getElementById("hdrrow").style.display="";
		for (var i=0;i<=colsize;i++){
			
			var tr = document.createElement("tr");
			var td =document.createElement("td");
			var td1 =document.createElement("td");
			
			if(i==0) {
				
				
				var input = document.createElement("input");
				input.value="Headers";
				input.disabled=true;
				input.readonly="readonly";
				
				td.appendChild(input);
				
				tr.appendChild(td)
				hdrtbl.appendChild(tr);
				
			}else {
			
				var input = document.createElement("input");
				input.id = "hdr"+i;
				input.setAttribute("maxlength",25);
				input.onkeypress = new Function("setValueType(this,'file')");
				input.onchange = new Function("chkcolumnexist(this)");
				input.onpaste="return false";
				
				
				td.appendChild(input);
				
				tr.appendChild(td);
				hdrtbl.appendChild(tr);
			}
		}
		
	}
}
	
	function addtblheader(){
		
		var dataSeparator = document.getElementById("dataSeparator").value;
		var fileName = document.getElementById("stFileName").value;
		var filetype = document.getElementById("filetype").value;
		var chkprevtbl =document.getElementById("chkprevtbl");
		var prev_table = document.getElementById("prev_table").value;
		var msg="";
		var headerlist=[];
		var colsize =document.getElementById("colsize").value;
		var hdrtbl = document.getElementById("hdrtbl");

		if(!(chkprevtbl.checked) ) {
			if(colsize>0) {
				
				var count = hdrtbl.rows.length;
				
				if(colsize!=count-1) {
					
					msg=msg+"Please create column.\n";
					
					
				}else{
				
					for (var i=1;i<=colsize;i++){
						
						var itext =document.getElementById("hdr"+i).value;
						
						if(itext!=null && itext != '') {
							
							headerlist.push(itext);
						
						}else {
							
							msg = msg+"Please enter all column names.\n";
							
						}
					}
					document.getElementById("tblHeader").value = headerlist;
					}
				}else  {
					msg = msg+"Please enter column size.\n";
				}
			} else {
					
					if(prev_table=="") {
						
						msg=msg+"Please select previous table.\n ";
					}
					
				}
			//Data Separator is not mandatory
				/*if(dataSeparator =="") {
					
					msg= msg+"Please Enter Data Seperator.\n";
					
				}*/
				if(fileName =="") {
					
					msg = msg+"Please Enter File Name.\n";
				
				}if(filetype=="select"){
					
					msg = msg+"Please select File type.\n";
					
				}
			
				if(msg!="") {
					
					alert(msg);
					return false;
				}else{
					
					return true;
				}
				
	}

	function chkcolumnexist(textbox){
		
		var colsize =document.getElementById("colsize").value;
		var hdrtbl = document.getElementById("hdrtbl");
		var count = hdrtbl.rows.length;
		var copycnt=0;
		
		
		if(textbox.value != null && textbox.value != '') {
			
			for (var i=1;i<=count-1;i++){
				
				var hdrid = document.getElementById("hdr"+i).id;
				var itext =document.getElementById("hdr"+i).value;
					
				
					
					if(textbox.value == itext && hdrid != textbox.id){
					
						copycnt= copycnt+1;
						
					}
				
				
			}
			if(copycnt>0) {
				textbox.value="";
				alert("Same column name already entered.");
				return false;
			
			}else{
				return true;
			}
		}
		
	}
	
	function changeStatus() {
		
		if(document.getElementById("chkstat").checked) {
			
			document.getElementById("activeFlag").value="A"
			
		}else {
			
			document.getElementById("activeFlag").value="I"
		}
	}