$(function(){
	document.onkeydown = function (event) {
	    event = (event || window.event);
	    if (event.keyCode == 123) {
	       // alert('No F-keys');
	        return false;
	    }
	};
	$(document).on("contextmenu",function(e){        
		   e.preventDefault();
		});
	$( "[title]" ).tooltip({
			position: {
				my: "left top",
				at: "right+5 top-5"
			}
		});
	$('#user_id').focus();
	$('#loginBtn').click(function(){
		return isValidLogin();
	});

	$('#resetBtn').click(function(){
		$('#user_id').val('').focus();
		$('#password').val('');
	});

	$('#endSession').click(function(){
		try{
			var user_id = $.trim($('#user_id').val());
			if(user_id == '' || user_id == undefined){
				throw new Error("User not available.");
			}
			$('body').find('#sessionForm').remove();
			var myForm = '<form id="sessionForm" name="sessionForm" action="InvalidateSession.do" method="GET">';
			myForm += '<input type="hidden" id="user_id" name="user_id" value="'+user_id+'" >';
			myForm += '</form>';
			$('body').append(myForm);
			$('form#sessionForm').submit();
		}catch (e) {
			alert(e.message);
		}
	});
	
	$('#closeSession').click(function(){
		/*alert("HIEEEE");*/
		/*try{
			$('body').find('#sessionForm').remove();
			var myForm = '<form id="sessionForm" name="sessionForm" action="closeSession.do" method="get">';
			myForm += '</form>';
			$('body').append(myForm);
			$('form#sessionForm').submit();
		}catch (e) {
			alert(e.message);
		}*/
		if($("#transDiv").length){
			$("#transDiv").remove();
		}

		try{
			$('body').append('<div id="transDiv"></div>');			
			 $( "#transDiv" ).dialog({
				autoOpen: false,
				modal: true,
				width : '700',
				title : 'BOUNCE MY LOGIN',
				id:'dialog1',
				height : 'auto',
				resizable : false,
				scrollable : false,
				draggable : false,
				position : {my : "center top", at :"center top+50" ,of : window},
				open : function(event, ui){
					$(this).load("closeSession.do?");
				//	$( "#transDiv" ).remove();
				},
				close : function(){
					$("#transDiv").remove();
			
				}, 
				/*buttons : {
					"Refresh" : function(){
						
						$(this).load("closeSession.do?");
						$(this).remove();
						$('#transDiv').remove();
						if($('#endUserDiv').length){
							$('#endUserDiv').remove();
						}
					}
				}*/
				});
			$( "#transDiv" ).dialog("open");
		}catch (e) {
			showMessage("Error", e.message);
		}
	});
	
});

function isValidLogin(){
	try{
		var user_id = $.trim($('#user_id').val());
		if(user_id == ''){
			alert("Username or Email is required.");
			$('#user_id').focus();
			return false;
		}
		var password = $('#password').val();
		if(password == ''){
			alert("Password is required.");
			$('#password').focus();
			return false;
		}else{
	        var aesUtil = new AesUtil();
	        var passphrase = aesUtil.createpassPharse()+ (Math.floor(Math.random()*999)) ;
			var iv = CryptoJS.lib.WordArray.random(128/8).toString(CryptoJS.enc.Hex);
			var salt = CryptoJS.lib.WordArray.random(128/8).toString(CryptoJS.enc.Hex);
	        var ciphertext = aesUtil.encrypt(salt, iv, passphrase, password);
	        $('#login').append($('<input type="hidden" name="passphrase" value="'+passphrase+'" />'));
			$('#login').append($('<input type="hidden" name="iv" value="'+iv+'" />'));
			$('#login').append($('<input type="hidden" name="salt" value="'+salt+'" />'));
	        $('#password').val(ciphertext);
		}
	     
	    	var jsess = new Date().getTime();
	    	document.cookie = "jsess="+jsess
	    	window.sessionStorage.setItem("lastname",jsess);
		return true;
	}catch (e) {
		alert(e.message);
	}
}
function endUserSession(user_id){
	
	try{
		$('body').append('<div id="endUserDiv"></div>');
		$('#endUserDiv').dialog({
			autoOpen: false,
			modal: true,
			width : '400',
			title : 'End Session',
			height : 'auto',
			resizable : false,
			scrollable : false,
			draggable : false,
			position : {my : "center top", at :"center top+50" ,of : window},
			buttons : {
				"Confirm" : function(){
					$.ajax({
							url : 'CloseUserSession.do',
							method : 'post',
							data :{user_id : user_id},
							success : function(responseJSON){
								responseJSON = $.trim(responseJSON);
								if(responseJSON != ''){
									responseJSON = $.parseJSON(responseJSON);
							
									if(responseJSON.Result == 'OK'){
										alert(responseJSON.Value);
									//	showMessage("Success",responseJSON.Value);
									}else if(responseJSON.Result == "ERROR"){
									//	showMessage("Error",responseJSON.Value);
										alert(responseJSON.Value);
									}
								}
															
								if($('#endUserDiv').length){
									//$('#endUserDiv').parent().hide();
									$('#endUserDiv').remove();
									//$("#transDiv").remove();
								
								}
								
								//$(".ui-dialog-content").dialog("close");
								//$("#transDiv").remove();
								//$(".ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix").remove();
								//$("#ui-id-1").parent().parent().fadeOut(300);
								
								location.reload("Login.do");
								//location.reload();

							//	$( "#transDiv" ).dialog("close");
								
							},
							error : function(jqXHR){
								//showMessage("Error", jqXHR.responseText);
								alert(jqXHR.responseText);
							}
						});
					},
					"Cancel" : function(){
						if($('#endUserDiv').length){
							$('#endUserDiv').remove();
						}
					}
				
			},
			close : function(){
				$('#endUserDiv').remove();
			}
		});
		$('#endUserDiv').dialog("open").html('<table align="center" style="width: 100%; color: red"><tr><th style="padding-top: 15px;">User Session will be terminated.<br/>Are you sure ?</th></tr></table>');
	}catch (e) {
		showMessage("Error", e.message);
	}
}
window.history.forward();

function noBack()
{
	
		  var myNav = navigator.userAgent.toLowerCase();
		  return (myNav.indexOf('msie') != -1) ? parseInt(myNav.split('msie')[1]) : false;
		
    window.history.forward();
 
} 


