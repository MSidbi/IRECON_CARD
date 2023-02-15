package com.recon.interceptor;

import static com.recon.util.GeneralUtil.ERROR_MSG;
import static com.recon.util.GeneralUtil.SUCCESS_MSG;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.recon.model.LoginBean;
import com.recon.model.RoleBean;
import com.recon.model.SessionModel;
import com.recon.model.UserBean;
import com.recon.service.UserService;
import com.recon.util.CSRFToken;

@SuppressWarnings("unused")
public class LoginInterceptorOg implements HandlerInterceptor {

	private static final Logger logger = Logger.getLogger(LoginInterceptorOg.class);

	@Autowired
	UserService userService;

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception exception) throws Exception {
		if(request.getSession().getAttribute(ERROR_MSG) != null){
			request.getSession().removeAttribute(ERROR_MSG);
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		String uri = request.getRequestURI();
		//System.out.println("uri is "+uri);
		if (!uri.equals("/IRECON/Login.do") && !uri.equals("/IRECON/") && !uri.equals("/IRECON/Logout.do") && !uri.equals("/IRECON/InvalidateSession.do") 
				&&  !uri.equals("/IRECON/closeSession.do")  && !uri.equals("/IRECON/CloseUserSession.do")) {

			LoginBean loginBean;
			//SessionModel sessionmodel = new SessionModel();
			//sessionmodel.setReq(request);
			 // System.out.println("interceptor Session"+SessionModel.req.getSession());
			/**Check if Session is Available.*/
			 /*if (request.getMethod().equalsIgnoreCase("POST") ) {
				 String sessionToken = CSRFToken.getTokenForSession(request.getSession());
			       String requestToken = CSRFToken.getTokenFromRequest(request);
			       
			 
			       
			       if (sessionToken.equals(requestToken)) {
			    	   
			         } else {
				         response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bad or missing CSRF value");
				         return false;
				      }
			 }*/
			
			
			
			
			
			try{
				/**Check if User Session Attribute is Available. */
				loginBean = (LoginBean) request.getSession(false).getAttribute("loginBean");
				if (loginBean == null) {
					throw new Exception("Invalid Session, Login to continue..");
				}
			}catch(Exception e){
				logger.error(e.getMessage());
				request.getSession().setAttribute(ERROR_MSG, e.getMessage());
				response.sendRedirect("Login.do");
				return false;
			}

			/**Check for Session Time Out. */
			try{
				
				if(Integer.parseInt(new SimpleDateFormat("mm").format(new Date())) - Integer.parseInt(new SimpleDateFormat("mm").format(new Date(request.getSession().getLastAccessedTime()))) > 120){
					UserBean userBean = new UserBean();
					userBean.setUser_id(loginBean.getUser_id());
					userService.endUserSession(userBean);
					request.getSession().invalidate();

					throw new Exception("Session Expired.");
				}
			}catch(Exception e){
				logger.error(e.getMessage());
				request.getSession().removeAttribute("loginBean");
				request.getSession().setAttribute(ERROR_MSG, e.getMessage());
				response.sendRedirect("Login.do");
				return false;
			}

			/**Check Current Login Status.(If any external interference is Occured, eg. from DBA) */
			try{
				UserBean userBean = new UserBean();
				userBean.setUser_id(loginBean.getUser_id());
				Map<String, Object> user_map = userService.currentUserStatus(userBean);
				if(Integer.parseInt(user_map.get("count").toString()) == 0 ){
					throw new Exception("User Session Terminated.");
				}

				/**If new session is created by invalidating this session. */
				if(!(request.getSession().getId().trim().equals(user_map.get("session_id").toString().trim()))){
					throw new Exception("Session Terminated, Please Re-Login to continue.");
				}
			}catch(Exception e){
				logger.error(e.getMessage());
				request.getSession().removeAttribute("loginBean");
				request.getSession().setAttribute(ERROR_MSG, e.getMessage());
				response.sendRedirect("Login.do");
				return false;
			}

			/**Check Authorised Access.*/
			try{
				RoleBean roleBean = new RoleBean();
				roleBean.setPage_url(uri.replace("/DebitCar_Recon/", ""));
				roleBean.setUser_id(loginBean.getUser_id());
				boolean status = true;//roleService.checkRole(roleBean);
			
				if(status == false){
					throw new Exception("Unauthorised Access..!");
				}
			}catch(Exception e){
				logger.error(e.getMessage());
				request.getSession().setAttribute(ERROR_MSG, e.getMessage());
				response.sendRedirect("Menu.do");
				return false;
			}
		}
		else if(uri.equals("/IRECON/Login.do")||uri.equals("/IRECON/"))
		{
			try
			{
			LoginBean loginBean;
			loginBean = (LoginBean) request.getSession(false).getAttribute("loginBean");
			if (loginBean != null) {
				request.getSession().invalidate();
				//return false;
			}
			}
			catch(Exception e)
			{
				System.out.println("exception is "+e);
			}
		}
		return true;
	}

}