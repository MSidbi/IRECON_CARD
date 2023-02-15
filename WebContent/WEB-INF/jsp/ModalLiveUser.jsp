<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<div class="jtable-main-container" >
	<div class="jtable-title" >
		<div class="jtable-title-text" style="height: 33px;">LIVE USER MANAGER</div>
	</div>
</div>
<table id="userTable" align="center" cellpadding="2" cellspacing="0" border="0" width="100%">
	<tr class="footerBtns">
		<th>User Id</th>
		<th class="leftSolid">User Name</th>
		<th class="leftSolid">Login In</th>
		<th class="leftSolid">IP Address</th>
		<th class="leftSolid">Action</th>
	</tr>
	<c:forEach var="user" items="${liveUserList}" varStatus="loop">
	<c:set var="rowClass" value="oddRow" />
	<c:if test="${loop.count %2 eq 0}">
		<c:set var="rowClass" value="evenRow" />
	</c:if>
	<tr class="${rowClass}">
		<td align="center">${user.user_id}</td>
		<td align="center" class="leftDotted">${user.user_name}</td>
		<td align="center" class="leftDotted">${user.in_time}</td>
		<td align="left" class="leftDotted">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${user.ip_address}</td>
		<td align="center" class="leftDotted"><img alt="End Session" style="cursor: pointer;" onclick="endUserSession('${user.user_id}')" src="images/end-session.png"></td>
	</tr>
	</c:forEach>
	<c:if test="${liveUserList eq null || liveUserList eq '' }">
		<tr class="oddRow">
			<td align="center" colspan="5">No Active User Found.</td>
		</tr>
	</c:if>
</table>