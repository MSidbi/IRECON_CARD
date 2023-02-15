<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
  
    <header>
    
   <link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />

<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>
    <script type="text/javascript" src="js/searchdata.js"></script>
    
   <script type="text/javascript">
   $(document).ready(function() {
	    $('#searchdata').dataTable( {
            bSort: false,
            aoColumns: [ { sWidth: "45%" }, { sWidth: "45%" }, { sWidth: "10%", bSearchable: false, bSortable: false } ],
        "scrollY":        "200px",
        "scrollCollapse": true,
        //"info":           true,
        "paging":         true,
         columnDefs: [ {
            orderable: false,
            className: 'select-checkbox',
            targets:   0
        } ],
        select: {
            style:    'os',
            selector: 'td:first-child'
        },
        order: [[ 1, 'asc' ]]
    } );
	    
	    
	    	
	    	//alert("click");
	      
	        $("#filedate").datepicker({dateFormat:"dd/mm/yy", maxDate:0});
	        $("#tran_date").datepicker({dateFormat:"dd/mm/yy", maxDate:0});
	       
	    
	    });
   
   function openTtumModal() {
	   $("#ttumStatusModal").modal('show');
   }
   
   </script>
  
    </header>
    <body>
    
<!-- Right side column. Contains the navbar and content of the page -->
      <div class="content-wrapper">

        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
            Global Search
            <!-- <small>Version 2.0</small> -->
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"> Home</a></li>
            <li class="active">Global Search</li>
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
                  <h3 class="box-title">Cashnet</h3>
                </div> --><!-- /.box-header -->
                <!-- form start -->
                <form role="form">
                  <div class="box-body">
				  <div class="row">
				  <div class="col-md-3">
                    <div class="form-group">
                      <label for="exampleInputEmail1">Networks</label><label style="color: red">*</label>
                      <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
					  <select class="form-control" id="category" style="" placeholder="Select Your Favorite" data-search="true" onchange="getSubCategory(this)">
						<option value="">--SELECT--</option>
						<option value="ONUS">ONUS</option>
						<option value="RUPAY">RUPAY</option> 
						<option value="AMEX">AMEX</option> 
						<option value="VISA">VISA</option>
						<option value="NFS">NFS</option>
						<option value="CASHNET">CASHNET</option>
						<option value="MASTERCARD">MASTERCARD</option>
						<option value="CARDTOCARD">CARD TO CARD</option>
						<option value="POS">ONUS POS</option>
					  </select>
                    </div>
					</div>
					<div class="col-md-3" id="trsubcat">
                    <div class="form-group">
                      <label for="exampleInputEmail1">SUB-Category</label><label style="color: red">*</label>
                      <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
					  <select class="form-control" id="stSubCategory" style="" placeholder="Select Your Favorite" data-search="true" onchange="getFilesdata()">
						<option value="SELECT">SELECT</option>
						
					  </select>
                    </div>
                 
					</div>
					<div class="col-md-3" id="">
					   <div class="form-group">
                      <label for="exampleInputEmail1">File</label><label style="color: red">*</label>
                      <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
					  <select class="form-control" id="stSelectedFile" style="" placeholder="Select Your Favorite" data-search="true" onchange="getFilestype();">
						<option value="SELECT">SELECT</option>
					  </select>
                    </div>
                    </div>
                    <div class="col-md-3" id="">
					   <div class="form-group">
                      <label for="exampleInputEmail1">Remarks</label>
                      <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
					  <select class="form-control" id="dcrs_remarks" style="" placeholder="Select Your Favorite" data-search="true">
						<option value="SELECT">SELECT</option>
					  </select>
                    </div>
                    </div>
					
					</div>
					<div class="row">
				  <div class="col-md-3">
                    <div class="form-group">
                      <label for="exampleInputPassword1">Card No.</label><label style="color: red">*</label>
                      <input type="text" class="form-control" id="card_No" placeholder="" maxlength="16" onkeypress="setValueType(this,'numeric')">
                    </div>
					</div>
					<div class="col-md-3">
					<div class="form-group">
                      <label for="exampleInputPassword1">Transaction Date</label><label style="color: red">*</label>
                      <input type="text" class="form-control" id="tran_date" placeholder="" maxlength="8" onkeypress="setValueType(this,'date')">
                    </div>
					</div>
					<div class="col-md-3">
					<div class="form-group">
                      <label for="exampleInputPassword1">Amount</label>
                      <input type="text" class="form-control" id="amount" maxlength="10" placeholder="" onkeypress="setValueType(this,'numeric')">
                    </div>
					</div>
					<div class="col-md-3">
					<div class="form-group">
                      <label for="exampleInputPassword1">FileDate</label>
                      <input type="text" class="form-control" id="filedate" placeholder="">
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
                  </div><!-- /.box-body -->

                  <div class="box-footer">
                    <button type="button" class="btn btn-primary" onclick="getsearchdata()">Search</button>
					<button type="button" class="btn btn-danger">Cancel</button>
                  </div>
                  </form>
                
              </div><!-- /.box -->

			  <div class="box box-primary">
				<div class="box-body">
					 <div class="box-header">
                  <h3 class="box-title">Search Result</h3>
                </div> <!-- /.box-header -->
                <div style="margin-bottom: 10px;"><a class="btn btn-sm btn-primary" onclick="return getTTUMdetails();">TTUTM STatus</a></div><!--  -->
				<!-- form start -->
                <form role="form">
                	<table id="searchdata" style="display: none;" class="table table-bordered searchtable" rule='box'> 
                	<!-- <tbody class="table-responsive"   >
                	
                	
                	
                	</tbody> -->
                	
                	</table>
						
				</form>
				</div>
			  </div>

			 

            <!-- </div> --><!--/.col (left) -->
           
          <!-- </div> -->   <!-- /.row -->
        </section>
      </div><!-- /.content-wrapper -->
      
     <!-- Modal -->
  <div class="modal fade" id="ttumStatusModal" role="dialog">
    <div class="modal-dialog modal-sm">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">TTUM Details</h4>
        </div>
        <div class="modal-body">
          <p> TTUM Id : <span id="ttumID"></span></p>
        </div>
        <!-- <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        </div> -->
      </div>
    </div>
  </div>
</div>
<div align="center" id="Loader"
		style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

		<img style="margin-left: 20px; margin-top: 200px;" src="images/unnamed.gif" alt="loader">

	</div>

    
    <!--  -->
      </body>
     
      
      