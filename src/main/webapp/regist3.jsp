<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%String contextPath = request.getContextPath();%>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8">
<meta name="keywords" content="图片轮播，图片切换，焦点图" />
<meta name="description" content="这是一个基于jquery的图片轮播效果演示页" />
<title>演示3：flexslider图片轮播、文字图片相结合滑动切换效果</title>
<jsp:include page="${contextPath}/jsp/inner.jsp"></jsp:include>
<link rel="stylesheet" type="text/css"
	href="http://www.helloweba.com/demo/flexslider/flexslider.css" />
<style type="text/css">
#registForm > div {
	width: 30%; 
	margin: 10px auto;
}
#registForm > ol{
	width: 50%; 
	text-align: center;
	margin: 15px auto;
	line-height: 20px;
}
#registForm > ol > li{
	line-height: 20px;
}
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
	src="http://www.helloweba.com/demo/flexslider/jquery.flexslider-min.js"></script>
<script type="text/javascript"
	src="http://www.helloweba.com/demo/flexslider/jquery.easing.min.js"></script>
<link rel="stylesheet" type="text/css"
	href="checkbox.css" />
<script type="text/javascript">
	$(function() {
		$(".flexslider2").flexslider({
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

		<div class="flexslider2 carousele>
			<ul class="slides">
				<li><input type="checkbox" id="cbtest_01" value="01"/><label for="cbtest_01" class="check-box"></label><img src="css/images/portrait/01.png" /></li>
				<li><input type="checkbox" id="cbtest_02" value="02"/><label for="cbtest_02" class="check-box"></label><img src="css/images/portrait/02.png" /></li>
				<li><input type="checkbox" id="cbtest_03" value="03"/><label for="cbtest_03" class="check-box"></label><img src="css/images/portrait/03.png" /></li>
				<li><input type="checkbox" id="cbtest_04" value="04"/><label for="cbtest_04" class="check-box"></label><img src="css/images/portrait/04.png" /></li>
				<li><input type="checkbox" id="cbtest_05" value="05"/><label for="cbtest_05" class="check-box"></label><img src="css/images/portrait/05.png" /></li>
				<li><input type="checkbox" id="cbtest_06" value="06"/><label for="cbtest_06" class="check-box"></label><img src="css/images/portrait/06.png" /></li>
				<li><input type="checkbox" id="cbtest_07" value="07"/><label for="cbtest_07" class="check-box"></label><img src="css/images/portrait/07.png" /></li>
				<li><input type="checkbox" id="cbtest_08" value="08"/><label for="cbtest_08" class="check-box"></label><img src="css/images/portrait/08.png" /></li>
				<li><input type="checkbox" id="cbtest_09" value="09"/><label for="cbtest_09" class="check-box"></label><img src="css/images/portrait/09.png" /></li>
				<li><input type="checkbox" id="cbtest_10" value="10"/><label for="cbtest_10" class="check-box"></label><img src="css/images/portrait/10.png" /></li>
				<li><input type="checkbox" id="cbtest_11" value="11"/><label for="cbtest_11" class="check-box"></label><img src="css/images/portrait/11.png" /></li>
				<li><input type="checkbox" id="cbtest_12" value="12"/><label for="cbtest_12" class="check-box"></label><img src="css/images/portrait/12.png" /></li>
				<li><input type="checkbox" id="cbtest_13" value="13"/><label for="cbtest_13" class="check-box"></label><img src="css/images/portrait/13.png" /></li>
				<li><input type="checkbox" id="cbtest_14" value="14"/><label for="cbtest_14" class="check-box"></label><img src="css/images/portrait/14.png" /></li>
				<li><input type="checkbox" id="cbtest_15" value="15"/><label for="cbtest_15" class="check-box"></label><img src="css/images/portrait/15.png" /></li>
				<li><input type="checkbox" id="cbtest_16" value="16"/><label for="cbtest_16" class="check-box"></label><img src="css/images/portrait/16.png" /></li>
				<li><input type="checkbox" id="cbtest_17" value="17"/><label for="cbtest_17" class="check-box"></label><img src="css/images/portrait/17.png" /></li>
				<li><input type="checkbox" id="cbtest_18" value="18"/><label for="cbtest_18" class="check-box"></label><img src="css/images/portrait/18.png" /></li>
			</ul>
		</div>
</body>
</html>
