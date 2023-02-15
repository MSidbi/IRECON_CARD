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
	var ttumType = document.getElementById("typeOfTTUM").value;
	debugger;
	
	if(fileDate == "" )
	{
		alert("Please Select file Date ");
		return false;
	}
	if(ttumType == "0")
	{
		alert("Please Select TTUM Type");
		return false;
	}

	return true;

}


function Process() {
	debugger;
	var frm = $('#reportform');
		var fileDate = document.getElementById("datepicker").value;
		var ttumType = document.getElementById("typeOfTTUM").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('fileDate',fileDate);
			oMyForm.append('typeOfTTUM',ttumType);
			$.ajax({
				type : "POST",
				url : "GenerateFisdomTTUM.do",
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
				
						alert(response);

				},
				
				error : function(err) {
					hideLoader();
					alert("Error Occurred");
				}
			});
			
		}
	
	
}

function Download() {
	debugger;
	var frm = $('#reportform');
		var fileDate = document.getElementById("datepicker").value;
		var ttumType = document.getElementById("typeOfTTUM").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('fileDate',fileDate);
			oMyForm.append('typeOfTTUM',ttumType);
			$.ajax({
				type : "POST",
				url : "ValidateFisdomTTUM.do",
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
						document.getElementById("processform").submit();
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

