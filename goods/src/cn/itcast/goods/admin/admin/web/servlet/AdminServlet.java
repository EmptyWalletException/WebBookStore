package cn.itcast.goods.admin.admin.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.admin.admin.domain.Admin;
import cn.itcast.goods.admin.admin.service.AdminService;
import cn.itcast.servlet.BaseServlet;

public class AdminServlet extends BaseServlet {
	private AdminService adminService = new AdminService();
	
	public String login(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//封装表单数据到Admin对象中,方便传给Service;
		Admin form = CommonUtils.toBean(req.getParameterMap(), Admin.class);
		Admin admin = adminService.login(form);
		if(null == admin){
			req.setAttribute("msg", "用户名或密码错误!");
			return "/adminjsps/login.jsp";
		}
		req.getSession().setAttribute("admin", admin);//将查询到数据保存到session中;
		return "r:/adminjsps/admin/index.jsp";//重定向到主页;此处如果是转发则不需要保存到session,只需保存到req里;
	}

}
