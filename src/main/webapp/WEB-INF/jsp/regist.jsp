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

.demo {
	width: 800px;
	margin: 20px auto
}

.warn {
	margin: 10px auto;
	display: block;
}

</style>
<script type="text/javascript"
	src="http://www.helloweba.com/demo/flexslider/jquery.flexslider-min.js"></script>
<script type="text/javascript"
	src="http://www.helloweba.com/demo/flexslider/jquery.easing.min.js"></script>
<script type="text/javascript">
	$(function() {
		$("#protraitDiv").flexslider({
			animation : "slide",
			animationLoop : false,
			controlNav : false,
			itemWidth : 120,
			itemMargin : 5,
			minItems : 2,
			maxItems : 9
		// pausePlay: true
		});
	});
</script>
</head>
<body>
	
	<div style="width: 90%; margin: auto; background-color: white">
		<h4 class="text-center text-info">
			请认真填写以下几个必填项. 
		</h4>
		<form id="registForm" class="form-horizontal">
			<div class="control-group">
				<label class="control-label" for="username"><span class="c1">*</span>用户名</label>
				<div class="controls">
					<input name="username" type="text" />
				</div>
				
			</div>
			<ol>
				<p>您的网上尊称即您准备起的网名，请不要使用特殊字符，最下不短于3个字符，最长为10个字符</p>
				<li>
					请勿以党和国家领导人或其他名人的真实姓名、字、号、艺名、笔名注册。
				</li>
				<li>
					个人网友的用户名申请中请勿包含国家组织机构或者其他组织机构名称。
				</li>
				<li>
					请勿注册和使用与其他网友相同、相仿的用户名。
				</li>
				<li>
					请勿注册和使用不文明、不健康的用户名。
				</li>
				<li>
					请勿注册和使用易产生歧义、引起他人误解或者带有各种奇形怪状符号的名称。
				</li>
			</ol>
			<div class="control-group">
				 <label class="control-label" for="password"><span class="c1">*</span>密码</label>
				<div class="controls">
					<input name="password" type="password" />
				</div>
			</div>
			<div class="control-group">
				 <label class="control-label" for="reInputPassword"><span class="c1">*</span>重复密码</label>
				<div class="controls">
					<input id="reInputPassword" type="password" />
				</div>
			</div>
			<p style="display: block; text-align: center;">密码必输使用英文字符与数字的组合，当然也可以包括特殊符号、汉字等，过于简单的密码系统不会接受</p>
			<div class="control-group">
				 <label class="control-label" for="email"><span class="c1">*</span>Email</label>
				<div class="controls">
					<input name="email" type="email" />
				</div>
			</div>
			<div class="control-group">
				 <label class="control-label" for="mobile"><span class="c1">*</span>手机</label>
				<div class="controls">
					<input name="mobile" type="text" />
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="city"><span class="c1">*</span>城市地区</label>
				<div class="controls">
					<select name="city">
						<option value=""></option>
				        <option value="01">北京</option>
						<option value="02">上海</option>
						<option value="03">天津</option>
						<option value="04">重庆</option>
						<option value="05">安徽</option>
						<option value="06">甘肃</option>
						<option value="07">福建</option>
						<option value="08">广东</option>
						<option value="09">广西</option>
						<option value="10">贵州</option>
						<option value="11">海南</option>
						<option value="12">河北</option>
						<option value="13">河南</option>
						<option value="14">黑龙江</option>
						<option value="15">湖北</option>
						<option value="16">湖南</option>
						<option value="17">吉林</option>
						<option value="18">江苏</option>
						<option value="19">江西</option>
						<option value="20">辽宁</option>
						<option value="21">宁夏</option>
						<option value="22">青海</option>
						<option value="23">山东</option>
						<option value="24">山西</option>
						<option value="25">陕西</option>
						<option value="26">四川</option>
						<option value="27">西藏</option>
						<option value="28">新疆</option>
						<option value="29">云南</option>
						<option value="30">浙江</option>
						<option value="31">香港</option>
						<option value="32">澳门</option>
						<option value="33">台湾</option>
						<option value="34">日本</option>
						<option value="35">韩国</option>
						<option value="36">新加坡</option>
						<option value="37">加拿大</option>
						<option value="38">美国</option>
						<option value="39">其他</option>
						<option value="40">内蒙古</option>
				    </select>
			    </div>
			</div>
			<div class="control-group">
				 <label class="control-label" for="captcha"><span class="c1">*</span>验证码</label>
				<div class="controls">
					<input id="captcha" type="text" />
				</div>
			</div>
			<p style="display: block; text-align: center;">请选择一个头像</p>
			<div id="protraitDiv" class="flexslider" style="width: 62%; box-sizing: border-box; margin: auto">
				<ul class="slides">
					<li><input type="checkbox" value="01"/><img style="width: 80px; display: inline" src="css/images/portrait/01.png" /></li>
					<li><input type="checkbox" value="02"/><img style="width: 80px; display: inline" src="css/images/portrait/02.png" /></li>
					<li><input type="checkbox" value="03"/><img style="width: 80px; display: inline" src="css/images/portrait/03.png" /></li>
					<li><input type="checkbox" value="04"/><img style="width: 80px; display: inline" src="css/images/portrait/04.png" /></li>
					<li><input type="checkbox" value="05"/><img style="width: 80px; display: inline" src="css/images/portrait/05.png" /></li>
					<li><input type="checkbox" value="06"/><img style="width: 80px; display: inline" src="css/images/portrait/06.png" /></li>
					<li><input type="checkbox" value="07"/><img style="width: 80px; display: inline" src="css/images/portrait/07.png" /></li>
					<li><input type="checkbox" value="08"/><img style="width: 80px; display: inline" src="css/images/portrait/08.png" /></li>
					<li><input type="checkbox" value="09"/><img style="width: 80px; display: inline" src="css/images/portrait/09.png" /></li>
					<li><input type="checkbox" value="10"/><img style="width: 80px; display: inline" src="css/images/portrait/10.png" /></li>
					<li><input type="checkbox" value="11"/><img style="width: 80px; display: inline" src="css/images/portrait/11.png" /></li>
					<li><input type="checkbox" value="12"/><img style="width: 80px; display: inline" src="css/images/portrait/12.png" /></li>
					<li><input type="checkbox" value="13"/><img style="width: 80px; display: inline" src="css/images/portrait/13.png" /></li>
					<li><input type="checkbox" value="14"/><img style="width: 80px; display: inline" src="css/images/portrait/14.png" /></li>
					<li><input type="checkbox" value="15"/><img style="width: 80px; display: inline" src="css/images/portrait/15.png" /></li>
					<li><input type="checkbox" value="16"/><img style="width: 80px; display: inline" src="css/images/portrait/16.png" /></li>
					<li><input type="checkbox" value="17"/><img style="width: 80px; display: inline" src="css/images/portrait/17.png" /></li>
					<li><input type="checkbox" value="18"/><img style="width: 80px; display: inline" src="css/images/portrait/18.png" /></li>
				</ul>
			</div>	
			
			<input name="protrait" type="hidden"></input>
			
			<div style="width: 62%; box-sizing: border-box; margin: 10px auto; padding-left: 0px; padding-right: 8px;">
				<textarea style="height: 150px; width: 100%">
					<jsp:include page="contract.jsp"></jsp:include>
				</textarea>
			</div>
			
			<div class="control-group">
				<div class="controls">
<!-- 					 <button type="submit" class="btn" id="registBtn">登陆1</button> -->
					<p id="registBtn">afeafe</p>
				</div>
			</div>
		</form>
	</div>
<script type="text/javascript">
$(function(){
	$("#protraitDiv input[type=checkbox]").on("change", function(){
		if($(this).is(":checked")) {
			var that = $(this);
			that.parent().siblings().children("input[type=checkbox]").removeAttr("checked");
			$("[name=protrait]").val(that.val())
		}
	})
	
	$("#protraitDiv input:eq(0)").prop("checked", true).parent().siblings().children("input[type=checkbox]").removeAttr("checked");
	$("[name=protrait]").val("01")
	
	
	$("#registBtn").on("click", function(){
		var availed = true;
		var username = $("[name=username]").val();
		if(!username || username.trim() == "") {
			$("[name=username]").after("<span class='c1 warn'>用戶名不能为空！</span>")
			availed =false;
		} else {
			$("[name=username]").siblings("span").remove();
		}
		
		var password = $("[name=password]").val();
		if(!password || password.trim() == "") {
			$("[name=password]").after("<span class='c1 warn'>密码不能为空！</span>")
			availed =false;
		} else {
			$("[name=password]").siblings("span").remove();
		}
		
		var reInputPassword = $("#reInputPassword").val();
		console.info(reInputPassword)
		if(!reInputPassword || reInputPassword.trim() == "") {
			$("#reInputPassword").after("<span class='c1 warn'>重复密码不能为空！</span>")
			availed =false;
		} else {
			$("#reInputPassword").siblings("span").remove();
		}
		
		if(password != reInputPassword) {
			$("#reInputPassword").after("<span class='c1 warn'>密码输入不一致！</span>")
			availed =false;			
		} else {
			$("#reInputPassword").siblings("span").remove();
		}
		
		var email = $("[name=email]").val();
		if(!email || email.trim() == "") {
			$("[name=email]").after("<span class='c1 warn'>邮箱不能为空！</span>")
			availed =false;
		} else {
			$("[name=email]").siblings("span").remove();
		}
		
		var mobile = $("[name=mobile]").val();
		if(!mobile || mobile.trim() == "") {
			$("[name=mobile]").after("<span class='c1 warn'>用户手机号码不能为空！</span>")
			availed =false;
		} else {
			$("[name=mobile]").siblings("span").remove();
		}
		
		var city = $("[name=city]").val();
		if(!city || city.trim() == "") {
			$("[name=city]").after("<span class='c1 warn'>地区不能为空！</span>")
			availed =false;
		} else {
			$("[name=city]").siblings("span").remove();
		}
		
		if(availed) {
			
			$.post("/mall/doRegist", $("#registForm").serialize() ,
					   function(data){
					     alert("Data Loaded: " + data);
					   });
		}
		
	})
	
})
	
</script>
</body>
</html>
