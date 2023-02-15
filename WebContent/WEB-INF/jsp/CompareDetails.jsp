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
<title>Recon Compare Details</title>

</head>
<body>

<form>

<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text" style="background-color: #1484e6">Recon Parameter</div>
			</div>


<table border="2" frame="void" style="background-color: #d5dbdb;" >

	<tr>
		
		<td style="size: 12px;"><font size="2" face="" >FILE HEADER</font></td>
		<td style="size: 12px;"><font size="2" face="" >PADDING</font></td>
		<td style="size: 12px;"><font size="2" face="" >STARTING CHAR POSITION</font></td>
		<td style="size: 12px;"><font size="2" face="" >CONDITION</font></td>
		<td style="size: 12px;"><font size="2" face="" >End CHAR POSITION</font></td>
		<td style="size: 12px;"><font size="2" face="" >FILE NAME</font></td>
		<td style="size: 12px;"><font size="2" face="" >ENTRY_BY</font></td>
		<td style="size: 12px;"><font size="2" face="" >ENTRY_DATE</font></td>
		
		
	</tr>
	
	<c:forEach var="recparamlist" items="${recparamlist}">
	<tr>
	
		
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${recparamlist.table_header}" value="${recparamlist.table_header }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${recparamlist.padding}" value="${recparamlist.padding }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${recparamlist.start_charpos}" value="${recparamlist.start_charpos }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${recparamlist.condition}" value="${recparamlist.condition }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${recparamlist.charsize}" value="${recparamlist.charsize }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${recparamlist.filename}" value="${recparamlist.filename }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${recparamlist.entry_by}" value="${recparamlist.entry_by }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${recparamlist.entry_date}" value="${recparamlist.entry_date }"></font></td>
	
	</tr>
	</c:forEach>


</table>
</div>

<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text" style="background-color: #1484e6">Recon Matching Criteria</div>
			</div>

<table border="2" frame="void" style="background-color: #d5dbdb;" >

	<tr>
		
		<td style="size: 12px;"><font size="2" face="" >FILE HEADER</font></td>
		<td style="size: 12px;"><font size="2" face="" >CATEGORY</font></td>
		<td style="size: 12px;"><font size="2" face="" >PADDING</font></td>
		<td style="size: 12px;"><font size="2" face="" >STARTING CHAR POSITION</font></td>
		<td style="size: 12px;"><font size="2" face="" >End CHAR POSITION</font></td>
		<td style="size: 12px;"><font size="2" face="" >FILE NAME</font></td>
		<td style="size: 12px;"><font size="2" face="" >MATCH ID</font></td>
		<td style="size: 12px;"><font size="2" face="" >ENTRY_BY</font></td>
		<td style="size: 12px;"><font size="2" face="" >ENTRY_DATE</font></td>
		
		
	</tr>
	
	<c:forEach var="matchcrtlist" items="${matchcrtlist}">
	<tr>
	
		
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcrtlist.table_header}" value="${matchcrtlist.table_header }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcrtlist.category}" value="${matchcrtlist.category }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcrtlist.padding}" value="${matchcrtlist.padding }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcrtlist.start_charpos}" value="${matchcrtlist.start_charpos }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcrtlist.charsize}" value="${matchcrtlist.charsize }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcrtlist.filename}" value="${matchcrtlist.filename }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcrtlist.match_id}" value="${matchcrtlist.match_id }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcrtlist.entry_by}" value="${matchcrtlist.entry_by }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcrtlist.entry_date}" value="${matchcrtlist.entry_date }"></font></td>
	
	</tr>
	</c:forEach>


</table>
</div>

<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text" style="background-color: #1484e6">Recon Matching Conditions</div>
			</div>


<table border="2" frame="void" style="background-color: #d5dbdb;" >

	<tr>
		
		<td style="size: 12px;"><font size="2" face="" >FILE HEADER</font></td>
		<td style="size: 12px;"><font size="2" face="" >CATEGORY</font></td>
		<td style="size: 12px;"><font size="2" face="" >PADDING</font></td>
		<td style="size: 12px;"><font size="2" face="" >START CHAR POSITION</font></td>
		<td style="size: 12px;"><font size="2" face="" >CONDITION</font></td>
		<td style="size: 12px;"><font size="2" face="" >End CHAR POSITION</font></td>
		<td style="size: 12px;"><font size="2" face="" >FILE NAME</font></td>
		<td style="size: 12px;"><font size="2" face="" >COMPARE VALUE</font></td>
		<td style="size: 12px;"><font size="2" face="" >ENTRY_BY</font></td>
		<td style="size: 12px;"><font size="2" face="" >ENTRY_DATE</font></td>
		
		
	</tr>
	
	<c:forEach var="matchcondlist" items="${matchcondlist}">
	<tr>
	
		
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcondlist.table_header}" value="${matchcondlist.table_header }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcondlist.category}" value="${matchcondlist.category }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcondlist.padding}" value="${matchcondlist.padding }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcondlist.start_charpos}" value="${matchcondlist.start_charpos }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcondlist.condition}" value="${matchcondlist.condition }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcondlist.charsize}" value="${matchcondlist.charsize }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcondlist.filename}" value="${matchcondlist.filename }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcondlist.pattern}" value="${matchcondlist.pattern }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcondlist.entry_by}" value="${matchcondlist.entry_by }"></font></td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${matchcondlist.entry_date}" value="${matchcondlist.entry_date }"></font></td>
	
	</tr>
	</c:forEach>


</table>
</div>


</form>
</body>
</html>