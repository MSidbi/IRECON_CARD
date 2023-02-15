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
<script type="text/javascript" src="js/viewFileSource.js"></script>
<script type="text/javascript" src="js/commonScript.js"></script>

<script type="text/javascript">

	

</script>
</head>
<body>

<div class="container">
			<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text" align="center">View File Details</div>
			</div>
			</div>
				<form:form  commandName="ConfigBean"  >
					
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
										<td align="left" style="padding-left: 10px"> <form:input type="text" value="" path="stFileName" id="fileName" maxlength="100" onkeypress="return setValueType(this,'search')" readonly="true"   title="File Name"  size="23px"/>
											<!-- <input type="button" value="List" style="background-image:  " onclick="getdata();">  -->
											<img alt="" src="images/listbtn.png" title="File List" onclick="viewfiledata();" style="vertical-align:middle; height: 20px; width: 20px;">
											<form:input type="hidden" value="0" path="inFileId" id="fileId"></form:input>
										</td>
										
									</tr>
									<tr>
										<td align="center" >Status <label style="padding-left: 110px"> :</label></td>
										<td style="padding-left: 20px"><input type="checkbox" id="chkstat" readonly="readonly" disabled="disabled" onchange="changeStatus();"> Active
										<form:hidden path="activeFlag" id="activeFlag" value="" />
										</td>
									</tr>
									
									<tr>
										<td align="center">Data Separator <label style="padding-left: 60px">:</label> </td>
										<td align="center" style="padding-left: 10px"> <form:input type="text" value="" path="dataSeparator" readonly="true" id="dataSeparator" title="Data Separator" onkeypress="return setValueType(this,'seperator')"  maxlength="1" size="23px"/> <font color="red">*Only '|' And Space are allowed.</font> </td><td align="right"></td>
									</tr>
									<tr>
										 <td align="center">Read Data From <label style="padding-left: 50px">: </label></td>
										<td style="padding-left: 10px" > <form:input path="rdDataFrm" type="text" value=""  id="rdDataFrm" maxlength="3" readonly="true" onkeypress="return setValueType(this,'numeric')"  title="Read Data From"  size="15px"/><font color="black">(Line No.)</font></td>
										
									</tr>
									<tr>
										 <td align="center">Starting Character Pattern<label style="padding-rigth: 10px"> :    </label></td>
										<td align="left" style="padding-left: 10px"> <form:input path="charpatt" type="text" value="" readonly="true" disabled="disabled"  id="charpatt" maxlength="100" onkeypress="return setValueType(this,'pattern')"  title="Character Pattern"  size="23px"/></td>
									
									</tr>
									
								</table>
								</div>
							
						</td>
					</tr>
						
							<tr>
								<td align="center"> 
									<input class="form-button" type="button" value="View Header" onclick="return viewHeaders();">
									
									<input class="form-button" type="button" value="Clear" onclick="clearSourceData();">
									
								</td>
							</tr>
				</table>
					 
				</form:form>
			</div>


</body>
</html>