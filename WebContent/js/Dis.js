function getsearchdata1()
    {
	//alert("his");
	if(ValidateData()){
			
    	debugger;
    	//alert("Inside");
    	 $('#dispute').show();
    	$('#DisputeTableContainer').jtable({
    		title : 'Details' ,
    		paging : true, // Enable paging
    		pageSize : 10, // Set page size (default: 10)
    		sorting : true, // Enable sorting
    		scrollTable : true,
    		selecting : true, 
    		multiselect: true,
    		selectingCheckboxes : true,
    		/*editinline : {
    			enable : true
    		},*/
    		toolbarsearch : true,

    		actions : {
    			listAction : 'SearchDispute1.do'    			
    			//deleteAction: 'UpdateDispute1.do'    	               

    		},

    		fields : {
    			category : {
    				key: true,
    				title : 'Category'
    				//width : '20%',
    				//edit : true
    			},
    			subCategory : {
    				key: true,
    				title : 'SubCategory'
    				//width : '20%',
    				//edit : true
    			},
    			filename : {
    				key: true,
    				title : 'Filename'
    				//width : '20%',
    				//edit : true
    			},
    			tran_date : {
    				key: true,
    				title : 'TRAN DATE'
    				//width : '20%',
    				//edit : true
    			},
    			amount : {
    				key: true,
    				title : 'AMOUNT'
    				//width : '20%',
    				//edit : true

    			},
    			card_No : {
    				key: true,
    				title : 'CARD NO'
    				//width : '20%',
    				//edit : true

    			},
    			filedate : {
    				key: true,
    				title : 'FILEDATE',
    				//width : '20%'
    				displayFormat: 'dd/mm/yyyy'
    				//edit : true
    			},
    			approvalCode : {
    				key: true,
    				title : 'APPROVAL CODE/AUTHNUM'
    				//width : '20%',
    				//edit : true
    			},
    			
    			arn : {
    				key: true,
    				title : 'ARN/ISSUER'
    				//width : '20%',
    				//edit : true
    			},
    			termid : {
    				key: true,
    				title : 'TERM ID'
    				//width : '20%',
    				//edit : true
    			},
    			trace : {
    				key: true,
    				title : 'TRACE'
    				//width : '20%',
    				//edit : true
    			},
    			local_time : {
    				key: true,
    				title : 'LOCAL TIME'
    				//width : '20%',
    				//edit : true
    			},
    			foracid : {
    				key: true,
    				title : 'FORACID'
    				//width : '20%',
    				//edit : true
    			},
    			uniqIdentifier:{
    				key: true,
    				title : 'UNIQ IDENTIFIER'
    				//width : '20%',
    				//edit : true
    			}
    			
    			
    		},
    		
    		  selectionChanged: function () {
                  //Get all selected rows
                  var $selectedRows = $('#DisputeTableContainer').jtable('selectedRows');
                 //alert($selectedRows);
   
                
              }/*,
              rowInserted: function (event, data) {
                  if (data.record.Name.indexOf('Andrew') >= 0) {
                      $('#DisputeTableContainer').jtable('selectRows', data.row);
                  }
              }*/
          });   

    	$('#DisputeTableContainer').jtable('load', {
    		category : $('#category').val() != "" ? $('#category').val() : 0,
    		stSubCategory : $('#stSubCategory').val() != null ? $('#stSubCategory').val() : 0,
    		file_name : $('#file_name').val() != null ? $('#file_name').val() : 0,
    		//dcrs_remarks : $('#dcrs_remarks').val() != null ? $('#dcrs_remarks').val() : 0,
    		filedate : $('#filedate').val() != null ? $('#filedate').val() : 0

    	});
    	
    	
    	
    	  $('#ForceMatch').button().click(function () {
    		  debugger;
              var $selectedRows = $('#DisputeTableContainer').jtable('selectedRows');
              var count = $selectedRows.length;
              //alert("Count : "+count);
              var count1=0;
              if(count>0){
              $selectedRows.each(function () {
            	  debugger;
            	  var record = $(this).data('record');
                //  alert(record.card_No);                  
                  $.ajax({
             		 type:'POST',
             		 url :'UpdateDispute1.do',
             		beforeSend : function() {
         				showLoader();
         			},
         			complete : function(data) {
         				hideLoader();
         			},
             		//data:{category:record.category},
             		data:{category:record.category,subCategory:record.subCategory,filename:record.filename,tran_date:record.tran_date,amount:record.amount,
             			card_No:record.card_No,filedate:record.filedate,approvalCode:record.approvalCode,arn:record.arn,termid:record.termid,
             			trace:record.trace,local_time:record.local_time,foracid:record.foracid,uniqIdentifier:record.uniqIdentifier},
             		
             		 success:function(response){
             			 //alert(response);
             			 count1++;
             			 if(count1 == count){
             				//  alert("Count : "+count+"  count1 : "+count1);
             				 alert(count+" Of "+response+" Records Updated Successfully");
             				window.location.reload();
             			 }
             		 },error: function(){
             			 alert("Error Occured in updating force match");
             		 },
             		 
             	 });
                  
              });
              }else{
            	  alert("Please select atleast one checkbox.");
              }
              
            //  $('#DisputeTableContainer').jtable('deleteRows', $selectedRows);
              
           /*   $('#DisputeTableContainer').jtable('load', {
          		category : $('#category').val() != "" ? $('#category').val() : 0,
          		stSubCategory : $('#stSubCategory').val() != null ? $('#stSubCategory').val() : 0,
          		file_name : $('#file_name').val() != null ? $('#file_name').val() : 0,
          		//dcrs_remarks : $('#dcrs_remarks').val() != null ? $('#dcrs_remarks').val() : 0,
          		filedate : $('#filedate').val() != null ? $('#filedate').val() : 0

          	});
              */
            //  window.location.reload();  
          	
          });
    	}
    else{
    	return false;
    }
    }

function getSubCategory()
{
	debugger;
	
//	alert("HELLO");
	var category  = document.getElementById("category").value;
	//alert("HELLO=="+category);
	/*if(category != "MASTERCARD")
		{
		document.getElementById("dollar").style.display="none";
		}
	else{
		document.getElementById("dollar").style.display="";
	}*/
	//alert("category is "+category);
//	if(category!="" && (category != "ONUS" && category != "AMEX" && category != "CARDTOCARD")) { 
		document.getElementById("trsubcat").style.display="";
		$.ajax({
			 
			 type:'POST',
			 url :'getSubCategorydetails.do',
			 data:{category:category},
			 success:function(response){
				 
				
				 var length =response.Subcategories.length;
				
				
				 var compareFile1 = document.getElementById("stSubCategory");
				
		 
				 var rowcount = compareFile1.childNodes.length;
					
					for(var j =1;j<=rowcount;j++ )
					{
						compareFile1.removeChild(compareFile1.lastChild);
						//compareFile2.removeChild(compareFile2.lastChild);
					}
				
				 var option1= document.createElement("option");
				 option1.value="";
				 option1.text="--Select--";
				 var opt1= document.createElement("option");
				 opt1.value="";
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
				 //displayContent();
							 

			 },error: function(){
				
				 alert("Error Occured");
				 
			 },
			 
		 });
	/*	}else {
			//alert("INSIDE ELSE");
		//	document.getElementById("trsubcat").style.display="none";
			document.getElementById("stSubCategory").value="-";
			$('#trsubcat').val('-');
			$('#stSubCategory').val('-');
			//alert("check it "+document.getElementById("stSubCategory").value);
			var subcate = $("#stSubCategory").val("-");
			alert("subcate is.............. "+subcate);
			
			
		//	alert("document.getElementById().value "+document.getElementById("stSubCategory").value );
			//getFilesdata();
			
			//alert("Please Select Category.");
			
			var compareFile1 = document.getElementById("stSubCategory");
			//compareFile1.removeChild(compareFile1.firstChild);
			compareFile1.removeChild(compareFile1.lastChild);
			 var option1= document.createElement("option");
			 option1.value="-";
			 option1.text="-";
			 compareFile1.appendChild(option1);
			 
			 getFiles();
			
		}*/
	
}

function forceMatch(){
//	alert("In Force match..");
	var category = $('#category').val();
	var stSubCategory =  $('#stSubCategory').val();
	var file_name = $('#file_name').val();
	//var dcrs_remarks = $('#dcrs_remarks').val();
	var filedate = $('#filedate').val(); 
	
	 var insert = [];
	 if($('#checkAll').is(":checked")){
		
	 }else{
	
	$('.get_value').each(function(){
		 if($(this).is(":checked"))
		 {
		 alert($(this).val());
		 insert.push($(this).val());

		 }
		 }); 

	 }
	 
	 insert = insert.toString();
		 //alert(insert);
	 //if($('#checkAll').is(":checked") || $('#data1').is(":checked")){	 
     /*if($('#checkAll').is(":not(:checked)")|| $('#data1').is(":not(:checked)")){
    	 alert("Please select atleast one checkbox.");
     }else{*/
    	 if($('#checkAll').is(":checked") || $('#data1').is(":checked")){	  
	 $.ajax({
		 
		 type:'POST',
		 url :'updateForceMatchstatus.do',
		 data:{insert:insert,category:category,stSubCategory:stSubCategory,file_name:file_name,filedate:filedate},
		 success:function(response){
			// alert(response);
			 window.location.reload();
			/* $('#category').val('');
			 $('#stSubCategory').val('');
			 $('#file_name').val('');
			 $('#filedate').val('');
			// $('#dcrs_remarks').val('');
			 $('#searchdata').html('');*/
			 
		 },error: function(){
				
			 alert("Error Occured in updating force match");
			 
		 },
		 
	 });
	 }else{
		 alert("Please select atleast one checkbox.");
	 }
	/*var TableData = new Array();       
	
	  TableData = JSON.stringify(TableData);*/

}

function getFiles() {
	//alert("In Files");
	debugger;
	var category = document.getElementById("category").value;
	//var subcat = "-";
	var subcat = document.getElementById("stSubCategory").value;
	
	
	if(category!="") { 
	$.ajax({
		 
		 type:'POST',
		 url :'getFiledetails.do',
		 data:{category:category,subcat:subcat},
		 success:function(response){
			 
			
			// response = $.parseJSON(response);
			 var length =response.Records.length;
			
			 var compareFile1 = document.getElementById("file_name");
			 //var compareFile2 = document.getElementById("Man_file");
			 
			 var rowcount = compareFile1.childNodes.length;
				
				for(var j =1;j<=rowcount;j++ )
				{
					compareFile1.removeChild(compareFile1.lastChild);
					//compareFile2.removeChild(compareFile2.lastChild);
				}
			
			 var option1= document.createElement("option")
			 option1.value="0";
			 option1.text="--Select--";
			 var opt1= document.createElement("option")
			 opt1.value="0";
			 opt1.text="--Select--";
			 compareFile1.appendChild(option1);
			//compareFile2.appendChild(opt1)
			 
			 for(var i =0;i<length;i++ ) {
				
				 var option= document.createElement("option")
				 
				/* if(response.Records[i].stFileName != 'CBS'){
					 option.value = response.Records[i].stFileName;
					 option.text= response.Records[i].stFileName;
					 compareFile1.appendChild(option);
				 }else{
*/				 //alert(response.Records[i].inFileId);
					 option.value = response.Records[i].stFileName+'-'+response.Records[i].inFileId;
					 option.text= response.Records[i].stFileName;
					 compareFile1.appendChild(option);
			//	 }
			 }
			/* for(var i =0;i<length;i++ ) {
					
				 var option= document.createElement("option")
				  option.value = response.Records[i].inFileId;
				 option.text= response.Records[i].stFileName;
					compareFile2.appendChild(option)
			 }*/
			
			 //document.getElementById("trbtn").style.display="none";
			// document.getElementById("category").disabled="disabled";
			 //document.getElementById("SubCat").disabled="disabled";
			 //displayContent();
			
			 

		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });
	}else {
		
		
		alert("Please Select Category.")
		
	}
	

	
}

function showLoader() {
	//alert("showLoader");
	$("#Loader").show();
}

function hideLoader() {
	//alert("hideLoader");
	$("#Loader").hide();
}

function ValidateData()
{
	//alert("Inside ValidateData() ");
	var category = document.getElementById("category").value;
	var subcategory = document.getElementById("stSubCategory").value;
	var file_name = document.getElementById("file_name").value;
	var filedate = document.getElementById("filedate").value;
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
	}else
	if((subcategory == ""))
	{
		alert("Please select subcategory ");
		return false;
	}else
		if(file_name == "0")
		{
			alert("Please select file_name ");
			return false;
		}else
	if(filedate == "")
	{
		alert("Please select filedate ");
		return false;
	}	
	else{
		return true;
	}
	
	
}