<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Headers</title>

</head>
<body>

<form>

<table border="2" frame="void" style="background-color: #d5dbdb;" >

	<tr>
		
		<td style="size: 12px;"><font size="2" face="" >Headers</font></td>
		
		
	</tr>
	
	<c:forEach var="headerlist" items="${headerlist}">
	<tr>
	
		
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${headerlist}" value="${headerlist }"></font>
			
		</td>
		
	
	</tr>
	</c:forEach>


</table>

</form>
</body>
</html>