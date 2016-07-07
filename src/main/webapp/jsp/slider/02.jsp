<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="keywords" content="图片轮播，图片切换，焦点图" />
<meta name="description" content="这是一个基于jquery的图片轮播效果演示页" />
<title>演示2：flexslider图片轮播、文字图片相结合滑动切换效果</title>
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

.slides h4 {
	height: 42px;
	line-height: 42px;
	font-size: 22px;
	opacity: .8;
}

.slides p {
	line-height: 22px;
	font-size: 16px
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
			slideshow : true
			
			
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
					src="http://www.helloweba.com/demo/flexslider/images/s1.jpg" alt="" />
					<div style="position: absolute; top: 70px; right: 200px;">
						<h4>FlexSlider!</h4>
						<p>多功能图片切换效果</p>
					</div></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/s2.jpg" alt="" />
					<div
						style="position: absolute; bottom: 0; right: 0; width: 100%; height: 42px; background: #000; text-indent: 20px; color: #fff">
						<h4>
							<a href="http://www.helloweba.com/view-blog-265.html"
								target="_blank">jquery flexslider滑块幻灯片插件图片和文字结合焦点图片切换</a>
						</h4>
					</div></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/s3.jpg" alt="" />
					<div
						style="position: absolute; top: 70px; right: 65px; width: 420px; padding: 10px; background: #333; opacity: .8; color: #fff">
						<h4>FlexSlider</h4>
						<p>
							FlexSlider是一款基于的jQuery内容滚动插件。它能让你轻松的创建内容滚动的效果，具有非常高的可定制性。它是将UL列表转换成内容滚动的列表，可以自动播放，或者使用导航按钮和键盘来控制。<a
								href="http://www.woothemes.com/flexslider/" target="_blank"
								rel="nofollow">FlexSlider官网</a> <a
								href="http://www.helloweba.com/view-blog-265.html"
								target="_blank">中文使用教程</a>
						</p>
					</div></li>
				<li><img
					src="http://www.helloweba.com/demo/flexslider/images/s4.jpg" alt="" />
					<div
						style="position: absolute; top: 20px; left: 200px; width: 520px;">
						<h4>FlexSlider</h4>
						<p>FlexSlider is a free responsive jQuery slider toolkit.
							Supported in all major browsers with custom navigation options
							and touch swipe support.</p>
					</div></li>
			</ul>
		</div>
	</div>
</body>
</html>

