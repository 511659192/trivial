package com.github.zhangkaitao.shiro.chapter16.web.controller;

import com.alibaba.fastjson.JSON;
import com.github.zhangkaitao.shiro.chapter16.entity.Resource;
import com.github.zhangkaitao.shiro.chapter16.entity.User;
import com.github.zhangkaitao.shiro.chapter16.service.ResourceService;
import com.github.zhangkaitao.shiro.chapter16.service.UserService;
import com.github.zhangkaitao.shiro.chapter16.web.bind.annotation.CurrentUser;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-2-14
 * <p>Version: 1.0
 */
@Controller
public class IndexController {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String index(@CurrentUser User loginUser, Model model) {
    	Subject subject =
    	SecurityUtils.getSubject();
    	PrincipalCollection collection = subject.getPrincipals();
        Set<String> permissions = userService.findPermissions(loginUser.getUsername());
        List<Resource> menus = resourceService.findMenus(permissions);
        model.addAttribute("menus", menus);
        return "index";
    }

    @RequestMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    
    @RequestMapping("/json")
    @ResponseBody
    public String json(HttpServletRequest request) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("11", "11");
    	map.put("22", "22");
    	String key = request.getParameter("jsonpcallback");
    	String result = key + "(" + JSON.toJSONString(map)+")";
    	return result;
	}

    @RequestMapping("/json2")
    @ResponseBody
    public Map<String, Object> json2(HttpServletRequest request) {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("11", "11");
    	map.put("22", "22");
    	return map;
	}
}
