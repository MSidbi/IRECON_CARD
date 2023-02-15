<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	
	<%
response.setHeader("Cache-Control","no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
response.setHeader("Pragma","no-cache");
response.setHeader("X-Frame-Options","deny");
%>

<link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/SettlementVoucher.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();
	
/* $("#dailypicker").datepicker({dateFormat:"dd-M-yy", maxDate:0}); */

	$("#dailypicker").datepicker({dateFormat:"yy/mm/dd", maxDate:0});

});
    

window.history.forward();
function noBack() { window.history.forward(); }


</script>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			Settlement Rectification
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Settlement Rectification</li>
		</ol>
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
		<div class="col-md-4"></div>
			<!-- left column -->
			<div class="col-md-4">
				<!-- general form elements -->
				<div class="box box-primary">
					<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>
<form:form id="reportform"  action="DownloadSettVoucher.do" method="POST"  commandName="nfsSettlementBean" enctype="multipart/form-data" >



					<div class="box-body" id="subcat">
					
					<%-- <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Interchange Calculation of: </label> 
							 <input type="text" id="rectyp" value="${category}" style="display: none"> 
							<select class="form-control" name="fileName" id="fileName" onchange="FileNameChange(this)">
								<option value="0">--Select --</option>
									<option value="NTSL-DFS">DFS</option>
									<option value="NTSL-JCB-UPI">JCB-UPI</option>
									<option value="NTSL-NFS">NFS</option>
								    <option value="PBGB">PBGB</option>
							</select>
						</div> --%>
						
						
					<%-- <div class="form-group" style="display:${display}">
							 
							<input type="text" id="timePeriod" value="Daily" style="display: none"> 
							<form:select class="form-control" path="timePeriod" id="timePeriod" >
								<option value="0">--Select --</option>
									<option value="Daily">Daily</option>
									<option value="Monthly">Monthly</option>	
							</form:select>
						</div> --%>
						<input type="text" id="rectyp" value="${category}" style="display: none"> 
					 <div class="form-group" style="display:none">
							<input type="text" id="stSubCategory" value="-" style="display: none"> 
							<%-- <select class="form-control" name="stSubCategory" id="stSubCategory">
								<option value="-">--Select --</option>
								<c:forEach var="subcat" items="${subcategory}">
									<option value="${subcat}">${subcat}</option>
								</c:forEach>
							</select> --%>
						</div> 
		
                    	
					  <div class="form-group" id ="cycles" style="display:${display}">
							<label for="exampleInputEmail1" >Cycle</label> 
							<select class="form-control" name="cycle" id="cycle">
								<option value="0">--Select --</option>
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>	
							</select>
						</div>
                    
                     <div class="form-group" id="Date">
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="datepicker" readonly="readonly"
								id="dailypicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
					</div>	
					
					 <div class="form-group" id="Date">
							<label for="exampleInputPassword1">Rectification Details</label> 
							<div>
							<label for="exampleInputPassword1">Add / Subtract</label> 
							<select class="form-control" name="sign" id="sign">
								<option value="0">--Select --</option>
									<option value="-">Subtract (-)</option>
									<option value="+">Add (+)</option>	
							</select>
							</div>
							<div>
							<label for="exampleInputPassword1">Amount</label> 
							<input class="form-control" type = "text" name="rectAmt" id="rectAmt" />
							</div>
					</div>

					<div class="box-footer" style="text-align: center">
						<!-- <button type="button" class="btn btn-primary" onclick="processSettVoucher();">Process</button> -->
					 	<a class="btn btn-danger" onclick="Rectify();">Rectify Settlement </a>
					<!-- 	<a onclick="processSettlement();" class="btn btn-primary">Process</a>
						<a onclick="skipSettlement();" class="btn btn-primary">Skip Settlement</a> -->
						
					</div>
					<!-- <div id="processTbl"></div> -->
</form:form>
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->

		</div>
		<!-- /.row -->
<!-- /.content-wrapper -->
</section>
</div>
<div align="center" id="Loader"
	style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

	<img style="margin-left: 20px; margin-top: 200px;"
		src="images/unnamed.gif" alt="loader">

</div>
<script>

</script>