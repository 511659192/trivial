<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="keywords" content="图片轮播，图片切换，焦点图" />
<meta name="description" content="这是一个基于jquery的图片轮播效果演示页" />
<title>演示1：flexslider图片轮播、文字图片相结合滑动切换效果</title>
<link rel="stylesheet" type="text/css"
	href="http://www.helloweba.com/demo/flexslider/../css/main.css" />
<link rel="stylesheet" type="text/css"
	href="http://www.helloweba.com/demo/flexslider/flexslider.css" />
<style type="text/css">
h3 {
	height: 42px;
	line-height: 42px;
	font-size: 16px;
	font-weight: normal;
	text-align: center
}

h3 a {
	margin: 10px
}

h3 a.cur {
	color: #f30
}
</style>
<script type="text/javascript"
	src="http://www.helloweba.com/demo/flexslider/../js/jquery-1.7.2.min.js"></script>
<script type="text/javascript"
	src="http://www.helloweba.com/demo/flexslider/jquery.flexslider-min.js"></script>
<script type="text/javascript">
	$(function() {
		$(".flexslider").flexslider({
			slideshowSpeed : 4000, //展示时间间隔ms
			animationSpeed : 400, //滚动时间ms
			touch : true
		//是否支持触屏滑动
		});
	});
</script>
</head>

<body>
	<div id="header">
		<div id="logo">
			<h1>
				<a href="http://www.helloweba.com" title="返回helloweba首页">helloweba</a>
			</h1>
		</div>
	</div>

	<div id="main">
		<div class="flexslider">
			<ul class="slides">
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/s1.jpg" /></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/s2.jpg" /></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/s3.jpg" /></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/s4.jpg" /></li>
			</ul>
		</div>
	</div>
</body>
</html>
