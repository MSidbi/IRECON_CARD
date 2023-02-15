<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<link href="css/jquery-ui.min.css" media="all" rel="stylesheet"
	type="text/css" />

<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<!--  <script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" />
<link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" />   -->

<script type="text/javascript" src="js/reconProcess.js"></script>
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
			${category} Recon Process
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			<li><a href="#"> Home</a></li>
			<li class="active">Recon Process</li>
		</ol>
	</section>

	<!-- Main content -->
	<section class="content">
		<div class="row">
			<!-- left column -->
			<div class="col-md-6">
				<!-- general form elements -->
				<form:form name="form" id="form" action="generateSettlTTUM.do" method="POST"  commandName="SettlementBean" >
				<div class="box box-primary">
					<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>

					<div class="box-body" id="subcat">

				
						
							<form:input
								type="text" id="rectyp" path="category" value="${category}"
								style="display: none"/> 
								
								<div class="form-group">
							<label for="exampleInputPassword1">Date</label> <form:input
								class="form-control" name="datepicker" path="datepicker"  readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
							<!-- <img alt="" src="images/listbtn.png" title="Last Uploaded File" onclick="getupldfiledetails();" style="vertical-align:middle; height: 20px; width: 20px;"> -->


					
							

						</div>
					
						</div> 

						

					</div>
					<!-- /.box-body -->

					<div class="box-footer">
						<!-- <a onclick="getReport()" class="btn btn-primary">Process</a> -->
						
						<input type="submit" value="Process" >
						
						
					</div>
					<div id="processTbl"></div>
					
				</div>
				</form:form>
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
	
	function getttumReport () {
		

		//alert("HELLO1");
		var rectyp = document.getElementById("rectyp").value;
		var form = document.getElementById("form");
	
		var datepicker = document.getElementById("datepicker").value;
		//alert("DONE");
		
			if(datepicker !="" && net_settl_amt !="" ){
			$.ajax({

				type:'POST',
				url :'generateSettlTTUM.do',
				async: true,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {

					hideLoader();

				},

				data:{category:rectyp,filedate:datepicker},
				success:function(response){
					

					alert(response);
					
					// document.getElementById("dollar_val").value="";
					 document.getElementById("datepicker").value="";
					

				},error: function(){

					alert("Error Occured");

				},

			}); 
			} else {
				
				alert("Please fill all the details");
			}
		
		
		
		
		
		
		

	}
</script>