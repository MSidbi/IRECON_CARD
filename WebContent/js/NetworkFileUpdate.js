	function getSubCategory()
	{
		debugger;
		
		//alert("HELLO");
		var category  = document.getElementById("category").value;
	//	alert("category is "+category);
		if(category!="" && (category != "ONUS" && category != "AMEX" && category != "CARDTOCARD")) { 
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
				//alert("INSIDE ELSE");trfile
				document.getElementById("trsubcat").style.display="none";
				document.getElementById("trfile").style.display="none";
				//document.getElementById("stSubCategory").value="-";
				$('#trsubcat').val('-');
				$('#SubCat').val('-');
				
				
				//getFiles();
			}
	}
	function getFiles() {
	
		debugger;
		var category = document.getElementById("category").value;
	
		var subcat = document.getElementById("SubCat").value;
		
		if(category!="") { 
		$.ajax({
			
			 
			 type:'POST',
			 url :'getFiledetails.do',
			 data:{category:category,subcat:subcat},
			 success:function(response){
				 
				
				// response = $.parseJSON(response);
				 var length =response.Records.length;
				
				 var compareFile1 = document.getElementById("stFileName");
				// var compareFile2 = document.getElementById("manFile");
				 
				 var rowcount = compareFile1.childNodes.length;
					
					for(var j =1;j<=rowcount;j++ )
					{
						compareFile1.removeChild(compareFile1.lastChild);
						//compareFile2.removeChild(compareFile2.lastChild);
					}
				
				 var option1= document.createElement("option");
				 option1.value="0";
				 option1.text="--Select--";
				 var opt1= document.createElement("option");
				 opt1.value="0";
				 opt1.text="--Select--";
				 compareFile1.appendChild(option1);
				//compareFile2.appendChild(opt1);
				 
				 for(var i =0;i<length;i++ ) {
					
					 var option= document.createElement("option");
					  option.value = response.Records[i].inFileId;
					 option.text= response.Records[i].stFileName;
					 //alert("option.text"+option.text);
					 if(option.text != "SWITCH" && option.text != "CBS")
					{
						 
						// alert("option.text"+option.text);	
						 compareFile1.appendChild(option);
					}
				 }
				/* for(var i =0;i<length;i++ ) {
						
					 var option= document.createElement("option");
					  option.value = response.Records[i].inFileId;
					 option.text= response.Records[i].stFileName;
					// if(option.text.equals("CBS"))
					//	compareFile2.appendChild(option);
				 }*/
				
				 
				 document.getElementById("category").disabled="disabled";
				 document.getElementById("SubCat").disabled="disabled";
				// document.getElementById("stSubCategory").value = document.getElementById("SubCat").value;
				// document.getElementById("trmanfile").style.display="";
				 document.getElementById("trfile").style.display="";
				//trmanfile
				//trfile
				 
				 //displayContent();

			 },error: function(){
				
				 alert("Error Occured");
				 
			 },
			 
		 });
		}else {
			
			alert("Please Select Category.")
			
		}
	}
	
	
	function Process()
	{
		debugger;
		//alert("Inside process()");
		/*var subcateg = document.getElementById("SubCat").value;
		form.submit();*/

	//	alert("HELLO1");
		var category = document.getElementById("stCategory").value;
		//alert("category "+category);
		var subCat= document.getElementById("stsubCategory").value;
		//alert("SubCat "+subCat);
		var fileselected ="";
		//alert("fileselected "+fileselected);
		var datepicker = document.getElementById("datepicker").value;
		//alert("datepicker "+datepicker);
		//var path = document.getElementById("path").value;
		//alert("DONE");
		if(ValidateData())
		{
			
			$.ajax({

				type:'POST',
				url :'NetworkFileUpdate.do',
				async: true,
				beforeSend : function() {
					showLoader();
				},
				/*complete : function(data) {

					hideLoader();

				},*/

				data:{category:category,filedate:datepicker,subCat:subCat,fileselected:fileselected},
				success:function(response){

					alert(response);
					
				},error: function(){

					alert("Error Occured");

				},
				complete : function(data) {

					hideLoader();

				},
			});
			
			
			
			
			//showLoader();
			//form.submit();
			//alert("inside if");
			/*$.ajax({

				type:'POST',
				url :'DownloadReports.do',
				async: true,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {

					hideLoader();

				},

				data:{category:category,filedate:datepicker,subCat:subCat,path:path},
				success:function(response){

					alert(response);

				},error: function(){

					alert("Error Occured");

				},

			});*/
		}
		
		
		
	}
	function Delete()
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
		var fileselected = document.getElementById("stFileName").value;
		//alert("fileselected "+fileselected);
		var datepicker = document.getElementById("datepicker").value;
		//alert("datepicker "+datepicker);
		//var path = document.getElementById("path").value;
		//alert("DONE");
		if(ValidateData())
		{
			
			$.ajax({

				type:'POST',
				url :'DeleteNetworkFileEntry.do',
				async: true,
				beforeSend : function() {
					showLoader();
				},
				/*complete : function(data) {

					hideLoader();

				},*/

				data:{category:category,filedate:datepicker,subCat:subCat,fileselected:fileselected},
				success:function(response){

					alert(response);
					
				},error: function(){

					alert("Error Occured");

				},
				complete : function(data) {

					hideLoader();

				},
			});
			
			
			
			
			//showLoader();
			//form.submit();
			//alert("inside if");
			/*$.ajax({

				type:'POST',
				url :'DownloadReports.do',
				async: true,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {

					hideLoader();

				},

				data:{category:category,filedate:datepicker,subCat:subCat,path:path},
				success:function(response){

					alert(response);

				},error: function(){

					alert("Error Occured");

				},

			});*/
		}
		
		
		
	
	}
	function ValidateData()
	{
		debugger;
	//	alert("Inside ValidateData() ");
		
		var category = document.getElementById("stCategory").value;
		//alert("category "+category);
		var subcategory= document.getElementById("stsubCategory").value;
		
		var datepicker = document.getElementById("datepicker").value;
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
			if(category != "ONUS" && category != "AMEX" && category != "CARDTOCARD")
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
	
	function showLoader(location) {
		
		$("#Loader").show();
	}

	function hideLoader(location) {
		
		$("#Loader").hide();
	}