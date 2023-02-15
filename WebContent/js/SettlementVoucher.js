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
		var fileDate = document.getElementById("dailypicker").value;
	debugger;
	if(category == "")
	{
		alert("Please select category ");
		return false;
	}
	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}
	//if(timePeriod == "Daily" && cycle == "0")
	return true;
	
}

function processSettVoucher() {

		var frm = $('#reportform');
		var category = document.getElementById("rectyp").value;
		var datepicker = document.getElementById("dailypicker").value;
			
		var oMyForm = new FormData();
		oMyForm.append('category', category);
		oMyForm.append('datepicker',datepicker);
		if(ValidateData())  {
		$.ajax({
				type : "POST",
				url : "NFSSettlementProcess.do",
				enctype:"multipart/form-data",
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
	
	function DownloadSettVoucher() {

		var frm = $('#reportform');
		var category = document.getElementById("rectyp").value;
		var datepicker = document.getElementById("dailypicker").value;
			
		var oMyForm = new FormData();
		oMyForm.append('category', category);
		oMyForm.append('datepicker',datepicker);
		if(ValidateData())  {
		$.ajax({
				type : "POST",
				url : "NFSSettlementTTUMValidation.do",
				enctype:"multipart/form-data",
				data :oMyForm ,

				processData : false,
				contentType : false,
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
				}
			});				
		
		}
			
	}

function processCoopTTUM() {

	var frm = $('#reportform');
	
	
	var filename = document.getElementById("fileName").value;
	var category = document.getElementById("rectyp").value;
		var datepicker = document.getElementById("dailypicker").value;
		if(filename == "NTSL-NFS")
		{
			var  cycle =document.getElementById("cycle").value;
		}
		else
		{
			var  cycle = "1";
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
		url :'NFSCoopValidation.do',
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
		
}

function ValidateDiffData()
{
	var category = document.getElementById("rectyp").value;
	var rectified_amt  = document.getElementById("rectAmt").value;
	var sign = document.getElementById("sign").value;
	var fileDate = document.getElementById("dailypicker").value;
	var  cycle =document.getElementById("cycle").value;
	debugger;
	if(category == "")
	{
		alert("Please select category ");
		return false;
	}
	if(fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}
	if(cycle == "0")
	{
		alert("Please Select cycle");
		return false;
	}
	if(rectified_amt >= 1)
	{
		alert("Amount cannot be greater than 1");
		return false;
	}
	if(sign == "0")
	{
			alert("Please select + or - from drop down");
			return false;
	}
	

	return true;
	
}

function Rectify()
{
	var frm = $('#reportform');
	var category = document.getElementById("rectyp").value;
	var rectified_amt  = document.getElementById("rectAmt").value;
	var sign = document.getElementById("sign").value;
	rectified_amt = sign+rectified_amt;

	var datepicker = document.getElementById("dailypicker").value;
	var  cycle =document.getElementById("cycle").value;
		
	var oMyForm = new FormData();
	
	
	//oMyForm.append('file',userfile.files[0])
	oMyForm.append('category', category);
	oMyForm.append('stSubCategory',"-");
	oMyForm.append('datepicker',datepicker);
	oMyForm.append('cycle',cycle);
	oMyForm.append('rectAmt',rectified_amt);
	if(ValidateDiffData())  {
	$.ajax({

		type:'POST',
		url :'SettlementRectify.do',
		async: true,
		beforeSend : function() {
			showLoader();
		},
		
		data:{category:category,datepicker:datepicker,stSubCategory:"-",cycle:cycle,rectAmt:rectified_amt},
		success:function(response){
			debugger;
			hideLoader();

			alert (response); 
			document.getElementById("rectyp").value=category;
			document.getElementById("cycle").value="0";
			document.getElementById("datepicker").value="";
			document.getElementById("sign").value="0";
			document.getElementById("rectAmt").value="";
		},error: function(){
			alert("Error Occured");

		},
		complete : function(data) {

			hideLoader();

		},
	});
	
	}
		

	
}

function NFSVoucRollBack()
{

	var frm = $('#reportform');
	var category = document.getElementById("rectyp").value;
	var datepicker = document.getElementById("dailypicker").value;
		
	var oMyForm = new FormData();
	oMyForm.append('category', category);
	oMyForm.append('datepicker',datepicker);
	if(ValidateData())  {
	$.ajax({
			type : "POST",
			url : "NFSVoucherRollBack.do",
			enctype:"multipart/form-data",
			data :oMyForm ,

			processData : false,
			contentType : false,
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
			}
		});				
	
	}
		
}
