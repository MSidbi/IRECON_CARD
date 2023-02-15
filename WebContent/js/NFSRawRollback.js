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
		var  cycle =document.getElementById("cycle").value;
		var subcate = document.getElementById("stSubCategory").value;
		
	debugger;
	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}
	if(subcate == 0)
	{
		alert("Please Select subcategory");
		return false;
	}

	return true;
	
}


	function processSettlement() {
	debugger;
	var frm = $('#uploadform');
	
	
		var fileDate = document.getElementById("datepicker").value;
		var cycle = document.getElementById("cycle").value;
		var fileName = document.getElementById("fileName").value;
		var oMyForm = new FormData();
		
		oMyForm.append('cycle', cycle);
		oMyForm.append('datepicker',fileDate);
		oMyForm.append('fileName',fileName);
		if(ValidateData())  {
			$.ajax({
				type : "POST",
				url : "CashnetSettlement.do",
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
	function downloadSettlement() {

		var datepicker = document.getElementById("datepicker").value;
		var  cycle =document.getElementById("cycle").value;
		var fileName = document.getElementById("fileName").value;
		
		var oMyForm = new FormData();
		oMyForm.append('datepicker',datepicker);
		oMyForm.append('cycle',cycle);
		oMyForm.append('fileName',fileName);
		
		if(ValidateData())  {
		$.ajax({
				type : "POST",
				url : "CashnetProcessValidation.do",
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
					if(response == "success")
					{
						alert("Reports are getting downloaded. Please Wait");
						document.getElementById("reportform").submit();
					}
					else
					{
						alert(response);
					}

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

function getFields(e)
{
	var  fileName =document.getElementById("fileName").value;		
	
	if(fileName == 'REPORT')
	{
		document.getElementById("cycles").style.display = '';
	}
	else
	{
		document.getElementById("cycles").style.display = 'none';
	}

}
function RawRollback(){

	debugger;
	var frm = $('#reportform');
	
		var subcate = document.getElementById("stSubCategory").value;
		var fileDate = document.getElementById("datepicker").value;
		var cycle = document.getElementById("cycle").value;
		var oMyForm = new FormData();
		
		oMyForm.append('cycle', cycle);
		oMyForm.append('datepicker',fileDate);
		oMyForm.append('stSubCategory',subcate);
		oMyForm.append('fileName',"NFS");
		if(ValidateData())  {
			$.ajax({
				type : "POST",
				url : "CashnetRawRollback.do",
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