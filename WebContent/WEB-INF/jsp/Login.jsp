<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
	<%-- <%
	
	
                   // EventResponse eventResponse = session.getAttribute("response") != null ? (EventResponse) session.getAttribute("response") : (new EventResponse(TransactionStatus.Success, ""));
                    String nextURL = (String) request.getAttribute("NextURL");
                    String browser = request.getHeader("user-agent");
                    System.out.println("Using browser "+browser);
                    if (browser.indexOf("IE") == -1  ) {
                        response.sendRedirect("invalidBrowser.do");
                    }
                    if(session.getAttribute("response") != null){
                        session.removeAttribute("response");
                    }
        %> --%>
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title>Login Form</title>
	    <link rel="stylesheet" href="css/login.css" > 
		<link rel="stylesheet" href="css/jquery-ui.css">
		<script type="text/javascript" src="js/jquery-1.9.1.js"></script>
		<script type="text/javascript" src="js/jquery-ui.js"></script>
		<script type="text/javascript" src="js/login.js"></script> 
	</head>
	<body onLoad="noBack();" onpageshow="noBack();" onUnload="">  
	
		<div class="container">
			<div class="login">
			<h1>Login </h1>
				<form:form action="Login.do" method="post" commandName="login" >
					<table align="center">
						<tr>
							<th>Username</th>
							<td>
								<form:input path="user_id" maxlength="15"/>
								<spring:bind path="user_id">
									<c:if test="${status.error}">
										<img src="images/error.png" style="vertical-align: middle;" title="${status.errorMessage}" />
									</c:if>
								</spring:bind>
							</td>
						</tr>
						<tr><td colspan="2" style="height: 5px"/></tr>
						<tr>
							<th>Password</th>
							<td>
								<form:password path="password" maxlength="15"/>
								<spring:bind path="password">
									<c:if test="${status.error}">
										<img src="images/error.png" style="vertical-align: middle;" title="${status.errorMessage}" />
									</c:if>
								</spring:bind>
							</td>
						</tr>
						<tr><td colspan="2" style="height: 5px"/></tr>
						<tr>
							<td align="center" colspan="2">
								<input type="submit" id="loginBtn" name="loginBtn" value="Login" >
								<input type="button" id="resetBtn" name="resetBtn" value="Reset" >
							</td>
						</tr>
						<tr>
						<td align="center" colspan="2">
							<a id='closeSession' style='color:teal;border-bottom: 1px dotted orange; cursor: pointer;text-decoration:none'>Bounce My Login</a>
						</td>
						</tr>
					</table>
				</form:form>
			</div>
		</div>
		<c:if test="${error_msg != null }">
			<div align="center"  class="errorMsg"><c:out value="${error_msg}" escapeXml="false" /></div>
		</c:if>
		<c:if test="${success_msg != null}">
			<div align="center"  class="successMsg"><c:out value="${success_msg}" escapeXml="false" /></div>
		</c:if>
	</body>
	 <script>
      function loginSelectionShow() {
		$("#loginSelection").modal("show");
	  }
    </script>
</html>