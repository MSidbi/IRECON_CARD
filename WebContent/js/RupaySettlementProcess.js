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
	var subcategory = document.getElementById("subcategory").value;
	var fileDate = document.getElementById("datepicker").value;
	var cycle = document.getElementById("cycle").value;
	debugger;
	
	if(fileDate == "")
	{
		alert("Please Select file Date ");
		return false;
	}
	if(subcategory=="-"){

		alert("Please Select Subcategory.");
		return false;
	}
	if(cycle == "0")
	{
		alert("Please Select Cycle.");
		return false;
	}
	
	return true;

}

function processSettlement(){


	debugger;
	var frm = $('#processform');
	var subcategory = document.getElementById("subcategory").value;
	var fileDate = document.getElementById("datepicker").value;
	var cycle = document.getElementById("cycle").value;

	if(ValidateData())  {
		var oMyForm = new FormData();
		oMyForm.append('subcategory', subcategory);
		oMyForm.append('fileDate',fileDate);
		oMyForm.append('cycle',cycle);
		$.ajax({
			type : "POST",
			url : "RupaySettlementProcess.do",
			data :oMyForm ,

			processData : false,
			contentType : false,
			beforeSend : function() {
				showLoader();
			},
			success : function(response) {
				debugger;
				hideLoader();

				alert (response); 
				/*document.getElementById("subcategory").value="-";
				document.getElementById("datepicker").value="";
				document.getElementById("cycle").value = "0";*/
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

	function DownloadSettlement() {
		debugger;
		var frm = $('#processform');
		var subcategory = document.getElementById("subcategory").value;
		var fileDate = document.getElementById("datepicker").value;
		var cycle = document.getElementById("cycle").value;
		
		if(ValidateData())  {
			var oMyForm = new FormData();
			oMyForm.append('subcategory', subcategory);
			oMyForm.append('fileDate',fileDate);
			oMyForm.append('cycle',cycle);
			$.ajax({
				type : "POST",
				url : "ValidateRupaySettlement.do",
				data :oMyForm ,

				processData : false,
				contentType : false,
				beforeSend : function() {
					showLoader();
				},
				success : function(response) {
					if(response == "success")
					{
						alert("Reports are getting downloaded. Please Wait");
						document.getElementById("processform").submit();
					}
					else
					{
						alert (response); 
						document.getElementById("subcategory").value="-";
						document.getElementById("datepicker").value="";
						document.getElementById("cycle").value="0";
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


function RupaySettlementRollback()
{
	debugger;
	var frm = $('#processform');
	var subcategory = document.getElementById("subcategory").value;
	var fileDate = document.getElementById("datepicker").value;
	var cycle = document.getElementById("cycle").value;
	
	if(ValidateData())  {
		var oMyForm = new FormData();
		oMyForm.append('subcategory', subcategory);
		oMyForm.append('fileDate',fileDate);
		oMyForm.append('cycle',cycle);
		$.ajax({
			type : "POST",
			url : "RupaySettRollback.do",
			data :oMyForm ,

			processData : false,
			contentType : false,
			beforeSend : function() {
				showLoader();
			},
			success : function(response) {
					alert (response); 
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