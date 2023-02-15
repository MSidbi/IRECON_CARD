function setfilename(e) {
	debugger;
	
	//|| e.value=="VISA"
	if(e.value=="SWITCH"){
		
		
		document.getElementById("trfileType").style.display='none';
		document.getElementById("trcategory").style.display='none';
		document.getElementById("trsubcat").style.display='none';
		
	}else if(e.value=="CBS") {
		
		/*document.getElementById("trfileType").style.display='';
		document.getElementById("trsubcat").style.display='';
		document.getElementById("trcategory").style.display=''; */
		document.getElementById("trfileType").style.display='none';
		document.getElementById("trsubcat").style.display='none';
		document.getElementById("trcategory").style.display='none';
	} else{
		
		document.getElementById("trfileType").style.display='';
		document.getElementById("trcategory").style.display='';
	
		
	}
	
	
}


function uploadFile(id,filename) {
	//alert("HELLO1");
	var rectyp = document.getElementById("rectyp").value;
	var subCat= document.getElementById("stSubCategory").value;
	var dollar_val= document.getElementById("dollar_val").value;
	var datepicker = document.getElementById("datepicker").value;
	var CSRFToken = $('[name=CSRFToken]').val();
	//alert("DONE");
	if(ValidateData())
	{
		
		$.ajax({

			type:'POST',
			url :'manualUploadFile.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			complete : function(data) {

				hideLoader();

			},

			data:{category:rectyp,filedate:datepicker,subCat:subCat,dollar_val:dollar_val,CSRFToken:$('[name=CSRFToken]').val()},
			success:function(response){

				alert(response);

			},error: function(){

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

function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}

//datepicker fileList dataFile
function getfiledetails(){
	
	
	window.open("../DebitCard_Recon/GetUplodedFile.do" , 'window', 'width=1000,height=500,location=no,toolbar=no,menubar=no,scrollbars=yes,resizable=no');

	
	
}
function validateupload(){
	
	
	
	var datepicker = document.getElementById("datepicker").value;
	//var filelist= document.getElementById("filename").value;
	var dataFile= document.getElementById("dataFile1").value;
	var category =document.getElementById("category").value;
	var msg="";
	
	if(datepicker==""){
		
		msg= msg+"Please Select Date for File.\n";
	}if(dataFile==""){
		
		msg=msg+"Please Upload File.\n";
	} 
		if(category==""){
			
			msg= msg+"Please select Cycles.\n "
		}
	
	
	if(msg!=""){
		
		alert(msg);
		return false;
	}else{
		
		document.getElementById("upload").disabled="disabled";
		 

		
		return true;
	}

	
}

function getSubCategory(e)
{
	debugger;
	
//	alert("HELLO");
	//alert("category is "+category);
	var filename = document.getElementById("filename").value;
	var category = e.value;
	if(category!="" && (category != "ONUS" && category != "AMEX" && category != "CARDTOCARD" && category != "VISA" && filename != "REV_REPORT" ) ) { 
		document.getElementById("trsubcat").style.display="";
		$.ajax({
			 
			 type:'POST',
			 url :'getSubCategorydetails.do',
			 data:{category:category},
			 success:function(response){
				 
				
				//var length =response.Subcategories.length;
				 
				 var length =response.subcategories.length;
				
				
				 var compareFile1 = document.getElementById("stSubCategory");
				
		 
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
					  option.value = response.subcategories[i];
					 option.text= response.subcategories[i];
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
			$('#stSubCategory').val('-');
			//alert("check it "+document.getElementById("stSubCategory").value);
			/*var subcate = $("#stSubCategory").val("-");
			alert("subcate is.............. "+subcate);*/
			
			
		//	alert("document.getElementById().value "+document.getElementById("stSubCategory").value );
			//getFilesdata();
			
			//alert("Please Select Category.");
			
		}
	
}




var uploaddate;
function processFileUpload() {
	debugger;

	var frm = $('#CashnetGLFileUpload1');
	
	//var filename = document.getElementById("filename").value;
	//var fileType = document.getElementById("cycle").value;
	var category = document.getElementById("category").value;
	var userfile = document.getElementById("dataFile1");
	var CSRFToken = $('[name=CSRFToken]').val();
	
	//var  stSubCategory =document.getElementById("stSubCategory").value;
	var fileDate = document.getElementById("datepicker").value;
	var oMyForm = new FormData();
	/*if(typeof FormData == "undefined"){
		var data = [];
		data.push('data', JSON.stringify(inputData));
		}
		else{
		var data = new FormData();
		    data.append('data', JSON.stringify(inputData));
		}*/
	
	oMyForm.append('file',userfile.files[0])
	//oMyForm.append('filename', filename);
	//oMyForm.append('fileType', fileType);
	oMyForm.append('category', category);
	//oMyForm.append('stSubCategory',stSubCategory);
	oMyForm.append('fileDate',fileDate);
	oMyForm.append('CSRFToken',CSRFToken);
	//"file=" + files[0] + "&filename=" + filename+"&fileType=" + fileType + "&category=" + category+"&fileDate=" + fileDate + "&stSubCategory=" + stSubCategory 
	if(validateupload())  { 
	$.ajax({
			type : "POST",
			//url : "CashnetGLFileUpload1.do",
			url: "CashnetGLFileUpload1.do",
			enctype:"multipart/form-data",
			data :oMyForm ,
			
			processData : false,
			contentType : false,
			//type : 'POST',
			beforeSend : function() {
				showLoader();
			},
			complete : function(data) {
				document.getElementById("CashnetGLFileUpload1").disabled="";
				hideLoader();

			},
			success : function(response) {
				debugger;
			hideLoader();
			
					alert(response);

			}
					});
	
	}
		
				}
	