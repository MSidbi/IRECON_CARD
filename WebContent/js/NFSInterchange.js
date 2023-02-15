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
	//	cycle = "0";
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
	


	return true;
	
}

function processInterchange() {
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
			stSubCategory = "-";
			/*if(filename == "NTSL-NFS")
			{
				var  cycle =document.getElementById("cycle").value;
			}
			else
			{
				var  cycle = "1";
			}
			alert("cycle is "+cycle);*/
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
			url :'NFSInterchangeValidation.do',
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

function skipSettlement() {

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
			var  cycle =document.getElementById("cycle").value;
		}
		else
		{
			var datepicker = document.getElementById("monthpicker").value;
			cycle = "0";
		}
		
		
		var oMyForm = new FormData();
		
		
		//oMyForm.append('file',userfile.files[0])
		oMyForm.append('fileName', filename);
		oMyForm.append('category', category);
		oMyForm.append('stSubCategory',stSubCategory);
		oMyForm.append('datepicker',datepicker);
		oMyForm.append('cycle',cycle);
		
		if(ValidateNTSLData())  {
		$.ajax({

			type:'POST',
			url :'SkipSettlement.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			
			data:{fileDate:datepicker,stSubCategory:stSubCategory,timePeriod:timePeriod,cycle:cycle,filename:filename},
			success:function(response){
				debugger;
				hideLoader();

				alert (response); 
				document.getElementById("fileName").value="0";
				// alert("2");
				document.getElementById("rectyp").value=category;
				// alert("3");
				document.getElementById("stSubCategory").value="-";
				// alert("4");
				document.getElementById("cycle").value="0";
				document.getElementById("datepicker").value="";
				document.getElementById("monthpicker").value="";
				
				document.getElementById("Month").style.display = 'none';
				document.getElementById("subCate").style.display = 'none';
				document.getElementById("cycles").style.display = 'none';
				document.getElementById("Date").style.display = 'none';
		
			},error: function(){
				alert("Error Occured");

			},
			complete : function(data) {

				hideLoader();

			},
		});
		
		}
			
	}

/*function getCycle(e)
{
	if(e.value == "Daily")
	{
		document.getElementById("cycles").style.display = '';
		document.getElementById("Date").style.display = '';
		document.getElementById("Skip").style.display = '';
		document.getElementById("Month").style.display = 'none';
		document.getElementById("subCate").style.display = 'none';
	}
	else
	{
		document.getElementById("cycles").style.display = 'none';
		document.getElementById("Date").style.display = 'none';
		document.getElementById("Skip").style.display = 'none';
		document.getElementById("Month").style.display = '';
		document.getElementById("subCate").style.display = '';
		
	}
	
	
}*/
function getFields(e)
{
	//GET filename
	var fileName = document.getElementById("fileName").value;
//	alert("Filename is "+fileName);
	if(fileName == "0")
	{
		alert("Please select File first");
	}
	else
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