/*function seerule(e) {
	
	document.getElementById("fileValue").value=e;
	
	window.open("../DebitCard_Recon/SeeRule.do" , 'SeeRule', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
}*/


function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}

function ValidateData()
{
		var fileDate = document.getElementById("datepicker").value;
	
	debugger;
	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}

	return true;
	
}

	function downloadSettlement() {

		var datepicker = document.getElementById("datepicker").value;
		
		var oMyForm = new FormData();
		oMyForm.append('datepicker',datepicker);
		
		if(ValidateData())  {
		$.ajax({
				type : "POST",
				url : "DhanalaxmiSettlementValidation.do",
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
	
	function downloadExcepReport() {

		var datepicker = document.getElementById("datepicker").value;
		
		var oMyForm = new FormData();
		oMyForm.append('datepicker',datepicker);
		
		if(ValidateData())  {
		$.ajax({
				type : "POST",
				url : "CbsGLUploadValidation.do",
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