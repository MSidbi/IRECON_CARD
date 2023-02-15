<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<!-- <link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" /> -->
<!-- <link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/DownloadReports.js"></script>
	<!-- <script src="js/jquery.jtable.js" type="text/javascript"></script> -->
<!-- <script type="text/javascript" src="js/jquery.fancyform.js"></script> -->

<!-- <script type="text/javascript" src="js/jquery.ui.datepicker.js"></script> -->
<script type="text/javascript" src="js/commonScript.js"></script> 
  
<script type="text/javascript">
$(document).ready(function() {
	
	//alert("click");
  
    $("#datepicker").datepicker({dateFormat:"dd/mm/yy", maxDate:0});
    });



</script>
<!-- 
<script type="text/ecmascript" src="js/jquery-1.9.1.js"></script> -->

    <!-- This is the localization file of the grid controlling messages, labels, etc.
    <!-- We support more than 40 localizations -->

        
        
       
       
	
 

<!-- <script type="text/javascript" src="js/jquery-1.12.4.js"></script> -->

<title>Network Files Update</title>
</head>
<body>

 <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
            Network Files Update
            <!-- <small>Version 2.0</small> -->
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"> Home</a></li>
            <li class="active">Network Files Update</li>
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
                <form:form name="reportform" id="reportform" action="DownloadReports.do" method="POST" commandName="SettlementBean" >
                <form role="form">
              
                  <div class="box-body">
                    <div class="form-group">
                      <label for="exampleInputEmail1">SubCategory</label>
                      <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
					 <form:select path="stsubCategory" class="form-control" id="stsubCategory" style="" placeholder="Select Your Favorite" data-search="true">
						<option value="-">--Select --</option>
						<c:forEach var="subcat" items="${subcategory}" >
                      <form:option  value="${subcat}">${subcat}</form:option>
                      </c:forEach>
					  </form:select>
					  <input type="text" id="rectyp" value="${category}" style="display: none">
					  <form:input path="category" id="category" name="category" value="${category}" style="display: none"/> 
                    </div>
                    <div class="form-group">
                      <label for="exampleInputPassword1">Date</label>
                      <form:input path="datepicker" readonly="readonly" id="datepicker"  placeholder="dd/mm/yyyy" class="form-control" />
                      <form:input type ="hidden" path = "stPath" value = "D:\Reports" name = "path" id="path" readonly = "true"/>
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
                    <button type="button" onclick = "Process();" class="btn btn-primary">Process</button>
                  </div>
                  
                </form>
                </form:form>
              </div><!-- /.box -->

              

            </div><!--/.col (left) -->
           
          </div>   <!-- /.row -->
        </section>
      </div><!-- /.content-wrapper -->
	
</body>
</html>