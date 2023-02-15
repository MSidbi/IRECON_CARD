function displayContent(){
	
	/*var layer= e.value;*/
	clearvalue();
		
		document.getElementById("file1").style.display="";
		document.getElementById("file2").style.display="";
		
		document.getElementById("trfile1match").style.display="";
		document.getElementById("trfile2match").style.display="";
		
		
		
	
	
	
}

function displayDtl(e){
	 
	debugger;
	//var count = document.getElementById("layerCount").value;
	var category = document.getElementById("category").value;
	
	if(category==""){
		
		alert("Please Select category for recon");
		return false;
		
	}
		
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
				
				if(e.value=="SET RECON PARAM")
					{ 
					
					var selectId = document.getElementById("setup_dtl_list[0].inFileId");
					$(selectId).empty();
					
						var seloption = document.createElement('option');
						seloption.value = "0";
						seloption.text = "--Select--";
						
						var option1 = document.createElement('option');
						option1.value = document.getElementById("compareFile1").value;
						var compfile1 =document.getElementById("compareFile1") 
						option1.text = compfile1.options[compfile1.selectedIndex].text;;
						
						var option2 = document.createElement('option');
						option2.value =  document.getElementById("compareFile2").value ;
						var compfile2 =document.getElementById("compareFile2") 
						option2.text = compfile2.options[compfile2.selectedIndex].text;
						selectId.appendChild(seloption);
						selectId.appendChild(option1);
						selectId.appendChild(option2);
					
					document.getElementById("id3").style.display="block";
					document.getElementById("reconsave").style.display="block";
					document.getElementById("compareFile1").readonly=true;
					document.getElementById("compareFile2").readonly=true;
					
					e.value="SET MATCHING PARAM";
					}
			
			/*document.getElementById("id1").style.display="none";*/
				else if(e.value=="SET MATCHING PARAM"){
					
					var td = $('#ReconTbl').find('td');
					
					//alert(td.text());
					var chkd= true;
					$(td).find("input:text,select").each(function() {
				       var textVal = this.value;
				       var tdclass = this.className;
				      /* alert(textVal);
				       alert(tdclass);*/
				       
				      if((textVal=="" && tdclass!="srch_pattern")||textVal=="0" && textVal=="select") {
				    	  
				    	  alert("Please Fill all the records");
				    	  return false;
				      }
				   
				    });
				setReconHeader();
				e.style.display="none";
				document.getElementById("id3").style.display="none";
				document.getElementById("id2").style.display="block";
				document.getElementById("reconsave").style.display="block";
				}
			}
			
		}
		
		
		
	
}

function setValue(e) {
	
	var id = e.id;
	var selectid="";
	var value = e.value;
	var size=1;
	var txtVal =  $('#'+id+' :selected').text();
	
	if(id=="compareFile1") {
		
		selectid ="fileColumn1";
		document.getElementById("compreFileName1").value = txtVal;
		
	}if(id=="compareFile2") {
		
		selectid ="fileColumn2";
		size=2;
		document.getElementById("compreFileName2").value = txtVal;
		
	}
	if(value!="0")
		{
	
		
			$.ajax({
			 
			 type:"POST",
			 url :'GetHeaderList.do',
			 data:{fileId:value},
			 success:function(hdrlist){
				 
				 if(hdrlist!="error"){
					
					 var headerlist ="headerlist"+size
					 document.getElementById(headerlist).value="";
					 document.getElementById(headerlist).value = hdrlist;
					 clearvalue();
						
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

function getFiles() {
	
	debugger;
	var category = document.getElementById("Categry").value;
	var subcat = document.getElementById("SubCat").value;
	
	if(category!="") { 
	$.ajax({
		 
		 type:'POST',
		 url :'getFiledetails.do',
		 data:{category:category,subcat:subcat},
		 success:function(response){
			 
			
			// response = $.parseJSON(response);
			 var length =response.Records.length;
			
			 var compareFile1 = document.getElementById("compareFile1");
			 var compareFile2 = document.getElementById("compareFile2") ;
			 
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
			
			 document.getElementById("trbtn").style.display="none";
			 document.getElementById("Categry").disabled="disabled";
			 document.getElementById("SubCat").disabled="disabled";
			 displayContent();
			
			 

		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });
	}else {
		
		
		alert("Please Select Category.")
		
	}
	
	
}





function createView() {
	
	var count = document.getElementById("layerCount").value;
	
	if(count!=0) {
	
		var tbl = document.getElementById("coldtl");
		
		
		
		var rowcount = tbl.rows.length;
	
			for(var j =1;j<=count;j++ )
			{
				tbl.removeChild(tbl.lastChild);
			}
		var tr = document.createElement("tr");
		tr.id="tr1";
		tr.style.width="100%";
		for(var lnth=1;lnth<=count;lnth++){
			
			var th = document.createElement("th");
			var td = document.createElement("td");
			td.style.width="50%";
			th.appendChild(document.createTextNode("File"+lnth+"Headers"));
			
			td.appendChild(th);
			tr.appendChild(td);
		}
		var tr2 = document.createElement("tr");
		tr2.id="tr2";
		tr2.style.width="100%";
		
		for(var lnth=1;lnth<=count;lnth++){
			
			var td = document.createElement("td");
			td.style.width="50%";
			td.id="fileColumn"+lnth;
			td.align='center';
			tr2.appendChild(td);
		}
		tbl.appendChild(tr);
		tbl.appendChild(tr2);
	}else{
		
		
		alert("Please Select valid Layer");
		document.getElementById("coldtl").style.display="none";
		return false;
		
		
	}
	
	
} 


function setReconHeader(){
	
	debugger;
	//var size = document.getElementById("layerCount").value;  

	for(var count=1;count<=2;count++) {
	 var headerlist ="headerlist"+count
	  var hdrlist = document.getElementById(headerlist).value ;
		var splhdr="";
		
		/*if(count==3) {
			
			document.getElementById("ThirdFile").style.display="";
		}*/
		
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

function addreconRow() {
	

	debugger
	
	var td = $('#ReconTbl').find('td')
	
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
	
	
		var count = parseInt($.trim($('#reconParam').val()), 10);
		count=count+1;
		$('#reconParam').val(count);
		var $row = $('<tr id="row'+count+'" class="oddRow" />');
		
			
	
		var compfile1 =document.getElementById("compareFile1");
		var compfile2 =document.getElementById("compareFile2");
		var text1 = compfile1.options[compfile1.selectedIndex].text;
		var text2 = compfile2.options[compfile2.selectedIndex].text;

			
			
			$row.append('<td align="center" class="lD" style="width: 100px"><select style="width: 100px" id="setup_dtl_list['+count+'].inFileId" name="setup_dtl_list['+count+'].inFileId" onchange="setHeader(this);"><option value="0">--Select--</option><option value='+compfile1.value+'>'+text1+'</option><option value='+compfile2.value+'>'+text2+'</option></select></td>');
			$row.append('<td align="center" class="lD"><select id="setup_dtl_list['+count+'].table_header" name="setup_dtl_list['+count+'].table_header" style="width:180px" value="0" maxlength="6""><option value="">--Select--</option></td>');
			$row.append('<td align="center" class="lD" style="width: 160px"><input style="width: 160px" type="text" id="setup_dtl_list['+count+'].srch_Pattern" name="setup_dtl_list['+count+'].srch_Pattern" class="srch_pattern" value="0" maxlength="50" onkeypress="'+"setValueType(this,'search')"+'"></td>');
			$row.append('<td align="center" class="lD"><select id="setup_dtl_list['+count+'].stPadding" name="setup_dtl_list['+count+'].stPadding"><option value="">--Select--</option><option value="Y">Yes</option><option value="N">No</option></select></td>');
			$row.append('<td align="center" class="lD"><input type="text" id="setup_dtl_list['+count+'].start_charpos" name="setup_dtl_list['+count+'].start_charpos" class="char_pos" value="0" maxlength="6" onkeypress="'+"setValueType(this,'numeric')"+'"></td>');
			$row.append('<td align="center" class="lD"><input type="text" id="setup_dtl_list['+count+'].charsize" name="setup_dtl_list['+count+'].charsize" class="char_pos" value="0" maxlength="6" onkeypress="'+"setValueType(this,'numeric')"+'"></td>');
			$row.append('<td align="center" class="lD"><select id="setup_dtl_list['+count+'].condition" name="setup_dtl_list['+count+'].condition" cssClass="char_pos"><option value="">--Select--</option><option value="=">=</option><option value="!=">!=</option><option value="like">LIKE</option><option value=">">></option></select>');
			$row.append('<td align="center" class="lD"><select id="setup_dtl_list['+count+'].matchCondn" name="setup_dtl_list['+count+'].matchCondn"><option value="">--Select--</option><option value="Y">Yes</option><option value="N">No</option></select></td>');
			$row.append('<td align="center" class="lD"><img alt="Logo" id="del'+count+'" src="images/delete.png" title="Delete" onClick="delRow(row'+count+');"  style="vertical-align:middle; height: 20px; width: 20px;" /></td>');
			//<input class="'+"form-button"+'" type="button" id="del'+count+'" name="del'+count+'" class="delButton" value="Delete"></td>');
			
			$('#ReconTbl').append($row);	
		
	}
}

function delRow(e) {
	
	var count = parseInt($.trim($('#count').val()), 10);
	

	$(e).remove();
	
	
}



function setHeader(e){
	
	
	var count=document.getElementById("reconParam").value;
	var id = e.id;
	var value=e.value;
	if(e.value!= "0"){

	 
		var splhdr="";		
		var selectID = "setup_dtl_list["+count+"].table_header";
		$.ajax({
			 
			 type:"POST",
			 url :'GetHeaderList.do',
			 data:{fileId:value},
			 success:function(hdrlist){
				 
				 if(hdrlist!="error"){
					
					 var filhdr =  id.replace('inFileId','table_header');
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
	}
		


	
	
}









function addrow1(){
	
	var count = parseInt($.trim($('#count').val()), 10);
	var layercount= document.getElementById("layerCount").value;
	
	/**Check if all earlier provided travel dates are filled.*/

	
	

	/**Adding NewField Row.*/
	var tr_count = $('#coldtl').find('tr').length;
	//alert("tr_count "+tr_count);
	var rowClass="oddRow";
	if(tr_count % 2 == 0){
		rowClass = "evenRow";
	}

	var $row = $('<tr id="reconFile'+count+'" class="'+rowClass+'" />');
	
	for(var lngth=1;lngth<=layercount;lngth++){
		
		var headerlist = "headerlist"+lngth;
		var splhdr = document.getElementById(headerlist).value.split(',');
		var td = document.createElement('td');
		td.align='center';
		var select = document.createElement('select');
		select.id ='columnDtls'+count+'.fileColumn'+lngth;
		select.name = 'columnDtls'+count+'.fileColumn'+lngth;
		td.appendChild(select);
		
		var seloption = document.createElement('option');
		seloption.value = '' ;
		seloption.text = '---Select---';
		select.appendChild(seloption);
		
		for(var i=0; i<splhdr.length; i++ ){
			
			var option = document.createElement('option');
			option.value = splhdr[i] ;
			option.text = splhdr[i];
			select.appendChild(option);
			
		}
		$row.appendChild(td);
		
	} 
	$row.appendChild('<td align="center" class="lD"><img alt="Logo" class="reconDel" id="del'+count+'" src="images/delete.png" title="Delete"  style="vertical-align:middle; height: 20px; width: 20px;" /></td>');
	count += 1;
	$('#coldtl').appendChild($row);	

}


$(document).on("click",".reconDel", function(){
	//	alert("del clicked");
		var count = parseInt($('#count1').val(), 10);
		if(count <= 1){
			alert("Atleast one record is required.");
			return false;
		}
		$(this).closest('tr').remove();
		$('#count1').val($('#count1').val() - 1);
	//	alert("count is "+count);
		refreshList();
		refreshList();
	});

function validatedata() {
	
/*	var msg="";
	var td = $('#coldtl').find('td')
	
	//alert(td.text());
	var chkd= true;
	$(td).find("input:text,select").each(function() {
       var textVal = this.value;
       
      if(textVal==""||textVal=="select") {
    	  
    	  chkd= false;
      }
   
    });
	
	if(!(chkd)){
		
		msg=msg+"Please fill the all records.\n";
	}
	
	if(msg!=""){
		
		alert(msg);
		return false;
		
		
	}else {*/
	
		document.getElementById("id1").style.display="none";
		document.getElementById("id2").style.display="block";
	//}
	
	
	
	
	
}


function clearvalue(){
	
	
		document.getElementById("columnDtls[0].matchCount" ).value="0";
		document.getElementById("columnDtls[0].fileColumn1" ).value="0";
		document.getElementById("columnDtls[0].stPadding"  ).value="0";
		document.getElementById("columnDtls[0].inStart_Char_Position"  ).value="0";
		document.getElementById("columnDtls[0].inEnd_char_position"  ).value="0";
		document.getElementById("columnDtls[0].dataType"  ).value="0";
		document.getElementById("columnDtls[0].datpattern" ).value="0";
		
	
		document.getElementById("columnDtls[0].fileColumn2" ).value="0";
		document.getElementById("columnDtls[0].stPadding2" ).value="";
		document.getElementById("columnDtls[0].inStart_Char_Position2").value="0";
		document.getElementById("columnDtls[0].inEnd_char_position2").value="0";
		document.getElementById("columnDtls[0].dataType2").value="0";
		document.getElementById("columnDtls[0].datpattern2").value="0";

		
	
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

$('#prev2').click(function(){
	
	document.getElementById("id1").style.display="block";
	document.getElementById("id2").style.display="none";
	
});


