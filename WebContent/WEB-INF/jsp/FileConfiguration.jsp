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
<script type="text/javascript" src="js/fileConfiguration.js"></script>
<script type="text/javascript" src="js/commonScript.js"></script>  
<title>File Configuration</title>


</head>
<body>
<div class="jtable-main-container">
				<div class="jtable-title">
					<div class="jtable-title-text">File Configuration</div>
				</div>
			</div>
<form:form name="form" action="addConfiguration.do" method="POST" commandName="ConfigBean">  
	
					<table align="center" class="table">
					
									<tr>
										<td align="center">FileName<font color="red">*</font> <label style="padding-left: 80px">:</label></td>
										<td align="center" style="padding-left: 25px"> <form:input path="stFileName" value=""  id="stFileName" maxlength="15" onkeypress="return setValueType(this,'file')"  title="File Name"  size="23px"/>
											<!-- <input type="button" value="List" style="background-image:  " onclick="getdata();">  -->
									
										</td>
										
									</tr>
									<tr>
										<td align="center" >Status <label style="padding-left: 105px">:</label></td>
										<td style="padding-left: 30px"><input type="checkbox" id="chkstat" onchange="changeStatus();"> Active
										<form:hidden path="activeFlag" id="activeFlag" value="I" />
										</td>
									</tr>
									
									<tr>
										<td align="center" >Category <label style="padding-left: 90px">:</label></td>
										<td align="left" style="size: 100px ;padding-left:25px" >
											<form:select path="stCategory" id="stCategory" >
						<form:option value="select">--SELECT--</form:option>
						<form:option value="ONUS">ONUS</form:option>
						<form:option value="RUPAY">RUPAY</form:option>
						<form:option value="AMEX">AMEX</form:option>
						<form:option value="VISA">VISA</form:option>
						<form:option value="MASTERCARD">MASTERCARD</form:option>
						<form:option value="CARDTOCARD">CARD TO CARD</form:option>
						<form:option value="POS">ONUS POS</form:option>
						<%-- <form:option value="AMEX">AMEX</form:option> --%>
											
												
											</form:select>
										
										</td>
										
									</tr>
									<tr>
										<td align="center" >Sub Category <label style="padding-left: 70px">:</label></td>
										<td align="left" style="size: 100px ;padding-left:25px" >
											<form:select path="stSubCategory" id="stSubCategory" >
												<form:option value="-">--SELECT--</form:option>
												<form:option value="DOMESTIC">DOMESTIC</form:option>
												<form:option value="INTERNATIONAL">INTERNATIONAL</form:option>
												<form:option value="SURCHARGE">SURCHARGE</form:option>
												<form:option value="ISSUER">ISSUER</form:option>
												<form:option value="ACQUIRER">ACQUIRER</form:option>
												
											
												
											</form:select>
										
										</td>
										
									</tr>
									
									<tr>
										<td align="center">File Type<font color="red">*</font> <label style="padding-left: 85px">:</label></td>
										<td align="left" style="size: 100px ;padding-left:25px" >
											<form:select path="fileType" id="filetype" >
												<form:option value="select">--SELECT--</form:option>
												<form:option value="txt">TXT</form:option>
												<form:option value="xml">XML</form:option>
												<form:option value="bmp">BITMAP</form:option>
											</form:select>
										
										</td>
										<!-- <td><input  type="button" class="form-button"  value="Add File pattern" style="width: 120px"></td> -->
									</tr>
									
									<tr>
										 <td align="center">Read Data From<label style="padding-left: 55px">:</label></td>
										<td align="center" style="padding-left: 20px"> <form:input path="rdDataFrm" type="text" value=""  id="rdDataFrm" maxlength="3" onkeypress="return setValueType(this,'numeric')"  title="Read Data From"  size="23px"/></td><td><font color="black">(Line No.)</font></td>
										
									</tr>
									
									<tr>
										 <td align="center">Starting Character Pattern <label style="padding-left: 5px">:</label></td>
										<td align="center" style="padding-left: 20px"> <form:input path="charpatt" type="text" value=""  id="charpatt" maxlength="100" onkeypress="return setValueType(this,'pattern')"  title="Character Pattern"  size="23px"/></td>
									
									</tr>
									
									<%-- <tr>
										 <td align="center">Table Name <font color="red"> * </font>   <label style="padding-left: 32px">:</label></td> 
										<td align="center" style="padding-left: 25px"> <form:input type="text" value="" path="tableName" id="tableName" title="Table Name" onkeypress="return setValueType(this,'file')" size="23px"/> </td>
									</tr> --%>
									<%-- <tr>
										<!-- <td align="center">Table Headers :</td> -->
										<td align="center" style="padding-left: 20px"> <form:input type="text" value="" path="tblHeader" id="tblHeader" title="Table Headers" size="23px"/> </td>
									</tr> --%>
									<tr>
										<td align="center"><label> CLASSIFICATION </label><label style="padding-left: 40px"> : </label></td>
										
										<td><input type="checkbox" id="chkclassify_off" onchange="setclassValue(this)" style="padding-left: 20px"> 
										<form:hidden path="classify_flag" value="N"/></td>
										
									</tr>
									<tr>
										<td align="center"><label> KNOCK_OFF </label><label style="padding-left: 70px">:</label></td>
										<td><input type="checkbox" id="chkknock_off" onchange="setKnockValue(this)" style="padding-left: 20px">
										<td><form:hidden path="knock_offFlag" value="N"/></td>
										
									</tr>
									<tr>
										<td align="center"><label> Use Previous Table </label><label style="padding-left: 40px"> : </label></td>
										<td> <input type="checkbox" id="chkprevtbl" onchange="setprev_tbl(this)" style="padding-left: 20px"> </td>
										<td><form:hidden path="prev_tblFlag" value="N"/></td>
									
									</tr>
									<tr id="trprevtable" style="display: none">
										<td align="center">Select Previous Table <label style="padding-left: 40px"> : </label> </td>
										<td><form:select path="prev_table" style="padding-left: 20px">
											<form:option value="">--Select--</form:option>	
											<c:forEach var="tbl_list" items="${tbl_list}">
												<form:option value="${tbl_list}" >${tbl_list}</form:option>
											</c:forEach>
											
										</form:select> </td>
									
									</tr>
									
									
									<tr>
										<td align="center">Data Separator<!-- <font color="red"></font> -->   <label style="padding-left: 55px">:</label> </td>
										<td align="center" style="padding-left: 25px"> <form:input type="text" value="" path="dataSeparator" id="dataSeparator" title="Data Separator" onkeypress="return setValueType(this,'seperator')"  maxlength="1" size="23px"/></td> <td><font color="red">*Only '|' And Space are allowed.</font></td>
									</tr>
									<tr id="trhdr">
										 <td align="center">Enter No. OF File Headers <font color="red">*</font>  : </td>
										<td align="center" style="padding-left: 25px"><input type="text" size="23px" id="colsize" value="0" onkeypress="return setValueType(this,'numeric')"></td>
											<td><input class="form-button" type="button" value="Name Headers" style="width: 130px" onclick="return crtcols()">
											<form:input type="hidden" path="stHeader" id="tblHeader"  value=""/>
										 </td>
									</tr>
									<tr id="hdrrow"style="display: none" align="center">
									<td></td>
										<td align="center">
											<table id="hdrtbl" align="center" border="1" rules="all">
												
											</table>
										</td>
									</tr>
									
								 <tr>
									<td><input type="submit" class="form-button" value="Save"   onclick="return addtblheader();"> </td>
								</tr>
					
					
					</table>
				
			</div>
			
			
		</div>
		
</form:form>
</body>
</html>