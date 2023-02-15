  <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
         XML TO EXCEL Converter
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
                <!-- form start -->
                <form:form name="form" id="form" action="rupayofflinexml2xl.do" method="POST"   enctype="multipart/form-data">
                  <div class="box-body">
                    
                    <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="attachment" id="attachment" title="Upload File" /></td>
                    </div>
                  </div> 

                  <div class="box-footer">
                    <button type="button" value="UPLOAD" id="upload" onclick="Upload();" class="btn btn-primary">Upload</button>
                  </div>
                </form:form>
              </div> 
            </div> 
          </div> 
        </section>
      </div> 
      
      	<div align="center" id="Loader"
		style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">
		<img style="margin-left: 20px; margin-top: 200px;" src="images/unnamed.gif" alt="loader">
	</div>
	
	<script type="text/javascript">
	function Upload()
	{
	 
		var $file = $('#attachment');
		 
		for(var inx =0; inx < $file[0].files.length ; inx++){
			if($file[0].files[inx].name.indexOf('.xml') == -1 ){
				return false;
			}
		}
			//alert("sub categroy is "+stsubcat);
		$('#form').submit();
		
	}
	</script>