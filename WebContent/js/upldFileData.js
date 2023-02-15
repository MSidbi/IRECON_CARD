

function getfiledata() {
	var winFeature =
        'location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no';

    window.open("../DebitCard_Recon/searchFileList.do", 'window', 'location=no,toolbar=no,menubar=no,scrollbars=yes');
}//width=1000,height=500,,resizable=no


function validateData() {
	
	
	var fileId = document.getElementById("fileId").value;
	
	if(fileId==null||fileId=="0"){
		
		
		alert("Kindly select file to update.\n");
		return false;
	}
}

function clearData() {
	
	
	document.getElementById("fileName").value  ="";
	document.getElementById("dataSeparator").value ="";
	document.getElementById("rdDataFrm").value  ="";
	document.getElementById("charpatt").value ="";
	//document.getElementById("tableName").value  ="";
	document.getElementById("fileId").value="0";
	document.getElementById("chkstat").checked  =false;
}

function changeStatus(chkbox) {
	
	
	if(document.getElementById("chkstat").checked) {
		
		document.getElementById("activeFlag").value ='A'
	}else{
		
		document.getElementById("activeFlag").value ='I'
	}
	
	
}



function isNumber(evt) {
	  var charCode = (evt.which) ? evt.which : event.keyCode
			  if (charCode > 31 && (charCode != 46 &&(charCode < 48 || charCode > 57)))
			    { return false;
			    }
			    return true;

}
function changetxt(btn){
	
	
	var password = document.getElementById("hdpwd").value;
	if(password=="Show Password") {
	
		document.getElementById("hdpwd").value="Hide Password";
		$('#ftpPwd').removeAttr("type");
		$('#ftpPwd').prop('type', 'text');
		
	}if(password=="Hide Password") {
	
		$('#ftpPwd').removeAttr("type");
		$('#ftpPwd').prop('type', 'password');
		document.getElementById("hdpwd").value="Show Password";
	}
	
}