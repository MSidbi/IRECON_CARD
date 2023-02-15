function getupldfiledetails(){
	
	
	window.open("../DebitCard_Recon/GetUplodedFile.do" , 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');

	
	
}

function validatedtls(){
	
	debugger;
	
	var dataFile = document.getElementById("dataFile1");
	var cycle = document.getElementById("cycle").value ;
	var datepicker = document.getElementById("datepicker").value;
	
	var msg="";
	
	
	if(dataFile == 0 ) {
		
		msg = msg+"Please Select File.\n"
		
	}else if(cycle== "-") {
		
		msg = msg+"Please Select Cycle.\n";
	} 
	
	if(datepicker==""){
		
		msg = msg+"Please Select Date.\n"
		
	}
	
	if(msg!=""){
		
		alert(msg);
		document.getElementById("uploadform").onsubmit = function() { 
           
            return false;
        };
		return false;
		
	}else {
		
		//var oMyForm = new FormData();
		
		
		//oMyForm.append('file',dataFile.files[0])
		//oMyForm.append('filename', filename);
		//oMyForm.append('fileType', fileType);
		//oMyForm.append('category', cycle);
		//oMyForm.append('stSubCategory',stSubCategory);
		//oMyForm.append('fileDate',fileDate);
		//oMyForm.append('CSRFToken',CSRFToken);
		document.getElementById("uploadform").onsubmit = function() { 
	           
            return true;
        };
		return true;
	}
	
	
	
}