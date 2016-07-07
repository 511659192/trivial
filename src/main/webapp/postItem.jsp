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
<style type="text/css">
</style>
</head>
<body>
	<div class="container-fluid" style="width: 90%; margin: auto; background-color: white">
		<div style="background-color: red; height: 30px; line-height: 30px;">
			<span>主题</span>/<span>目录</span>
			<div style="display: inline; float: right;">
			sgrgs
			</div>
		</div>
		
		<div>
			<div style="display: inline-block; width: 30%">
				查看：1024    回复：888
			</div>
			<div style="display: inline-block; width: 30%">
			
			</div>
		</div>
	</div>

</body>
</html>

