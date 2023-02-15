package com.recon.control;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.model.LoginBean;
import com.recon.model.SessionModel;
import com.recon.model.UserBean;
import com.recon.service.LoginService;
import com.recon.util.demo;

@Controller
public class LogoutController {

	@Autowired
	LoginService loginService;

	private static final Logger logger = Logger.getLogger(LoginController.class);

	/** Response Constants */
	private static final String ERROR_MSG = "error_msg";
	private static final String SUCCESS_MSG = "success_msg";

	@RequestMapping(value = "Logout")
	public String logout(LoginBean loginBean, HttpSession httpSession,HttpServletRequest req, RedirectAttributes redirectAttributes, SessionModel sessionmodel) throws Exception {
		try {

			logger.info("***** LogoutController.logout Start ****");
			
			loginBean = (LoginBean) httpSession.getAttribute("loginBean");

			/** Update Login Master. */
			loginService.invalidateUser(loginBean);
			
			try {
				//sessionmodel.getReq().getSession(false).invalidate();
				//sessionmodel.setReq(null);
				  //System.out.println("logout Session"+SessionModel.req.getSession());
				 // req.getSession(false).invalidate();
				httpSession.invalidate();
			} catch (Exception e) {
				// TODO: handle exception
			}

			/** Invalidating User Session Object **/
			//httpSession.invalidate();
			
			redirectAttributes.addFlashAttribute("success_msg", "Logged Out Successfully.");
			
			logger.info("***** LogoutController.logout End ****");
			
			return "redirect:Login.do";

		} catch (Exception e) {
			demo.logSQLException(e, "LogoutController.logout");
			logger.error(" error in LogoutController.logout", new Exception("LogoutController.logout",e));
			redirectAttributes.addFlashAttribute("error_msg", "Error Logging Out.");
			return "redirect:Login.do";

		}
	}

	@RequestMapping(value = "InvalidateSession")
	public String invalidateSession(@RequestParam("user_id") String user_id, LoginBean loginBean, HttpSession httpSession, RedirectAttributes redirectAttributes) throws Exception {
		try {
			
			/**Filling Parameters in User Bean. */
			loginBean.setUser_id(user_id);

			loginService.invalidateUser(loginBean);

			/** Invalidating User Session Object **/
			httpSession.invalidate();

			redirectAttributes.addFlashAttribute(SUCCESS_MSG, "Login to continue.");
			return "redirect:Login.do";
		} catch (Exception e) {
			demo.logSQLException(e, "LogoutController.invalidateSession");
			logger.error(e.getMessage());
			redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
			return "redirect:Login.do";
		}
	}
	
	@RequestMapping(value = "closeSession" ,  method = RequestMethod.GET)
	public String closeSession(LoginBean loginBean,Model model,HttpServletRequest request, HttpSession httpSession, RedirectAttributes redirectAttributes) throws Exception {
		try {
			
			/**Filling Parameters in User Bean. */
			/*InetAddress IP=InetAddress.getLocalHost();
			System.out.println("ip address is "+IP.getHostAddress());*/
			String ipAddress = request.getRemoteAddr();
			loginBean.setIp_address(ipAddress);
			Map<String, Object> allSession_map =  (HashMap<String, Object>) loginService.getAllSession(loginBean);
			//loginService.closeSession(loginBean);
		//	System.out.println("total count is "+allSession_map.size());
			
			
			List<LoginBean> users_list = (List<LoginBean>) allSession_map.get("USER_LIST");
			if(users_list.size() == 0)
			{
				//redirectAttributes.addFlashAttribute(ERROR_MSG, "No users Logged in");
				//request.setAttribute("error", "users not logged in");
				throw new Exception("No Users Logged in");
			}

			//System.out.println("user id is.. "+users_list.get(0).getUser_id());
			
			/*redirectAttributes.addFlashAttribute(SUCCESS_MSG, "Login to continue.");*/
			model.addAttribute("users", users_list);
			return "BounceMyLogin";
		} catch (Exception e) {
			demo.logSQLException(e, "LogoutController.closeSession");
			logger.error(e.getMessage());
			request.setAttribute("error", e.getMessage());
			return "BounceMyLogin";
		}
	}

}
