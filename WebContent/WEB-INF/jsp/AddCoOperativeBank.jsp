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
<script type="text/javascript" src="js/AddCoOperativeBank.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();



});
    

window.history.forward();
function noBack() { window.history.forward(); }


</script>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			Add Co-Operative Bank
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Add Co-Operative Bank</li>
		</ol>
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
			<!-- left column -->
			<div class="col-md-6">
				<!-- general form elements -->
				<div class="box box-primary">
					<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>
<form:form id="reportform">



					<div class="box-body" id="subcat">
					
					<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Enter Bank Name</label> 
							<%-- <input type="text" id="rectyp" value="${category}" style="display: none">  --%>
							<!-- <input type = "text" value = "Daily Settlement Statement for" readOnly/> -->
							<input type="text" class="form-control" id="BankName" placeholder="" /> 
						<!-- 	<input type = "text" value = " as on 24/02/2021" readOnly/> -->
						</div>
						
					<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Enter Account Number</label> 
							<%-- <input type="text" id="rectyp" value="${category}" style="display: none">  --%>
							<input type="text" class="form-control" id="AccNo" placeholder="" /> 
						</div>	
					

							

					<div class="box-footer">
						<button type="button" class="btn btn-primary" onclick="Process();">Process</button>
					<!-- 	<input type="button" style="display:none" id ="Skip" class="btn btn-danger" onclick="skipSettlement();" value="Skip Settlement"/> -->
					<!-- 	<button type="button" id ="Skip" class="btn btn-danger" onclick="skipSettlement();">Skip Settlement</button> -->
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