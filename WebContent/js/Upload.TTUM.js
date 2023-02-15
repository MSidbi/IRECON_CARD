$(function(){
	
	/*$('#stStart_Date').datepicker({
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
	*/
	/*$('#stDate').datepicker({
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
	});*/
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
	});
	$("input:checkbox:not(.tri)").transformCheckbox({
		checked : "images/chk_on.png",
		unchecked : "images/chk_off.png"
	});
	$("input.tri:checkbox").transformCheckbox({
		checked : "images/chk_on.png",
		unchecked : "images/chk_off.png",
		tristateHalfChecked : "images/chk_tri.png",
		tristate : true
	});
*/
	//$( "input[type=submit], input[type=button], button" ).button();

	/*$('#search').click(function(){
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
	});*/

	/*$('#process').on('click', function(){
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
		alert("hiez");
		try{
			$('#userDiv').find('img').prop("src","images/chk_off.png");
			$('#userDiv :checkbox').prop("class","tri").removeAttr('checked');
		}catch (e) {
			showMessage("Error", e.message);
		}
	});*/
});






function process() {
	
	var stEnd_Date = document.getElementById("stEnd_Date").value;
	var stStart_Date =document.getElementById("stStart_Date").value;
	var stSelectedFile =document.getElementById("stSelectedFile").value;
	var stCategory =document.getElementById("stCategory").value;
	var msg="";
	
	if(stSelectedFile=="") {
		
		msg= msg+" Please Select File.\n";
		
		
	}if(stCategory=="") {
		
		msg= msg+" Please Select category.\n";
		
	}/*if(stEnd_Date=="") {
		
		msg= msg+" Please Select from date.\n";
		
	}if(stStart_Date=="") {
		
		msg= msg+" Please Select end date.\n";
	}*/
	if(stStart_Date!="" && stEnd_Date!="") {
		
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
		
		return true;
	}
	
	
	
	
	
}

/*function getFilesdata() {
	debugger;
//	alert("inside getFilesdata");
//	alert("click");
	var category = document.getElementById("stCategory").value;
//	var subcat = "-";
//	alert("category is "+category);
	var subcat = document.getElementById("stSubCategory").value;
//	alert("category is "+category+" and subcategory is "+subcat);
	
	
	
	
	
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
				 if(option.text != "RUPAY" && option.text != "VISA" )
				 compareFile1.appendChild(option);
			 }
			 for(var i =0;i<length;i++ ) {
					
				 var option= document.createElement("option")
				  option.value = response.Records[i].inFileId;
				 option.text= response.Records[i].stFileName;
					compareFile2.appendChild(option)
			 }
			
			 //document.getElementById("trbtn").style.display="none";
			// document.getElementById("stCategorynew").disabled="disabled";
			 //document.getElementById("SubCat").disabled="disabled";
			 displayContent();
			
			 

		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });
	}else {
		
		
		alert("Please Select Category.");
		
	}
	
	
}*/

function chngsubcat(e){
	
	
	if(e.value=="RUPAY" || e.value == "VISA"){
			
			document.getElementById("trsubcat").style.display="";
		}else{
			
			document.getElementById("trsubcat").style.display="none";
			document.getElementById("stSubCategory").value="-";
			getFilesdata();
			
		}
		
		
		
}

function Upload()
{
 
 	if(validate())
	{
		//alert("sub categroy is "+stsubcat);
		form.submit();
	}
	
}

function validate()
{
	//alert("HIE................");
	var stcategory = document.getElementById("stCategory").value;
	var stsubcat = document.getElementById("stSubCategory").value;
	var stfileselc = document.getElementById("stSelectedFile").value;
	//alert("stfileselc"+stfileselc);
	var stuploadfile = document.getElementById("FileUpload").value;

	if(stcategory == "")
	{
		alert("Select Category");
		return false;
	}
	if(stcategory == "RUPAY" && stsubcat == "-")
	{
		alert("Please select sub category");
		return false;
	}
	if(stfileselc == "0")
	{
		alert("Please select file name");
		return false;
	}
	if(stuploadfile == "")
	{
		alert("Please browse a file for uploading");
		return false;
	}
	return true;
}

function getSubCategory()
{
	var category  = document.getElementById("stCategory").value;
	if(category!="" && (category != "ONUS" && category != "AMEX" && category != "VISA" )) { 
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
				 option1.value="0";
				 option1.text="--Select--";
				 var opt1= document.createElement("option");
				 opt1.value="0";
				 opt1.text="--Select--";
				 compareFile1.appendChild(option1);
				//compareFile2.appendChild(opt1)
				 
				 for(var i =0;i<length;i++ ) {
					
					 var option= document.createElement("option");
					  option.value = response.Subcategories[i];
					 option.text= response.Subcategories[i];
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
			
			
			if(category == 'VISA') {
				
				document.getElementById("stSubCategory").value= "ISSUER";
			}else {
				
				document.getElementById("stSubCategory").value="-";
			}
			document.getElementById("trsubcat").style.display="none";
			getFilesdata();
			
			//alert("Please Select Category.");
			
		}
	
}