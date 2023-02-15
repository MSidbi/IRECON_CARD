$(function(){
	
	if($("#user_id").length){
		$('#user_id').focus();
	}

	$( "input[type=submit], input[type=button], button" ).button();

	$('#add_user').click(function(){
		try{
			$('body').append('<div id="userDiv"></div>');
			$('#userDiv').dialog({
				autoOpen: false,
				modal: true,
				width : '700',
				title : 'Add User',
				height : 'auto',
				resizable : false,
				scrollable : false,
				draggable : false,
				closeOnEscape : false,
				position : {my : "center top", at :"center top+50" ,of : window},
				open :function(event, ui){
					$(this).load("AddUser.do");
				},
				close : function(){
					$('#userDiv').remove();
				}
			});
			$('#userDiv').dialog("open");

		}catch (e) {
			showMessage("Error", e.message);
		}
	});

	$('#add').click(function(){
		if(isValidUser()){
			$(this).val('Adding..').css("width","100").off('click').prop("disabled",true);
			$('#cancel').off('click').prop("disabled",true);
			$('form').submit();
		}
	});

	$('#modify').click(function(){
		if(isValidModifyUser()){
			$(this).val('Modifying..').css("width","100").off('click').prop("disabled",true);
			$('#cancel').off('click').prop("disabled",true);
			$('form').submit();
		}
	});

	$('#delete').click(function(){
		$(this).val('Deleting..').css("width","100").off('click').prop("disabled",true);
		$('#cancel').off('click').prop("disabled",true);
		$('form').submit();
	});

	$('#cancel').click(function(){
		if($('#userDiv').length){
			$('#userDiv').remove();
			$('body').find('#userDiv').remove();
		}
	});

	$('#live_user').click(function(){

		try{
			if($('#liveUserDiv').length){
				$('#liveUserDiv').remove();
			}
			if($('#endUserDiv').length){
				$('#endUserDiv').remove();
			}

			$('body').append('<div id="liveUserDiv"></div>');
			$('#liveUserDiv').dialog({
				autoOpen: false,
				modal: true,
				width : '700',
				height : 'auto',
				minHeight : '0',
				resizable : false,
				scrollable : false,
				draggable : false,
				closeOnEscape : false,
				position : {my : "center top", at :"center top+50" ,of : window},
				open :function(event, ui){
					$(this).load("ModalLiveUser.do");
				},
				close : function(){
					$('#liveUserDiv').remove();
					if($('#endUserDiv').length){
						$('#endUserDiv').remove();
					}
				}, buttons : {
					"Close" : function(){
						$('#liveUserDiv').remove();
						if($('#endUserDiv').length){
							$('#endUserDiv').remove();
						}
					}
				}
			});
			$('#liveUserDiv').dialog("open");
		}catch (e) {
			showMessage("Success", e.message);
		}
	});

	$('#closeUserBtn').click(function(){
	
		alert("function called..");
		if($('#liveUserDiv').length){
			$('#liveUserDiv').remove();
			$('body').find('#liveUserDiv').remove();
		}
	});
});

function editUser(user_id){
	try{
		$('body').append('<div id="userDiv"></div>');
		$('#userDiv').dialog({
			autoOpen: false,
			modal: true,
			width : '700',
			title : 'Edit User',
			height : 'auto',
			resizable : false,
			scrollable : false,
			draggable : false,
			closeOnEscape : false,
			position : {my : "center top", at :"center top+50" ,of : window},
			open :function(event, ui){
				$(this).load("EditUser.do?user_id="+user_id);
			},
			close : function(){
				$('#userDiv').remove();
			}
		});
		$('#userDiv').dialog("open");

	}catch (e) {
		showMessage("Error", "Exception Occured.");
	}
}

function deleteUser(user_id){
	try{
		$('body').append('<div id="userDiv"></div>');
		$('#userDiv').dialog({
			autoOpen: false,
			modal: true,
			width : '400',
			title : 'Delete User',
			height : 'auto',
			resizable : false,
			scrollable : false,
			draggable : false,
			closeOnEscape : false,
			position : {my : "center top", at :"center top+50" ,of : window},
			open :function(event, ui){
				$(this).load("DeleteUser.do?user_id="+user_id);
			},
			close : function(){
				$('#userDiv').remove();
			}
		});
		$('#userDiv').dialog("open");

	}catch (e) {
		showMessage("Error", "Exception Occured.");
	}
}

function userLog(user_id){
	try{
		$('body').append('<div id="userDiv"></div>');
		$('#userDiv').dialog({
			autoOpen: false,
			modal: true,
			width : '650',
			title : 'User Log.',
			height : 'auto',
			resizable : false,
			scrollable : false,
			draggable : false,
			position : {my : "center top", at :"center top+50" ,of : window},
			open :function(event, ui){
				$(this).load("UserLog.do?user_id="+user_id);
			},
			close : function(){
				$('#userDiv').remove();
			},
			buttons: {
				"Close" : function(){
					$('#userDiv').remove();
				}
			}
		});
		$('#userDiv').dialog("open");
	}catch (e) {
		showMessage("Error", e.message);
	}
	
}

function isValidUser(){
	try{
		var user_id = $.trim($('#user_id').val());
		if(user_id == ""){
			//showAlert("Error", "User Id is required.", "user_id");
			showDefaultAlert("User Id is required.", "user_id");
			return false;
		}
		var user_name = $.trim($('#user_name').val());
		if(user_name == ""){
			//showAlert("Error", "User Name is required.", "user_name");
			showDefaultAlert("User Name is required.", "user_name");
			return false;
		}
		/*var user_type = $.trim($('#user_type').val());
		if(user_type == ""){
			//showAlert("Error", "A descriptive User Type is required.", "user_type");
			showDefaultAlert("A descriptive User Type is required.", "user_type");
			return false;
		}*/
		var user_status = $.trim($('#user_status').val());
		if(user_status==""){
			//showAlert("Error", "User Status is required", "user_status");
			showDefaultAlert("User Status is required", "user_status");
			return false;
		}
		return true;
	}catch (e) {
		showMessage("Error", e.message);
	}
}

function isValidModifyUser(){
	try{
		var user_name = $.trim($('#user_name').val());
		if(user_name == ""){
			showAlert("Error", "User Name is required.", "user_name");
			return false;
		}
		/*var user_type = $.trim($('#user_type').val());
		if(user_type == ""){
			showAlert("Error", "A descriptive User Type is required.", "user_type");
			return false;
		}*/
		var user_status = $.trim($('#user_status').val());
		if(user_status==""){
			showAlert("Error", "User Status is required", "user_status");
			return false;
		}
		return true;
	}catch (e) {
		showMessage("Error", e.message);
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
						url : 'EndUserSession.do',
						method : 'post',
						data :{user_id : user_id},
						success : function(responseJSON){
							responseJSON = $.trim(responseJSON);
							if(responseJSON != ''){
								responseJSON = $.parseJSON(responseJSON);
								
								alert("RESPONSE "+responseJSON.Result);
								if(responseJSON.Result == 'OK'){
									showMessage("Success",responseJSON.Value);
								}else if(responseJSON.Result == "ERROR"){
									showMessage("Error",responseJSON.Value);
								}
							}
							/**Show All Current User's.*/
							showCurrentUser();
							
							if($('#endUserDiv').length){
								$('#endUserDiv').remove();
							}
						},
						error : function(jqXHR){
							showMessage("Error", jqXHR.responseText);
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

function showCurrentUser(){
	try{
		var count = 0;
		$.ajax({
			method : 'post',
			url : 'GetCurrentUser.do',
			success : function(responseJSON){
				responseJSON = $.trim(responseJSON);
				if(responseJSON != ''){
					responseJSON = $.parseJSON(responseJSON);
					/** Searching and Deleting all rows from Detail Table except Header Row **/
					$('table#userTable').find("tr:gt(0)").remove();

					/** Inserting Detail Records in userTable. **/
					$.each(responseJSON.user_dets, function(key, value){
						var rowClass="oddRow";
						key = parseInt(key, 10) + 1;
						if(key % 2 == 0){
							rowClass = "evenRow";
						}
						var newRow = $('<tr>').addClass(rowClass);
						var cols = '<td align="center">'+value.user_id+'</td>';
							cols += '<td align="center" class="leftDotted">'+value.user_name+'</td>';
							cols += '<td align="center" class="leftDotted">'+value.in_time+'</td>';
							cols += '<td align="left" class="leftDotted">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'+value.ip_address+'</td>';
							cols += '<td align="center" class="leftDotted"><img alt="End Session" style="cursor: pointer;" onclick="endUserSession('+value.user_id+')" src="images/end-session.png"></td>';
						newRow.append(cols);
						count += 1;
						$('table#userTable').append(newRow);
					});
				}
				if(count == 0){
					var newRow = $('<tr>').addClass('oddRow');
					var cols = '<td align="center" colspan="5">No Active User Found.</td>';
					newRow.append(cols);
					$('table#userTable').append(newRow);
				}
				return;
			}
		});
	}catch (e) {
		showMessage("Error", e.message);
	}
}