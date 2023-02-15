<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" />
<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="js/processedData.js"></script>
	<!-- <script src="js/jquery.jtable.js" type="text/javascript"></script> -->
<script type="text/javascript" src="js/jquery.fancyform.js"></script>

<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/commonScript.js"></script> 
  
<script type="text/javascript">
$(document).ready(function() {
	
	//alert("click");
  
    $("#datepicker").datepicker({dateFormat:"dd-mm-yy", maxDate:0});
    });



</script>
<!-- 
<script type="text/ecmascript" src="js/jquery-1.9.1.js"></script> -->

    <!-- This is the localization file of the grid controlling messages, labels, etc.
    <!-- We support more than 40 localizations -->

        
        
       
       
	
 

<!-- <script type="text/javascript" src="js/jquery-1.12.4.js"></script> -->

<title>Processed Data Reports</title>
</head>
<body>

<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text">Processed Data Reports</div>
			</div>
</div>
<div>

	<table align="center" class="table">
		<form:form name="form" action="GenerateReport.do" method="POST" commandName="SettlementBean" >
		
		
		<tr>
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Category </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="category" id="category" name="category" onchange="chngsubcat(this)">
						<form:option value="">--Select--</form:option>
						<form:option value="ONUS">ONUS</form:option>
						<form:option value="AMEX">AMEX</form:option> 
						<form:option value="RUPAY">RUPAY</form:option>
						<form:option value="VISA">VISA</form:option>
						<form:option value="NFS">NFS</form:option>
            <form:option value="CASHNET">CASHNET</form:option>
						<form:option value="MASTERCARD">MASTERCARD</form:option>
						<form:option value="CARDTOCARD">CARDTOCARD</form:option>
					</form:select>
						
				</td>
			</tr>
		
		 <%--  <tr class="evenRow" id="stsubCategory" style="display: none">
			<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;SubCategory</th>
			<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th>			
				<td align="left" colspan="11">
	
			
					  <select name="stsubCategory" id="stsubCategory" onchange="getFiles();"> 
						<option value="-">- SELECT-</option>
						<option value="DOMESTIC">DOMESTIC</option>
						<option value="INTERNATIONAL">INTERNATIONAL</option>
						<option value="SURCHARGE">SURCHARGE</option>
						<option value="ISSUER">ISSUER</option>
						<option value="ACQUIRER">ACQUIRER</option>
					</select> 
					
					<form:hidden path="stsubCategory" value=""/>  
			</td> 
			
	</tr>  --%> 
		<tr id="trsubcategory" style="display: none">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Sub Category </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<select  id="SubCat" name="SubCat" onchange="getFiles();">
							<option value="-">--Select--</option>
							<option value="DOMESTIC">DOMESTIC</option>
							<option value="INTERNATIONAL">INTERNATIONAL</option>
							<option value="SURCHARGE">SURCHARGE</option>
							<option value="ISSUER">ISSUER</option>
							<option value="ACQUIRER">ACQUIRER</option>
						</select>
						 <form:hidden path="stSubCategory" value=""/> 
				</td>
			</tr>
		<tr> <th align="left">&nbsp;&nbsp;&nbsp;&nbsp;File Name</th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
			<td>
				<form:select path="setltbl" id="setltbl" onchange="getFilestype()">
					<form:option value="">--SELECT--</form:option>
					<%-- <form:option value="settlement_switch"> Switch </form:option>
					<form:option value="settlement_cbs"> CBS </form:option> --%>
				</form:select>
				
			</td>
		</tr>
		
		<tr> <th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Type Of Data </th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
			<td>
				<form:select path="dataType" id="dataType">
				<form:option value="">--SELECT--</form:option>
				<%-- <form:option value="ONUS-RECON">ONUS-RECON</form:option>
				<form:option value="ONUS">ONUS</form:option>
				<form:option value="ONUS-MATCHED"> ONUS-MATCHED </form:option>
				<form:option value="ONUS-KNOCKOFF"> ONUS-KNOCKOFF </form:option> --%>
				</form:select>
				
			</td>
		</tr>
		
		
		<tr class="oddRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Date</th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
		<td><form:input path="datepicker" readonly="readonly" id="datepicker"  placeholder="dd-mm-yyyy"/></td>
	
		</tr>
		<tr class="evenRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Search Value</th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
		<td><form:input path="searchValue" id="searchValue" onkeypress="return setValueType(this,'search')" /> </td>
		
		
		</tr>
		  <tr>
		  <td><input type="button" class="form-button" value="Get Data" onclick="getjtbleReconData();" />  </td>
		   <!-- <td><input type="button" class="form-button" value="Get Data" onclick="getReconData();" />  </td> -->
   				<td> <input type="submit" class="form-button" value="Generate Report" onclick="generateReport();" /></td>  
   				
   		
   		</tr>
		</form:form>
	</table>
	
	
	
<!-- 	<table id="TypeDetailTableContainer"></table> -->
	 
    
	 <br /><br />

   
    <br /><br /> 
	
	</div>
	
	
</body>
</html>