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
		var fileName = document.getElementById("fileName").value;
	
	debugger;
	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}
	if(fileName == "0")
	{
		alert("Please Select cycle");
		return false;
	}

	return true;
	
}


function processEOD() {
	debugger;
	var frm = $('#uploadform');
	
	
		var fileDate = document.getElementById("datepicker").value;
		var fileName = document.getElementById("fileName").value;
		var oMyForm = new FormData();
		
		oMyForm.append('datepicker',fileDate);
		oMyForm.append('fileName',fileName);
		if(ValidateData())  {
			$.ajax({
				type : "POST",
				url : "ProcessEODReport.do",
				enctype:"multipart/form-data",
				data :oMyForm ,

				processData : false,
				contentType : false,
				//type : 'POST',
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {
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
	function downloadEOD() {
        debugger;
		var datepicker = document.getElementById("datepicker").value;
		var fileName = document.getElementById("fileName").value;
		
		var oMyForm = new FormData();
		oMyForm.append('datepicker',datepicker);
		oMyForm.append('fileName',fileName);
		
		
		if(fileName == "REPORT")
		{
		if(ValidateData())  {
		$.ajax({
				type : "POST",
				url : "EODReportProcessValidation.do",
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
						//document.getElementById("reportform").submit();
						$('#reportform').attr('action', 'DownloadEODReport.do');
						$('#reportform').submit();
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
	else
	{
		if(ValidateData())  {
			$.ajax({
					type : "POST",
					url : "EODReportProcessValidation.do",
					enctype:"multipart/form-data",
					data :oMyForm ,

					processData : false,
					contentType : false,
					beforeSend : function() {
						showLoader();
					},
					complete : function(data) {
						hideLoader();

					},
					success : function(response) {
						if(response == "success")
					{
						alert("Reports are getting downloaded. Please Wait");
						//document.getElementById("reportform").submit();
						$('#reportform').attr('action', 'DownloadEODVoucher.do');
						$('#reportform').submit();
					}
					else
					{
						alert(response);
					}
								

					},				
					error : function(err) {
						hideLoader();
						alert("Error Occurred");
					}
				});				
			
			}	
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