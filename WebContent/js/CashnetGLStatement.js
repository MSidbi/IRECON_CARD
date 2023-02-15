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
	var category = document.getElementById("category").value;
	var  stSubCategory =document.getElementById("stSubCategory").value;
	var fileDate = document.getElementById("fileDate").value;
	if(category === 'VISA'){
		var atmPos = document.getElementById("atmPos").value;
		}
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
	if(fileDate == ""){
		alert("Please enter filedate");
		return false;
	}
	
	if(atmPos == "-" && stSubCategory !== 'ACQUIRER'){
		alert("Please enter VISA type");
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
		var fileDate = document.getElementById("fileDate").value;
		var closingbal = document.getElementById("closingBal").value;
		if(category === 'VISA'){
		var atmPos = document.getElementById("atmPos").value;
		}
		
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('fileDate',fileDate);
			oMyForm.append('closingBal',closingbal);
			if(category === 'VISA'){
				oMyForm.append('atmPos',atmPos);	
			}
			$.ajax({
				type : "POST",
				url : "getCashnetGLStatement.do",
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
		var fileDate = document.getElementById("fileDate").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('fileDate',fileDate);
			$.ajax({
				type : "POST",
				url : "checkCashnetStatementProcessed.do",
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
	var  stSubCategory =document.getElementById("stSubCategory").value;		
	var category = document.getElementById("category").value;
	
	if(category ==! 'VISA'){
		if(stSubCategory == 'ISSUER')
		{
			document.getElementById("Closing").style.display = 'none';
		}
		else
		{
			document.getElementById("Closing").style.display = '';
		}
	}else
	{
		if(stSubCategory == 'ISSUER'){
		
		document.getElementById("visaType").style.display = '';
		}
		document.getElementById("Closing").style.display = '';
	}

}


function TTUMrollback()
{

	debugger;
	var frm = $('#reportform');
		var category = document.getElementById("category").value;
		var  stSubCategory =document.getElementById("stSubCategory").value;
		var fileDate = document.getElementById("fileDate").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('fileDate',fileDate);
			$.ajax({
				type : "POST",
				url : "CashnetStatementRollback.do",
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

