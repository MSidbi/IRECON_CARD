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

function setFileParam() {
	
	
	var rectyp = document.getElementById("rectyp").value
	var filedate = document.getElementById("datepicker").value;
	var tblFiledtl = document.getElementById("tblFiledtl");
	
	if(rectyp !="" && filedate!= ""){
		
		document.getElementById("lbltype").innerHTML = "Selected Recon Type: <b>" + rectyp+"</b>";
		document.getElementById("lbldate").innerHTML= "Selected File Date : <b>" +filedate+"</b>";
		
		  
		
	$.ajax({
		 
		 type:'POST',
		 url :'Filedetails.do',
		 data:{category:'ONUS'},
		 success:function(response){
			 
			
			// response = $.parseJSON(response);
			 var length =response.Records.length;
			 var rec_set_id = response.Records["0"].rec_set_id;
			 for(var i =0;i<length;i++ ) {
				
				 var fileid = response.Records[i].inFileId;
				 var filename= response.Records[i].stFileName;
				 var display ="";
				 if(i>0){
				 	
					 display = 'none';
				 }
				 var $row = $('<tr id="row'+i+'" class="even" style="display: '+display+'"  />');
				 tblFiledtl.style.display="";
				$('#tblFiledtl').append($row);

				$row.append('<td align="center" class="lD"><label>'+filename+' File</label></td>');
				$row.append('<td align="center" class="lD"><input type="file"  id='+filename+'   name='+filename+'" ></td>');
				$row.append('<td align="center" class="lD"><input type="button" id= btn'+fileid+' name = btn'+fileid+' value="See Rule" onclick=" seerule('+fileid+');" ></td>');
				$row.append('<td align="center" class="lD"><input type="button" id= run'+fileid+'  name = run'+fileid+' value="Run" onclick=" run('+fileid+','+filename+');" ></td>');
			 }
			 
			 document.getElementById("rec_set_id").value= rec_set_id;

		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });
	
	 document.getElementById("filedtl").style.display="";
	 document.getElementById("tbltypdtl").style.display="";
	 document.getElementById("tbltype").style.display="none";
	 document.getElementById("next1").style.display="none";
	}else{
		
		alert("Please Select Category" );
		
	}

	
	
}

function run(id,filename) {
	
	//alert("HELLO");
	var rectyp = document.getElementById("rectyp").value;
	var filedate = document.getElementById("datepicker").value;
	var file = document.getElementById(filename.id).value;
	var count = document.getElementById("count").value;	
	
	var data = new FormData();
	jQuery.each(jQuery('#'+filename.id)[0].files, function(i, file) {
	    data.append('file', file[0]);
	    data.append('category', rectyp);
	    data.append('FileId', id);
	    data.append('Filename', filename.id);
	    data.append('CSRFToken', $('#CSRFToken').val());
	  
	});
	
	$.ajax({
		 
		 type:'POST',
		 url :'runProcess.do',
		 enctype: 'multipart/form-data',
		 data:data,
		 processData: false,
		 success:function(response){
			 if(rectyp=="ONUS") {
				 if(filename=='SWITCH') {
					 alert("All process completed. Please process CBS File");
					 document.getElementById("row"+count).style.display="none";
					 count = count+1;
					 document.getElementById("row"+count).style.display="";
					 
				 }else if(filename=='CBS') {
					 
					 alert("All process completed.Please proceed to Compare");
					 document.getElementById("compare").style.display="";
					 
				 }
										 
				 }
		 	}
		 });
	
	
	
	
	
	
}

function Process() {
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
			url :'runProcess.do',
			async: true,
			beforeSend : function() {
				showLoader();
			},
			complete : function(data) {

				hideLoader();

			},

			data:{category:rectyp,filedate:datepicker,subCat:subCat,dollar_val:dollar_val,CSRFToken:CSRFToken},
			success:function(response){

				alert(response);
				 document.getElementById("stSubCategory").value="-";
				 document.getElementById("dollar_val").value="";
				 document.getElementById("datepicker").value="";
				

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
//ADDED BY INT5779 FOR GETTING SUB CATEGORIES DYNAMICALLY
function getSubCategory()
{
	debugger;
	
//	alert("HELLO");
	var category  = document.getElementById("rectyp").value;
	if(category != "MASTERCARD")
		{
		document.getElementById("dollar").style.display="none";
		}
	else{
		document.getElementById("dollar").style.display="";
	}
	//alert("category is "+category);
	if(category!="" && (category != "ONUS" && category != "AMEX" && category != "CARDTOCARD" && category != "WCC")) { 
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
				 displayContent();
							 

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
/*function chngsubcat(e){
	
	if(e.value=="RUPAY" || e.value == "VISA" || e.value == "POS"){
			
			document.getElementById("trsubcat").style.display="";
		}else{
			
			document.getElementById("trsubcat").style.display="none";
			document.getElementById("SubCat").value="-";
			
		}
		
		
		
	}*/

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
		if(category != "ONUS" && category != "AMEX" && category != "CARDTOCARD" && category != "WCC")
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