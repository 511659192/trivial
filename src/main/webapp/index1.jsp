<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%-- <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> --%>
<%-- <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> --%>
<%@ page isELIgnored="false"%>
<%
	String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
<title>index.jsp</title>
<jsp:include page="${contextPath}/jsp/inner.jsp"></jsp:include>
</head>
<!-- 
http://www.runoob.com/try/bootstrap/layoutit/  

-->
<body>
	<div class="container-fluid" style="width: 90%; margin: auto">
		<div class="row-fluid" style="background-color: #d9faff">
			<div class="span6" style="height: 30px;">
				<p style="display: block; float: left; height: 30px;  line-height: 30px;">
					[<span class="c1">bpqd</span>发布]<span class="c2">NGC-PF69/星/70UC
						94中国国代5枚银币全套afef</span>
				</p>
			</div>
			<div class="span6" style="height: 30px;">
				<div
					style="float: right; height: 30px; min-width: 60px; line-height: 30px; background-color: #3586bc; text-align: center; box-sizing: border-box">
					<span id="modal-537679" href="#modal-container-88955"
						role="button" style="color: white" data-toggle="modal">登录</span>
				</div>
				<div
					style="float: right; height: 30px; min-width: 60px; line-height: 30px; background-color: #3586bc; text-align: center; box-sizing: border-box">
					<span id="modal-537679-2" href="#modal-container-88955-2"
						role="button" style="color: white" data-toggle="modal">注册</span>
				</div>
				<span
					style="display: block; float: right; height: 30px; line-height: 30px; margin-right: 10px;">同德收藏欢迎你，更多功能请</span>
				<div id="modal-container-88955" class="modal hide fade"
					role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h3 id="myModalLabel">标题栏</h3>
					</div>
					<div class="modal-body">
						<p>显示信息</p>
					</div>
					<div class="modal-footer">
						<button class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
						<button class="btn btn-primary">保存设置</button>
					</div>
				</div>
				
				<div id="modal-container-88955-2" class="modal hide fade"
					role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-hidden="true">×</button>
						<h3 id="myModalLabel">标题栏2</h3>
					</div>
					<div class="modal-body">
						<p>显示信息2</p>
					</div>
					<div class="modal-footer">
						<button class="btn" data-dismiss="modal" aria-hidden="true">关闭</button>
						<button class="btn btn-primary">保存设置</button>
					</div>
				</div>
			</div>
		</div>
		<div id="myLogo" class="row-fluid" style="position: relative;">
				<div>
					<img src="css/images/logo.png" width="100%" />
				</div>
				<span style="bottom: 30%; right: 20%;"><a>客服01</a></span> <span
					style="bottom: 30%; right: 13%;"><a>客服01</a></span> <span
					style="bottom: 30%; right: 6%;"><a>同德财务</a></span>
			</div>
	</div>
</body>
</html>
