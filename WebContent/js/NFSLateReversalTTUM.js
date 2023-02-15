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
  var fileDate = document.getElementById("dailypicker").value;

	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}

	return true;
	
}
function processReversalTTUM() {

	var frm = $('#reportform');
	var category = document.getElementById("rectyp").value;
	var datepicker = document.getElementById("dailypicker").value;

	if(ValidateData())  {
		var oMyForm = new FormData();
		oMyForm.append('category',category);
		oMyForm.append('datepicker',datepicker);
		$.ajax({
			type : "POST",
			url : "NFSLateReversalProcess.do",
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

function DownloadReversalTTUM() {

	var frm = $('#reportform');
	var category = document.getElementById("rectyp").value;
	var datepicker = document.getElementById("dailypicker").value;

	

	if(ValidateData())  {
		var oMyForm = new FormData();
		oMyForm.append('category',category);
		oMyForm.append('datepicker',datepicker);
		$.ajax({
			type : "POST",
			url : "NFSLateReversalValidate.do",
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



