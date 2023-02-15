function addknockoffRow(){
	

	

	//alert("ADD ROW");
	
/*	var firsttd = $('selectTd').children().eq(0);
	var tdchild = firsttd.lastChild;
	var childvalue = tdchild.find('input').val();*/
	
	/*if(tdchild !='' || tdchild != undefined)*/
	var stHeader = document.getElementById("comp_dtl_list0.stHeader");
	if(stHeader!=undefined)
	{
	document.getElementById("comphdr").value = document.getElementById("comp_dtl_list0.stHeader").value
	}
	
	

	
		try{
			var count = parseInt($.trim($('#count1').val()), 10);
		
			count += 1;
			/**Check if all earlier provided travel dates are filled.*/
		
			
			var splhdr = document.getElementById("headerlist").value.split(',');

			/**Adding New Travel Date Field Row.*/
			var tr_count = $('.dtl_table1').find('tr').length;
			//alert("tr_count "+tr_count);
			var rowClass="oddRow";
			if(tr_count % 2 == 0){
				rowClass = "evenRow";
			}
		
			var $row = $('<tr id="knockrow'+count+'" class="'+rowClass+'" />');
			
			var td = document.createElement('td');
			td.align='center';
			var select = document.createElement('select');
			select.id ='comp_dtl_list'+count+'.knockoff_col';
			select.name = 'comp_dtl_list['+count+'].knockoff_col';
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
			$row.append(td);
			
		/*	var td1 = document.createElement('td');
			var input = document.createElement('input');
			input.type='text'
			input.id='comp_dtl_list'+count+'.stSearch_Pattern';
			input.name='comp_dtl_list'+count+'.stSearch_Pattern'
			input.class="srch_pattern" 
			input.maxlength="50"*/
			
			$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list'+count+'.knockoff_OrgVal" name="comp_dtl_list['+count+'].knockoff_OrgVal" onkeypress="'+"setValueType(this,'search')"+'" class="srch_pattern" maxlength="10"></td>');
			//$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list'+count+'.to_place" name="comp_dtl_list['+count+'].stPadding" class="place" maxlength="50"></td>');
		//	$row.append('<td align="center" class="lD"><select id="comp_dtl_list'+count+'.stPadding" name="comp_dtl_list['+count+'].stPadding"><option value="">- S E L E C T -</option><option value="Y">Yes</option><option value="N">No</option></select></td>');
			$row.append('<td align="center" class="lD"><input type="text" class="srch_pattern" id="comp_dtl_list'+count+'.knockoff_comprVal" name="comp_dtl_list['+count+'].knockoff_comprVal"  value="" maxlength="10" onkeypress="'+"setValueType(this,'search')"+'"></td>');
			/*$row.append('<td align="center" class="lD"><input type="text" id="cnv_dtl_list'+count+'.amount" name="cnv_dtl_list['+count+'].amount" class="amt claim_amount" maxlength="16"></td>');
		*///	$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list'+count+'.purpose" name="cnv_dtl_list['+count+'].purpose" class="purpose" maxlength="300" onkeypress="'+"setValueType(this,'numeric')"+'"></td>');
			$row.append('<td align="center" class="lD"><img alt="Logo" class="KnockoffDel" id="del'+count+'" src="images/delete.png" title="Delete"  style="vertical-align:middle; height: 20px; width: 20px;" /></td>');
		/*<input class="'+"form-button"+'" type="button" id="del'+count+'" name="del'+count+'" class="delButton" value="Delete"></td>');
*/	
			
			$('.dtl_table1').append($row);	


			/**Set Jquery Button CSS */
			$("input[type=submit], input[type=button]").button();

			/**Function to Set Hover on newly Created DOM's. */
			$('.delButton').hover(function(){
				//$(this).closest('tr').css('background','#e8eaef');
				$(this).closest('tr').addClass('hoverRow');
			}, function(){
				//$(this).closest('tr').css('background','');
				$(this).closest('tr').removeClass('hoverRow');
			});

			/**Reset the value of count with current effect.*/
			$('#count1').val(count);
		//	$('#acount').val(parseInt($('#acount').val())+1);
		}catch (e) {
			alert(e);
			return false;
		}
	/*}*/

	
}

function addknockoffcrtRow() {
	


	//alert("ADD ROW");
	
/*	var firsttd = $('selectTd').children().eq(0);
	var tdchild = firsttd.lastChild;
	var childvalue = tdchild.find('input').val();*/
	
	/*if(tdchild !='' || tdchild != undefined)*/
	
	
	

	
		try{
			var count = parseInt($.trim($('#count2').val()), 10);
			count= count+1
		
			/**Check if all earlier provided travel dates are filled.*/
		
			
			var splhdr = document.getElementById("headerlist").value.split(',');

			/**Adding New Travel Date Field Row.*/
			var tr_count = $('.dtl_table2').find('tr').length;
			//alert("tr_count "+tr_count);
			var rowClass="oddRow";
			if(tr_count % 2 == 0){
				rowClass = "evenRow";
			}
		
			var $row = $('<tr id="knockcmprrow'+count+'" class="'+rowClass+'" />');
			
			var td = document.createElement('td');
			td.align='center';
			var select = document.createElement('select');
			select.id ='comp_dtl_list'+count+'.knockoff_header';
			select.name = 'comp_dtl_list['+count+'].knockoff_header';
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
			$row.append(td);
			
		/*	var td1 = document.createElement('td');
			var input = document.createElement('input');
			input.type='text'
			input.id='comp_dtl_list'+count+'.stSearch_Pattern';
			input.name='comp_dtl_list'+count+'.stSearch_Pattern'
			input.class="srch_pattern" 
			input.maxlength="50"*/
			
			$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list'+count+'.knockoffSrch_Pattern" name="comp_dtl_list['+count+'].knockoffSrch_Pattern" onkeypress="'+"setValueType(this,'search')"+'" class="srch_pattern" maxlength="50"></td>');
			//$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list'+count+'.to_place" name="comp_dtl_list['+count+'].stPadding" class="place" maxlength="50"></td>');
			$row.append('<td align="center" class="lD"><select id="comp_dtl_list'+count+'.knockoff_stPadding" name="comp_dtl_list['+count+'].knockoff_stPadding"><option value="">--Select--</option><option value="Y">Yes</option><option value="N">No</option></select></td>');
			$row.append('<td align="center" class="lD"><select id="comp_dtl_list'+count+'.knockoff_condition" name="comp_dtl_list['+count+'].knockoff_condition" cssClass="char_pos"><option value="">--Select--</option><option value="=">=</option><option value="!=">!=</option><option value="like">LIKE</option></select>');
			$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list'+count+'.knockoffStart_Char_Pos" name="comp_dtl_list['+count+'].knockoffStart_Char_Pos" class="char_pos" value="0" maxlength="6" onkeypress="'+"setValueType(this,'numeric')"+'"></td>');
			$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list'+count+'.knockoffEnd_char_pos" name="comp_dtl_list['+count+'].knockoffEnd_char_pos" class="char_pos" value="0" maxlength="6" onkeypress="'+"setValueType(this,'numeric')"+'"></td>');
			$row.append('<td align="center" class="lD"><img alt="Logo" id="del'+count+'" class="KnockoffDelcrt" src="images/delete.png" title="Delete"  style="vertical-align:middle; height: 20px; width: 20px;" /></td>');
	
			
			$('.dtl_table2').append($row);	


			/**Set Jquery Button CSS */
			$("input[type=submit], input[type=button]").button();

			/**Function to Set Hover on newly Created DOM's. */
			$('.delButton').hover(function(){
				//$(this).closest('tr').css('background','#e8eaef');
				$(this).closest('tr').addClass('hoverRow');
			}, function(){
				//$(this).closest('tr').css('background','');
				$(this).closest('tr').removeClass('hoverRow');
			});

			/**Reset the value of count with current effect.*/
			$('#count2').val(count);
		//	$('#acount').val(parseInt($('#acount').val())+1);
		}catch (e) {
			alert(e);
			return false;
		}

}



function knockoffvalidData(){
	
	var stCategory = document.getElementById('stCategory').value;
	var fileID = document.getElementById('fileList').value;
	var msg="";
	
	if(stCategory == ""){
		
		msg=msg+"Please select category.\n"
	}if(fileID == "0"){
		
		msg=msg+"Please select File.\n"
	}
	
	document.getElementById("comphdr1").value = document.getElementById("comp_dtl_list0.knockoff_col").value;
	document.getElementById("comphdr2").value = document.getElementById("comp_dtl_list0.knockoff_header").value;
	var td = $('#knockoffcompareDetailstbl').find('td')
	
	//alert(td.text());
	var chkd= true;
	$(td).find("input:text,select").each(function() {
       var textVal = this.value;
       
       console.log(textVal);
      if(textVal==""||textVal=="select") {
    	  
    	  chkd= false;
      }
   
    });
	
	var td1 = $('#knockoffcompareDetailstbl1').find('td')
	
	//alert(td.text());
	var chkd= true;
	$(td1).find("input:text,select").each(function() {
       var textVal = this.value;
       var tdclass = this.className;
     
      if((textVal=="" && tdclass!='srch_pattern') ||textVal=="select") {
    	  
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
		
		return true;
		
	}
	
}

