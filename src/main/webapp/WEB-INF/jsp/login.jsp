<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
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
<style type="text/css">
#registForm > div {
	width: 400px; 
	margin: 10px auto;
}

.error{color:red;}
</style>
</head>
<body>
	<div style="width: 90%; margin: auto; background-color: white">
		<div style="margin: 0px auto; width: 40%; text-align: center;" class="error">${error}</div>
		<form id="registForm" class="form-horizontal" action="" method="post">
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
				<label class="control-label" for="inputPassword"><span class="c1">*</span>验证码：</label>
				<div class="controls">
					<input id="inputPassword" name="jcaptchaCode" type="text" /><img class="jcaptcha-btn jcaptcha-img" src="${pageContext.request.contextPath}/jcaptcha.jpg" title="点击更换验证码"><a class="jcaptcha-btn" href="javascript:;">换一张</a>
				</div>
			</div>
			<c:if test="${jcaptchaEbabled}">
			</c:if>
			<div class="control-group">
				<div class="controls">
					 <label class="checkbox"><input type="checkbox" name="rememerMe" style="margin-top: -5px;"/> 记住我</label> 
					 <button type="submit" class="btn">登陆</button>
				</div>
			</div>
		</form>
	</div>
<script>
    $(function() {
        $(".jcaptcha-btn").click(function() {
            $(".jcaptcha-img").attr("src", '${pageContext.request.contextPath}/jcaptcha.jpg?'+new Date().getTime());
        });
    });
</script>
</body>
</html>
