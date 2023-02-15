/*function processFileDownload(){
	var form = document.getElementById("nfsFeeGstform");
	var network = document.getElementById("network").value;
	var fromDate = document.getElementById("fromDate").value;
	var toDate = document.getElementById("toDate").value;
	
	var oMyForm = new FormData();
		
		
		//oMyForm.append('file',userfile.files[0])
		oMyForm.append('network', network);
		oMyForm.append('fromDate', fromDate);
		oMyForm.append('toDate',toDate);
	
	if(ValidateData())
	{
	 //form.submit();

		$.ajax({

			type:'POST',
			url :'ValidateNFSFeeReport.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			
			data:oMyForm,
			success:function(response){
				
				if(response == "success")
				{
					alert("Reports are getting downloaded. Please Wait");
					document.getElementById("nfsFeeGstform").submit();
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
function processFileDownload(){
	

		var network = document.getElementById("network").value;
	var fromDate = document.getElementById("fromDate").value;
	var toDate = document.getElementById("toDate").value;
		
		var oMyForm = new FormData();
		oMyForm.append('network', network);
		oMyForm.append('fromDate', fromDate);
		oMyForm.append('toDate',toDate);
		
		if(ValidateData())  {
		$.ajax({

			type:'POST',
			url :'ValidateNFSFeeReport.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			
			data:{network:network,fromDate:fromDate,toDate:toDate},
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

function ValidateData()
	{
		var network = document.getElementById("network").value;
		var fromDate = document.getElementById("fromDate").value;
		var toDate = document.getElementById("toDate").value;
	
		debugger;
		if(network == "")
		{
			alert("Please select network ");
			return false;
		}
		if((fromDate == "" ))
		{
				alert("Please select fromDate ");
				return false;
			
		}
		if(toDate == "")
		{
			alert("Please select toDate");
			return false;
		}
		
		return true;
		
	}


function ValidateDataSuspctTxn()
{
	var network = document.getElementById("network").value;
	var fromDate = document.getElementById("date").value;
	

	if(network == "")
	{
		alert("Please select network ");
		return false;
	}
	if((fromDate == "" ))
	{
			alert("Please select date ");
			return false;
		
	}
	
	return true;
	
}
function fileDownloadSuspectTxn(){
	var network = document.getElementById("network").value;
	var date = document.getElementById("date").value;
	var form = document.getElementById("nfsSuspectTxnform");
	var data="network="+network+"&"+"date="+date;
	if(ValidateDataSuspctTxn()){
	$.ajax({
		type:'POST',
		url :'checkIfSuspectTxnProcess.do',
		async: true,
		beforeSend :function(){
			showLoader();
		},
		data:data,
		
		success :function(response){
			if(response == "success"){
				form.submit();
			}else {
				alert(response);
			}
		},error: function(){
			alert("Error Occured");
		},
		complete : function(){
			hideLoader();
		}
		
	})
	}
}

function processNFSSuspectTxn(){
	var network = document.getElementById("network").value;
	var date = document.getElementById("date").value;
	
	var data="network="+network+"&"+"date="+date;
	if(ValidateDataSuspctTxn()){
	$.ajax({
		type:'POST',
		url :'processNFSSuspectTxn.do',
		async: true,
		beforeSend :function(){
			showLoader();
		},
		data:data,
		
		success :function(response){
			alert(response);
		},error: function(){
			alert("Error Occured");
		},
		complete : function(){
			hideLoader();
		}
		
	})
	}
}
function showLoader() {
	
	$("#Loader").show();
}

function hideLoader() {
	
	$("#Loader").hide();
}