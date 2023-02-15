<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>FTP Configuration</title>

</head>
<body>

	<div class="container">
			<div class="login">
			<h3>FTP Configuration</h3>
					<table align="center">
					
						<tr>
							<td align="center">File Name<font color="red"> * </font> <label style="padding-left: 20px">:</label></td> 
							<td align="center"><form:input path="stFileName" id="stFileName1"  maxlength="15" onkeypress="setValueType(this,'file')" onchange="changeValue();" size="23px"/></td>
							
						</tr>
						<tr>
							<td align="center">IP Address<font color="red"> * </font> <label style="padding-left: 15px">:</label> </td>
							<td align="center"><form:input type="text" value="" path="fileLocation" id="fileLocation" title="FTP File Location" maxlength="300" onkeypress="return setValueType(this,'ip')" size="23px"/></td><td>(FTP Server) </td>
						</tr>
						<tr>
							<td align="center">FTP File Path<font color="red"> * </font> <label style="padding-left: 5px">:</label> </td>
							<td align="center"> <form:input type="text" value="" path="filePath" id="filePath" maxlength="100" title="FTP File Path"  onkeypress="return setValueType(this,'filepath')" size="23px"/> </td>
						</tr>
						<tr>
							<td align="center">FTP Port<font color="red"> * </font> <label style="padding-left: 28px">:</label></td>
							<td align="center"> <form:input type="text" value="" path="ftpPort" maxlength="5" id="ftpPort" title="FTP Port" onkeypress="return setValueType(this,'numeric')" size="23px"/> </td>
						</tr>
						<tr>
							<td align="center">FTP Username<font color="red">*</font> :</td>
							<td align="center"> <form:input type="text" value="" path="ftpUser" id="ftpUser" title="FTP Username"  maxlength="100" size="23px"/> </td>
						</tr>
						<tr>
							<td align="center">FTP Password<font color="red">*</font> :</td>
							<td align="center" style="padding-left: 20px"> <form:input type="password" value="" path="ftpPwd" id="ftpPwd" title="FTP Password" maxlength="15" size="23px"/> 
							<img alt="Logo" id="rdpwd" src="images/showpwd.png" onclick="changetxt()" title="Show Password"  style="vertical-align:middle; height: 20px; width: 20px;" />
							<input type="hidden" id="hdpwd" value="Show Password">
							</td>
						</tr>
						
					
					
					</table>
				
			</div>
		</div>
		

</body>
</html>