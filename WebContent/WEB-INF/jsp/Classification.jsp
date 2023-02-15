<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<link href="css/fancyform.css" type="text/css" rel="stylesheet" />


<%-- <form:form name="form" action="ABCD.do" method="POST" commandName="ConfigBean">  --%>


<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text">Classification CONFIGURATION</div>
			</div>
</div>
 <table align="center" cellpadding="2" cellspacing="0" border="0" id="datatbl" class="table" width="100%">
<tr class="evenRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;File Name</th>
		 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
		<td align="left" colspan="11">
				<form:select path="inFileId"  onchange="addcolumn()" id="fileList">
				<form:option value="0" >---Select---</form:option>
					<c:forEach var="configfilelist" items="${configBeanlist}">
						<form:option id="${configfilelist.inFileId}" value="${configfilelist.inFileId}" >${configfilelist.stFileName}</form:option>
						</c:forEach>
					</form:select>
					<input type="hidden" id="headerlist" value="">
					
					<form:hidden path="stFileName" id="stFileName"/>
		</td>

</tr>
<tr class="oddRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Category</th>
		<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th>
		<td align="left" colspan="11">
			<%-- <form:input path="stCategory" maxlength="6" cssClass="file_name" /> --%>
			<form:select path="stCategory" id="stCategory">
			<form:option value="">--Select -</form:option>
			<form:option value="ONUS">ONUS</form:option>
			<form:option value="ACQUIRER">ACQUIRER</form:option>
			<form:option value="ISSUER">ISSUER</form:option>
			</form:select>
		</td>

</tr>

<tr>
  <!-- <td><input type="button" value="Previous" id="prev3"/></td> -->
  <!-- <td><input type="submit" value="Submit" id="submit"/></td> -->
  
</tr>
</table> 
<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text">COMPARE DETAILS</div>
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
		<c:forEach var="cnv" items="${ConfigBean.comp_dtl_list}" varStatus="loop">
		<tr class="${rowClass}">
		<%-- <td>
			<form:input path="comp_dtl_list[${loop.index}].stHeader"/>
		</td> --%>
			<td align="center" id="selectTd">
				 <form:hidden path="comp_dtl_list[${loop.index}].stHeader" id="comphdr" cssClass="header"/>  
				<%--   <form:input path="stHeader"/>   --%>
				 
			</td>
			<td align="center">
				 <form:input path="comp_dtl_list[${loop.index}].stSearch_Pattern" maxlength="50" cssClass="srch_pattern" onkeypress="return setValueType(this,'search')"/> 
				
			</td>
			<td align="center" class="lD">
				<%--  <form:input path="comp_dtl_list[${loop.index}].stPadding" maxlength="50" cssClass="place" /> --%> 
				<form:select path="comp_dtl_list[${loop.index}].stPadding">
					<form:option value=""> --Select-- </form:option>
					<form:option value="Y">Yes</form:option>
					<form:option value="N">No</form:option>
				</form:select>
			</td>
			<td align="center" class="lD">
				 <form:select path="comp_dtl_list[${loop.index}].condition" cssClass="char_pos">
				 	<form:option value="">--Select--</form:option>
				 	<form:option value="=">=</form:option>
				 	<form:option value="!=">!=</form:option>
				 	<form:option value="LIKE">LIKE</form:option>
				 </form:select> 
			</td> 
			<td align="center" class="lD">
				 <form:input path="comp_dtl_list[${loop.index}].inStart_Char_Position" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"/> 
			</td> 
			<td align="center" class="lD">
				 <form:input path="comp_dtl_list[${loop.index}].inEnd_char_position" maxlength="6" cssClass="char_pos" onkeypress="return setValueType(this,'numeric')"/> 
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
		<input class="form-button"type="button" id="reset" name="reset" value="Reset">
	</div>
<%-- </form:form> --%>