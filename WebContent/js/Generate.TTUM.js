/*$(document).ready(function(){
    
	document.getElementById("tr_function_code").style.display="none";
	
 }); */

$(function(){
	
	$('#stStart_Date').datepicker({
		showOn : "button",
		buttonImage : "images/calendar.png",
		buttonImageOnly : true,
		buttonText : 'Calendar',
		yearRange : '-60:+0',
		maxDate : '0D',
		dateFormat:"dd/mm/yy",
		changeMonth : true,
		changeYear : true,
		onClose : function(){
			$('#rtr_on').focus();
		}
	});
	
	
	$('#stEnd_Date').datepicker({
		showOn : "button",
		buttonImage : "images/calendar.png",
		buttonImageOnly : true,
		buttonText : 'Calendar',
		yearRange : '-60:+0',
		maxDate : '0D',
		dateFormat:"dd/mm/yy",
		changeMonth : true,
		changeYear : true,
		onClose : function(){
			$('#rtr_on').focus();
		}
	});

	$('#stEnd_Date').blur(function(){
		var rtr_on = $.trim($('#stEnd_Date').val());
		if(rtr_on != '' && rtr_on != undefined){
			if(validDate(rtr_on) == false){
				showError("Invalid Date.", "stEnd_Date");
			}
		}
	});
	
	$('#stStart_Date').blur(function(){
		var rtr_on = $.trim($('#stStart_Date').val());
		if(rtr_on != '' && rtr_on != undefined){
			if(validDate(rtr_on) == false){
				showError("Invalid Date.", "stStart_Date");
			}
		}
	});
	
	$('#stDate').datepicker({
		showOn : "button",
		buttonImage : "images/calendar.png",
		buttonImageOnly : true,
		buttonText : 'Calendar',
		yearRange : '-60:+0',
		maxDate : '0D',
		dateFormat:"dd/mm/yy",
		changeMonth : true,
		changeYear : true,
		onClose : function(){
			$('#rtr_on').focus();
		}
	});
	$('#stDate').blur(function(){
		var rtr_on = $.trim($('#stDate').val());
		if(rtr_on != '' && rtr_on != undefined){
			if(validDate(rtr_on) == false){
				showError("Invalid Date.", "stDate");
			}
		}
	});
	

	/*$('li :checkbox').on('click', function () {
		var $chk = $(this), $li = $chk.closest('li'), $ul, $parent;
		if ($li.has('ul')) {
			$li.find(':checkbox').not(this).prop('checked', this.checked);
		}
		do {
			$ul = $li.parent();
			$parent = $ul.siblings(':checkbox');
			if ($chk.is(':checked')) {
				$parent.prop('checked', $ul.has(':checkbox:not(:checked)').length == 0);
			} else {
				$parent.prop('checked', false);
			}
			$chk = $parent;
			$li = $chk.closest('li');
		} while ($ul.is(':not(.someclass)'));
	});*/

	/**Code to transform Checkbox in images.*/
	/*$(".class input:checkbox").transformCheckbox({
		base : "class",
		checked : "images/chk_on.png",
		unchecked : "images/chk_off.png"
	});*/
/*	$("input:checkbox:not(.tri)").transformCheckbox({
		checked : "images/chk_on.png",
		unchecked : "images/chk_off.png"
	});
	$("input.tri:checkbox").transformCheckbox({
		checked : "images/chk_on.png",
		unchecked : "images/chk_off.png",
		tristateHalfChecked : "images/chk_tri.png",
		tristate : true
	});*/

	/*$( "input[type=submit], input[type=button], button" ).button();

	$('#search').click(function(){
		try{
			var user_id = $.trim($('#user_id').val());
			if(user_id != '' && user_id != undefined){
				fetchUserRole(user_id);
			}else{
				$('#userDiv').empty();
				$('#userDiv').append('<table align="center" width="100%"><tr><td align="center">Select User from above list.<br/> To view assigned roles</td></tr></table>');
			}
			clearAll();
		}catch (e) {
			showMessage("Error", e.message);
		}
	});

	$('#process').on('click', function(){
		alert("check validations");
	});

	$('#revoke').on('click', function(){
		revokeRole();
	});

	$('#reset_all').click(function(){
		//alert("HELLOZ");
		try{
			$('#allDiv').find('img').prop("src","images/chk_off.png");
			$('#allDiv :checkbox').prop("class","tri").removeAttr('checked');
		}catch (e) {
			showMessage("Error", e.message);
		}
	});

	$('#reset_user').click(function(){
		//alert("hiez");
		try{
			$('#userDiv').find('img').prop("src","images/chk_off.png");
			$('#userDiv :checkbox').prop("class","tri").removeAttr('checked');
		}catch (e) {
			showMessage("Error", e.message);
		}
	});*/
});



function displayDate(){
	var category = document.getElementById("stCategory").value;
	document.getElementById("stCategory").value=category;
	if(category =='RUPAY' || category == 'VISA') {
		document.getElementById("startDate").style.display='none';
		document.getElementById("enddate").style.display='none';
		document.getElementById("Date").style.display='';
		
		
	}else {
		document.getElementById("Date").style.display='none';
		document.getElementById("startDate").style.display='';
		document.getElementById("enddate").style.display='';
	}
	
}


function surchrgChng() {
	
	var category = document.getElementById("stCategory").value;
	var inRec_Set_Id = document.getElementById("inRec_Set_Id").value;
//	alert("category is "+category);
	var subcat = document.getElementById("stSubCategory").value;
//	alert("category is "+category+" and subcategory is "+subcat);
	
	if(category =='VISA' && subcat=='ISSUER' && inRec_Set_Id=="-1"  ) {
		document.getElementById("startDate").style.display='none';
		document.getElementById("enddate").style.display='none';
		document.getElementById("Date").style.display='';
		
		
	}else if(category =='VISA' && subcat=='ISSUER' && inRec_Set_Id!="-1"  ) {
//		alert("Inside else");
		document.getElementById("Date").style.display='none';
		document.getElementById("startDate").style.display='';
		document.getElementById("enddate").style.display='';
	}
}


function process() {
	debugger;
	var stEnd_Date = document.getElementById("stEnd_Date").value;
	var stStart_Date = document.getElementById("stStart_Date").value;
	var stDate = document.getElementById("stDate").value;
	var stSelectedFile =document.getElementById("stSelectedFile").value;
	var inRec_Set_Id = document.getElementById("inRec_Set_Id").value;
	//alert("stselectfile "+stSelectedFile);
	var stCategory =document.getElementById("stCategory").value;
	var msg="";
	
	if(stSelectedFile==0) {
		
		msg= msg+" Please Select File.\n";
		
		
	}if(stCategory=="") {
		
		msg= msg+" Please Select category.\n";
		
	}if(inRec_Set_Id=="0") {
		
		msg= msg+" Please Select cycle.\n";
		
	}if((stStart_Date=="")&&(stEnd_Date=="")) {
		
		if(stDate=="") {
			msg= msg+" Please Select date.\n";
		
		}
	}if((stDate=="")) {
		
		if((stStart_Date=="")||(stEnd_Date=="")) {
			msg= msg+" Please Select date.\n";
		
		}
	}
//	alert("hello");
	if(stStart_Date!="" && stEnd_Date!="") {
		
		/*if(compareDate(stStart_Date,stEnd_Date) == false)
				msg = msg+"Start date should be smaller than end date.\n";	
		
	}*/
		var date1 = new Date(stStart_Date);
		var date2= new Date(stEnd_Date);
	
		if(!(checkEnteredDates(stStart_Date,stEnd_Date))) {
			
			msg = msg+"Start date should be smaller than end date.\n";	
		}
	
}
	
	if(msg!=""){
		
		alert(msg);
		return false;
	}else {
		
		return true;
	}
	
	
	
	
	
}


function checkEnteredDates(stdateval,endateval){
	 //seperate the year,month and day for the first date
	 var stryear1 = stdateval.substring(6);
	 var  strday1 = stdateval.substring(0,2);
	 var  strmth1 = stdateval.substring(5,3);
	 var date1    = new Date(stryear1 ,strmth1 ,strday1);
	 
	 //seperate the year,month and day for the second date
	 var stryear2 = endateval.substring(6);
	 var  strday2 = endateval.substring(0,2);
	 var strmth2  = endateval.substring(5,3);
	 var date2    = new Date(stryear2 ,strmth2 ,strday2 );
	 
	
	 if(stryear1 <= stryear2) {
		 
		 if(strmth1< strmth2) {
			 return true;
			}
		 else {
			 
			 if(strmth1== strmth2) {
				 if(strday1<= strday2) {
					 return true;
				 	}
				 else {
					 
					 return false;
				 }
			  }else {
				  
				  false;
			  }
			 }
		 
	 } else {
		 
		 return true;
	 }
	 
	 
	 if(datediffval <= 0){
	  alert("Start date must be prior to end date");
	  return false;
	 }
	 return true;
	}


function generateTTUM() {
	
	
	var stEnd_Date = document.getElementById("stEnd_Date").value;
	var stStart_Date =document.getElementById("stStart_Date").value;
	var stSelectedFile =document.getElementById("stSelectedFile").value;
	//alert("stselectfile "+stSelectedFile);
	var stCategory =document.getElementById("stCategory").value;
	var msg="";
	
	if(stSelectedFile==0) {
		
		msg= msg+" Please Select File.\n";
		
		
	}if(stCategory=="") {
		
		msg= msg+" Please Select category.\n";
		
	}/*if(stEnd_Date=="") {
		
		msg= msg+" Please Select from date.\n";
		
	}if(stStart_Date=="") {
		
		msg= msg+" Please Select end date.\n";
	}*/
//	alert("hello");
	if(stStart_Date!="" && stEnd_Date!="") {
		
		/*if(compareDate(stStart_Date,stEnd_Date) == false)
				msg = msg+"Start date should be smaller than end date.\n";	
		
	}*/
		var date1 = new Date(stStart_Date);
		var date2= new Date(stEnd_Date);
	
		if(date1.getTime()>date2.getTime()) {
			msg = msg+"Start date should be smaller than end date.\n";	
		}
	
}
	
	if(msg!=""){
		
		alert(msg);
		return false;
	}else {
		
		
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

			data:{category:rectyp,filedate:datepicker,subCat:subCat,dollar_val:dollar_val},
			success:function(response){

				alert(response);
				 document.getElementById("stSubCategory").value="-";
				 document.getElementById("dollar_val").value="";
				 document.getElementById("datepicker").value="";
				

			},error: function(){

				alert("Error Occured");

			},

		});
		
		return true;
	}
	
}


function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}

/*function compareDate(datePrev, dateNext){
	try{
		if(validDate(datePrev) == false){
			throw new Error("Invalid First Date");
		}
		if(validDate(dateNext) == false){
			throw new Error("Invalid Second Date");
		}
		var splitter1 = datePrev.split("/");
		datePrev = new Date(splitter1[2], parseInt(splitter1[1], 10) -1, splitter1[0] );
		
		var splitter2 = dateNext.split("/");
		dateNext = new Date(splitter2[2], parseInt(splitter2[1], 10) -1, splitter2[0] );
		
		//if(Date.parse(datePrev) < Date.parse(dateNext)){
		if(datePrev < dateNext){
			return true;
		}else{
			return false;
		}
	}catch (e) {
		return false; 
	}
}
function validDate(date){
	alert("date is "+date);
	var dateRegex = /^(((0[1-9]|[12]\d|3[01])\/(0[13578]|1[02])\/((19|[2-9]\d)\d{2}))|((0[1-9]|[12]\d|30)\/(0[13456789]|1[012])\/((19|[2-9]\d)\d{2}))|((0[1-9]|1\d|2[0-8])\/02\/((19|[2-9]\d)\d{2}))|(29\/02\/((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))))$/;
	if (date.match(dateRegex)) {
		return true;
	}
	return false;
}*/

function getFilesdata() {
//	alert("inside getFilesdata");
	debugger;
///	alert("inside getFilesdata");
//	alert("click");
	//validate();
	var category = document.getElementById("stCategory").value;
//	var subcat = "-";
//	alert("category is "+category);
	var subcat = document.getElementById("stSubCategory").value;
//	alert("category is "+category+" and subcategory is "+subcat);
	
	if(category =='RUPAY' || category == 'VISA') {
		document.getElementById("startDate").style.display='none';
		document.getElementById("enddate").style.display='none';
		document.getElementById("Date").style.display='';
		
		
	}else {
//		alert("Inside else");
		document.getElementById("Date").style.display='none';
		document.getElementById("startDate").style.display='';
		document.getElementById("enddate").style.display='';
	}
	
	if(category!="") { 
	$.ajax({
		 
		 type:'POST',
		 url :'getFiledetails.do',
		 data:{category:category,subcat:subcat},
		 success:function(response){
			 
			console.log(response);
			// response = $.parseJSON(response);
			 var length =response.records.length;
			
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
				  option.value = response.records[i].stFileName;
				 option.text= response.records[i].stFileName;
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

function chngsubcat(e){
	
	
	if(e.value=="RUPAY" || e.value == "VISA"){
			
			document.getElementById("trsubcat").style.display="";
		}else{
			
			document.getElementById("trsubcat").style.display="none";
			document.getElementById("stSubCategory").value="-";
			getFilesdata();
			
		}
		
		
		
	}

function getSubCategory()
{
	var category  = document.getElementById("stCategory").value;
	if(category!="" && (category != "ONUS" && category != "AMEX"  && category != "CARDTOCARD")) { 
		document.getElementById("trsubcat").style.display="";
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
					  option.value = response.subcategories[i];
					 option.text= response.subcategories[i];
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
			//alert("category is "+category);
			document.getElementById("trsubcat").style.display="none";
			document.getElementById("stSubCategory").value="-";
			getFilesdata();
			
			//alert("Please Select Category.");
			
		}
	
	
	
}




function getRespCode () {
	
	debugger
	validate();
	var category = document.getElementById("stCategory").value;

	var subcat = document.getElementById("stSubCategory").value;
	
	
	var fileName = document.getElementById("stSelectedFile").value;
	
	var filedate = document.getElementById("stStart_Date").value;
	
	if ( (category!=="" && subcat!=="" && fileName!=="" && (category=="CASHNET" || category=="MASTERCARD" || category=="VISA"))) {
		
	
		$.ajax({
				 
				 type:'POST',
				 url :'getRespCode.do',
				 data:{category:category,subcat:subcat,filename:fileName,filedate:filedate},
				 success:function(response){
					 
					
					// response = $.parseJSON(response);
					 var length =response.respCode.length;
					
					 var compareFile1 = document.getElementById("respcode");
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
					// alert("Helloooo");
					 for(var i =0;i<length;i++ ) {
						
						 var option= document.createElement("option");
						  option.value = response.respCode[i];
						 option.text= response.respCode[i];
						 compareFile1.appendChild(option);
					 }
					/* var option= document.createElement("option");
					  option.value = 'All';
					 option.text= 'All';
					 compareFile1.appendChild(option);*/
					/* for(var i =0;i<length;i++ ) {
							
						 var option= document.createElement("option")
						  option.value = response.Records[i].inFileId;
						 option.text= response.Records[i].stFileName;
							compareFile2.appendChild(option)
					 }*/
					
					 document.getElementById("trrespcode").style.display="";
					// document.getElementById("stCategorynew").disabled="disabled";
					 //document.getElementById("SubCat").disabled="disabled";
					 
					 
				
					
					 
		
				 },error: function(){
					
					 alert("Error Occured");
					 
				 },
				 
			 });
	}
}




