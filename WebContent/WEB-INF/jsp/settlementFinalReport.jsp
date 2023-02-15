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
				<form:form name="form" id="form" action="settlementFinalReport.do" method="POST"  commandName="SettlementBean" >
				<div class="box box-primary">
					<!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>

					<div class="box-body" id="subcat">

				
						<div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" onchange="CallDollar()">Net Settlement Amount</label> 
							<form:input
								type="text" id="rectyp" path="category" value="${category}"
								style="display: none"/> 
								<form:input class="form-control" path="net_settl_amt"
								name="net_settl_amt" id="net_settl_amt" value="00.00"/>
								


						</div>
						<div>
						<label for="exampleInputEmail1" >Issuer Debit adjustment</label> 
						<form:input class="form-control" path="Man_Iss_represnment"
								name="Man_Iss_represnment" id="Man_Iss_represnment" value="00.00"/>
						
						</div>
						
						<div>
						<label for="exampleInputEmail1" >Acquirer Debit adjustment</label> 
						<form:input class="form-control" path="Man_ACQ_represnment"
								name="Man_ACQ_represnment" id="Man_ACQ_represnment" value="00.00"/>
						
						</div>
						
						
						<!-- <div class="form-group" id="dollor_div">
							<label for="exampleInputEmail1">Dollar Value</label> <input
								type="text" class="form-control" name="dollar_field"
								id="dollar_val" onkeypress="return Validate(event);">


						</div> -->

						<div class="form-group">
							<label for="exampleInputPassword1">Date</label> <form:input
								class="form-control" name="datepicker" path="datepicker"  readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
							<!-- <img alt="" src="images/listbtn.png" title="Last Uploaded File" onclick="getupldfiledetails();" style="vertical-align:middle; height: 20px; width: 20px;"> -->


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
	
	function getReport () {
		

		//alert("HELLO1");
		var rectyp = document.getElementById("rectyp").value;
		var net_settl_amt= document.getElementById("net_settl_amt").value;
		var form = document.getElementById("form");
	
		var datepicker = document.getElementById("datepicker").value;
		//alert("DONE");
		
			if(datepicker !="" && net_settl_amt !="" ){
			$.ajax({

				type:'POST',
				url :'settlementFinalReport.do',
				async: true,
				beforeSend : function() {
					showLoader();
				},
				complete : function(data) {

					hideLoader();

				},

				data:{category:rectyp,filedate:datepicker,subCat:subCat},
				success:function(response){
					

					alert(response);
					 document.getElementById("net_settl_amt").value="00.00";
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