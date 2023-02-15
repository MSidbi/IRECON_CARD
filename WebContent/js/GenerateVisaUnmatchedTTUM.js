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
	//var fileDate = document.getElementById("datepicker").value;
	//var fileName = document.getElementById("fileName").value;
	var ttumType = document.getElementById("typeOfTTUM").value;
	var  stSubCategory =document.getElementById("stSubCategory").value;
	debugger;
	
	/*if(ttumType == "SURCHARGE" && fileDate == "" )
	{
		alert("Please Select file Date ");
		return false;
	}*/
	if(stSubCategory == "-")
	{
		alert("Please Select Sub Category");
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
		var category = document.getElementById("category").value;
		var  stSubCategory =document.getElementById("stSubCategory").value;
		//var fileDate = document.getElementById("datepicker").value;
		//var fileName = document.getElementById("fileName").value;
		var ttumType = document.getElementById("typeOfTTUM").value;
		var localDate = document.getElementById("localDate").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('typeOfTTUM',ttumType);
			oMyForm.append('localDate',localDate);
			$.ajax({
				type : "POST",
				url : "GenerateVisaUnmatchedTTUM.do",
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
		var category = document.getElementById("category").value;
		var  stSubCategory =document.getElementById("stSubCategory").value;
		var ttumType = document.getElementById("typeOfTTUM").value;
		var localDate = document.getElementById("localDate").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('typeOfTTUM',ttumType);
			oMyForm.append('localDate',localDate);
			$.ajax({
				type : "POST",
				url : "checkVisaTTUMProcessed.do",
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

function RollbackTTUM()
{

	debugger;
	var frm = $('#reportform');
		var category = document.getElementById("category").value;
		var  stSubCategory =document.getElementById("stSubCategory").value;
		//var fileDate = document.getElementById("datepicker").value;
		//var fileName = document.getElementById("fileName").value;
		var ttumType = document.getElementById("typeOfTTUM").value;
		var localDate = document.getElementById("localDate").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('typeOfTTUM',ttumType);
			oMyForm.append('localDate',localDate);
			$.ajax({
				type : "POST",
				url : "VisaTTUMRollback.do",
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

function getFields(e)
{
	var  stSubCategory =document.getElementById("stSubCategory").value;		
	
	if(stSubCategory == 'ISSUER')
	{
		document.getElementById("issuerOpt").style.display = '';
		document.getElementById("acquirerOpt").style.display = 'none';
	}
	else
	{
		document.getElementById("issuerOpt").style.display = 'none';
		document.getElementById("acquirerOpt").style.display = '';
	}

}