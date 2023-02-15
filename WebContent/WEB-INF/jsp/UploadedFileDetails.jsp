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
<title>File Details</title>

</head>
<body>

<form>




<table border="2" frame="void" style="background-color: #d5dbdb;" class="table">
<th style="background-color: #1484e6" width="100%" colspan="9" align="center" >File Details</th>
	<tr>
		
		<td style="size: 12px;"><font size="2" face="" >FILE NAME</font></td>
		<td style="size: 12px;"><font size="2" face="" >FILE DATE</font></td>
		<td style="size: 12px;"><font size="2" face="" >CATEGORY</font></td>
		<td style="size: 12px;"><font size="2" face="" >SUB CATEGORY</font></td>
		<td style="size: 8px;"><font size="2" face="" >UPLOAD_FLAG</font></td>
		<td style="size: 8px;"><font size="2" face="" >MANUAL_UPLOAD_FLAG</font></td>
		<td style="size: 8px;"><font size="2" face="" >FILTER_FLAG</font></td>
		<td style="size: 8px;"><font size="2" face="" >KNOCKOFF_FLAG</font></td>
		<td style="size: 8px;"><font size="2" face="" >COMAPRE_FLAG</font></td>
		<td style="size: 12px;"><font size="2" face="" >UPLOADED BY</font></td>
		<td style="size: 12px;"><font size="2" face="" >UPLOADED DATE</font></td>
		
		
		
		
	</tr>
	
	<c:forEach var="setupBeans" items="${setupBeans}">
	<tr>
	
		
		<td style="size: 12px;" align="center"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${setupBeans.stFileName}" value="${setupBeans.stFileName }"></font></td>
		<td style="size: 12px;" align="center"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${setupBeans.fileDate}" value="${setupBeans.fileDate }"></font></td>
		<td style="size: 12px;" align="center"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${setupBeans.category}" value="${setupBeans.category }"></font></td>
		<td style="size: 12px;" align="center"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${setupBeans.stSubCategory}" value="${setupBeans.stSubCategory }"></font></td>
		<td style="size: 8px;" align="center"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${setupBeans.upload_Flag}" value="${setupBeans.upload_Flag }"></font></td>
		<td style="size: 8px;" align="center"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${setupBeans.manupload_flag}" value="${setupBeans.manupload_flag }"></font></td>
		<td style="size: 8px;" align="center"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${setupBeans.filter_Flag}" value="${setupBeans.filter_Flag }"></font></td>
		<td style="size: 8px;" align="center"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${setupBeans.knockoff_Flag}" value="${setupBeans.knockoff_Flag }"></font></td>
		<td style="size: 8px;" align="center"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${setupBeans.comapre_Flag}" value="${setupBeans.comapre_Flag }"></font></td>
		<td style="size: 12px;" align="center"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${setupBeans.createdBy}" value="${setupBeans.createdBy }"></font></td>
		<td style="size: 12px;" align="center"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${setupBeans.entry_date}" value="${setupBeans.entry_date }"></font></td>
		

	</tr>
	</c:forEach>


</table>




</form>
</body>
</html>