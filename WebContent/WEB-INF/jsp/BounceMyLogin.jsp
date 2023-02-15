<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	 <!--    <link rel="stylesheet" href="css/login.css" > --> 
		<link rel="stylesheet" href="css/jquery-ui.css">
		<script type="text/javascript" src="js/jquery-1.9.1.js"></script>
		<script type="text/javascript" src="js/jquery-ui.js"></script>
		<script type="text/javascript" src="js/login.js"></script> 
	</head>
	<body>
	<% if(request.getAttribute("error")!=null){ %>
			<div align="center"  class="errorMsg"><%=request.getAttribute("error") %></div>
		<%} else {%>
	<form:form action="closeSession.do" method="post" commandName="loginBean">
	<table id="userTable" align="center" cellpadding="2" cellspacing="0" border="0" width="100%">
	  <tr class="footerBtns">
			<th class="leftSolid">User Id</th>
			<!-- <th class="leftSolid">Ip Address</th> -->
			<th class="leftSolid">In Time</th>
			<th class="leftSolid">Session</th>
			
		</tr>
	
	  <c:forEach var="listVar" items="${users}" varStatus="var"> 
	  <tr class="evenRow">
	   <td class="leftDotted" align="left" width="20%"><c:out value="${listVar.user_id}" />&nbsp;&nbsp;&nbsp;&nbsp;</td>
	  <%--  <td class="leftDotted" align="left" width="40%"><c:out value="${listVar.ip_address}" />&nbsp;&nbsp;&nbsp;&nbsp;</td> --%>
	   <td class="leftDotted" align="left" width = "60%"><c:out value="${listVar.in_time}" />&nbsp;&nbsp;&nbsp;&nbsp;</td>
	   <td align="center" class="leftDotted"><img alt="End Session" style="cursor: pointer;" onclick="endUserSession('${listVar.user_id}')" src="images/end-session.png"></td>
	   
	   </tr>
	   <tr class="oddRow"/>
	  </c:forEach>
	
	
	</table>
	</form:form>

	<%} %>	
		<c:if test="${success_msg != null}">
			<div align="center"  class="successMsg"><c:out value="${success_msg}" escapeXml="false" /></div>
		</c:if>
	</body>
</html>