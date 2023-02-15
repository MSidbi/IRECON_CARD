function savemanData(){
	
	debugger;
	var datacount= parseInt($.trim($('#datacount').val()), 10);
	var msg="";
	var category = document.getElementById("category").value;
	var count = parseInt($.trim($('#count').val()), 10);

	
	if(count < 1) {
		
		msg= msg+"Please add matching Details";
		alert(msg);
		
		return false;
		
	}else if(category==""){
		

		msg= msg+"Please select category" ;
		alert(msg);
		return false;
		
	}
	else if(datacount<1) {
		
		
		msg= msg+"Please add Compare Details";
		alert(msg);
		return false;
		
	}else {
	
		return true;
	}
	
	
}
	
	
	
//added by int6345 on 10/25/2017

function getFiles() {
	
	debugger;
	var category = document.getElementById("category").value;
	var subcat = "-";
	
	if(category!="") { 
	$.ajax({
		 
		 type:'POST',
		 url :'getFiledetails.do',
		 data:{category:category,subcat:subcat},
		 success:function(response){
			 
			
			// response = $.parseJSON(response);
			 var length =response.Records.length;
			
			 var compareFile1 = document.getElementById("Comp_File");
			 var compareFile2 = document.getElementById("Man_file");
			 
			 var rowcount = compareFile1.childNodes.length;
				
				for(var j =1;j<=rowcount;j++ )
				{
					compareFile1.removeChild(compareFile1.lastChild);
					compareFile2.removeChild(compareFile2.lastChild);
				}
			
			 var option1= document.createElement("option")
			 option1.value="0";
			 option1.text="--Select--";
			 var opt1= document.createElement("option")
			 opt1.value="0";
			 opt1.text="--Select--";
			 compareFile1.appendChild(option1);
			compareFile2.appendChild(opt1)
			 
			 for(var i =0;i<length;i++ ) {
				
				 var option= document.createElement("option")
				  option.value = response.Records[i].inFileId;
				 option.text= response.Records[i].stFileName;
				 compareFile1.appendChild(option);
			 }
			 for(var i =0;i<length;i++ ) {
					
				 var option= document.createElement("option")
				  option.value = response.Records[i].inFileId;
				 option.text= response.Records[i].stFileName;
					compareFile2.appendChild(option)
			 }
			
			 //document.getElementById("trbtn").style.display="none";
			 document.getElementById("category").disabled="disabled";
			 //document.getElementById("SubCat").disabled="disabled";
			 displayContent();
			
			 

		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });
	}else {
		
		
		alert("Please Select Category.");
		
	}
	
	
}





function validate() {
	
	var datacount = document.getElementById("datacount").value;
	var layerCount =2;
	
	var fileselect1 = document.getElementById("fileselect1").value;
	var staPdding1 = document.getElementById("staPdding1").value;
	var startPos1 = document.getElementById("startPos1").value;
	var charsize1 = document.getElementById("charsize1").value;
	var dataType1  =document.getElementById("dataType1").value;
	var datpattern1 =document.getElementById("datpattern1").value;
	
	var value1 = document.getElementById("value1").value;
	var condn1= document.getElementById("condn1").value;
	
	
	

	var fileselect2 =document.getElementById("fileselect2").value;
	var staPdding2 =document.getElementById("staPdding2").value;
	var startPos2 =document.getElementById("startPos2").value;
	var charsize2 =document.getElementById("charsize2").value;
	var dataType2 =document.getElementById("dataType2").value;
	var datpattern2 =document.getElementById("datpattern2").value;

	var value2 = document.getElementById("value2").value;
	var condn2= document.getElementById("condn2").value;


	
	var msg = ""
	
		
			if(fileselect1==""||fileselect2=="") {
			

				msg=msg+"Please Select Columns \n";
				
			} if(dataType1!="") {
				
				if(datpattern1==""){
					
					msg=msg+"Please Enter Data pattern for file1 header \n";
				}
			}if(dataType2!="") {
				
				if(datpattern2==""){
					
					msg=msg+"Please Enter Data pattern for file2 header \n";
				}
			}if(staPdding1==""||staPdding2==""){
				
				msg=msg+"Please Select Padding \n";
			}
	
	
	
	if(msg!="") {
		
		alert(msg);
		return false;
	}else{
		return true;
	}
	
	
}

function setcompvalue(e){
	
	
	document.getElementById("stComp_File").value=e.options[e.selectedIndex].text;
	document.getElementById("comp_File").value=e.value;
	
	if(e.value!="") {
		
		$.ajax({
			 
			 type:"POST",
			 url :'GetHeaderList.do',
			 data:{fileId:e.value},
			 success:function(hdrlist){
				 
				 if(hdrlist!="error"){
					
					 document.getElementById("headerlist1").value = hdrlist;
							
						
				 }
			 },error: function(){
				
				 alert("Error Occured");
				 
			 },
			 
		 });
		
	}
	
}
function setmanfileValue(e){
	
	
	document.getElementById("stMan_file").value=e.options[e.selectedIndex].text;
	document.getElementById("man_File").value=e.value;
	
		if(e.value!="") {
				
				$.ajax({
					 
					 type:"POST",
					 url :'GetHeaderList.do',
					 data:{fileId:e.value},
					 success:function(hdrlist){
						 
						 if(hdrlist!="error"){
							 
							 document.getElementById("headerlist2").value = hdrlist;
						 }
					 },error: function(){
						
						 alert("Error Occured");
						 
					 },
					 
				 });
				
			}
	
	
}

function setFile(e){
	
	debugger;
	//var count = document.getElementById("count").value;
	var Comp_File = document.getElementById("Comp_File").value;
	var Man_file =document.getElementById("Man_file").value;
	
	if(Comp_File!=Man_file) {
			
		if(e.value=="Set Match Value") {
		var selectId = document.getElementById("comp_dtl_list[0].refFileId");
		$(selectId).empty();
		
			var seloption = document.createElement('option');
			seloption.value = "";
			seloption.text = "--Select--";
			
			var option1 = document.createElement('option');
			option1.value = document.getElementById("comp_File").value;
			option1.text = document.getElementById("stComp_File").value;
			
			var option2 = document.createElement('option');
			option2.value =  document.getElementById("man_File").value ;
			option2.text = document.getElementById("stMan_file").value;
			selectId.appendChild(seloption);
			selectId.appendChild(option1);
			selectId.appendChild(option2);
			document.getElementById("matchdiv").style.display="";
			e.value="Add Compare Setup";
			document.getElementById("Comp_File").disabled="disabled";
			document.getElementById("Man_file").disabled="disabled";
			
		} else if(e.value="Add Compare Setup") {
			
	
			var td = $('#matchingtbl').find('td')
			
			//alert(td.text());
			var chkd= true;
			$(td).find("input:text,select").each(function() {
		       var textVal = this.value;
		       
		      if(textVal==""||textVal=="select") {
		    	  
		    	  chkd= false;
		      }
		   
		    });
			
			if(chkd){
			
			var headerlist1= document.getElementById("headerlist1").value;
			var headerlist2= document.getElementById("headerlist2").value;
			
			 var select1 = document.getElementById("fileselect1");
				// $('#comp_dtl_list[0].refFileHdr').empty();
				 $(select1).empty();
				 var option1 = document.createElement('option');
					option1.value = "" ;
					option1.text = "--SELECT--";
					
			
				splhdr = headerlist1.split(',');
				select1.appendChild(option1);
				for(var i=0; i<splhdr.length; i++ ){
					var option = document.createElement('option');
					option.value = splhdr[i] ;
					option.text = splhdr[i];
					select1.appendChild(option);
				}
				
				 var select2 = document.getElementById("fileselect2");
					// $('#comp_dtl_list[0].refFileHdr').empty();
					 $(select2).empty();
					 var option2 = document.createElement('option');
						option2.value = "" ;
						option2.text = "--SELECT--";
						select2.appendChild(option2);
				
					splhdr = headerlist2.split(',');
					for(var i=0; i<splhdr.length; i++ ){
						var option = document.createElement('option');
						option.value = splhdr[i] ;
						option.text = splhdr[i];
						select2.appendChild(option);
					}
			
			
					e.disabled="disabled"
					document.getElementById("matchdiv").style.display="none";
					document.getElementById("cmpdiv").style.display="";
			}else {
				
				alert("Please fill the all records.")
			}
			
		
		} 
		
	}else {
		
		alert("Please Select Different File")
	}
		
	
	
	
}


function setManValue(e) {
	
	var id = e.id;
	
	var value = e.value;
	var size=1;
	var count = document.getElementById("count").value;
	
	if(value!="")
		{
	
		
			$.ajax({
			 
			 type:"POST",
			 url :'GetHeaderList.do',
			 data:{fileId:value},
			 success:function(hdrlist){
				 
				 if(hdrlist!="error"){
					
					 var filhdr =  id.replace('refFileId','refFileHdr');
					 var select = document.getElementById(filhdr);
					// $('#comp_dtl_list[0].refFileHdr').empty();
					 $(select).empty();
					 var option = document.createElement('option');
						option.value = "" ;
						option.text = "--SELECT--";
						select.appendChild(option);
						splhdr = hdrlist.split(',');
						for(var i=0; i<splhdr.length; i++ ){
							
							var option = document.createElement('option');
							option.value = splhdr[i] ;
							option.text = splhdr[i];
							select.appendChild(option);
							
						}
				 }
			 },error: function(){
				
				 alert("Error Occured");
				 
			 },
			 
		 });
	}else{
		
		alert("Please Select Valid File");
		
		return false;
	}
	
}


function addManRow() {
	
	var td = $('#matchingtbl').find('td')
	
	//alert(td.text());
	var chkd= true;
	$(td).find("input:text,select").each(function() {
       var textVal = this.value;
       
      if(textVal==""||textVal=="select") {
    	  
    	  chkd= false;
      }
   
    });
	
	if(!(chkd)){
		var msg="";
		 msg=msg+"Please fill the all records.\n";
		alert(msg);
	}else{
	
	
		var count = parseInt($.trim($('#count').val()), 10);
		count=count+1;
		$('#count').val(count);
		var $row = $('<tr id="row'+count+'" class="oddRow" />');
		
			
		
			var comp_File = document.getElementById("comp_File").value;
			var stComp_File = document.getElementById("stComp_File").value;
			
		
			var man_File =  document.getElementById("man_File").value ;
			var stMan_file = document.getElementById("stMan_file").value;
			
			
			$row.append('<td align="center" class="lD"><select id="comp_dtl_list['+count+'].refFileId" name="comp_dtl_list['+count+'].refFileId" onchange="setManValue(this);"><option value="0">--Select--</option><option value='+comp_File+'>'+stComp_File+'</option><option value='+man_File+'>'+stMan_file+'</option></select></td>');
			$row.append('<td align="center" class="lD"><select id="comp_dtl_list['+count+'].refFileHdr" name="comp_dtl_list['+count+'].refFileHdr" style="width:180px" value="0" maxlength="6""><option value="">--Select--</option></td>');
			$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list['+count+'].stSearch_Pattern" name="comp_dtl_list['+count+'].stSearch_Pattern" class="srch_pattern" value="0" maxlength="50" onkeypress="'+"setValueType(this,'search')"+'"></td>');
			$row.append('<td align="center" class="lD"><select id="comp_dtl_list['+count+'].stPadding" name="comp_dtl_list['+count+'].stPadding"><option value="">--Select--</option><option value="Y">Yes</option><option value="N">No</option></select></td>');
			$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list['+count+'].stChar_Pos" name="comp_dtl_list['+count+'].stChar_Pos" class="char_pos" value="0" maxlength="6" onkeypress="'+"setValueType(this,'numeric')"+'"></td>');
			$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list['+count+'].stChar_Size" name="comp_dtl_list['+count+'].stChar_Size" class="char_pos" value="0" maxlength="6" onkeypress="'+"setValueType(this,'numeric')"+'"></td>');
			$row.append('<td align="center" class="lD"><select id="comp_dtl_list['+count+'].condition" name="comp_dtl_list['+count+'].condition" cssClass="char_pos"><option value="">--Select--</option><option value="=">=</option><option value="!=">!=</option><option value="like">LIKE</option></select>');
			$row.append('<td align="center" class="lD"><img alt="Logo" id="del'+count+'" src="images/delete.png" title="Delete" onClick="delRow(row'+count+');"  style="vertical-align:middle; height: 20px; width: 20px;" /></td>');
			//<input class="'+"form-button"+'" type="button" id="del'+count+'" name="del'+count+'" class="delButton" value="Delete"></td>');
			
			$('#matchingtbl').append($row);	
		
	}
}

function delRow(e) {
	
	var count = parseInt($.trim($('#count').val()), 10);
	

	$(e).remove();
	
	
}

function delcompRow(e) {
	var datacount = parseInt($.trim($('#datacount').val()), 10);
	$(e).remove();
	//$('#datacount').val(datacount-1);
}


function compRow() {
	

	
	
	
	//var layerCount =document.getElementById("layerCount").value;
	var fileselect1 = document.getElementById("fileselect1").value;
	var staPdding1 = document.getElementById("staPdding1").value;
	var startPos1 = document.getElementById("startPos1").value;
	var charsize1 = document.getElementById("charsize1").value;
	var dataType1  =document.getElementById("dataType1").value;
	var datpattern1 =document.getElementById("datpattern1").value;
	

	var fileselect2 =document.getElementById("fileselect2").value;
	var staPdding2 =document.getElementById("staPdding2").value;
	var startPos2 =document.getElementById("startPos2").value;
	var charsize2 =document.getElementById("charsize2").value;
	var dataType2 =document.getElementById("dataType2").value;
	var datpattern2 =document.getElementById("datpattern2").value;
	var condn1 = document.getElementById("condn1").value;
	var condn2 = document.getElementById("condn2").value;
	var count = parseInt($.trim($('#reconcount').val()), 10);
	var datacount = parseInt($.trim($('#datacount').val()), 10);
	
	var detailtbl = document.getElementById("detailtbl");
	
	
	if(validate()) {
		
		
		if(datacount==0) {
			
			
			setFirstvalue();
			$('#datacount').val(datacount+1);
			document.getElementById("rowid").value="display";
			detailtbl.style.display="";
			
		}
		else{
	var $row = $('<tr id="comprow'+datacount+'" class="even" />');
	
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
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].condn1" name="columnDtls['+datacount+'].condn1" value="'+condn1 +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].strValue1" name="columnDtls['+datacount+'].strValue1" value="'+value1 +'" ></td>');
	
	
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].stPadding2"  name="columnDtls['+datacount+'].stPadding2" value="'+staPdding2 +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].inStart_Char_Position2" name="columnDtls['+datacount+'].inStart_Char_Position2" value="'+startPos2  +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].inEnd_char_position2" name="columnDtls['+datacount+'].inEnd_char_position2" value="'+charsize2  +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].dataType2" name="columnDtls['+datacount+'].dataType2" value="'+dataType2 +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].datpattern2" name="columnDtls['+datacount+'].datpattern2" value="'+datpattern2 +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].condn2" name="columnDtls['+datacount+'].condn2" value="'+condn2 +'" ></td>');
	$row.append('<td align="center" style="display:none;" class="lD"><input type="hidden" id="columnDtls['+datacount+'].strValue2" name="columnDtls['+datacount+'].strValue2" value="'+value2 +'" ></td>');
	
	
	$row.append('<td align="center" class="lD"><img alt="Logo" id="del'+datacount+'" onclick="return delcompRow(comprow'+datacount+');" class="deldtl" src="images/delete.png" title="Delete"  style="vertical-align:middle; height: 20px; width: 20px;" /></td>');
	
	$('#datacount').val(datacount+1);
		}
		
	}else{ 
		
		return false;
	}

	
}


function clearManvalue(){
	
	
	document.getElementById("columnDtls[0].matchCount" ).value="0";
	document.getElementById("columnDtls[0].fileColumn1" ).value="0";
	document.getElementById("columnDtls[0].stPadding"  ).value="0";
	document.getElementById("columnDtls[0].inStart_Char_Position"  ).value="0";
	document.getElementById("columnDtls[0].inEnd_char_position"  ).value="0";
	document.getElementById("columnDtls[0].dataType"  ).value="0";
	document.getElementById("columnDtls[0].datpattern" ).value="0";
	document.getElementById("columnDtls[0].strValue1" ).value="" ;
	document.getElementById("columnDtls[0].condn1" ).value=""; 		

	document.getElementById("columnDtls[0].fileColumn2" ).value="0";
	document.getElementById("columnDtls[0].stPadding2" ).value="";
	document.getElementById("columnDtls[0].inStart_Char_Position2").value="0";
	document.getElementById("columnDtls[0].inEnd_char_position2").value="0";
	document.getElementById("columnDtls[0].dataType2").value="0";
	document.getElementById("columnDtls[0].datpattern2").value="0";
	document.getElementById("columnDtls[0].strValue2" ).value="" ;
	document.getElementById("columnDtls[0].condn2" ).value="";
	
	
	document.getElementById("fileselect1").value="";
	document.getElementById("staPdding1").value="";
	document.getElementById("startPos1").value="0";
	document.getElementById("charsize1").value="0";
	document.getElementById("dataType1").value="";
	document.getElementById("datpattern1").value="";
	

	document.getElementById("fileselect2").value="";
	document.getElementById("staPdding2").value="";
	document.getElementById("startPos2").value="0";
	document.getElementById("charsize2").value="0";
	document.getElementById("dataType2").value="";
	document.getElementById("datpattern2").value="";


	document.getElementById("fileselect3").value="";
	document.getElementById("staPdding3").value="";
	document.getElementById("startPos3").value="0";
	document.getElementById("charsize3").value="0";
	document.getElementById("dataType3").value="";
	document.getElementById("datpattern3").value="";
	document.getElementById("value1").value="";
	document.getElementById("value2").value="";
	document.getElementById("value3").value="";
	document.getElementById("condn1").value="";
	document.getElementById("condn2").value="";
	document.getElementById("condn3").value="";
	
	document.getElementById("datacount").value = "0";
	document.getElementById("detailtbl").style.display="none";
	document.getElementById("id2").style.display="none";
	var hdrtbl = document.getElementById("detailtbl");
	var count = hdrtbl.rows.length
	try{
		for(var j =0;j<=count;j++ )
		{
			if(j > 1 ){
			document.getElementById("detailtbl").deleteRow(j)
			}
			
			
		}
		
	}catch (e) {
		
	}

}



function setReconHeader(){
	
	var size = 2;  

	for(var count=1;count<=size;count++) {
	 var headerlist ="headerlist"+count
	  var hdrlist = document.getElementById(headerlist).value ;
		var splhdr="";
		
		
		
		var selectID = 'fileselect'+count;
		$('#'+selectID).empty();
		var select = document.getElementById(selectID);
		var option = document.createElement('option');
		option.value = "" ;
		option.text = "--SELECT--";
		select.appendChild(option);
		splhdr = hdrlist.split(',');
		for(var i=0; i<splhdr.length; i++ ){
			
			var option = document.createElement('option');
			option.value = splhdr[i] ;
			option.text = splhdr[i];
			select.appendChild(option);
			
		}
	}
		

}




function displaymanDtl(){
	
	var count ="2";
	var category = document.getElementById("category").value;
	
	if(category=""){
		
		alert("Please Select category for recon");
		return false;
		
	}
	if(count=="0") {
		
		alert("Please Select layers for recon");
		return false;
		
	}if(count=="2"){
		
		var file1= document.getElementById("compareFile1").value;
		var file2= document.getElementById("compareFile2").value;

		if(file1==0||file2==0){
			
			alert("Please Select File For Recon"  );
			return false;
		}else{
			if(file1==file2) {
				
				alert("Select Different File.");
				return false;
			}else{
			setReconHeader()
			
						
			document.getElementById("id2").style.display="block";
			document.getElementById("reconsave").style.display="block";
			
			//document.getElementById("coldtl").style.display="";
		
			///	document.getElementById("nxt1").style.display="";
			
			
			document.getElementById("category").readonly=true;
			document.getElementById("compareFile1").readonly=true;
			document.getElementById("compareFile2").readonly=true;
			
			/*document.getElementById("id1").style.display="none";*/
			document.getElementById("id2").style.display="block";
			document.getElementById("reconsave").style.display="block";
			}
			
		}
		
		
		
	} 
}

function setFirstvalue(){
	
	var datacount = document.getElementById("datacount").value;
	var fileColumn1 ="columnDtls["+datacount+"].fileColumn1";
	
	var fileColumn2 ="columnDtls["+datacount+"].fileColumn2";
	var stPadding = "columnDtls["+datacount+"].stPadding";
	var inStart_Char_Position= "columnDtls["+datacount+"].inStart_Char_Position";
	var inEnd_char_position= "columnDtls["+datacount+"].inEnd_char_position";
	var dataType= "columnDtls["+datacount+"].dataType";
	var datpattern= "columnDtls["+datacount+"].datpattern";
	var matchCondn1= "columnDtls["+datacount+"].matchCondn1";
	var strCondn1= "columnDtls["+datacount+"].condn1";
	
	
	var stPadding2= "columnDtls["+datacount+"].stPadding2";
	var inStart_Char_Position2= "columnDtls["+datacount+"].inStart_Char_Position2";
	var inEnd_char_position2= "columnDtls["+datacount+"].inEnd_char_position2";
	var dataType2= "columnDtls["+datacount+"].dataType2";
	var datpattern2= "columnDtls["+datacount+"].datpattern2";
	var matchCondn2 = "columnDtls["+datacount+"].matchCondn2";
	var strCondn2= "columnDtls["+datacount+"].condn2";
	var strValue2= "columnDtls["+datacount+"].strValue2";
	var matchCount = "columnDtls["+datacount+"].matchCount";
	
	var fileselect1 = document.getElementById("fileselect1").value;
	var staPdding1 = document.getElementById("staPdding1").value;
	var startPos1 = document.getElementById("startPos1").value;
	var charsize1 = document.getElementById("charsize1").value;
	var dataType1  =document.getElementById("dataType1").value;
	var datpattern1 =document.getElementById("datpattern1").value;
	var value1 =document.getElementById("value1").value;
	var Condn1= document.getElementById("condn1").value;

	var fileselect2 =document.getElementById("fileselect2").value;
	var staPdding2 =document.getElementById("staPdding2").value;
	var startPos2 =document.getElementById("startPos2").value;
	var charsize2 =document.getElementById("charsize2").value;
	var valdataType2 =document.getElementById("dataType2").value;
	var valdatpattern2 =document.getElementById("datpattern2").value;
	var value2 =document.getElementById("value2").value;
	var Condn2= document.getElementById("condn2").value;
	//var count =document.getElementById("reconcount").value;
	
	//document.getElementById(matchCount).value = count;
	document.getElementById(fileColumn1).value = fileselect1;
	document.getElementById(stPadding).value = staPdding1;
	document.getElementById(inEnd_char_position).value= charsize1 ;
	document.getElementById(dataType).value = dataType1;
	document.getElementById(datpattern).value=datpattern1;
	document.getElementById(inStart_Char_Position).value=startPos1;
	
	//document.getElementById(matchCondn1).value=strmatchCondn1;
	document.getElementById(strCondn1).value=Condn1;

	document.getElementById(inStart_Char_Position2).value=startPos2;
	document.getElementById(fileColumn2).value = fileselect2;
	document.getElementById(stPadding2).value = staPdding2;
	document.getElementById(inEnd_char_position2).value= charsize2 ;
	document.getElementById(dataType2).value = valdataType2;
	document.getElementById(datpattern2).value=valdatpattern2;
	//document.getElementById(matchCondn2).value=strmatchCondn2;
	document.getElementById(strCondn2).value=Condn2;

	document.getElementById(fileColumn1).style.display=""
	document.getElementById(fileColumn2).style.display=""
	
	
}
	
