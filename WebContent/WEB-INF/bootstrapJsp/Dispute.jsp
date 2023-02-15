<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
  
    <header>
 <link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />


<!-- <script src="js/jquery-1.9.1.js" type="text/javascript"></script> -->
<script src="js/jquery-ui.js" type="text/javascript"></script>
<!-- <script src="js1/jtable.2.3.0/jquery.jtable.js" type="text/javascript"></script> -->
<!-- <script src="js/jquery-1.8.3.js" type="text/javascript"></script> -->
<script src="js/jquery-ui-1.10.3.custom.js" type="text/javascript"></script>
<script src="js/jquery.jtable.js" type="text/javascript"></script>

    <script type="text/javascript" src="js/jquery-ui.min.js"></script>
    <!-- <script type="text/javascript" src="js/dispute.js"></script> -->
    <script type="text/javascript" src="js/Dis.js"></script>
    
    



		
   <script type="text/javascript">
    $(document).ready(function() {   	
    	
    	
    	   
    	 $("#filedate").datepicker({dateFormat:"dd/mm/yy", maxDate:0});
    	 
    /* 		$('#DisputeTableContainer').jtable({
    			title : 'Search',
    			paging : true, // Enable paging
    			pageSize : 10, // Set page size
    			sorting : true,
    			selecting : true,
    			selectingCheckboxes : true,
    			actions : {
    				listAction : 'SearchDispute1.do',
    				//updateAction : 'updateProject.do'

    			},
    			fields : {
    				/* category : {
    					title : 'CATEGORY',
    					width : '25%',
    					key : true,
    					list : true,
    					edit : true
    				}, 
    				tran_date : {
    					title : 'TRAN DATE',
    					width : '20%',
    					edit : true
    				},
    				amount : {
    					title : 'AMOUNT',
    					width : '30%',
    					edit : true

    				},
    				card_No : {
    					title : 'CARD NO',
    					width : '10%',
    					edit : true

    				},
    				filedate : {
    					title : 'FILEDATE',
    					width : '30%',
    					edit : true
    				}    				
    				
    			}
    		});

    		$('#view').click(function(e) {
    			alert("in view");
    			e.preventDefault();
    			
    			$('#DisputeTableContainer').jtable('load', {
    				category : $('#category').val() != "" ? $('#category').val() : 0,
    				stSubCategory : $('#stSubCategory').val() != null ? $('#stSubCategory').val() : 0,
    				file_name : $('#file_name').val() != null ? $('#file_name').val() : 0,
    				//dcrs_remarks : $('#dcrs_remarks').val() != null ? $('#dcrs_remarks').val() : 0,
    				filedate : $('#filedate').val() != null ? $('#filedate').val() : 0

    			});
    		});
    		$('#view').click();
 
    	 
	     $('#searchdata').dataTable( {
            bSort: false,
             aoColumns: [ { sWidth: "45%" }, { sWidth: "45%" }, { sWidth: "10%", bSearchable: false, bSortable: false } ],
        "scrollY":        "200px",
        "scrollCollapse": true,
        "info":           true,
        "paging":         true
        /* fixedColumns:   {
            leftColumns: 2
        },
 
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

    } ); */

} ); 

/* $('#searchdata').DataTable({
    columnDefs: [{
      orderable: false,
      className: 'select-checkbox',
      targets: 0
    }, {
      "targets": [2],
      "visible": false,
      "searchable": false
    }]
})
    */
   
  /*  $(document).ready(function (){
	   var table = $('#searchdata').DataTable({
	      'ajax': {
	         'url': '/lab/articles/jquery-datatables-how-to-add-a-checkbox-column/ids-arrays.txt'
	      },
	      'columnDefs': [{
	         'targets': 0,
	         'searchable': false,
	         'orderable': false,
	         'className': 'dt-body-center',
	         'render': function (data, type, full, meta){
	             return '<input type="checkbox" name="id[]" value="' + $('<div/>').text(data).html() + '">';
	         }
	      }],
	      'order': [[1, 'asc']]
	   });

	   // Handle click on "Select all" control
	   $('#example-select-all').on('click', function(){
	      // Get all rows with search applied
	      var rows = table.rows({ 'search': 'applied' }).nodes();
	      // Check/uncheck checkboxes for all rows in the table
	      $('input[type="checkbox"]', rows).prop('checked', this.checked);
	   });

	   // Handle click on checkbox to set state of "Select all" control
	   $('#example tbody').on('change', 'input[type="checkbox"]', function(){
	      // If checkbox is not checked
	      if(!this.checked){
	         var el = $('#example-select-all').get(0);
	         // If "Select all" control is checked and has 'indeterminate' property
	         if(el && el.checked && ('indeterminate' in el)){
	            // Set visual state of "Select all" control
	            // as 'indeterminate'
	            el.indeterminate = true;
	         }
	      }
	   });

	   // Handle form submission event
	   $('#frm-example').on('submit', function(e){
	      var form = this;

	      // Iterate over all checkboxes in the table
	      table.$('input[type="checkbox"]').each(function(){
	         // If checkbox doesn't exist in DOM
	         if(!$.contains(document, this)){
	            // If checkbox is checked
	            if(this.checked){
	               // Create a hidden element
	               $(form).append(
	                  $('<input>')
	                     .attr('type', 'hidden')
	                     .attr('name', this.name)
	                     .val(this.value)
	               );
	            }
	         }
	      });
	   });

	});
    */
   
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
						<option value="WCC">WCC</option>
					  </select>
                    </div>
					</div>
					<div class="col-md-3" id="trsubcat">
                    <div class="form-group">
                      <label for="exampleInputEmail1">SUB-Category</label>
                      <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
					  <select class="form-control" id="stSubCategory" onchange="getFiles(this)" style="" placeholder="Select Your Favorite" data-search="true">
						<!-- <option value="Dispute">Issuer</option>
						<option value="opt2">option 2</option>
						<option value="opt3">option 3</option> -->
					  </select>
                    </div>
					</div>
					
					
					
					 <div class="col-md-3">
                    <div class="form-group">
                      <label for="dcrs_remarks">File Name</label>
                       <select class="form-control" id="file_name" style="" placeholder="Select Your Favorite" data-search="true"></select>
                      <!--  <select class="form-control" id="file_name" style="" placeholder="Select Your Favorite" data-search="true"  >
						<option value="SWITCH">Switch</option>
						<option value="CBS">CBS</option>
						<option value="NETWORK">Network</option>
					  </select> -->
                    </div>
					</div>
					
				<!--   <div class="col-md-3">
                    <div class="form-group">
                      <label for="dcrs_remarks">DCRS Remarks</label>
                       <select class="form-control" id="dcrs_remarks" style="" placeholder="Select Your Favorite" data-search="true">
                       <select class="form-control" id="dcrs_remarks" style="" placeholder="Select Your Favorite" data-search="true">
						<option value="UNRECON-1">Unrecon 1</option>
						<option value="UNRECON-2">Unrecon 2</option>
						<option value="UNRECON-3">Unrecon 3</option>
					  </select>
                    </div>
					</div> -->
					
					  <div class="col-md-3">
                    <div class="form-group">
                      <label for="Filedate">Filedate</label>
                      <input type="text" class="form-control" id="filedate"  readonly="readonly" placeholder="dd/mm/yyyy"/>
                    </div>
					</div>
					
					</div>
					
				<!-- 	<div class="row">
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
					</div>		 -->				
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
                    <button type="button" class="btn btn-primary" onclick="getsearchdata1()">Search</button>
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
				
				
				<div style="overflow-x: auto;padding: 0;box-shadow: 1px 1px 8px -2px #333;">
				
	<div id="DisputeTableContainer" style="width: 1800px"></div>
	
	</div>
               
               <div id = "dispute" style="display: none;text-align: center;margin-top: 10px;">
                 <form role="form">
                
						 <button type="button" class="btn btn-primary" id="ForceMatch">Force Match</button>
				<!-- 	<button type="button" class="btn btn-primary" onclick="generateTTUM()">Generate TTUM</button> -->
				</form> </div>
				</div>
			  </div>

			 

            <!-- </div> --><!--/.col (left) -->
           
          <!-- </div> -->   <!-- /.row -->
        </section>
      </div><!-- /.content-wrapper -->
      
        	<div align="center" id="Loader"
		style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

		<img style="margin-left: 20px; margin-top: 200px;" src="images/unnamed.gif" alt="loader">

	</div>
      
    
    <!--  -->
      </body>
     
      
      