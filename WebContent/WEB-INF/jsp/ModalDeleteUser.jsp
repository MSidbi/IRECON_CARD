<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript" src="js/admin.usermanager.js"></script>
<form:form action="DeleteUser.do" method="post" commandName="user">
	<table align="center" cellpadding="0" cellspacing="0" style="width: 100%; color: red">
		<tr>
			<th style="padding-top: 15px;" align="center">
				This user and all assigned roles will be deleted.
			</th>
		</tr>
		<tr>
			<th align="center" style="margin-left: 100px;padding-top:10px;padding-bottom: 35px;">
				Are you sure ?
				<form:hidden path="user_id" />
			</th>
		</tr>
		<tr>
			<td align="center" class="footerBtns">
				<input type="button" name="delete" id="delete" value="Delete">
				<input type="button" name="cancel" id="cancel" value="Cancel">
			</td>
		</tr>
	</table>
</form:form>