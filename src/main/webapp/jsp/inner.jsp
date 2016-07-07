<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="org.apache.commons.lang3.StringUtils"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<%String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();%>
<%String contextPath = request.getContextPath();%>
<%String version = "20160417";%>

<%
Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
Cookie[] cookies = request.getCookies();
if (null != cookies) {
	for (Cookie cookie : cookies) {
		cookieMap.put(cookie.getName(), cookie);
	}
}
String easyuiTheme = "gray";//指定如果用户未选择样式，那么初始化一个默认样式
if (cookieMap.containsKey("easyuiTheme")) {
	Cookie cookie = (Cookie) cookieMap.get("easyuiTheme");
	easyuiTheme = cookie.getValue();
}
%>

<script type="text/javascript">
var ym = ym || {};
ym.contextPath = '<%=contextPath%>';
ym.basePath = '<%=basePath%>';
ym.version = '<%=version%>';
ym.pixel_0 = '<%=contextPath%>/style/images/pixel_0.gif';//0像素的背景，一般用于占位
</script>

<%-- 引入jQuery --%>
<%
String User_Agent = request.getHeader("User-Agent");
if (StringUtils.indexOfIgnoreCase(User_Agent, "MSIE") > -1 && (StringUtils.indexOfIgnoreCase(User_Agent, "MSIE 6") > -1 || StringUtils.indexOfIgnoreCase(User_Agent, "MSIE 7") > -1 || StringUtils.indexOfIgnoreCase(User_Agent, "MSIE 8") > -1)) {
	out.println("<script src='" + contextPath + "/js/jquery/jquery-1.12.3.js' type='text/javascript' charset='utf-8'></script>");
} else {
	out.println("<script src='" + contextPath + "/js/jquery/jquery-2.2.3.js' type='text/javascript' charset='utf-8'></script>");
}
%>

<%-- 引入EasyUI --%>
<link id="easyuiTheme" rel="stylesheet" href="<%=contextPath%>/js/easyui/jquery-easyui-1.4.5/themes/<%=easyuiTheme%>/easyui.css" type="text/css">
<script type="text/javascript" src="<%=contextPath%>/js/easyui/jquery-easyui-1.4.5/jquery.easyui.min.js" charset="utf-8"></script>
<script type="text/javascript" src="<%=contextPath%>/js/easyui/jquery-easyui-1.4.5/locale/easyui-lang-zh_CN.js" charset="utf-8"></script>
<link rel="stylesheet" href="<%=contextPath%>/js/easyui/jquery-easyui-1.4.5/themes/icon.css" type="text/css">



<%-- <link href="<%=contextPath%>/js/bootstrap/bootstrap-3.3.6-dist/css/bootstrap.css" type="text/css" rel="stylesheet" /> --%>
<script src="<%=contextPath%>/js/bootstrap/bootstrap-3.3.6-dist/js/bootstrap.js" type="text/javascript"></script>
<link rel="stylesheet" href="<%=contextPath%>/css/bootstrap-combined.min.css" type="text/css">
<link rel="stylesheet" href="<%=contextPath%>/css/mall.css" type="text/css">
<link rel="stylesheet" href="<%=contextPath%>/css/demo.css" type="text/css">
