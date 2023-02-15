<%@page import="com.recon.model.LoginBean"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <script type="text/javascript">
 $( document ).ready(function() {
	 startTime();
});
 function checkTime(i) {
	    if (i < 10) {i = "0" + i};  // add zero in front of numbers < 10
	    return i;
	}
function startTime() {
	
    var today = new Date();
    var h = today.getHours();
    var m = today.getMinutes();
    var s = today.getSeconds();
    m = checkTime(m);
    s = checkTime(s);
    document.getElementById('txt').innerHTML =
    h + ":" + m + ":" + s;
    var t = setTimeout(startTime, 500);
    if(h>=6 && h<=11)
    	{
    	
    	$("#mrng").show();
    	$('#mrng').html("Good Morning <i class='fa fa-sun-o' aria-hidden='true' style='font-size: 15px;color: #f9eb35;'></i>");
    	}
    else if(h>=11 && h<=16)
	{
    	
    	$("#aftrn").show();
	$('#aftrn').html("Good Afternoon <i class='fa fa-cloud' aria-hidden='true' style='font-size: 15px;color: #b3fbff;'></i>");
	}
    else if(h>=17 && h<=23)
	{
    	
    	$("#evng").show();
	$('#evng').html("Good Evening <i class='fa fa-moon-o' aria-hidden='true' style='font-size: 15px;color: #fff;'></i>");
	}
    else if(h>=0 && h<=5)
	{
    	$("#evng").show();
	$('#evng').html("Good Evening <i class='fa fa-moon-o' aria-hidden='true' style='font-size: 15px;color: #fff;'></i>");
	}
    
    var dd = today.getDate();
    var mm = today.getMonth()+1; //January is 0!
    var yyyy = today.getFullYear();

    if(dd<10) {
        dd = '0'+dd
    } 

    if(mm<10) {
        mm = '0'+mm
    } 
    document.getElementById('today').innerHTML =
    	dd + '/' + mm + '/' + yyyy;
}


 

/* document.getElementById('today1').innerHTML =
	mm + '/' + dd + '/' + yyyy; */

document.onkeydown = function (event) {
	//alert("keycode is "+event.keyCode);
    event = (event || window.event);
    if (event.keyCode == 123) {
       // alert('No F-keys');
        return false;
    }
    if (event.keyCode == 116)
    {
        alert('F5 is Disabled');
         return false;
     }
};

window.history.forward();
function noBack()
{
	window.history.forward();
   
} 


</script>
    
<header class="main-header">




        <!-- Logo -->
        <a href="UserHome.do" class="logo"><img src="dist/img/logo_recon.png" height="60px"></a>
        <!-- Header Navbar: style can be found in header.less -->
        <nav class="navbar navbar-static-top" role="navigation">
          <!-- Sidebar toggle button-->
          <!-- <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
            <span class="sr-only">Toggle navigation</span>
          </a> -->
          <!-- Navbar Right Menu -->
          <div class="navbar-custom-menu">
            <ul class="nav navbar-nav">
              <!-- Messages: style can be found in dropdown.less-->
             <li class="dropdown user user-menu">
                 
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" >
              <span class="hidden-xs"><i class="fa fa-calendar" aria-hidden="true"></i> <span id="today"></span> &nbsp;&nbsp;&nbsp; <i class="fa fa-clock-o" aria-hidden="true"></i> <span id="txt"></span></span>
              </a>
              <!-- User Account: style can be found in dropdown.less -->
              <li class="dropdown user user-menu">
                 
                <a href="#" class="dropdown-toggle" data-toggle="dropdown" style="padding-top: 24px;padding-bottom: 8px;">
                  <img src="dist/images/maleprofile.png" class="user-image" alt="User Image" style="margin-top: -9px;"/> 
               		<!-- <span id="mrng" style="position: absolute; top: 7px;font-size: 11px;font-weight: bold;">Good Morning</span> -->
               		 <span style="position: absolute;top: 7px;font-size: 11px;font-weight: bold; display: none;" id="mrng" >Good Morning <i class="fa fa-sun-o" aria-hidden="true" style="font-size: 15px;color: #f9eb35;"></i> </span>  
                  <span style="position: absolute;top: 7px;font-size: 11px;font-weight: bold;display: none;" id="aftrn">Good Afternoon <i class="fa fa-cloud" aria-hidden="true" style="font-size: 15px;color: #b3fbff;"></i> </span>
                  <span style="position: absolute;top: 7px;font-size: 11px;font-weight: bold;display: none;" id="evng">Good Evening <i class="fa fa-moon-o" aria-hidden="true" style="font-size: 15px;color: #fff;"></i> </span>
                  <span class="hidden-xs"><%=((LoginBean) request.getSession().getAttribute("loginBean")).getUser_name()%></span>
                </a>
                <!-- <ul class="dropdown-menu"> -->
                  <!-- User image -->
                  <!-- <li class="user-header">
                    <img src="dist/img/user2-160x160.jpg" class="img-circle" alt="User Image" />
                    <p>
                      Alexander Pierce - Web Developer
                      <small>Member since Nov. 2012</small>
                    </p>
                  </li> -->
                  <!-- Menu Body -->
                  <!-- <li class="user-body">
                    <div class="col-xs-4 text-center">
                      <a href="#">Followers</a>
                    </div>
                    <div class="col-xs-4 text-center">
                      <a href="#">Sales</a>
                    </div>
                    <div class="col-xs-4 text-center">
                      <a href="#">Friends</a>
                    </div>
                  </li> -->
                  <!-- Menu Footer-->
                  <!-- <li class="user-footer">
                    <div class="pull-left">
                      <a href="#" class="btn btn-default btn-flat">Profile</a>
                    </div>
                    <div class="pull-right">
                      <a href="Logout.do" class="btn btn-default btn-flat">Sign out</a>
                    </div>
                  </li> -->
                <!-- </ul> -->
              </li>
            </ul>
          </div>
        </nav>
      </header>
      
      