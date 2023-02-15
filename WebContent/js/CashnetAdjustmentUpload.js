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
	//var stSubCategory = document.getElementById("stSubCategory").value;
	var fileDate = document.getElementById("datepicker").value;
	var dataFile= document.getElementById("dataFile1").value;
	var leng= dataFile.length - 4;
	var fileExten = dataFile.substring(leng,dataFile.length);
	debugger;
	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}
	
	/*	if(stSubCategory == "0")
	{
		alert("Please select Subcategory");
		return false;
	}*/

	if(dataFile==""){

		alert("Please Upload File.");
		return false;
	}
	if(fileExten != '.xls' && fileExten != 'xlsx')
	{
		alert("Upload Excel file format");
		return false;
	}
	return true;

}

function processAdjFileUpload() {
	debugger;
	var frm = $('#uploadform');
	
	
		var filename = document.getElementById("fileName").value;
		//var stSubCategory = document.getElementById("stSubCategory").value;
		var userfile = document.getElementById("dataFile1");
		var fileDate = document.getElementById("datepicker").value;
		var oMyForm = new FormData();
		
		oMyForm.append('file',userfile.files[0])
		oMyForm.append('filename', filename);
		oMyForm.append('category', "CASHNET");
		oMyForm.append('datepicker',fileDate);
		//oMyForm.append('stSubCategory',stSubCategory);
		if(ValidateData())  {
			$.ajax({
				type : "POST",
				url : "CashnetAdjustmentFileUpload.do",
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

function getCycle(e)
{
	//GET filename
	var fileName = document.getElementById("fileName").value;
	
	if(fileName == "0")
	{
		alert("Please select File first");
	}
	else
	{
		if(fileName == "NTSL-NFS")
		{
			if(e.value == "Daily")
			{
				document.getElementById("cycles").style.display = '';
				document.getElementById("Date").style.display = '';
				document.getElementById("Month").style.display = 'none';
				document.getElementById("subCate").style.display = 'none';

			}
			else
			{
				document.getElementById("cycles").style.display = 'none';
				document.getElementById("Date").style.display = 'none';
				document.getElementById("Month").style.display = '';
				document.getElementById("subCate").style.display = '';
			}
		}
		else
		{
			if(e.value == "Daily")
			{
				document.getElementById("cycles").style.display = 'none';
				document.getElementById("Date").style.display = '';
				document.getElementById("Month").style.display = 'none';
				document.getElementById("subCate").style.display = 'none';

			}
			else
			{
				document.getElementById("cycles").style.display = 'none';
				document.getElementById("Date").style.display = 'none';
				document.getElementById("Month").style.display = '';
				document.getElementById("subCate").style.display = '';
			}
			
		}
	}
}
function FileNameChange(e)
{
	document.getElementById("timePeriod").value = "0";
	//document.getElementById("timePeriod").value = "0";
	document.getElementById("cycles").style.display = 'none';
	document.getElementById("Date").style.display = 'none';
	document.getElementById("Month").style.display = 'none';
	document.getElementById("subCate").style.display = 'none';
}
