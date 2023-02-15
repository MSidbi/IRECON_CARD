<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<link href="css/fancyform.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="js/ViewConfigType.js"></script>
</head>

<body>
<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text">View CONFIGURATION</div>
			</div>
</div>
<form:form commandName="ConfigBean">
 <table align="center" cellpadding="2" cellspacing="0" border="0" id="datatbl" class="table" width="100%">
 
 
 <tr >
		<td align="right">&nbsp;&nbsp;&nbsp;&nbsp;Category :</td>
		
		<td align="left">
			<%-- <form:input path="stCategory" maxlength="6" cssClass="file_name" /> --%>
			<form:select path="stCategory" id="stCategory"
						onchange="return setupValue();">
						<form:option value="">--Select -</form:option>
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

<tr id="trsubcat" style="display:none" >
		<td align="right">&nbsp;&nbsp;&nbsp;&nbsp;SubCategory :</td>
		
		<td align="left">
			<%-- <form:input path="stCategory" maxlength="6" cssClass="file_name" /> --%>
			<form:select path="stSubCategory" id="stSubCategory" onchange="return setupValue();">
			<form:option value="-">--Select -</form:option>
			<form:option value="DOMESTIC">DOMESTIC</form:option>
			<form:option value="INTERNATIONAL">INTERNATIONAL</form:option>
			<form:option value="SURCHARGE">SURCHARGE</form:option>
			<form:option value="ISSUER">ISSUER</form:option>
			<form:option value="ACQUIRER">ACQUIRER</form:option>
			<form:option value="ONUS">ONUS</form:option>
			</form:select>
		</td>

</tr>
 
 
<tr id="trfilelist" style="display: none;">
		<td align="right">&nbsp;&nbsp;&nbsp;&nbsp;File Name :</td>
		 
		<td align="left">
				<form:select path="inFileId"  id="fileList"> </form:select>
			
					<input type="hidden" id="headerlist" value="">
					
					<form:hidden path="stFileName" id="stFileName"/>
		</td>

</tr>


<tr>
  <td><input class="form-button" type="button" value="Get Classification Details " id="getdtls" onclick="getDetails();"/>
  
  </td>
  </tr>
  <tr>
  <td><input class="form-button" type="button" value="Get Knock off Details " id="getknokoffdtls" onclick="getknckoffDetails();"/>
  </td>
  <!-- <td><input type="submit" value="Submit" id="submit"/></td> -->
  
</tr>
</table> 

 </form:form>
 </body>
 </html>
 