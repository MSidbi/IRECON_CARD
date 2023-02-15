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
  
	$("#stSubCategory").on('change',function(){
		if($(this).val()=="ACQUIRER"){
			$("#acq").show();
		}else{
			$("#acq").hide();
		}
		
		
	});
	
    $("#datepicker").datepicker({dateFormat:"dd-M-yy", maxDate:0});
    });
    

window.history.forward();
function noBack() { window.history.forward(); }


</script>


<div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
          Raise Charge Back
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
        <div class="col-md-6">
        <!-- <div class="box box-primary">
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
                <form:form name="form" id="form" action="raiseChargeBack.do" method="POST" commandName="cbbean"   enctype="multipart/form-data">
                  <div class="box-body">
                  
                  <div class="form-group" style="display:${display}">
							<label for="exampleInputEmail1" >Sub Category</label> <input
								type="text" id="rectyp" value="${category}"
								style="display: none"> <select class="form-control"
								name="subCat" id="stSubCategory">
								<option value="-">--Select --</option>
								<%-- <c:forEach var="subcat" items="${subcategory}">
									<option value="${subcat}">${subcat}</option>
								</c:forEach> --%>
								<option value="ISSUER">ISSUER</option>
								<option value="ACQUIRER">ACQUIRER</option>
							</select>
							<input type="hidden" name="CSRFToken" id="CSRFToken" value ="${CSRFToken }"> 
						</div>
                    
                    <div class="form-group" style="display:none" id="acq">
				
						 <p style="color: red">You are uploading <b>ACQUIRER</b> report only for reporting purpose in issuer chargeback<p>  
						
						</div>
                    
                    <div class="form-group">
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="filedate" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" onchange="getupldfiledetails();"/>
<!-- 					 <img alt="" src="images/listbtn.png" title="Last Uploaded File" onclick="getupldfiledetails();" style="vertical-align:middle; height: 20px; width: 20px;"> 
 -->

						</div>
						
						  <div class="form-group">
						<!--    <input type="radio" name="fileType" value="DCMS" id="fileType"/>
                      <label for="exampleInputFile">DCMS</label>
                     
                     <input type="radio" name="fileType" value="CB" id="fileType"/>
                       <label for="exampleInputFile">Chargeback</label>
                      
                       <input type="radio" name="fileType" value="PA" id="fileType"/>
                       <label for="exampleInputFile">Pre-Arbitration</label> -->
                     
                      <form:radiobutton path = "fileType" value = "DCMS" label = "DCMS" />
                  <form:radiobutton path = "fileType" value = "CB" label = "Chargeback" />
                   <form:radiobutton path = "fileType" value = "PA" label = "Pre-Arbitration" />
                    </div>
                    
                    <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="attachment" id="attachment" title="Upload File" />
                    </div>
                  <!--   
                     <div class="form-group">
                      <label for="exampleInputFile">ChargeBack File Upload</label>
                      <input type="file" name="attachment" id="attachment" title="Upload File" />
                    </div>
                     -->
                     <span id="message"></span>
                     <div id="fileDtl">
                      <!-- <span id="message1"></span>
 -->                     </div>
                  </div> 

                  <div class="box-footer">
                    <button type="button" value="UPLOAD" id="upload" onclick="Upload();" class="btn btn-primary">Upload</button>
                  </div>
                </form:form>
              </div> 
            </div> 
          </div> 
         <!--  <div class="row">
          <div class="col-md-6">
				<td colspan="2"> <a href="chargebackxlstemplate.do" >Click Here</a> To Download Template </td>
          </div>
          </div>   --> 
       <%--    <c:if test="${error_msg != null }">
			<div align="center"  class="errorMsg"><c:out value="${error_msg}" escapeXml="false" /></div>
		</c:if>
		<c:if test="${success_msg != null}">
			<div align="center"  class="successMsg"><c:out value="${success_msg}" escapeXml="false" /></div>
		</c:if> --%>
        </section>
      </div> 
      
      	<div align="center" id="Loader"
		style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">
		<img style="margin-left: 20px; margin-top: 200px;" src="images/unnamed.gif" alt="loader">
	</div>
	
<!-- 	<script type="text/javascript">
	function Upload()
	{
	 
		var $file = $('#attachment');
		 
		for(var inx =0; inx < $file[0].files.length ; inx++){
			if($file[0].files[inx].name.indexOf('.xls') == -1 ){
				return false;
			}
		}
		
		ValidateData();
		
			 //$('#message').text('please wait your file is being downloading...');
		$('#form').submit();
		
	}
	</script> -->