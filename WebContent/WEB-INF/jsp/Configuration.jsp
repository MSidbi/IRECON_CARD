<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript" src="js/configure.forms.js"></script>
<script type="text/javascript" src="js/navigate.forms.js"></script>
<script type="text/javascript" src="js/jquery.fancyform.js"></script>
<script type="text/javascript" src="js/commonScript.js"></script> 
<link href="css/fancyform.css" type="text/css" rel="stylesheet" />

 <div class="tab">
  <button id="tab1" style="background-color: #1484e6;" >Classification</button>
  <button id="tab2">Knock-off</button>

  
</div> 

 <form:form name="form" action="addConfiguration.do" method="POST" commandName="ConfigBean">  
<div id="id1" class="tabcontent" style="display: block;" >



<jsp:include page="FTPConfiguration.jsp"></jsp:include>

 


  <input class="form-button" type="button" value="Next" id="next1" style="background-color: #1484e6" />

</div>

<!-- DIV 2 -->
<div id="id2" class="tabcontent">
<jsp:include page="FileConfiguration.jsp"></jsp:include>
<%-- <table align="center" cellpadding="2" cellspacing="0" border="0" class="table" width="100%">
<tr>
<td colspan="2">
tab2  <form:input path="stremarks"/> 
</td>
</tr>
<tr>
  <td><input type="button" value="Previous" id="prev2"/></td>
  <td><input type="button" value="Next" id="next2"/></td>
  
</tr>
</table> --%>

<input class="form-button" type="button" value="Next" id="next2" /> <!-- onclick="return addtblheader();" -->
<input class="form-button" type="button" value="Previous" id="prev2"/>
</div> 

<!-- DIV 3: Filteration Configuration  -->
<div id="id3" class="tabcontent">
<jsp:include page="Classification.jsp"></jsp:include>

<%-- <table align="center" cellpadding="2" cellspacing="0" border="0" class="table" width="100%">
<tr>
<td colspan="2">
tab3 <form:input path="stCheck"/>
</td>
</tr>
<tr>
  <td><input type="button" value="Previous" id="prev3"/></td>
  <td><input type="submit" value="Submit" id="submit"/></td>
  
</tr>
</table> --%>

 </div> 

 </form:form> 