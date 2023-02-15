function setfilename(e) {
	
	
	document.getElementById("stFileName").value = e.options[e.selectedIndex].text;
	
	
}

function getfiledetails(){
	
	
	window.open("../DebitCard_Recon/GetUplodedFile.do" , 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');

	
	
}



function validateclassify(){
	
	var datepicker = document.getElementById("datepicker").value;
	var filelist= document.getElementById("fileList").value;

	var category =document.getElementById("category").value;
	var msg="";
	
	if(datepicker==""){
		
		msg= msg+"Please Select Date for File.\n";
	}if(filelist=="0"){
		
		msg=msg+"Please Select File Name.\n";
	}if(category==""){
		
		msg= msg+"Please select category.\n "
	}
	
	if(msg!=""){
		
		alert(msg);
		return false;
	}else{
		
		document.getElementById("classify").disabled="disabled";
		 
		document.getElementById("overlay").style.display="block";
		
		return true;
	}

	
}