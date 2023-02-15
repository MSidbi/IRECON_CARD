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
	var timePeriod = document.getElementById("timePeriod").value;
	var category = document.getElementById("rectyp").value;
	var subcategory = document.getElementById("stSubCategory").value;
	if(timePeriod == "Daily")
	{	
		var fileDate = document.getElementById("datepicker").value;
		//var  cycle =document.getElementById("cycle").value;
	}
	else
	{
		var fileDate = document.getElementById("monthpicker").value;
		//var cycle = "0";
	}
	var fileSelected = document.getElementById("fileName").value;
	//var  cycle =document.getElementById("cycle").value;
	var dataFile= document.getElementById("dataFile1").value;
	var leng= dataFile.length - 3;
	var fileExten = dataFile.substring(leng,dataFile.length);
	debugger;
	/*if(category == "")
	{
		alert("Please select category ");
		return false;
	}*/
	if(timePeriod == "Monthly" && (subcategory == "" || subcategory == "-"))
	{
			alert("Please select subcategory for "+category);
			return false;
	}
	if(fileSelected == "0")
	{
		alert("Please select NTSL file of option");
		return false;
	}
	if(timePeriod == "0")
	{
		alert("Please select Time Period");
	}
	if(timePeriod != "0" && fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}

	if(dataFile==""){

		alert("Please Upload File.");
		return false;
	}
	if(fileExten != 'xls')
	{
		alert("Upload Excel file format");
		return false;
	}
	
	return true;

}

function processNTSLFileUpload() {
	debugger;
	var frm = $('#uploadform');
	var timePeriod = document.getElementById("timePeriod").value;
	
		var filename = document.getElementById("fileName").value;
		var category = document.getElementById("rectyp").value;
		var userfile = document.getElementById("dataFile1");
		var  stSubCategory =document.getElementById("stSubCategory").value;
		if(timePeriod == "Daily")
		{	
			var fileDate = document.getElementById("datepicker").value;
			//var  cycle =document.getElementById("cycle").value;
			if(filename != "NTSL-NFS")
			{
				var  cycle = "1";
			}
		}
		else
		{
			var fileDate = document.getElementById("monthpicker").value;
			cycle = "0";
		}
		var files = [];
		for(var i= 0 ; i< userfile.files.length ; i++)
		{
			files[i] = userfile.files[i];
		}
		
		if(ValidateData())  {
			
			for(var i= 0 ; i< userfile.files.length ; i++)
			{
			
			var oMyForm = new FormData();
			//oMyForm.append('file',userfile.files[0])
			oMyForm.append('file',files[i])
			oMyForm.append('fileName', filename);
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('datepicker',fileDate);
			//oMyForm.append('cycle',cycle);
			oMyForm.append('timePeriod',timePeriod);
			$.ajax({
				type : "POST",
				url : "NTSLFileUpload.do",
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
					/*document.getElementById("fileName").value="0";
					// alert("2");
					document.getElementById("rectyp").value=category;
					// alert("3");
					document.getElementById("stSubCategory").value="-";
					// alert("4");
					document.getElementById("timePeriod").value = "0";
					//alert("5");
					document.getElementById("dataFile1").value="";
					// alert("8");
					//document.getElementById("cycle").value="0";
					document.getElementById("datepicker").value="";
					document.getElementById("monthpicker").value="";
					
					//document.getElementById("cycles").style.display = 'none';
					document.getElementById("Date").style.display = 'none';
					document.getElementById("Month").style.display = 'none';
*/
				},
				
				error : function(err) {
					hideLoader();
					alert("Error Occurred");
				}
			});
			}
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
				//document.getElementById("cycles").style.display = '';
				document.getElementById("Date").style.display = '';
				document.getElementById("Month").style.display = 'none';
				document.getElementById("subCate").style.display = 'none';

			}
			else
			{
				//document.getElementById("cycles").style.display = 'none';
				document.getElementById("Date").style.display = 'none';
				document.getElementById("Month").style.display = '';
				document.getElementById("subCate").style.display = '';
			}
		}
		else
		{
			if(e.value == "Daily")
			{
				//document.getElementById("cycles").style.display = 'none';
				document.getElementById("Date").style.display = '';
				document.getElementById("Month").style.display = 'none';
				document.getElementById("subCate").style.display = 'none';

			}
			else
			{
				//document.getElementById("cycles").style.display = 'none';
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
	//document.getElementById("cycles").style.display = 'none';
	document.getElementById("Date").style.display = 'none';
	document.getElementById("Month").style.display = 'none';
	document.getElementById("subCate").style.display = 'none';
}

function RollBackNTSL()
{
	debugger;

	var frm = $('#reportform');
	
	var timePeriod = document.getElementById("timePeriod").value;
	
	var filename = document.getElementById("fileName").value;
	var category = document.getElementById("rectyp").value;
	
	if(timePeriod == "Daily")
	{	
		var datepicker = document.getElementById("dailypicker").value;
		if(filename == "NTSL-NFS")
		{
			var  cycle =document.getElementById("cycle").value;
		}
		else
		{
			var  cycle = "1";
		}
	}
	else
	{
		var datepicker = document.getElementById("monthpicker").value;
		cycle = "0";
	}
	
	
	var oMyForm = new FormData();
	
	
	//oMyForm.append('file',userfile.files[0])
	oMyForm.append('fileName', filename);
	oMyForm.append('category', category);
	oMyForm.append('stSubCategory',"-");
	oMyForm.append('datepicker',datepicker);
	oMyForm.append('cycle',cycle);
	
	if(RollbackValidateData())  {
	$.ajax({
			type : "POST",
			url : "NTSLRollback.do",
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
					alert(response);

			},				
			error : function(err) {
				hideLoader();
				alert("Error Occurred");
			},
			complete : function(data) {

				hideLoader();

			},
	});
	
	}
}
function RollbackValidateData()
{
	var timePeriod = document.getElementById("timePeriod").value;
	var category = document.getElementById("rectyp").value;
	var subcategory = document.getElementById("stSubCategory").value;
	var fileDate = document.getElementById("dailypicker").value;
	var fileSelected = document.getElementById("fileName").value;
	var cycle = document.getElementById("cycle").value;
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
	if(cycle == "0")
		{
			alert("Please select cycle");
			return false;
		}

	return true;

}