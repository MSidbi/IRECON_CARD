<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<link href="css/fancyform.css" type="text/css" rel="stylesheet" />


<%-- <form:form name="form" action="ABCD.do" method="POST" commandName="ConfigBean">  --%>


<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text">KNOCKOFF CONFIGURATION</div>
			</div>
</div>
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
				<div class="jtable-title-text">KNOCKOFF COMPARE DETAILS</div>
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
				 <form:input path="comp_dtl_list[${loop.index}].knockoff_OrgVal" maxlength="50" cssClass="srch_pattern" onkeypress="return setValueType(this,'search')"/> 
				
			</td>
			<td align="center" class="lD">
				<form:input path="comp_dtl_list[${loop.index}].knockoff_comprVal" maxlength="50" cssClass="srch_pattern" onkeypress="return setValueType(this,'search')"/> 
				
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
				<div class="jtable-title-text">KNOCKOFF CRITERIA</div>
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
				 <form:input path="comp_dtl_list[${loop.index}].knockoffSrch_Pattern" maxlength="50" cssClass="srch_pattern" onkeypress="return setValueType(this,'search')"/> 
				
			</td>
			<td align="center" class="lD">
				<%--  <form:input path="comp_dtl_list[${loop.index}].stPadding" maxlength="50" cssClass="place" /> --%> 
				<form:select path="comp_dtl_list[${loop.index}].knockoff_stPadding">
					<form:option value=""> --Select-- </form:option>
					<form:option value="Y">Yes</form:option>
					<form:option value="N">No</form:option>
				</form:select>
			</td>
			<td align="center" class="lD">
				 <form:select path="comp_dtl_list[${loop.index}].knockoff_condition" cssClass="char_pos">
				 	<form:option value="">--Select--</form:option>
				 	<form:option value="=">=</form:option>
				 	<form:option value="!=">!=</form:option>
				 	<form:option value="like">LIKE</form:option>
				 </form:select> 
			</td> 
			<td align="center" class="lD">
				 <form:input path="comp_dtl_list[${loop.index}].knockoffStart_Char_Pos" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"/> 
			</td> 
			<td align="center" class="lD">
				 <form:input path="comp_dtl_list[${loop.index}].knockoffEnd_char_pos" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"/> 
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
		
		<input class="form-button"type="button" id="reset" name="reset" value="Reset">
	</div>
<%-- </form:form> --%>