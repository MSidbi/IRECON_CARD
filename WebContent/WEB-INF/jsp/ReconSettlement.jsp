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

<script type="text/javascript" src="js/settlement.js"></script>
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

<title>Recon Settlement</title>
</head>
<body>

<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text">Recon Settlement</div>
			</div>
</div>
<div>
<form:form name="form" action="addConfiguration.do" method="POST" commandName="SettlementBean">
	<table align="center">
		
		<tr> <th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Recon File</th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
			<td>
				<select id="setltbl">
					<option value="">--SELECT--</option>
					<option value="settlement_switch"> Switch </option>
					<option value="settlement_cbs"> CBS </option>
				</select>
				
			</td>
		</tr>
		
		<tr class="oddRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Date</th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
		<td><input  readonly="readonly" id="datepicker"  placeholder="dd-mm-yyyy"/></td>
	
		</tr>
	</table>
	</form:form>
	
	
<!-- 	<table id="TypeDetailTableContainer"></table> -->
	 
    <div id="jqGridPager"></div>
	 <br /><br />

    <input type="button" class="form-button" value="Get Recon Data" onclick="getReconData();" />   
    <br /><br /> 
	
	</div>
	
	
</body>
</html>