$(function(){
	
	//alert("HELLOOOOO");
	//$( "input[type=submit], input[type=button], button" ).button();
	
	
	/*$('#tab1').click(function(){
		
		$('#id1').css("display", "block");
		$('#id2').css("display", "none");
		$('#id3').css("display", "none");
		
	});
	$('#tab2').click(function(){
		//alert("tab2");
		$('#id1').css("display", "none");
		$('#id2').css("display", "block");
		$('#id3').css("display", "none");
	});
	$('#tab3').click(function(){
		//alert("tab3");
		$('#id1').css("display", "none");
		$('#id2').css("display", "none");
		$('#id3').css("display", "block");
	});*/
	
	$('#submit').click(function(){
		//alert("SUBMIT");
		
	});

	$('.delButton').hover(function(){
		//$(this).closest('tr').css('background','#e8eaef');
		$(this).closest('tr').addClass('hoverRow');
	}, function(){
		//$(this).closest('tr').css('background','');
		$(this).closest('tr').removeClass('hoverRow');
	});
	
	
	$('#addRow').click(function(){
		//alert("ADD ROW");
		
	/*	var firsttd = $('selectTd').children().eq(0);
		var tdchild = firsttd.lastChild;
		var childvalue = tdchild.find('input').val();*/
		
		/*if(tdchild !='' || tdchild != undefined)*/
		var stHeader = document.getElementById("clasify_dtl_list[0].stHeader");
		
		var stHeaderval = stHeader.value;
		
		
		/*if(stHeader!=undefined)
		{
		document.getElementById("comphdr").value = document.getElementById("comp_dtl_list[0].stHeader").value
		}*/
		
		
		if(stHeader!=null || stHeader!=""){
		
			try{
				var count = parseInt($.trim($('#count').val()), 10);
				count = count+1;
				/**Check if all earlier provided travel dates are filled.*/
			
				
				var splhdr = document.getElementById("headerlist").value.split(',');

				/**Adding New Travel Date Field Row.*/
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
				select.id ='clasify_dtl_list'+count+'.stHeader';
				select.name = 'clasify_dtl_list['+count+'].stHeader';
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
				
			/*	var td1 = document.createElement('td');
				var input = document.createElement('input');
				input.type='text'
				input.id='comp_dtl_list'+count+'.stSearch_Pattern';
				input.name='comp_dtl_list'+count+'.stSearch_Pattern'
				input.class="srch_pattern" 
				input.maxlength="50"*/
				
				$row.append('<td align="center" class="lD"><input type="text" id="clasify_dtl_list'+count+'.stSearch_Pattern" name="clasify_dtl_list['+count+'].stSearch_Pattern" onkeypress="'+"setValueType(this,'search')"+'" class="srch_pattern" maxlength="50"></td>');
				//$row.append('<td align="center" class="lD"><input type="text" id="comp_dtl_list'+count+'.to_place" name="comp_dtl_list['+count+'].stPadding" class="place" maxlength="50"></td>');
				$row.append('<td align="center" class="lD"><select id="clasify_dtl_list'+count+'.stPadding" name="clasify_dtl_list['+count+'].stPadding"><option value="">--Select--</option><option value="Y">Yes</option><option value="N">No</option></select></td>');
				$row.append('<td align="center" class="lD"><select id="clasify_dtl_list'+count+'.condition" name="clasify_dtl_list['+count+'].condition" cssClass="char_pos"><option value="">--Select--</option><option value="=">=</option><option value="!=">!=</option><option value="like">LIKE</option></select>');
				$row.append('<td align="center" class="lD"><input type="text" id="clasify_dtl_list'+count+'.inStart_Char_Position" name="clasify_dtl_list['+count+'].inStart_Char_Position" class="char_pos" value="0" maxlength="6" onkeypress="'+"setValueType(this,'numeric')"+'"></td>');
				$row.append('<td align="center" class="lD"><input type="text" id="clasify_dtl_list'+count+'.inEnd_char_position" name="clasify_dtl_list['+count+'].inEnd_char_position" class="char_pos" value="0" maxlength="6" onkeypress="'+"setValueType(this,'numeric')"+'"></td>');
				
				
			$row.append('<td align="center" class="lD"><img alt="Logo" id="del'+count+'" class="delButton" src="images/delete.png" title="Delete"  style="vertical-align:middle; height: 20px; width: 20px;" /></td>');
			
	/*<input class="'+"form-button"+'" type="button" id="del'+count+'" name="del'+count+'" class="delButton" value="Delete"></td>');
	*/	
				
				$('.dtl_table').append($row);	


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
				$('#count').val(count);
			//	$('#acount').val(parseInt($('#acount').val())+1);
			}catch (e) {
				alert(e);
				return false;
			}
		}else{
			
			alert("Please select file name");
			return false;
		}
			
		/*}*/
	});
	
	
	$(document).on("click",".delButton", function(){
	//	alert("del clicked");
		var count = parseInt($('#count').val(), 10);
		
		$(this).closest('tr').remove();
		$('#count').val($('#count').val() - 1);
	//	alert("count is "+count);
		refreshList();
		refreshList();
	});
	
	$(document).on("click",".KnockoffDel", function(){
		//	alert("del clicked");
			var count = parseInt($('#count1').val(), 10);
			
			$(this).closest('tr').remove();
			$('#count1').val($('#count1').val() - 1);
		//	alert("count is "+count);
			refreshList();
			refreshList();
		});
	$(document).on("click",".KnockoffDelcrt", function(){
		//	alert("del clicked");
			var count = parseInt($('#count2').val(), 10);
			
			$(this).closest('tr').remove();
			$('#count2').val($('#count2').val() - 1);
		//	alert("count is "+count);
			refreshList();
			refreshList();
		});
	
	$('#reset').click(function(){
		clearAllfields();
	});
	
});

function changeValue() {
	
	document.getElementById("stFileName2").value=$('#stFileName1').val();
	document.getElementById("stFileName3").value=$('#stFileName1').val();
}




function changetxt(btn){
	
	
	var password = document.getElementById("hdpwd").value;
	if(password=="Show Password") {
	
		document.getElementById("hdpwd").value="Hide Password";
		$('#ftpPwd').removeAttr("type");
		$('#ftpPwd').prop('type', 'text');
		
	}if(password=="Hide Password") {
	
		$('#ftpPwd').removeAttr("type");
		$('#ftpPwd').prop('type', 'password');
		document.getElementById("hdpwd").value="Show Password";
	}
}

function changeStatus(chkbox) {
	
	
	if(document.getElementById("chkstat").checked) {
		
		document.getElementById("activeFlag").value ='A'
	}else{
		
		document.getElementById("activeFlag").value ='I'
	}
	
	
}


function clearAll(){
	$('body').find('input:checkbox').removeAttr('checked');
	$('#allDiv').find('img').prop("src","images/chk_off.png");
	$('#allDiv :checkbox').prop("class","tri").removeAttr('checked');
}


function setvalue(e) {
	
	if(e.id=="Category") {
		
		document.getElementById("stCategory").value = e.value;
		
	}else if(e.id=="SubCat") {
		
		document.getElementById("stSubCategory").value = e.value;
		
	}
}


function getFiles() {
	
	var category = document.getElementById("Category").value;
	var subcat = document.getElementById("SubCat").value;
	
	if(category!="") { 
	$.ajax({
		 
		 type:'POST',
		 url :'getFiledetails.do',
		 data:{category:category,subcat:subcat},
		 success:function(response){
			 
			
			// response = $.parseJSON(response);
			 var length =response.Records.length;
			
			 var fileList = document.getElementById("fileList");
			 
			 var rowcount = fileList.childNodes.length;
				
				for(var j =1;j<=rowcount;j++ )
				{
					 fileList.removeChild(fileList.lastChild);
				}
			
			 var option1= document.createElement("option")
			 option1.value="0";
			 option1.text="--Select--";
			fileList.appendChild(option1);
			 
			 for(var i =0;i<length;i++ ) {
				
				 var option= document.createElement("option")
				  option.value = response.Records[i].inFileId;
				 option.text= response.Records[i].stFileName;
				fileList.appendChild(option);

			 }
			 document.getElementById("trfilelist").style.display="";
			 document.getElementById("Category").disabled="disabled";
			 document.getElementById("SubCat").disabled="disabled";
			 document.getElementById("trbtn").style.display="none";
			
			 

		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });
	}else {
		
		
		alert("Please Select Category.")
		
	}
	
	
}







