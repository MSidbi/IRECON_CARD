package com.recon.control;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.model.FileSourceBean;
import com.recon.service.IFileSourceService;


@Controller

public class FileUploadController  {

	@Autowired IFileSourceService fileSourceService;
	

	private static final String ERROR_MSG = "error_msg";
	private static final String SUCCESS_MSG = "success_msg";
	private static final Logger logger = Logger.getLogger(FileUploadController.class);
	
	
	
	@RequestMapping(value = "FileUpload", method = RequestMethod.GET)
	public String fileUplodmngr(Model model , FileSourceBean ftpBean, HttpSession httpSession) {
		try {
			
			

			//System.out.println(((LoginBean) httpSession.getAttribute("loginBean")).getUser_id());

			List<FileSourceBean> ftpFileList =fileSourceService.getFileList();
			model.addAttribute("ftpFileList", ftpFileList);
			model.addAttribute("ftpBean", ftpBean);
			
			return "FileUpload";
		
		} catch (Exception e) {
			
			logger.error(e.getMessage());
			model.addAttribute("ftpFileList", null);
			return "FileUpload";
		}
	}
	
	@RequestMapping(value = "/uploadMultipleFile", method = RequestMethod.POST)
	
	public String uploadMultipleFileHandler(@ModelAttribute("ftpBean") @Valid FileSourceBean ftpBean,@RequestParam("mannFile") MultipartFile[] files,
			RedirectAttributes redirectAttributes) {

		String message = "";
		if (files.length !=0)
		{

		
		String name= "uploadfile";
	
			MultipartFile file = files[0];
			
			try {
				byte[] bytes = file.getBytes();
				
				boolean result =  fileSourceService.uploadFile(bytes, ftpBean.getFileId());
				
				if(result) {

					
					result = fileSourceService.uplodData(ftpBean);
					
					if(result ==true){
						
						System.out.println("Data Uploaded successfully!!");
						redirectAttributes.addFlashAttribute(SUCCESS_MSG, "Data Uploaded successfully!!");
						
					}else{
						
						System.out.println("Error occurred while inserting a data.");
						redirectAttributes.addFlashAttribute(ERROR_MSG, "Error occurred while inserting a data.");
						
					}
					//redirectAttributes.addFlashAttribute(SUCCESS_MSG, "File Uploaded successfully!!");
				}else {
					
					redirectAttributes.addFlashAttribute(ERROR_MSG, "File not uploaded.");
					
				}
						
			} catch (Exception e) {
				
				redirectAttributes.addFlashAttribute(ERROR_MSG, "Error occurred while uploading file.");
			}
		}
		return "redirect:FileUpload.do";
	}

}
