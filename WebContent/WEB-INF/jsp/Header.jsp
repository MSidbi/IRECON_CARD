<%@page import="com.recon.model.LoginBean"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="X-UA-Compatible" content="IE=Edge">
<head>
<script>
//disabling Right Click
$(document).on("contextmenu",function(e){        
	   e.preventDefault();
	});
	//disabling F12
document.onkeydown = function (event) {
	//alert("keycode is "+event.keyCode);
    event = (event || window.event);
    if (event.keyCode == 123) {
       // alert('No F-keys');
        return false;
    }
    if (event.keyCode == 116)
    {
        alert('F5 is Disabled');
         return false;
     }
};

  //preventing from clicking browser back button
  window.history.forward();
        function noBack()
        {
        	window.history.forward();
           
        } 
       
</script>
</head>
 <!-- <body onLoad="noBack();" onpageshow="if (event.persisted) noBack();" onUnload=""> -->
 <body onLoad="noBack();" onpageshow="noBack();" onUnload=""> 

<table align="center" width="100%" cellpadding="0" cellspacing="0" class="table" style="background-color: #f6f6f6">
	<tr>
		<td width="100px" style="float: left;"><img alt="Logo" src="images/logo.png" style="height: 40px; width: 180px;" /></td>
		<th width="500px;" valign="middle" align="center" style="font-size: 15px;">DEBIT CARD RECONCILIATION</th>
		<th width="200px;" align="left" valign="middle" >
			<%--USER | ${loginBean.user_id}--%>
			USER | <%=((LoginBean)request.getSession().getAttribute("loginBean")).getUser_name()%>
		</th>
	</tr>
</table>
</body>