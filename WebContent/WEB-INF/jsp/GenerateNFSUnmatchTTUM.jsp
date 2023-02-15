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
<script type="text/javascript" src="js/GenerateNFSUnmatchedTTUM.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();
$("#localDate").datepicker({dateFormat:"yy/mm/dd", maxDate:0});



});
    

window.history.forward();
function noBack() { window.history.forward(); }


</script>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			NFS TTUM
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">NFS TTUM</li>
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
<form:form id="reportform"  action="DownloadNFSUnmatchedTTUM.do" method="POST"  commandName="unmatchedTTUMBean">



					<div class="box-body" id="subcat">
                    
                    <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1">Sub Category</label> 
							<select class="form-control"
								name="stSubCategory" id="stSubCategory" onchange="getFields(this)">
								<option value="-">--Select --</option>
								<c:forEach var="subcat" items="${subcategory}">
									<option value="${subcat}">${subcat}</option>
								</c:forEach>
							</select>

						</div>
						
                    <div class="form-group" id="issuerOpt" style="display:none">
							<label for="exampleInputEmail1" >TTUM Type</label> 
							<input type="text" name ="category" id="category" value="${category}" style="display: none"> 
							<form:select class="form-control" path="typeOfTTUM" id="typeOfTTUM">
								<option value="0">--Select --</option>
									<option value="LATEREV">Late Reversal</option>
									<option value="FAILED">Failed</option>
									 <option value="UNRECON">UnRecon</option> 
									  <option value="RUPAYONUS">Rupay-Onus</option>
							</form:select>
						</div>
						
					<div class="form-group" id="acquirerOpt" style="display:none">
							<label for="exampleInputEmail1" >TTUM Type</label> 
							<form:select class="form-control" path="acqtypeOfTTUM" id="acqtypeOfTTUM">
								<option value="0">--Select --</option>
									<option value="NIH">Not in Host</option>
									<option value="ACQFAILED">Failed</option>
							</form:select>
						</div>	
						
                     <div class="form-group" id="Date">
                     <input type="text" id="rectyp" value="${category}" style="display: none"> 
							<label for="exampleInputPassword1">Tran Date</label>
							 <input class="form-control" readonly="readonly"
								id="localDate" name = "localDate" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
					</div>	
					</div>

					<div class="box-footer" style="text-align: center">
						<!-- <input type="submit" class="btn btn-primary" name="Process" id="Process" value="Process" onclick="return ValidateData();"> -->
						
						<a onclick="Process();" class="btn btn-primary">Process</a> 
						<a onclick="Download();" class="btn btn-info">Download</a>
						<a onclick="RollbackTTUM();" class="btn btn-info">Rollback</a>
						
					</div>
					
</form:form>
				</div>



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