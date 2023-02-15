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

<script type="text/javascript" src="js/UploadGlStatement.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();
  
    $("#datepicker").datepicker({dateFormat:"dd-M-yy", maxDate:0});
    });
    

window.history.forward();
function noBack() { window.history.forward(); }


</script>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			 GL Statement Upload
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">GL Statement Upload</li>
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
<form:form name="reportform" id="reportform" action="runAct4Report.do" method="POST" commandName="actBean" >
					<div class="box-body" id="subcat">

					  <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Network :</label> 
							<form:select class="form-control" path="category" id="category" onChange="getSubCategory(this)">
								<option value="0">--Select --</option>
									<option value="RUPAY">Rupay</option>
									<!-- <option value="FISDOM">Fisdom</option> --> 
									<option value="VISA">Visa</option>
									<option value="NFS">Nfs</option>
							</form:select>
						</div>

						
						<div id="trsubcat" class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" onchange="CallDollar()">Sub Category</label> 
								<select class="form-control" name="stSubCategory" id="stSubCategory" onChange="getGlAccounts(this)">
								 <option value="-">--Select --</option>
								<%--<c:forEach var="subcat" items="${subcategory}">
									<option value="${subcat}">${subcat}</option>
								</c:forEach> --%>
							</select>

						</div>
						
						<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" onchange="CallDollar()">GL Accounts</label>
							 <select class="form-control" name="glAccount" id="glAccount">
								 <option value="-">--Select --</option>
								<%--<c:forEach var="glAccounts" items="${glAccount}">
									<option value="${glAccounts}">${glAccounts}</option>
								</c:forEach> --%>
							</select>

						<div class="form-group">
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="datepicker" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
						</div>
						
						 <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="file" id="dataFile1" title="Upload File" /></td>
                   		 </div>
						
						</div>

					
					<!-- /.box-body -->

					<div class="box-footer" style="text-align: center">
						<a onclick="Process();" class="btn btn-primary">Upload</a>
					</div>
					</div>
					<div id="processTbl"></div>
</form:form>
					<%-- </form> --%>
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->

		</div>
		<!-- /.row -->
	</section>
</div>
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