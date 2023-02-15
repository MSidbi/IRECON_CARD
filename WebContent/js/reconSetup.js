


function previous() { 
	
	document.getElementById("id1").style.display="block";
	document.getElementById("id2").style.display="none";	
	document.getElementById("reconsave").style.display="none";
//	document.getElementById("layerCount").readonly=true;
	document.getElementById("category").readonly=true;
	document.getElementById("compareFile1").readonly=true;
	document.getElementById("compareFile2").readonly=true;
}


function displaycatg(e){
	
	var category = e.value;
	document.getElementById("category").value=category;
	if(category=='RUPAY' || category == 'VISA' || category == 'MASTERCARD') {
		
		document.getElementById("trsubcategory").style.display='';
		
		
	}else {
		
		document.getElementById("trsubcategory").style.display='none';
	}
	
}

function setSubCat(){
	
	var stSubCategory = document.getElementById("SubCat").value;
	document.getElementById("stSubCategory").value=stSubCategory;
	
}



function addRow(){
	
	
	
	
	//var layerCount =document.getElementById("layerCount").value;
	var fileselect1 = document.getElementById("fileselect1").value;
	var staPdding1 = document.getElementById("staPdding1").value;
	var startPos1 = document.getElementById("startPos1").value;
	var charsize1 = document.getElementById("charsize1").value;
	var dataType1  =document.getElementById("dataType1").value;
	var datpattern1 =document.getElementById("datpattern1").value;
	var relaxparam1 = document.getElementById("stRelaxParam1").value;
	

	var fileselect2 =document.getElementById("fileselect2").value;
	var staPdding2 =document.getElementById("staPdding2").value;
	var startPos2 =document.getElementById("startPos2").value;
	var charsize2 =document.getElementById("charsize2").value;
	var dataType2 =document.getElementById("dataType2").value;
	var datpattern2 =document.getElementById("datpattern2").value;
	var relaxparam2 = document.getElementById("stRelaxParam2").value;

	

	
	var count = parseInt($.trim($('#reconcount').val()), 10);
	var datacount = parseInt($.trim($('#datacount').val()), 10);
	
	var detailtbl = document.getElementById("detailtbl");
	
	
	if(validate()) {
		
		
		
		if(datacount==0) {
			
			
			
			setFirstvalue();
			
			document.getElementById("rowid").value="display";
			detailtbl.style.display="";
			
		}
		else{
	var $row = $('<tr id="row'+datacount+'" class="even" />');
	
	detailtbl.style.display="";
	$('#detailtbl').append($row);

	$row.append('<td align="center" class="lD"><input type="text" readonly="true" id="columnDtls['+datacount+'].fileColumn1" name="columnDtls['+datacount+'].fileColumn1" value="'+fileselect1 +'" ></td>');
	$row.append('<td align="center" class="lD"><input type="text" readonly="true" id="columnDtls['+datacount+'].fileColumn2"  name="columnDtls['+datacount+'].fileColumn2" value="'+fileselect2+'" ></td>');
	

//	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'.matchCount"   name="columnDtls['+datacount+'].matchCount" value='+count+' ></td>');
	
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].stPadding"   name="columnDtls['+datacount+'].stPadding" value="'+staPdding1 +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].inStart_Char_Position"   name="columnDtls['+datacount+'].inStart_Char_Position" value="'+startPos1 +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].inEnd_char_position"   name="columnDtls['+datacount+'].inEnd_char_position" value="'+charsize1 +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].dataType"   name="columnDtls['+datacount+'].dataType" value="'+dataType1  +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].datpattern"  name="columnDtls['+datacount+'].datpattern" value="'+datpattern1 +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].stRelaxParam1"  name="columnDtls['+datacount+'].stRelaxParam1" value="'+relaxparam1 +'" ></td>');
		
	
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].stPadding2"  name="columnDtls['+datacount+'].stPadding2" value="'+staPdding2 +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].inStart_Char_Position2" name="columnDtls['+datacount+'].inStart_Char_Position2" value="'+startPos2  +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].inEnd_char_position2" name="columnDtls['+datacount+'].inEnd_char_position2" value="'+charsize2  +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].dataType2" name="columnDtls['+datacount+'].dataType2" value="'+dataType2 +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].datpattern2" name="columnDtls['+datacount+'].datpattern2" value="'+datpattern2 +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].stRelaxParam2"  name="columnDtls['+datacount+'].stRelaxParam2" value="'+relaxparam2 +'" ></td>');
	
	$row.append('<td align="center" class="lD"><img alt="Logo" id="del'+datacount+'" onclick="return deleteRow(row'+datacount+');" class="deldtl" src="images/delete.png" title="Delete"  style="vertical-align:middle; height: 20px; width: 20px;" /></td>');
	
		}
		resetvalues();
	}else{ 
		
		return false;
	}
}


function deleteRow(id) {
	
	
	$(id).remove();
	
	
}

function validate() {
	
	
	var datacount = document.getElementById("datacount").value;
	//var layerCount =document.getElementById("layerCount").value;
	
	var fileselect1 = document.getElementById("fileselect1").value;
	var staPdding1 = document.getElementById("staPdding1").value;
	var startPos1 = document.getElementById("startPos1").value;
	var charsize1 = document.getElementById("charsize1").value;
	var dataType1  =document.getElementById("dataType1").value;
	var datpattern1 =document.getElementById("datpattern1").value;
	var relaxParam1 = document.getElementById("stRelaxParam1").value;

		

	var fileselect2 =document.getElementById("fileselect2").value;
	var staPdding2 =document.getElementById("staPdding2").value;
	var startPos2 =document.getElementById("startPos2").value;
	var charsize2 =document.getElementById("charsize2").value;
	var dataType2 =document.getElementById("dataType2").value;
	var datpattern2 =document.getElementById("datpattern2").value;
	var relaxParam2 = document.getElementById("stRelaxParam2").value;

	

	var msg = ""
	/*if(layerCount==3){
		
		if(fileselect1==""||fileselect2==""||fileselect3=="") {
			
			msg=msg+"Please Select Columns \n";
		}
		if(dataType1!="") {
			
			if(datpattern1==""){
				
				msg=msg+"Please Enter Data pattern for file1 header \n";
			}
		}
		if(dataType2!="") {
			
			if(datpattern2==""){
				
				msg=msg+"Please Enter Data pattern for file2 header \n";
			}
		}if(dataType3!="") {
			
			if(datpattern2==""){
				
				msg=msg+"Please Enter Data pattern for file3 header \n";
			}
		}
		if(staPdding1==""||staPdding2==""||staPdding3==""){
			
			msg=msg+"Please Select Padding \n";
			
		}
		
	}else*/ {
		
			if(fileselect1==""||fileselect2=="") {
			

				msg=msg+"Please Select Columns \n";
				
			} 
			/*if(dataType1!="") {
				
				if(datpattern1==""){
					
					msg=msg+"Please Enter Data pattern for file1 header \n";
				}
			}if(dataType2!="") {
				
				if(datpattern2==""){
					
					msg=msg+"Please Enter Data pattern for file2 header \n";
				}
			}
			*/if(staPdding1==""||staPdding2==""){
				
				msg=msg+"Please Select Padding \n";
			}
			if(relaxParam1 == "" || relaxParam2 == "")
			{
				msg=msg+"Please Select Relax Parameter \n";
				
			}
			if(relaxParam1 != relaxParam2)
			{
				msg=msg+"Please Select Same Relax Parameters \n";
			}
	}
	
	
	if(msg!="") {
		
		alert(msg);
		return false;
	}else{
		return true;
	}
	
	
}


function saveData(){
	
	
	var datacount= parseInt($.trim($('#datacount').val()), 10);
	var msg="";

	
	var reconform= document.getElementById("reconform");
	
	if(datacount<1) {
		
		
		msg= msg+"Please Configure Columns";
		alert(msg);
		return false;
		
	}else {
		
	reconform.submit();

		return true;
	}
	
	
}
	$('.deldtl').hover(function(){
		//$(this).closest('tr').css('background','#e8eaef');
		$(this).closest('tr').addClass('hoverRow');
	}, function(){
		//$(this).closest('tr').css('background','');
		$(this).closest('tr').removeClass('hoverRow');
	});
	
	

function setFirstvalue(){
	
	
	var datacount = document.getElementById("datacount").value;
	var fileColumn1 ="columnDtls["+datacount+"].fileColumn1";

	
	var fileColumn2 ="columnDtls["+datacount+"].fileColumn2";
	var stPadding = "columnDtls["+datacount+"].stPadding";
	var inStart_Char_Position=  "columnDtls["+datacount+"].inStart_Char_Position";
	var inEnd_char_position= "columnDtls["+datacount+"].inEnd_char_position";
	var dataType= "columnDtls["+datacount+"].dataType";
	var datpattern= "columnDtls["+datacount+"].datpattern";
	var relaxParam1 = "columnDtls["+datacount+"].RelaxParam1";
	
	
	
	var stPadding2= "columnDtls["+datacount+"].stPadding2";
	var inStart_Char_Position2= "columnDtls["+datacount+"].inStart_Char_Position2";
	var inEnd_char_position2= "columnDtls["+datacount+"].inEnd_char_position2";
	var dataType2= "columnDtls["+datacount+"].dataType2";
	var datpattern2= "columnDtls["+datacount+"].datpattern2";
	var relaxParam2 = "columnDtls["+datacount+"].RelaxParam2";
	
	
	
	var matchCount = "columnDtls["+datacount+"].matchCount";

	
	var fileselect1 = document.getElementById("fileselect1").value;
	var staPdding1 = document.getElementById("staPdding1").value;
	var startPos1 = document.getElementById("startPos1").value;
	var charsize1 = document.getElementById("charsize1").value;
	var dataType1  =document.getElementById("dataType1").value;
	var datpattern1 =document.getElementById("datpattern1").value;
	var Relax_param1 = document.getElementById("stRelaxParam1").value;

	var fileselect2 =document.getElementById("fileselect2").value;
	var staPdding2 =document.getElementById("staPdding2").value;
	var startPos2 =document.getElementById("startPos2").value;
	var charsize2 =document.getElementById("charsize2").value;
	var valdataType2 =document.getElementById("dataType2").value;
	var valdatpattern2 =document.getElementById("datpattern2").value;
	var Relax_Param2 = document.getElementById("stRelaxParam2").value;
	

	
	//var count =document.getElementById("reconcount").value;
	//var layerCount =document.getElementById("layerCount").value;
	
	//document.getElementById(matchCount).value = count;
	document.getElementById(fileColumn1).value = fileselect1;
	document.getElementById(stPadding).value = staPdding1;
	document.getElementById(inStart_Char_Position).value= startPos1 ;
	document.getElementById(inEnd_char_position).value= charsize1 ;
	document.getElementById(dataType).value = dataType1;
	document.getElementById(datpattern).value=datpattern1;
	document.getElementById(relaxParam1).value = Relax_param1;

	document.getElementById(fileColumn2).value = fileselect2;
	document.getElementById(stPadding2).value = staPdding2;
	document.getElementById(inStart_Char_Position2).value= startPos2 ;
	document.getElementById(inEnd_char_position2).value= charsize2 ;
	document.getElementById(dataType2).value = valdataType2;
	document.getElementById(datpattern2).value=valdatpattern2;
	document.getElementById(relaxParam2).value = Relax_Param2;

	
	
	
	
	document.getElementById(fileColumn1).style.display=""
	document.getElementById(fileColumn2).style.display=""
	

	
}

function resetvalues(){
	
	
	document.getElementById("fileselect1").value="";
	document.getElementById("staPdding1").value="";
	document.getElementById("startPos1").value="0";
	document.getElementById("charsize1").value="0";
	document.getElementById("dataType1").value="";
	document.getElementById("datpattern1").value="";
	document.getElementById("stRelaxParam1").value = "";
	

	document.getElementById("fileselect2").value="";
	document.getElementById("staPdding2").value="";
	document.getElementById("startPos2").value="0";
	document.getElementById("charsize2").value="0";
	document.getElementById("dataType2").value="";
	document.getElementById("datpattern2").value="";
	document.getElementById("stRelaxParam2").value = "";

	
	
	//var count = parseInt($.trim($('#reconcount').val()), 10);
	var datacount = parseInt($.trim($('#datacount').val()), 10);
	
	/*document.getElementById("reconcount").value = count+1;
	document.getElementById("reconcount2").value = count+1;
	document.getElementById("reconcount3").value = count+1;*/
	
	
	document.getElementById("datacount").value = datacount+1;
	
}


function chngval1(e) {
	
	if(e.checked){
		
		document.getElementById("file1match").value='Y';
	}else{
		
		document.getElementById("file1match").value='N';
	}
	
}


function chngval2(e) {
	
if(e.checked){
		
		document.getElementById("file2match").value='Y';
	}else{
		
		document.getElementById("file2match").value='N';
	}
	
}










