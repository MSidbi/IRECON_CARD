<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>

<script>
$(document).ready(function() {
$("#glDate").datepicker({dateFormat:"dd/M/yy", maxDate:0});
$("#endglDate").datepicker({dateFormat:"dd/M/yy", maxDate:0});

$("#glReport").hide();
//var iCnt = 0;
});

function onchange()
{
	debugger;
	$("#glReport").show();
	//alert($("#glDate").val());
	$("#dateval").text($("#glDate").val());
	$("#dateval1").text($("#endglDate").val());
	//$("#settlementDate").datepicker({dateFormat:"dd/M/yy", maxDate:0});
	
	$('#glDate').addClass('disable');
	//$('#endglDate').addClass('disable');
	}
	
function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}
function fetchRec()
{

	debugger;
	var date_val = $("#dateval").text();
	var date_val2 = $("#dateval1").text();
	var closing_bal = $("#closing_bal").val();
	
	
	var settlement_id = $("#settlement_id").val();
	
	var diff1=closing_bal-settlement_id;
	var diff = diff1.toFixed(2);
	$("#diff").val(diff);
	//jsonObj_settle["filedate"]=date_val;
	
	$('#closing_bal').addClass('disable');
	$('#settlement_id').addClass('disable');
	
	
	
	
			//$('#targetMain').slideDown();
	$.ajax({
		type : "POST",
		url : "Get_glbalance.do",
		//contentType:"application/json",
		data : {
			Date_val : date_val,
			Date_val2 : date_val2,
		
			beforeSend : function() {
				showLoader();
			},
					},
			   	        //timeout: 30000,
		// data: { name: "John", location: "Boston" }, // parameters
		success : function(response) {
			debugger;
			hideLoader();
			//alert(response.Records[0].full_name);
			//response = $.parseJSON(response);
			//$('#name').append(response.Records[0].full_name);
			//$('#name').text(response.Records[0].full_name);
			
			$('#settlement_match').val(response.Records[0].settlement_matched);
			$('#nxtdate').val(response.Records[0].nxtdate);
			$('#surcharge').val(response.Records[0].surcharge);
			$('#lpcase').val(response.Records[0].lpcase);
			$('#cbs_unrecon').val((response.Records[0].cbs_unrecon));
			$('#switch_unrecon').val((response.Records[0].switch_unrecon));
			var nobase2=(parseFloat(response.Records[0].cbs_unrecon)+parseFloat(response.Records[0].switch_unrecon)); 
			
			//var nobas=$('#nobase2').val();
			
			var nobse3=nobase2.toFixed(2);
			$('#nobase2').val(nobse3);
			 var settlemtn=(parseFloat(response.Records[0].settlement_matched)+parseFloat(response.Records[0].nxtdate)+parseFloat(response.Records[0].surcharge)+parseFloat(response.Records[0].lpcase));
			
			var settl2=settlemtn.toFixed(2);
			$('#settlementTotal').val(settl2);
			
			for(var i=0;i<response.Records1.length;i++)
				{
			$('#myformelement').append('<div class="form-group row"><label for="example-text-input" class="col-md-5 col-form-label"><span id="surcharge'+i+'""></span></label><div class="col-md-7"><input class="form-control disable surcharge_class" type="text" value="" id="surcharge'+i+'_inp"></div>');
			
			$('#surcharge'+i+'').text(response.Records1[i].filedate);
			$('#surcharge'+i+'_inp').val(parseFloat(response.Records1[i].surhcrage_amount));
			
				}
			var sum = 0;
			for(var i=0;i<response.Records1.length;i++)
				{
				
				sum +=parseFloat($('#surcharge'+i+'_inp').val());
				//var sur1=$('#surcharge'+i+'_inp').val();
				var sum_val=sum.toFixed(2);
				$('#surch_total').val(sum_val);
				}
			    // Create a file input
			   for(var k=0;k<response.Records2.length;k++)
				   {
				   $('#Autorev').append('<div class="form-group row"><label for="example-text-input" class="col-md-5 col-form-label"><span id="autorev'+k+'""></span></label><div class="col-md-7"><input class="form-control disable autorev_class" type="text" value="" id="autorev'+k+'_inp"></div>');
				   
				   $('#autorev'+k+'').text(response.Records2[k].filedate);
					$('#autorev'+k+'_inp').val(parseFloat(response.Records2[k].amount));
				   
				   }
			   var sum1 = 0;
				for(var k=0;k<response.Records2.length;k++)
					{
					
					sum1 +=parseFloat($('#autorev'+k+'_inp').val());
					//var sur1=$('#surcharge'+i+'_inp').val();
					var sum_val1=sum1.toFixed(2);
					$('#autorev_total').val(sum_val1);
					}
				
				//LP cases
				
				
			    // Create a file input
			   for(var l=0;l<response.Records3.length;l++)
				   {
				   $('#Lpcase').append('<div class="form-group row"><label for="example-text-input" class="col-md-5 col-form-label"><span id="lpcase'+l+'""></span></label><div class="col-md-7"><input class="form-control disable lpcases" type="text" value="" id="lpcase'+l+'_inp"></div>');
				   
				   $('#lpcase'+l+'').text(response.Records3[l].filedate);
					$('#lpcase'+l+'_inp').val(parseFloat(response.Records3[l].amount));
				   
				   }
			   var sum2 = 0;
				for(var m=0;m<response.Records3.length;m++)
					{
					
					sum2 +=parseFloat($('#lpcase'+m+'_inp').val());
					//var sur1=$('#surcharge'+i+'_inp').val();
					var sum_val2=sum2.toFixed(2);
					$('#lpcasetotal').val(sum_val2);
					}
			  
				var final_total=(parseFloat($('#settlementTotal').val())-parseFloat($('#surch_total').val())-parseFloat($('#lpcasetotal').val())+parseFloat($("#diff").val()-0));
			     var final_val=final_total.toFixed(2);
				$('#final_total').val(final_val);
				/* var nobase21=$('#cbs_unrecon').val();
			var nobase22=$('#switch_unrecon').val();
			var nobase23=nobase21+nobase22;
			$('#nobase2').val(nobase23); */
		},
		error : function(err) {
			alert(err);
		}
	});


}

function SaveRec()
{
	debugger;
	 var item_setdt = [];
	  var item_settlamnt = [];
	var jsonObj={} ;
	/* $('.settlementDate').each(function(){
		//  var i = this.name.split('surcharge_amount')[1];
		  item_setdt.push( $(this).val());
		});
	jsonObj["settlementDate"] = item_setdt;
	
	$('.settlementAmount').each(function(){
		//  var i = this.name.split('surcharge_amount')[1];
		  item_settlamnt.push( $(this).val());
		});
	jsonObj["settlementAmount"] = item_setdt; */
	  var item = [];
	  var item1 = [];
	  var item2 = [];
	  
	$('.surcharge_class').each(function(){
	//  var i = this.name.split('surcharge_amount')[1];
	  item.push( $(this).val());
	});
	$('.autorev_class').each(function(){
		//  var i = this.name.split('surcharge_amount')[1];
		  item1.push( $(this).val());
		});
	$('.lpcases').each(function(){
		//  var i = this.name.split('surcharge_amount')[1];
		  item2.push( $(this).val());
		});
	jsonObj["closingbal"]= $("#closing_bal").val();
	jsonObj["settlementid"] = $("#settlement_id").val();
	
	jsonObj["surchargeamount"] = item;
	jsonObj["autorevamount"] = item1;
	jsonObj["lpcases"] = item2;
	
	jsonObj["settlementmatch"]=$('#settlement_match').val();
	jsonObj["nxtdate"]=$('#nxtdate').val();
	jsonObj["surcharge"]=$('#surcharge').val();
	jsonObj["lpcase"]=$('#lpcase').val();
	jsonObj["cbs_unrecon"]=$('#cbs_unrecon').val();
	jsonObj["switchunrecon"]=$('#switch_unrecon').val();
	jsonObj["surchtotal"]=$('#surch_total').val();
	jsonObj["autorevtotal"]=$('#autorev_total').val();
	jsonObj["lpcasetotal"]=$('#lpcasetotal').val();
	jsonObj["diff"] = $("#diff").val();
	
	jsonObj["nobase2"]=$('#nobase2').val();
	jsonObj["settlementTotal"]=$('#settlementTotal').val();
	
	jsonObj["finaltotal"]=$('#final_total').val();
	jsonObj["dateval"]=$("#dateval").text();
	
	$.ajax({
		type : "POST",
		url : "Save.do",
		//data : oMyForm,
		data : 	jsonObj,
		success : function(response) {
			debugger;
			alert("Click here to dowbload the report");
			generateFile();
			//$("#successAlert").modal('show');

		},

		error : function(err) {
			//$("#failureAlert").modal('show');
		}
	});

}
	
	
function SaveRec()
{
	debugger;
	 var item_setdt = [];
	  var item_settlamnt = [];
	var jsonObj={} ;
	/* $('.settlementDate').each(function(){
		//  var i = this.name.split('surcharge_amount')[1];
		  item_setdt.push( $(this).val());
		});
	jsonObj["settlementDate"] = item_setdt;
	
	$('.settlementAmount').each(function(){
		//  var i = this.name.split('surcharge_amount')[1];
		  item_settlamnt.push( $(this).val());
		});
	jsonObj["settlementAmount"] = item_setdt; */
	  var item = [];
	  var item1 = [];
	  var item2 = [];
	  
	$('.surcharge_class').each(function(){
	//  var i = this.name.split('surcharge_amount')[1];
	  item.push( $(this).val());
	});
	$('.autorev_class').each(function(){
		//  var i = this.name.split('surcharge_amount')[1];
		  item1.push( $(this).val());
		});
	$('.lpcases').each(function(){
		//  var i = this.name.split('surcharge_amount')[1];
		  item2.push( $(this).val());
		});
	jsonObj["closingbal"]= $("#closing_bal").val();
	jsonObj["settlementid"] = $("#settlement_id").val();
	
	jsonObj["surchargeamount"] = item;
	jsonObj["autorevamount"] = item1;
	jsonObj["lpcases"] = item2;
	
	jsonObj["settlementmatch"]=$('#settlement_match').val();
	jsonObj["nxtdate"]=$('#nxtdate').val();
	jsonObj["surcharge"]=$('#surcharge').val();
	jsonObj["lpcase"]=$('#lpcase').val();
	jsonObj["cbs_unrecon"]=$('#cbs_unrecon').val();
	jsonObj["switchunrecon"]=$('#switch_unrecon').val();
	jsonObj["surchtotal"]=$('#surch_total').val();
	jsonObj["autorevtotal"]=$('#autorev_total').val();
	jsonObj["lpcasetotal"]=$('#lpcasetotal').val();
	jsonObj["diff"] = $("#diff").val();
	
	jsonObj["nobase2"]=$('#nobase2').val();
	jsonObj["settlementTotal"]=$('#settlementTotal').val();
	
	jsonObj["finaltotal"]=$('#final_total').val();
	jsonObj["dateval"]=$("#dateval").text();
	
	$.ajax({
		type : "POST",
		url : "Save.do",
		//data : oMyForm,
		data : 	jsonObj,
		success : function(response) {
			debugger;
			alert("Click here to dowbload the report");
			generateFile();
			//$("#successAlert").modal('show');

		},

		error : function(err) {
			//$("#failureAlert").modal('show');
		}
	});

}

function getSettlement()
{
	debugger;
	//alert("Cas");
	 var item_setdt = [];
	  var item_settlamnt = [];
	var jsonObj={} ;
	var countval1=0;
	var countval2=0;
	var settledate;
	var settleAmount;
	$('.settlementDate').each(function(){
		//  var i = this.name.split('surcharge_amount')[1];
		// item_setdt.push( $(this).val());
		  countval1=countval1+1;
		  settledate=$('#settlementDate'+countval1+'').val();
		});
	jsonObj["settlementDate"] = settledate;
	
	$('.settlementAmount').each(function(){
		//  var i = this.name.split('surcharge_amount')[1];
		 // item_settlamnt.push( $(this).val());
		  countval2=countval2+1;
		  settleAmount=$('#settlementAmount'+countval2+'').val();
		});
	jsonObj["settlementAmount"] = settleAmount;
	
	$.ajax({
		type : "POST",
		url : "Get_Settlemntdtamnt.do",
		//data : oMyForm,
		data : 	jsonObj,
		success : function(response) {
			debugger;
			//alert("Ok");
			var countval=0;
			// $('#labelAmount1').html(response);
			$('.lableamount').each(function(){
				//  var i = this.name.split('surcharge_amount')[1];
				
				 countval=countval+1;
				 $('#labelAmount'+countval+'').html(response);
				});
			
		},

		error : function(err) {
			//$("#failureAlert").modal('show');
		}
	});

}
	
function generateFile()
{
	debugger;
	var dateval=$("#dateval").text();
	var input = $("<input>").attr("type", "hidden").attr("name", "dateval").val(dateval);
    $('#formId').append(input);
	$("#formId").submit();
	
	}
	
var iCnt = 0;
var sum_total=0;
	function addSettlementRow(){
		debugger;
		
		if(iCnt<= 30)
			{
			iCnt = iCnt + 1;
		$("#settlementAddDiv").append('<div class="row"><div class="col-lg-6"><div class="form-group"><label for="exampleInputEmail1">Date </label><input type="text" class="form-control settlementDate" id="settlementDate'+iCnt+'" placeholder="" ></div></div><div class="col-lg-4"><div class="form-group"><label for="exampleInputEmail1">Amount </label><input type="text" class="form-control settlementAmount" id="settlementAmount'+iCnt+'" onchange="getSettlement()" placeholder=""><label id="labelAmount'+iCnt+'" class="label-danger lableamount" style="font-size: 10px;"></label></div></div></div>');
		
		$("#settlementDate"+iCnt+"").datepicker({dateFormat:"dd/M/yy", maxDate:0});
		
			}
	}
	
	function Settle()
	{
		debugger;
		//var jsonObj={} ;
		var jsonObj_settle={} ;
		
		  var item_setdt = [];
		  var item_settlamnt = [];
		for(var i=1;i<=iCnt;i++)
		{
		
		sum_total +=parseFloat($("#settlementAmount"+i+"").val());
		//var sur1=$('#surcharge'+i+'_inp').val();
		var sum_val12=sum_total.toFixed(2);
		$('#settlement_id').val(sum_val12);
		}
		
		var date_val = $("#dateval").text();
		var date_val2 = $("#dateval1").text();
		
		var d1 = Date.parse(date_val);
		var d2 = Date.parse(date_val2);
		if (d1 <= d2) {
			 var date = new Date(d1);
			 var date1 = new Date(d2);
			    var newdate = new Date(date);
			    var newdate1 = new Date(date1);

			    newdate.setDate(newdate.getDate() + 1);
			    
			    var dd = newdate.getDate();
			    var mm = newdate.getMonth() + 1;
			    var y = newdate.getFullYear();
			    var dd1 = newdate1.getDate();
			    
			    var someFormattedDate = mm + '/' + dd + '/' + y;
			    for(var i=dd;i<=dd1;i++)
			    	{
			    	var someFormattedDate1 = mm + '/' + dd + '/' + y;
			    	
			    	}
		}
		
		fetchRec();
		}
	
</script>


    <!-- Right side column. Contains the navbar and content of the page -->
    <div class="content-wrapper">

      <!-- Content Header (Page header) -->
      <section class="content-header">
        <h1>
         Rupay GL Report
          <!-- <small>Version 2.0</small> -->
        </h1>
        <ol class="breadcrumb">
          <li>
            <a href="#"> Home</a>
          </li>
          <li class="active"> GL Report</li>
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
                <div class="col-lg-4">
                  <div class="form-group">
                    <label for="exampleInputEmail1">Start Date </label>
                    <input type="text" class="form-control" id="glDate" placeholder=""  >
                    
                  </div>
                </div>
                <!-- <div class="col-lg-3">
                  <a style="margin-top: 23px;" onclick="onchange()" class="btn btn-primary">Process</a>
                </div> -->
              </div>
              
              <div class="row">
                <div class="col-lg-4">
                  <div class="form-group">
                    <label for="exampleInputEmail1">End Date </label>
                    <input type="text" class="form-control" id="endglDate" placeholder="" onclick="onchange()" >
                    
                  </div>
                </div>
                <!-- <div class="col-lg-3">
                  <a style="margin-top: 23px;" onclick="onchange()" class="btn btn-primary">Process</a>
                </div> -->
              </div>
<!-- 
              <div class="box-footer">

              </div> -->

            </div>
          </form>
          <!-- /.box -->



        </div>
        <div class="box box-primary" id="glReport" style="display: none;">
          <div class="box-header">
                  <h3 class="box-title">Rupay GL</h3>
                </div>
                
          <!-- /.box-header
          <!-- form start -->
          <form role="form" method="post" action="Download_Repo.do" id="formId">
            <div class="box-body">
              <div class="row">
                  
                
                 
                    
                    <div class="col-md-12">
                    <div>Start date <b><span id="dateval"></span></b></div>
                    <div>End date <b><span id="dateval1"></span></b></div>
                    <div class="bs-callout bs-callout-info" id="callout-alerts-no-default"> 
                    <div class="row">
                    	<div class="col-md-6">
                    		<div class="form-group row">
                            <label for="example-text-input" class="col-md-5 col-form-label">Closing balance as on<span style="color: red">*</span> </label>
                            <div class="col-md-7">
                              <input class="form-control" type="text" value="" id="closing_bal">
                            </div>
                          </div>
                          
                          <!-- <div class="input-group input-group-sm">
                <input type="text" class="form-control">
                    <span class="input-group-btn">
                      <button type="button" class="btn btn-info btn-flat">Go!</button>
                    </span>
              </div> -->
                          
                          
                          <div class="form-group row">
                              <label for="example-text-input" class="col-md-5 col-form-label">Settlement<span style="color: red">*</span> </label>
                              <div class="col-md-7 input-group" style="padding-right: 15px !important;padding-left: 15px !important;">
                              <input type="text" class="form-control" id="settlement_id" onchange="fetchRec()">
                    		<span class="input-group-btn">
                      			<button type="button" class="btn btn-info btn-flat" data-toggle="modal" data-target="#settlementAdd">+ ADD</button>
                    		</span>
                                <!-- <input class="form-control" type="text"  value="" id="settlement_id" onchange="fetchRec()"> -->
                              </div>
                            </div>
                            <div class="form-group row">
                                <label for="example-text-input" class="col-md-5 col-form-label">Difference </label>
                                <div class="col-md-7">
                                  <input class="form-control disable" type="text" value="" id="diff" style="font-weight: bold">
                                </div>
                              </div>
                    	</div>
                    	
                    	<div class="col-md-6">
                    	<div style="height: 140px;overflow-y: scroll;overflow-x: hidden;">
                    		<div class="form-group row">
                            <label for="example-text-input" class="col-md-5 col-form-label">Settlement matched </label>
                            <div class="col-md-7">
                              <input class="form-control disable"  type="text" value="" id="settlement_match">
                            </div>
                          </div>
                          <div class="form-group row">
                              <label for="example-text-input" class="col-md-5 col-form-label">Next Date </label>
                              <div class="col-md-7">
                                <input class="form-control disable" type="text" value="" id="nxtdate">
                              </div>
                            </div>
                            <div class="form-group row">
                                <label for="example-text-input" class="col-md-5 col-form-label">Surcharge </label>
                                <div class="col-md-7">
                                  <input class="form-control disable" type="text" value="" id="surcharge">
                                </div>
                              </div>
                              <div class="form-group row">
                                  <label for="example-text-input" class="col-md-5 col-form-label">Lp Case </label>
                                  <div class="col-md-7">
                                    <input class="form-control disable" type="text" value="" id="lpcase">
                                  </div>
                                </div>
                                <div class="form-group row">
                                  <label for="example-text-input" class="col-md-5 col-form-label">Total </label>
                                  <div class="col-md-7">
                                    <input class="form-control disable" type="text" value="" id="settlementTotal" style="font-weight: bold">
                                  </div>
                                </div>
                    	</div>
                    	</div>
                    	</div>
                    	<div class="row">
                        <div class="col-md-6">
                        <div style="margin-bottom: 12px;font-size: 16px;"><b>GL Unrecon</b></div>
                          <div class="form-group row">
                              <label for="example-text-input" class="col-md-5 col-form-label">CBS Unrecon 1 </label>
                              <div class="col-md-7">
                                <input class="form-control disable" type="text" value="" id="cbs_unrecon">
                              </div>
                            </div>
                            <div class="form-group row">
                                <label for="example-text-input" class="col-md-5 col-form-label">Switch Unrecon 2 </label>
                                <div class="col-md-7">
                                  <input class="form-control disable" type="text" value="" id="switch_unrecon">
                                </div>
                              </div>
                              <div class="form-group row">
                                  <label for="example-text-input" class="col-md-5 col-form-label">Total </label>
                                  <div class="col-md-7">
                                    <input class="form-control disable" type="text" value="" id="nobase2" style="font-weight: bold">
                                  </div>
                                </div>
                                </div>
								
								<div class="col-md-6">
                                <div style="margin-bottom: 12px;font-size: 16px;"><b>Surcharge Entries</b></div>
								
                                <div style="height: 140px;overflow-y: scroll;overflow-x: hidden;">
                                    <div id="myformelement"></div>
                              <label for="example-text-input" class="col-md-5 col-form-label">Total </label>
                                  <div class="col-md-7">
                                    <input class="form-control disable" type="text" value="" id="surch_total" style="font-weight: bold">
                                  </div>
                                </div>
                                </div>
                                </div>
							<div class="row">
								<div class="col-md-6">
                                <div style="margin-bottom: 12px;font-size: 16px;"><b>Auto Reversal</b></div>

                                <div style="height: 150px;overflow-y: scroll;overflow-x: hidden;margin-bottom: 12px;">
                                                <div id="Autorev"></div>                              
                                            <div class="form-group row">
                                              <label for="example-text-input" class="col-md-5 col-form-label">Total </label>
                                              <div class="col-md-7">
                                                <input class="form-control disable" type="text" value="" id="autorev_total" style="font-weight: bold">
                                              </div>
                                            </div>

                                </div>
                                </div>
                                
                                <div class="col-md-6">
                                <div style="margin-bottom: 12px;font-size: 16px;"><b>LP Case(s)</b></div>
                                <div style="height: 150px;overflow-y: scroll;overflow-x: hidden;margin-bottom: 12px;">
                                 <div id="Lpcase"></div>
                               
                                            <div class="form-group row">
                                              <label for="example-text-input" class="col-md-5 col-form-label">Total </label>
                                              <div class="col-md-7">
                                                <input class="form-control disable" type="text" value="" id="lpcasetotal" style="font-weight: bold">
                                              </div>
                                            </div>
                                            </div>

                                </div>
                                </div>
                                </div>
                                <br/>
                                <div class="row">
                                <div class="col-md-6">
                                	<div class="form-group row">
                                              <label for="example-text-input" class="col-md-5 col-form-label">Final Total </label>
                                              <div class="col-md-7">
                                                <input class="form-control disable" type="text" value="" id="final_total" style="font-weight: bold">
                                              </div>
                                            </div>
                                </div>
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
              <a  class="btn btn-primary" onclick="SaveRec()">Process</a>
              <button type="submit" class="btn btn-danger">Cancel</button>
              <button type="button" class="btn btn-danger" onclick="location.reload();">Reset</button>
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

    <div align="center" id="Loader"
		style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

		<img style="margin-left: 20px; margin-top: 200px;" src="images/unnamed.gif" alt="loader">

	</div>
	
	
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