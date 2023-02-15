<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="js/ViewCompareDetails.js"></script>

<title>View Compare Details</title>
</head>
<body onload="noBack();" >

	<div class="jtable-main-container">
				<div class="jtable-title">
					<div class="jtable-title-text">Search Details</div>
				</div>
	</div>
	<form:form id="reconform" name="form" commandName="CompareSetupBean">
		<table align="center" class="table" >
			<tr>
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Category </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="category" id="category" name="category" onchange=" displaydtls(this);">
						<form:option value="">--Select--</form:option>
						<form:option value="ONUS">ONUS</form:option>
						<form:option value="RUPAY">RUPAY</form:option>
						<form:option value="AMEX">AMEX</form:option>
						<form:option value="VISA">VISA</form:option>
						<form:option value="MASTERCARD">MASTERCARD</form:option>
						<form:option value="CARDTOCARD">CARD TO CARD</form:option>
						<form:option value="POS">ONUS POS</form:option>
					</form:select>
				</td>
			</tr>
			<tr id="trsubcat">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;SubCategory </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="stSubCategory" id="stSubCategory" name="stSubCategory" >
						<form:option value="">--Select--</form:option>
						<form:option value="DOMESTIC">DOMESTIC</form:option>
						<form:option value="INTERNATIONAL">INTERNATIONAL</form:option>
						<form:option value="SURCHARGE">SURCHARGE</form:option>
						<form:option value="ISSUER">ISSUER</form:option>
						<form:option value="ACQUIRER">ACQUIRER</form:option>
						<form:option value="ONUS">ONUS</form:option>
					</form:select>
				</td>
			</tr>
			
			<tr id="filelist" style="display: none">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Files </th>
				<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
				<td><select id="selFillist">
					<option value="">--SELECT--</option>
				</select>
				</td>
				
			</tr>
			<tr><td><input type="button" class="form-button" value="See Files" onclick="getFiles();"></td>
				<td id="getDetails" style="display: none"><input type="button" class="form-button" value="Get Details" onclick="getCompareDetails();"></td>
			</tr>
			</table>
		</form:form>	

</body>
</html>