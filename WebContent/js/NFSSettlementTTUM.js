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
	var fileSelected = document.getElementById("fileName").value;
	
	var timePeriod = document.getElementById("timePeriod").value;
	
	if(timePeriod == "Daily")
	{	
		var fileDate = document.getElementById("dailypicker").value;
		//var  cycle =document.getElementById("cycle").value;
	}
	else
	{
		var fileDate = document.getElementById("monthpicker").value;
		//cycle = "0";
	}
	
	debugger;
	if(category == "")
	{
		alert("Please select category ");
		return false;
	}
	if(timePeriod == "Monthly" && (subcategory == "" || subcategory == "-"))
	{
			alert("Please select subcategory for "+category);
			return false;
	}
	if(fileSelected == "0" || fileSelected == " ")
	{
		alert("Please select file");
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
	/*if(timePeriod == "Daily" && cycle == "0")
	{
		alert("Please Select cycle");
		return false;
	}*/
	


	return true;
	
}
function processSettlementTTUM() {

		var frm = $('#reportform');
		
		var timePeriod = document.getElementById("timePeriod").value;
		
		var filename = document.getElementById("fileName").value;
		var category = document.getElementById("rectyp").value;
		
		/*var userfile = document.getElementById("dataFile1");
		alert("file is "+userfile);*/
		//var CSRFToken = $('[name=CSRFToken]').val();
		
		var  stSubCategory =document.getElementById("stSubCategory").value;
		/*var fileDate = document.getElementById("datepicker").value;
		alert("file date "+fileDate);*/
		if(timePeriod == "Daily")
		{	
			var datepicker = document.getElementById("dailypicker").value;
			//var  cycle =document.getElementById("cycle").value;
		}
		else
		{
			var datepicker = document.getElementById("monthpicker").value;
			//cycle = "0";
		}
		
		
		var oMyForm = new FormData();
		
		
		//oMyForm.append('file',userfile.files[0])
		oMyForm.append('fileName', filename);
		oMyForm.append('category', category);
		oMyForm.append('stSubCategory',stSubCategory);
		oMyForm.append('datepicker',datepicker);
		//oMyForm.append('cycle',cycle);
		
		if(ValidateData())  {
		$.ajax({

			type:'POST',
			url :'NFSMonthlySettValidation.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			
			data:{fileDate:datepicker,stSubCategory:stSubCategory,timePeriod:timePeriod,filename:filename},
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
	/*if(e.value == "Daily")
	{
		document.getElementById("cycles").style.display = '';
		document.getElementById("Date").style.display = '';
		document.getElementById("Month").style.display = 'none';
		document.getElementById("subCate").style.display = 'none';
	}
	else
	{
		document.getElementById("cycles").style.display = 'none';
		document.getElementById("Date").style.display = 'none';
		document.getElementById("Month").style.display = '';
		document.getElementById("subCate").style.display = '';
		
	}
	*/

	//GET filename
	var fileName = document.getElementById("fileName").value;
	
	if(fileName == "0")
	{
		alert("Please select File first");
	}
	else
	{
		if(fileName == "NTSL-NFS")
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
		else
		{
			if(e.value == "Daily")
			{
				//document.getElementById("cycles").style.display = 'none';
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
	}

}
function FileNameChange(e)
{
	document.getElementById("timePeriod").value = "0";
	//document.getElementById("timePeriod").value = "0";
	//document.getElementById("cycles").style.display = 'none';
	document.getElementById("Date").style.display = 'none';
	document.getElementById("Month").style.display = 'none';
	document.getElementById("subCate").style.display = 'none';
}
