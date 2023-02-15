function setFilename(e){
		
	//document.getElementById("stFileSelected").value = e.options[e.selectedIndex].text;
	
	
}

function chngsubcat(e){
	document.getElementById("Category").value =e.value;
	if(e.value=="RUPAY" || e.value == "VISA"){
			
			document.getElementById("trsubcat").style.display="";
			
		}else{
			
			document.getElementById("trsubcat").style.display="none";
			document.getElementById("SubCat").value="-";
			getFiles();
			
		}
		
		
		
	}

function setmanFilename(e){
	
	//document.getElementById("manFile").value = e.options[e.selectedIndex].text;
	
}

//trmanfile
//trfile
function getFiles() {
	
	var category = document.getElementById("Category").value;
	var subcat = document.getElementById("SubCat").value;
	
	if(category!="") { 
	$.ajax({
		
		 
		 type:'POST',
		 url :'getFiledetails.do',
		 data:{category:category,subcat:subcat},
		 success:function(response){
			 
			
			// response = $.parseJSON(response);
			 var length =response.Records.length;
			
			 var compareFile1 = document.getElementById("stFileSelected");
			 var compareFile2 = document.getElementById("manFile");
			 
			 var rowcount = compareFile1.childNodes.length;
				
				for(var j =1;j<=rowcount;j++ )
				{
					compareFile1.removeChild(compareFile1.lastChild);
					compareFile2.removeChild(compareFile2.lastChild);
				}
			
			 var option1= document.createElement("option");
			 option1.value="0";
			 option1.text="--Select--";
			 var opt1= document.createElement("option");
			 opt1.value="0";
			 opt1.text="--Select--";
			 compareFile1.appendChild(option1);
			compareFile2.appendChild(opt1);
			 
			 for(var i =0;i<length;i++ ) {
				
				 var option= document.createElement("option");
				  option.value = response.Records[i].inFileId;
				 option.text= response.Records[i].stFileName;
				 //if(option.text.equals("SWITCH"))
				 compareFile1.appendChild(option);
			 }
			 for(var i =0;i<length;i++ ) {
					
				 var option= document.createElement("option");
				  option.value = response.Records[i].inFileId;
				 option.text= response.Records[i].stFileName;
				// if(option.text.equals("CBS"))
					compareFile2.appendChild(option);
			 }
			
			 
			 document.getElementById("Category").disabled="disabled";
			 document.getElementById("SubCat").disabled="disabled";
			// document.getElementById("stSubCategory").value = document.getElementById("SubCat").value;
			 document.getElementById("trmanfile").style.display="";
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





/*function process(){

	var stManualFile = document.getElementById("manFile").value;
	var stFileSelected = document.getElementById("stFileSelected").value;
	var msg="";
	
	if(stManualFile=="") {
		
		msg= msg+"Please Select File to Process.\n";
	
	}if(stFileSelected=="") {
		
		msg=msg+"Please Select Manual File to Process.\n";
		
	}if(msg!=""){
		
		alert(msg);
		return false;
		
	}else {	
		
		document.getElementById("Process").disabled="disabled";
		document.getElementById("Loader").style.display="";
	     return true;

	}
}*/

function Process() {
	//alert("Process()") 
	var cate = document.getElementById("Category").value;
	var subCat= document.getElementById("SubCat").value;
	var datepicker = document.getElementById("datepicker").value;
	var selectedFile = document.getElementById("stFileSelected").value;
	var manualFile = document.getElementById("manFile").value;
	//alert("Process(") ;
	$.ajax({
		 
		 type:'POST',
		 url :'manualFileProcess.do',
		 async: true,
		 beforeSend : function() {
				showLoader();
			},
		complete : function(data) {
				
				hideLoader();
				
			},
			
		// data:{category:rectyp,filedate:datepicker,subCat:subCat},
		  data:{cat:cate,Subcat:subCat,Filedate:datepicker,selectedFile:selectedFile,ManFile:manualFile},
		 success:function(response){
			 
			alert(response);
			
		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });
	
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

function showLoader(location) {
	//alert("showLoader");
	$("#Loader").show();
}

function hideLoader(location) {
	//alert("hideLoader");
	$("#Loader").hide();
}