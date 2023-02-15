<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<ul id="nav" class="dropdown dropdown-horizontal">
	<li id="first"><a href="Menu.do" title="Return to Main Menu">Home</a></li>
	<c:forEach var="menu" items="${navigationBean.menu}">
		<c:choose>
			<c:when test="${menu.parent_id eq 5 && fn:trim(menu.page_url) eq '' && fn:trim(menu.page_level) eq 2 }">
				<li>
					<span class="dir">${menu.page_name}</span>
					<ul>
						<c:forEach var="sub_menu" items="${navigationBean.menu}">
							<c:if test="${menu.page_id eq sub_menu.parent_id}">
								<li><a href="${fn:trim(sub_menu.page_url)}" title="${fn:trim(sub_menu.page_title)}">${fn:trim(sub_menu.page_name)}</a></li>
							</c:if>
						</c:forEach>
					</ul>
				</li>
			</c:when>
			<c:when test="${menu.parent_id eq 5 && fn:trim(menu.page_url) != '' && fn:trim(menu.page_level) eq 2 }">
				<li><a href="${fn:trim(menu.page_url)}" title="${menu.page_title}">${menu.page_name}</a></li>
			</c:when>
		</c:choose>
	</c:forEach>
	<li style="float: right;" id="first"><a href="Logout.do" title="Click To Log Out.">Log Out </a></li>
</ul>