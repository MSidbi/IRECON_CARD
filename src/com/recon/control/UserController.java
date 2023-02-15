package com.recon.control;

import static com.recon.util.GeneralUtil.ERROR_MSG;
import static com.recon.util.GeneralUtil.SUCCESS_MSG;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.model.LoginBean;
import com.recon.model.UserBean;
import com.recon.service.UserService;

@Controller
@SuppressWarnings({"unchecked"})
public class UserController {

	@Autowired
	UserService userService;

	private static final Logger logger = Logger.getLogger(UserController.class);

	/**
	 * Requesting UserMaanager with all Users Details.
	 * @param model
	 * @param userBean
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "UserManager", method = RequestMethod.GET)
	public String userManager(Model model, UserBean userBean, HttpSession httpSession) {
		try {

			/** Filling Current user in Model Bean Object. */
			userBean.setUser_id(((LoginBean) httpSession.getAttribute("loginBean")).getUser_id());

			List<UserBean> userList = userService.viewUser(userBean);
			model.addAttribute("userList", userList);
			return "UserManager";
		} catch (Exception e) {
			logger.error(e.getMessage());
			model.addAttribute(ERROR_MSG, e.getMessage());
			model.addAttribute("userList", null);
			return "UserManager";
		}
	}
	
	/**
	 * Requesting Modal Add User for Adding New User. 
	 * @param userBean
	 * @param modelAndView
	 * @return
	 */
	@RequestMapping(value="AddUser", method=RequestMethod.GET)
	public String addUser(UserBean userBean, Model model){
		model.addAttribute("user", userBean);
		return "ModalAddUser";
	}
	
	/**
	 * Adding New User Details. 
	 * @param userBean
	 * @param model
	 * @param request
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value="AddUser", method=RequestMethod.POST)
	public String addUserDetail(@ModelAttribute("user") UserBean userBean, Model model, HttpSession session, RedirectAttributes redirectAttributes){
		try{

			/** Filling Current user in Model Bean Object. */
			userBean.setEntry_by(((LoginBean) session.getAttribute("loginBean")).getUser_id());
			userBean.setUser_id(userBean.getUser_id().toUpperCase());
			userService.addUser(userBean);

			redirectAttributes.addFlashAttribute(SUCCESS_MSG, "New User Added Successfully.");
			return "redirect:UserManager.do";
		}catch(Exception e){
			redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
			return "redirect:UserManager.do";
		}
	}
	
	
	/**
	 * Modifying User Details.
	 * @param userBean
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param redirectAttributes
	 * @return
	 */

	@RequestMapping(value = "EditUser", method = RequestMethod.GET)
	public String editUser(@RequestParam("user_id") String user_id, UserBean userBean, Model model, HttpServletRequest request){
		try
		{
			userBean.setUser_id(user_id);
		
			userBean = userService.viewUserDetail(userBean);
			model.addAttribute("user", userBean);
			return "ModalEditUser";
		}
		catch(Exception e)
		{
			logger.error(e.getMessage());
			model.addAttribute("user", new UserBean());
			return "ModalEditUser";
		}
	}
	
	/**
	 * Modifying User Details.
	 * @param userBean
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value="EditUser", method=RequestMethod.POST)
	public String editUserDetail(@ModelAttribute("user") UserBean userBean, BindingResult bindingResult, Model model, HttpSession session, RedirectAttributes redirectAttributes ){
		try {
			//** Filling Current user in Model Bean Object. *//*
			userBean.setUpdt_by(((LoginBean) session.getAttribute("loginBean")).getUser_id());

			userService.modifyUser(userBean);

			redirectAttributes.addFlashAttribute(SUCCESS_MSG, "Record Modified Successfully.");
			return "redirect:UserManager.do";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
			return "redirect:UserManager.do";
		}
	}
	
	
	/**
	 * Requesting Delete User Modal for deleting an User.
	 * @param user_id
	 * @param userBean
	 * @param model
	 * @return
	 */
	@RequestMapping(value="DeleteUser", method=RequestMethod.GET)
	public String deleteUser(@RequestParam("user_id") String user_id, UserBean userBean, Model model){
		try{
			/**Filling request parameters in Model Bean Object. */
			userBean.setUser_id(user_id);
			model.addAttribute("user", userBean);
			return "ModalDeleteUser";
		}catch(Exception e){
			logger.error(e.getMessage());
			model.addAttribute("user", new UserBean());
			return "ModalDeleteUser";
		}
	}
	
	/**
	 * Deleting User Detail. 
	 * @param userBean
	 * @param request
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value="DeleteUser", method=RequestMethod.POST)
	public String deleteUserDetail(@ModelAttribute("user") UserBean userBean,HttpSession session, RedirectAttributes redirectAttributes){
		try{

			/** Filling Current user in Model Bean Object. */
			userBean.setUpdt_by(((LoginBean) session.getAttribute("loginBean")).getUser_id());

			userService.deleteUser(userBean);

			redirectAttributes.addFlashAttribute(ERROR_MSG, "User Deleted Successfully.");
			return "redirect:UserManager.do";
		}catch(Exception e){
			logger.error(e.getMessage());
			redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
			return "redirect:UserManager.do";
		}
	}

	/**
	 * Fetching & Displaying List of Currently Logged in Users.
	 * @param userBean
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="ModalLiveUser", method=RequestMethod.GET)
	public String modalLiveUser(UserBean userBean, Model model, HttpSession session){
		try{

			/** Filling Current user in User Bean Object. */
			userBean.setUser_id(((LoginBean) session.getAttribute("loginBean")).getUser_id());

			List<UserBean> liveUserList = userService.liveUser(userBean);
			if(liveUserList.size() == 0){
				throw new Exception("No Data Found.");
			}
			model.addAttribute("liveUserList", liveUserList);
			return "ModalLiveUser";
		}catch(Exception e){
			logger.error(e.getMessage());
			model.addAttribute("liveUserList", null);
			return "ModalLiveUser";
		}
	}
	
	/**
	 * Fetching and passing Current User details in JSON string. 
	 * @param userBean
	 * @param request
	 * @return
	 */
	@RequestMapping(value="GetCurrentUser", method=RequestMethod.POST)
	@ResponseBody
	public String getCurrentUser(UserBean userBean, HttpSession session){
		try{

			/** Filling Current user in User Bean Object. */
			userBean.setUser_id(((LoginBean) session.getAttribute("loginBean")).getUser_id());

			return userService.liveUserJSON(userBean);
		}catch(Exception e){
			logger.error(e.getMessage());
			return "";
		}
	}
	
	
	
	
	/**
	 * Terminating User Sesion.
	 * @param user_id
	 * @param userBean
	 * @param request
	 * @return
	 */
	@RequestMapping(value="EndUserSession", method=RequestMethod.POST)
	@ResponseBody
	public String endUserSession(@RequestParam("user_id") String user_id, UserBean userBean, HttpSession session){
		JSONObject jsonObject = new JSONObject();
		try{
			/**Filling Parameters in User Bean Object */
			userBean.setUser_id(user_id);

			/** Filling Current user in User Bean Object. */
			userBean.setUpdt_by(((LoginBean) session.getAttribute("loginBean")).getUser_id());

			userService.endUserSession(userBean);

			jsonObject.put("Result", "OK");
			jsonObject.put("Value", "Session Terminated Successfully.");
		}catch(Exception e){
			logger.error(e.getMessage());
			jsonObject.put("Result", "ERROR");
			jsonObject.put("Value", e.getMessage());
		}
		return jsonObject.toString();
	}
	
	/**
	 * Terminating User Sesion.
	 * @param user_id
	 * @param userBean
	 * @param request
	 * @return
	 */
	@RequestMapping(value="CloseUserSession", method=RequestMethod.POST)
	@ResponseBody
	public String closeUserSession(@RequestParam("user_id") String user_id,HttpServletRequest request, UserBean userBean, HttpSession session){
		JSONObject jsonObject = new JSONObject();
		try{
			/**Filling Parameters in User Bean Object */
			userBean.setUser_id(user_id);

			/** Filling Current user in User Bean Object. */
//			userBean.setUpdt_by(((LoginBean) session.getAttribute("loginBean")).getUser_id());
			

			userService.endUserSession(userBean);

			jsonObject.put("Result", "OK");
			jsonObject.put("Value", "Session Terminated Successfully.");
			
			/*request.setAttribute("error", "done");
			return "BounceMyLogin";*/
			
		}catch(Exception e){
			logger.error(e.getMessage());
			jsonObject.put("Result", "ERROR");
			jsonObject.put("Value", e.getMessage());
		}
		return jsonObject.toString();
	}
	
	
	@RequestMapping(value="UserLog", method=RequestMethod.GET)
	public String userLog(@RequestParam("user_id") String user_id, UserBean userBean, Model model){
		try{
			/**Filling Paramters in User Bean Object. */
			userBean.setUser_id(user_id);

			List<UserBean> user_log_list = userService.userLog(userBean);
			
			model.addAttribute("user_log_list", user_log_list);
			return "ModalUserLog";
		}catch(Exception e){
			model.addAttribute("user_log_list", "");
			return "ModalUserLog";
		}
	}
}
