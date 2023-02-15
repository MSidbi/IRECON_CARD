<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

    
    <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<!-- <link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" /> -->
<!-- <link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->





<title></title>
</head>


<body>
    <div class="content-wrapper">

      <!-- Content Header (Page header) -->
      <section class="content-header">
        <h1>
          Chargeback
          <!-- <small>Version 2.0</small> -->
        </h1>
        <ol class="breadcrumb">
          <li>
            <a href="#"> Home</a>
          </li>
          <li class="active">Chargeback</li>
        </ol>
      </section>

      <!-- Main content -->
      <section class="content">
        <!-- <div class="row"> -->
        <!-- left column -->
        <!-- <div class="col-md-6"> -->
        <!-- general form elements -->
        <div class="box box-primary">
          <!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> -->
          <!-- /.box-header -->
          <!-- form start -->
           <form role="form">
            <div class="box-body">
              <div class="row">
                <div class="col-lg-3">
                  <div class="form-group">
                    <label for="exampleInputEmail1">Interchange</label>
                    <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
                    <select class="form-control" id="category" style="" placeholder="Select Your Favorite" data-search="true">
                      <option value="select">-- Select --</option>
                      <option value="mastercard">MASTERCARD</option>
                    </select>
                  </div>
                </div>
                <div class="col-lg-3">
                  <div class="form-group">
                    <label for="exampleInputEmail1">ARN</label>
                    <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
                    <input class="form-control" id="arnid" style="" >
                      
                  </div>
                </div>
                <div class="col-lg-3">
                  <div class="form-group">
                    <label for="exampleInputEmail1">Date</label>
                    <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
                    <input class="form-control" id="arndate" style="" >
                      
                  </div>
                </div>
                <div class="col-lg-3">
                  <a style="margin-top: 23px;" onclick="callsearch()" class="btn btn-primary">Search</a>
                </div>
              </div>

              <div class="box-footer">

              </div>

            </div>
          </form>
          <!-- /.box -->



        </div>
        <div class="box box-primary">
          <div class="box-header">
                  <h3 class="box-title">Chargeback</h3>
                </div>
          <!-- /.box-header
          <!-- form start -->
          <form role="form" >
          <!-- <form method="POST" action="GenerateMastercardChargebk.do" id="form"> -->
            <div class="box-body">
              <div class="row">
                <div class="col-md-5">
                  <div class="form-group">
                    <label for="exampleInputEmail1">Microfilm</label>
                    <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
                    <input class="form-control" id="microfilmid" style="" value="" disabled >
                  </div>
                </div>
                <div class="col-lg-4">
                  <button style="margin-top: 23px;" class="btn btn-primary">Info</button>
                </div>
              </div>
              <div class="row">
                <div class="col-md-4">
                  <div class="form-group">
                    <label for="exampleInputEmail1">Reference Id</label>
                    <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
                    <input class="form-control" id="refid" style="">
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="col-md-4">
                  <div class="form-group">
                    <label for="exampleInputPassword1">Settle Amount</label>
                    <input type="text" class="form-control" id="settlmentamount" placeholder="">
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="form-group">
                    <label for="exampleInputPassword1">Settle Currency</label>
                    <select class="form-control" id="settlmentCurrid" >
                      <option value="select">-- Select --</option>
                                        </select>
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="col-md-4">
                  <div class="form-group">
                    <label for="exampleInputPassword1">Txn. Amount</label>
                    <input type="text" class="form-control" id="txnamountid" placeholder="">
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="form-group">
                    <label for="exampleInputPassword1">Txn. Currency</label>
                    <select class="form-control" id="txtcurrencyid" >
                      <option value="select">-- Select --</option>
                      </select>
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="col-md-4">
                  <div class="form-group">
                    <label for="exampleInputPassword1">Reason</label>
                      <select class="form-control" id="reason" >
                      <option value="select">-- Select --</option>
                      <option value="4807">Cardholder Denies Transaction</option>
					<option value="4515">Warning bulletin  </option>
					<option value="4808">Requested/required</option>
					<option value="4812">Account number was not on file.</option>
					<option value="4831">Transaction amount differs.</option>
					<option value="4834">Duplicate processing</option>
					<option value="4837">Fraudulent transaction; nocardholder authorization</option>
					<option value="4840">Fraudulent processing of</option>
					<option value="4841">Canceled recurring transaction</option>
					<option value="4842">Late presentment</option>
					<option value="4846">Correct transaction currency code was not provided.</option>
					<option value="4849">Questionable card acceptor</option>
					<option value="4850">Installment Transaction Dispute18</option>
					<option value="4853">Cardholder Dispute Defective/Not as Described</option>
					<option value="4855">Non-receipt of merchandise </option>
					<option value="4859">Services not rendered </option>
					<option value="4860">Credit not processed </option>
					<option value="4863">Cardholder does not recognize—Potential fraud</option>
					<option value="4807">Warning bulletin </option>
					<option value="4808">Requested/required authorization not obtained</option>
					<option value="4812">Account number was not onfile.</option>
					<option value="4831">Transaction amount differs. </option>
					<option value="4834">Duplicate processing.  </option>
					<option value="4837">Fraudulent transaction; no cardholder authorization</option>
					<option value="4840">Fraudulent processing of transaction</option>
					<option value="4841">Canceled recurring transaction</option>
					<option value="4842">Late presentment Yes N/A N/A</option>
					<option value="4846">Correct transaction currency code was not provided.</option>
					<option value="4849">Questionable card acceptor activity</option>
					<option value="4850">Installment Transaction Dispute19</option>
					<option value="4853">Cardholder DisputeDefective/Not as Described</option>
					<option value="4855">Non-receipt of merchandise</option>
					<option value="4859">Services not rendered </option>
					<option value="4860">Credit not processed  </option>
					<option value="4863">Cardholder does not recognize—Potential fraud</option>
					<option value="4901">Required documentation not received to support prior Second Presentment</option>
					<option value="4902">Documentation received was illegible.</option>
					<option value="4903">Scanning error–unrelated documents or partial scan</option>
					<option value="4905">Invalid Acquirer Reference Data in Second Presentment</option>
					<option value="4908">Invalid Acquirer Reference Data in Second Presentment</option>

                    </select>
                  
                  </div>
                </div>
                <div class="col-md-4">
                  <div class="form-group">
                    <label for="exampleInputPassword1">Documentation</label>
                    <select class="form-control" id="dcosid" >
                      <option value="select">-- Select --</option>
                      <option value="1">DOCS IS REQUIRED</option>
                      <option value="0">DOCS IS NOT REQUIRED</option>
                    </select>
                  </div>
                </div>
              </div>
              <div id="remarksDiv" class="clearfix"
										style="">
										<!--                                        Rejection Remarks:
                                        <textarea id="rejectTextArea"></textarea>-->
										<div class="col-sm-4 well">
											<form accept-charset="UTF-8" action="" method="POST">
												<textarea class="form-control" id="remarksTextArea"
													name="text" placeholder="Type in Remarks"
													rows="5"></textarea>
												<h6 class="pull-right" id="count_message"></h6>

											</form>
										</div>
									</div>
              <!-- <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" id="exampleInputFile">
                      <p class="help-block">Example block-level help text here.</p>
                    </div> -->
              <!-- <div class="checkbox">
                      <label>
                        <input type="checkbox"> Check me out
                      </label>
                    </div> -->
            </div>
            <!-- /.box-body -->

            <div class="box-footer">
              <button onclick="savechargeback1()" class="btn btn-primary">Process</button>
              <button type="submit" class="btn btn-danger">Cancel</button>
            </div>
            
          </form>
         
        </div>
        <!-- /.box -->



        <!-- </div> -->
        <!--/.col (left) -->

        <!-- </div> -->
        <!-- /.row -->
      </section>
    </div>
    <!-- /.content-wrapper -->
   <!--  <form action="GenerateMastercardChargebk.do" method="GET" id="formid">
      </form> -->
   
      <form action="GenerateMastercardChargebk.do" method="POST" id="formid" >
      
                 <input  name="Reasonval" id="reasonval" type="hidden"/>
				 <input  name="Arn" id="arnnum"  type="hidden"/>
				 <input  name="Arndate" id="arndateval"  type="hidden"/>  
				 
				<!--  <input class="form-control" name="arnnum" id="arnnum"  style="">
				<input type="hidden" name="Reason" id="reason" /> -->
				</form>
    </body>
    <script>
    
    $(document).ready(function() {
		$("#arndate").datepicker({dateFormat:"mm/dd/yy", maxDate:0});
		
		//var iCnt = 0;
		});
    
    function callsearch()
    {

		debugger;
		var arnid = $("#arnid").val();
		
		
		
				//$('#targetMain').slideDown();
		$.ajax({
			type : "POST",
			url : "GetChargeback.do",
			//contentType:"application/json",
			data : {
				ArnNo : arnid
			},
				   	        timeout: 30000,
			// data: { name: "John", location: "Boston" }, // parameters
			success : function(response) {
				debugger;
				//alert(response.Records[0].full_name);
				//response = $.parseJSON(response);
				//$('#name').append(response.Records[0].full_name);
				//$('#name').text(response.Records[0].full_name);
				var microfilmid = $("#arnid").val();
				$('#microfilmid').val(microfilmid);
				$('#settlmentamount').val(response.Records[0].settlement_amount);
				$('#settlmentCurrid').append('<option value=' + response.Records[0].settlement_currency + '>' + response.Records[0].settlement_currency + '</option>');
				$('#txtcurrencyid').append('<option value=' + response.Records[0].txn_currency + '>' + response.Records[0].txn_currency + '</option>');
				//$('#settlmentCurrid').val(response.Records[0].settlement_currency);
				$('#txnamountid').val(response.Records[0].txn_amount);
				
				
								
				
				
				/* outbal
				application_con_fee */

				/* $('#actualConversion').val(response.Records.actualConversion);
				$('#amountWaiver').val(	($('#conversionFee').val(response.Records.conversionFee)-($('#actualConversion').val(response.Records.actualConversion)))); */

			},
			error : function(err) {
				alert(err);
			}
		});

	
    }
    
   
    
    function formsubmit()
    {

		debugger;
		var arnid = $("#arnid").val();
		var reason = $("#reason").val();
		
		
		
				//$('#targetMain').slideDown();
		$.ajax({
			type : "POST",
			url : "GenerateMastercardChargebk.do",
			//contentType:"application/json",
			data : {
				ArnNo : arnid,
				Reason : reason
			},
				   	        timeout: 30000,
			// data: { name: "John", location: "Boston" }, // parameters
			success : function(response) {
				debugger;
				alert("Success");
				
				
				
				/* outbal
				application_con_fee */

				/* $('#actualConversion').val(response.Records.actualConversion);
				$('#amountWaiver').val(	($('#conversionFee').val(response.Records.conversionFee)-($('#actualConversion').val(response.Records.actualConversion)))); */

			},
			error : function(err) {
				alert(err);
			}
		});

	
    }
    
    function savechargeback1()
    {

		debugger;
		var arnid = $("#arnid").val();
		var microfilmid = $('#microfilmid').val();
		var refid = $('#refid').val();
		var settlmentamount = $('#settlmentamount').val();
		var settlmentCurrid = $('#settlmentCurrid').val();
		var txnamountid = $('#txnamountid').val();
		var txtcurrencyid = $('#txtcurrencyid').val();
		var reason = $('#reason').val();
		var dcosid = $('#dcosid').val();
				var remrk = $('#remarksTextArea').val();
		
		
		
				//$('#targetMain').slideDown();
		$.ajax({
			type : "POST",
			url : "Savemastercardchargebk.do",
			//contentType:"application/json",
			data : {
				Microfilmid : microfilmid,
			   Refid : refid,
			 Settlmentamount : settlmentamount,
				SettlmentCurrid : settlmentCurrid,
				Txnamountid : txnamountid,
				Txtcurrencyid : txtcurrencyid,
				Reason : reason,
				Dcosid : dcosid,
				Remrk : remrk
			},
				   	        timeout: 30000,
			// data: { name: "John", location: "Boston" }, // parameters
			success : function(response) {
				debugger;
				//alert(response.Records[0].full_name);
				//alert("Success");
				var arnid = $("#arnid").val();
				var reason = $('#reason').val();
				var arndate = $('#arndate').val();
				 downlaodRepo(arnid,reason,arndate);
				//downloadFileUsingForm(); 
				//formsubmit();
				//location.reload();
								
				
				
				/* outbal
				application_con_fee */

				/* $('#actualConversion').val(response.Records.actualConversion);
				$('#amountWaiver').val(	($('#conversionFee').val(response.Records.conversionFee)-($('#actualConversion').val(response.Records.actualConversion)))); */

			},
			error : function(err) {
				alert(err);
			}
		});

	
    }
    
    function savechargeback() {
		debugger;
		//alert("inside save");
		var microfilmid = $('#microfilmid').val();
		var refid = $('#refid').val();
		var settlmentamount = $('#settlmentamount').val();
		var settlmentCurrid = $('#settlmentCurrid').val();
		var txnamountid = $('#txnamountid').val();
		var txtcurrencyid = $('#txtcurrencyid').val();
		var reason = $('#reason').val();
		var dcosid = $('#dcosid').val();
		var remrk = $('#remarksTextArea').val();
		
		$.ajax({
			type : "POST",
			url : "Savemastercardchargebk.do",
			//contentType:"application/json",
			data : {
				Microfilmid : microfilmid
				/* Refid : refid,
				Settlmentamount : settlmentamount,
				SettlmentCurrid : settlmentCurrid,
				Txnamountid : txnamountid,
				Txtcurrencyid : txtcurrencyid,
				Reason : reason,
				Remrk : remrk, */
				},
			/* async : false,
			cache : false,
			timeout : 30000, */

			// data: { name: "John", location: "Boston" }, // parameters
			success : function(response) {
				debugger;
				//$("#successAlert").modal('show');
				//location.reload();
				alert("Success");

			},
			error : function(err) {
				
				alert(err);
				
			}
		});

	}
   
    
    
    function downloadFileUsingForm() { 
    	debugger;
        
    	
    	debugger;
    	//var dateval=$("#dateval").text();
    	//var input = $("<input>").attr("type", "hidden").attr("name", "dateval").val(dateval);
       // $('#formId').append(input);
    	$("#formid").submit();
         
    	/* var form = document.createElement("form");
        form.method = "POST";
        form.action = "GenerateMastercardChargebk.do";
        document.body.appendChild(form);
        form.submit();
        document.body.removeChild(form);  */
        
      /*   $.ajax({
        	type:'POST',
        	url: "GenerateMastercardChargebk.do"
        	//data: jsonfile,
        	//dataType: "json"
        	}); */
       
    }
    
    
    //downloadFileUsingForm("/SettlementController/GenerateMastercardChargebk.do");

					function downlaodRepo(arnnum,reason,arndate) {
						debugger;
						
						$('#reasonval').val(reason);
						$('#arnnum').val(arnnum);
						$('#arndateval').val(arndate);
						
						
						
						$('#formid').submit();

					}
					
					
				</script>
    
</html>