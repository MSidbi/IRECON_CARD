package com.recon.control.rupyoffline;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.recon.service.rupayoffline.RupayOfflineUploadService;
import com.recon.util.rupayoffline.FILEPATH;
 

@Controller
public class RupayOfflineUploadController {

	@Autowired
	RupayOfflineUploadService service;
	
	@RequestMapping(value = "rupayofflinexml2xl", method = RequestMethod.GET)
	public   String rupayofflinexml2xlGet(HttpSession session, HttpServletResponse response, HttpServletRequest req, Model model ) {
	
		 return "rupayoffline";
	}
	
	@RequestMapping(value = "rupayofflinexml2xl", method = RequestMethod.POST)
	public void   rupayofflinexml2xlPost(HttpSession session, HttpServletResponse response, HttpServletRequest req, Model model, @RequestParam("attachment") MultipartFile file) {
 
		try {
		 service.readOfflineXml(file.getInputStream());
		} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("message", e.getMessage());
	    }
		
		ServletOutputStream sou = null;
		 
		try {
			System.out.println("File Received");
		 	response.setContentType("application/octet-stream");
	        response.setHeader("Content-Disposition","attachment; filename= "+FILEPATH.filepath);
	        FileInputStream ins = new FileInputStream(new File(FILEPATH.filepath));
	        sou = response.getOutputStream();
	        sou.write(IOUtils.toByteArray(ins));
	        ins.close();
	        sou.flush();
			sou.close();
		} catch (Exception e) {
			//	e.printStackTrace();
	    }finally{
	    		try {
	    			new File(FILEPATH.filepath).delete();
				} catch (Exception e2) {
				}
	    }
		
	}
}
