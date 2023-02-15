<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
     <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
       <link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
    <header>
    
   
    <script type="text/javascript" src="js/searchChargeback.js"></script>
    
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
	  
   
   
   $("#tran_date").datepicker({dateFormat:"dd-M-yy", maxDate:0});
});


window.history.forward();
function noBack() { window.history.forward(); }

   </script>
  
    </header>
    <body>
    
<!-- Right side column. Contains the navbar and content of the page -->
      <div class="content-wrapper">

        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
            ChargeBack Search
            <!-- <small>Version 2.0</small> -->
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"> Home</a></li>
            <li class="active">ChargeBack Search</li>
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
                      <input
								type="text" id="rectyp" value="${category}"
								style="display: none"> <select class="form-control"
								name="subCat" id="stSubCategory">
								<option value="-">--Select --</option>
								<c:forEach var="subcat" items="${subcategory}">
									<option value="${subcat}">${subcat}</option>
								</c:forEach>
							</select>
                    </div>
					</div>
					
					</div>
					<div class="row">
				  <div class="col-md-3">
                    <div class="form-group">
                      <label for="exampleInputPassword1">Card No.</label>
                      <input type="text" class="form-control" name="card_number" id="card_number" placeholder="">
                    </div>
					</div>
					<div class="col-md-3">
					<div class="form-group">
                      <label for="exampleInputPassword1">RRN</label>
                      <input type="text" class="form-control" name="rrn" id="rrn" placeholder="">
                    </div>
					</div>
					<div class="col-md-3">
					<div class="form-group">
                      <label for="exampleInputPassword1">Tran Date</label>
                      <input type="text" class="form-control" name="tran_date" readonly="readonly"
								id="tran_date" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" placeholder="">
                    </div>
					</div>
					</div>						
                   
                  </div>

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

        </section>
      </div><!-- /.content-wrapper -->
      
    
    <!--  -->
      </body>
     
      
      