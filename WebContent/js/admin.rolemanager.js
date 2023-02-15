$(function(){
	/*$('li :checkbox').on('click', function () {
		var $chk = $(this), $li = $chk.closest('li'), $ul, $parent;
		if ($li.has('ul')) {
			$li.find(':checkbox').not(this).prop('checked', this.checked);
		}
		do {
			$ul = $li.parent();
			$parent = $ul.siblings(':checkbox');
			if ($chk.is(':checked')) {
				$parent.prop('checked', $ul.has(':checkbox:not(:checked)').length == 0);
			} else {
				$parent.prop('checked', false);
			}
			$chk = $parent;
			$li = $chk.closest('li');
		} while ($ul.is(':not(.someclass)'));
	});*/

	/**Code to transform Checkbox in images.*/
	$(".class input:checkbox").transformCheckbox({
		base : "class",
		checked : "images/chk_on.png",
		unchecked : "images/chk_off.png"
	});
	$("input:checkbox:not(.tri)").transformCheckbox({
		checked : "images/chk_on.png",
		unchecked : "images/chk_off.png"
	});
	$("input.tri:checkbox").transformCheckbox({
		checked : "images/chk_on.png",
		unchecked : "images/chk_off.png",
		tristateHalfChecked : "images/chk_tri.png",
		tristate : true
	});

	$( "input[type=submit], input[type=button], button" ).button();

	$('#search').click(function(){
		try{
			var user_id = $.trim($('#user_id').val());
			if(user_id != '' && user_id != undefined){
				fetchUserRole(user_id);
			}else{
				$('#userDiv').empty();
				$('#userDiv').append('<table align="center" width="100%"><tr><td align="center">Select User from above list.<br/> To view assigned roles</td></tr></table>');
			}
			clearAll();
		}catch (e) {
			showMessage("Error", e.message);
		}
	});

	$('#assign').on('click', function(){
		assignRole();
	});

	$('#revoke').on('click', function(){
		revokeRole();
	});

	$('#reset_all').click(function(){
		
		try{
			$('#allDiv').find('img').prop("src","images/chk_off.png");
			$('#allDiv :checkbox').prop("class","tri").removeAttr('checked');
		}catch (e) {
			showMessage("Error", e.message);
		}
	});

	$('#reset_user').click(function(){
		
		try{
			$('#userDiv').find('img').prop("src","images/chk_off.png");
			$('#userDiv :checkbox').prop("class","tri").removeAttr('checked');
		}catch (e) {
			showMessage("Error", e.message);
		}
	});
});

function fetchUserRole(user_id){
	try{
		$.ajax({
			type : 'post',
			url : 'UserRole.do',
			data : {'user_id' : user_id},
			success : function(responseJson){
				if(responseJson != null){
					displayUserRole(responseJson);
					clearAll();
				}else{
					showMessage("Error", "No Data Found.");
				}
			}
		});
	}catch (e) {
		showMessage("Error", e.message);
	}
}

function displayUserRole(responseJson){
	try{
		responseJson = $.parseJSON(responseJson);
		Menu = responseJson.Menu;
		$('#userDiv').empty();
		var cols= '<ul id="tristate">';
		$(Menu).each(function (index, object){
			cols += '<li style="padding-left: 25px;">';
			cols += '	<input type="checkbox" id="u_checkbox'+object.page_id+'" name="u_checkbox'+object.page_id+'" style="display:none;" class="tri" value="'+object.page_id+'" />&nbsp;&nbsp;'+object.page_name;
			cols += '		<ul>';
			$.each(object.Child, function(index, object){
				cols += '		<li style="padding-left: 25px;">';
				cols += '			<input type="checkbox" id="u_checkbox'+object.page_id+'" name="u_checkbox'+object.page_id+'" style="display:none;" class="tri" value="'+object.page_id+'" />&nbsp;&nbsp;'+object.page_name;
				if(object.hasOwnProperty('SubChild')){
					cols += '			<ul>';
					$.each(object.SubChild, function(index, object){
						cols += '			<li style="padding-left: 25px;">';
						cols += '				<input type="checkbox" id="u_checkbox'+object.page_id+'" name="u_checkbox'+object.page_id+'" style="display:none;" class="tri" value="'+object.page_id+'" />&nbsp;&nbsp;'+object.page_name;
						cols += '			</li>';
					});
					cols += '			</ul>';
				}
				cols += '		</li>';
			});
			cols += '		</ul>';
			cols += '	</li>';
		});
			cols += '</ul>';

		$('#userDiv').append(cols);

		$("input:checkbox").transformCheckbox({
			checked: "images/chk_on.png", // Checked image
			unchecked: "images/chk_off.png", // Unchecked image
			tristateHalfChecked : "images/chk_tri.png", // Tri-state image
			changeHandler: function (is_checked) { }, // handler when checkbox is clicked
			trigger: "self", // Can be self, parent
			tristate : true // Use tri state? need to be ul > li > checkbox // ul > li > ul > li > checkbox
			});
		
		if($(Menu).length == 0){
			$('#userDiv').empty();
			$('#userDiv').append('<table align="center" width="100%"><tr><td align="center">No Roles Assigned Yet.</td></tr></table>');
		}
	}catch (e) {
		showMessage("Error", e.message);
	}
}

function assignRole(){
	try{
		var user_id = $.trim($('#user_id').val());
		if(user_id != '' && user_id != undefined){
			var roles = [];
			$('#allDiv :checkbox').each(function(){
				var image = $(this).closest('li').find('img').attr('src').replace( /^.*?([^\/]+)\..+?$/, '$1' );
				if(image == 'chk_on' || image == 'chk_tri'){
					roles.push($(this).val());
				}
			});
			if(roles.length > 0){
				$.ajax({
					type : 'POST',
					url : 'AssignRole.do',
					traditional : true,
					data : {roles : roles, user_id : user_id},
					success : function(response){
						fetchUserRole(user_id);
						showMessage("Info", response);
					}/*,
					error : function(e){
						showMessage("Error", e);
					}*/
				});
			}else{
				showMessage("Info", "No Roles Selected for Assigning.");
			}
		}else{
			showMessage("Error", "No User Found to Assign Roles.");
		}
	}catch (e) {
		showMessage("Error", e.message);
	}
}

function revokeRole(){
	try{
		var user_id = $.trim($('#user_id').val());
		if(user_id != '' && user_id != undefined){
			var roles = [];
			$('#userDiv :checkbox').each(function(){
				var image = $(this).closest('li').find('img').attr('src').replace( /^.*?([^\/]+)\..+?$/, '$1' );
				if(image == 'chk_on'){
					roles.push($(this).val());
				}
			});
			if(roles.length > 0){
				$.ajax({
					type : 'post',
					url : 'RevokeRole.do',
					traditional : true,
					data : {roles : roles, user_id : user_id},
					success : function(response){
						fetchUserRole(user_id);
						showMessage("Info", response);
					}/*,
					error : function(e){
						showMessage("Error", e);
					}*/
				});
			}else{
				showMessage("Info", "No Roles Selected for Revoking.");
			}
			
		}else{
			showMessage("Error", "No User Found to Revoke Roles.");
		}
	}catch (e) {
		showMessage("Error", e.message);
	}
}

function clearAll(){
	$('body').find('input:checkbox').removeAttr('checked');
	$('#allDiv').find('img').prop("src","images/chk_off.png");
	$('#allDiv :checkbox').prop("class","tri").removeAttr('checked');
}
