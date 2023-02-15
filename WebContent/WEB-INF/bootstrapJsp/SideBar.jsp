<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@page import="com.recon.model.*"%>
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
            <li class="header"><img src="dist/img/logo_uco.jpg" style="width: 100%;margin-top: 10px"></li>
			<li class="active">
              <a href="Menu.do">
                <i class="fa fa-th"></i> <span>Dashboard</span> <!-- <small class="label pull-right bg-green">new</small> -->
              </a>
            </li>
             <li class="treeview">
              <a href="#">
                <i class="fa fa-dashboard"></i> <span>Administrator</span> <i class="fa fa-angle-left pull-right"></i>
              </a>
              <ul class="treeview-menu">
                <li><a href="UserManager.do"><i class="fa fa-angle-right"></i> User Manager</a></li>
                <!-- <li><a href="RoleManager.do"><i class="fa fa-angle-right"></i> Role Manager</a></li> -->
              </ul>
            </li> 
            
            <li class="treeview">
                 <a href="ManualUpload.do">
                <i class="fa fa-upload"></i> <span>File Upload</span>
                <!-- <i class="fa fa-angle-left pull-right"></i> -->
              </a>
                 
            </li>

			<li class="treeview">
              <a href="#">
                <i class="fa fa-wifi"></i> <span>Networks</span>
                <i class="fa fa-angle-left pull-right"></i>
              </a>
              <ul class="treeview-menu">
                 <li>
                   <a href="#"><i class="fa fa-circle-o"></i><img src="dist/img/cardicons/rupay.jpg" alt="rupay"> RUPAY <i class="fa fa-angle-left pull-right"></i></a>
                  <ul class="treeview-menu">
                    <li><a href="ReconProcess.do?category=RUPAY"><i class="fa fa-angle-right"></i> Process</a></li>
                     <li>
                      <a href="getRupayUnmatchedTTUM.do?category=RUPAY"><i class="fa fa-angle-right"></i> Generate TTUM </a>
                      
                   <!--    <a href="RupayInternationalTTUM.do?category=RUPAY"><i class="fa fa-angle-right"></i> Generate International TTUM </a> -->
                    </li>
					<!-- <li>
                      <a href="UploadTTUM.do?category=RUPAY"><i class="fa fa-angle-right"></i> Upload TTUM </a>
                    </li> -->
                     <li><a href="DownloadDhanaReports.do?category=RUPAY"><i class="fa fa-angle-right"></i>Reports</a></li>
					<!-- <li>
                      <a href="DownloadReports.do?category=RUPAY"><i class="fa fa-angle-right"></i> Generate Reports </a>
                    </li> -->
                    <li><a href="manulRollBack.do?category=RUPAY"><i class="fa fa-angle-right"></i> Recon RollBack</a></li>
                    
                     <!-- <li><a href="manualKnockoff.do?category=RUPAY"><i class="fa fa-angle-right"></i> Knockoff Records</a></li> -->
                     
                     <li><a href="rupayNetworkAdjustment.do?"><i class="fa fa-angle-right"></i>Adjustment File Upload</a></li>
                     
                     <li><a href="AdjustmentTTUM.do?"><i class="fa fa-angle-right"></i>Adjustment TTUM</a></li>
                     
                    </li>
                     </li>
                     
                     <li>
                  <a href="#"><i class="fa fa-circle-o"></i><img src="dist/img/cardicons/rupay.jpg" alt="rupay"> RUPAY SETTLEMENT <i class="fa fa-angle-left pull-right"></i></a>
                  <ul class="treeview-menu">
                      <li><a href="RupayFileUpload.do?category=RUPAY"><i class="fa fa-angle-right"></i> Settlement File Upload</a></li>
                      
                      <li><a href="NCMCFileUpload.do?category=RUPAY"><i class="fa fa-angle-right"></i> NCMC Settlement File Upload</a></li>
                     
                     <li><a href="RupaySettlementProcess.do?category=RUPAY"><i class="fa fa-angle-right"></i> Settlement Processing</a></li>
                     
                     <li><a href="RupaySettlementProcessTTUM.do?category=RUPAY"><i class="fa fa-angle-right"></i> Settlement TTUM</a></li>
                     <li><a href="RupayRawRollback.do?category=CASHNET"><i class="fa fa-angle-right"></i>RawFile Rollback</a></li>
				   </ul>
                   </li> 
                   
                  </ul>
                </li> 
	
				 
                   
				<!--     <li>
                  <a href="#"><i class="fa fa-circle-o"></i><img src="dist/img/cardicons/rupay.jpg" alt="rupay"> RUPAY <i class="fa fa-angle-left pull-right"></i></a>
                  <ul class="treeview-menu">
                    <li><a href="ReconProcess.do?category=RUPAY"><i class="fa fa-angle-right"></i> Process</a></li>
                     <li>
                      <a href="GenerateTTUM.do?category=RUPAY"><i class="fa fa-angle-right"></i> Generate TTUM </a>
                    </li>
					<li>
                      <a href="UploadTTUM.do?category=RUPAY"><i class="fa fa-angle-right"></i> Upload TTUM </a>
                    </li>
					<li>
                      <a href="DownloadReports.do?category=RUPAY"><i class="fa fa-angle-right"></i> Generate Reports </a>
                    </li>
                    <li>
                      <a href="NetworkFileUpdt.do?category=RUPAY"><i class="fa fa-angle-right"></i> Network Files Update </a>
                    </li>
                  </ul>
                </li>    -->

 					<li>
                  <a href="#"><i class="fa fa-circle-o"></i><img src="dist/img/cardicons/visa.jpg" alt="rupay"> CASHNET <i class="fa fa-angle-left pull-right"></i></a>
                  <ul class="treeview-menu">
                    
                    <li><a href="ReconProcess.do?category=CASHNET"><i class="fa fa-angle-right"></i> Process</a></li>
                     <li><a href="manulRollBack.do?category=CASHNET"><i class="fa fa-angle-right"></i>Recon RollBack</a></li>
                     <li><a href="DownloadDhanaReports.do?category=CASHNET"><i class="fa fa-angle-right"></i>Reports</a></li><!-- need to complete coding  -->
                     
                   <!--  <li><a href="DownloadReports.do?category=CASHNET"><i class="fa fa-angle-right"></i>Generate All Reports</a></li>need to complete coding  -->
                    
                    <li><a href="CashnetAdjustmentFileUpload.do?category=NFS"><i class="fa fa-angle-right"></i>Adjustment Upload</a></li>
                    
                    <li><a href="CashnetSettlement.do"><i class="fa fa-angle-right"></i> Settlement Processing</a></li>
                    
                     <li><a href="CashnetGLFileUpload.do"><i class="fa fa-angle-right"></i> Upload Cashnet GL </a></li> 
                    
                     <li><a href="getCashnetUnmatchedTTUM.do?category=CASHNET"><i class="fa fa-angle-right"></i>Generate TTUM</a></li>
                     
                     <li><a href="getCashnetGLStatement.do?category=CASHNET"><i class="fa fa-angle-right"></i>GL Statement</a></li>
                     
                     <li><a href="CashnetRawRollback.do?category=CASHNET"><i class="fa fa-angle-right"></i>RawFile Rollback</a></li>
                     
                    
                  </ul>
                  </li> 

				  <li>
                  <a href="#"><i class="fa fa-circle-o"></i><img src="dist/img/cardicons/visa.jpg" alt="rupay"> VISA <i class="fa fa-angle-left pull-right"></i></a>
                  <ul class="treeview-menu">
                    <li><a href="ReconProcess.do?category=VISA"><i class="fa fa-angle-right"></i> Process</a></li>
                    <li>
                      <a href="getVisaUnmatchedTTUM.do?category=VISA"><i class="fa fa-angle-right"></i> Generate TTUM </a>
                    </li>
					<!-- <li>
                      <a href="UploadTTUM.do?category=VISA"><i class="fa fa-angle-right"></i> Upload TTUM </a>
                    </li> -->
                    <li><a href="DownloadDhanaReports.do?category=VISA"><i class="fa fa-angle-right"></i>Reports</a></li>
					<!-- <li>
                      <a href="DownloadReports.do?category=VISA"><i class="fa fa-angle-right"></i> Generate Reports </a>
                    </li> -->
                    <!-- <li>
                      <a href="GlBalancing.do?category=VISA"><i class="fa fa-angle-right"></i> GL BALANCE </a>
                    </li> -->
                     <li><a href="manulRollBack.do?category=VISA"><i class="fa fa-angle-right"></i> Recon RollBack</a></li>
                    </li>
                    <!-- <li><a href="manualKnockoff.do?category=VISA"><i class="fa fa-angle-right"></i> Knockoff Records</a></li> -->
                    
                     <li><a href="VisaEPFileRead.do"><i class="fa fa-angle-right"></i>EP File Upload</a></li>
                     <li><a href="VisaSettlementProces.do"><i class="fa fa-angle-right"></i>Visa Settlement Process</a></li>
                      <li><a href="VisaSettlementTTUM.do"><i class="fa fa-angle-right"></i>Visa Settlement TTUM</a></li> 
                      <li><a href="getCashnetGLStatement.do?category=VISA"><i class="fa fa-angle-right"></i>GL Statement</a></li>
                      <li><a href="VisaRawRollback.do?category=CASHNET"><i class="fa fa-angle-right"></i>RawFile Rollback</a></li>
                      <!-- <li><a href="getCashnetGLStatement.do?category=VISA"><i class="fa fa-angle-right"></i>GL Statement</a></li>
                      <li><a href="getCashnetGLStatement.do?category=VISA"><i class="fa fa-angle-right"></i>MIS Report</a></li> -->
                  </ul>
                </li>  

				<li>
                  <a href="#"><i class="fa fa-circle-o"></i><img src="dist/img/cardicons/nfs.jpg" alt="rupay"> NFS <i class="fa fa-angle-left pull-right"></i></a>
                  <ul class="treeview-menu">
                    <li><a href="ReconProcess.do?category=NFS"><i class="fa fa-angle-right"></i> Process</a></li>
                    <li>
                       <a href="getNFSUnmatchedTTUM.do?category=NFS"><i class="fa fa-angle-right"></i> Generate TTUM </a>
                    </li>
					<!-- <li>
                      <a href="UploadTTUM.do?category=NFS"><i class="fa fa-angle-right"></i> Upload TTUM </a>
                    </li> -->
					<li><a href="DownloadDhanaReports.do?category=NFS"><i class="fa fa-angle-right"></i>Reports</a></li><!-- need to complete coding  -->
					<li>
                      <a href="DownloadReports.do?category=NFS"><i class="fa fa-angle-right"></i> Generate All Reports </a>
                    </li>
                     <li><a href="manulRollBack.do?category=NFS"><i class="fa fa-angle-right"></i> Recon RollBack</a></li>
                    </li>
                     <li><a href="manualKnockoff.do?category=NFS"><i class="fa fa-angle-right"></i> Knockoff Records</a></li>
                     
                     <li><a href="NIHReport.do?category=NFS"><i class="fa fa-angle-right"></i>NIH Report</a></li>
                     
                     <!-- <li><a href="addCooperativeBank.do?category=NFS"><i class="fa fa-angle-right"></i>Add Cooperative Bank</a></li> -->
                        
                   <!--      <li><a href="addNodalSol.do"><i class="fa fa-angle-right"></i>Add Nodal Sol</a></li> -->
                        
                      <!-- <a href="#"><i class="fa fa-square-o"></i>&nbsp NFS Settlement <i class="fa fa-angle-left pull-right"></i></a>
                    <ul class="treeview-menu"> -->
                     <li><a href="nfsFileUpload.do?category=NFS"><i class="fa fa-angle-right"></i> File Upload</a></li>
                
                     <li><a href="NTSLFileUpload.do?category=NFS"><i class="fa fa-angle-right"></i> NTSL Upload</a></li>
                      <li><a href="NTSLRollback.do?category=NFS"><i class="fa fa-angle-right"></i> NTSL Upload Rollback</a></li>
                     
                     <li><a href="AdjustmentFileUpload.do?category=NFS"><i class="fa fa-angle-right"></i>Adjustment Report Upload</a></li>
                     
                      <li><a href="NFSSettlement.do?category=NFS"><i class="fa fa-angle-right"></i>Settlement Report</a></li>
                      
                 <!--      <li><a href="NFSSettlementTTUM.do?category=NFS"><i class="fa fa-angle-right"></i>Income TTUM</a></li> -->
                      
                     <!--  <li><a href="NFSInterchange.do?category=NFS"><i class="fa fa-angle-right"></i>Income Distribution</a></li> -->
                      
                    
                      
                       <li><a href="NFSSettVoucher.do?category=NFS"><i class="fa fa-angle-right"></i>Settlement Rectification</a></li>
                       
                        <li><a href="NFSSettlementVouc.do?category=NFS"><i class="fa fa-angle-right"></i>Settlement TTUM</a></li>
                        
                        <li><a href="NFSRawRollback.do?category=NFS"><i class="fa fa-angle-right"></i>RawFile Rollback</a></li>
                        <li><a href="getCashnetGLStatement.do?category=NFS"><i class="fa fa-angle-right"></i>GL Statement</a></li>
                       
                       <!-- <li><a href="NFSAdjustmentTTUM.do?category=NFS"><i class="fa fa-angle-right"></i>Adjustment TTUM</a></li> -->
                       
                       <!-- <li><a href="NFSFeeGSTReport.do"><i class="fa fa-angle-right"></i>Fee-GST Report</a></li>
                        <li><a href="NFSSuspectedTxnReport.do"><i class="fa fa-angle-right"></i>Suspected-Txn Report</a></li> -->
                       
                      <!--  <li><a href="CooperativeTTUM.do?category=NFS"><i class="fa fa-angle-right"></i>Cooperative TTUM</a></li> -->
                       
                      <!--  <li><a href="DownloadReversalTTUM.do?category=NFS"><i class="fa fa-angle-right"></i>Late Reversal TTUM</a></li> -->
                   <!--  </ul> -->
                   
                  </ul>
                </li>
					 <li>
                  <a href="#"><i class="fa fa-circle-o"></i><img src="dist/img/cardicons/visa.jpg" alt="rupay"> ONUS <i class="fa fa-angle-left pull-right"></i></a>
                  <ul class="treeview-menu">
                    <li><a href="ReconProcess.do?category=ONUS"><i class="fa fa-angle-right"></i> Process</a></li>
                    <!-- <li>
                      <a href="DownloadReports.do?category=CARDTOCARD"><i class="fa fa-angle-right"></i> Generate Reports </a>
                    </li> -->
                    
                     <li><a href="manulRollBack.do?category=ONUS"><i class="fa fa-angle-right"></i> Recon RollBack</a></li>
                     
                    <!--  <li><a href="GenerateCardtoCardTTUM.do?category=CARDTOCARD"><i class="fa fa-angle-right"></i> Generate TTUM</a></li> -->
                  </ul>
                </li>  
                </li> 
                
                
              </ul>
            </li>
            
            <!-- <li class="treeview">
                 <a href="searchData.do">
                <i class="fa fa-search"></i> <span>Search Data</span>
                <i class="fa fa-angle-left pull-right"></i>
              </a>
                 
            </li> -->
            
            <!-- <li class="treeview">
                 <li><a href="Act4Report.do"><i class="fa fa-angle-right"></i>ACT4 Report</a></li>
                 <li><a href="GlFileUpload.do"><i class="fa fa-angle-right"></i>Upload GL statement</a></li>
                <i class="fa fa-angle-left pull-right"></i>
              </a>
                 
            </li> -->
            
            <li class="treeview">
              <!--    <li><a href="Act4Report.do"><i class="fa fa-angle-right"></i>ACT4 Report</a></li> -->
                 <li><a href="EODReport.do"><i class="fa fa-angle-right"></i>EOD Report Download</a></li>
                <!-- <i class="fa fa-angle-left pull-right"></i> -->
              </a>
                 
            </li>
             <li class="treeview">
              <!--    <li><a href="Act4Report.do"><i class="fa fa-angle-right"></i>ACT4 Report</a></li> -->
                 <li><a href="DownloadOneWayReconReports.do"><i class="fa fa-angle-right"></i>Switch GL Summary Report</a></li>
                <!-- <i class="fa fa-angle-left pull-right"></i> -->
              </a>
                 
            </li>
            
             <li class="treeview">
              <!--    <li><a href="Act4Report.do"><i class="fa fa-angle-right"></i>ACT4 Report</a></li> -->
                 <li><a href="CbsGLExcep.do"><i class="fa fa-angle-right"></i>Gl Recon and CBS Exception Report</a></li>
                <!-- <i class="fa fa-angle-left pull-right"></i> -->
              </a>
                 
            </li>
            
            </li>
             <li class="treeview">
              <!--    <li><a href="Act4Report.do"><i class="fa fa-angle-right"></i>ACT4 Report</a></li> -->
                 <li><a href="DhanalaxmiSettlement.do"><i class="fa fa-angle-right"></i>Settlement Upload Format</a></li>
                <!-- <i class="fa fa-angle-left pull-right"></i> -->
              </a>
                 
            </li>
            
             <li class="treeview"><!-- Added by INT8624 -->
                 <a href="#">
                <i class="fa fa-search"></i> <span>Refund TTUM</span>
                <i class="fa fa-angle-left pull-right"></i>
              </a>
                 <ul class="treeview-menu">
                 		<!-- <li><a href="RefundTTUMMatching.do"><i class="fa fa-angle-right"></i> Refund Records Matching</a></li>
                 		<li><a href="RefundTTUMGeneration.do"><i class="fa fa-angle-right"></i> Refund TTUM Generation</a></li>
                 		<li><a href="RefundTTUMKnockoff.do"><i class="fa fa-angle-right"></i> Refund TTUM Knockoff</a></li> -->
                 		<li><a href="FullRefundTTUM.do"><i class="fa fa-angle-right"></i>Full Refund TTUM</a></li>
                 </ul>
            </li> 
             <li class="treeview">
                 <a href="Logout.do">
                <i class="fa fa-sign-out"></i> <span>Signout</span>
                
              </a>
                 
            </li> 
             <!-- <li class="treeview"><a href="#" onclick="onusForm()"> <i
				class="fa fa-wifi"></i> <span>ONUS TTUM</span> <i class="fa fa-angle-left pull-right"></i>
		</a></li> -->
		 <%-- <form id="onusForm"
    		action="https://10.142.8.23:9011/ONUSTTUM/LoginOnus.do" method="post"
    		style="display: none">
    		<input type="text" id="userName" name="userName"
    			value="<%=((LoginBean) request.getSession().getAttribute("loginBean")).getUser_id()%>">
    	</form> --%>
           <!--  <li class="treeview"><a href="#" onclick="Form()"> <i
				class="fa fa-sign-out"></i> <span>Signout</span> <i class="fa fa-angle-left pull-right"></i>
		</a></li> -->
            
            
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
    	 
          $(selector).removeClass('active');
          $(this).addClass('active');
      });
      </script>