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
	var adjType =document.getElementById("adjType").value;
	var fileDate = document.getElementById("dailypicker").value;
	
	debugger;
	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}
	if(adjType == "")
	{
		alert("Please select Adjustment Type");
		return false;
	}
	return true;
	
}

function DownloadAdjTTUM() {

		var frm = $('#reportform');
		
		var adjType =document.getElementById("adjType").value;
		var fileDate = document.getElementById("dailypicker").value;
			
		var oMyForm = new FormData();
		
		oMyForm.append('fileDate',fileDate);
		oMyForm.append('adjType',adjType);
		
		if(ValidateData())  {
		$.ajax({

			type:'POST',
			url :'ValidateDownloadRupayAdjTTUM.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			
			data:{fileDate:fileDate,adjType:adjType},
			success:function(response){
				
				response = "success";
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

function processAdjTTUM() {

	var frm = $('#reportform');
	
	var adjType =document.getElementById("adjType").value;
	var fileDate = document.getElementById("dailypicker").value;
	var oMyForm = new FormData();
	
	oMyForm.append('fileDate',fileDate);
	oMyForm.append('adjType',adjType);
	
	if(ValidateData())  {
	$.ajax({

		type:'POST',
		url :'RupayAdjustmentProcess.do',
		async: true,
		beforeSend : function() {
			showLoader();
		},
		
		data:{fileDate:fileDate,adjType:adjType},
		success:function(response){
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
function getCycle(e)
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
		if(fileName == "NTSL-NFS")
		{
			if(e.value == "Daily")
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
		}
		else
		{
			if(e.value == "Daily")
			{
				document.getElementById("cycles").value = "1";
				document.getElementById("cycles").style.display = 'none';
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
			
		}
	}
}
function FileNameChange(e)
{
	if(e.value == "NTSL-NFS")
	{
		document.getElementById("cycles").style.display = '';
	}
	else
	{
	//document.getElementById("timePeriod").value = "0";
	//document.getElementById("timePeriod").value = "0";
	document.getElementById("cycles").style.display = 'none';
	//document.getElementById("Date").style.display = 'none';
	//document.getElementById("Month").style.display = 'none';
	//document.getElementById("subCate").style.display = 'none';
	}
}