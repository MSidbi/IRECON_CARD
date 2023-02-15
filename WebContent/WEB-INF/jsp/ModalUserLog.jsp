<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<c:choose>
<c:when test="${not empty user_log_list && user_log_list != '' }">
<table align="center" cellpadding="2" cellspacing="0" border="0" width="100%">
	<tr class="footerBtns">
		<th>User Id</th>
		<th class="leftSolid">In Time</th>
		<th class="leftSolid">Out Time</th>
		<th class="leftSolid">Ip Address</th>
	</tr>
	<c:forEach items="${user_log_list}" var="user" varStatus="loop">
	<c:set var="rowClass" value="oddRow" />
	<c:if test="${loop.count %2 eq 0}">
		<c:set var="rowClass" value="evenRow" />
	</c:if>
	<tr class="${rowClass}">
		<td align="center">${user.user_id}</td>
		<td align="center" class="leftDotted">${user.in_time}</td>
		<td align="center" class="leftDotted">${user.out_time}</td>
		<td align="center" class="leftDotted">${user.ip_address}</td>
	</tr>
	</c:forEach>
</table>
</c:when>
<c:otherwise>
	<center><br/><b>No Record Found.</b></center>
</c:otherwise>
</c:choose>