package com.recon.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.recon.model.LoginBean;

@Controller
public class LoginInterface {
	@RequestMapping(value = "IRECONMODULE", method = RequestMethod.POST)
	public String INTERFACE(HttpServletRequest request, HttpSession session, LoginBean loginBean) {
		System.out.println(" username" + request.getParameter("userName"));
		System.out.println(" SESSIONID" + request.getParameter("SESSIONID"));
		System.out.println(" IP ADDRESS" + request.getParameter("IPADDRESS"));
		loginBean.setSession_id(request.getParameter("SESSIONID"));
		loginBean.setIp_address(request.getParameter("IPADDRESS"));
		loginBean.setUser_id(request.getParameter("userName"));
		loginBean.setUser_name(request.getParameter("empname"));
		request.getSession().setAttribute("loginBean", loginBean);
		return "redirect:Menu.do";
	}
}
