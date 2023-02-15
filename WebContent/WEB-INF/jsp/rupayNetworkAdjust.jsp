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


<link href="css/jquery-ui.min.css" media="all" rel="stylesheet"	type="text/css" />
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/RupayNetworkAdjust.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	 /* $("#datepicker").datepicker({dateFormat:"dd-M-yy", maxDate:0});
 }); */

 $("#datepicker").datepicker({dateFormat:"yy/mm/dd", maxDate:0});
});
 
window.history.forward();
function noBack() { window.history.forward(); }


</script>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			Rupay Adjustment File Upload
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Rupay Adjustment File Upload</li>
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
					
<form:form id="uploadform"  action="rupayAdjustmentFileUpload.do" method="POST"  enctype="multipart/form-data" >

					<div class="box-body" id="subcat">
					 <div class="form-group" id="Date" style="display:${display}" >
							<label>File Date</label> <input
								class="form-control" name="fileDate" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
					 </div>	 
					 
					 <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Cycle</label> 
							<select class="form-control" path="cycle" id="cycle">
								<option value="0">--Select --</option>
									 <option value="1">1</option>
									<option value="2">2</option> 
									<option value="3">3</option>
									<option value="4">4</option>
							</select>
						</div>
						
					<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Network</label> 
							<select class="form-control" path="network" id="network" onchange="getFields(this)">
								<option value="0">--Select --</option>
									 <option value="RUPAY">RUPAY</option>
									<!-- <option value="NCMC">NCMC</option> --> 
							</select>
						</div>	
					 
					 <div class="form-group" id="subCat" style="display:none">
							<label for="exampleInputEmail1" >Network</label> 
							<select class="form-control" path="subcate" id="subcate">
								<option value="0">--Select --</option>
									 <option value="DOMESTIC">DOMESTIC</option>
									<!-- <option value="INTERNATIONAL">INTERNATIONAL</option>  -->
							</select>
						</div>	
					 
						
					 <!-- <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="file" id="file" title="Upload File" />
                    </div> -->
                    
                     <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="file" id="dataFile1" title="Upload File" /></td>
                    </div>
                    
                    </div>
					<div class="box-footer" style="text-align: center">
						<a onclick="processRupayAdjustUpload();" class="btn btn-primary">Upload</a>
					</div>
					
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