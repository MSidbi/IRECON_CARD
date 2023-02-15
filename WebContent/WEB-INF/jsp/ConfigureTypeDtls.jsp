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
<title>Configure Type Details</title>

</head>
<body>

<form>

<table border="2" frame="void" style="background-color: #d5dbdb;" >

	<tr>
		
		<td style="size: 12px;"><font size="2" face="" >FILE HEADER</font></td>
		<td style="size: 12px;"><font size="2" face="" >SEARCH_PATTERN</font></td>
		<td style="size: 12px;"><font size="2" face="" >PADDING</font></td>
		<td style="size: 12px;"><font size="2" face="" >START_CHARPOSITION</font></td>
		<td style="size: 12px;"><font size="2" face="" >END_CHARPOSITION</font></td>
		<td style="size: 12px;"><font size="2" face="" >CATEGORY</font></td>
		<td style="size: 12px;"><font size="2" face="" >CONDITION</font></td>
	</tr>
	
	<c:forEach var="comparedtllist" items="${comparedtllist}">
	<tr>
	
		
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${comparedtllist.stHeader}" value="${comparedtllist.stHeader }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${comparedtllist.stSearch_Pattern}" value="${comparedtllist.stSearch_Pattern }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${comparedtllist.stPadding}" value="${comparedtllist.stPadding }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${comparedtllist.inStart_Char_Position}" value="${comparedtllist.inStart_Char_Position }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${comparedtllist.inEnd_char_position}" value="${comparedtllist.inEnd_char_position }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${comparedtllist.stCategory}" value="${comparedtllist.stCategory }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${comparedtllist.condition}" value="${comparedtllist.condition }"></font></td>
				
	
	</tr>
	</c:forEach>


</table>

</form>
</body>
</html>