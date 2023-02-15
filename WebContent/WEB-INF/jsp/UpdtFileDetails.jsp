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
<title>Update File Details</title>
<script type="text/javascript" src="js/jquery-1.9.1.js"></script>
<script type="text/javascript" src="js/jquery-ui.js"></script>
<script type="text/javascript" src="js/upldFileData.js"></script>
<script type="text/javascript" src="js/commonScript.js"></script>

<script type="text/javascript">

	

</script>
</head>
<body>

		<div class="jtable-main-container">
				<div class="jtable-title">
					<div class="jtable-title-text">Update File Details</div>
				</div>
		</div>

				<form:form action="saveFileData.do"  commandName="ConfigBean" method="POST" >
					
					<table align="center" class="table">
					
					<thead>	<tr style="background-color:  #d5dbdb">
						<td>
							<label style="text-align: right;background-color: #d5dbdb;">File Details</label>
							
						</td>
					</thead>
					<tr>
						<td>
								<div  id="fileDetails">						
								<table align="right" id="tblfileDetails">
									
									<tr>
										<td align="center">FileName  <label style="padding-left: 90px">:</label></td>
										<td align="left" style="padding-left: 10px"> <form:input type="text" value="" path="stFileName" id="fileName" maxlength="100" onkeypress="return setValueType(this,'search')" readonly="true"  title="File Name"  size="23px"/>
											<!-- <input type="button" value="List" style="background-image:  " onclick="getdata();">  -->
											<img alt="" src="images/listbtn.png" title="File List" onclick="getfiledata();" style="vertical-align:middle; height: 20px; width: 20px;">
											<form:input type="hidden" value="" path="inFileId" id="fileId"></form:input>
										</td>
										
									</tr>
									<tr>
										<td align="center" >Status <label style="padding-left: 110px"> :</label></td>
										<td style="padding-left: 20px"><input type="checkbox" id="chkstat" onchange="changeStatus()"> Active
										<form:hidden path="activeFlag" id="activeFlag" value="" />
										</td>
									</tr>
									
									<tr style="display: none;" >
										<td> <input type="text" id="stCategory" name="stCategory"> </td>
										<td> <input type="text" id="stSubCategory" name="stSubCategory"> </td>
									</tr>
									<%-- <tr>
										<!-- <td align="center">Table Name :</td> -->
										<td align="center" > <form:input type="hidden" value="" path="tableName" id="tableName" title="Table Name" size="23px"/> </td>
									</tr>
									<tr>
										<!-- <td align="center">Table Headers :</td> -->
										<td align="center" > <form:input type="hidden" value="" path="tblHeader" id="tblHeader" title="Table Headers" size="23px"/> </td>
									</tr> --%>
									<tr>
										<td align="center">Data Separator <label style="padding-left: 60px">:</label> </td>
										<td align="center" style="padding-left: 10px"> <form:input type="text" value="" path="dataSeparator" id="dataSeparator" title="Data Separator" onkeypress="return setValueType(this,'seperator')"  maxlength="1" size="23px"/> <font color="red">*Only '|' And Space are allowed.</font> </td><td align="right"></td>
									</tr>
									<tr>
										 <td align="center">Read Data From <label style="padding-left: 50px">: </label></td>
										<td style="padding-left: 10px" > <form:input path="rdDataFrm" type="text" value=""  id="rdDataFrm" maxlength="3" onkeypress="return setValueType(this,'numeric')"  title="Read Data From"  size="15px"/><font color="black">(Line No.)</font></td>
										
									</tr>
									<tr>
										 <td align="center">Starting Character Pattern<label style="padding-rigth: 10px"> :    </label></td>
										<td align="left" style="padding-left: 10px"> <form:input path="charpatt" type="text" value=""  id="charpatt" maxlength="100" onkeypress="return setValueType(this,'pattern')"  title="Character Pattern"  size="23px"/></td>
									
									</tr>
									
									
									
								</table>
								</div>
							
						</td>
					</tr>
						<%-- <thead>	<tr style="background-color:  #d5dbdb">
							<td>
								<label style="text-align: right;background-color: #d5dbdb;">FTP Details</label>
								<label id="filedtlsid" style="padding-left: 500px; size:10px;background-color: #d5dbdb;" onclick="chngsign();">-</label> 
							</td>
						</thead>
						<tr>
						<td>
								<div  id="ftpDetails">						
								<table align="center" id="tblftpDetails">
									
									<tr>
										<td align="center">FTP File Location : </td>
										<td align="center"><form:input type="text" value="" path="fileLocation" id="fileLocation" title="FTP File Location" maxlength="300" onkeypress="return setValueType(this,'ip')" size="23px"/> </td>
									</tr>
									<tr>
										<td align="center">FTP File Path : </td>
										<td align="center"> <form:input type="text" value="" path="filePath" id="filePath" maxlength="100" title="FTP File Path"  onkeypress="return setValueType(this,'filepath')" size="23px"/> </td>
									</tr>
									<tr>
										<td align="center">FTP Port :</td>
										<td align="center"> <form:input type="text" value="" path="ftpPort" maxlength="5" id="ftpPort" title="FTP Port" onkeypress="return setValueType(this,'numeric')" size="23px"/> </td>
									</tr>
									<tr>
										<td align="center">FTP Username :</td>
										<td align="center"> <form:input type="text" value="" path="ftpUser" id="ftpUser" title="FTP Username"  maxlength="100" size="23px"/> </td>
									</tr>
									<tr>
										<td align="center">FTP Password :</td>
										<td align="center" style="padding-left: 20px;"> <form:input type="password" value="" path="ftpPwd" id="ftpPwd" title="FTP Password" maxlength="15" size="23px"/> 
										<img alt="Logo" id="rdpwd" src="images/showpwd.png" onclick="changetxt()" title="Show Password"  style="vertical-align:middle; height: 20px; width: 20px;" />
										<input type="hidden" id="hdpwd" value="Show Password">
										</td>
									</tr>
								</table>
								</div>
							
						</td>
					</tr>
						 --%>
							<tr>
								<td align="center"> 
									<input class="form-button" type="submit" value="Update" onclick="return validateData();">
									
									<input class="form-button" type="button" value="Clear" onclick="clearData();">
									
								</td>
							</tr>
				</table>
					 
				</form:form>


</body>
</html>