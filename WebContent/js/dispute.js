function getsearchdata1() {
	alert("hi");
	
debugger;

	$('#DisputeTableContainer').jtable({
		title : 'Search',
		paging : true, // Enable paging
		pageSize : 10, // Set page size
		sorting : true,
		selecting : true,
		selectingCheckboxes : true,
		actions : {
			listAction : 'SearchDispute1.do'
			//updateAction : 'updateProject.do'

		},
		fields : {
			/*category : {
				title : 'CATEGORY',
				width : '25%',
				key : true,
				list : true,
				edit : true
			},*/
			/*tran_date : {
				title : 'TRAN DATE',
				width : '20%',
				edit : true
			},
			amount : {
				title : 'AMOUNT',
				width : '20%',
				edit : true

			},
			card_No : {
				title : 'CARD NO',
				width : '20%',
				edit : true

			},*/
			filedate : {
				title : 'FILEDATE',
				width : '20%',
				edit : true,
			}

			
		}
	});
	
	$('#DisputeTableContainer').jtable('load', {
		category : $('#category').val() != "" ? $('#category').val() : 0,
		stSubCategory : $('#stSubCategory').val() != null ? $('#stSubCategory').val() : 0,
		file_name : $('#file_name').val() != null ? $('#file_name').val() : 0,
		//dcrs_remarks : $('#dcrs_remarks').val() != null ? $('#dcrs_remarks').val() : 0,
		filedate : $('#filedate').val() != null ? $('#filedate').val() : 0

	});
	

/*	$('#view').click(function(e) {
		e.preventDefault();
		// alert($('#drop').val());
		// alert($('#emply_cd').val());
		$('#DisputeTableContainer').jtable('load', {
			category : $('#category').val() != "" ? $('#category').val() : 0,
			stSubCategory : $('#stSubCategory').val() != null ? $('#stSubCategory').val() : 0,
			file_name : $('#file_name').val() != null ? $('#file_name').val() : 0,
			//dcrs_remarks : $('#dcrs_remarks').val() != null ? $('#dcrs_remarks').val() : 0,
			filedate : $('#filedate').val() != null ? $('#filedate').val() : 0

		});
	});
	$('#view').click();
*/
}



//var response1;
function getsearchdata() {
	
	var category = $('#category').val();
	var stSubCategory =  $('#stSubCategory').val();
	var file_name = $('#file_name').val();
	var dcrs_remarks = $('#dcrs_remarks').val();
	var filedate = $('#filedate').val(); 
	
debugger;
if(category!=="" && stSubCategory!=="" && file_name!=="" && filedate!="" && dcrs_remarks!="") {

$.ajax({
		 
		 type:'POST',
		 url :'SearchDispute.do',
		 data:{category:category,stSubCategory:stSubCategory,file_name:file_name,dcrs_remarks:dcrs_remarks,filedate:filedate},
		 success:function(response){
			 
				console.log(response);
				var res= JSON.parse(response);
				//response1 = JSON.parse(response);
				var tbl = document.getElementById("searchdata");
				var headerlength = res.excelheaders.length;
				var datalngth = res.data.length;
				var rowcount = datalngth/headerlength;
				var datacount=0;
				var datacount1=0;
				var pan='';
				var appCd='';
				var arn='';
				var amt='';
				var val1='',val2='',val3='',val4='';
				var val='val';
				var value='';
				var value2='';
				
				//tbl.removeChild(tbl.lastChild);
				
				/*var tbody =document.createElement('tbody');
				t*/
				
				var tr = document.createElement('tr');
				
				var td= document.createElement('th');
				 tr.appendChild(td);
				 $("<td />").html('<input type="checkbox" class="get_value_All" name="checkAll" id="checkAll" onchange="changeCheck(this)"/>').appendTo(tr);
				for(var i=0;i<res.excelheaders.length; i++) {					
					
					var td= document.createElement('th');
					var text = document.createTextNode(res.excelheaders[i]);
					td.appendChild(text);
					 tr.appendChild(td);
					
					
				}
				tbl.appendChild(tr);
				var tr1 =null; 
				var headSize = res.matchHeadList.length;
				var size=0;
				for(var record=0;record<rowcount;record++) {
					//if(record==0){
					for(var i=0;i<res.excelheaders.length; i++) {					
						/*if(res.excelheaders[i] == res.matchHeadList[0]){
							val1 = res.data[datacount1];						
						}else if(res.excelheaders[i] == res.matchHeadList[1]){
							val2 = res.data[datacount1];						
						}else if(res.excelheaders[i] == res.matchHeadList[2]){
							val3 = res.data[datacount1];						
						}else if(res.excelheaders[i] == res.matchHeadList[3]){
							val4 = res.data[datacount1];						
						}*/
						for(var j=0;j<res.matchHeadList.length;j++){
							if(res.excelheaders[i] == res.matchHeadList[j]){
								value = res.data[datacount1];
								
							}
							//value = val+j+'#';
							//alert(value);
						}
						
						//value1=value1+value+'#';
						//alert(value);
						
						datacount1=datacount1+1;
					}
					//}
				for(var i=0;i<res.excelheaders.length; i++) {					
					/*if(res.excelheaders[i] == res.matchHeadList[0]){
						val1 = res.data[datacount];						
					}else if(res.excelheaders[i] == res.matchHeadList[1]){
						val2 = res.data[datacount];						
					}else if(res.excelheaders[i] == res.matchHeadList[2]){
						val3 = res.data[datacount];						
					}else if(res.excelheaders[i] == res.matchHeadList[3]){
						val4 = res.data[datacount];						
					}
					*/
					
					if(category == 'RUPAY' && file_name == 'SWITCH'){
						if(res.excelheaders[i] == 'PAN'){
							pan = res.data[datacount];
						}else if(res.excelheaders[i] == 'TRACE'){
							appCd = res.data[datacount];						
						}else if(res.excelheaders[i] == 'MERCHANT_TYPE'){
							arn = res.data[datacount];
						}else if(res.excelheaders[i] == 'AMOUNT'){
							amt = res.data[datacount];
						}
					}
					else if(category == 'RUPAY' && file_name == 'CBS'){
						if(res.excelheaders[i] == 'REMARKS'){
							pan = res.data[datacount];
						}else if(res.excelheaders[i] == 'REF_NO'){
							appCd = res.data[datacount];						
						}else if(res.excelheaders[i] == 'PARTICULARALS2'){
							arn = res.data[datacount];
						}else if(res.excelheaders[i] == 'AMOUNT'){
							amt = res.data[datacount];
						}
					}
					else if(category == 'RUPAY' && file_name == 'network'){
						if(res.excelheaders[i] == 'PRIMARY_ACCOUNT_NUMBER'){
							pan = res.data[datacount];
						}else if(res.excelheaders[i] == 'APPROVAL_CODE'){
							appCd = res.data[datacount];						
						}else if(res.excelheaders[i] == 'ACQUIRER_REFERENCE_DATA'){
							arn = res.data[datacount];
						}else if(res.excelheaders[i] == 'AMOUNT_TRANSACTION'){
							amt = res.data[datacount];
						}
					}
					else if(category == 'VISA' &&  stSubCategory == 'ACQUIRER' ){
						if(res.excelheaders[i] == 'CARD_NUMBER'){
							pan = res.data[datacount];
						}else if(res.excelheaders[i] == 'TRACE'){
							appCd = res.data[datacount];						
						}else if(res.excelheaders[i] == 'REFERENCE_NUMBER'){
							arn = res.data[datacount];
						}else if(res.excelheaders[i] == 'SOURCE_AMOUNT'){
							amt = res.data[datacount];
						}
					}
					else if(category == 'VISA' &&  stSubCategory == 'ISSUER' ){
						if(res.excelheaders[i] == 'CARD_NUMBER'){
							pan = res.data[datacount];
						}else if(res.excelheaders[i] == 'AUTHORIZATION_CODE'){
							appCd = res.data[datacount];				
						}else if(res.excelheaders[i] == 'DESTINATION_AMOUNT'){
							amt = res.data[datacount];
						}
					}
					
					
					
				
					if(i==0) {
						value2 = pan+'#'+appCd+'#'+amt;
						alert(value2);
					 tr1 = document.createElement('tr');
					 var td= document.createElement('td');
					 tr1.appendChild(td); 
					// alert(res.data[datacount]);
					 //if(record!=0){
					// $("<td />").html('<input type="checkbox" class="get_value" name="data1" id="data1" value="'+val1+'#'+val2+'#'+val3+'#'+val4+'"/>').appendTo(tr1);
					 $("<td />").html('<input type="checkbox" class="get_value" name="data1" id="data1" value="'+value1+'"/>').appendTo(tr1);
					//}
				}
					var td= document.createElement('td');
					
					var text = document.createTextNode(res.data[datacount]);
					td.appendChild(text);
					 tr1.appendChild(td);
					 datacount=datacount+1;
				//}
				}
				
				// $("<td />").html('<input type="checkbox" class="get_value" name="data1" id="data1" value="'+val1+'#'+val2+'#'+val3+'#'+val4+'"/>').appendTo(tr1);
				    tbl.appendChild(tr1);
				   
				}   
				    
				    //td.appendChild(text2);
				    tbl.style.display="";
				    
				   
				  
				    
				// response = $.parseJSON(response);
				

			 },error: function(){
			
			 alert("Error Occured in searching data");
			 
		 },
		 
	 });

}else {
	
	alert("PLEASE FILL THE IMPORTANT FIELDS TO GET RESULT");
	
}

}

function changeCheck(){	 
	alert("In Check all");
	 var select_all = document.getElementById("checkAll"); //select all checkbox
	 var checkboxes = document.getElementsByClassName("get_value"); //checkbox items

	 //select all checkboxes
	  select_all.addEventListener("change", function(e){
	      for (i = 0; i < checkboxes.length; i++) { 
	          checkboxes[i].checked = select_all.checked;
	      }
	 });


	 for (var i = 0; i < checkboxes.length; i++) {
	      checkboxes[i].addEventListener('change', function(e){ //".checkbox" change 
	          //uncheck "select all", if one of the listed checkbox item is unchecked
	          if(this.checked == false){
	              select_all.checked = false;
	          }
	          //check "select all" if all checkbox items are checked
	          if(document.querySelectorAll('.checkbox:checked').length == checkboxes.length){
	              select_all.checked = true;
	          }
	      });
	 }
	 
}




function forceMatch(){
	alert("In Force match..");
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
			 alert(response);
			 $('#category').val('');
			 $('#stSubCategory').val('');
			 $('#file_name').val('');
			 $('#filedate').val('');
			// $('#dcrs_remarks').val('');
			 $('#searchdata').html('');
			 
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


function getDcrsRemarks(){
	var category = $('#category').val();
	var stSubCategory =  $('#stSubCategory').val();
	var file_name = $('#file_name').val();
	
	$.ajax({
		 
		 type:'POST',
		 url :'getDcrsRemarks.do',
		 data:{category:category,stSubCategory:stSubCategory,file_name:file_name},
		 success:function(response){
			 
		 },error: function(){
				
			 alert("Error Occured");
			 
		 },
		 
	 });
	
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

