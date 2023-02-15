

function seerule(e) {
	
	document.getElementById("fileValue").value=e;
	
	window.open("../DebitCard_Recon/SeeRule.do" , 'SeeRule', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
}


function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}


function ValidateData()
{
	//alert("Inside ValidateData() ");
	var category = document.getElementById("rectyp").value;
	var subcategory = document.getElementById("stSubCategory").value;
	var datepicker = document.getElementById("datepicker").value;
	var oldRemarks = document.getElementById("oldRemarks").value;
	var newRemarks = document.getElementById("newRemarks").value;
	var fileSelected = document.getElementById("stSelectedFile").value;
	var dataFile= document.getElementById("dataFile1").value;
	//alert("dataFile is "+dataFile);
	var leng= dataFile.length - 3;
	//alert("leng is "+leng);
	var fileExten = dataFile.substring(leng,dataFile.length);
	//alert("Extension is "+fileExten);
	
	debugger;
	if(category == "")
	{
		alert("Please select category ");
		return false;
	}
	if((subcategory == "" || subcategory == "-"))
	{
		if(category != "ONUS" && category != "AMEX" && category != "CARDTOCARD" && category != "WCC")
		{
			alert("Please select subcategory for "+category);
			return false;
		}
		/*else
		{
			document.getElementById("stSubCategory").value = "-";
			alert("1. "+document.getElementById("stSubCategory").value);
			var subcate = document.getElementById("stSubCategory").value;
			alert("check subcate "+subcate);
		}*/
		
	}
	if(fileSelected == "0")
	{
		alert("Please select file");
		return false;
	}
	if(datepicker == "")
	{
		alert("Please select date for processing");
		return false;
	}
	if(oldRemarks == "")
	{
		alert("Please enter Report Remarks");
		return false;
	}
	if(newRemarks == "")
	{
		alert("Please enter New Remarks to be updated in backend");
		return false;
	}
if(dataFile==""){
		
		alert("Please Upload File.");
		return false;
	}
if(fileExten != 'txt' && fileExten != 'dat')
{
	alert("Upload text/dat file");
	return false;
}

	return true;
	
}
function getFilesdata() {
	//alert("inside getFilesdata");
	debugger;
///	alert("inside getFilesdata");
//	alert("click");
	var category = document.getElementById("rectyp").value;
	//alert("rectyp "+category);
var subcat= document.getElementById("stSubCategory").value;
//alert("SUBCAT "+subcat);
	/*
	if(category =='RUPAY' || category == 'VISA') {
		document.getElementById("startDate").style.display='none';
		document.getElementById("enddate").style.display='none';
		document.getElementById("Date").style.display='';
		
		
	}else {
//		alert("Inside else");
		document.getElementById("Date").style.display='none';
		document.getElementById("startDate").style.display='';
		document.getElementById("enddate").style.display='';
	}*/
	
	if(subcat!="") {
		//alert("subcat is not blank ")
	$.ajax({
		 
		 type:'POST',
		 url :'getFiledetails.do',
		 data:{category:category,subcat:subcat},
		 success:function(response){
			 
			console.log(response);
			// response = $.parseJSON(response);
			 var length =response.records.length;
			
			 var compareFile1 = document.getElementById("stSelectedFile");
			// var compareFile2 = document.getElementById("Man_file");
			 
			 var rowcount = compareFile1.childNodes.length;
				
				for(var j =1;j<=rowcount;j++ )
				{
					compareFile1.removeChild(compareFile1.lastChild);
					//compareFile2.removeChild(compareFile2.lastChild);
				}
			
			 var option1= document.createElement("option");
			 option1.value="0";
			 option1.text="--Select--";
			 var opt1= document.createElement("option");
			 opt1.value="0";
			 opt1.text="--Select--";
			 compareFile1.appendChild(option1);
			//compareFile2.appendChild(opt1)
			 
			 for(var i =0;i<length;i++ ) {
				
				 var option= document.createElement("option");
				  option.value = response.records[i].stFileName;
				 option.text= response.records[i].stFileName;
				 compareFile1.appendChild(option);
			 }
			 
			
			

		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });
	}else {
		
		
		alert("Please Select Sub Category.");
		
	}
	
	
}

function processFileUpload() {
	debugger;
//alert("1111");
	var frm = $('#uploadform');
	

	
	var filename = document.getElementById("stSelectedFile").value;
//	alert("filename "+filename);
	var category = document.getElementById("rectyp").value;
	
	var userfile = document.getElementById("dataFile1");
	//alert("file is "+userfile);
	//var CSRFToken = $('[name=CSRFToken]').val();
	
	var  stSubCategory =document.getElementById("stSubCategory").value;
	
	var fileDate = document.getElementById("datepicker").value;
	
	var newRemarks = document.getElementById("newRemarks").value;
	var oldRemarks = document.getElementById("oldRemarks").value;
	
	var oMyForm = new FormData();
	
	
	oMyForm.append('file',userfile.files[0])
	oMyForm.append('stSelectedFile', filename);
	//oMyForm.append('fileType', fileType);
	oMyForm.append('category', category);
	oMyForm.append('stSubCategory',stSubCategory);
	oMyForm.append('datepicker',fileDate);
	oMyForm.append('oldRemarks',oldRemarks);
	oMyForm.append('newRemarks',newRemarks);
	
	//oMyForm.append('CSRFToken',CSRFToken);
	// alert("2222");
	if(ValidateData())  { 
	$.ajax({
			type : "POST",
			url : "manualKnockoff.do",
			enctype:"multipart/form-data",
			data :oMyForm ,
			
			processData : false,
			contentType : false,
			//type : 'POST',
			beforeSend : function() {
				showLoader();
			},
			complete : function(data) {
				document.getElementById("upload").disabled="";
				hideLoader();
				
			},
			//data:{category:rectyp,filedate:datepicker,subCat:subCat,fileSelected:fileSelected,oldRemarks:oldRemarks,newRemarks:newRemarks,userfile:userfile},
			success : function(response) {
				debugger;
				hideLoader();
				
				alert (response); 
				
				
				 //alert("1");
				 document.getElementById("stSelectedFile").value="";
				// alert("2");
				 document.getElementById("rectyp").value=category;
				// alert("3");
				 document.getElementById("stSubCategory").value="-";
				// alert("4");
				 document.getElementById("datepicker").value="";
				 //alert("5");
				 document.getElementById("oldRemarks").value="";
				 //alert("6");
				 document.getElementById("newRemarks").value="";
				 //alert("7");
				 document.getElementById("dataFile1").value="0";
				// alert("8");
				
				
						},
						/*  complete: function() {
						    window.location = 'SourceFileUpload.jsp';
						  },
						 */
		
						error : function(err) {
							hideLoader();
							alert("Error Occured");
						}
					});
	
	}
		
				}