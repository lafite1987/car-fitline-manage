<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- #section:basics/sidebar -->
			<div id="sidebar" class="sidebar responsive">
				<script type="text/javascript">
					try{ace.settings.check('sidebar' , 'fixed')}catch(e){}
				</script>

				<ul class="nav nav-list">
					<li class="active" id="homeMenu">
						<a href="/manage/home" target="mainFrame" onclick="clickHome()">
							<i class="menu-icon fa fa-desktop"></i>
							<span class="menu-text"> 查看首页 </span>
						</a>

						<b class="arrow"></b>
					</li>
					<c:forEach items="${menuList}" var="menu">
				<c:if test="${menu.onMenu == 1}">
				<li class="hsub open" id="lm${menu.id }">
					  <a href="#" class="dropdown-toggle" >
						<i class="menu-icon fa fa-cogs"></i>
						<span class="menu-text">${menu.name }</span>
						<b class="arrow fa fa-angle-down"></b>
					  </a>
					  <b class="arrow"></b>
					  <ul class="submenu nav-show" style="display: block;">
							<c:forEach items="${menu.childList}" var="sub">
								<c:if test="${sub.onMenu == 1}">
								<c:choose>
									<c:when test="${not empty sub.url}">
									<li class="" id="z${sub.id }">
										<a target="mainFrame"  href="${sub.url }" onclick="siMenu('z${sub.id }','lm${menu.id }','${sub.name }','${sub.url }')">
											<i class="menu-icon fa fa-caret-right"></i>${sub.name }
										</a>
									</li>
									</c:when>
									<c:otherwise>
									<li><a href="javascript:void(0);"><i class="icon-double-angle-right"></i>${sub.name }</a></li>
									</c:otherwise>
								</c:choose>
								</c:if>
							</c:forEach>
				  		</ul>
				</li>
				</c:if>
			</c:forEach>
				</ul><!-- /.nav-list -->

				<!-- #section:basics/sidebar.layout.minimize -->
				<div class="sidebar-toggle sidebar-collapse" id="sidebar-collapse">
					<i class="ace-icon fa fa-angle-double-left" data-icon1="ace-icon fa fa-angle-double-left" data-icon2="ace-icon fa fa-angle-double-right"></i>
				</div>

				<!-- /section:basics/sidebar.layout.minimize -->
				<script type="text/javascript">
					try {
						ace.settings.check('sidebar' , 'collapsed');
					}catch(e){
						
					}
					var fmid = "fhindex";
					var mid = "fhindex";
					function siMenu(id,fid,MENU_NAME,MENU_URL){
						if(id != mid){
							$("#"+mid).removeClass();
							mid = id;
						}
						if(fid != fmid){
							$("#"+fmid).removeClass();
							fmid = fid;
						}
						$("#"+fid).attr("class","active open");
						$("#"+id).attr("class","active");
						$("#homeMenu").removeClass("active");
					}
					function clickHome() {
						$(".active").removeClass("active");
						$("#homeMenu").addClass("active");
					}
				</script>
			</div>