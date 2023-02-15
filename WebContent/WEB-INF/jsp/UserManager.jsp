<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript" src="js/admin.usermanager.js"></script>

<div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
           User Manager
            <!-- <small>Version 2.0</small> -->
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
            <li class="active">User Manager</li>
          </ol>
        </section>

<section class="content">
<!-- <div class="jtable-main-container">
	<div class="jtable-title">
		<div class="jtable-title-text">USER MANAGER</div>
		<div class="jtable-toolbar">
			<span id="add_user" class="jtable-toolbar-item jtable-toolbar-item-add-record" style="">
				<span class="jtable-toolbar-item-icon"></span>
				<span class="jtable-toolbar-item-text"><b>Add User</b></span>
			</span>
		</div>
	</div>
</div> -->
 <section class="content">
          <div class="row">
            <!-- left column -->
            <div class="col-md-12">
              <!-- general form elements -->
              <div class="box box-primary">
                <!-- <div class="box-header">
                  <h3 class="box-title">Quick Example</h3>
                </div> --><!-- /.box-header -->
                <!-- form start -->
                <%-- <form role="form"> --%>
                  <div class="box-body">
                  <a class="btn btn-primary" id="add_user" style="float: right;margin-bottom: 10px;"><i class="fa fa-plus" aria-hidden="true"></i> Add User</a>
<table align="center" cellpadding="2" cellspacing="0" border="0" width="100%" class="table table-bordered">
	<tr class="footerBtns">
		<th>User Id</th>
		<th class="leftSolid">User Name</th>
		<!-- <th class="leftSolid">User Type</th> -->
		<th class="leftSolid">User Status</th>
		<th class="leftSolid">Last Login</th>
		<th class="leftSolid" colspan="2">Action</th>
	</tr>
	<c:forEach var="user" items="${userList}" varStatus="loop">
	<c:set var="rowClass" value="oddRow" />
	<c:if test="${loop.count %2 eq 0}">
		<c:set var="rowClass" value="evenRow" />
	</c:if>
	<tr class="${rowClass}">
		<td align="center"><a onclick="userLog('${user.user_id}')" title="View User Log" style="border-bottom: 1px dotted blue; cursor: pointer;" ><c:out value="${user.user_id}" /></a></td>
		<td align="center" class="leftDotted"><c:out value="${user.user_name}" /></td>
		<%-- <td align="center" class="leftDotted"><c:out value="${user.user_type}" /></td> --%>
		<td align="center" class="leftDotted">
			<c:choose>
				<c:when test="${user.user_status == 'A' }">Active</c:when>
				<c:when test="${user.user_status == 'I' }">Inactive</c:when>
				<c:otherwise>-</c:otherwise>
		</c:choose>
		<td align="center" class="leftDotted"><c:out value="${user.last_login}" /></td>
		<td align="center" class="leftDotted"><img alt="Edit Record" id="edit${loop.count}" onclick="editUser('${user.user_id}')" src="images/edit.png" title="Edit Record" style="cursor: pointer;"></td>
		<td align="center" class="leftDotted"><img alt="Delete Record" id="delete${loop.count}" onclick="deleteUser('${user.user_id}')" src="images/delete.png" title="Delete Record" style="cursor: pointer;"></td>
	</tr>
	</c:forEach>
	<c:if test="${empty userList}">
	<tr>
		<td colspan="7" align="center" class="oddRow">No Data Found..</td>
	</tr>
	</c:if>
	<tr>
		<td class="footerBtns" colspan="7" align="center">&nbsp;
			<input type="button" class="btn btn-primary" id="live_user" name="live_user" value="Live User" style="width: 90px">
		</td>
	</tr>
</table>
</div><!-- /.box-body -->

              
                <%-- </form> --%>
              </div><!-- /.box -->

              

            </div><!--/.col (left) -->
           
          </div>   <!-- /.row -->
</section>
</div>
