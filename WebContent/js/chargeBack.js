$(function(){
});

function Upload()
{
 
	var $file = $('#attachment');
	 
	for(var inx =0; inx < $file[0].files.length ; inx++){
		if($file[0].files[inx].name.indexOf('.xls') == -1 ){
			return false;
		}
	}
	
	//ValidateData();
	
	var fileType = $('#fileType').val();
	var subcategory = $('#stSubCategory').val();
	var attachment = $('#attachment').val();
	var datepicker = $('#datepicker').val();
	
	debugger;
	
	if((subcategory == "" || subcategory == "-"))
	{
			alert("Please select subcategory ");
			return false;				
	}else if(datepicker == ""){
		alert("Please select date ");
		return false;	
	}else if(fileType == "")
	{
		alert("Please select fileType ");
		return false;
	}
	else if(attachment == "")
	{
		alert("Please select file for processing");
		return false;
	}else{
	
		 $('#message').text('please wait Records are updating...');
	$('#form').submit();
	}
}

function getupldfiledetails(){
	try{
		var filedate = $('#datepicker').val();
		var subCat = $('#stSubCategory').val();
		//alert(filedate);
		//alert(subCat);
		$.ajax({
			type : 'post',
			url : 'UploadFileDetails.do',
			data : {'subCat' : subCat, 'filedate' : filedate },
			success : function(responseJson){
				if(responseJson != null){
				//	alert(responseJson);
					//displayUserRole(responseJson);
					//clearAll();
					//$('#fileDtl').show;
					//$('#message1').text('please wait your file is being downloading...');
					$('#message1').text(responseJson);
					
				}else{
					showMessage("Error", "No Data Found.");
				}
			}
		});
	}catch (e) {
		showMessage("Error", e.message);
	}
}

