	function getSubCategory()
	{
		debugger;
		
		//alert("HELLO");
		var category  = document.getElementById("category").value;
		//alert("category is "+category);
		if(category!="" && (category != "ONUS" && category != "AMEX" && category != "WCC")) { 
			document.getElementById("trsubcat").style.display="";
			$.ajax({
				 
				 type:'POST',
				 url :'getSubCategorydetails.do',
				 data:{category:category},
				 success:function(response){
					 
					
					 var length =response.Subcategories.length;
					
					
					 var compareFile1 = document.getElementById("SubCat");
					
			 
					 var rowcount = compareFile1.childNodes.length;
						
						for(var j =1;j<=rowcount;j++ )
						{
							compareFile1.removeChild(compareFile1.lastChild);
							//compareFile2.removeChild(compareFile2.lastChild);
						}
					
					 var option1= document.createElement("option");
					 option1.value="-";
					 option1.text="--Select--";
					 var opt1= document.createElement("option");
					 opt1.value="-";
					 opt1.text="--Select--";
					 compareFile1.appendChild(option1);
					//compareFile2.appendChild(opt1)
					 
					 for(var i =0;i<length;i++ ) {
						
						 var option= document.createElement("option");
						  option.value = response.Subcategories[i];
						 option.text= response.Subcategories[i];
						 //alert("check this "+option.text);
						 if(option.text != "SURCHARGE")
						 compareFile1.appendChild(option);
					 }
					/* for(var i =0;i<length;i++ ) {
							
						 var option= document.createElement("option")
						  option.value = response.Records[i].inFileId;
						 option.text= response.Records[i].stFileName;
							compareFile2.appendChild(option)
					 }*/
					
					 //document.getElementById("trbtn").style.display="none";
					// document.getElementById("stCategorynew").disabled="disabled";
					 //document.getElementById("SubCat").disabled="disabled";
					// displayContent();
								 

				 },error: function(){
					
					 alert("Error Occured");
					 
				 },
				 
			 });
			
			}else {
				//alert("INSIDE ELSE");
				document.getElementById("trsubcat").style.display="none";
				//document.getElementById("stSubCategory").value="-";
				$('#trsubcat').val('-');
				$('#SubCat').val('-');
				
				
				//getFiles();
			}
	}
	
	
	function ViewData()
	{
		debugger;
		alert("Inside ViewData()");
		/*var subcateg = document.getElementById("SubCat").value;
		form.submit();*/

	//	alert("HELLO1");
		var category = document.getElementById("category").value;
		alert("category "+category);
		var subCat= document.getElementById("SubCat").value;
		alert("SubCat "+subCat);
		/*var fileselected = document.getElementById("stFileName").value;
		alert("fileselected "+fileselected);*/
		/*var datepicker = document.getElementById("datepicker").value;
		alert("datepicker "+datepicker);*/
		//var path = document.getElementById("path").value;
		//alert("DONE");
		if(ValidateData())
		{
			alert("Done"+category);
			alert("subcategory "+subCat);
		/*	window.open("../DebitCard_Recon/ViewReconStatus.do?category = "+category+"&subcate = "+subCat, 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');*/
			window.open("../DebitCard_Recon/ViewReconStatus.do?category="+category+"&subcate="+subCat, 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');
			
			
			
		}
		
		
		
	}
	function DownloadData()
	{

		debugger;
		//alert("Inside process()");
		/*var subcateg = document.getElementById("SubCat").value;
		form.submit();*/

	//	alert("HELLO1");
		var category = document.getElementById("category").value;
		//alert("category "+category);
		var subCat= document.getElementById("SubCat").value;
		//alert("SubCat "+subCat);
		//var fileselected = document.getElementById("stFileName").value;
		//alert("fileselected "+fileselected);
		/*var datepicker = document.getElementById("datepicker").value;
		alert("datepicker "+datepicker);*/
		//var path = document.getElementById("path").value;
		//alert("DONE");
		if(ValidateData())
		{
			form.submit();
			
			/*$.ajax({

				type:'POST',
				url :'DeleteNetworkFileEntry.do',
				async: true,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {

					hideLoader();

				},

				data:{category:category,filedate:datepicker,subCat:subCat,fileselected:fileselected},
				success:function(response){

					alert(response);
					
				},error: function(){

					alert("Error Occured");

				},
				complete : function(data) {

					hideLoader();

				},
			});*/
		
		}
		
		
		
	
	}
	function ValidateData()
	{
		alert("Inside ValidateData() ");
		var category = document.getElementById("category").value;
		var subcategory = document.getElementById("SubCat").value;
		alert("subcategory "+subcategory); 	
	//	var datepicker = document.getElementById("datepicker").value;
	/*	//alert("subcategory "+subcategory);
		alert("done");
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
			if(category != "ONUS" && category != "AMEX")
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
		/*if(datepicker == "")
		{
			alert("Please select date for processing");
			return false;
		}*/
		
		return true;
		
	}
	
	function showLoader(location) {
		
		$("#Loader").show();
	}

	function hideLoader(location) {
		
		$("#Loader").hide();
	}