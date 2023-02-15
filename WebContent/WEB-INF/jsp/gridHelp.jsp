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
<title>Grid Help</title>
<script type="text/javascript" src="js/gridHelp.js"></script>
</head>
<body>

<form>

<table border="2" frame="void" style="background-color: #d5dbdb;" >

	<tr>
		<td style="size: 12px;"><font size="2" face="" >Category</font></td>
		<td style="size: 12px;"><font size="2" face="" >Sub Category</font></td>
		<td style="size: 12px;"><font size="2" face="" >File Name</font></td>
		<td style="size: 12px;"><font size="2" face="" >File Name</font></td>
		<td style="size: 12px;"><font size="2" face="" >Data Separator</font></td>
		<td style="size: 12px;"><font size="2" face="" >Read Data From</font></td>
		<td style="size: 12px;"><font size="2" face="" >Starting Character Pattern</font></td>
		
		<!-- <td style="size: 12px;"><font size="2" face="" >Status</font></td> -->
	</tr>
	
	<c:forEach var="configfilelist" items="${configBeanlist}">
	<tr onclick="getdata(${configfilelist.inFileId})">
	
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="stCategory${configfilelist.inFileId}" value="${configfilelist.stCategory }"></font>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="stSubCategory${configfilelist.inFileId}" value="${configfilelist.stSubCategory }"></font>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="fileName${configfilelist.inFileId}" value="${configfilelist.stFileName }"></font>
			<input type="hidden" disabled="disabled" id="fileid${configfilelist.inFileId}" value="${configfilelist.inFileId}"> 
		</td>
		<td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="dataSeparator${configfilelist.inFileId}" value="${configfilelist.dataSeparator}"></font></td>
		 <td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="rdDataFrm${configfilelist.inFileId}" value="${configfilelist.rdDataFrm}"></font></td>
		 <td style="size: 12px;"><font size="1" face="" ><input type="text" disabled="disabled" id="charpatt${configfilelist.inFileId}" value="${configfilelist.charpatt}"></font></td>
		
		<td  style="size: 12px;"><input type="text" disabled="disabled" id="activeFlag${configfilelist.inFileId}" value="${configfilelist.activeFlag}"></td>
		
	
	</tr>
	</c:forEach>


</table>

</form>
</body>
</html>