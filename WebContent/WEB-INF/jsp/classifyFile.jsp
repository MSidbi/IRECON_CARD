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
<script type="text/javascript" src="js/classifyFile.js"></script>



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

<form:form id="uploadform"  action="ManualclassifyFile.do" method="POST" commandName="CompareSetupBean" >

<div class="jtable-main-container">
				<div class="jtable-title">
					<div class="jtable-title-text"> File Filtration</div>
				</div>
</div>

<table align="center" cellpadding="2" cellspacing="0" border="0" id="datatbl" class="table" width="100%">
	<tr class="evenRow">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;File Name</th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
			<td align="left" colspan="11">
					<form:select path="inFileId" id="fileList" onchange="setfilename(this);">
					<form:option value="0" >---Select---</form:option>
						<c:forEach var="configfilelist" items="${configBeanlist}">
							<form:option id="${configfilelist.inFileId}" value="${configfilelist.inFileId}" >${configfilelist.stFileName}</form:option>
							</c:forEach>
						</form:select> <img alt="" src="images/listbtn.png" title="Last Uploaded File" onclick="getfiledetails();" style="vertical-align:middle; height: 20px; width: 20px;"> 
						<input type="hidden" id="headerlist" value="">
						
						<form:hidden path="stFileName" id="stFileName"/>
			</td>
			
	
	</tr>
	<tr id="rowtype" class="oddRow" style="display: none;">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;File Type</th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
		<td><form:select path="stfileType"  id="stfileType">
		
			<form:option value="Domestic">Domestic</form:option>
			<form:option value="International">International</form:option>
		
		</form:select></td>
	
	</tr>
	<tr class="category">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Category</th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
		<td><form:select path="category" id="category">
			<form:option value="">--Select -</form:option>
			<form:option value="ONUS">ONUS</form:option>
			<%-- <form:option value="ACQUIRER">ACQUIRER</form:option>
			<form:option value="ISSUER">ISSUER</form:option>
			<form:option value="RUPAY">RUPAY</form:option> --%>
			</form:select></td>
	
	</tr>
	
	<tr class="oddRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Date</th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
		<td><form:input path="fileDate" readonly="readonly" id="datepicker"  placeholder="dd/mm/yyyy"/></td>
	
	</tr>
	
	


</table>
 

<input class="form-button" type="submit" value="Classify" id="classify" onclick="return validateclassify();" >

</form:form>

<div id="overlay" align="center" style="display: none">

	<label><font color="green" size="8px" > Please Wait...</font></label>

	</div>



</body>
</html>