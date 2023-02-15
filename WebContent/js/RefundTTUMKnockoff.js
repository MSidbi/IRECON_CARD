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
	var category = document.getElementById("category").value;
	var operation = document.getElementById("operation").value;
	var remarks = document.getElementById("newRemarks").value;
	var dataFile= document.getElementById("dataFile1").value;
	var leng= dataFile.length - 3;
	var fileExten = dataFile.substring(leng,dataFile.length);
	
	debugger;
	
	if(category == "0")
	{
		alert("Please select category ");
		return false;
	}
	if(fileDate == "" )
	{
		alert("Please Select file Date ");
		return false;
	}
	if(operation == "0")
	{
		alert("Please select operation ");
		return false;
	}
	if(operation == "2" && remarks == "")
	{
		alert("Please Enter remarks");
		return false;
	}
	if(operation == "2" && dataFile == "")
	{
		alert("Please Upload file");
		return false;
	}
	if(fileExten != "dat" && fileExten != "txt")
	{
		alert("Please upload dat or text file");
		return false;
	}
	return true;

}

function process() {
	debugger;
	var frm = $('#reportform');
		var category = document.getElementById("category").value;
//		var  stSubCategory =document.getElementById("stSubCategory").value;
		var fileDate = document.getElementById("datepicker").value;
		var userfile = document.getElementById("dataFile1");
		var operation = document.getElementById("operation").value;
		var newRemarks = document.getElementById("newRemarks").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('file',userfile.files[0]);
			oMyForm.append('category', category);
//			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('fileDate',fileDate);
			oMyForm.append('operation',operation);
			oMyForm.append('newRemarks',newRemarks);
			$.ajax({
				type : "POST",
				url : "RefundTTUMKnockoff.do",
				data :oMyForm ,

				processData : false,
				contentType : false,
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



function getField(e)
{
		
		if(e.value == "2")
		{
			document.getElementById("Remarks").style.display = '';
		//	document.getElementById("fileUpload").style.display = '';
		}
		else
		{
			document.getElementById("Remarks").style.display = 'none';
			//document.getElementById("fileUpload").style.display = 'none';
		}
}

