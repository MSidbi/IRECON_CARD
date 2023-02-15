package com.recon.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.recon.model.FisdomFileUploadBean;
import com.recon.model.LoginBean;
import com.recon.model.RupayUploadBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.FisdomReconService;
import com.recon.util.CSRFToken;

@Controller
public class FisdomReconController {

	private static final Logger logger = Logger.getLogger(FisdomReconController.class);
	
	@Autowired
	FisdomReconService fisReconService;
	
	@RequestMapping(value = "FisdomReconProcess", method = RequestMethod.GET)
	public ModelAndView FisdomReconProcessGet(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
		logger.info("***** FisdomReconController.Get Start ****");
		FisdomFileUploadBean fisdomFileUploadBean = new FisdomFileUploadBean();
		logger.info("FisdomReconProcess GET");
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("fisdomFileUploadBean",fisdomFileUploadBean);
		modelAndView.setViewName("FisdomReconProcess");
		
		logger.info("***** FisdomReconController.FisdomReconProcess GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "FisdomReconProcess", method = RequestMethod.POST)
	@ResponseBody
	public String FisdomReconProcessPost(String fileDate,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** FisdomReconController.Post Start ****");
		logger.info("File date is "+fileDate);
		// 1. VALIDATE WHETHER FILES ARE UPLOADED FOR SELECTED DATE
		
		HashMap<String, Object> output = fisReconService.checkFileUploaded(fileDate);
		boolean ExecutionFlag = false;
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		
		if(output != null && (Boolean) output.get("result"))
		{
			//2. VALIDATE WHETHER PREV DAY RECON IS DONE
			output = fisReconService.checkpreviousReconProcess(fileDate);
			
			if(output != null && (Boolean) output.get("result"))
			{
				
				// RECON IS ALREADY PROCESSED
				output = fisReconService.reconAlreadyProcessed(fileDate);
				
				if(output !=  null && (Boolean) output.get("result"))
				{	

					//3. PROCESS RECON
					ExecutionFlag = fisReconService.runFisdomRecon(fileDate, Createdby);

					if(ExecutionFlag)
					{
						return "Recon is Processed \n Please download Reports and check";
					}
					else
					{
						return "Recon Process not complete";
					}
				}
				else
				{
					return output.get("msg").toString();
				}
			}
			else
			{
				return output.get("msg").toString();
			}
			
		}
		else
		{
			return output.get("msg").toString();
		}
		
	}
	
	@RequestMapping(value = "FisdomTTUMReport", method = RequestMethod.GET)
	public ModelAndView FisdomTTUMReportGet(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
		logger.info("***** FisdomReconController.FisdomTTUMReport.Get Start ****");
		FisdomFileUploadBean fisdomFileUploadBean = new FisdomFileUploadBean();
		logger.info("FisdomTTUMReport GET");
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("fisdomFileUploadBean",fisdomFileUploadBean);
		modelAndView.setViewName("GenerateFisdomTTUM");
		
		logger.info("***** FisdomReconController.FisdomTTUMReport GET End ****");
		return modelAndView;
	}
	

	@RequestMapping(value = "GenerateFisdomTTUM", method = RequestMethod.POST)
	@ResponseBody
	public String GenerateFisdomTTUM(@ModelAttribute("fisdomFileUploadBean")  FisdomFileUploadBean beanObj,
			HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** FisdomReconController GenerateFisdomTTUM.Post Start ****");
		logger.info("File date is "+beanObj.getFileDate());
		logger.info("TTUM Type is "+beanObj.getTypeOfTTUM());
		
		//1. VALIDATE WHETHER RECON IS PROCESSED
		HashMap<String, Object> output = fisReconService.reconAlreadyProcessed(beanObj.getFileDate());
		
		if(output != null && !(Boolean) output.get("result") && output.get("msg").toString().equals("recon is already processed"))
		{
			//check ttum is already processed
			boolean ttumAlreadyProcessed = fisReconService.checkTTUMAlreadyProcessed(beanObj);
			
			if(!ttumAlreadyProcessed)
			{
				//CHECK IF THERE ARE RECORDS FOR GENERATING TTUM
				boolean checkRecordsFlag = fisReconService.checkRecordsPresent(beanObj);

				if(checkRecordsFlag)
				{
					//2. IF YES THEN PROCESS TTUM
					if(fisReconService.runFisdomTTUM(beanObj))
					{
						return "TTUM Processing Completed \n Please download TTUM";

					}
					else
					{
						return "Issue while processing TTUM";
					}
				}
				else
				{

					return "No records for generating TTUM";
				}
			}
			else
			{
				return "TTUM is already processed";
			}
		}
		else
		{
			return "Recon is not processed for selected date";
		}

	}
	
	@RequestMapping(value = "ValidateFisdomTTUM", method = RequestMethod.POST)
	@ResponseBody
	public String ValidateFisdomTTUM(@ModelAttribute("fisdomFileUploadBean")  FisdomFileUploadBean beanObj,
			HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** FisdomReconController GenerateFisdomTTUM.Post Start ****");
		logger.info("File date is "+beanObj.getFileDate());
		logger.info("TTUM Type is "+beanObj.getTypeOfTTUM());
		
		// VALIDATE WHETHER TTUM IS PROCESSED
		boolean processFlag = fisReconService.validateTTUMProcessed(beanObj);
		
		if(processFlag)
		{
			return "success";
		}
		else
		{
			return "TTUM is not processed for selected date";
		}
	}
	
	@RequestMapping(value = "DownloadFisdomTTUM", method = RequestMethod.POST)
	@ResponseBody
	public void GenerateFisdomTTUM(@ModelAttribute("fisdomFileUploadBean") FisdomFileUploadBean beanObj,
			HttpServletResponse response, HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.DownloadUnmatchedTTUM post Start ****");
		logger.info("DownloadUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		beanObj.setCreatedBy(Createdby);
		
		String TEMP_DIR = System.getProperty("java.io.tmpdir");
		logger.info("TEMP_DIR"+TEMP_DIR);
		
		String stpath = TEMP_DIR;//+beanObj.getCategory()+File.separator+stnewDate;
		beanObj.setStPath(stpath);
		beanObj.setCategory("FISDOM");
		logger.info("Path is "+stpath);
		
		
		boolean directoryCreated = fisReconService.checkAndMakeDirectory(beanObj);
		
		if(directoryCreated)
		{
			stpath = fisReconService.createTTUMFile(beanObj);
			logger.info("Path returned is "+stpath);

		}
		
		File file = new File(stpath);
		logger.info("path of zip file "+stpath);
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
		
		//	return "Done";
	}
}
