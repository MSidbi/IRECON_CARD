package com.recon.control;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;















import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.recon.model.ConfigurationBean;
import com.recon.model.LoginBean;
import com.recon.service.LoginService;
//import com.recon.validation.LoginValidator;
import com.recon.util.demo;

@Controller
@RequestMapping(value = {"/Login","/"})
public class LoginController {

//	@Autowired
//	//LoginValidator loginValidator;
	
	@Autowired
	LoginService loginService;
	Logger logger =Logger.getLogger(LoginController.class);

	/** Response Constants */
	private static final String ERROR_MSG = "error_msg";
	StringBuilder error_string = new StringBuilder();

	//private static final Logger logger = Logger.getLogger(LoginController.class);

	/**
	 * View Login Page.
	 * @param modelAndView
	 * @param loginBean
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView init(ModelAndView modelAndView, LoginBean loginBean, HttpServletRequest request) {
		//System.out.println("HIE");
		modelAndView.setViewName("loginb");	
		modelAndView.addObject("login", loginBean);
		return modelAndView;
	}
	

	/**
	 * Execute Login Verification Process.
	 * @param loginBean
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception 
	 */	
	@RequestMapping(method = RequestMethod.POST)
//	public ModelAndView validateUser(@ModelAttribute("login") @Valid LoginBean loginBean, BindingResult bindingResult, Model model, HttpServletRequest request, HttpSession httpSession,RedirectAttributes redirect,ModelAndView modelAndView) {
	public String validateUser(@ModelAttribute("login") LoginBean loginBean , BindingResult bindingResult, Model model, HttpServletRequest request, HttpSession httpSession,RedirectAttributes redirect,ModelAndView modelAndView) throws Exception {
	
	try {

			logger.info("***** LoginController.validateuser Start ****");
			logger.debug("validateuser");
			
				
				
			
			//loginValidator.validate(loginBean, bindingResult);
			if (bindingResult.hasErrors()) {
				
				return "loginb";
			}
			if(loginBean.getProcessType().equalsIgnoreCase("DCRS")) {
			/** Validate Username and Password from AD.*/
			InetAddress IP=InetAddress.getLocalHost();
			
			loginBean.setPassword(new com.recon.util.UnwarpRequest(request.getParameter("salt"),request.getParameter("iv"),request.getParameter("passphrase"),request.getParameter("password")).decrypt() );
			//loginService.validateUser(loginBean); //commented ad login part
			loginBean.setSession_id(request.getSession().getId());
			loginBean.setIp_address(IP.getHostAddress());
			
			String ipAddress = request.getRemoteAddr();
			//System.out.println("NOW CHECK TH IP ADDRESS "+ipAddress);
			loginBean.setIp_address(ipAddress);

			/** Checking whether user has already logged in from ip **/
			/* boolean alreadyLoggerdIn = loginService.checkIp(loginBean); 
		//	System.out.println("ip is used ? "+alreadyLoggerdIn);
			if(alreadyLoggerdIn)
			{
				
				throw new Exception("Only One User Can log in from one System");
				
			}*/ //commented by sushant for short period 
			
			/**Fetch User Details  */
			loginBean = loginService.getUserDetail(loginBean);

			if(loginBean.getUser_status().equals("I")){
				throw new Exception("User Currently Inactive, Please contact DBA.");
			}

			/**Filling Currently Used Session Id in LoginBean. */
			loginBean.setSession_id(request.getSession().getId());
			//loginBean.setSession_id(request.getSession().getId()+loginBean.getUser_id());
			
			//System.out.println("session id in bean is "+loginBean.getSession_id());
			
			
			request.getSession().setAttribute("loginBean", loginBean);
			
			logger.info("***** LoginController.validateuser End ****");
			
			return "redirect:Menu.do";
			} 
			else if(loginBean.getProcessType().equalsIgnoreCase("ATMCIA")) {
				
				
				
				//return sb.toString();
				//return "redirect:http://10.143.11.52:8080/ATM-CIA/NewTest?message="+loginBean.getUser_id()+"&&message1="+loginBean.getPassword()+"";
				return "redirect:http://10.144.136.101:9090/ATM-CIA/NewTest?message="+loginBean.getUser_id()+"&&message1="+loginBean.getPassword()+"";
				
				
			}else {
				
				
				return "loginb";
			}
			
			
			
		} catch (Exception e) {
			httpSession.invalidate();
			//System.out.println("EXCEP "+e);
			//e.printStackTrace();
			//demo.logSQLException(e, "LoginController.validateUser");
			//logger.error(" error in LoginController.validateUser", new Exception("LoginController.validateUser",e));
			model.addAttribute(ERROR_MSG, e.getMessage().toString());
			return "loginb";
		}
	
		
		
	}
	
	/*@RequestMapping(method = RequestMethod.POST)
	public String validateUser(@ModelAttribute("login") @Valid LoginBean loginBean, BindingResult bindingResult, Model model, HttpServletRequest request, HttpSession httpSession) {
		try {

			logger.debug("validateuser");
			
			//loginValidator.validate(loginBean, bindingResult);
			if (bindingResult.hasErrors()) {
				return "Login";
			}
			*//** Validate Username and Password from AD.*//*
			InetAddress IP=InetAddress.getLocalHost();
			
			
			loginService.validateUser(loginBean);
			loginBean.setSession_id(request.getSession().getId());
			loginBean.setIp_address(IP.getHostAddress());
			
			String ipAddress = request.getRemoteAddr();
			//System.out.println("NOW CHECK TH IP ADDRESS "+ipAddress);
			loginBean.setIp_address(ipAddress);

			*//** Checking whether user has already logged in from ip **//*
			 boolean alreadyLoggerdIn = loginService.checkIp(loginBean); 
		//	System.out.println("ip is used ? "+alreadyLoggerdIn);
			if(alreadyLoggerdIn)
			{
				
				throw new Exception("Only One User Can log in from one System");
				
			} //commented by sushant for short period 
			
			*//**Fetch User Details  *//*
			loginBean = loginService.getUserDetail(loginBean);

			if(loginBean.getUser_status().equals("I")){
				throw new Exception("User Currently Inactive, Please contact DBA.");
			}

			*//**Filling Currently Used Session Id in LoginBean. *//*
			loginBean.setSession_id(request.getSession().getId());
			//loginBean.setSession_id(request.getSession().getId()+loginBean.getUser_id());
			
			//System.out.println("session id in bean is "+loginBean.getSession_id());
			
			
			request.getSession().setAttribute("loginBean", loginBean);
			
			return "redirect:Menu.do";
		} catch (Exception e) {
			httpSession.invalidate();
			System.out.println("EXCEP "+e);
			//logger.error(e.getMessage());
			model.addAttribute(ERROR_MSG, e.getMessage().toString());
			return "loginb";
		}
	}*/
	
	@RequestMapping(value="invalidBrowser" , method = RequestMethod.GET)
	public ModelAndView invalidBrowser(ModelAndView modelAndView, LoginBean loginBean, HttpServletRequest request) {
		System.out.println("HIE");
		modelAndView.setViewName("invalidBrowser");	
		
		return modelAndView;
	}
	 @RequestMapping(value = "errors", method = RequestMethod.GET)
	    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
	         
	        ModelAndView errorPage = new ModelAndView("errorPage");
	        String errorMsg = "";
	        int httpErrorCode = getErrorCode(httpRequest);
	 
	        switch (httpErrorCode) {
	            case 400: {
	                errorMsg = "Http Error Code: 400. Bad Request";
	                break;
	            }
	           
	        }
	        errorPage.addObject("errorMsg", errorMsg);
	        return errorPage;
	    }
	     
	    private int getErrorCode(HttpServletRequest httpRequest) {
	        return (Integer) httpRequest
	          .getAttribute("javax.servlet.error.status_code");
	    }

	
}
