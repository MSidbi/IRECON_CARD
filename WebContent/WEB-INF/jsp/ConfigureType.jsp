<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="js/configure.forms.js"></script>
<script type="text/javascript" src="js/navigate.forms.js"></script>
<script type="text/javascript" src="js/jquery.fancyform.js"></script>
<script type="text/javascript" src="js/commonScript.js"></script> 
<script type="text/javascript" src="js/knockoff.js"></script> 
<script type="text/javascript" src="js/classification.js"></script> 
<script type="text/javascript">
$(document).ready(function () {
	clearfield();
});

</script>
<link href="css/fancyform.css" type="text/css" rel="stylesheet" />
</head>


<body onload="clearfield();">

 <form:form name="form" action="saveClassification.do" method="POST" commandName="ConfigBean" onreset="clearfield();">  
	<div id="id1" class="tabcontent" style="display: block;" >
	
	
	
	<div class="jtable-main-container">
				<div class="jtable-title">
					<div class="jtable-title-text1">CLASSIFICATION CONFIGURATION</div>
				</div>
	</div>
	 <table align="center" cellpadding="2" cellspacing="0" border="0" id="datatbl" class="table" width="100%">
	
	<tr class="oddRow">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Category</th>
			<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th>
			<td align="left" colspan="11">
				<%-- <form:input path="stCategory" maxlength="6" cssClass="file_name" /> --%>
				<select name="Category" id="Category" onchange="setvalue(this)">
							<option value="">--Select--</option>
							<option value="ONUS">ONUS</option>
							<option value="RUPAY">RUPAY</option>
							<option value="AMEX">AMEX</option>
							<option value="VISA">VISA</option>
							<option value="MASTERCARD">MASTERCARD</option>
							<option value="CARDTOCARD">CARD TO CARD</option>
							<option value="POS">ONUS POS</option>
					</select> <form:hidden path="stCategory" id="stCategory" value=""/>
			</td>
	
	</tr>
	<tr class="oddRow">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Sub Category</th>
			<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th>
			<td align="left" colspan="11">
				<%-- <form:input path="stCategory" maxlength="6" cssClass="file_name" /> --%>
				<select name="SubCat" id="SubCat" onchange = "setvalue(this)">
				<option value="-">--Select --</option>
				<option value="DOMESTIC">DOMESTIC</option>
				<option value="INTERNATIONAL">INTERNATIONAL</option>
				<option value="SURCHARGE">SURCHARGE</option>
				<option value="ISSUER">ISSUER</option>
				<option value="ACQUIRER">ACQUIRER</option>
				<option value="ONUS">ONUS</option>
				</select>
			 <form:hidden path="stSubCategory" id="stSubCategory" value=""/> 
			</td>
	
	</tr>
	
	<tr class="oddRow" id="trbtn">
		<td>&nbsp;&nbsp;&nbsp;&nbsp;<input class ="form-button"  type="button" value="Get Files" onclick="getFiles()">
		</td>
		<th align="right">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
		<th align="right">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
			
	</tr>
	
	<tr class="evenRow" id="trfilelist" style="display: none">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;File Name</th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
			<td align="left" colspan="11">
					<form:select path="inFileId"  onchange="addcolumn()" id="fileList">
					 <%-- <form:option value="0" >---Select---</form:option>
						<c:forEach var="configfilelist" items="${configBeanlist}">
							<form:option id="${configfilelist.inFileId}" value="${configfilelist.inFileId}" >${configfilelist.stFileName}</form:option>
							</c:forEach>  --%>
						</form:select>
					<%-- <form:hidden path="inFileId" id="inFileId"/> --%>
						<input type="hidden" id="headerlist" value="">
						<form:hidden path="stFileName" id="stFileName"/>
			</td>
	
	</tr>
	
	
	
	<tr>
	  <!-- <td><input type="button" value="Previous" id="prev3"/></td> -->
	  <!-- <td><input type="submit" value="Submit" id="submit"/></td> -->
	  
	</tr>
	</table> 
	<div class="jtable-main-container">
				<div class="jtable-title">
					<div class="jtable-title-text1">COMPARE DETAILS</div>
				</div>
			</div>
		<table align="center" cellpadding="0" cellspacing="0" width="100%" class="table dtl_table" id="compareDetailstbl">
		<tr class="tableHeader">
				<th>Header</th>
				<th>Search Pattern</th>
				<th>Padding</th>
				<th>Condition</th>
				<th>Start Char Position</th>
				<th>End Char Position</th>
				
			</tr>
		
		<c:set var="rowClass" value="oddRow"/>
			<c:forEach var="cnv" items="${ConfigBean.clasify_dtl_list}" varStatus="loop">
			<tr class="${rowClass}">
			<%-- <td>
				<form:input path="comp_dtl_list[${loop.index}].stHeader"/>
			</td> --%>
			<%-- <form:hidden path="comp_dtl_list[${loop.index}].stHeader" id="comp_dtl_list[${loop.index}].stHeader"  cssClass="header"/>   --%>
				<td align="center" id="selectTd">
					 
					<%--   <form:input path="stHeader"/>   --%>
					 
				</td>
				<td align="center">
					 <form:input path="clasify_dtl_list[${loop.index}].stSearch_Pattern" id="clasify_dtl_list[${loop.index}].stSearch_Pattern" maxlength="50" cssClass="srch_pattern" onkeypress="return setValueType(this,'search')"/> 
					
				</td>
				<td align="center" class="lD">
					<%--  <form:input path="comp_dtl_list[${loop.index}].stPadding" maxlength="50" cssClass="place" /> --%> 
					<form:select path="clasify_dtl_list[${loop.index}].stPadding" id="clasify_dtl_list[${loop.index}].stPadding">
						<form:option value=""> --Select-- </form:option>
						<form:option value="Y">Yes</form:option>
						<form:option value="N">No</form:option>
					</form:select>
				</td>
				<td align="center" class="lD">
					 <form:select path="clasify_dtl_list[${loop.index}].condition" id="clasify_dtl_list[${loop.index}].condition" cssClass="char_pos">
					 	<form:option value="">--Select--</form:option>
					 	<form:option value="=">=</form:option>
					 	<form:option value="!=">!=</form:option>
					 	<form:option value="LIKE">LIKE</form:option>
					 </form:select> 
				</td> 
				<td align="center" class="lD">
					 <form:input path="clasify_dtl_list[${loop.index}].inStart_Char_Position" id="clasify_dtl_list[${loop.index}].inStart_Char_Position" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"/> 
				</td> 
				<td align="center" class="lD">
					 <form:input path="clasify_dtl_list[${loop.index}].inEnd_char_position" id="clasify_dtl_list[${loop.index}].inEnd_char_position" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"/> 
				</td>
	
			<%-- 	<td align="center" class="lD"><input type="button" id="del${loop.index}" name="del${loop.index}" class="delButton" value="Delete"></td> --%>
			</tr>
			<c:choose>
				<c:when test="${rowClass eq 'oddRow' }"><c:set var="rowClass" value="evenRow" /></c:when>
				<c:when test="${rowClass eq 'evenRow' }"><c:set var="rowClass" value="oddRow" /></c:when>
			</c:choose>
			
			</c:forEach>
		<form:hidden path="knock_offFlag" id="knock_offFlag" value="N"/>
		
		</table>
	<table align="center" cellpadding="0" cellspacing="0" width="100%" class="table">
			<tr class="row1">
				<th align="left" style="padding-left: 25px; background-color: #d7dbdd" ><input class="form-button" type="button" id="addRow" name="addRow" value="Add Row" /></th>
			</tr>
	</table>
	
	<div class="footerRow" align="center">
			<input type="checkbox"  id="chkproceed"  onchange="return knockoffproceed(this)"/> Knockoff
			<input  class="form-button" type="button" value="Proceed to Knockoff" id="btnknckoff" style="size:150px; display: none" onclick="moveKnockOff();"/>
			<input class="form-button" type="submit" id="submit" name="Submit" value="Submit" onclick="return validData();">
			<input type="hidden" id="count" name="count" value="${fn:length(ConfigBean.comp_dtl_list)}">
			<input class="form-button"type="button" id="reset" name="reset" value="Reset" onclick="clearfield();">
		</div>
	
	 
	
	
	 
	
	</div>

<!-- DIV 2 -->
<div id="id2" class="tabcontent">

	<!-- <div class="jtable-main-container">
				<div class="jtable-title">
					<div class="jtable-title-text1">KNOCKOFF CONFIGURATION</div>
				</div>
	</div> -->
	 <table align="center" cellpadding="2" cellspacing="0" border="0" id="datatbl" class="table" width="100%">
	<tr class="evenRow" style="display: none">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;File Name</th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
			<td align="left" colspan="11">
					<select disabled="disabled" readonly="true" id="fileList1">
					<option value="0" >---Select---</option>
						<c:forEach var="configfilelist" items="${configBeanlist}">
							<option value="${configfilelist.inFileId}" >${configfilelist.stFileName}</option>
							</c:forEach>
						</select>
						<input type="hidden" id="headerlist" value="">
						
			</td>
	
	</tr>
	
	
	<tr>
	  <!-- <td><input type="button" value="Previous" id="prev3"/></td> -->
	  <!-- <td><input type="submit" value="Submit" id="submit"/></td> -->
	  
	</tr>
	</table> 
	<div class="jtable-main-container">
				<div class="jtable-title">
					<div class="jtable-title-text1">KNOCKOFF COMPARE DETAILS</div>
				</div>
			</div>
		<table align="center" cellpadding="0" cellspacing="0" width="100%" class="table dtl_table1" id="knockoffcompareDetailstbl">
		<tr class="tableHeader">
				<th>Header</th>
				<th>Original value</th>
				<th>Comparision value</th>
				
				
			</tr>
		
		<c:set var="rowClass" value="oddRow"/>
			<c:forEach var="cnv" items="${ConfigBean.comp_dtl_list}" varStatus="loop">
			<tr class="odd">
			<%-- <td>
				<form:input path="comp_dtl_list[${loop.index}].stHeader"/>
			</td> --%>
				<td align="center" id="knockoffselectTd">
					  <form:hidden path="comp_dtl_list[${loop.index}].knockoff_col" id="comphdr1" cssClass="header"/>  
					<%--   <form:input path="stHeader"/>   --%>
					 
				</td>
				<td align="center">
					 <form:input path="comp_dtl_list[${loop.index}].knockoff_OrgVal" id="comp_dtl_list[${loop.index}].knockoff_OrgVal" maxlength="50" cssClass="srch_pattern" onkeypress="return setValueType(this,'search')"/> 
					
				</td>
				<td align="center" class="lD">
					<form:input path="comp_dtl_list[${loop.index}].knockoff_comprVal" id="comp_dtl_list[${loop.index}].knockoff_comprVal" maxlength="50" cssClass="srch_pattern" onkeypress="return setValueType(this,'search')"/> 
					
				</td>
				
	
			<%-- 	<td align="center" class="lD"><input type="button" id="del${loop.index}" name="del${loop.index}" class="delButton" value="Delete"></td> --%>
			</tr>
			<c:choose>
				<c:when test="${rowClass eq 'oddRow' }"><c:set var="rowClass" value="evenRow" /></c:when>
				<c:when test="${rowClass eq 'evenRow' }"><c:set var="rowClass" value="oddRow" /></c:when>
			</c:choose>
			
			</c:forEach>
		
		
		</table>
	<table align="center" cellpadding="0" cellspacing="0" width="100%" class="table">
			<tr class="row1">
				<th align="left" style="padding-left: 25px; background-color: #d7dbdd;"><input class="form-button" onclick="addknockoffRow();" type="button" id="addRow1" name="addRow1" value="Add Row" /></th>
			</tr>
	</table>
	
			<div class="jtable-main-container">
				<div class="jtable-title">
					<div class="jtable-title-text1">KNOCKOFF CRITERIA</div>
				</div>
			</div>
			<table align="center" cellpadding="0" cellspacing="0" width="100%" class="table dtl_table2" id="knockoffcompareDetailstbl1">
			<tr class="tableHeader">
				<th>Header</th>
				<th>Header Value</th>
				<th>Padding</th>
				<th>Condition</th>
				<th>Start Char Position</th>
				<th>End Char Position</th>
				
				
			</tr>
		
		<c:set var="rowClass" value="oddRow"/>
			<c:forEach var="cnv" items="${ConfigBean.comp_dtl_list}" varStatus="loop">
			<tr class="${rowClass}">
			<%-- <td>
				<form:input path="comp_dtl_list[${loop.index}].stHeader"/>
			</td> --%>
				<td align="center" id="knckoffcrtselectTd">
					<form:hidden path="comp_dtl_list[${loop.index}].knockoff_header" id="comphdr2" cssClass="header"/>
					<%--   <form:input path="stHeader"/>   --%>
					 
				</td>
				<td align="center">
					 <form:input path="comp_dtl_list[${loop.index}].knockoffSrch_Pattern" id="comp_dtl_list[${loop.index}].knockoffSrch_Pattern" maxlength="50" class="header" cssClass="srch_pattern" onkeypress="return setValueType(this,'search')"/> 
					
				</td>
				<td align="center" class="lD">
					<%--  <form:input path="comp_dtl_list[${loop.index}].stPadding" maxlength="50" cssClass="place" /> --%> 
					<form:select path="comp_dtl_list[${loop.index}].knockoff_stPadding" id="comp_dtl_list[${loop.index}].knockoff_stPadding">
						<form:option value=""> --Select-- </form:option>
						<form:option value="Y">Yes</form:option>
						<form:option value="N">No</form:option>
					</form:select>
				</td>
				<td align="center" class="lD">
					 <form:select path="comp_dtl_list[${loop.index}].knockoff_condition" id="comp_dtl_list[${loop.index}].knockoff_condition" cssClass="char_pos">
					 	<form:option value="">--Select--</form:option>
					 	<form:option value="=">=</form:option>
					 	<form:option value="!=">!=</form:option>
					 	<form:option value="like">LIKE</form:option>
					 </form:select> 
				</td> 
				<td align="center" class="lD">
					 <form:input path="comp_dtl_list[${loop.index}].knockoffStart_Char_Pos" id="comp_dtl_list[${loop.index}].knockoffStart_Char_Pos" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"/> 
				</td> 
				<td align="center" class="lD">
					 <form:input path="comp_dtl_list[${loop.index}].knockoffEnd_char_pos" id="comp_dtl_list[${loop.index}].knockoffEnd_char_pos" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"/> 
				</td>
	
			<%-- 	<td align="center" class="lD"><input type="button" id="del${loop.index}" name="del${loop.index}" class="delButton" value="Delete"></td> --%>
			</tr>
			<c:choose>
				<c:when test="${rowClass eq 'oddRow' }"><c:set var="rowClass" value="evenRow" /></c:when>
				<c:when test="${rowClass eq 'evenRow' }"><c:set var="rowClass" value="oddRow" /></c:when>
			</c:choose>
			
			</c:forEach>
		
		
		</table>
	<table align="center" cellpadding="0" cellspacing="0" width="100%" class="table">
			<tr class="row1">
				<th align="left" style="padding-left: 25px; background-color: #d7dbdd;"><input class="form-button" onclick="addknockoffcrtRow();" type="button" id="addRow2" name="addRow1" value="Add Row" /></th>
			</tr>
	</table>
			
	
	
	
	
	
	<div class="footerRow" align="center">
		
			<input class="form-button" type="submit" id="submit" name="Submit" value="Submit" onclick="return knockoffvalidData();">
			<input type="hidden" id="count1" name="count1" value="${fn:length(ConfigBean.comp_dtl_list)}">
			<input type="hidden" id="count2" name="count2" value="${fn:length(ConfigBean.comp_dtl_list)}">
			
			
		</div>
	
	
	<!-- <input type="button" class="form-button" value="Previous" id="prev2"/> -->
</div> 

<!-- DIV 3: Filteration Configuration  -->


<%-- <table align="center" cellpadding="2" cellspacing="0" border="0" class="table" width="100%">btnknckoff
<tr>
<td colspan="2">
tab3 <form:input path="stCheck"/>
</td>
</tr>
<tr>
  <td><input type="button" value="Previous" id="prev3"/></td>
  <td><input type="submit" value="Submit" id="submit"/></td>
  
</tr>
</table> --%>



 </form:form> 
 </body>
 </html>