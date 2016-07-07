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
.myBtn {
	height: 30px;
	width: 60px;
	vertical-align: middle;
	line-height: 30px;
	text-align: center;
	margin-right: 10px;
}

.myBtn span {
	color: white;
}
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
		<div class="row-fluid">
			<div class="span12">
				<div class="pagination pagination-right">
					<ul>
						<li><a href="#">上一页</a></li>
						<li><a href="#">1</a></li>
						<li><a href="#">2</a></li>
						<li><a href="#">3</a></li>
						<li><a href="#">4</a></li>
						<li><a href="#">5</a></li>
						<li><a href="#">下一页</a></li>
					</ul>
				</div>
				<table class="table">
					<thead>
						<tr>
							<th width="40%">板块主题</th>
							<th width="20%">作者</th>
							<th width="20%">回复/查看</th>
							<th width="20%">最后发表</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>1</td>
							<td>TB - Monthly</td>
							<td>01/04/2012</td>
							<td>Default</td>
						</tr>
						<tr class="success">
							<td>1</td>
							<td>TB - Monthly</td>
							<td>01/04/2012</td>
							<td>Approved</td>
						</tr>
						<tr class="error">
							<td>2</td>
							<td>TB - Monthly</td>
							<td>02/04/2012</td>
							<td>Declined</td>
						</tr>
						<tr class="warning">
							<td>3</td>
							<td>TB - Monthly</td>
							<td>03/04/2012</td>
							<td>Pending</td>
						</tr>
						<tr class="info">
							<td>4</td>
							<td>TB - Monthly</td>
							<td>04/04/2012</td>
							<td>Call in to confirm</td>
						</tr>
					</tbody>
				</table>
				<div style="background-color: #c3883e; display: inline-block;" class="myBtn">
					<span>发帖</span>
				</div>
				<div style="background-color: #6bd432; display: inline-block;" class="myBtn">
					<span>精华</span>
				</div>
				<form class="form-search form-inline" style="display: inline-block;">
					<input class="input-medium search-query" type="text" style="border-radius: 0px;"/>
					<button type="submit" class="btn">查找</button>
				</form>
				<table class="table">
					<thead>
						<tr>
							<th width="40%">板块主题</th>
							<th width="20%">作者</th>
							<th width="20%">回复/查看</th>
							<th width="20%">最后发表</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>1</td>
							<td>TB - Monthly</td>
							<td>01/04/2012</td>
							<td>Default</td>
						</tr>
						<tr class="success">
							<td>1</td>
							<td>TB - Monthly</td>
							<td>01/04/2012</td>
							<td>Approved</td>
						</tr>
						<tr class="error">
							<td>2</td>
							<td>TB - Monthly</td>
							<td>02/04/2012</td>
							<td>Declined</td>
						</tr>
						<tr class="warning">
							<td>3</td>
							<td>TB - Monthly</td>
							<td>03/04/2012</td>
							<td>Pending</td>
						</tr>
						<tr class="info">
							<td>4</td>
							<td>TB - Monthly</td>
							<td>04/04/2012</td>
							<td>Call in to confirm</td>
						</tr>
					</tbody>
				</table>
				<div class="pagination pagination-right">
					<ul>
						<li><a href="#">上一页</a></li>
						<li><a href="#">1</a></li>
						<li><a href="#">2</a></li>
						<li><a href="#">3</a></li>
						<li><a href="#">4</a></li>
						<li><a href="#">5</a></li>
						<li><a href="#">下一页</a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript">
</script>
</html>

