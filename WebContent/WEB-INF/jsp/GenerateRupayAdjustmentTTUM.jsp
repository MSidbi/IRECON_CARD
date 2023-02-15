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
<script type="text/javascript" src="js/RupayAdjustmentTTUM.js"></script>
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
			Adjustment TTUM
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Adjustment TTUM</li>
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
<form id="reportform"  action="DownloadRupayAdjTTUM.do" method="POST" enctype="multipart/form-data" >



					<div class="box-body" id="subcat">
					
					  <div class="form-group">
					 		<label for="exampleInputEmail1" >Adjustment Type</label> 
							 <select class="form-control" name="adjType" id="adjType">
								<option value="-">--Select --</option>
									<!-- <option value="Chargeback Acceptance">Chargeback Acceptance</option>
									<option value="Credit Adjustment">Credit Adjustment</option>
									<option value="Pre-Arbitration Acceptance">Pre-Arbitration Acceptance</option>
									<option value="Goodfaith chargeback Acceptance">Goodfaith chargeback Acceptance</option>	
									<option value="Arbitration Acceptance">Arbitration Acceptance</option>
									<option value="Good faith chargeback Deemed Acceptance">Good faith chargeback Deemed Acceptance</option>
									<option value="Chargeback Deemed Acceptance">Chargeback Deemed Acceptance</option> -->
									<option value="ADJUSTMENT">Adjustment</option>
									<option value="FEE">Fees</option>
							</select> 
						</div> 
		
                     <div class="form-group" id="Date">
							<label for="exampleInputPassword1">Date</label> 
							<input class="form-control" name="fileDate" readonly="readonly"
								id="dailypicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
					</div>	
					 <!-- <div class="form-group">
							<label for="exampleInputPassword1">To Date</label> <input
								class="form-control" name="toDate" readonly="readonly"
								id="toDate" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
					</div> -->	 
						
						</div>	
						
						<!--  <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="file" id="dataFile1" title="Upload File" /></td>
                      <p class="help-block">Example block-level help text here.</p>
                    </div>
                     -->
						<!--  <div class="box-footer">
                  		  <button type="button" value="UPLOAD" id="upload" onclick="return processFileUpload();" class="btn btn-primary">Upload</button>
                 		 </div>

						</div> -->
					
					
					   
                    

							

					<div class="box-footer" style="text-align: center">
						<button type="button" class="btn btn-primary" onclick="processAdjTTUM();">Process</button>
						<button type="button" class="btn btn-info" onclick="DownloadAdjTTUM();">Dowload Report</button>
					<!-- 	<button type="button" id ="Skip" class="btn btn-danger" onclick="skipSettlement();">Skip Settlement</button> -->
					<!-- 	<a onclick="processSettlement();" class="btn btn-primary">Process</a>
						<a onclick="skipSettlement();" class="btn btn-primary">Skip Settlement</a> -->
						
					</div>
					<!-- <div id="processTbl"></div> -->
</form>
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