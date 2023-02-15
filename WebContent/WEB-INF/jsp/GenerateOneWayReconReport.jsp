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
<script type="text/javascript" src="js/OneWayReconReport.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();
/* $("#datepicker").datepicker({dateFormat:"dd-M-yy", maxDate:0}); */


	$("#datepicker").datepicker({dateFormat:"yy/mm/dd", maxDate:0});


});
    

window.history.forward();
function noBack() { window.history.forward(); }


</script>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			One Way Recon Report
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">One Way Recon Report</li>
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
<%-- <form:form id="reportform"  action="DownloadSettlementreport.do" method="POST"  commandName="nfsSettlementBean" enctype="multipart/form-data" > --%>
<form:form id="reportform"  action="DownloadOneWayReconReport.do" method="POST"  commandName="nfsSettlementBean" enctype="multipart/form-data" >


					 <div class="box-body" id="subcat">
					
					<%-- <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >FileName</label> 
							<select class="form-control" name="fileName" id="fileName">
								<option value="0">--Select --</option>
									<option value="REPORT">Report</option>
									<option value="VOUCHER">Voucher</option>
							</select>
						</div> --%>
                    
                     <div class="form-group" id="Date">
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="datepicker" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
					</div>	
					
					<div class="box-footer" style="text-align: center">
						<!-- <button type="button" class="btn btn-primary" onclick="processEOD();">Process</button> -->
						<button type="button" class="btn btn-primary" onclick="downloadOneWay();">Download</button>
						
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