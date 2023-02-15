function getDetails() {
	
	var fileId = document.getElementById("fileList").value;
	var category= document.getElementById("stCategory").value;
	var subcat = document.getElementById("stSubCategory").value;
	
	if(fileId != '0' && category !=''){
	
		window.open("../DebitCard_Recon/ViewConfigureTypeDtls.do?fileId="+fileId+"&category="+category+"&subcat="+subcat, 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
	}else {
		
		alert("Select File and category");
	}
	
}

function getknckoffDetails() {
	
	var fileId = document.getElementById("fileList").value;
	var category= document.getElementById("stCategory").value;
	//added by INT5779 FOR SUBCATEGORY
	var subcat = document.getElementById("stSubCategory").value;
	
	if(fileId != '0' && category !=''){
	
		//window.open("../DebitCard_Recon/ViewKnockoffDtls.do?fileId="+fileId+"&category="+category, 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
		//MODIFIED BY INT5779 FOR PASSING SUBCATEGORY
		window.open("../DebitCard_Recon/ViewKnockoffDtls.do?fileId="+fileId+"&category="+category+"&subcat="+subcat, 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
	}else {
		
		alert("Select File and category");
	}
	
}

function setupValue() {

	
	debugger;
	var stCategory = document.getElementById("stCategory").value;
	var subcat= document.getElementById("stSubCategory").value;
	
	if(stCategory=='ONUS') {
		

		document.getElementById("trsubcat").style.display="none";
		if(stCategory!="") {
			
			displayFile(stCategory,subcat);	
		
		}else {
			
			alert("Please Select Valid Category");
			return false;
		}
		
		
	}
	else if(stCategory=='AMEX') {
		

		document.getElementById("trsubcat").style.display="none";
		if(stCategory!="") {
			
			displayFile(stCategory,subcat);	
		
		}else {
			
			alert("Please Select Valid Category");
			return false;
		}
		
		
	}
	else if(stCategory=='RUPAY') {
		document.getElementById("trsubcat").style.display="";
		if(stCategory!="" && subcat !="") {		
	
			displayFile(stCategory,subcat);	
			
		}else {
			
			alert("Please Select Valid Category or SubCategory");
			return false;
		}
		
	}
	//added by INT5779
	else if(stCategory == 'VISA')
		{
		document.getElementById("trsubcat").style.display="";
		if(stCategory!="" && subcat !="") {		
	
			displayFile(stCategory,subcat);	
			
		}else {
			
			alert("Please Select Valid Category or SubCategory");
			return false;
		}
		}
	else if(stCategory == 'POS')
	{
	document.getElementById("trsubcat").style.display="";
	if(stCategory!="" && subcat !="") {		

		displayFile(stCategory,subcat);	
		
	}else {
		
		alert("Please Select Valid Category or SubCategory");
		return false;
	}
	}
}


function displayFile(category,subcat) {
	
	
	$.ajax({
		 
		 type:'POST',
		 url :'getFiledetails.do',
		 data:{category:category,subcat:subcat},
		 success:function(response){
			 
			
			// response = $.parseJSON(response);
			 var length =response.Records.length;
			
			 var fileList = document.getElementById("fileList");
			 
			 var rowcount = fileList.childNodes.length;
				
				for(var j =1;j<=rowcount;j++ )
				{
					 fileList.removeChild(fileList.lastChild);
				}
			
			 var option1= document.createElement("option")
			 option1.value="0";
			 option1.text="--Select--";
			fileList.appendChild(option1);
			 
			 for(var i =0;i<length;i++ ) {
				
				 var option= document.createElement("option")
				  option.value = response.Records[i].inFileId;
				 option.text= response.Records[i].stFileName;
				fileList.appendChild(option);

			 }
			 document.getElementById("trfilelist").style.display="";
			 

		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });
	
	
}




