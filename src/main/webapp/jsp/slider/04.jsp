<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="keywords" content="图片轮播，图片切换，焦点图" />
<meta name="description" content="这是一个基于jquery的图片轮播效果演示页" />
<title>演示4：flexslider图片轮播、文字图片相结合滑动切换效果</title>
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

.demo {
	width: 800px;
	margin: 20px auto
}
</style>
<script type="text/javascript"
	src="http://www.helloweba.com/demo/flexslider/../js/jquery-1.7.2.min.js"></script>
<script type="text/javascript"
	src="http://www.helloweba.com/demo/flexslider/jquery.flexslider-min.js"></script>
<script type="text/javascript">
	$(function() {
		$('#carousel').flexslider({
			animation : "slide",
			controlNav : false,
			animationLoop : false,
			slideshow : false,
			itemWidth : 210,
			itemMargin : 5,
			asNavFor : '#slider'
		});

		$('#slider').flexslider({
			animation : "slide",
			controlNav : false,
			animationLoop : false,
			slideshow : false,
			sync : "#carousel",
			start : function(slider) {
				$('body').removeClass('loading');
			}
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
		<div class="demo">
			<div id="slider" class="flexslider">
				<ul class="slides">
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc1.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc2.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc3.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc4.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc5.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc6.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc1.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc2.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc3.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc4.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc5.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc6.jpg" /></li>
				</ul>
			</div>

			<div id="carousel" class="flexslider">
				<ul class="slides">
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc1.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc2.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc3.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc4.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc5.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc6.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc1.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc2.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc3.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc4.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc5.jpg" /></li>
					<li><img
						src="http://www.helloweba.com/demo/flexslider/images/sc6.jpg" /></li>
				</ul>
			</div>

		</div>
	</div>

</body>
</html>
