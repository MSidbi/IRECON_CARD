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
	
	var fileSelected = document.getElementById("fileName").value;
	//var  cycle =document.getElementById("cycle").value;
	var dataFile= document.getElementById("dataFile1").value;
	var leng= dataFile.length - 4;
	var fileExten = dataFile.substring(leng,dataFile.length);
	debugger;
	/*if(category == "")
	{
		alert("Please select category ");
		return false;
	}*/
	if(fileSelected == "0")
	{
		alert("Please select NTSL file of option");
		return false;
	}
	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}

	if(dataFile==""){

		alert("Please Upload File.");
		return false;
	}
	if(fileExten != '.txt' && fileExten != '.rpt')
	{
		alert("Upload Text or rpt file format");
		return false;
	}
	return true;

}

function processFisdomUpload() {
	debugger;
	var frm = $('#uploadform');
	
	
		var filename = document.getElementById("fileName").value;
		var userfile = document.getElementById("dataFile1");
		var fileDate = document.getElementById("datepicker").value;
		var oMyForm = new FormData();
		oMyForm.append('file',userfile.files[0])
		oMyForm.append('fileName', filename);
		oMyForm.append('fileDate',fileDate);
		if(ValidateData())  {
			$.ajax({
				type : "POST",
				url : "FisdomFileUpload.do",
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
					document.getElementById("fileName").value="0";
					// alert("2");
					// alert("3");
					document.getElementById("dataFile1").value="";
					// alert("8");
					document.getElementById("datepicker").value="";

				},
				
				error : function(err) {
					hideLoader();
					alert("Error Occurred");
				}
			});

		}
	
	
}
