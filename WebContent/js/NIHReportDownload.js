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
	var ttumType = document.getElementById("typeOfTTUM").value;
	var  localDate =document.getElementById("localDate").value;
	debugger;
	
	/*if(ttumType == "SURCHARGE" && fileDate == "" )
	{
		alert("Please Select file Date ");
		return false;
	}*/
	if(ttumType == "0")
	{
		alert("Please Select TTUM Type");
		return false;
	}
	if(localDate == "")
	{
		alert("Please Select date");
		return false;
	}
	

	return true;

}

function Download() {
	debugger;
	var frm = $('#reportform');
		var ttumType = document.getElementById("typeOfTTUM").value;
		var localDate = document.getElementById("localDate").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('typeOfTTUM',ttumType);
			oMyForm.append('localDate',localDate);
			$.ajax({
				type : "POST",
				url : "checkNIHData.do",
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
	
function getFields(e)
{
	debugger;
	//GET filename
	var typeOfTTUM = document.getElementById("typeOfTTUM").value;
	
	if(typeOfTTUM == "0")
	{
		alert("Please select TTUM Type");
	}
	else
	{
		if(typeOfTTUM != "REVERSAL")
		{
				document.getElementById("ttumDate").style.display = '';
				document.getElementById("date").style.display = 'none';
		}
		else
		{
				document.getElementById("ttumDate").style.display = 'none';
				document.getElementById("date").style.display = '';
		}
		
		
	}

}

