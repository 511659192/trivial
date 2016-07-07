
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
<title>afef</title>
<jsp:include page="jsp/inner.jsp"></jsp:include>
</head>
<!-- 
http://www.runoob.com/try/bootstrap/layoutit/ 

-->
<body>

	    <table class="easyui-datagrid" title="Basic DataGrid" style="width:700px;height:250px"
            data-options="singleSelect:true,collapsible:true,url:'datagrid_data1.json',method:'get'">
        <thead>
            <tr>
                <th data-options="field:'itemid',width:80">Item ID</th>
                <th data-options="field:'productid',width:100">Product</th>
                <th data-options="field:'listprice',width:80,align:'right'">List Price</th>
                <th data-options="field:'unitcost',width:80,align:'right'">Unit Cost</th>
                <th data-options="field:'attr1',width:250">Attribute</th>
                <th data-options="field:'status',width:60,align:'center'">Status</th>
            </tr>
        </thead>
    </table>

	<table id="tt" class="easyui-datagrid"
		style="width: 400px; height: auto;">
		<thead>
			<tr>
				<th field="name1" width="50">Col 1</th>
				<th field="name2" width="50">Col 2</th>
				<th field="name3" width="50">Col 3</th>
				<th field="name4" width="50">Col 4</th>
				<th field="name5" width="50">Col 5</th>
				<th field="name6" width="50">Col 6</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>Data 1</td>
				<td>Data 2</td>
				<td>Data 3</td>
				<td>Data 4</td>
				<td>Data 5</td>
				<td>Data 6</td>
			</tr>
			<tr>
				<td>Data 1</td>
				<td>Data 2</td>
				<td>Data 3</td>
				<td>Data 4</td>
				<td>Data 5</td>
				<td>Data 6</td>
			</tr>
			<tr>
				<td>Data 1</td>
				<td>Data 2</td>
				<td>Data 3</td>
				<td>Data 4</td>
				<td>Data 5</td>
				<td>Data 6</td>
			</tr>
			<tr>
				<td>Data 1</td>
				<td>Data 2</td>
				<td>Data 3</td>
				<td>Data 4</td>
				<td>Data 5</td>
				<td>Data 6</td>
			</tr>
		</tbody>
	</table>
	
<script type="text/javascript">


$(function(){
	$.ajax({
		dataType:"json",
		type:"post",
		jsonp:"jsonpcallback",
		url:"http://localhost/mall/json2",
		success:function(ret){
			console.info(ret)
		}
	});
})
</script>
	
</body>
</html>
