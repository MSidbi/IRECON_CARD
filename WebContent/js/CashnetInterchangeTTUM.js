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
	var category = document.getElementById("rectyp").value;
	var subcategory = document.getElementById("stSubCategory").value;
	var timePeriod = document.getElementById("timePeriod").value;
	
	if(timePeriod == "Daily")
	{	
		var fileDate = document.getElementById("dailypicker").value;
		//var  cycle =document.getElementById("cycle").value;
	}
	else
	{
		var fileDate = document.getElementById("monthpicker").value;
	//	cycle = "0";
	}
	
	debugger;
	
	if(timePeriod == "Monthly" && (subcategory == "" || subcategory == "-"))
	{
			alert("Please select subcategory for "+category);
			return false;
	}
	if(timePeriod == "0")
	{
		alert("Please select Time Period");
		return false;
	}
	if(timePeriod != "0" && fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}
	


	return true;
	
}

function processInterchangeTTUM() {
		var frm = $('#reportform');
		
		var timePeriod = document.getElementById("timePeriod").value;
		var category = document.getElementById("rectyp").value;
		var  stSubCategory =document.getElementById("stSubCategory").value;
		
		if(timePeriod == "Daily")
		{	
			var datepicker = document.getElementById("dailypicker").value;
			stSubCategory = "-";
			
		}
		else
		{
			var datepicker = document.getElementById("monthpicker").value;
			//cycle = "0";
			alert("datepicker "+datepicker);
		}
		
		var oMyForm = new FormData();
		oMyForm.append('category', category);
		oMyForm.append('stSubCategory',stSubCategory);
		oMyForm.append('datepicker',datepicker);
		oMyForm.append('timePeriod',timePeriod);
		
		if(ValidateData())  {
		$.ajax({

			type:'POST',
			url :'ProcessCashnetInterchangeTTUM.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			
			data:{datepicker:datepicker,stSubCategory:stSubCategory,timePeriod:timePeriod},
			success:function(response){
				
				/*if(response == "success")
				{
					alert("Reports are getting downloaded. Please Wait");
					document.getElementById("reportform").submit();
				}
				else
				{
					alert(response);
				}*/
				alert(response);

			},error: function(){
				alert("Error Occured");

			},
			complete : function(data) {

				hideLoader();

			},
		});
		
		}
			
	}
function DownloadInterchangeTTUM() {
	var frm = $('#reportform');
	
	var timePeriod = document.getElementById("timePeriod").value;
	var category = document.getElementById("rectyp").value;
	var  stSubCategory =document.getElementById("stSubCategory").value;
	
	if(timePeriod == "Daily")
	{	
		var datepicker = document.getElementById("dailypicker").value;
		stSubCategory = "-";
		
	}
	else
	{
		var datepicker = document.getElementById("monthpicker").value;
		//cycle = "0";
	}
	
	var oMyForm = new FormData();
	oMyForm.append('category', category);
	oMyForm.append('stSubCategory',stSubCategory);
	oMyForm.append('datepicker',datepicker);
	oMyForm.append('timePeriod',timePeriod);
	
	if(ValidateData())  {
		$.ajax({

			type:'POST',
			url :'ValidateDownloadInterchangeTTUM.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			
			data:{datepicker:datepicker,stSubCategory:stSubCategory,timePeriod:timePeriod},
			success:function(response){
				
				if(response == "success")
				{
					alert("Reports are getting downloaded. Please Wait");
					document.getElementById("reportform").submit();
				}
				else
				{
					alert(response);
				}

			},error: function(){
				alert("Error Occured");

			},
			complete : function(data) {

				hideLoader();

			},
		});
		
	}
		
}


function getFields(e)
{
			if(e.value == "Daily")
			{
				//document.getElementById("cycles").style.display = '';
				document.getElementById("Date").style.display = '';
				document.getElementById("Month").style.display = 'none';
				document.getElementById("subCate").style.display = 'none';

			}
			else
			{
				//document.getElementById("cycles").style.display = 'none';
				document.getElementById("Date").style.display = 'none';
				document.getElementById("Month").style.display = '';
				document.getElementById("subCate").style.display = '';
			}
	
}
