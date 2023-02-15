function viewfiledata() {
	var winFeature =
        'location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no';

    window.open("../DebitCard_Recon/searchFileList.do", 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
}
function viewHeaders(){
	
	var fileId= document.getElementById("fileId").value;
	if(fileId!=0){
	var winFeature =
        'location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no';

    window.open("../DebitCard_Recon/viewHeader.do?fileId="+fileId, 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');

	}else {
		
		alert("Please Select File.")
	}
}

function changeStatus() {
	
	if(document.getElementById("chkstat").checked) {
		
		document.getElementById("activeFlag").value="A"
		
	}else {
		
		document.getElementById("activeFlag").value="I"
	}
}

function clearSourceData() {
	
	
	document.getElementById("fileName").value  ="";
	document.getElementById("dataSeparator").value ="";
	document.getElementById("rdDataFrm").value  ="";
	document.getElementById("charpatt").value ="";
	//document.getElementById("tableName").value  ="";
	document.getElementById("fileId").value="0";
	document.getElementById("chkstat").checked  =false;
}

