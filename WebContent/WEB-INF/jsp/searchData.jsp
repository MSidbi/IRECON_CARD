<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
  
    <header>
    
   
    <script type="text/javascript" src="js/searchdata.js"></script>
    
   <script type="text/javascript">
   $(document).ready(function() {
	    $('#searchdata').dataTable( {
            bSort: false,
            aoColumns: [ { sWidth: "45%" }, { sWidth: "45%" }, { sWidth: "10%", bSearchable: false, bSortable: false } ],
        "scrollY":        "200px",
        "scrollCollapse": true,
        //"info":           true,
        "paging":         true
    } );
	    });
   
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
                      <label for="exampleInputEmail1">Networks</label>
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
                      <label for="exampleInputEmail1">SUB-Category</label>
                      <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
					  <select class="form-control" id="stSubCategory" style="" placeholder="Select Your Favorite" data-search="true">
						<option value="Dispute">Issuer</option>
						<option value="opt2">option 2</option>
						<option value="opt3">option 3</option>
					  </select>
                    </div>
					</div>
					
					</div>
					<div class="row">
				  <div class="col-md-3">
                    <div class="form-group">
                      <label for="exampleInputPassword1">Card No.</label>
                      <input type="text" class="form-control" id="exampleInputPassword1" placeholder="">
                    </div>
					</div>
					<div class="col-md-3">
					<div class="form-group">
                      <label for="exampleInputPassword1">Date</label>
                      <input type="text" class="form-control" id="exampleInputPassword1" placeholder="">
                    </div>
					</div>
					<div class="col-md-3">
					<div class="form-group">
                      <label for="exampleInputPassword1">Amount</label>
                      <input type="text" class="form-control" id="exampleInputPassword1" placeholder="">
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
      
    
    <!--  -->
      </body>
     
      
      