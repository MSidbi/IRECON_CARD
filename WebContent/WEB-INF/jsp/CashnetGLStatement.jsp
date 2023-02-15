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

<link href="css/jquery-ui.min.css" media="all" rel="stylesheet"
	type="text/css" />

<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<!--  <script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" />
<link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" />   -->

<script type="text/javascript" src="js/CashnetGLStatement.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	debugger;
    
    $("#fileDate").datepicker({dateFormat:"yy/mm/dd", maxDate:0});
    
    });
    


    

window.history.forward();
function noBack() { window.history.forward(); }


</script>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			${category} GL Statement
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">GL Statement</li>
		</ol>
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
			<!-- left column -->
			<div class="col-md-4"></div>
			<div class="col-md-4">
				<!-- general form elements -->
				<div class="box box-primary">
					<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>

<form:form id="processform"  action="DownloadCashnetGLStatement.do" method="POST"  commandName="unmatchedTTUMBean" >

					<div class="box-body" id="subcat">
						
						
						<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" onchange="CallDollar()">Sub Category</label> 
							<input type="text" id="category" name="category"
							 value="${category}" style="display: none"> 
							<select class="form-control"
								name="stSubCategory" id="stSubCategory" onchange="getFields(this)">
								<option value="-">--Select --</option>
								<c:forEach var="subcat" items="${subcategory}">
									<option value="${subcat}">${subcat}</option>
								</c:forEach>
							</select>

						</div>

							<div class="form-group" id="visaType" style="display: none">
								<c:if test="${category == 'VISA'}">
									<label for="exampleInputEmail1">VISA Type</label>
									<select class="form-control" name="atmPos" id="atmPos">
										<option value="-">--Select --</option>
										<option value="pos">POS</option>
										<option value="atm">ATM</option>
									</select>
								</c:if>
							</div>

							<div class="form-group" id = "Closing" style="display:none">
								<label for="exampleInputEmail1" onchange="CallDollar()">Closing Balance</label> 
						<br>
							<input class="form-control" type="text" id="closingBal" name = "closingBal" /> 
						</div>

						<div class="form-group" style="display:${display}">
							<label for="exampleInputPassword1">Date</label> 
							<input class="form-control" name="fileDate" readonly="readonly" id="fileDate" 
							placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
						</div>
						<%-- <input type="hidden" name="category" value="${category }" > --%>
						</div>
						
					<!-- </div> -->
					<!-- /.box-body -->

					<div class="box-footer" style="text-align: center">
						<a onclick="Process();" class="btn btn-primary">Process</a>
						<a onclick="Download();" class="btn btn-info">Download</a>
						<a onclick="TTUMrollback();" class="btn btn-info">Rollback</a>
					</div>
					<div id="processTbl"></div>
</form:form>
</div>
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->
</section>
		</div>
		<!-- /.row -->
	
<!-- </div> -->
<!-- /.content-wrapper -->

<div align="center" id="Loader"
	style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

	<img style="margin-left: 20px; margin-top: 200px;"
		src="images/unnamed.gif" alt="loader">

</div>
<script>
function CallDollar()
{
	debugger;
	alert("sas");
	}
</script>