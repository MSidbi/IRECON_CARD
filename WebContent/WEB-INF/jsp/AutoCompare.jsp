<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Auto Compare</title>
</head>
<body>
<form:form id="uploadform" name="form" commandName="CompareSetupBean">

<table align="center"  >
			<tr>
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Category </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="category" id="category" name="category">
						<form:option value="">--Select--</form:option>
						<form:option value="ONUS">ONUS</form:option>
						<form:option value="ACQUIRER">ACQUIRER</form:option>
						<form:option value="ISSUER">ISSUER</form:option>
					</form:select>
				</td>
			</tr>
			<tr class="evenRow">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp; Layer </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="layerCount" id="layerCount" onchange="displayContent(this);">
							<form:option value="">--Select--</form:option>
							<form:option value="2">TWO</form:option>
							<form:option value="3">THREE</form:option>
					</form:select>
				</td>
			</tr>
	</table>





</form:form>



</body>
</html>