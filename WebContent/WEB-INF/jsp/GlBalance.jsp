<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 <link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/glbalance.js"></script>

<script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();
  
    $("#datepicker").datepicker({dateFormat:"dd-M-yy", maxDate:0});
    });
   
   var iCnt=0;
function addSettlementRow(){
	debugger;
	
	if(iCnt<= 30)
		{
		iCnt = iCnt + 1;
	$("#settlementAddDiv").append('<div class="row"><div class="col-lg-6"><div class="form-group"><label for="exampleInputEmail1">Date </label><input type="text" class="form-control settlementDate" id="settlementDate'+iCnt+'" placeholder="" ></div></div><div class="col-lg-4"><div class="form-group"><label for="exampleInputEmail1">Amount </label><input type="text" class="form-control settlementAmount" id="settlementAmount'+iCnt+'" placeholder=""></div></div></div>');
	
	$("#settlementDate"+iCnt+"").datepicker({dateFormat:"dd/M/yy", maxDate:0});
	
		}
}
    

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
			<li class="active">GL Balance</li>
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
		<form:form name="reportform" id="reportform" action="GLReportDownload.do" method="POST" commandName="SettlementBean" >
					<div class="box-body" id="subcat">

					<div class="form-group" >
							<label for="exampleInputEmail1" >Sub Category</label> <input
								type="text" id="rectyp" name="rectyp" value="${category}"
								style="display: none"> <select class="form-control"
								name="stSubCategory" id="stSubCategory">
								<option value="-">--Select --</option>
								
									<option value="ISSUER">ISSUER</option>
									<option value="ACQUIRER">ACQUIRER</option>
							</select>
					
						
						
						

						<div class="form-group">
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="fileDate" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy"  onchange="return displayBalance();"/>
							<!-- <img alt="" src="images/listbtn.png" title="Last Uploaded File" onclick="getupldfiledetails();" style="vertical-align:middle; height: 20px; width: 20px;"> -->


						</div>
						
						
						
						<div class="form-group" id="dollor_div">
							<label for="exampleInputEmail1">Closing Balance</label> <input
								type="text" class="form-control" name="closing_balance"
								id="closing_balance" onkeypress=" setValueType(this,'ip');" onchange="return caldifference();" >


						</div>
						
						<div class="form-group" id="dollor_div">
							<label for="exampleInputEmail1">Total Cash Dispensed </label>
							<div class="input-group">
							 <input
								type="text" class="form-control" name="cash_dispense"
								id="cash_dispense" onkeypress=" setValueType(this,'ip');" onchange="return caldifference();" style="z-index: 0" readonly="readonly">
								
								<div id="setdt" style="display: none;">
								<input type="text"  id="item_settlamnt" name="item_settlamnt" value="">
								<input type="text"  id="item_setdt" name="item_setdt" value="">
								</div>
								<span class="input-group-btn">
                      			<button type="button" class="btn btn-info btn-flat" data-toggle="modal" data-target="#settlementAdd">ADD</button>
                    		</span>
                    		</div>

						</div>
						
						
						
						<div class="form-group" id="dollor_div">
							<label for="exampleInputEmail1">Difference</label> <input
								type="text" class="form-control" name="difference" id="difference" readonly="readonly" id="difference" >


						</div>

					</div>
					<!-- /.box-body -->

					<div class="box-footer">
						<a onclick="Process();" class="btn btn-primary">Process</a>
					</div>
					<div id="processTbl"></div>
					
					<%-- </form> --%>
				</div>
				<!-- /.box -->
		</form:form>


			</div>
			<!--/.col (left) -->
			
			<div align="center" id="Loader"
	style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

	<img style="margin-left: 20px; margin-top: 200px;"
		src="images/unnamed.gif" alt="loader">

</div>

		</div>
		<!-- /.row -->
	</section>
</div>
<!-- /.content-wrapper -->


<!-- Modal -->
<div id="settlementAdd" class="modal fade" role="dialog">
  <div class="modal-dialog">

    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title">Add Settlement</h4>
        <button type="button" class="btn btn-sm btn-primary" onclick=addSettlementRow(); style="float: right;margin-top: -29px;margin-right: 33px;" >Add</button>
      </div>
      <div class="modal-body" style="height: 300px;overflow-y: scroll;">
      <div class="row">
      	
      </div>
       
              <div id="settlementAddDiv"></div>
      </div>
      <div class="modal-footer">
      <button class="btn btn-default" data-dismiss="modal" onclick="Settle()">Save</button>
       <!--  <a  class="btn btn-default" onclick="settle()" data-dismiss="modal">Save</a> -->
      </div>
      
    </div>
   
  </div>
</div>

<div id="settlementdispense" class="modal fade" role="dialog">
  <div class="modal-dialog">

		 <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" onclick="closeModal();">&times;</button>
        <h4 class="modal-title">Previous Dispense</h4>
        <!-- <button type="button" class="btn btn-sm btn-primary" onclick=addSettlementRow(); style="float: right;margin-top: -29px;margin-right: 33px;" >Add</button> -->
      </div>
      <div class="modal-body" style="height: 300px;overflow-y: scroll;">
      <div class="row">
      
      	
      	
      </div>
       
              <div id="prevDispense"></div>
      </div>
      <div class="modal-footer">
      <!-- <button class="btn btn-default" data-dismiss="modal" onclick="Settle()">Save</button> -->
       <!--  <a  class="btn btn-default" onclick="settle()" data-dismiss="modal">Save</a> -->
      </div>
      
    </div>
    </div>
			
</div>