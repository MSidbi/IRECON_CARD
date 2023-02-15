function getupldfiledetails(){
	
	
	window.open("../DebitCard_Recon/GetUplodedFile.do" , 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');

	
	
}

function Validate(event) {
	debugger;
	//alert("jjh");
    var regex = new RegExp("^[0-9-!@#$%*?.]");
    var key = String.fromCharCode(event.charCode ? event.which : event.charCode);
    if (!regex.test(key)) {
        event.preventDefault();
        alert("Only decimal values are allowed");
        return false;
    }
}

function Process() {
	var category = document.getElementById("category").value;
	var subCat= document.getElementById("stSubCategory").value;
	var datepicker = document.getElementById("datepicker").value;
	var glAccount = document.getElementById("glAccount").value;
	var form = document.getElementById("reportform");
	var userfile = document.getElementById("dataFile1");
	

	if(ValidateData())  {
			
			var oMyForm = new FormData();
			oMyForm.append('file',userfile.files[0]);
			oMyForm.append('category',category);
			oMyForm.append('stSubCategory',subCat);
			oMyForm.append('datepicker',datepicker);
			oMyForm.append('glAccount',glAccount);
			
			$.ajax({
				type : "POST",
				url : "GlFileUpload.do",
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

					alert(response);
					/*document.getElementById("stSubCategory").value="-";
					document.getElementById("glAccount").value="-";
					document.getElementById("datepicker").value="";*/
					
				},
				
				error : function(err) {
					hideLoader();
					alert("Error Occurred");
				}
			});
			
		}
	
}

function getSubCategory()
{
	debugger;
	
//	alert("HELLO");
	var category  = document.getElementById("category").value;
	//alert("category is "+category);
	if(category!="" && (category != "FISDOM")) { 
		document.getElementById("trsubcat").style.display="";
		$('#glAccount').val('-');
		$.ajax({
			 
			 type:'POST',
			 url :'getSubCategorydetails.do',
			 data:{category:category},
			 success:function(response){
				
				 var length =response.subcategories.length;
				
				
				 var compareFile1 = document.getElementById("stSubCategory");
				
		 
				 var rowcount = compareFile1.childNodes.length;
					
					for(var j =1;j<=rowcount;j++ )
					{
						compareFile1.removeChild(compareFile1.lastChild);
					}
				
				 var option1= document.createElement("option");
				 option1.value="-";
				 option1.text="--Select--";
				 var opt1= document.createElement("option");
				 opt1.value="-";
				 opt1.text="--Select--";
				 compareFile1.appendChild(option1);
				 
				 for(var i =0;i<length;i++ ) {
					
					 var option= document.createElement("option");
					  option.value = response.subcategories[i];
					 option.text= response.subcategories[i];
					 if(option.text != "SURCHARGE")
					 compareFile1.appendChild(option);
				 }
				 displayContent();
							 
					
			
			 },error: function(){
				
				 alert("Error Occured");
				 
			 },
			 
		 });
		}else {
			document.getElementById("trsubcat").style.display="none";
			//document.getElementById("stSubCategory").value="-";
			$('#trsubcat').val('-');
			$('#stSubCategory').val('-');
			getGlAccounts();
			//alert("check it "+document.getElementById("stSubCategory").value);
			/*var subcate = $("#stSubCategory").val("-");
			alert("subcate is.............. "+subcate);*/
			
			
		//	alert("document.getElementById().value "+document.getElementById("stSubCategory").value );
			//getFilesdata();
			
			//alert("Please Select Category.");
			
		}
	
}

function getGlAccounts()
{
	debugger;
	
//	alert("HELLO");
	var category  = document.getElementById("category").value;
	var subcategory  = document.getElementById("stSubCategory").value;
	//alert("category is "+category);

		$.ajax({
			 
			 type:'GET',
			 url :'getGlAccount.do',
			 data:{category:category,subcategory:subcategory},
			 success:function(res){
				
				 $('#' + 'glAccount').find('option').remove().end();
			$('#' + 'glAccount').append(
			'<option value="-" selected> --Select -- </option>');
			res.forEach(function(element, index){

			$('#' + 'glAccount').append(
			'<option value="' + element + '">'
			+ element+ '</option>');

});
							 
					
			
			 },error: function(){
				
				 alert("Error Occured");
				 
			 },
			 
		 });
		
	
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
	var category = document.getElementById("category").value;
	var subcategory = document.getElementById("stSubCategory").value;
	var datepicker = document.getElementById("datepicker").value;
	var glAccount = document.getElementById("glAccount").value;
	
	var dataFile= document.getElementById("dataFile1").value;
	var leng= dataFile.length - 4;
	var fileExten = dataFile.substring(leng,dataFile.length);
	
	debugger;
	if(category == "")
	{
		alert("Please select category ");
		return false;
	}
	if((subcategory == "" || subcategory == "-"))
	{
		if(category != "FISDOM")
		{
			alert("Please select subcategory for "+category);
			return false;
		}
		
	}
	if(datepicker == "")
	{
		alert("Please select date for processing");
		return false;
	}
	if(glAccount == "-")
	{
		alert("Please select gl Account");
		return false;
	}
	if(fileExten != '.rpt')
	{
		alert("Please upload rpt file");
		return false;
	}
	
	return true;
	
}



function checkNull(data){
	var val="NONE";
	if(data !=null || data !=undefined){
		val=data;
	}
	return val;
}