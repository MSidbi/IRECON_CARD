 
function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}

function ValidateData()
{
	
		var fileDate = document.getElementById("datepicker").value;
	//	var  cycle =document.getElementById("cycle").value;
		var cate = document.getElementById("stSubCategory").value;
		
	debugger;
	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}


 

	return true;
	
}


 

function VisaRawRollback(){

	debugger;
	var frm = $('#reportform');
	
	
	
	
	
        var fileDate = document.getElementById("datepicker").value;
	//	var  cycle =document.getElementById("cycle").value;
		var cate = document.getElementById("stSubCategory").value;
		
		
		
			
 
		
	//	oMyForm.append('cycle', cycle);
        var oMyForm = new FormData();
		oMyForm.append('datepicker',fileDate);
		oMyForm.append('stSubCategory',cate);
		oMyForm.append('fileName',"NFS");
		if(ValidateData())  {
			$.ajax({
				type : "POST",
				url : "VisaRawRollback.do",
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