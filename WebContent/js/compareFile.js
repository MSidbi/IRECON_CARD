function getupldfiledetails(){
	
	
	window.open("../DebitCard_Recon/GetUplodedFile.do" , 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');

	
	
}

function validatedtls(){
	
	
	var compareFile1 = document.getElementById("compareFile1").value;
	var compareFile2 = document.getElementById("compareFile2").value ;
	var datepicker = document.getElementById("datepicker").value;
	var msg="";
	
	
	if(compareFile1 == 0 || compareFile2== 0) {
		
		msg = msg+"Please Select Files to Compare.\n"
		
	}else if(compareFile1==compareFile2) {
		
		msg = msg+"Both Files are same.\n";
	} 
	
	if(datepicker==""){
		
		msg = msg+"Please Select Date.\n"
		
	}
	
	if(msg!=""){
		
		alert(msg);
		return false;
		
	}else {
		
		document.getElementById("compare").disabled="disabled";
		document.getElementById("overlay").style.display="";
		
		/* $('#overlay1').css('display', 'block');
         $('#loading').css('display', 'block');
         $('#loading').html(" <img align='center' src='images/loader.gif' alt='Loading'>");*/
		return true;
	}
	
	
	
}