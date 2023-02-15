function addcolumn() {
	
	
	var fileId = document.getElementById("fileList").value;
	
	 var count1 = parseInt($.trim($('#count').val()), 10);
	
	 if(count1>1) {
		 
		 for(var i =2; i<=count1;i++)
			 
		 	{
			 var row= i-1;
			 var rowid='#row'+row;
			// $('#datatbl').parents(rowid).remove();
			 $('#row'+row).remove();
			
			 }
	 }
	
	
	 if(fileId=="0"){
		
		 var toAppend = document.getElementById('selectTd');
			toAppend.removeChild(toAppend.lastChild)
		return false;
	 }else {
		 $.ajax({
			 
			 type:"POST",
			 url :'GetHeaderList.do',
			 data:{fileId:fileId},
			 success:function(hdrlist){
				 
				 if(hdrlist!="error"){
					 
				 document.getElementById("headerlist").value=hdrlist;
				 
				 var splhdr = document.getElementById("headerlist").value.split(',');
					
					var toAppend = document.getElementById('selectTd');
					toAppend.removeChild(toAppend.lastChild)
					var select = document.createElement('select');
					select.id ='clasify_dtl_list[0].stHeader';
					select.name='clasify_dtl_list[0].stHeader';
					select.style.size="100px";
					toAppend.appendChild(select);
					
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
					
				 }else{
					
				 
				 }
			 },error: function(){
				
				 alert("Error Occured");
				 
			 },
			 
		 });
		 
	 }
	
} 

/*$('#addRow').click(function(){
	//alert("ADD ROW");
	
	var firsttd = $('selectTd').children().eq(0);
	var tdchild = firsttd.lastChild;
	var childvalue = tdchild.find('input').val();
	z
	if(tdchild !='' || tdchild != undefined)
	var stHeader = document.getElementById("comp_dtl_list0.stHeader");
	if(stHeader!=undefined)
	{
	document.getElementById("comphdr").value = document.getElementById("comp_dtl_list0.stHeader").value
	}
	
	

	
		try{
			var count = parseInt($.trim($('#count').val()), 10);
		
			*//**Check if all earlier provided travel dates are filled.*//*
		
			
			var splhdr = document.getElementById("headerlist").value.split(',');

			*//**Adding New Travel Date Field Row.*//*
			var tr_count = $('.dtl_table').find('tr').length;
			//alert("tr_count "+tr_count);
			var rowClass="oddRow";
			if(tr_count % 2 == 0){
				rowClass = "evenRow";
			}
		
			var $row = $('<tr id="row'+count+'" class="'+rowClass+'" />');
			
			var td = document.createElement('td');
			td.align='center';
			var select = document.createElement('select');
			select.id ='comp_dtl_list'+count+'.stHeader';
			select.name = 'comp_dtl_list['+count+'].stHeader';
			td.appendChild(select);
			
			var seloption = document.createElement('option');
			seloption.value = 'SELECT' ;
			seloption.text = '---Select---';
			select.appendChild(seloption);
			
			for(var i=0; i<splhdr.length; i++ ){
				
				var option = document.createElement('option');
				option.value = splhdr[i] ;
				option.text = splhdr[i];
				select.appendChild(option);
				
			}
			$row.append(td);
			
			var td1 = document.createElement('td');
			var input = document.createElement('input');
			input.type='text'
			input.id='comp_dtl_list'+count+'.stSearch_Pattern';
			input.name='comp_dtl_list'+count+'.stSearch_Pattern'
			input.class="srch_pattern" 
			input.maxlength="50"
			
			$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list'+count+'.stSearch_Pattern" name="comp_dtl_list['+count+'].stSearch_Pattern" onkeypress="'+"setValueType(this,'search')"+'" class="srch_pattern" maxlength="50"></td>');
			//$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list'+count+'.to_place" name="comp_dtl_list['+count+'].stPadding" class="place" maxlength="50"></td>');
			$row.append('<td align="center" class="lD"><select id="comp_dtl_list'+count+'.stPadding" name="comp_dtl_list['+count+'].stPadding"><option value="">- S E L E C T -</option><option value="Y">Yes</option><option value="N">No</option></select></td>');
			$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list'+count+'.inChar_Position" name="comp_dtl_list['+count+'].inChar_Position" class="char_pos" value="0" maxlength="6" onkeypress="'+"setValueType(this,'numeric')"+'"></td>');
			$row.append('<td align="center" class="lD"><select id="comp_dtl_list'+count+'.mode_of_conveyance" name="cnv_dtl_list['+count+'].mode_of_conveyance"><option value="">- S E L E C T -</option><option value="AUTO">Auto</option><option value="TWH">Two Wheeler</option><option value="TAXI">Taxi</option><option value="OWC">Own Car</option><option value="PT">Public Transport</option></select></td>');
			$row.append('<td align="center" class="lD"><input type="text" id="cnv_dtl_list'+count+'.amount" name="cnv_dtl_list['+count+'].amount" class="amt claim_amount" maxlength="16"></td>');
			$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list'+count+'.purpose" name="cnv_dtl_list['+count+'].purpose" class="purpose" maxlength="300"></td>');
			$row.append('<td align="center" class="lD"><img alt="Logo" id="del'+count+'" src="images/delete.png" title="Delete"  style="vertical-align:middle; height: 20px; width: 20px;" /></td>');
		<input class="'+"form-button"+'" type="button" id="del'+count+'" name="del'+count+'" class="delButton" value="Delete"></td>');
	
			count += 1;
			$('.dtl_table').append($row);	


			*//**Set Jquery Button CSS *//*
			$("input[type=submit], input[type=button]").button();

			*//**Function to Set Hover on newly Created DOM's. *//*
			$('.delButton').hover(function(){
				//$(this).closest('tr').css('background','#e8eaef');
				$(this).closest('tr').addClass('hoverRow');
			}, function(){
				//$(this).closest('tr').css('background','');
				$(this).closest('tr').removeClass('hoverRow');
			});

			*//**Reset the value of count with current effect.*//*
			$('#count').val(count);
		//	$('#acount').val(parseInt($('#acount').val())+1);
		}catch (e) {
			alert(e);
			return false;
		}
	}
});*/

function knockoffproceed(e){
	
	
	
	if(e.checked){
		
		
		document.getElementById("btnknckoff").style.display="";
		document.getElementById("knock_offFlag").value="Y";
		document.getElementById("submit").style.display="none";
		 
		
	}else{
		
		//clearfield();
		
		document.getElementById("btnknckoff").style.display="none";
		document.getElementById("knock_offFlag").value="N";
		document.getElementById("submit").style.display="";
		document.getElementById("id2").style.display="none";
		
		document.getElementById("count1").value=0;
		document.getElementById("count2").value=0;
		
	}
	
	
	
}

function validData(){
	
	debugger;
	var stCategory = document.getElementById('stCategory').value;
	var fileID = document.getElementById('fileList').value;
	var msg="";
	
	if(stCategory == ""){
		
		msg=msg+"Please select category.\n"
	}if(fileID == "0"){
		
		msg=msg+"Please select File.\n"
	}
	
	var td = $('#compareDetailstbl').find('td')
	
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

	
	if(msg!="") {
		
		
		alert(msg);
		return false;
	}else{
		
		/*var fileList = document.getElementById("fileList").value;
		alert(fileList);*/
	//this.options[this.selectedIndex].innerHTML
		//document.getElementById("stFileName").value = document.getElementById("fileList").innerHTML;
		//document.getElementById("stFileName").value = 'SWITCH';
		 var text = $("#fileList :selected").text();
		 //alert(text);
		 document.getElementById("stFileName").value = text;
		
		return true;
		
	}
	
}


function moveKnockOff(){
	
	
	
	if(validData()) {
	document.getElementById("fileList1").value = document.getElementById("fileList").value
	document.getElementById("fileList1").disabled="disabled";
	
	
	 
	 var splhdr = document.getElementById("headerlist").value.split(',');
		
	 createselect(splhdr)
	 
		
		var toAppend1 = document.getElementById('knckoffcrtselectTd');
	
		toAppend1.removeChild(toAppend1.lastChild)
		
		var crtselect = document.createElement('select');
		crtselect.id ='comp_dtl_list0.knockoff_header';
		crtselect.name = 'comp_dtl_list0.knockoff_header';
		crtselect.style.size="100px";
		
		toAppend1.appendChild(crtselect)
		var seloption1 = document.createElement('option');
		seloption1.value = '' ;
		seloption1.text = '---Select---';
		crtselect.appendChild(seloption1);
		
		
		for(var i=0; i<splhdr.length; i++ ){
			
			var option1 = document.createElement('option');
			option1.value = splhdr[i] ;
			option1.text = splhdr[i];
			crtselect.appendChild(option1);
			
		}
	
	
	
	
	/*$('#id1').css("display", "none");
	$('#tab1').css('background-color','Green') ;*/
	$('#id2').css("display", "block");
	}else{
		
		return false;
	}
	
}

function createselect(splhdr){
	
	var toAppend = document.getElementById('knockoffselectTd');
	
	toAppend.removeChild(toAppend.lastChild)
	var select = document.createElement('select');
	select.id ='comp_dtl_list0.knockoff_col';
	select.name ='comp_dtl_list0.knockoff_col';
	select.style.size="100px";
		
	toAppend.appendChild(select);
		
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
	
	
}

function clearfield(){
	
	 document.getElementById("stCategory").value="";
	 document.getElementById("clasify_dtl_list[0].stSearch_Pattern").value="";
	 document.getElementById("clasify_dtl_list[0].stPadding").value="";
	 document.getElementById("clasify_dtl_list[0].condition").value="";
	 document.getElementById("clasify_dtl_list[0].inStart_Char_Position").value="";
	 document.getElementById("clasify_dtl_list[0].inEnd_char_position").value="";
	 document.getElementById("id2").style.display="none";
	 document.getElementById("btnknckoff").style.display="none";
	 document.getElementById("knock_offFlag").value="N";
	 document.getElementById("submit").style.display="";
	 
	 document.getElementById("comp_dtl_list[0].knockoff_OrgVal").value="";
	 document.getElementById("comp_dtl_list[0].knockoff_comprVal").value="";
	 document.getElementById("comp_dtl_list[0].knockoffSrch_Pattern").value="";
	document.getElementById("comp_dtl_list[0].knockoff_stPadding").value="";
	document.getElementById("comp_dtl_list[0].knockoff_condition").value="";
	document.getElementById("comp_dtl_list[0].knockoffStart_Char_Pos").value="";
	document.getElementById("comp_dtl_list[0].knockoffEnd_char_pos").value="";
	document.getElementById("fileList").value=0;
	document.getElementById("stFileName").value=""
	document.getElementById("count").value=0;
	document.getElementById("count2").value=0;
	document.getElementById("count1").value=0;
	
	var knockoffcompareDetailstbl1 = document.getElementById("knockoffcompareDetailstbl1");
	var count = knockoffcompareDetailstbl1.rows.length
	try{
		for(var j =0;j<=count;j++ )
		{
			if(j > 1) {
			document.getElementById("knockoffcompareDetailstbl1").deleteRow(j)
			}
		
		}
		
	}catch (e) {
		
	}
	var knockoffcompareDetailstbl = document.getElementById("knockoffcompareDetailstbl");
	var count1 = knockoffcompareDetailstbl.rows.length
	try{
		for(var j =0;j<=count1;j++ )
		{
			if(j>1) {
			document.getElementById("knockoffcompareDetailstbl").deleteRow(j)
			}
		
		}
		
	}catch (e) {
		
	}
	
	var compareDetailstbl = document.getElementById("compareDetailstbl");
	var count2 = compareDetailstbl.rows.length
	try{
		for(var j =0;j<=count2;j++ )
		{
			if(j>1) {
			document.getElementById("compareDetailstbl").deleteRow(j)
			}
		
		}
		
	}catch (e) {
		
	}
	
	
	 
	
	
}


