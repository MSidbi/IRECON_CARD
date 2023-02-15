 <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
         <link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
    
     <script type="text/javascript" src="js/chargeBack.js"></script>
    
    <script type="text/javascript">
$(document).ready(function() {
	debugger;
	//$('#dollar_field').hide();
  
    $("#datepicker").datepicker({dateFormat:"dd-M-yy", maxDate:0});
    });
    

window.history.forward();
function noBack() { window.history.forward(); }


</script>


<div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
          Charge Back Report
            <!-- <small>Version 2.0</small> -->
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"> Home</a></li>
            <li class="active">Report</li>
          </ol>
        </section>

        <!-- Main content -->
        <section class="content">
        
        <div class="row">
        <div class="col-md-6">
       <!--  <div class="box box-primary">
        <span id="message"></span>
        </div> -->
        </div>
        </div>
        
          <div class="row">
            <!-- left column -->
            <div class="col-md-6">
              <!-- general form elements -->
              <div class="box box-primary">
                <!-- form start -->
                <form:form name="form" id="form" action="reportChargeBack.do" method="POST" >
                  <div class="box-body">
                  
                  <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Sub Category</label> <input
								type="text" id="rectyp" value="${category}"
								style="display: none"> <select class="form-control"
								name="subCat" id="stSubCategory">
								<option value="-">--Select --</option>
								<c:forEach var="subcat" items="${subcategory}">
									<option value="${subcat}">${subcat}</option>
								</c:forEach>
							</select>
						</div>
                    
                    <div class="form-group">
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="filedate" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
<!-- 					 <img alt="" src="images/listbtn.png" title="Last Uploaded File" onclick="getupldfiledetails();" style="vertical-align:middle; height: 20px; width: 20px;"> 
 -->

						</div>
						
                 
                  </div> 

                  <div class="box-footer">
                    <button type="submit" value="Submit" id="submit" class="btn btn-primary">Generate Report</button>
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
	
	