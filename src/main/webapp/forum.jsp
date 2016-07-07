<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%-- <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> --%>
<%@ page isELIgnored="false"%>
<%
	String contextPath = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>index.jsp</title>
<jsp:include page="${contextPath}/jsp/inner.jsp"></jsp:include>
<script type="text/javascript" src="<%=contextPath%>/js/easyui/jquery.portal.js" charset="utf-8"></script>
<link rel="stylesheet" type="text/css"
	href="http://www.helloweba.com/demo/flexslider/flexslider.css" />
<style type="text/css">
.panel-header {
	height: 40px;
}

#pp .panel-header {
	height: 20px;
}
.panel-title {
	line-height: 20px;
}

ul li {
	display: inline-block; 
	width: 200px;
	line-height: 30px;
}

ul {
	margin-top: 10px;
}
</style>
</head>
<body>
	<div id="tt">
		<a style="width: 100px; text-align: right;"><p style="font-size:14px; color: red;">申请版主 》》</p></a>
	</div>

	<div
		style="width: 90%; margin: auto; background-color: white">
		<div id="p" class="easyui-panel" title="艺术类<p style='color: red; display: inline'>(1024)</p><p style='font-size:13px; color: red; '>版主：李白、杜甫、白居易</p>"
			style="width: 100%; padding: 10px;" data-options="tools:'#tt'">
			<ul>
				<li><a>青铜艺术</a></li>
				<li><a>古代兵器</a></li>
				<li><a>铜镜专区</a></li>
				<li><a>造像专区</a></li>
				<li><a>铜炉专区</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
			</ul>
		</div>
		
		<div class="easyui-panel" title="艺术类<p style='color: red; display: inline'>(1024)</p><p style='font-size:13px; color: red; '>版主：李白、杜甫、白居易</p>"
			style="width: 100%; padding: 10px;" data-options="tools:'#tt'">
			<ul>
				<li><a>青铜艺术</a></li>
				<li><a>古代兵器</a></li>
				<li><a>铜镜专区</a></li>
				<li><a>造像专区</a></li>
				<li><a>铜炉专区</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
			</ul>
		</div>
		
		<div class="easyui-panel" title="艺术类<p style='color: red; display: inline'>(1024)</p><p style='font-size:13px; color: red; '>版主：李白、杜甫、白居易</p>"
			style="width: 100%; padding: 10px;" data-options="tools:'#tt'">
			<ul>
				<li><a>青铜艺术</a></li>
				<li><a>古代兵器</a></li>
				<li><a>铜镜专区</a></li>
				<li><a>造像专区</a></li>
				<li><a>铜炉专区</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
				<li><a>青铜艺术</a></li>
			</ul>
		</div>
		<div id="pp" style="position:relative">
			<div style="width:30%;">
			    <div title="本月最热话题" style="height:200px;padding:5px;">
			    	<div class="t-list"><a href="http://www.w3cschool.cc/jeasyui/jeasyui-layout-layout.html">abc</a></div>
			    	<div class="t-list"><a href="http://www.w3cschool.cc/jeasyui/jeasyui-layout-panel.html">cde</a></div>
			    </div>
			</div>
			<div style="width:40%;">
				<div title="最新推荐主题" style="height:200px;padding:5px;">
			    	<div class="t-list"><a href="http://www.w3cschool.cc/jeasyui/jeasyui-layout-layout.html">abc</a></div>
			    	<div class="t-list"><a href="http://www.w3cschool.cc/jeasyui/jeasyui-layout-panel.html">cde</a></div>
			    </div>
			</div>
			<div style="width:30%;">
				<div title="最新精华" style="height:200px;padding:5px;">
			    	<div class="t-list"><a href="http://www.w3cschool.cc/jeasyui/jeasyui-layout-layout.html">abc</a></div>
			    	<div class="t-list"><a href="http://www.w3cschool.cc/jeasyui/jeasyui-layout-panel.html">cde</a></div>
			    </div>
			</div>
		</div>
	</div>
	
</body>
<script type="text/javascript">
$(function(){
	$('#pp').portal({
		border:false,
		fit:true
	});
});
</script>
</html>

