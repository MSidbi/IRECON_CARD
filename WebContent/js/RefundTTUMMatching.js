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
	var category = document.getElementById("category").value;
	debugger;
	
	if(category == "0")
	{
		alert("Please select category ");
		return false;
	}
	if(fileDate == "" )
	{
		alert("Please Select file Date ");
		return false;
	}
	
	return true;

}

function process() {
	debugger;
	var frm = $('#reportform');
		var category = document.getElementById("category").value;
//		var  stSubCategory =document.getElementById("stSubCategory").value;
		var fileDate = document.getElementById("datepicker").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
//			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('fileDate',fileDate);
			$.ajax({
				type : "POST",
				url : "RefundTTUMMatching.do",
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

					alert (response); 
					/*document.getElementById("category").value="0";
					document.getElementById("datepicker").value="";*/

				},
				
				error : function(err) {
					hideLoader();
					alert("Error Occurred");
				}
			});
			
		}
	
	
}

function downloadReports() {
	debugger;
	var frm = $('#reportform');
		var category = document.getElementById("category").value;
//		var  stSubCategory =document.getElementById("stSubCategory").value;
		var fileDate = document.getElementById("datepicker").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
//			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('fileDate',fileDate);
			$.ajax({
				type : "POST",
				url : "ValidateRefundMatching.do",
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

function getCount() {
	debugger;
	var frm = $('#reportform');
		var category = document.getElementById("category").value;
//		var  stSubCategory =document.getElementById("stSubCategory").value;
		var fileDate = document.getElementById("datepicker").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('category', category);
//			oMyForm.append('stSubCategory',stSubCategory);
			oMyForm.append('fileDate',fileDate);
			$.ajax({
				type : "POST",
				url : "getRefundDataCount.do",
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