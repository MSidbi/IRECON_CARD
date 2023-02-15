<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	
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

<!--  <script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<link href="css/jquery-ui1.css" media="all" rel="stylesheet" type="text/css" />
<link href="css/jquery.ui.datepicker.css" media="all" rel="stylesheet" type="text/css" />   -->

<script type="text/javascript">
$(document).ready(function() {
	//debugger;
	//$('#dollar_field').hide();
  
    /*  $("#datepicker").datepicker({dateFormat:"dd-M-yy", maxDate:0}); */
    
     $("#datepicker").datepicker({dateFormat:"yy/mm/dd", maxDate:0});
    
     $("#monthpicker").datepicker({
    	 dateFormat:"MM yy", 
    		changeMonth: true,
    	changeYear: true,
    	showButtonPanel: true,
    	/* onClose: function(){
    		var iMonth = $('#ui-datpicker-div .ui-datepicker-month :selected').val();
    		var iYear = $('#ui-datepicker-div .ui-datepicker-year :selected').val();
    		$(this).datepicker('setDate',new Date(iYear,iMonth,1));
    	} */
    	onClose: function(dateText, inst){
        	$(this).datepicker('setDate',new Date(inst.selectedYear, inst.selectedMonth,1));
        }
    	//dateFormat:"dd-M-yy", maxDate:0
    	});
   

});

window.history.forward();
function noBack() { window.history.forward(); }


</script>
<div class="content-wrapper">
	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			Rupay Settlement File Upload
			<!-- <small>Version 2.0</small> -->
		</h1>
		<ol class="breadcrumb">
			
			<li class="active">Rupay Settlement File Upload</li>
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
                </div> -->
					<!-- /.box-header -->
					<!-- form start -->
					<%-- <form role="form"> --%>
<form:form id="uploadform"  action="getRupSettlttum.do" method="POST"  commandName="rupaySettlementBean" enctype="multipart/form-data" >

					<div class="box-body" id="subcat">
					
					
						 <div class="form-group" id ="subCate" >
							<label for="exampleInputEmail1">Sub Category</label> 
							
							<select class="form-control" name="stSubCategory" id="stSubCategory">
								<option value="0">--Select --</option>
								<option value="DOMESTIC">Domestic</option>
									<option value="International">International</option>	
							</select>
						</div>
					  <div class="form-group" id="cycles" >
							<label for="exampleInputEmail1" >Cycle</label> 
							<select class="form-control" name="cycle" id="cycle">
								<option value="0">--Select --</option>
									<option value="1">1</option>
									<option value="2">2</option>	
										
							</select>
						</div> 
                    
                    
                     <div class="form-group" id="Date" >
							<label for="exampleInputPassword1">Date</label> <input
								class="form-control" name="fileDate" readonly="readonly"
								id="datepicker" placeholder="dd/mm/yyyy" title="dd/mm/yyyy" />
					</div>	 
						
						 <div class="form-group">
                      <label for="exampleInputFile">File Upload</label>
                      <input type="file" name="file" id="dataFile1" title="Upload File" /></td>
                    
                    </div>
                    
				
					
					
					   
                    

							

					<div class="box-footer">
						<!-- <a onclick="Process();" class="btn btn-primary">Process</a> -->
						<a   onclick="processRupSettlemntFileUpload()" class="btn btn-primary">Process</a>   <!--  onclick="processNTSLFileUpload();"  -->
					</div>
					<!-- <div id="processTbl"></div> -->
</form:form>
				</div>
				<!-- /.box -->



			</div>
			<!--/.col (left) -->

		</div>
		<!-- /.row -->
<!-- /.content-wrapper -->
</section>
</div>
<div align="center" id="Loader"
	style="background-color: #ffffff; position: fixed; opacity: 0.7; z-index: 99999; height: 100%; width: 100%; left: 0px; top: 0px; display: none">

	<img style="margin-left: 20px; margin-top: 200px;"
		src="images/unnamed.gif" alt="loader">

</div>
<script>
function showLoader(location) {
	
	$("#Loader").show();
}

function hideLoader(location) {
	
	$("#Loader").hide();
}
function ValidateData()
{
	
	var subcategory = document.getElementById("stSubCategory").value;
	
	
	
	var dataFile= document.getElementById("dataFile1").value;
	var leng= dataFile.length - 3;
	var fileExten = dataFile.substring(leng,dataFile.length);
	//debugger;
	var fileDate = document.getElementById("datepicker").value;
	var  cycle =document.getElementById("cycle").value;
	
	if( subcategory == "0")
	{
		alert("Please select sub-category");
		return false;
	}
	
	if( cycle == "0")
	{
		alert("Please select cycle");
		return false;
	}
	
	
	if( fileDate == "")
	{
		alert("Please select date for processing");
		return false;
	}

	if(dataFile==""){

		alert("Please Upload File.");
		return false;
	}
	
	/* alert(fileExten);
	if(fileExten != 'xlsm')
	{
		alert("Upload Excel file format");
		return false;
	} */
	
	 
	   /*  var validExts = new Array(".xlsm");
		var fileExt = dataFile;
		fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
		if (fileExt != '.xlsm' || fileExt != '.xls' || fileExt != '.xlsx') {
			alert("Upload Excel file format with extention (.xlsm,.xls,.xlsx) ");
			return false;
		} */
		
		var validExts = new Array(".xlsm",".xls",".xlsx");
		var fileExt = dataFile;
		fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
		if(validExts.indexOf(fileExt)<0){
			
			alert("Invalid  file selected ,valid files are of "+validExts.toString()+" types");
			return false;
		}

		return true; 

	}

	function processRupSettlemntFileUpload() {
		//alert("inside processRupSettlemntFileUpload............");
		//debugger;
		var frm = $('#uploadform');

		var fileDate = document.getElementById("datepicker").value;

		var userfile = document.getElementById("dataFile1");
		var stSubCategory = document.getElementById("stSubCategory").value;
		var cycle = document.getElementById("cycle").value;

		var oMyForm = new FormData();
		oMyForm.append('file', userfile.files[0])
		oMyForm.append('stSubCategory', stSubCategory);
		oMyForm.append('datepicker', fileDate);
		oMyForm.append('cycle', cycle);

		if (ValidateData()) {
			$.ajax({
				type : "POST",
				url : "postUploadRupSettlFile.do",
				enctype : "multipart/form-data",
				data : oMyForm,

				processData : false,
				contentType : false,
				//type : 'POST',
				beforeSend : function() {
					showLoader();
				},

				success : function(response) {

					hideLoader();

					alert(response);

					/* document.getElementById("fileName").value="";
					
					// alert("3");
					document.getElementById("stSubCategory").value="";
					
					//alert("5");
					document.getElementById("dataFile1").value="";
					// alert("8");
					document.getElementById("cycle").value="0"; */
					window.location.reload(true);

				},

				error : function(err) {
					hideLoader();
					alert("Error Occurred");
				}
			});

		}

	}
</script>