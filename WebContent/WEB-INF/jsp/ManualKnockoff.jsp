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

<script type="text/javascript" src="js/ManualKnockoff.js"></script>
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
			${category} Manual Knockoff Process
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Manual Knockoff Process</li>
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
<form:form id="uploadform"  action="manualKnockoff.do" method="POST"  commandName="manualKnockoffBean" enctype="multipart/form-data" >

					<div class="box-body" id="subcat">
					
					<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" onchange="test();">Sub Category</label> 
							<input type="text" id="rectyp" value="${category}" style="display: none"> 
							<select class="form-control" name="stSubCategory" id="stSubCategory" onchange="getFilesdata();">
								<option value="-">--Select --</option>
								<c:forEach var="subcat" items="${subcategory}">
									<option value="${subcat}">${subcat}</option>
								</c:forEach>
							</select>
							
							<%-- <form:select class="form-control" path="filename" id="filename" onchange="setfilename(this);">
						<form:option value="0" >---Select---</form:option>
						<c:forEach var="configfilelist" items="${configBeanlist}">
							<form:option id="${configfilelist.stFileName}" value="${configfilelist.stFileName}" >${configfilelist.stFileName}</form:option>
							
							</c:forEach>
							</form:select> --%>

						</div>
		<%-- <form:select class="form-control" path="stFileName" id="stFileName">
			 <form:option value="-">--Select -</form:option>
			 <form:option value="ACQUIRER">ACQUIRER</form:option>
			<form:option value="ISSUER">ISSUER</form:option> 
			<option value="CARDTOCARD">CARD TO CARD</option>
			</form:select> --%>

					<div class="form-group">
                      <label for="exampleInputEmail1">File</label><label style="color: red">*</label>
                      <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
					  <select class="form-control" id="stSelectedFile" style="" data-search="true">
						<option value="SELECT">SELECT</option>
					  </select>
                    </div>
                    
                    <div class="form-group">
                      <label for="exampleInputEmail1">Report Remarks</label>
                       <input type="text" class="form-control" id="oldRemarks" placeholder="" onkeyup = "this.value = this.value.toUpperCase();"> 
					  
                    </div>
						
						<div class="form-group">
                      <label for="exampleInputEmail1">Knockoff Remarks</label>
                       <input type="text" class="form-control" id="newRemarks" placeholder="" onkeyup = "this.value = this.value.toUpperCase();"> 
					  
                    </div>
                    
                    <div class="form-group">
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="fileDate" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
							<!-- <img alt="" src="images/listbtn.png" title="Last Uploaded File" onclick="getupldfiledetails();" style="vertical-align:middle; height: 20px; width: 20px;"> -->


						</div>	
						
						 <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="file" id="dataFile1" title="Upload File" /></td>
                      <!-- <p class="help-block">Example block-level help text here.</p> -->
                    </div>
                    
						<!--  <div class="box-footer">
                  		  <button type="button" value="UPLOAD" id="upload" onclick="return processFileUpload();" class="btn btn-primary">Upload</button>
                 		 </div>

						</div> -->
					
					
					   
                    

							

					<div class="box-footer" style="text-align: center">
						<!-- <a onclick="Process();" class="btn btn-primary">Process</a> -->
						<a onclick="processFileUpload();" class="btn btn-primary">Process</a>
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