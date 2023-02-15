<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 <%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>FTP Upload</title>
<script type="text/javascript" src="/js/ftpupload.js"></script> 
</head>
<body>

	<div class="container">
			<div class="login">
			<h3>Upload FTP File</h3>
				<form:form action="UploadFtpFile.do"  commandName="ftpBean" method="POST" >
					<table align="center">
					
						<tr>
							<td>File Name :</td> 
							<td>
							<form:select path="fileId" >
							<form:option value="00">Select</form:option>
							 	<c:forEach items="${ftpFileList}" var="fileList">
									<form:option value="${fileList.fileId}">${fileList.fileName}</form:option>
								</c:forEach> 
							</form:select>
							</td>
							
						</tr>
						<tr>
							<td> <input class="form-button" type="submit" id="upload" value="Upload" onclick="validate()"> </td>
						
						</tr>
					
					
					</table>
				</form:form>
			</div>
		</div>
		

</body>
</html>