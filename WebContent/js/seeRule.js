function ShowDtl(e) {
	
	
	var fileId = window.opener.document.getElementById("fileValue").value;
	var category = window.opener.document.getElementById("rectyp").value;
	var rec_set_id = window.opener.document.getElementById("rec_set_id").value; 
	if(e=="Classify") {
		
		window.open("../DebitCard_Recon/ViewConfigureTypeDtls.do?fileId="+fileId+"&category="+category, 'Classification', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
		
	}else if(e=="Knockoff") {
		
		window.open("../DebitCard_Recon/ViewKnockoffDtls.do?fileId="+fileId+"&category="+category, 'KnockOFF', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
		
	}else if(e=="Compare"){
		
		window.open("../DebitCard_Recon/ViewCompareDetls.do?recId="+rec_set_id, 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
		
	}
}