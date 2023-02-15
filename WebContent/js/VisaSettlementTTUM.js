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
	//var subCategory= document.getElementById("subCategory").value;
	
	if(fileDate == "")
	{
		alert("Please select File Date");
		return false;
	}
/*	if(subCategory=="0"){

		alert("Please select subcategory.");
		return false;
	}*/
	return true;

}

function TTUMProcess() {
	debugger;
	var frm = $('#processform');
		//var subCategory = document.getElementById("subCategory").value;
		var fileDate = document.getElementById("datepicker").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			//oMyForm.append('subCategory',subCategory)
			oMyForm.append('fileDate',fileDate);
			$.ajax({
				type : "POST",
				url : "VisaSettlementTTUMProces.do",
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

function DownloadTTUMReport() {
	debugger;
	var frm = $('#processform');
		//var subCategory = document.getElementById("subCategory").value;
		var fileDate = document.getElementById("datepicker").value;
		
		if(ValidateData())  {
			
			var oMyForm = new FormData();
			//oMyForm.append('subCategory',subCategory)
			oMyForm.append('fileDate',fileDate);
			$.ajax({
				type : "POST",
				url : "ValidateVisaSettlementTTUM.do",
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
				}
			});
			
		}
	
	
}

