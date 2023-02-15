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
	var fileDate = document.getElementById("datepicker").value;
	var dataFile= document.getElementById("dataFile1").value;
	var leng= dataFile.length - 3;
	var fileExten = dataFile.substring(leng,dataFile.length);
	var fileName = document.getElementById("fileName").value;
	var fileType = document.getElementById("fileType").value;
	
	debugger;
	
	if(fileDate == "")
	{
		alert("Please select File Date");
		return false;
	}
	if(dataFile==""){

		alert("Please Upload File.");
		return false;
	}
	if(fileExten != 'TXT' && fileExten != 'txt')
	{
		alert("Upload Text format");
		return false;
	}
	if(fileType == "")
	{
		alert("Select File Type");
		return false;
	}
	/*if(fileName == "JV" && fileExten != 'xls')
	{
		alert("Upload Excel format");
		return false;
	}*/
	if(fileName=="0")
	{
		alert("Please select file name from drop down");
		return false;
	}
	
	return true;

}

function FileUpload() {
	debugger;
	var frm = $('#uploadform');
		var userfile = document.getElementById("dataFile1");
		var fileDate = document.getElementById("datepicker").value;
		var fileName = document.getElementById("fileName").value;
		var fileType = document.getElementById("fileType").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('file',userfile.files[0])
			oMyForm.append('fileDate',fileDate);
			oMyForm.append('fileName',fileName);
			oMyForm.append('fileType',fileType);
			$.ajax({
				type : "POST",
				url : "VisaEPUpload.do",
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
				success : function(response) {
					debugger;
					hideLoader();

					alert (response); 
				},
				
				error : function(err) {
					hideLoader();
					alert("Error Occurred");
				}
			});
			
		}
	
	
}

