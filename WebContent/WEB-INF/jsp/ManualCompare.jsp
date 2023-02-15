<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" />
<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/compareFile.js"></script>



<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Manual Compare</title>
<script type="text/javascript">
$(document).ready(function() {
	
	//alert("click");
  
    $("#datepicker").datepicker({dateFormat:"dd/mm/yy", maxDate:0});
    });



</script>
</head>
<body>

<form:form id="uploadform"  action="manualCompareFiles.do" method="POST" commandName="CompareSetupBean" enctype="multipart/form-data">

<div class="jtable-main-container">
				<div class="jtable-title">
					<div class="jtable-title-text">Compare Files</div>
				</div>
</div>

<table align="center" cellpadding="2" cellspacing="0" border="0" id="datatbl" class="table" width="100%">
	
	<tr class="evenRow">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Compare level </th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
			<td align="left" colspan="11">
					<form:select path="compareLvl" id="compareLvl">
					<form:option value="0" >---Select---</form:option>
							<form:option value="2" >Two</form:option>
							<%-- <form:option value="3" >Three</form:option> --%>
					</form:select>
						
			</td>
			
	
	</tr>
	
	<tr class="evenRow">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;First File </th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
			<td align="left" colspan="11">
					<form:select path="compareFile1" id="compareFile1">
					<form:option value="0" >---Select---</form:option>
						<c:forEach var="configfilelist" items="${configBeanlist}">
							<form:option id="${configfilelist.inFileId}" value="${configfilelist.inFileId}" >${configfilelist.stFileName}</form:option>
							</c:forEach>
						</form:select> <img alt="" src="images/listbtn.png" title="Last Uploaded File" onclick="getupldfiledetails();" style="vertical-align:middle; height: 20px; width: 20px;"> 
						
						<form:hidden path="stFileName" id="stFileName"/>
			</td>
			
	
	</tr>
	<tr class="evenRow">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Second File </th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
			<td align="left" colspan="11">
					<form:select path="compareFile2" id="compareFile2">
					<form:option value="0" >---Select---</form:option>
						<c:forEach var="configfilelist" items="${configBeanlist}">
							<form:option id="${configfilelist.inFileId}" value="${configfilelist.inFileId}" >${configfilelist.stFileName}</form:option>
							</c:forEach>
						</form:select>
			</td>
			
	
	</tr>
	
	
	<tr class="oddRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Date</th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
		<td><form:input path="fileDate" readonly="readonly" id="datepicker"  placeholder="dd/mm/yyyy"/></td>
	
	</tr>
	
	
	
	


</table>
 
<input class="form-button" id="compare" type="submit" value="COMPARE" onclick="return validatedtls();" >

<div id="overlay" align="center" style="display: none">

	<label><font color="green" size="8px" > Please Wait...</font></label>

</div>



</form:form>



</body>
</html>