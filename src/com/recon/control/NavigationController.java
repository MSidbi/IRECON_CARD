package com.recon.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.model.LoginBean;
import com.recon.model.NavigationBean;
import com.recon.model.ProcessDtlBean;
import com.recon.service.LoginService;
import com.recon.service.NavigationService;
import com.recon.util.CSRFToken;



@Controller
@SuppressWarnings({"unused"})
public class NavigationController {

	@Autowired
	NavigationService navigationService;
	
	@Autowired
	LoginService loginService;

	Logger logger = Logger.getLogger(NavigationController.class);

	/** Response Constants */
	private static final String ERROR_MSG = "error_msg";
	private static final String SUCCESS_MSG = "success_msg";
	

	/**
	 * View Menu Page Formed by Roles assigned to Individual User. 
	 * @param loginBean
	 * @param navigationBean
	 * @param request
	 * @param redirectAttributes
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/Menu")
	public String getMenu(LoginBean loginBean, NavigationBean navigationBean, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes, HttpSession httpSession) throws Exception {
		try {

			System.out.println("MENU.DO");
			/** Filling Current user in ModelAttribute */
			loginBean.setUser_id(((LoginBean) request.getSession().getAttribute("loginBean")).getUser_id().trim());

			List<NavigationBean> menu = new ArrayList<NavigationBean>();
			List <ProcessDtlBean> uploadDtlBeans = loginService.getProcessdtls("UPLOAD_FLAG");
			List<ProcessDtlBean> compareDtlbean = loginService.getProcessdtls("COMAPRE_FLAG");
			
			
			if(menu.size() == 0){
				/*loginService.invalidateUser(loginBean);
				httpSession.invalidate();
				throw new Exception("No Roles have been assigned yet, please contact DBA/System Admin.");*/
				model.addAttribute(ERROR_MSG, "No Roles have been assigned yet, please contact DBA/System Admin.");

			}

			System.out.println(menu);
			navigationBean.setMenu(menu);
			request.getSession().setAttribute("navigationBean", navigationBean);
			
			String csrf = CSRFToken.getTokenForSession(request.getSession());
			 
			redirectAttributes.addFlashAttribute("CSRFToken", csrf);
			model.addAttribute("CSRFToken", csrf);
			model.addAttribute("UploadBean", uploadDtlBeans);
			model.addAttribute("CompareBean", compareDtlbean);
			return "Menu";
		} catch (Exception e) {
			logger.error(e.getMessage());
			redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
			return "redirect:Login.do";
		}
	}
	
	/**
	 * View Admin Menu Page.
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value = "AdminMenu", method = RequestMethod.GET)
	public ModelAndView administrator(ModelAndView modelAndView) {
		modelAndView.setViewName("AdminMenu");
		return modelAndView;
	}
	@RequestMapping(value = "Master_chargeback", method = RequestMethod.GET)
	public ModelAndView Master_chargeback(ModelAndView modelAndView) {
		modelAndView.setViewName("Master_chargebk");
		return modelAndView;
	}
	/**
	 * View Configuration Menu Page.
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value = "ConfigurationMenu", method = RequestMethod.GET)
	public ModelAndView Configuration(ModelAndView modelAndView) {
		modelAndView.setViewName("ConfigurationMenu");
		return modelAndView;

	}
	
	
	@RequestMapping(value = "Gl_balancing", method = RequestMethod.GET)
	public ModelAndView Gl_balancing(ModelAndView modelAndView) {
		modelAndView.setViewName("Gl_balancing");
		return modelAndView;
	}
	
}
