function caldifference(){
	
	var cash_dispense = parseFloat(document.getElementById("cash_dispense").value);
	var closing_balance = parseFloat(document.getElementById("closing_balance").value);
	
	if(cash_dispense!= "" && closing_balance!= "" ) {
		
		var diff  = closing_balance - cash_dispense
		document.getElementById("difference").value = parseFloat(diff).toFixed(2);
		
		
	}
	
	
}

function Settle() {
	
	var total_amount =0 ;
	
	$('.settlementAmount').each(function(){
		//  var i = this.name.split('surcharge_amount')[1];
		total_amount = total_amount+ parseFloat($(this).val());
		  
		});
	
	document.getElementById("cash_dispense").value = parseFloat(total_amount);
	caldifference();
}

function  displayBalance() {
	debugger;
	
	var disCnt=0;
	
	document.getElementById("datepicker").disabled = true;
	var datepicker = document.getElementById("datepicker").value;
	var stSubCategory = document.getElementById("stSubCategory").value;
	var category = document.getElementById("rectyp").value;
	
	if (datepicker !="" && stSubCategory !="") {
	$.ajax({
		type:'POST',
		url :'PrevDispense.do',
		data:{filedate:datepicker,subCat:stSubCategory,category:category},
		success:function(response){ 
			
			for(var i=0;i<response.length;i++) {
				
				$("#prevDispense").append('<div class="row"><div class="col-lg-6"><div class="form-group"><label for="exampleInputEmail1">Date </label><input type="text" class="form-control settlementDate" id="settlementDate'+i+'" value='+response[i].filedate+' placeholder="" ></div></div><div class="col-lg-4"><div class="form-group"><label for="exampleInputEmail1">Amount </label><input type="text" class="form-control settlementAmount" id="settlementAmount'+i+'" value='+response[i].amount+'  placeholder=""></div></div></div>');
				/*$( "#settlementAdd" ).load(window.location.href + " #settlementAdd" );*/
				$("#settlementdispense").modal('show');
				 
			}
			alert("SUCCESS");
			
		},error: function(){
			 
			alert("Error Occured");
		}
		
		});
	
	/**/
	}
	
	
	
	
}

function closeModal(){
	debugger;
	 $( "#prevDispense" ).load(window.location.href + " #prevDispense" );
	$("#settlementdispense").modal('hide');
	
}

function Process() {
	
	debugger;
	var rectyp = document.getElementById("rectyp").value;
	var cash_dispense1 = parseFloat(document.getElementById("cash_dispense").value);
	var closing_balance1 = parseFloat(document.getElementById("closing_balance").value);
	var difference1 = parseFloat(document.getElementById("difference").value);
	var datepicker = document.getElementById("datepicker").value;
	var stSubCategory = document.getElementById("stSubCategory").value;
	
	var cash_dispense = cash_dispense1.toFixed(2);
	var closing_balance = closing_balance1.toFixed(2);
	var difference = difference1.toFixed(2);
	
	var item_setdt = [];
	var item_settlamnt =[];
	$('.settlementDate').each(function(){
		//  var i = this.name.split('surcharge_amount')[1];
		  item_setdt.push( $(this).val());
		});
	
	$('.settlementAmount').each(function(){
		//  var i = this.name.split('surcharge_amount')[1];
		  item_settlamnt.push( $(this).val());
		});
	/*jsonObj["settlementAmount"] = item_setdt;*/
	
	/*for(var i=1;i<=iCnt;i++)
	{
	
	sum_total +=parseFloat($("#settlementAmount"+i+"").val());
	//var sur1=$('#surcharge'+i+'_inp').val();
	var sum_val12=sum_total.toFixed(2);
	$('#settlement_id').val(sum_val12);
	}*/
	
	var settlementAddDiv = document.getElementById("settlementAddDiv");
	
	//var setdt = item_setdt
	document.getElementById("item_setdt").value =  item_setdt;
	document.getElementById("item_settlamnt").value = item_settlamnt;
	
	var setdt = document.getElementById("item_setdt").value;
	
	if(cash_dispense!= "" && closing_balance!= "")
	{
		
		var jobj = {};
		jobj["dispense_amount"]=cash_dispense;
		jobj["filedate"]=datepicker;
		jobj["subCat"]=stSubCategory;
		jobj["difference"]=difference;
		jobj["item_setdt"]=item_setdt;
		jobj["item_settlamnt"]=item_settlamnt;
		jobj["category"] =rectyp;
		
		
		$.ajax({

			type:'POST',
			url :'checkDispense.do',
			async: true,
			data:jobj,
			beforeSend : function() {
				showLoader();
			},
			complete : function(data) {

				hideLoader();

			},
			success:function(response){
				alert(response);
				
				var jobjdis = {};
				jobjdis["category"] =rectyp;
				jobjdis["closing_balance"]=closing_balance;
				jobjdis["cash_dispense"]=cash_dispense;
				jobjdis["dispense_amount"]=cash_dispense;
				jobjdis["filedate"]=datepicker;
				jobjdis["subCat"]=stSubCategory;
				jobjdis["difference"]=difference;
				jobjdis["item_setdt"]=item_setdt;
				jobjdis["item_settlamnt"]=item_settlamnt;
				
				
				if(parseFloat(response).toFixed(2) == cash_dispense ) {
				$.ajax({
			
						type:'POST',
						url :'GlBalancing.do',
						async: true,
						data: jobjdis,  //{category:rectyp,closing_balance:closing_balance,cash_dispense:cash_dispense,filedate:datepicker,subCat:stSubCategory,difference:difference,item_setdt:item_setdt ,item_settlamnt:item_settlamnt,},
						beforeSend : function() {
							showLoader();
						},
						complete : function(data) {
			
							hideLoader();
			
						},
						success:function(response){
							alert(response);
							
							document.getElementById("datepicker").disabled="";			
							 var form = document.getElementById("reportform");
							form.submit();
							 
							 
						},error: function(){
			
							alert("Error Occured");
			
						},
			
					});
				
				} else {
					
					alert("Enter appropriate cash dispense ");
				}
		
			},error: function(){

				alert("Error Occured");

			},

		});
		
	}

}

function submit() {
	
	
	var form = document.getElementById("reportform");
	form.submit();
}

function showLoader() {
	
	$("#Loader").show();
}

function hideLoader() {
	
	$("#Loader").hide();
}