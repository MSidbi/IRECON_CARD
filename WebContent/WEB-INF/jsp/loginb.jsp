<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="UTF-8">
    <title>Login</title>
    <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>
       <meta http-equiv="X-UA-Compatible" content="IE=10,edge"> 
    <!-- Bootstrap 3.3.2 -->
     <script src="plugins/jQuery/jQuery-2.1.3.min.js"></script>	
    <script src="js/jquery-ui.js" type="text/javascript"></script>
    
    <script type="text/javascript" src="js/login.js"></script>
    <script type="text/javascript" src="js/wrapping/pbkdf2.js"></script> 
		<script type="text/javascript" src="js/wrapping/aes.js"></script>
		<script type="text/javascript" src="js/wrapping/AesUtil.js"></script>
    <link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
    <!-- Font Awesome Icons -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
    <!-- Theme style -->
    <link href="./dist/css/main.css" rel="stylesheet" type="text/css" />
    <!-- iCheck -->	
    <link href="./plugins/iCheck/square/blue.css" rel="stylesheet" type="text/css" />
    
    <script type="text/javascript">
    
    document.cookie = "username=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    </script>
    
   

</head>
<body class="login-page">
<div style="padding: 16px;"><a href=""><img src="./dist/img/logo_uco.jpg" height="70px"></a></div>
 <form:form action="Login.do" method="post" commandName="login" id="login" >
    <div id="loginPage" class="login-box" align="center">
      <div class="login-logo">
        <a href=""><img src="./dist/img/logo_recon.png" height="70px"></a>
	
      </div><!-- /.login-logo -->
      <div class="login-box-body">
      <!--   <p class="login-box-msg">Rate Of Interest</p> -->
       
          <div class="form-group has-feedback">
          <form:input path="user_id" id="user_id" maxlength="15" class="form-control" placeholder="User ID" />
           <!--  <input type="text" class="form-control" placeholder="Email"/> -->
            <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
          </div>
          <div class="form-group has-feedback">
          <form:password path="password" id="password" maxlength="15" class="form-control" placeholder="Password" />
          <form:input path="processType" id="processType" style="display:none"/>
            <!-- <input type="password" class="form-control" placeholder="Password"/> -->
            <span class="glyphicon glyphicon-lock form-control-feedback"></span>
          </div>
          <div class="row">
            <!-- <div class="col-xs-8">    
              <div class="checkbox icheck">
                <label>
                  <input type="checkbox"> Remember Me
                </label>
              </div>                        
            </div> --><!-- /.col -->
            <div class="col-xs-12">
              <!-- <a onclick="loginSelectionShow()" class="btn btn-primary btn-block btn-flat">Sign In</a> -->
             <!--  <input type="submit" id="loginBtn" name="loginBtn" value="Login"  class="btn btn-primary btn-block btn-flat"> -->
              <a onclick="loginSelectionShow()" class="btn btn-primary btn-block btn-flat" id="signIn">Sign In</a>
            </div><!-- /.col -->
          </div>
       
<c:if test="${error_msg != null }">
			<div align="center"  class="errorMsg"><c:out value="${error_msg}" escapeXml="false" /></div>
		</c:if>
		<c:if test="${success_msg != null}">
			<div align="center"  class="successMsg"><c:out value="${success_msg}" escapeXml="false" /></div>
		</c:if>
		
		<div class="clear"></div>
       

        <!-- <a href="#">I forgot my password</a><br>
        <a href="register.html" class="text-center">Register a new membership</a> -->

      </div><!-- /.login-box-body -->
       <div style="font-size: 11px;font-weight: bold;color: #fff;text-align: center;margin-top: 10px;">Designed & Developed by IDBI Intech Ltd.</div>
       <div style="font-size: 11px;font-weight: bold;color: #fff;text-align: center;margin-top: 0px;">Version 1.0.0.0</div>
    </div><!-- /.login-box -->



	<!-- Modal -->
<div id="loginSelection" class="modal fade" data-backdrop="static" role="dialog">
  <div class="modal-dialog" style="width: 50%">

    <!-- Modal content-->
    <div class="modal-content">
      <div class="modal-header">
       <!--  <button type="button" class="close" data-dismiss="modal">&times;</button> -->
        <h4 class="modal-title">Proceed with</h4>
      </div>
      <div class="modal-body">
        <div class="row">
            <div class="col-lg-6 col-xs-12">
              <!-- small box -->
              <div class="small-box bg-blue">
                <div class="inner">
                  <h3>
                   DEBIT CARD
                  </h3>
                  <p>
                    
                  </p>
                </div>
                <div class="icon">
                  <i class="ion ion-ios-cart-outline"></i>
                </div>
                <!-- <a href="" class="small-box-footer" onclick="submitform('DCRS')"> -->
                  <button class="small-box-footer"  type="button" onclick="submitform('DCRS');" style="border: 0; width: 100%;"> Proceed  <i class="fa fa-arrow-circle-right"></i></button>
                <!-- </a> -->
              </div>
            </div><!-- ./col -->
            <div class="col-lg-6 col-xs-12">
              <!-- small box -->
              <div class="small-box bg-purple">
                <div class="inner">
                  <h3>
                    DIGITAL CHANNELS
                  </h3>
                  <p>
                    
                  </p>
                </div>
                <div class="icon">
                  <i class="ion ion-ios-briefcase-outline"></i>
                </div>
                <a href="#" class="small-box-footer">
                  Proceed <i class="fa fa-arrow-circle-right"></i>
                </a>
              </div>
            </div><!-- ./col -->
            <div class="col-lg-6 col-xs-12">
              <!-- small box -->
              <div class="small-box bg-teal">
                <div class="inner">
                  <h3>
                    ATM
                  </h3>
                  <p>
                    
                  </p>
                </div>
                <div class="icon">
                  <i class="ion ion-ios-alarm-outline"></i>
                </div>
                 <input type="text" value="false" id="processvalue" style="display: none;" > 
                <button class="small-box-footer"  type="button" onclick="submitform('ATMCIA');" style="border: 0; width: 100%;"> Proceed <i class="fa fa-arrow-circle-right"></i></button>
               <!--  <a href="#" class="small-box-footer">
                  Login to ATM <i class="fa fa-arrow-circle-right"></i>
                </a> -->
              </div>
            </div><!-- ./col -->
            
            
            <div class="col-lg-6 col-xs-12">
              <!-- small box -->
              <div class="small-box bg-yellow">
                <div class="inner" style="height: 50px;">
                  <h3 style="font-size: 19px">
                   INTERNATIONAL REMITTANCE
                  </h3>
                  <p>
                    
                  </p>
                </div>
                <div class="icon">
                  <i class="ion ion-ios-briefcase-outline"></i>
                </div>
                <a href="#" class="small-box-footer">
                  Proceed <i class="fa fa-arrow-circle-right"></i>
                </a>
              </div>
            </div><!-- ./col -->
            
          </div><!-- /.row -->
      </div>
     <!--  <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div> -->
    </div>

  </div>
</div>
</form:form>

    <!-- jQuery 2.1.3 -->
    <script src="./plugins/jQuery/jQuery-2.1.3.min.js"></script>
    <!-- Bootstrap 3.3.2 JS -->
    <script src="./bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
    <!-- iCheck -->
    <script src="./plugins/iCheck/icheck.min.js" type="text/javascript"></script>
   <!--  <script>
      function loginSelectionShow() {
		$("#loginSelection").modal("show");
	  }
    </script> -->
    <script>
   
      function loginSelectionShow() {
    	
    	  document.getElementById("processvalue").value="true";
    	  if(isValidLogin()){ 
      
			$("#loginSelection").modal("show");
    	  } 
	  }
    /*   $("#loginPage").keyup(function(event) {
    	    if (event.keyCode === 13) {
    	        $("#signIn").click();
    	    }
      }); */
      function submitform(processType) {
    	  
    	  
    	
    	  document.getElementById("processType").value = processType;
    	  var form = document.getElementById("login");
    	  form.submit();
    	  
    	  
      }
 
      
      $("#loginPage").keyup(function(event) {
          if (event.keyCode === 13) {
              $("#signIn").click();
          }
      });

/*       $("#signIn").click(function() {
        alert("Button code executed.");
      }); */
      
      
      
      
    </script>
  </body>
   
</html>