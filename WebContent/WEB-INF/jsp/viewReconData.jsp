<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>   
<%--
    Document   : index
    Created on : Apr 19, 2017, 10:03:44 AM
    Author     : int6346
--%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<html>
    <head>
     <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <link href="css/buttons.dataTables.min.css" rel="stylesheet"/>
      <link href="css/jquery.dataTables.min.css" rel="stylesheet"/>
         <link href="css/bootsrtap.min.css" rel="stylesheet"/>
        <link href="css/dataTables.bootstrap.css" rel="stylesheet"/>
        <script src="js/jquery-1.12.4.js" type="text/javascript"></script>
        <script src="js/jquery.dataTables.min.js" type="text/javascript"></script>
        <script src="js/dataTables.buttons.min.js" type="text/javascript"></script>
        <script src="js/buttons.flash.min.js" type="text/javascript"></script>
        <script src="js/jszip.min.js" type="text/javascript"></script>
        <script src="js/pdfmake.min.js" type="text/javascript"></script>
        <script src="js/vfs_fonts.js" type="text/javascript"></script>
        <script src="js/buttons.html5.min.js" type="text/javascript"></script>
        <script src="js/buttons.print.min.js" type="text/javascript"></script>
        <script src="js/dataTables.bootstrap.min.js" type="text/javascript"></script>
        <script src="js/datatablenew.js" type="text/javascript"></script>

    </head>
    <body>
        
<div style="max-width: 1420px">
        <table  class="table table-striped table-bordered" id="newtable" cellspacing="0" style="width: 75%;">
		<c:if test="${table == 'settlement_switch'}">
			<thead>
				<tr>
					<th style="width: 2px">PAN</th>
					<th style="width: 2px">TERMID</th>
					<th style="width: 2px">TRACE</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="dataList" items="${dataList}">
					<tr>
						<td style="size: 5px;">${dataList.pan}</td>
						<td style="size: 5px;">${dataList.tERMID}</td>
						<td style="size: 5px;">${dataList.tRACE}</td>
					</tr>
				</c:forEach>
			</tbody>
			</c:if>
			<c:if test="${table == 'settlement_cbs'}">
			<thead>
				<tr>
					<th style="width: 2px">ACCOUNT_NUMBER</th>
					<th style="width: 2px">CONTRA_ACCOUNT</th>
					<th style="width: 2px">TRANDATE</th>
					<th style="width: 2px">TRAN_PARTICULAR</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="dataList" items="${dataList}">
					<tr>
					
						<td style="size: 5px;">${dataList.aCCOUNT_NUMBER}</td>
						<td style="size: 5px;">${dataList.cONTRA_ACCOUNT}</td>
						<td style="size: 5px;">${dataList.tRANDATE}</td>
						<td style="size: 5px;">${dataList.tRAN_PARTICULAR}</td>
					</tr>
				</c:forEach>
			</tbody>
			</c:if>
			
			
			
			
		</table>
</div>
    </body>
</html>


