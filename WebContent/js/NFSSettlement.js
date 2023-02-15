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
	//var subcategory = document.getElementById("stSubCategory").value;
	var fileSelected = document.getElementById("fileName").value;
	
	/*var timePeriod = document.getElementById("timePeriod").value;
	
	if(timePeriod == "Daily")
	{*/	
		var fileDate = document.getElementById("dailypicker").value;
		var  cycle =document.getElementById("cycle").value;
	/*}
	else
	{
		var fileDate = document.getElementById("monthpicker").value;
		cycle = "0";
	}*/
	
	debugger;
	if(category == "")
	{
		alert("Please select category ");
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
	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}
	//if(timePeriod == "Daily" && cycle == "0")
	if(fileSelected == "NTSL-NFS" && cycle == "0")
	{
		alert("Please Select cycle");
		return false;
	}
	


	return true;
	
}

function ValidateRawData()
{
	//var category = document.getElementById("rectyp").value;
	var subcategory = document.getElementById("stSubCategory").value;
	var fileDate = document.getElementById("datepicker").value;
	//var  cycle =document.getElementById("cycle").value;
	var fileSelected = document.getElementById("fileName").value;
	//var  cycle =document.getElementById("cycle").value;
	var dataFile= document.getElementById("dataFile1").value;
	var leng= dataFile.length - 4;
	var fileExten = dataFile.substring(leng,dataFile.length);
	
	//alert("FileExten "+fileExten);
	
	debugger;
	if((subcategory == "" || subcategory == "-"))
	{
			alert("Please select subcategory for "+category);
			return false;
	}
	if(fileSelected == "0" || fileSelected == "")
	{
		alert("Please select NTSL file of option");
		return false;
	}
	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}

	if(dataFile==""){

		alert("Please Upload File.");
		return false;
	}
	if(fileExten != 'mDLB')
	{
		alert("Upload mDLB file format");
		return false;
	}
	/*if(cycle == "0")
	{
		alert("Please Select cycle");
		return false;
	}*/

	return true;

}

function processRawFileUpload() {

	var frm = $('#uploadform');
	
	//var timePeriod = document.getElementById("timePeriod").value;
	
	var filename = document.getElementById("fileName").value;
	var category = document.getElementById("rectyp").value;
	/*var userfile = document.getElementById("dataFile1");
	alert("file is "+userfile);*/
	//var CSRFToken = $('[name=CSRFToken]').val();
	
	var  stSubCategory =document.getElementById("stSubCategory").value;
	//var  cycle =document.getElementById("cycle").value;
	var fileDate = document.getElementById("datepicker").value;
	/*var fileDate = document.getElementById("datepicker").value;
	alert("file date "+fileDate);*/
	var userfile = document.getElementById("dataFile1");
	
	var oMyForm = new FormData();
	
	
	oMyForm.append('file',userfile.files[0])
	oMyForm.append('fileName', filename);
	oMyForm.append('category', category);
	oMyForm.append('stSubCategory',stSubCategory);
	oMyForm.append('datepicker',fileDate);
//	oMyForm.append('cycle',cycle);
	
	if(ValidateRawData())  {
	$.ajax({
		type : "POST",
		url : "nfsFileUpload.do",
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
		success:function(response){

			debugger;
			hideLoader();

			alert (response); 


			document.getElementById("fileName").value="";
			// alert("2");
			document.getElementById("rectyp").value=category;
			// alert("3");
			document.getElementById("stSubCategory").value="-";
			// alert("4");
			//alert("5");
			document.getElementById("dataFile1").value="";
			// alert("8");
			//document.getElementById("cycle").value="0";
			document.getElementById("datepicker").value="";
			
		
			

		},error: function(){
			alert("Error Occured");

		},
		complete : function(data) {

			hideLoader();

		},
	});
	
	}
		
}
/*function processSettlement() {

		var frm = $('#reportform');
		
		var timePeriod = document.getElementById("timePeriod").value;
		
		var filename = document.getElementById("fileName").value;
		var category = document.getElementById("rectyp").value;
		
		if(timePeriod == "Daily")
		{	
			var datepicker = document.getElementById("dailypicker").value;
			if(filename == "NTSL-NFS")
			{
				var  cycle =document.getElementById("cycle").value;
			}
			else
			{
				var  cycle = "1";
			}
		}
		else
		{
			var datepicker = document.getElementById("monthpicker").value;
			cycle = "0";
		}
		
		
		var oMyForm = new FormData();
		
		
		oMyForm.append('fileName', filename);
		oMyForm.append('category', category);
		oMyForm.append('stSubCategory',"-");
		oMyForm.append('datepicker',datepicker);
		oMyForm.append('cycle',cycle);
		
		if(ValidateData())  {
		$.ajax({

			type:'POST',
			url :'NFSSettlementValidation.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			
			data:{fileDate:datepicker,stSubCategory:"-",cycle:cycle,filename:filename},
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
			
	}*/
	
	function processSettlement() {

		var frm = $('#reportform');
		
		var timePeriod = document.getElementById("timePeriod").value;
		
		var filename = document.getElementById("fileName").value;
		var category = document.getElementById("rectyp").value;
		
		if(timePeriod == "Daily")
		{	
			var datepicker = document.getElementById("dailypicker").value;
			if(filename == "NTSL-NFS")
			{
				var  cycle =document.getElementById("cycle").value;
			}
			else
			{
				var  cycle = "1";
			}
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
		oMyForm.append('stSubCategory',"-");
		oMyForm.append('datepicker',datepicker);
		oMyForm.append('cycle',cycle);
		
		if(ValidateData())  {
		$.ajax({

			type:'POST',
			url :'NFSSettlementValidation.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			
			data:{fileDate:datepicker,stSubCategory:"-",cycle:cycle,filename:filename},
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
	function downloadSettlement() {

		var frm = $('#reportform');
		
		var timePeriod = document.getElementById("timePeriod").value;
		
		var filename = document.getElementById("fileName").value;
		var category = document.getElementById("rectyp").value;
		
		if(timePeriod == "Daily")
		{	
			var datepicker = document.getElementById("dailypicker").value;
			if(filename == "NTSL-NFS")
			{
				var  cycle =document.getElementById("cycle").value;
			}
			else
			{
				var  cycle = "1";
			}
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
		oMyForm.append('stSubCategory',"-");
		oMyForm.append('datepicker',datepicker);
		oMyForm.append('cycle',cycle);
		
		if(ValidateData())  {
		$.ajax({
				type : "POST",
				url : "NFSSettProcessValidation.do",
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
		if(filename != "NTSL-NFS")
		{
			cycle = "1";
		}
		var oMyForm = new FormData();
		
		
		//oMyForm.append('file',userfile.files[0])
		oMyForm.append('fileName', filename);
		oMyForm.append('category', category);
		oMyForm.append('stSubCategory',stSubCategory);
		oMyForm.append('datepicker',datepicker);
		oMyForm.append('cycle',cycle);
		if(ValidateData())  {
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

function RollBackSettlement()
{
	var frm = $('#reportform');
	
	var timePeriod = document.getElementById("timePeriod").value;
	
	var filename = document.getElementById("fileName").value;
	var category = document.getElementById("rectyp").value;
	
	if(timePeriod == "Daily")
	{	
		var datepicker = document.getElementById("dailypicker").value;
		if(filename == "NTSL-NFS")
		{
			var  cycle =document.getElementById("cycle").value;
		}
		else
		{
			var  cycle = "1";
		}
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
	oMyForm.append('stSubCategory',"-");
	oMyForm.append('datepicker',datepicker);
	oMyForm.append('cycle',cycle);
	
	if(ValidateData())  {
	$.ajax({
			type : "POST",
			url : "NFSSettlementRollback.do",
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
					alert(response);

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