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
<script type="text/javascript" src="js/NFSInterchange.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();
$("#dailypicker").datepicker({dateFormat:"dd-M-yy", maxDate:0});
$("#monthpicker").datepicker({
	 dateFormat:"mm/yy", 
		changeMonth: true,
	changeYear: true,
	showButtonPanel: true,
	/* onClose: function(){
		var iMonth = $('#ui-datpicker-div .ui-datepicker-month :selected').val();
		var iYear = $('#ui-datepicker-div .ui-datepicker-year :selected').val();
		$(this).datepicker('setDate',new Date(iYear,iMonth,1));
	} */
	onClose: function(dateText, inst){
   	$(this).datepicker('setDate',new Date(inst.selectedYear, inst.selectedMonth,1));
   }
	//dateFormat:"dd-M-yy", maxDate:0
	});


});
    

window.history.forward();
function noBack() { window.history.forward(); }


</script>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			Interchange Report
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Interchange Report</li>
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
<form:form id="reportform"  action="DownloadInterchangereport.do" method="POST"  commandName="nfsSettlementBean" enctype="multipart/form-data" >



					<div class="box-body" id="subcat">
					
					<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Income Distribution of: </label> 
							<%-- <input type="text" id="rectyp" value="${category}" style="display: none">  --%>
							<select class="form-control" name="fileName" id="fileName" onchange="FileNameChange(this)">
								<option value="0">--Select --</option>
									<option value="NTSL-DFS">DFS</option>
									<option value="NTSL-JCB-UPI">JCB-UPI</option>
									<option value="NTSL-NFS">NFS</option>
								
							</select>
						</div>
						
						
					<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Time Period</label> 
							<input type="text" id="rectyp" value="${category}" style="display: none"> 
							<form:select class="form-control" path="timePeriod" id="timePeriod" onchange="getFields(this)">
							<!-- <select class="form-control" name="timePeriod" id="timePeriod" onchange="getCycle(this)"> -->
								<option value="0">--Select --</option>
									<option value="Daily">Daily</option>
									<option value="Monthly">Monthly</option>	
							</form:select>
						</div>
						
					 <div class="form-group" id = "subCate" style="display:none">
							<label for="exampleInputEmail1">Sub Category</label> 
							<input type="text" id="rectyp" value="${category}" style="display: none"> 
							<select class="form-control" name="stSubCategory" id="stSubCategory">
								<option value="-">--Select --</option>
								<c:forEach var="subcat" items="${subcategory}">
									<option value="${subcat}">${subcat}</option>
								</c:forEach>
							</select>
						</div>
		
                    	
					  <!-- <div class="form-group" id ="cycles" style="display:none">
							<label for="exampleInputEmail1" >Cycle</label> 
							<select class="form-control" name="cycle" id="cycle">
								<option value="0">--Select --</option>
									<option value="1">1</option>
									<option value="2">2</option>	
							</select>
						</div>
                     -->
                    
                       <div class="form-group" id="Month" style="display:none" >
							<label for="exampleInputPassword1">Month</label> <input
								class="form-control" name="datepicker" readonly="readonly"
								id="monthpicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
					</div>
                    
                     <div class="form-group" id="Date" style="display:none" >
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="datepicker" readonly="readonly"
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
					
					
					   
                    

							

					<div class="box-footer">
						<button type="button" class="btn btn-primary" onclick="processInterchange();">Process</button>
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