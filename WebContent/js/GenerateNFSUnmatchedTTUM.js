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
	var localDate = document.getElementById("localDate").value;
	var ttumType = document.getElementById("typeOfTTUM").value;
	var  stSubCategory =document.getElementById("stSubCategory").value;
	
	if(stSubCategory == 'ACQUIRER')
		{	
			ttumType = document.getElementById("acqtypeOfTTUM").value;
		}
	debugger;
	
	if(stSubCategory == "-")
	{
		alert("Please Select Sub Category");
		return false;
	}
	
	if(localDate == "" )
	{
		alert("Please Select Date ");
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
		var ttumType = document.getElementById("typeOfTTUM").value;
		var localDate = document.getElementById("localDate").value;
		var  stSubCategory =document.getElementById("stSubCategory").value;
		
		if(stSubCategory == 'ACQUIRER')
		{	
			ttumType = document.getElementById("acqtypeOfTTUM").value;
		}
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('typeOfTTUM',ttumType);
			oMyForm.append('localDate',localDate);
			$.ajax({
				type : "POST",
				url : "GenerateNFSUnmatchedTTUM.do",
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
		var localDate = document.getElementById("localDate").value;
		var  stSubCategory =document.getElementById("stSubCategory").value;	
		var ttumType = document.getElementById("typeOfTTUM").value;
		
		if(stSubCategory == 'ACQUIRER')
		{	
			ttumType = document.getElementById("acqtypeOfTTUM").value;
		}		
		
				
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('localDate',localDate);
			oMyForm.append('typeOfTTUM',ttumType);
			$.ajax({
				type : "POST",
				url : "checkNFSTTUMProcessed.do",
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
	var  stSubCategory =document.getElementById("stSubCategory").value;		
	
	if(stSubCategory == 'ISSUER')
	{
		document.getElementById("issuerOpt").style.display = '';
		document.getElementById("acquirerOpt").style.display = 'none';
		document.getElementById("acqtypeOfTTUM").value = "0";
	}
	else
	{
		document.getElementById("issuerOpt").style.display = 'none';
		document.getElementById("acquirerOpt").style.display = '';
		document.getElementById("typeOfTTUM").value = "0";
	}

}

function RollbackTTUM()
{
	debugger;
	var frm = $('#reportform');
		var category = document.getElementById("category").value;
		var ttumType = document.getElementById("typeOfTTUM").value;
		var localDate = document.getElementById("localDate").value;
		var  stSubCategory =document.getElementById("stSubCategory").value;
		
		if(stSubCategory == 'ACQUIRER')
		{	
			ttumType = document.getElementById("acqtypeOfTTUM").value;
		}
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('typeOfTTUM',ttumType);
			oMyForm.append('localDate',localDate);
			$.ajax({
				type : "POST",
				url : "NFSTTUMRollback.do",
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