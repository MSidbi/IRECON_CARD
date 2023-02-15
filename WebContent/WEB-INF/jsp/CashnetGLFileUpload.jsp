<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 <meta http-equiv="X-UA-Compatible" content="IE=10"> 
 <link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->
<%
response.setHeader("Cache-Control","no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
response.setHeader("Pragma","no-cache");
response.setHeader("X-Frame-Options","deny");
%>

<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/CashnetGLFileUpload.js"></script>





<script type="text/javascript">
$(document).ready(function() {
	
	//alert("click");
  
   /*  $("#datepicker").datepicker({dateFormat:"dd/mm/yy", maxDate:0});
    });
 */
 $("#datepicker").datepicker({dateFormat:"yy/mm/dd", maxDate:0});
});


</script>


<div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
           Upload GL File
            <!-- <small>Version 2.0</small> -->
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"> Home</a></li>
            <li class="active">Upload GL File</li>
          </ol>
        </section>

        <!-- Main content -->
        <section class="content">
          <div class="row">
            <!-- left column -->
            <div class="col-md-4"></div>
            <div class="col-md-4">
              <!-- general form elements -->
              <div class="box box-primary">
                <!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> --><!-- /.box-header -->
                <!-- form start -->
                 <form:form id="uploadform"  action="CashnetGLFileUpload.do" method="POST" commandName="CompareSetupBean"  enctype="multipart/form-data" > 
                  <div class="box-body">
                    <div class="form-group">
                      <label for="exampleInputEmail1">Category</label>
                      <!-- <input type="email" class="form-control" id="exampleInputEmail1" placeholder=""> -->
                      <%-- <input type="hidden" name="CSRFToken" id="CSRFToken" value ="${CSRFToken }">  --%>
                      <div class="form-control">
                      <input type="text" value="Acquirer" readonly>
                      </div>
                      <%-- <form:select class="form-control" path="filename" id="filename" onchange="setfilename(this);">
					<form:option value="0" >---Select---</form:option>
						<c:forEach var="configfilelist" items="${configBeanlist}">
							<form:option id="${configfilelist.stFileName}" value="${configfilelist.stFileName}" >${configfilelist.stFileName}</form:option>
							
							</c:forEach> --%>
							<%-- <form:option id="ctf" value="CTF" >CTF</form:option> --%>
						<%-- </form:select> --%> <!-- <img alt="" src="images/listbtn.png" title="Last Uploaded File" onclick="getfiledetails();" style="vertical-align:middle; height: 20px; width: 20px;"> --> 
						<input type="hidden" id="headerlist" value="">
						
						<%-- <form:hidden path="stFileName" id="stFileName"/> --%>
                      
                      
					   
                    </div>
                    
                    <div class="form-group">
                    <label for="exampleInputEmail1">Select Cycle</label>
                      <select class="form-control" id="cycle" style="" placeholder="Select Cycle">
						<option value="-">Select Cycle</option>
						<option value="1">1</option>
						<option value="2">2</option>
					  </select> 
                    </div>
                    
                    
                    <div class="form-group">
                      <label for="exampleInputPassword1">Date</label>
                      <form:input path="fileDate" class="form-control" readonly="readonly" name = "datepicker" id="datepicker"  placeholder="dd/mm/yyyy"/>
                    </div>
                    <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="file" id="dataFile1" title="Upload File" /></td>
                      <!-- <p class="help-block">Example block-level help text here.</p> -->
                    </div>
                    
                  </div><!-- /.box-body -->

                  <!-- <div class="box-footer">
                    <button type="button" value="UPLOAD" id="upload" onclick="return processFileUpload();" class="btn btn-primary">Upload</button>
                  </div> -->
                  <div class="box-footer">
                    <input type="submit"  onclick="validatedtls()" value ="Upload" class="btn btn-primary">
                  </div>
                  <div class="box-footer" style="display: none">
                    <input type="text" id="dummy" value="012">
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
