function Process() {
	var rectyp = document.getElementById("rectyp").value;
	var subCat= document.getElementById("stSubCategory").value;
	var datepicker = document.getElementById("datepicker").value;
	var CSRFToken = $('[name=CSRFToken]').val();
	
	if(ValidateData())
	{
		
		$.ajax({

			type:'POST',
			url :'manulRollBack.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			complete : function(data) {

				hideLoader();

			},

			data:{category:rectyp,filedate:datepicker,subCat:subCat,CSRFToken:CSRFToken},
			success:function(response){

				alert(response);
				 document.getElementById("stSubCategory").value="-";
				// document.getElementById("dollar_val").value="";
				 document.getElementById("datepicker").value="";
				

			},error: function(){

				alert("Inside error");
				alert("Error Occured");

			},

		});
	}
	/*else
	{
		alert("Enter complete details");
	}*/
	
	/*setInterval(function(){$.ajax({
		 
		 type:'POST',
		 url :'CheckStatus.do',
		 data:{category:rectyp,filedate:datepicker,subcat:subCat},
		 success:function(response){
			 
			 var tbl = document.getElementById("processTbl");
			 document.getElementById("processTbl").style.display="";
			 
			 
			 var lngth =tbl.children.length;
			
			 if(lngth>0) {
			 for(var i= 0;i<lngth;i++ ) {
				 
				 tbl.removeChild(tbl.lastChild);
			 }
			 }
			 
			 var $row = $('<tr id="row1" class="even" />');
			
			 	$row.append('<td align="center" class="lD"><label>Category</label></td>');
				$row.append('<td align="center" class="lD"><label>Upload_FLAG</label></td>');
				$row.append('<td align="center" class="lD"><label>Filter_FLAG</label></td>');
				$row.append('<td align="center" class="lD"><label>Knockoff_FLAG</label></td>');
				$row.append('<td align="center" class="lD"><label>Compare_FLAG</label></td>');
				
				 $('#processTbl').append($row);
				 
				
				  var $row = $('<tr id="row2" class="even" />');
				  $row.append('<td align="center" class="lD"><label>'+rectyp+'</label></td>');				  
				  $row.append('<td align="center" class="lD"><label>'+response.beanRecords.upload_Flag+'</label></td>');
					$row.append('<td align="center" class="lD"><label>'+response.beanRecords.filter_Flag+'</label></td>');
					$row.append('<td align="center" class="lD"><label>'+response.beanRecords.knockoff_Flag+'</label></td>');
					$row.append('<td align="center" class="lD"><label>'+response.beanRecords.comapre_Flag+'</label></td>');
				  
				  $('#processTbl').append($row);
				 
			 
		
			
		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });}, 10000);*/
	
	
	
	
	
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
function ValidateData()
{
	//alert("Inside ValidateData() ");
	var category = document.getElementById("rectyp").value;
	var subcategory = document.getElementById("stSubCategory").value;
	var datepicker = document.getElementById("datepicker").value;
	//alert("subcategory "+subcategory);
	/*alert("done");
	alert("category "+category);
	alert("subcategory "+subcategory);
	alert("date "+datepicker);*/
	
	debugger;
	if(category == "")
	{
		alert("Please select category ");
		return false;
	}
	if((subcategory == "" || subcategory == "-"))
	{
		if(category != "ONUS" && category != "AMEX" && category != "CARDTOCARD" && category != "WCC" && category != "FISDOM")
		{
			alert("Please select subcategory for "+category);
			return false;
		}
		/*else
		{
			document.getElementById("stSubCategory").value = "-";
			alert("1. "+document.getElementById("stSubCategory").value);
			var subcate = document.getElementById("stSubCategory").value;
			alert("check subcate "+subcate);
		}*/
		
	}
	if(datepicker == "")
	{
		alert("Please select date for processing");
		return false;
	}
	
	return true;
	
}