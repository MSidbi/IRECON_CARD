<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<aside class="main-sidebar">
        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">
          <!-- Sidebar user panel -->
          <!-- <div class="user-panel">
            <div class="pull-left image">
              <img src="dist/img/user2-160x160.jpg" class="img-circle" alt="User Image" />
            </div>
            <div class="pull-left info">
              <p>Alexander Pierce</p>
          
              <a href="#"><i class="fa fa-circle text-success"></i> Online</a>
            </div>
          </div> -->
          <!-- search form -->
          <!-- <form action="#" method="get" class="sidebar-form">
            <div class="input-group">
              <input type="text" name="q" class="form-control" placeholder="Search..."/>
              <span class="input-group-btn">
                <button type='submit' name='search' id='search-btn' class="btn btn-flat"><i class="fa fa-search"></i></button>
              </span>
            </div>
          </form> -->
          <!-- /.search form -->
          <!-- sidebar menu: : style can be found in sidebar.less -->
          <ul class="sidebar-menu">
            <li class="header">ATM Cash Account</li>
			<li class="active">
              <a href="UserHome.do">
                <i class="fa fa-th"></i> <span>Dashboard</span> <!-- <small class="label pull-right bg-green">new</small> -->
              </a>
            </li>
			<li class="">
              <a href="SourceFileUploadController.do">
                <i class="fa fa-upload"></i> <span>File Upload</span> <!-- <small class="label pull-right bg-green">new</small> -->
              </a>
            </li>
			<li class="">
              <a href="TallyData.do">
                <i class="fa fa-file-o"></i> <span>View Process Data</span> <!-- <small class="label pull-right bg-green">new</small> -->
              </a>
            </li>
			<li class="">
              <a href="Settlment.do">
                <i class="fa fa-file"></i> <span>View Settlement</span> <!-- <small class="label pull-right bg-green">new</small> -->
              </a>
            </li>
			<li class="">
              <a href="Switch_disp.do">
                <i class="fa fa-file-o"></i> <span>Switch Dispense</span> <!-- <small class="label pull-right bg-green">new</small> -->
              </a>
            </li>
			<li class="">
              <a href="#">
                <i class="fa fa-file"></i> <span>TTUM Generation</span> <!-- <small class="label pull-right bg-green">new</small> -->
              </a>
            </li>
			<li class="">
              <a href="ViewStatus.do">
                <i class="fa fa-file-o"></i> <span>View Status</span> <!-- <small class="label pull-right bg-green">new</small> -->
              </a>
            </li>
            <!-- <li class="treeview">
              <a href="#">
                <i class="fa fa-dashboard"></i> <span>Administrator</span> <i class="fa fa-angle-left pull-right"></i>
              </a>
              <ul class="treeview-menu">
                <li><a href="#"><i class="fa fa-circle-o"></i> User Manager</a></li>
                <li><a href="#"><i class="fa fa-circle-o"></i> Role Manager</a></li>
              </ul>
            </li> -->

			
            
            <!-- <li class="treeview">
              <a href="#">
                <i class="fa fa-share"></i> <span>Networks</span>
                <i class="fa fa-angle-left pull-right"></i>
              </a>
              <ul class="treeview-menu">
                <li><a href="#"><i class="fa fa-circle-o"></i> Level One</a></li>
                <li>
                  <a href="#"><i class="fa fa-circle-o"></i> Level One <i class="fa fa-angle-left pull-right"></i></a>
                  <ul class="treeview-menu">
                    <li><a href="#"><i class="fa fa-circle-o"></i> Level Two</a></li>
                    <li>
                      <a href="#"><i class="fa fa-circle-o"></i> Level Two <i class="fa fa-angle-left pull-right"></i></a>
                      <ul class="treeview-menu">
                        <li><a href="#"><i class="fa fa-circle-o"></i> Level Three</a></li>
                        <li><a href="#"><i class="fa fa-circle-o"></i> Level Three</a></li>
                      </ul>
                    </li>
                  </ul>
                </li>
                <li><a href="#"><i class="fa fa-circle-o"></i> Level One</a></li>
              </ul>
            </li> -->
           
          </ul>
        </section>
        <!-- /.sidebar -->
      </aside>
      
      <script>
      var selector = '.sidebar-menu li';

      $(selector).on('click', function(){
    	  debugger;
          $(selector).removeClass('active');
          $(this).addClass('active');
      });
      </script>