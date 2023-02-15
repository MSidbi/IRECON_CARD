<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>File Upload</title>

<script>

function validateFile() {
	
	
	var file = $("input[name=mannFile]").val();
	var fileId = document.getElementById("fileId").value;
	
	if(file=="") {
		
		alert("Please upload file");
		
		return false;
		
	} if(fileId=="00" ) {
		
		alert("Please select file type");
		
		return false;
		
	} 
	
	
	
	
	
	
}


</script>
</head>
<body>
	
	<form:form action="uploadMultipleFile.do" method="POST" modelAttribute="ftpBean" enctype="multipart/form-data">
	
	<table align="center">
	
	<tr>
		<td>
			File Type :
						<form:select path="fileId" id="fileId" >
							<form:option value="00">Select</form:option>
							 	<c:forEach items="${ftpFileList}" var="fileList">
									<form:option value="${fileList.fileId}">${fileList.fileName}</form:option>
								</c:forEach> 
							</form:select>
				
				
		</td>
	</tr>
	<tr>
		<td>	
			File To uplaod :<input type="file" id="mannFile" name="mannFile" value="">
		</td>
		<td><input type="submit" value="Attach" onclick="return validateFile();"></td>
	</tr>
	<tr>
	
	
		
	</tr>
	
	</table>
	
	</form:form>
	
	
</body>
</html>