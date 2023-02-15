package com.recon.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.model.CompareSetupBean;
import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.VisaUploadBean;
import com.recon.service.ICompareConfigService;
import com.recon.service.VisaSettlementService;
import com.recon.util.CSRFToken;
import com.recon.util.GenerateUCOTTUM;

@Controller
public class VisaSettlementController {

	private static final Logger logger = Logger.getLogger(AdjustmentFileController.class);
	
	@Autowired ICompareConfigService icompareConfigService;
	
	@Autowired VisaSettlementService visaSettlementService;
	
	@RequestMapping(value = "VisaEPFileRead", method = RequestMethod.GET)
	public ModelAndView VisaEPFileReadGet(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
		logger.info("***** VisaSettlementController.Get Start ****");
		logger.info("VisaEPFileRead GET");
		String display="";
		VisaUploadBean visaUploadBean = new VisaUploadBean();
		
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
 		modelAndView.addObject("visaUploadBean",visaUploadBean);
		modelAndView.setViewName("ReadEPFile");
		
		logger.info("***** VisaSettlementController.VisaEPFileRead GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "VisaEPUpload", method = RequestMethod.POST)
	@ResponseBody
	public String VisaEPUploadPost(@ModelAttribute("visaUploadBean")  VisaUploadBean visaUploadBean,HttpServletRequest request,
			@RequestParam("file") MultipartFile file, HttpSession httpSession,
			RedirectAttributes redirectAttributes) throws Exception{
		logger.info("***** VisaSettlementController.Get Start ****");
		logger.info("VisaEPFileRead Post "+visaUploadBean.getFileDate());
		logger.info("VisaEPUpload Post : File Type is "+visaUploadBean.getFileType());
		logger.info("File name is  "+file.getOriginalFilename());
		String display="";
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		visaUploadBean.setCreatedBy(Createdby);
		
		if(visaUploadBean.getFileName().equalsIgnoreCase("EP"))
		{
			HashMap<String , Object> output = visaSettlementService.checkFileAlreadyUpload(visaUploadBean.getFileDate(), visaUploadBean.getFileType());

			if(output != null && (Boolean) output.get("result"))
			{
				boolean uploadFlag = visaSettlementService.uploadFile(visaUploadBean, file);

				if(uploadFlag)
				{
					return "File Uploaded Successfully";
				}
				else
				{
					return "File Not Uploaded";
				}
			}
			else
			{
				return output.get("msg").toString();
			}
		}
		else
		{
			HashMap<String , Object> output = visaSettlementService.checkJVUploaded(visaUploadBean);
			
			if(output != null && (Boolean) output.get("result"))
			{
				boolean uploadFlag = visaSettlementService.readJVFile(visaUploadBean, file);
				
				if(uploadFlag)
				{
					return "File Uploaded Successfully";
				}
				else
				{
					return "File not uploaded";
				}
			}
			else
			{
				return "JV File is already uploaded for selected date";
			}
			
		}
         
	}
	
	@RequestMapping(value = "VisaSettlementProces", method = RequestMethod.GET)
	public ModelAndView VisaSettlementProces(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
		logger.info("***** VisaSettlementController.VisaSettlementProces. Get Start ****");
		logger.info("VisaSettlementProces GET");
		String display="";
		VisaUploadBean visaUploadBean = new VisaUploadBean();
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
 		modelAndView.addObject("visaUploadBean",visaUploadBean);
		modelAndView.setViewName("VisaSettlementProcess");
		
		
		logger.info("***** VisaSettlementController.VisaSettlementProces GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "VisaSettlementProces", method = RequestMethod.POST)
	@ResponseBody
	public String VisaSettlementProcesPost(@ModelAttribute("visaUploadBean")  VisaUploadBean visaUploadBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes) throws Exception{
		logger.info("***** VisaSettlementController.VisaSettlementProces.Get Start ****");
		logger.info("VisaSettlementProces Post "+visaUploadBean.getFileDate());
		String display="";
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		visaUploadBean.setCreatedBy(Createdby);
		
		//CHECK WHETHER EP FILE IS UPLOADED
		boolean uploadStatus = visaSettlementService.checkFileUpload(visaUploadBean);
		
		//CHECK WHETHER SETTLEMENT IS ALREADY PROCESS
		if(uploadStatus)
		{
			HashMap<String, Object> output = visaSettlementService.checkSettlementProcess(visaUploadBean);
			
			if(output != null && (Boolean) output.get("result"))
			{
				boolean flag = visaSettlementService.runVisaSettlement(visaUploadBean);
				
				if(flag)
				{
					return "Processing Completed Successfully \n Download the reports";
				}
				else
				{
					return "Issue while processing settlement";
				}
			}
			else
			{
				return output.get("msg").toString();
			}
		}
		else
		{
			return "One of the Ep file is not uploaded for selected date";
		}
		
	}
	
	@RequestMapping(value = "DownloadVisaSettlement", method = RequestMethod.POST)
	@ResponseBody
	public String DownloadVisaSettlement(@ModelAttribute("visaUploadBean")  VisaUploadBean visaUploadBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes) throws Exception{
		logger.info("***** VisaSettlementController.VisaSettlementProces.Get Start ****");
		logger.info("VisaSettlementProces Post "+visaUploadBean.getFileDate());
		String display="";
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		visaUploadBean.setCreatedBy(Createdby);
		
		//CHECK WHETHER SETTLEMENT IS PROCESSED
		HashMap<String, Object> output = visaSettlementService.checkSettlementProcess(visaUploadBean);
		
		if(output != null && !(Boolean) output.get("result"))
		{
			return "success";
		}
		else
		{
			return "Settlement is not processed for selected date.";
		}
		
		
	}
	
	@RequestMapping(value = "DownloadVisaSettlementReport", method = RequestMethod.POST)
	public String DownloadVisaSettlementReport(@ModelAttribute("visaUploadBean")  VisaUploadBean visaUploadBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception{
		logger.info("***** VisaSettlementController.VisaSettlementProces.Get Start ****");
		logger.info("VisaSettlementProces Post "+visaUploadBean.getFileDate());
		String display="";
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		visaUploadBean.setCreatedBy(Createdby);
		List<Object> Excel_data = new ArrayList<Object>();
		
		Excel_data = visaSettlementService.getSettlementData(visaUploadBean);
		
		model.addAttribute("ReportName", "Visa_Settlement_Report");
		model.addAttribute("data", Excel_data);
		
		logger.info("***** VisaSettlementController.DownloadVisaSettlementReport POST End ****");
		return "GenerateVisaSettlementReport";
		
		
		
	}
	
/***************** VISA SETTLEMENT TTUM **********************/
	@RequestMapping(value = "VisaSettlementTTUM", method = RequestMethod.GET)
	public ModelAndView VisaSettlementTTUM(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
		logger.info("***** VisaSettlementController.VisaSettlementProces. Get Start ****");
		logger.info("VisaSettlementProces GET");
		String display="";
		VisaUploadBean visaUploadBean = new VisaUploadBean();
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
 		modelAndView.addObject("visaUploadBean",visaUploadBean);
		modelAndView.setViewName("VisaSettlementTTUM");
		
		
		logger.info("***** VisaSettlementController.VisaSettlementProces GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "VisaSettlementTTUMProces", method = RequestMethod.POST)
	@ResponseBody
	public String VisaSettlementTTUMProces(@ModelAttribute("visaUploadBean")  VisaUploadBean visaUploadBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes) throws Exception{
		logger.info("***** VisaSettlementController.VisaSettlementTTUMProces.Get Start ****");
		logger.info("VisaSettlementTTUMProces Post "+visaUploadBean.getFileDate());
		String display="";
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		visaUploadBean.setCreatedBy(Createdby);
		
		//1. VALIDATE WHETHER TTUM IS ALREADY PROCESSED
		HashMap<String, Object> output =  visaSettlementService.CheckTTUMProcessed(visaUploadBean);
		
		if(output != null && (Boolean) output.get("result"))
		{
			//output = visaSettlementService.checkJVUploaded(visaUploadBean);
			
			//if(output != null &&  !(Boolean) output.get("result"))
			{
				 boolean flag = visaSettlementService.runVisaSettlementTTUM(visaUploadBean);
				 
				 if(flag)
				 {
					 return "Processing Completed";
				 }
				 else
				 {
					 return "Problem while Processing TTUM";
				 }
			}
			/*else
			{
				return "JV is not uploaded for selected date";
			}*/
		}
		else
		{
			return output.get("msg").toString();
		}
		
	}
	
	@RequestMapping(value = "ValidateVisaSettlementTTUM", method = RequestMethod.POST)
	@ResponseBody
	public String ValidateSettlementTTUM(@ModelAttribute("visaUploadBean")  VisaUploadBean visaUploadBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes) throws Exception{
		logger.info("***** VisaSettlementController.ValidateSettlementTTUM.Post Start ****");
		logger.info("ValidateSettlementTTUM Post "+visaUploadBean.getFileDate());
		String display="";
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		visaUploadBean.setCreatedBy(Createdby);
		
		HashMap<String, Object> output = visaSettlementService.CheckTTUMProcessed(visaUploadBean);
		
		if(output != null && !(Boolean) output.get("result"))
		{
			return "success";
		}
		else
		{
			return "TTUM is not processed for selected date";
		}
		
	}
	
	@RequestMapping(value = "DownloadVisaSettlementTTUM", method = RequestMethod.POST)
	@ResponseBody
	public void DownloadVisaSettlementTTUM(@ModelAttribute("visaUploadBean")  VisaUploadBean visaUploadBean,HttpServletRequest request,
			HttpServletResponse response,HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception{
		logger.info("***** VisaSettlementController.DownloadVisaSettlementTTUM.Get Start ****");
		logger.info("DownloadVisaSettlementTTUM Post "+visaUploadBean.getFileDate());
		String display="";
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		visaUploadBean.setCreatedBy(Createdby);
		List<Object> Excel_data = new ArrayList<Object>();
		
		Excel_data = visaSettlementService.getSettlementTTUMData(visaUploadBean);
		
		/*model.addAttribute("ReportName", "Visa_Settlement_TTUM");
		model.addAttribute("data", Excel_data);
		
		logger.info("***** VisaSettlementController.DownloadVisaSettlementTTUM POST End ****");
		return "GenerateVisaSettlementReport";
		*/
		
		// Downloading TEXT TTUM FOR UCO
				String fileName = "VISA_SETTLEMENT_TTUM.txt";
				
				String stPath = System.getProperty("java.io.tmpdir");
				logger.info("TEMP_DIR"+stPath);
				
				GenerateUCOTTUM obj = new GenerateUCOTTUM();
				stPath = obj.checkAndMakeDirectory(visaUploadBean.getFileDate(), "RUPAY");
				obj.generateTTUMFile(stPath, fileName, Excel_data);
				logger.info("File is created");
				
				
				File file = new File(stPath +File.separator+fileName);
				logger.info("path of zip file "+stPath +File.separator+fileName);
				FileInputStream inputstream = new FileInputStream(file);
				response.setContentLength((int) file.length());
				logger.info("before downloading zip file ");
				response.setContentType("application/txt");
				logger.info("download completed");
				
				/** Set Response header */
				String headerKey = "Content-Disposition";
				String headerValue = String.format("attachment; filename=\"%s\"",
						file.getName());
				response.setHeader(headerKey, headerValue);

				/** Write response. */
				OutputStream outStream = response.getOutputStream();
				IOUtils.copy(inputstream, outStream);
				response.flushBuffer();
		
	}
	
	//Settlement rollback code
	@RequestMapping(value = "VisaSettRollback", method = RequestMethod.POST)
	@ResponseBody
	public String VisaSettRollback(@ModelAttribute("visaUploadBean")  VisaUploadBean visaUploadBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes) throws Exception{
		logger.info("***** VisaSettlementController.VisaSettRollback.Get Start ****");
		logger.info("VisaSettRollback Post "+visaUploadBean.getFileDate());
		String display="";
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		visaUploadBean.setCreatedBy(Createdby);
		
		//1. VALIDATE WHETHER TTUM IS ALREADY PROCESSED
		HashMap<String, Object> output =  visaSettlementService.checkSettlementProcess(visaUploadBean);
		
		if(output != null && !(Boolean) output.get("result"))
		{
			//checck if ttum is processed
			
			//rollback of settlement report
			if(visaSettlementService.VisaSettRollback(visaUploadBean))
			{
				return "Rollback Completed";
			}
			else
			{
				return "Issue while rolling back";
			}
		}
		else
		{
			return "Settlement is not processed";
		}
		
	}	
}
