<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<link href="css/fancyform.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/manualFile.js"></script>

<link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" />
<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" />
<script type="text/javascript">
$(document).ready(function() {
	
	//alert("click");
  
    $("#datepicker").datepicker({dateFormat:"dd/mm/yy", maxDate:0});
    });
    
    
</script>


<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text">Manual File Processing</div>
			</div>
		</div>
 <%-- <form:form name="form" action="ManualFileProcess.do" method="POST" commandName="manualFileBean"> --%> 
 
 
 <table align="center" cellpadding="2" cellspacing="0" border="0" class="table" width="100%">
 
 
	 <tr class="evenRow">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Category</th>
			<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th>
			<%-- <c:forEach var="file" items="${filterBean.file_list}" varStatus="loop"> --%>
			
				<td align="left" colspan="11">
	
					<select name="Category" id="Category" onchange="chngsubcat(this)">
						<option value="-">- S E L E C T</option>
						<option value="ONUS">ONUS</option>
						<option value="RUPAY">RUPAY</option>
						<option value="VISA">VISA</option>
						<option value="AMEX">AMEX</option>
						
					</select>
					<%-- <form:hidden path="stCategory" id="stCategory"/> --%>
					
			</td> 
			
	</tr>
	 
	 
	  <tr class="evenRow" id="trsubcat">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;SubCategory</th>
			<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th>			
				<td align="left" colspan="11">
	
			
					 <select name="SubCat" id="SubCat" onchange="getFiles();"> 
						<option value="-">- SELECT-</option>
						<option value="DOMESTIC">DOMESTIC</option>
						<option value="INTERNATIONAL">INTERNATIONAL</option>
						<option value="SURCHARGE">SURCHARGE</option>
						<option value="ISSUER">ISSUER</option>
						<option value="ACQUIRER">ACQUIRER</option>
					</select>
					
					<%-- <form:hidden path="stSubCategory" id="stSubCategory" /> --%>
			</td> 
			
	</tr> 
	
	
  <tr class="evenRow" id="trfile" style="display: none;">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;File</th>
			<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th>
				
				<td align="left" colspan="11">
	
					<select id="stFileSelected" onchange="setFilename(this);">
						
					</select>
					<%-- <form:hidden path="stFileName" id="stFileName" value="" /> --%>
					
			</td> 
			
	</tr>
	
	<tr class="evenRow" id="trmanfile" style="display: none;">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Manual File</th>
			<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th>
			
			
				<td align="left" colspan="11">
	
					<select  id="manFile" onchange="setmanFilename(this);">
						
					</select>
					<%-- <form:hidden path="stManualFile" id="stManualFile" value="" /> --%>
			</td> 
			
	</tr> 
	
	 
	<%-- <tr class="evenRow">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;File</th>
			<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th>
			<c:forEach var="file" items="${filterBean.file_list}" varStatus="loop">
			
				<td align="left" colspan="11">
	
					<form:select path="stTable1">
						<form:option value="">- S E L E C T</form:option>
						<form:option value="ONUS_SWITCH">ONUS_MANUAL_CBS</form:option>
					</form:select>
					<form:select path="stTable2">
						<form:option value="">- S E L E C T</form:option>
						<form:option value="ONUS_CBS">ONUS_SWITCH</form:option>
					</form:select>
					<form:select path="stFileSelected">
						<form:option value="">- S E L E C T -</form:option>
						<c:forEach var="cnv" items="${files}" varStatus="loop">
							<form:option value="${cnv.stFilesAvail}">${cnv.stFilesAvail}</form:option>
						</c:forEach>
					</form:select> 
			</td> 
			
	</tr> --%>
	
	<tr>
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;File Date</th>
			<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th>
			<%-- <c:forEach var="file" items="${filterBean.file_list}" varStatus="loop"> --%>
				<td align="left">
					<input id="datepicker" cssClass="date-short" maxlength="10" title="dd/mm/yyyy" />
				
			</td> 
	</tr>
	
	<tr class="oddRow">
			<td align="center" class="footerBtns" colspan="8">
						<!--  <input type="submit" name="Process" id="Process" value="Process" > --> 
						<input type="button" value="Process" onclick="Process();" >
						 <input type="button" name="cancel" id="cancel" value="Cancel">
			</td>
	  
	  
	</tr>

</table>
<div align="center" id="Loader"
		style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

		<img style="margin-left: 20px; margin-top: 200px;" src="images/unnamed.gif" alt="loader">

	</div>

 <%-- </form:form>  --%>