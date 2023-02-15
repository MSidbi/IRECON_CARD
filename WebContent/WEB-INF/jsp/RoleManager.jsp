<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript" src="js/admin.rolemanager.js"></script>
<script type="text/javascript" src="js/jquery.fancyform.js"></script>
<link href="css/fancyform.css" type="text/css" rel="stylesheet" />

<div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
           Role Manager
            <!-- <small>Version 2.0</small> -->
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
            <li class="active">Role Manager</li>
          </ol>
        </section>


<!-- Main content -->
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
<!-- <div class="jtable-main-container">
	<div class="jtable-title">
		<div class="jtable-title-text">ROLE MANAGER</div>
	</div>
</div> -->
<table align="center" cellpadding="2" cellspacing="0" border="0" class="table" width="100%">
	<tr class="oddRow">
		<td width = "50%" colspan="2">&nbsp;</td>
		<th align="right">User
			<select id="user_id" name="user_id" style="vertical-align: middle;">
				<option value="">--SELECT--</option>
				<c:forEach var="user" items="${user_list}" varStatus="loop">
				<option value="${user.user_id}">${user.user_id}&nbsp;&nbsp;-&nbsp;&nbsp;${user.user_name}</option>
				</c:forEach>
			</select>
		</th>
		<td align="left">
			<input type="image" id="search" value="search" src="images/search1.png" style="padding-left: 10px;" >
		</td>
	</tr>
	<tr >
		<th colspan="2" class="footerBtns" style="border: 1px solid #C8C8C8; border-left: none;border-right: none">Available Roles</th>
		<th class="footerBtns" style="border: 1px solid #C8C8C8; border-right : none" colspan="2">Assigned Roles</th>
	</tr>
	<tr>
		<td align="left" colspan="2">
			<div id="allDiv" align="left" style="width: 100%; height: 450px; overflow-y:scroll; overflow-x:hidden ">
				<ul id="tristate">
					<c:forEach var="role" items="${role_list}" varStatus="loop">
						<li style="padding-left: 25px;">
							<input type="checkbox" id="checkbox${role.page_id}" name="checkbox${role.page_id}" class="tri" value="${role.page_id}" />&nbsp;&nbsp;${role.page_name}
							<ul>
								<c:forEach var="role_child" items="${roleService.viewRole(role)}">
									<li style="padding-left: 25px;">
										<input type="checkbox" id="checkbox${role_child.page_id}" name="checkbox${role_child.page_id}" class="tri" value="${role_child.page_id }" />&nbsp;&nbsp;${role_child.page_name}
										<c:if test="${role_child.page_url eq null or role_child.page_url eq '' }">
											<ul>
												<c:forEach var="role_sub_child" items="${roleService.viewRole(role_child)}">
													<li style="padding-left: 25px;">
														<input type="checkbox" id="checkbox${role_sub_child.page_id}" name="checkbox${role_sub_child.page_id}" class="tri" value="${role_sub_child.page_id }" />&nbsp;&nbsp;${role_sub_child.page_name}
													</li>
												</c:forEach>
											</ul>
										</c:if>
									</li>
								</c:forEach>
							</ul>
						</li>
					</c:forEach>
				</ul>
			</div>
		</td>
		<td colspan="2" align="left">
			<div id="userDiv" align="left" style="width: 100%; height: 450px; overflow-y:scroll; overflow-x:hidden "></div>
		</td>
	</tr>
	<tr>
		<td class="footerBtns" colspan="2" align="center">
			<input class="btn btn-primary" type="button" id="assign" name="assign" value="Assign">
			<input class="btn btn-primary" type="button" id="reset_all" name="reset_all" value="Reset">
		</td>
		<td class="footerBtns leftSolid" colspan="2" align="center">
			<input class="btn btn-primary" type="button" id="revoke" name="revoke" value="Revoke">
			<input class="btn btn-primary" type="button" id="reset_user" name="reset_user" value="Reset">
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