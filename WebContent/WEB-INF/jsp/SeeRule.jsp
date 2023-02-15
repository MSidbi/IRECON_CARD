<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>SeeRule</title>
<script type="text/javascript" src="js/seeRule.js"></script>
</head>
<body>

	<table>
	
		<tr> <td><a onclick="ShowDtl('Classify');">Classification</a></td> </tr>
		<tr> <td><a onclick="ShowDtl('Knockoff');">Knockoff</a></td> </tr>
		<tr> <td><a onclick="ShowDtl('Compare')">Compare</a></td> </tr>
			
	</table>

<input type="hidden" id="fileId">
<input type="hidden" id="category">
</body>
</html>