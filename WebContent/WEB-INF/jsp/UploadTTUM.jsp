<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<meta http-equiv="X-UA-Compatible" content="IE=Edge">
 <link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/Upload.TTUM.js"></script>





<script type="text/javascript">
$(document).ready(function() {
	
	//alert("click");
  
    $("#datepicker").datepicker({dateFormat:"dd/mm/yy", maxDate:0});
    });



</script>


<div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
           File Upload
            <!-- <small>Version 2.0</small> -->
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"> Home</a></li>
            <li class="active">File Upload</li>
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
                </div> --><!-- /.box-header -->
                <!-- form start -->
                <form:form name="form" action="UploadTTUM.do" method="POST" commandName="UploadTTUMBean" enctype="multipart/form-data">
                  <div class="box-body">
                    
                    <div class="form-group" id="trsubcat">
                      <label for="exampleInputEmail1">SubCategory</label>
                      <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
                               <form:select class="form-control" path="stSubCategory" id="stSubCategory">
							 <form:option  value="-">--Select --</form:option>
		                      <c:forEach var="subcat" items="${subcategory}" >
		                      <form:option  value="${subcat}">${subcat}</form:option>
		                      </c:forEach>
		                      </form:select>
		                      <form:input type="text" path="stCategory" id="stCategory" value="${category}" style="display: none"/>
		                      
		                      
                    </div>
                    
                    <div class="form-group" id="select1">
                      <label for="exampleInputEmail1">File</label>
                      <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
                      <form:select class="form-control" path="stSelectedFile" id="stSelectedFile">
							<form:option value="">- S E L E C T</form:option>
							<form:option value="dispute">Dispute</form:option>
							
						</form:select>
                      
					
                    </div>
                     
                    <div class="form-group" id="select2">
                      <label for="exampleInputEmail1">File</label>
                      <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
                      <form:select class="form-control" path="stSelectedFile1" id="stSelectedFile">
							<form:option value="">- S E L E C T</form:option>
							<form:option value="SUCCESS">SUCCESS</form:option>
							<form:option value="FAIL">FAIL</form:option>
							
						</form:select>
                      
					
                    </div>
                    
                    
                    <div class="form-group" id ="Date">
                      <label for="exampleInputPassword1">Date</label>
                      <form:input path="stDate" class="form-control" id="stDate" cssClass="date-short" maxlength="10" placeholder="dd/mm/yyyy" />
                    </div>
                    
                    <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="FileUpload" id="FileUpload" title="Upload File" /></td>
                      <!-- <p class="help-block">Example block-level help text here.</p> -->
                    </div>
                    <!-- <div class="checkbox">
                      <label>
                        <input type="checkbox"> Check me out
                      </label>
                    </div> -->
                  </div><!-- /.box-body -->

                  <div class="box-footer">
                    <button type="button" value="UPLOAD" id="upload" onclick="Upload();" class="btn btn-primary">Upload</button>
                  </div>
                </form:form>
              </div><!-- /.box -->

              

            </div><!--/.col (left) -->
           
          </div>   <!-- /.row -->
        </section>
      </div><!-- /.content-wrapper -->
      
      	<div align="center" id="Loader"
		style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

		<img style="margin-left: 20px; margin-top: 200px;" src="images/unnamed.gif" alt="loader">

	</div>
<script>
$(document).ready(function() {
	debugger
		var category=$('#stCategory').val();
		if(category=="CARDTOCARD")
			{
	    $('#select1').hide();
	    $('#select2').show();
			}
		else{
			 $('#select1').show();
			    $('#select2').hide();
			
		}

	});
</script>