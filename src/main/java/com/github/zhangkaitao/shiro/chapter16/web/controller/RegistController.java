package com.github.zhangkaitao.shiro.chapter16.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.zhangkaitao.shiro.chapter16.entity.User;

@Controller
public class RegistController {
	@RequestMapping(value = "/regist")
    public String regist(HttpServletRequest request, Model model) {
        return "regist";
    }
	
	@RequestMapping(value = "doRegist")
	@ResponseBody
	public String doRegist(User user, HttpServletRequest request, Model model) {
		System.out.println(user);
		return "faefafeafe";
	}
}
