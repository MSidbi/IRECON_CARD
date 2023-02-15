<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<!-- <link href="css/fancyform.css" type="text/css" rel="stylesheet" /> -->


<%
response.setHeader("Cache-Control","no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
response.setHeader("Pragma","no-cache");
response.setHeader("X-Frame-Options","deny");
%>
<link href="css/jquery-ui.min.css" media="all" rel="stylesheet" type="text/css" />
<!--<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<!-- <link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" />
<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" />


<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script> -->
<script type="text/javascript">
$(document).ready(function() {
	
    $("#stStart_Date").datepicker({dateFormat:"dd/mm/yy", maxDate:0});
    $("#stEnd_Date").datepicker({dateFormat:"dd/mm/yy", maxDate:0});
    $("#stDate").datepicker({dateFormat:"dd/mm/yy", maxDate:0});
    });
    
 
window.history.forward();
function noBack() { window.history.forward(); }


</script> 



<script type="text/javascript" src="js/Generate.TTUM.js"></script>
<div class="content-wrapper">

        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
            Generate TTUM
            <!-- <small>Version 2.0</small> -->
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"> Home</a></li>
            <li class="active">Generate TTUM</li>
          </ol>
        </section>

        <!-- Main content -->
        <section class="content">

<form:form name="form" action="GenerateTTUM.do" method="POST" commandName="generateTTUMBean">


  			 <div class="box box-primary">
                <!-- form start -->
                <form role="form">
                  <div class="box-body">
				  <div class="row">
				  
				  <div class="col-md-6"style="display: ${display}">
                    <div class="form-group">
                      <label for="exampleInputEmail1">SubCategory</label>
					<form:select class="form-control" path="stSubCategory" id="stSubCategory" onchange="getFilesdata()" >
					<form:option value="-">--Select --</form:option>
					 <c:forEach var="subcat" items="${subcategory}" >
                      <form:option  value="${subcat}">${subcat}</form:option>
                      </c:forEach>
					</form:select>
					<form:input path="stCategory" id="stCategory" value="${category}" style="display:none;" />
				</div>
					</div>
					<div class="col-md-6">
					<div class="form-group">
                      <label for="exampleInputEmail1">File</label>
				<form:select class="form-control" path="stSelectedFile" id="stSelectedFile" onchange="getRespCode()">
					<form:option value="">- S E L E C T</form:option>
					<c:forEach var="stfile" items="${files}" >
                      <form:option  value="${stfile}">${stfile}</form:option>
                      </c:forEach>
					</form:select>
					</div>
					</div>
					</div>
					
					
				
	<!-- <input type="button" id="respbutton" value="Get Responsecode" onclick="getRespCode();">  -->
		
		
		<div class="row">
					  <div class="col-md-6">
						<div class="form-group">
	                      <label for="exampleInputEmail1">Cycle</label>
					<form:select class="form-control" path="inRec_Set_Id" onchange="surchrgChng()">
						<form:option value = "0">- S E L E C T -</form:option>
						<%-- <form:option value="SWITCH">SWITCH</form:option>
						<form:option value="CBS">CBS</form:option> --%>
						
						<form:option value="1">UNMATCHED- CYCLE 1</form:option>
						<form:option value="2">UNMATCHED- CYCLE 2</form:option>
						<form:option value="3">UNMATCHED- CYCLE 3</form:option>
						<form:option value="-1">SURCHARGE-MATCHED RECORDS</form:option>
						<form:option value="4">MATCHED - INTERNATIONAL</form:option>
						<form:option value="5">SUCCESS</form:option>
						<form:option value="6">FAILED</form:option>
						<form:option value="7">MATCHED</form:option>
						<form:option value="8">UNMATCHED</form:option>
						
						
						</form:select>
					 </div>
						</div>
				
				
	
	<div class="col-md-6" id="trrespcode" style="display: none">
					<div class="form-group">
                      <label for="exampleInputEmail1">Response Code</label>
		
				<form:select class="form-control" path="respcode" id="respcode" multiple="multiple">
				</form:select>
				</div>
					</div>
					</div>



					<div class="row">
						<div class="col-md-6" id="startDate">
							<div class="form-group">
								<label for="exampleInputPassword1">Start File Date</label>
								<form:input class="form-control" path="stStart_Date" id="stStart_Date"
									 maxlength="10" />
							</div>
						</div>


						<div class="col-md-6" id="enddate">
							<div class="form-group">
								<label for="exampleInputPassword1">End File Date</label>
								<form:input class="form-control" path="stEnd_Date" id="stEnd_Date"
									 maxlength="10" />

							</div>
						</div>
					</div>

					<!-- For fixed date -->

		<div class="row">
				  <div class="col-md-6" id ="Date"  style="display: none">
                    <div class="form-group">
	 <label for="exampleInputPassword1">File Date</label>
				<form:input class="form-control" path="stDate" id="stDate"  maxlength="10" />
				</div>
				 </div>
				
			
	
			<div class="col-md-6" id="tr_rupay_function_code" style="display: none" >
					
					<div class="form-group">
					<label for="exampleInputPassword1">Function Code</label>

				<form:select class="form-control" path="stFunctionCode" id="stFunctionCode">
					<form:option value="-">- S E L E C T</form:option>
					<form:option value="200">200</form:option>
					<form:option value="262">262</form:option>
					
				</form:select>
				</div>
				</div>
				
		
					<div class="col-md-6" id="tr_visa_function_code" style="display: none" >
					
					<div class="form-group">
					<label for="exampleInputPassword1">Function Code</label>

				<form:select class="form-control" path="visaFunctionCode" id="visaFunctionCode">
					<form:option value="-">- S E L E C T</form:option>
					<form:option value="05">05</form:option>
					<form:option value="07">07</form:option>
					<form:option value="06">06</form:option>
					<form:option value="00">00</form:option>
					<form:option value="25">25</form:option>
					<form:option value="26">26</form:option>
					
				</form:select>
				
		</div>
		
		
		</div>
		<div class="col-md-6">
		<div class="form-group">
					<label for="exampleInputPassword1">DIFF_AMOUNT</label>

				<form:select class="form-control" path="DIFF_AMOUNT" id="visaFunctionCode">
					<form:option value="-">- S E L E C T</form:option>
					<form:option value="1">Positive</form:option>
					<form:option value="-1">Negetive</form:option>
					
					
				</form:select>
				
		</div>
		</div>
		
		</div>

		 <div class="box-footer">
					 <input type="submit" class="btn btn-primary" name="Process" id="Process" value="Process" onclick="return process();"> 
					 <input type="button" class="btn btn-danger" name="cancel" id="cancel" value="Cancel">
					 
			    </div>
			    </div>
                </form>
              </div>
		
 
</form:form>
</section>
</div>

<!-- <script>
function getFilesdata() {
	
	debugger;
	alert("click");
	var category = document.getElementById("stCategorynew").value;
	var subcat = "-";
	
	if(category!="") { 
	$.ajax({
		 
		 type:'POST',
		 url :'getFiledetails.do',
		 data:{category:category,subcat:subcat},
		 success:function(response){
			 
			
			// response = $.parseJSON(response);
			 var length =response.Records.length;
			
			 var compareFile1 = document.getElementById("stSelectedFile");
			// var compareFile2 = document.getElementById("Man_file");
			 
			 var rowcount = compareFile1.childNodes.length;
				
				for(var j =1;j<=rowcount;j++ )
				{
					compareFile1.removeChild(compareFile1.lastChild);
					//compareFile2.removeChild(compareFile2.lastChild);
				}
			
			 var option1= document.createElement("option");
			 option1.value="0";
			 option1.text="--Select--";
			 var opt1= document.createElement("option");
			 opt1.value="0";
			 opt1.text="--Select--";
			 compareFile1.appendChild(option1);
			//compareFile2.appendChild(opt1)
			 
			 for(var i =0;i<length;i++ ) {
				
				 var option= document.createElement("option");
				  option.value = response.Records[i].stFileName;
				 option.text= response.Records[i].stFileName;
				 compareFile1.appendChild(option);
			 }
			/* for(var i =0;i<length;i++ ) {
					
				 var option= document.createElement("option")
				  option.value = response.Records[i].inFileId;
				 option.text= response.Records[i].stFileName;
					compareFile2.appendChild(option)
			 }*/
			
			 //document.getElementById("trbtn").style.display="none";
			// document.getElementById("stCategorynew").disabled="disabled";
			 //document.getElementById("SubCat").disabled="disabled";
			 displayContent();
			
			 

		 },error: function(){
			
			 alert("Error Occured");
			 
		 },
		 
	 });
	}else {
		
		
		alert("Please Select Category.");
		
	}
	
	
}
</script> -->

<script>
function validate()
{
	debugger;
	//alert("Inside validation");
	var file_name = document.getElementById("stSelectedFile").value;
	if(file_name=="RUPAY")
		{
	document.getElementById("tr_rupay_function_code").style.display='';
		}
	else if(file_name=="VISA") {
		
		
		document.getElementById("tr_visa_function_code").style.display='';
		
		
	}
	else{
		document.getElementById("tr_rupay_function_code").style.display='none';
		document.getElementById("tr_visa_function_code").style.display='none';
		document.getElementById("stFunctionCode").value='-';
	}
	}

</script>
