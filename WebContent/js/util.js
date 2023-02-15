$(function(){
	checkBack();

	$.ajaxSetup({ 
		cache: false,
		key : 'value',
		error: function(jqXHR, exception) {
			if (jqXHR.status === 0) {
				showMessage('Error','Not connect.\n Verify Network.');
			} else if (jqXHR.status == 404) {
				showMessage('Error','Requested page not found. [404]');
			} else if (jqXHR.status == 500) {
				showMessage('Error','Internal Server Error [500].');
			} else if (exception === 'parsererror') {
				showMessage('Error','Requested JSON parse failed.');
			} else if (exception === 'timeout') {
				showMessage('Error','Time out error.');
			} else if (exception === 'abort') {
				showMessage('Error','Ajax request aborted.');
			} else {
				showMessage('Error','Uncaught Error.\n' + jqXHR.responseText);
			}
		}
	});

	$("[title]").tooltip({
		position: {
			my: "left top",
			at: "right+5 top-7"
		}
	});

	$( "input[type=submit], input[type=button], button" ).button();

	$(document).on("blur", ".amt", function(){
		var amount = $.trim($(this).val());
		if(amount != "" && $.isNumeric(amount)){
			$(this).val(number_format(amount, 2, ".", ""));
		}
	});
});

/**Function to disable buttons. */
function disableButtons(){
	$("input[type=submit], input[type=button]" ).off("click","submit").prop("disabled",true);
}

/**Function for removing focus from field/input*/
function showBlur(field){
	$('#'+field).blur();
}

/** Function for disabling Backspace on readonly fields */
function checkBack(){
	$(document).on("keydown", function (e) {
		if (e.which === 8 && (!$(e.target).is("input:not([readonly]), textarea") || $(e.target).is('input[type = "button"]:focus,input[type = "submit"]:focus'))) {
			e.preventDefault();
		}
	});
}

function showLastDay(date) {
	if (date.getDate() == getLastDayOfYearAndMonth(date.getFullYear(), date.getMonth())) {
		return [ true, '' ];
	}
	return [ false, '' ];
}

function getLastDayOfYearAndMonth(year, month){
	return(new Date((new Date(year, month + 1, 1)) - 1)).getDate();
}

/**Function for Displaying Success/Failure Message with field focus.*/
function showError(message, field){
	$('#errorDiv').remove();
	$('body').append('<div id="errorDiv" class="infoDiv"></div>');
	$('#errorDiv').dialog({
		autoOpen: false,
		modal: true,
		title: "Error",
		width : 400,
		height : 'auto',
		resizable : false,
		draggable : false,
		position : {my : "center top", at :"center top+75" ,of : window},
		open :function(event, ui){
			$('.ui-widget-overlay').bind('click', function() {
				$('#errorDiv').remove();
				$('#'+field).val('').focus();
			});
			/*setTimeout(function(){
				$('#errorDiv').remove();
				$('#'+field).val('').focus();
			},1000);*/
		},
		buttons: {
			"Ok" : function(){
				$('#errorDiv').remove();
				$('#'+field).val('').focus();
			}
		}
	});
	$('#errorDiv').html(message).dialog('open');
}

/**Function for Displaying Success/Failure Message**/
function showMessage(title,message){

	$('#successDiv').remove();
	$('body').append('<div id="successDiv" class="infoDiv"></div>');
	$('#successDiv').dialog({
		autoOpen : false,
		modal : true,
		title : title,
		resizable : false,
		draggable : false,
		width : '400',
		height : 'auto',
		position : {my : "center top", at :"center top+75" ,of : window},
		open : function(event, ui){
			$('.ui-widget-overlay').bind('click', function() {
				$('#successDiv').remove();
			});
		},
		buttons: {
			"Ok" : function(){
				$('#successDiv').remove();
			}
		}
	});
	$('#successDiv').html(message).dialog('open');
}

/**Function for displaying error messages(Mainly for Form Validation) */
function showAlert(title, message, field){
	$('#alertDiv').remove();
	$('body').append('<div id="alertDiv" class="infoDiv"></div>');
	$('#alertDiv').dialog({
		autoOpen : false,
		modal : true,
		title : title,
		resizable : false,
		draggable : false,
		minWidth : 200,
		height : 'auto',
		position : {my : "center top", at :"center top+75" ,of : window},
		open : function(event, ui){
			$('.ui-widget-overlay').bind('click', function() {
				$('#alertDiv').remove();
				$('#'+field).focus();
			});
		},
		buttons: {
			"Ok" : function(){
				$('#alertDiv').remove();
				$('#'+field).focus();
			}
		}
	});
	$('#alertDiv').html(message).dialog('open');
}

function showDefaultAlert(msg, field){
	alert(msg);
	$('#'+field).focus();
}

function validDate(date){
	var dateRegex = /^(((0[1-9]|[12]\d|3[01])\/(0[13578]|1[02])\/((19|[2-9]\d)\d{2}))|((0[1-9]|[12]\d|30)\/(0[13456789]|1[012])\/((19|[2-9]\d)\d{2}))|((0[1-9]|1\d|2[0-8])\/02\/((19|[2-9]\d)\d{2}))|(29\/02\/((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))))$/;
	if (date.match(dateRegex)) {
		return true;
	}
	return false;
}

function compareDate(datePrev, dateNext){

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
		if(datePrev <= dateNext){
			return true;
		}else{
			return false;
		}
	}catch (e) {
		return false; 
	}
}

function compareDateE(datePrev, dateNext) {
	try {
		if (validDate(datePrev) == false) {
			throw new Error("Invalid First Date");
		}
		if (validDate(dateNext) == false) {
			throw new Error("Invalid Second Date");
		}
		var splitter1 = datePrev.split("/");
		datePrev = new Date(splitter1[2], parseInt(splitter1[1], 10) - 1, splitter1[0]);

		var splitter2 = dateNext.split("/");
		dateNext = new Date(splitter2[2], parseInt(splitter2[1], 10) - 1, splitter2[0]);

		// if(Date.parse(datePrev) < Date.parse(dateNext)){
		if (datePrev <= dateNext) {
			return true;
		} else {
			return false;
		}
	} catch (e) {
		return false;
	}
}

function validBirthDate(d_o_b){
	try{
		if(validDate(d_o_b) == false){
			return false;
		}
		var splitter = d_o_b.split("/");
		d_o_b = new Date(splitter[2], parseInt(splitter[1],10) -1, splitter[0]);

		var d= new Date();
		var current = new Date(d.getFullYear(), d.getMonth() , d.getDate());
		if(d_o_b < current){
			return true;
		}else{
			return false;
		}
	}catch (e) {
		return false;
	}
}

function calculateAge(d_o_b){
	try{
		if(validDate(d_o_b) == false){
			//$('#age').val('');
			return false;
		}
		var splitter = d_o_b.split("/");
		d_o_b = new Date(parseInt(splitter[2], 10), parseInt(splitter[1], 10) - 1, parseInt(splitter[0], 10) );
		var today =  new Date();
		var age = Math.floor((today-d_o_b) / (365.25 * 24 * 60 * 60 * 1000));
		$('#age').val(age);
	}catch (e) {
		//$('#age').val('');
		showError(e.message, "d_o_b");
	}
	
}

function calculateBirthDate(age){
	try{
		if(age = age/1){
			var date = new Date();
			var day = parseInt(date.getDate(),10);
			var month = parseInt(date.getMonth() + 1, 10);
			var year = parseInt(date.getFullYear());
			if(day < 10){day = "0"+day;}
			if(month < 10){month = "0"+month;}
		
			
			$('#d_o_b').val(day+"/"+month+"/"+(year-age));
		}
	}catch (e) {
		return false;
	}
}

function clearAllfields(){
	try{
		$('body').find('input:text,input:password,input:file,select,textarea').val('');
		$('body').find('input:radio,input:checkbox').removeAttr('checked').removeAttr('selected');
		/*$(this).closest('form').find('input:text, input:password,input:file,select,textarea').val('');
		$(this).closest('form').find('input:radio, input:checkbox').removeAttr('checked').removeAttr('selected');*/
	}catch (e) {
		showError(e.message, "");
	}
	
}

/**Function for Getting Last Date of Month. */
function getLastDateOfMonth(date){
	try{
		var splitter = date.split("/");
		var dated = new Date(splitter[2], splitter[1], 0);
		var day = dated.getDate();
		if(parseInt(dated.getDate(), 10) < 10){
			day = "0"+day;
		}
		var month = dated.getMonth()+1;
		if(parseInt(month, 10) < 10){
			month="0"+month;
		}
		date = day+"/"+month+"/"+splitter[2];
		return date;
	}catch (e) {
		throw new Error("Error Formatting Date.");
		//return date;
	}
}

/**Setting Date value to Last/Maximum Date of the same Year Month **/
function setLastDayOfMonth(effctv_dt, field) {
	try {
		effctv_dt = $.trim(effctv_dt);
		if (effctv_dt != '') {
			if (validDate(effctv_dt) == false) {
				throw new Error("Invalid Date..");
				return false;
			}

			effctv_dt = getLastDateOfMonth(effctv_dt);
			$("#" + field).val(effctv_dt);
		}
	} catch (e) {
		showError(e.message, field);
	}
}

/**Setting last or maximum date of month **/
function setLastDoM(field){
	try{
		var effctv_dt = $.trim($('#'+field).val());
		if(effctv_dt != '' && effctv_dt != undefined){
			if(validDate(effctv_dt) == false){
				throw new Error("Invalid Date..");
				return false;
			}
			effctv_dt = getLastDateOfMonth(effctv_dt);
			$('#'+field).val(effctv_dt);
		}
	}catch (e) {
		showError(e.message, field);
	}
}

/**Show Employee Master List **/
function showEmployeeList(){
	$('body').append('<div id="employeeDiv" style="overflow:hidden;"></div>');
	$('#employeeDiv').dialog({
		autoOpen: false,
		modal: true,
		width : '700',
		height : 'auto',
		resizable : false,
		scrollable : false,
		draggable : false,
		position : {my : "center top", at :"center top+50" ,of : window},
		open :function(event, ui){
			$(this).load("EmployeeList.do");
		},
		buttons: {
			"Close" : function(){
				$('#employeeDiv').remove();
			}
		},
		close : function(){
			$(this).dialog('destroy').remove();
		}
	});
	$('#employeeDiv').dialog("open");
}

/**Fetching List of Office Code's */
function showOfficeList(){
	$('body').append('<div id="officeDiv" style="overflow:hidden;"></div>');
	$('#officeDiv').dialog({
		autoOpen: false,
		modal: true,
		width : '650',
		height : 'auto',
		draggable : false,
		resizable : false,
		scrollable : false,
		position : {my : "center top", at :"center top+50" ,of : window},
		open :function(event, ui){
			$(this).load("OfficeCodeList.do");
		},
		buttons: {
			"Close" : function(){
				$('body').find('#officeDiv').remove();
				$('#officeDiv').remove();
			}
		},
		close : function(){
			$(this).dialog('destroy').remove();
		}
	});
	$('#officeDiv').dialog("open");
}
/****Show loan codes only for Advance****/
  function showAdvanceLoanCodeList(){
	 $('body').append('<div id="loanDiv"></div>');
	$('#loanDiv').dialog({
		autoOpen: false,
		modal: true,
		width : '750',
		height : 'auto',
		resizable : false,
		scrollable : false,
		draggable : false,
		position : {my : "center top", at :"center top+50" ,of : window},
		open :function(event, ui){
			$(this).load("AdvanceLoanCodeList.do");
		},
		buttons: {
			"Close" : function(){
				$('#loanDiv').remove();
			}
		},
		close : function(){
			$(this).dialog('destroy').remove();
		}
	});
	$('#loanDiv').dialog('open');
}

 




/**Show Loan Master List ******/
function showLoanCodeList(){
	$('body').append('<div id="loanDiv"></div>');
	$('#loanDiv').dialog({
		autoOpen: false,
		modal: true,
		width : '750',
		height : 'auto',
		resizable : false,
		scrollable : false,
		draggable : false,
		position : {my : "center top", at :"center top+50" ,of : window},
		open :function(event, ui){
			$(this).load("LoanCodeList.do");
		},
		buttons: {
			"Close" : function(){
				$('#loanDiv').remove();
			}
		},
		close : function(){
			$(this).dialog('destroy').remove();
		}
	});
	$('#loanDiv').dialog('open');
}

/**Fetching Next Serial Number **/
function getSerialNo(systemname, parameter, field){
	try{
		var serialNo;
		$.ajax({
			type : 'post',
			url : 'getLoanNumber.do',
			data : {'systemname' : systemname, 'parameter' : parameter},
			success : function(response){
				serialNo = response;
				$('#'+field).val(serialNo);
			}
		});
	}catch (e) {
		return e; 
	}
}

/***Validating Date and Checking if it does not falls in between previous Committed Year***/
function validateHalfYearDate(eff_dt){
	try{
		var effctv_dt = $.trim($('#'+eff_dt).val());
		if(effctv_dt != ''){
			if(validDate(effctv_dt) == false){
				throw new Error("Invalid Date..");
			}
			
			effctv_dt = getLastDateOfMonth(effctv_dt);
			$.ajax({
				method : 'post',
				url : 'checkValidEffectiveDate.do',
				data : {"effctv_dt" : effctv_dt},
				success : function(responseJSON){
					if($.trim(responseJSON)=="false"){
						//showError("Cannot touch Last half yearly balance.", eff_dt);
						//showError("Date from Last Half Yearly Balance or Future Financial Year found.", eff_dt);
						showError("<center>Invalid Date<br/></center>Either from Past/Future Financial Year or <br/>from Last Committed Half Year.", eff_dt);
					}else{
						$('#'+eff_dt).val(effctv_dt);
					}
				}
			});
		}
	}catch (e) {
		showError(e.message, eff_dt);
	}
}

/***Validating Sanction Date and Checking if it does not falls in between previous Committed Year***/
function validateSanctionDate(){
	try{
		var snc_dt = $.trim($('#snc_dt').val());
		if(snc_dt != ''){
			if(validDate(snc_dt) == false){
				throw new Error("Invalid Date..");
			}
			
			snc_dt = getLastDateOfMonth(snc_dt);
			$('#snc_dt').val(snc_dt);
			$.ajax({
				method : 'post',
				url : 'checkValidEffectiveDate.do',
				data : {"effctv_dt" : snc_dt},
				success : function(responseJSON){
					if($.trim(responseJSON)=="false"){
						showError("Cannot touch Last half yearly balance.", "snc_dt");
					}
				}
			});
		}
	}catch (e) {
		showError(e.message, "snc_dt");
	}
}

/***Validating Disburse Date in Modal and Checking if it does not falls in between previous Committed Year***/
function validateModalDisburseDate(){
	try{
		var disb_dt = $.trim($('#disb_dt').val());
		if(disb_dt != ''){
			if(validDate(disb_dt) == false){
				throw new Error("Invalid Date..");
			}
			
			disb_dt = getLastDateOfMonth(disb_dt);
			$('#disb_dt').val(disb_dt);
			$.ajax({
				method : 'post',
				url : 'checkValidEffectiveDate.do',
				data : {"effctv_dt" : disb_dt},
				success : function(responseJSON){
					if($.trim(responseJSON)=="false"){
						showError("Cannot touch Last half yearly balance.", "disb_dt");
					}
				}
			});
		}
	}catch (e) {
		showError(e.message, "disb_dt");
	}
}

/**validating Disburse Date in Edit Modal of Loan.**/
function validateEditModalDisburseDate(){
	try{
		var disb_dt = $.trim($('#disb_dt').val());
		if(disb_dt != ''){
			if(validDate(disb_dt) == false){
				throw new Error("Invalid Date..");
			}
			
			disb_dt = getLastDateOfMonth(disb_dt);
			$('#disb_dt').val(disb_dt);
			$.ajax({
				method : 'post',
				url : 'checkValidEffectiveDate.do',
				data : {"effctv_dt" : disb_dt},
				success : function(responseJSON){
					if($.trim(responseJSON)=="false"){
						showMessage("Error","Cannot touch Last half yearly balance.");
						$('#disb_dt').val($('#bkup_disb_dt').val());
					}
				}
			});
		}else{
			$('#disb_dt').val($('#bkup_disb_dt').val());
		}
	}catch (e) {
		showError(e.message, "disb_dt");
	}
}

/****************************************************************************************************************************************************/
/*********************************************Field Level Validation****************************************************/

//Permitting only Integer Values.
function insertInteger(){
	var key = event.charCode || event.keyCode || event.which || 0;
	// allow backspace, tab, delete, enter, arrows, numbers and keypad numbers ONLY
	// home, end, period, and numpad decimal
	return (
			key == 8 || 
			key == 9 ||
			key == 13 ||
			key == 46 ||
			(key >= 35 && key <= 40) ||
			(key >= 48 && key <= 57) ||
			(key >= 96 && key <= 105));
}

function isInteger(num){
	var regex = /^\d+$/;
	return parseFloat(num) === num >>> 0 && regex.test(num);
}

function isLongInt(num){
	var regex = /^\d+$/;
	return regex.test(num);
}

function insertAmount(){
	var key = event.charCode || event.keyCode || event.which || 0;
	// allow backspace, tab, delete, enter, arrows, numbers and keypad numbers ONLY
	// home, end, period, dash and numpad decimal
	return (
			key == 8 || 
			key == 9 ||
			key == 13 ||
			key == 46 ||
			key == 110 ||
			key == 190 ||
			key == 109 ||
			key == 189 ||
			(key >= 35 && key <= 40) ||
			(key >= 48 && key <= 57) ||
			(key >= 96 && key <= 105));
}

/**Function for displaying Amount in Decimal Format Format, especially incase of Jtable display */
	function number_format(number, decimals, dec_point, thousands_sep) {
		number = (number + '').replace(/[^0-9+\-Ee.]/g, '');
		var n = !isFinite(+number) ? 0 : +number,
		prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
		sep = (typeof thousands_sep === 'undefined') ? ',' : thousands_sep,
		dec = (typeof dec_point === 'undefined') ? '.' : dec_point,
		s = '',
		toFixedFix = function(n, prec) {
			var k = Math.pow(10, prec);
			return '' + (Math.round(n * k) / k).toFixed(prec);
		};
		// Fix for IE parseFloat(0.55).toFixed(0) = 0;
		s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
		if (s[0].length > 3) {
			s[0] = s[0].replace(/\B(?=(?:\d{3})+(?!\d))/g, sep);
		}
		if ((s[1] || '').length < prec) {
			s[1] = s[1] || '';
			s[1] += new Array(prec - s[1].length + 1).join('0');
		}
		return s.join(dec);
	}