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
<title>Knock Off Details</title>

</head>
<body>

<form>

<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text" style="background-color: #1484e6">KNOCKOFF COMPARE DETAILS</div>
			</div>
</div>

<table border="2" frame="void" style="background-color: #d5dbdb;" >

	<tr>
		
		<td style="size: 12px;"><font size="2" face="" >FILE HEADER</font></td>
		<td style="size: 12px;"><font size="2" face="" >Original Value</font></td>
		<td style="size: 12px;"><font size="2" face="" >Compare Value</font></td>
		
	</tr>
	
	<c:forEach var="knockofflist" items="${knockofflist}">
	<tr>
	
		
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${knockofflist.knockoff_col}" value="${knockofflist.knockoff_col }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${knockofflist.knockoff_OrgVal}" value="${knockofflist.knockoff_OrgVal }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${knockofflist.knockoff_comprVal}" value="${knockofflist.knockoff_comprVal }"></font></td>
	
	</tr>
	</c:forEach>


</table>

<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text" style="background-color: #1484e6">KNOCK OFF CRITERIA</div>
			</div>
</div>
<table border="2" frame="void" style="background-color: #d5dbdb;" >

	<tr>
		
		<td style="size: 12px;"><font size="2" face="" >FILE HEADER</font></td>
		<td style="size: 12px;"><font size="2" face="" >SEARCH_PATTERN</font></td>
		<td style="size: 12px;"><font size="2" face="" >PADDING</font></td>
		<td style="size: 12px;"><font size="2" face="" >START_CHARPOSITION</font></td>
		<td style="size: 12px;"><font size="2" face="" >END_CHARPOSITION</font></td>
		<td style="size: 12px;"><font size="2" face="" >CONDITION</font></td>
	</tr>
	
	<c:forEach var="knockoffcrtlist" items="${knockoffcrtlist}">
	<tr>
	
		
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${knockoffcrtlist.knockoff_header}" value="${knockoffcrtlist.knockoff_header }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${knockoffcrtlist.knockoffSrch_Pattern}" value="${knockoffcrtlist.knockoffSrch_Pattern }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${knockoffcrtlist.knockoff_stPadding}" value="${knockoffcrtlist.knockoff_stPadding }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${knockoffcrtlist.knockoffStart_Char_Pos}" value="${knockoffcrtlist.knockoffStart_Char_Pos }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${knockoffcrtlist.knockoffEnd_char_pos}" value="${knockoffcrtlist.knockoffEnd_char_pos }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${knockoffcrtlist.knockoff_condition}" value="${knockoffcrtlist.knockoff_condition }"></font></td>
				
	
	</tr>
	</c:forEach>


</table>



</form>
</body>
</html>