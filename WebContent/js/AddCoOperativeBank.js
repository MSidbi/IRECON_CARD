function Process() {
	
	var bankName = document.getElementById("BankName").value;
	alert("Bank Name is "+bankName);
	var accNumber = document.getElementById("AccNo").value;
	alert("account NUmber is "+accNumber);
	
	if(ValidateData())
	{
		alert("After Validation");
	
		$.ajax({

			type:'POST',
			url :'addCooperativeBank.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			
			data:{bankName:bankName,accNumber:accNumber},
			success:function(response){

				alert(response);
				/*if(response == "success")
				{
					alert("Reports are getting downloaded. Please Wait");
					document.getElementById("reportform").submit();
				}
				else
				{
					alert(response);
				}*/
				

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
	var bankName = document.getElementById("BankName").value;
	alert("Bank Name is "+bankName);
	var accNumber = document.getElementById("AccNo").value;
	alert("account NUmber is "+accNumber);
	
	
	if(bankName == "")
	{
		alert("Please enter Bank Name");
		return false;
	}
	if(accNumber == "")
	{
		alert("Please enter Account Number");
		return false;
	}
	return true;
}

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
function validateSolData()
{
	var solId = document.getElementById("solId").value;
	var incomePH = document.getElementById("incomePH").value ;
	var incomeAccNo = document.getElementById("incomeAccNo").value;
	var nodalSolId = document.getElementById("nodalSolId").value;
	var nodalAccNo = document.getElementById("nodalAccNo").value ;
	var nodalPh = document.getElementById("nodalPh").value;
	var state = document.getElementById("state").value; 
	var gstin = document.getElementById("gstin").value;
	
	if(solId == "")
	{
		alert("Please Enter SolId");
		return false;
	}
	if(state == "")
	{
		alert("Please Enter State");
		return false;
	}
	if(gstin == "")
	{
		alert("Please Enter GstIn");
		return false;
	}
	if(gstin == "")
	{
		alert("Please Enter GstIn");
		return false;
	}
	
	return true;
	
}

function ProcessSolAdd()
{
	var solId = document.getElementById("solId").value;
	var incomePH = document.getElementById("incomePH").value ;
	var incomeAccNo = document.getElementById("incomeAccNo").value;
	var nodalSolId = document.getElementById("nodalSolId").value;
	var nodalAccNo = document.getElementById("nodalAccNo").value ;
	var nodalPh = document.getElementById("nodalPh").value;
	var state = document.getElementById("state").value; 
	var gstin = document.getElementById("gstin").value;
	var oMyForm = new FormData();
	/*if(typeof FormData == "undefined"){
		var data = [];
		data.push('data', JSON.stringify(inputData));
		}
		else{
		var data = new FormData();
		    data.append('data', JSON.stringify(inputData));
		}*/
	
	oMyForm.append('solId',solId)
	oMyForm.append('incomePH', incomePH);
	oMyForm.append('incomeAccNo', incomeAccNo);
	oMyForm.append('nodalSolId', nodalSolId);
	oMyForm.append('nodalAccNo',nodalAccNo);
	oMyForm.append('nodalPh',nodalPh);
	oMyForm.append('state',state);
	oMyForm.append('gstin',gstin);
	if(validateSolData)
	{
		$.ajax({

			type:'POST',
			url :'saveNodalDetails.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			data:{solId:solId,incomePH:incomePH,incomeAccNo:incomeAccNo,nodalSolId:nodalSolId,nodalAccNo:nodalAccNo,nodalPh:nodalPh,state:state,gstin:gstin},
			success:function(response){
				alert(response);
				document.getElementById("solId").value = "";
				document.getElementById("incomePH").value = "";
				document.getElementById("incomeAccNo").value = "";
				document.getElementById("nodalSolId").value = "";
				document.getElementById("nodalAccNo").value = "";
				document.getElementById("nodalPh").value = "";
				document.getElementById("state").value = ""; 
				document.getElementById("gstin").value = "";
			},error: function(){

				alert("Error Occured");

			},
			complete : function(data) {

				hideLoader();

			},
		});
		
	}
}

function getData(e)
{
	var solId = document.getElementById("solId").value;
	var incomePH = document.getElementById("incomePH").value ;
	document.getElementById("incomeAccNo").value = solId+incomePH;
}

function getNodalSolId(e)
{
	var state = e.value
	$.ajax({

		type:'POST',
		url :'getNodalDetails.do',
		async: true,
		beforeSend : function() {
			showLoader();
		},
		
		data:{state:state},
		success:function(response){
			console.log(response);
			 var length =response.length;
			 if(length > 0)
				 {
			 for(var i = 0 ; i<length ; i++)
			{
				if(i == 0)
				{
					document.getElementById("nodalSolId").value = response[i];
					document.getElementById("nodalAccNo").value = response[i]+'36000010011';
				}
				else
				{
					document.getElementById("nodalPh").value = response[i];
				}
			}
		}
		else
			{
			alert("Issue while extracting Nodal Sol and PH Account Number");
			document.getElementById("nodalSolId").value = "";
			document.getElementById("nodalAccNo").value = "";
			document.getElementById("nodalPh").value = "";
			}
			/*if(response == "success")
			{
				alert("Reports are getting downloaded. Please Wait");
				document.getElementById("reportform").submit();
			}
			else
			{
				alert(response);
			}*/
			

		},error: function(){

			alert("Error Occured");

		},
		complete : function(data) {

			hideLoader();

		},
	});
}
