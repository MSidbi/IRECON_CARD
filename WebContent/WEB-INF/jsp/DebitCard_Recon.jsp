<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="X-UA-Compatible" content="IE=7,8,9,10" >
		<link href="css/style.css" rel="stylesheet" type="text/css" media="all" />
		<link href="css/dropdown.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/dropdown.vertical.rtl.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/default.ultimate.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/fields.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/form.css" media="all" rel="stylesheet" type="text/css" />

		<link href="css/jquery-ui.css" media="all" rel="stylesheet" type="text/css" />
		<link href="css/jtable.css" media="all" rel="stylesheet" type="text/css" />
		<script src="js/jquery-1.8.3.js" type="text/javascript"></script>
		<script src="js/jquery-ui.js" type="text/javascript"></script>
		<!-- <script src="js/jquery.jtable.js" type="text/javascript"></script> -->
		<script src="js/json2.js" type="text/javascript"></script>
		<script src="js/date.js" type="text/javascript"></script>
		<!-- js and css for datatable  -->
		
	
		<script src="js/util.js" type="text/javascript"></script>

		<title><tiles:insertAttribute name="title" ignore="true" /></title>
	</head>
	<body>
		<%--<div class="topBar" style="width: 100%">
			<div style="float:left;">&nbsp;&nbsp;WELCOME : <c:out value="${fn:trim(loginBean.user_name)}" /></div>
			<div style="float:right"><a style="text-decoration: none; color: blue" href="#">Logout</a>&nbsp;&nbsp;</div>
		</div>--%>
		<div id="container">
			<div id="body">
				<table border="0" align="center" width="874px;" cellpadding="0" cellspacing="0" >
					<tbody>
						
						<tr>
							<td><tiles:insertAttribute name="header"  /></td>
						</tr>
						<tr class="blankRow">
							<td>
								<c:if  test="${error_msg != null}">
									<div class="errorMsg"><c:out value="${error_msg}" /></div>
								</c:if>
								<c:if  test="${success_msg != null}">
									<div class="successMsg"><c:out value="${success_msg}" /></div>
								</c:if>
							</td>
						</tr>
						<tr>
							<td>
								<tiles:insertAttribute name="body"  />
							</td>
						</tr>
						<tr class="blankRow">
							<td>&nbsp;</td>
						</tr>
						<%--<tr>
							<td>
								<tiles:insertAttribute name="footer" />
							</td>
						</tr>--%>
					</tbody>
				</table>
			</div>
			<div id="footer" align="center">
				<tiles:insertAttribute name="footer" />
			</div>
		</div>
	</body>
</html>