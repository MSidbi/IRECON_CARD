package com.recon.control;

import java.util.List;

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

import com.recon.model.FileSourceBean;
import com.recon.model.LoginBean;
import com.recon.service.IFileSourceService;

@Controller

public class UpdateFileDetlsController {

	@Autowired IFileSourceService fileSourceService;
	
	private static final String ERROR_MSG = "error_msg";
	private static final String SUCCESS_MSG = "success_msg";
	private static final Logger logger = Logger.getLogger(UpdateFileDetlsController.class);
	
	@RequestMapping(value = "FileSource1", method = RequestMethod.GET)
	public ModelAndView fileSource(ModelAndView modelAndView) {
		modelAndView.setViewName("FileSource");
		return modelAndView;
	}
	
	
	@RequestMapping(value = "UpdateFileDtls1", method = RequestMethod.GET)
	public String fileDetlmngr(Model model , FileSourceBean ftpBean, HttpSession httpSession) {
		try {
			
			

			//System.out.println(((LoginBean) httpSession.getAttribute("loginBean")).getUser_id());

			//List<FTPBean> ftpFileList =fileSourceService.getFileList();
			//model.addAttribute("ftpFileList", ftpFileList);
			model.addAttribute("ftpBean", ftpBean);
			
			return "UpdtFileDetails";
		
		} catch (Exception e) {
			
			logger.error(e.getMessage());
			//model.addAttribute("ftpFileList", null);
			return "UpdtFileDetails";
		}
	}
	
	 @RequestMapping(value = "/searchspring1", method = RequestMethod.GET)
	    public String view(Model model) throws Exception {
		 FileSourceBean ftpBean  = new FileSourceBean();
		List<FileSourceBean> ftpBeanlist = 	fileSourceService.getFileDetails();
	       
		model.addAttribute("ftpBean", ftpBean);
		model.addAttribute("ftpBeanlist", ftpBeanlist);
	        return "gridHelp";
	    }
	 
	 @RequestMapping(value = "/saveFileData1", method = RequestMethod.POST)
		public String validateUser(@ModelAttribute("ftpBean") @Valid FileSourceBean ftpBean, BindingResult bindingResult, Model model, HttpServletRequest request, HttpSession httpSession,RedirectAttributes redirectAttributes) {
			try {

				System.out.println("1");
				//loginValidator.validate(loginBean, bindingResult);
				if (bindingResult.hasErrors()) {
					return "Login";
				}
				 boolean result =false;
				System.out.println(ftpBean.getFileId());
				result =fileSourceService.updateFileDetails(ftpBean);
				
				if(result) {
				
					redirectAttributes.addFlashAttribute(SUCCESS_MSG, "Record Updated successfully!!");
				
				}else{
					
					redirectAttributes.addFlashAttribute(ERROR_MSG, "Record not Updated !!");
					
				}
				
				
				return "redirect:UpdateFileDtls.do";
			} catch (Exception e) {
				httpSession.invalidate();
				//logger.error(e.getMessage());
				model.addAttribute(ERROR_MSG, e.getMessage().toString());
				redirectAttributes.addFlashAttribute(ERROR_MSG, "Error occurred while updating a record!!");
				return "redirect:UpdtFileDetails.do";
			}
		}
	
}
