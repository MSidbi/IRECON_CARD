function processRupayAdjustUpload() {

	var frm = $('#uploadform');
	
	var fileDate = document.getElementById("datepicker").value;
	var  cycle =document.getElementById("cycle").value;
	var  network =document.getElementById("network").value;
	var userfile = document.getElementById("dataFile1");
	var subcate = document.getElementById("subcate").value;
	
	
	var oMyForm = new FormData();
	oMyForm.append('file',userfile.files[0])
	oMyForm.append('fileDate', fileDate);
	oMyForm.append('cycle', cycle);
	oMyForm.append('network', network);
	oMyForm.append('subcate', subcate);
	
	if(validateData())  {
	$.ajax({
		type : "POST",
		url : "rupayAdjustmentFileUpload.do",
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

		},error: function(){
			alert("Error Occured");

		},
		complete : function(data) {

			hideLoader();

		},
	});
	
	}	
	}
function validateData(){
debugger;
	var fileDate = document.getElementById("datepicker").value;
	var file = document.getElementById("dataFile1").value;
	var cycle = document.getElementById("cycle").value;
	var leng= file.length - 4;
	var fileExten = file.substring(leng,file.length);
	var  network =document.getElementById("network").value;
	var subcate = document.getElementById("subcate").value;
	
	if(fileDate == ""){
		alert("Kindly select date");
		return false;
	}
	
	if(file == ""){
		alert("Kindly select file");
		return false;
	}
	if(cycle == "0")
	{
		alert("Please select cycle");
		return false;
	}
	if(fileExten != '.csv')
	{
		alert("Please upload csv File");
		return false;
	}
	if(network == "")
	{
		alert("Please select network");
		return false;
	}
	if(network == 'RUPAY' && subcate == "0")
	{
		alert("Please select Subcategory");
		return false;
	}
	return true;
	
}
function showLoader(location) {
	
	$("#Loader").show();
}
function hideLoader(location) {
	
	$("#Loader").hide();
}

function getFields(e)
{
	var network = document.getElementById("network").value;
	
	if(network == "RUPAY")
	{
		document.getElementById("subCat").style.display = '';
	}
	else
	{
		document.getElementById("subCat").style.display = 'none';
	}
	
}