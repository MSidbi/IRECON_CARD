<%--
    Document   : beforeLogin
    Created on : Oct 15, 2015, 10:37:02 AM
    Author     : INT4341
--%>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%          response.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
            response.setHeader("Pragma", "no-cache"); //HTTP 1.0
%>

<%
response.setHeader("Cache-Control","no-cache");
response.setHeader("Cache-Control","no-store");
response.setDateHeader("Expires", 0);
response.setHeader("Pragma","no-cache");
response.setHeader("X-Frame-Options","deny");
%>
  
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>i-Recon</title>
    <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>
    <meta http-equiv="X-UA-Compatible" content="IE=10"> 
    <!-- Bootstrap 3.3.2 -->
    <link href="bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
    <!-- Font Awesome Icons -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
    <!-- Ionicons -->
    <!-- <link href="http://code.ionicframework.com/ionicons/2.0.0/css/ionicons.min.css" rel="stylesheet" type="text/css" /> -->
    <!-- Daterange picker -->
    <link href="plugins/daterangepicker/daterangepicker-bs3.css" rel="stylesheet" type="text/css" />
    <!-- Theme style -->
    <link href="dist/css/main.css" rel="stylesheet" type="text/css" />
    <link href="dist/css/animatia.css" rel="stylesheet" type="text/css" />
    
    <!-- AdminLTE Skins. Choose a skin from the css/skins 
         folder instead of downloading all of them to reduce the load. -->
    <link href="dist/css/skins/skin-blue.css" rel="stylesheet" type="text/css" />
    <!-- jQuery 2.1.3 -->
    
    
    <!-- <link href="css/style.css" rel="stylesheet" type="text/css" media="all" /> -->
	<!-- 	<link href="css/dropdown.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/dropdown.vertical.rtl.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/default.ultimate.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/fields.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/form.css" media="all" rel="stylesheet" type="text/css" /> -->

		<link href="css/jquery-ui.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/jtable.css" media="all" rel="stylesheet" type="text/css" />
		<!-- <link href="css/datatables.min.css" rel="stylesheet" type="text/css" />
		<link href="https://cdn.datatables.net/select/1.2.7/css/select.dataTables.min.css" rel="stylesheet" type="text/css" /> -->
		<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/dt/dt-1.10.18/af-2.3.0/b-1.5.2/cr-1.5.0/fc-3.2.5/fh-3.1.4/kt-2.4.0/r-2.2.2/rg-1.0.3/rr-1.2.4/sc-1.5.0/sl-1.2.6/datatables.min.css"/>
 

		
		<script src="plugins/jQuery/jQuery-2.1.3.min.js"></script>
		<script type="text/javascript" src="https://cdn.datatables.net/v/dt/dt-1.10.18/af-2.3.0/b-1.5.2/cr-1.5.0/fc-3.2.5/fh-3.1.4/kt-2.4.0/r-2.2.2/rg-1.0.3/rr-1.2.4/sc-1.5.0/sl-1.2.6/datatables.min.js"></script>
		<script src="js/commonScript.js" type="text/javascript"></script> 
<!-- 		<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js" type="text/javascript"></script>
		<script src="plugins/jQuery/jQuery-2.1.3.min.js" type="text/javascript"></script> -->
		<script src="js/jquery-ui.js" type="text/javascript"></script>
		<script src="plugins/slimScroll/jquery.slimscroll.min.js" type="text/javascript"></script>	
		<script type="text/javascript" src="js/wrapping/pbkdf2.js"></script> 
		<script type="text/javascript" src="js/wrapping/aes.js"></script>
		<script type="text/javascript" src="js/wrapping/AesUtil.js"></script>
		
		
		<!-- <script src="js/jquery.jtable.js" type="text/javascript"></script>   /IRECON/WebContent/js/wrapping -->
		<!-- <script src="js/json2.js" type="text/javascript"></script>
		<script src="js/date.js" type="text/javascript"></script> -->
		
		

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
  </head>
  <body class="skin-blue fixed">
    <div class="wrapper">
        <tiles:insertAttribute name="header" />
        <tiles:insertAttribute name="sidebar" />
        <tiles:insertAttribute name="body" />
        <tiles:insertAttribute name="footer" />
    </div><!-- ./wrapper -->

<!-- Modal -->
<div id="whatsNew" class="modal fade whatsnewModal" data-backdrop="static" role="dialog">
  <div class="modal-dialog animated zoomIn" style="width: 30%; ">

    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
         <button type="button" class="close" data-dismiss="modal">&times;</button> 
        <h4 class="modal-title" style="text-align: center;"><span style="border: 2px solid #2979b3; border-radius: 27px; padding: 6px 12px; background: #fff;color:#333"><span class="coloranim">Whats New !</span></span> <span style="font-size: 12px;">Version 1.0.0.0 </span></h4>
      </div>
      <div class="modal-body" style="height: 300px;overflow-y: scroll;">
      <span class="label-New">New Changes</span>
       <ul style="line-height: 20px; font-size: 11px;">
       	
	       		<li> A NEW AND CLEAN UI</li>
		<li> SINGLE SIGN-ON (FOR FUTURE RELEASE)</li>
		<li> NEW INFORMATIVE DASHBOARD</li>
		<li> MORE AUTHENTIC USER CONTROL</li>
		<li> NO MORE JAR REQUIRED TO UPLOAD RAW DATA</li>
		<li> MORE CONTROL MENU OPTIONS</li>
		<li> PROCESSED RECORD COUNT</li>
		<li> SYSTEM ALERTS</li>
		<li> VERSION CONTROL</li>
		<li> SEARCH TRANSACTIONS</li>
	</ul>
	
	<!-- span class="label-fix">Bug-Fixes</span>
	<ul style="line-height: 20px; font-size: 11px;">
	
	<li>RUPAY -KNOCK OFF DISPUTE TTUM.</li>
	<li>RUPAY - DISPUTE TTUM WITH EXTRA FIELDS.</li>
	<li>RUPAY FILE DUPLICATION CONTROL.</li>
	<li>VISA - CARD NUMBER CAPTURED IN THE ACCOUNT NO. FIELD INSTEAD OF ACCOUNT NO.</li>
	<li>NFS - DYNAMIC DATE FIELD CAPABILITY ADDED.</li>
	<li>NFS - BULK DATA LOAD CONTROL ENABLED.</li>
	<li>NFS - NEW EFFICIENT LOGIC TO FILTER DATA.</li>
	<li>MASTERCARD - GCO DOWNLOAD.</li>
	
     

       	
       </ul-->
      </div>
       <!-- <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>  -->
    </div>

  </div>
</div>
    
    <!-- Bootstrap 3.3.2 JS -->
    <script src="bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
   
    <!-- FastClick -->
    <script src='plugins/fastclick/fastclick.min.js'></script>
    <!-- AdminLTE App -->
    <script src="dist/js/app.min.js" type="text/javascript"></script>
    <!-- Sparkline -->
    <script src="plugins/sparkline/jquery.sparkline.min.js" type="text/javascript"></script>
    <!-- daterangepicker -->
    <!-- <script src="plugins/daterangepicker/daterangepicker.js" type="text/javascript"></script> -->
    <!-- datepicker -->
    <!-- <script src="plugins/datepicker/bootstrap-datepicker.js" type="text/javascript"></script> -->
    <!-- iCheck -->
    <script src="plugins/iCheck/icheck.min.js" type="text/javascript"></script>
    <!-- SlimScroll 1.3.0 -->
    
    
    
  

  </body>
</html>
