<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<style>
/* Reset overflow value to hidden for all non-IE browsers. */
html>body div.tableContainer {
	overflow: hidden;
	
}

/* define width of table. IE browsers only                 */
div.tableContainer table {
	float: left;
	/* width: 740px */
}

/* define width of table. Add 16px to width for scrollbar.           */
/* All other non-IE browsers.                                        */
html>body div.tableContainer table {
	/* width: 756px */
}

/* set table header to a fixed position. WinIE 6.x only                                       */
/* In WinIE 6.x, any element with a position property set to relative and is a child of       */
/* an element that has an overflow property set, the relative value translates into fixed.    */
/* Ex: parent element DIV with a class of tableContainer has an overflow property set to auto */

thead.fixedHeader tr {
	position: relative;
}

/* set THEAD element to have block level attributes. All other non-IE browsers            */
/* this enables overflow to work on TBODY element. All other non-IE, non-Mozilla browsers */

/* make the TH elements pretty */


html>body tbody.scrollContent {
	display: block;
	height: 262px;
	overflow: auto;
	width: 100%
}

html>body thead.fixedHeader {
	display: table;
	overflow: auto;
	width: 100%
}

/* make TD elements pretty. Provide alternating classes for striping the table */
/* http://www.alistapart.com/articles/zebratables/                             */
tbody.scrollContent td, tbody.scrollContent tr.normalRow td {
	background: #FFF;
	border-bottom: none;
	border-left: none;
	border-right: 1px solid #CCC;
	border-top: 1px solid #DDD;
	padding: 2px 3px 3px 4px
}



tbody.scrollContent tr.alternateRow td {
	background: #EEE;
	border-bottom: none;
	border-left: none;
	border-right: 1px solid #CCC;
	border-top: 1px solid #DDD;
	padding: 2px 3px 3px 4px
}
.scrollTable thead tr th{
	    background: #d0e7f5;
}
</style>
<script type="text/javascript">
/*  $(function() {
    var visit=GetCookie("COOKIE1");

    if (visit==null){
        alert("First Time");
    }
    var expire=new Date();
    expire=new Date(expire.getTime()+7776000000);
    document.cookie="COOKIE1=here; expires="+expire;
}); 
 
 function GetCookie(cname) {
	    var name = cname + "=";
	    var decodedCookie = decodeURIComponent(document.cookie);
	    var ca = decodedCookie.split(';');
	    for(var i = 0; i <ca.length; i++) {
	        var c = ca[i];
	        while (c.charAt(0) == ' ') {
	            c = c.substring(1);
	        }
	        if (c.indexOf(name) == 0) {
	            return c.substring(name.length, c.length);
	        }
	    }
	    return null;
	} */
	
	 
    $( document ).ready(function() {
        $("#whatsNew").modal('show');
    });
   
</script>

   <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
            Dashboard
            <!-- <small>Version 2.0</small> -->
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
            <li class="active">Dashboard</li>
          </ol>
        </section>

        <!-- Main content -->
        <section class="content">
          <!-- Info boxes -->
          <div class="row">
            <div class="col-md-6">
              <div class="box">
                <div class="box-header">
                  <h3 class="box-title">Upload Staus</h3>
                </div><!-- /.box-header -->
                <div class="box-body">
                  <table class="table table-bordered scrollTable">
                    <thead class="fixedHeader">
                    <tr>
                      <th style="width: 132px">Networks</th>
                      <th style="width: 50px">Switch</th>
                      <th style="width: 50px">CBS</th>
                      <th style="width: 50px">Network File</th>
                    </tr>
                    </thead>
                    <tbody class="scrollContent">
                    <c:forEach var="UploadBean" items="${UploadBean}">
                    	<tr>
                    	<td style="width: 250px">${UploadBean.category}${UploadBean.subCategory }</td>
                      <td style="width: 50px"><span class="badge bg-green">${UploadBean.switch_date}</span></td>
                      <td style="width: 50px">
                        <span class="badge bg-green">${UploadBean.cbs_date}</span>
                      </td>
                      <td style="width: 50px"><span class="badge bg-green">${UploadBean.network_date}</span></td>
                      </tr>
                    </c:forEach>
                    </tbody>
                  </table>
                </div><!-- /.box-body -->
                
              </div><!-- /.box -->
			  </div>

			              <div class="col-md-6">
              <div class="box">
                <div class="box-header">
                  <h3 class="box-title">Recon Staus</h3>
                </div><!-- /.box-header -->
                <div class="box-body">
                  <table class="table table-bordered scrollTable">
                  <thead class="fixedHeader">
                    <tr>
                      <th style="width: 132px">Networks</th>
                      <th style="width: 50px">Switch</th>
                      <th style="width: 50px">CBS</th>
                      <th style="width: 50px">Network File</th>
                    </tr>
                    </thead>
                    <tbody class="scrollContent">
                   <c:forEach var="CompareBean" items="${CompareBean}">
                    	<tr>
                    	<td style="width: 250px">${CompareBean.category}${CompareBean.subCategory }</td>
                      <td style="width: 50px"><span class="badge bg-green">${CompareBean.switch_date}</span></td>
                      <td style="width: 50px">
                        <span class="badge bg-green">${CompareBean.cbs_date}</span>
                      </td>
                      <td style="width: 50px"><span class="badge bg-green">${CompareBean.network_date}</span></td>
                      </tr>
                    </c:forEach>
                    </tbody>
                  </table>
                </div><!-- /.box-body -->
                
              </div><!-- /.box -->
			  </div>
			  </div>

         

        </section><!-- /.content -->
      </div><!-- /.content-wrapper -->
