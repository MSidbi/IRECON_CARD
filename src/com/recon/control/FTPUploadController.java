package com.recon.control;

import static com.recon.util.GeneralUtil.ERROR_MSG;
import static com.recon.util.GeneralUtil.SUCCESS_MSG;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.model.FileSourceBean;
import com.recon.model.LoginBean;
import com.recon.model.UserBean;
import com.recon.service.IFileSourceService;

@Controller
@SuppressWarnings({"unused"})
public class FTPUploadController {

@Autowired IFileSourceService fileSourceService;

private static final Logger logger = Logger.getLogger(FTPUploadController.class);
	

private static final String ERROR_MSG = "error_msg";
private static final String SUCCESS_MSG = "success_msg";

@RequestMapping(value = "FtpUpload", method = RequestMethod.GET)
public String userManager(Model model , FileSourceBean ftpBean, HttpSession httpSession) {
	try {
		
		

		//System.out.println(((LoginBean) httpSession.getAttribute("loginBean")).getUser_id());

		List<FileSourceBean> ftpFileList =fileSourceService.getFileList();
		model.addAttribute("ftpFileList", ftpFileList);
		model.addAttribute("ftpBean", ftpBean);
		
		return "FtpUpload";
	
	} catch (Exception e) {
		
		logger.error(e.getMessage());
		model.addAttribute("ftpFileList", null);
		return "FtpUpload";
	}
}

@RequestMapping(value = "UploadFtpFile", method = RequestMethod.POST)
public String uploadFTPFile(@ModelAttribute("ftpBean") @Valid FileSourceBean ftpBean, BindingResult bindingResult, Model model, HttpServletRequest request, HttpSession httpSession,RedirectAttributes redirectAttributes ) {
	try {

		if(bindingResult.hasErrors()){
			
			System.out.println(bindingResult.toString());
		}
		FileSourceBean daoftpBean = null;
		boolean result = false;

		daoftpBean = fileSourceService.getFTPDetails(ftpBean.getFileId()); // returns FTPBEAN

		if (daoftpBean != null) {
			
			System.out.println(daoftpBean.getFtpUser());
			System.out.println(daoftpBean.getFtpPwd());
			

			result = fileSourceService.uplodFTPFile(daoftpBean); // Upload File From Ftp server to Bank server
			
			if(result== true) {
				
				
				result = fileSourceService.uplodData(daoftpBean);
				
				if(result ==true){
					
					System.out.println("Data Uploaded successfully!!");
					redirectAttributes.addFlashAttribute(SUCCESS_MSG, "Data Uploaded successfully!!");
					
				}else{
					
					System.out.println("Error occurred while inserting a data.");
					redirectAttributes.addFlashAttribute(ERROR_MSG, "Error occurred while inserting a data.");
					
				}
				
				
			}else {
				
				System.out.println("File not uploaded from FTP server");
				redirectAttributes.addFlashAttribute(ERROR_MSG, "File not uploaded from FTP server.");
			}

		} else {

			System.out.println("FTP server Not found");
			redirectAttributes.addFlashAttribute(ERROR_MSG, "FTP server Not found");
		}
		
		
		return "redirect:FtpUpload.do";
	
		//return "FtpUpload";
	} catch (Exception e) {
		logger.error(e.getMessage());
		model.addAttribute(ERROR_MSG, e.getMessage());
		//model.addAttribute("userList", null);
		return "FtpUpload";
	}
}



}
