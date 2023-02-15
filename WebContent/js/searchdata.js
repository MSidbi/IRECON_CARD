function getsearchdata() {
	
var category = document.getElementById("category").value;
var stSubCategory = document.getElementById("stSubCategory").value  ;
var card_No = document.getElementById("card_No").value  ;
var tran_date=document.getElementById("tran_date").value  ;
var amount=document.getElementById("amount").value  ; 
var filename=document.getElementById("stSelectedFile").value  ; 
var dcrs_remarks = document.getElementById("dcrs_remarks").value  ;
var filedate= document.getElementById("filedate").value;

if(category!=="" && stSubCategory!=="" && card_No!=="" && tran_date!=="") {
	$.ajax({
			 
			 type:'POST',
			 url :'searchData.do',
			 data:{category:category,stSubCategory:stSubCategory,card_No:card_No,tran_date:tran_date,amount:amount,filename:filename,dcrs_remarks:dcrs_remarks,filedate:filedate},
			 beforeSend : function() {
					showLoader();
				},
				complete : function(data) {

					hideLoader();

				},success:function(response){
				 
				console.log(response);
				var res= JSON.parse(response);
				var tbl = document.getElementById("searchdata");
				var headerlength = res.excelheaders.length;
				var datalngth = res.data.length;
				var rowcount = datalngth/headerlength;
				var datacount=0;
				
				var count = $('#searchdata tr').length;
				for (var i=0;i<count;i++) {
					
					tbl.removeChild(tbl.lastChild);
				}
				
				
				/*var tbody =document.createElement('tbody');
				t*/
				
				var tr = document.createElement('tr');
				
				var td= document.createElement('th');
				 tr.appendChild(td);
				 
				for(var i=0;i<res.excelheaders.length; i++) {
					
					
					var td= document.createElement('th');
					var text = document.createTextNode(res.excelheaders[i]);
					td.appendChild(text);
					 tr.appendChild(td);
					
				}
				tbl.appendChild(tr);
				var tr1 =null; ;
				for(var record=0;record<rowcount;record++) {
				for(var i=0;i<res.excelheaders.length; i++) {
				
					if(i==0) {
					 tr1 = document.createElement('tr');
					 var td= document.createElement('td');
					 tr1.appendChild(td);
					 
					}
					var td= document.createElement('td');
					
					var text = document.createTextNode(res.data[datacount]);
					td.appendChild(text);
					 tr1.appendChild(td);
					 datacount=datacount+1;
					
				}
					
				    tbl.appendChild(tr1);
				}
				   
				    
				    
				    //td.appendChild(text2);
				    tbl.style.display="";
				    
				   
				  
				    
				// response = $.parseJSON(response);
				
	
			 },error: function(){
				
				 alert("Error Occured");
				 
			 },
			 
		 });

}else {
	
	alert("PLEASE FILL THE IMPORTANT FIELDS TO GET RESULT");
	
}

}



function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}

function getTTUMdetails(){
	
	var category = document.getElementById("category").value;
	var stSubCategory = document.getElementById("stSubCategory").value  ;
	var card_No = document.getElementById("card_No").value  ;
	var tran_date=document.getElementById("tran_date").value  ;
	var amount=document.getElementById("amount").value  ; 
	var filename=document.getElementById("stSelectedFile").value  ; 
	var dcrs_remarks = document.getElementById("dcrs_remarks").value  ;
	var filedate= document.getElementById("filedate").value;

	if(category!=="" && stSubCategory!=="" && card_No!=="" && tran_date!=="") {
$.ajax({
	type : 'POST',
	url : 'checkttum.do',
	data:{category:category,stSubCategory:stSubCategory,card_No:card_No,tran_date:tran_date,amount:amount,filename:filename,dcrs_remarks:dcrs_remarks,filedate:filedate},
	success:function(response) {
		debugger;
		//hideLoader();
		
		//alert (response); t 
		
		document.getElementById("ttumID").innerHTML=response;
		openTtumModal();
		
				},
				/*  complete: function() {
				    window.location = 'SourceFileUpload.jsp';
				  },
				 */

				error : function(err) {
					alert(err);
				}
			});
	}
}

function getFilestype() {
	
	debugger;
	var category = document.getElementById("category").value;
	var type = document.getElementById("stSelectedFile").value;
	
	//var type = document.getElementById("setltbl").text;
	//var subcat = "-";
	var subcat = document.getElementById("stSubCategory").value;
	var tablename="";
	if(category=="NFS" || category == "CASHNET") {
		
		 tablename='SETTLEMENT'+'_'+category+'_'+subcat.substring(0,3)+'_'+type
	} else {
	 tablename='SETTLEMENT'+'_'+category+'_'+type;
	}
	//var tablename= document.getElementById('dataType');
	//tablename.options[tablename.options.length]= new Option(concatabl, '1');
	if(category!="") { 
	$.ajax({
		 
		 type:'POST',
		 url :'getFileTypedetails.do',
		 data:{category:category,subcat:subcat,tablename:tablename},
		 success:function(response){
			 
			debugger;
			// response = $.parseJSON(response);
			 var length =response.Records.length;
			
			 var compareFile1 = document.getElementById("dcrs_remarks");
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
				
				 var option= document.createElement("option");
				  //option.value = response.Records[i].inFileId;
				 option.text= response.Records[i].remarks;
				 compareFile1.appendChild(option);
			 }
			/* for(var i =0;i<length;i++ ) {
					
				 var option= document.createElement("option")
				  option.value = response.Records[i].inFileId;
				 option.text= response.Records[i].stFileName;
					compareFile2.appendChild(option)
			 }*/
			
			 //document.getElementById("trbtn").style.display="none";
			 //document.getElementById("category").disabled="disabled";
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



function getFilesdata() {
//	alert("inside getFilesdata");
	debugger;
///	alert("inside getFilesdata");
//	alert("click");
	var category = document.getElementById("category").value;
//	var subcat = "-";
//	alert("category is "+category);
	var subcat = document.getElementById("stSubCategory").value;
//	alert("category is "+category+" and subcategory is "+subcat);
	/*
	if(category =='RUPAY' || category == 'VISA') {
		document.getElementById("startDate").style.display='none';
		document.getElementById("enddate").style.display='none';
		document.getElementById("Date").style.display='';
		
		
	}else {
//		alert("Inside else");
		document.getElementById("Date").style.display='none';
		document.getElementById("startDate").style.display='';
		document.getElementById("enddate").style.display='';
	}*/
	
	if(category!="") { 
	$.ajax({
		 
		 type:'POST',
		 url :'getFiledetails.do',
		 data:{category:category,subcat:subcat},
		 success:function(response){
			 
			
			// response = $.parseJSON(response);
			 var length =response.Records.length;
			
			 var compareFile1 = document.getElementById("stSelectedFile");
			// var compareFile2 = document.getElementById("Man_file");
			 
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
			//compareFile2.appendChild(opt1)
			 
			 for(var i =0;i<length;i++ ) {
				
				 var option= document.createElement("option");
				  option.value = response.Records[i].stFileName;
				 option.text= response.Records[i].stFileName;
				 compareFile1.appendChild(option);
			 }
			 
			
			

		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });
	}else {
		
		
		alert("Please Select Category.");
		
	}
	
	
}

function getSubCategory()
{
	debugger;
	
//	alert("HELLO");
	var category  = document.getElementById("category").value;
	
	//alert("category is "+category);
	if(category!="" && (category != "ONUS" && category != "AMEX" && category != "CARDTOCARD")) { 
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
				 //displayContent();
							 

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



