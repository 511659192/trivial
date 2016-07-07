<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="keywords" content="图片轮播，图片切换，焦点图" />
<meta name="description" content="这是一个基于jquery的图片轮播效果演示页" />
<title>演示3：flexslider图片轮播、文字图片相结合滑动切换效果</title>
<link rel="stylesheet" type="text/css"
	href="http://www.helloweba.com/demo/flexslider/flexslider.css" />
<style type="text/css">
#main {
	width: 60%
}

.slides li p {
	height: 24px;
	line-height: 24px;
	text-align: center
}
</style>
<script type="text/javascript"
	src="http://www.helloweba.com/demo/flexslider/../js/jquery-1.7.2.min.js"></script>
<script type="text/javascript"
	src="http://www.helloweba.com/demo/flexslider/jquery.flexslider-min.js"></script>
<script type="text/javascript"
	src="http://www.helloweba.com/demo/flexslider/jquery.easing.min.js"></script>
<script type="text/javascript">
	$(function() {
		$(".flexslider").flexslider({
			animation : "slide",
			animationLoop : false,
			itemWidth : 210,
			itemMargin : 5,
			minItems : 2,
			maxItems : 4
		// pausePlay: true
		});
	});
</script>
</head>

<body>
	<div id="main">
		<div class="flexslider carousel">
			<ul class="slides">
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/sc1.jpg" />
				<p>图片展示1</p></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/sc2.jpg" />
				<p>图片展示2</p></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/sc3.jpg" />
				<p>图片展示3</p></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/sc4.jpg" />
				<p>图片展示4</p></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/sc5.jpg" />
				<p>图片展示5</p></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/sc6.jpg" />
				<p>图片展示6</p></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/sc1.jpg" />
				<p>图片展示7</p></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/sc2.jpg" />
				<p>图片展示8</p></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/sc3.jpg" />
				<p>图片展示9</p></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/sc4.jpg" />
				<p>图片展示10</p></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/sc5.jpg" />
				<p>图片展示11</p></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/sc6.jpg" />
				<p>图片展示12</p></li>
			</ul>
		</div>
	</div>
</body>
</html>

