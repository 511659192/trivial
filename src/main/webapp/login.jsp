<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> --%>
<%-- <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> --%>
<%-- <%@ page isELIgnored="false"%> --%>
<%
	String contextPath = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>index.jsp</title>
<jsp:include page="${contextPath}/jsp/inner.jsp"></jsp:include>
<link rel="stylesheet" type="text/css"
	href="http://www.helloweba.com/demo/flexslider/flexslider.css" />
<style type="text/css">
#registForm > div {
	width: 30%; 
	margin: 10px auto;
}
#registForm > ol{
	width: 50%; 
	text-align: center;
	margin: 15px auto;
	line-height: 20px;
}
#registForm > ol > li{
	line-height: 20px;
}

.demo {
	width: 800px;
	margin: 20px auto
}
</style>
<script type="text/javascript"
	src="http://www.helloweba.com/demo/flexslider/jquery.flexslider-min.js"></script>
<script type="text/javascript"
	src="http://www.helloweba.com/demo/flexslider/jquery.easing.min.js"></script>
<script type="text/javascript">
	$(function() {
		$("#protraitDiv").flexslider({
			animation : "slide",
			animationLoop : false,
			controlNav : false,
			itemWidth : 120,
			itemMargin : 5,
			minItems : 2,
			maxItems : 9
		// pausePlay: true
		});
	});
</script>
</head>
<body>
	
	<div style="width: 90%; margin: auto; background-color: white">
		<form id="registForm" class="form-horizontal">
			<div class="control-group">
				<label class="control-label" for="username"><span class="c1">*</span>用户名</label>
				<div class="controls">
					<input id="username" name="username" type="text" />
				</div>
			</div>
			<div class="control-group">
				 <label class="control-label" for="inputPassword"><span class="c1">*</span>密码</label>
				<div class="controls">
					<input id="inputPassword" name="password" type="password" />
				</div>
			</div>
			<div class="control-group">
				 <label class="control-label" for="captcha"><span class="c1">*</span>验证码</label>
				<div class="controls">
					<input id="captcha" type="text" name="captcha"/>
				</div>
			</div>
			<div class="control-group">
				<div class="controls">
					 <label class="checkbox"><input type="checkbox" name="rememerMe" style="margin-top: -5px;"/> 记住我</label> 
					 <button type="submit" class="btn">登陆</button>
				</div>
			</div>
		</form>
	</div>
</body>
</html>
