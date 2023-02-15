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

<script type="text/javascript" src="js/ReconStatus.js"></script>
	<!-- <script src="js/jquery.jtable.js" type="text/javascript"></script> -->
<script type="text/javascript" src="js/jquery.fancyform.js"></script>

<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/commonScript.js"></script> 
  
<script type="text/javascript">
$(document).ready(function() {
	
	//alert("click");
  
    $("#datepicker").datepicker({dateFormat:"dd/mm/yy", maxDate:0});
    });



</script>


<title>Recon Status</title>
</head>
<body>

<div class="jtable-main-container">
			<div class="jtable-title">
				<div class="jtable-title-text">Recon Status</div>
			</div>
</div>
<div>

	<table align="center" class="table">
		<form:form name="form" action="DownloadReconStatus.do" method="POST" commandName="ReconStatusBean" >
		
		
		<tr>
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Category </th>
			 	<th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
					<td align="left">
						<form:select path="stCategory" id="category" name="category" onchange="getSubCategory(this)">
						<form:option value="">--Select--</form:option>
						 <c:forEach var="cate" items = "${cate_list}">
						 <form:option value="${cate}">${cate}</form:option>
							<%-- <form:option value="${cate_list.stCategory}"></form:option> --%>
						</c:forEach>
					<%-- 	<%-- <form:option value="ONUS">ONUS</form:option>
						<form:option value="AMEX">AMEX</form:option>  --%>
						<%-- <form:option value="RUPAY">RUPAY</form:option>
						<form:option value="VISA">VISA</form:option>
						<form:option value="NFS">NFS</form:option>
						<form:option value="CASHNET">CASHNET</form:option> --%>
					</form:select>
						
				</td>
			</tr>
		
				
			<tr id="trsubcat">
				<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Sub Category </th>
				<th align="left">:&nbsp;&nbsp;&nbsp;&nbsp;</th>
				<td align = "left">
				
				<form:select path="stSubCategory" name="SubCat" id="SubCat">
				<option value="-">--Select --</option>
				</form:select></td>
		</tr>
		
	<%-- 
		 <tr class="oddRow">
		<th align="left">&nbsp;&nbsp;&nbsp;&nbsp;Date</th>
			 <th align="right">&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;</th> 
		<td><form:input path="stDatepicker" readonly="readonly" id="datepicker"  placeholder="dd/mm/yyyy"/></td>
		</tr> --%>
		<tr><td><center> 
		<!-- <input type="button" value="Process" onclick="Process();"> -->
		<input type="button" value="ViewData" id = "process" onclick="ViewData();">
		<input type="button" value="DownloadData" id = "delete" onclick="DownloadData();">
		
		</center> </td></tr>
				 
		
		</form:form>
	</table>
	
	
	 <br /><br />

   
    <br /><br /> 
	
	</div>
	
	<!-- <div align="center" id="Loader"
		style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

		<img style="margin-left: 20px; margin-top: 200px;" src="images/unnamed.gif" alt="loader">

	</div> -->
	
	
</body>
</html>