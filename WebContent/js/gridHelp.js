

function getdata(value) {
	

	

	// window.opener.document.getElementById(logo).value = document.getElementById(value+fileid).value 
	 window.opener.document.getElementById("stCategory").value = document.getElementById("stCategory"+value).value
	 window.opener.document.getElementById("stSubCategory").value = document.getElementById("stSubCategory"+value).value 
	 window.opener.document.getElementById("fileName").value = document.getElementById("fileName"+value).value 
	 window.opener.document.getElementById("dataSeparator").value = document.getElementById("dataSeparator"+value).value  
	 window.opener.document.getElementById("fileId").value = document.getElementById( "fileid"+value).value;
	 window.opener.document.getElementById("rdDataFrm").value = document.getElementById( "rdDataFrm"+value).value;
	 window.opener.document.getElementById("charpatt").value = document.getElementById( "charpatt"+value).value;
	
	 
	 if(document.getElementById( "activeFlag"+value).value =='A') {
		 
		 window.opener.document.getElementById("chkstat").checked=true;
		 window.opener.document.getElementById("activeFlag").value ='A';
		 
	 }if(document.getElementById( "activeFlag"+value).value =='I') {
		 
		 window.opener.document.getElementById("chkstat").checked=false;
		 window.opener.document.getElementById("activeFlag").value ='I'
	 }
	 
	 window.close();
	 
	 
	 
}