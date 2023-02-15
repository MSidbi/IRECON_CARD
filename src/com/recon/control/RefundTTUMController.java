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

import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.RefundTTUMBean;
import com.recon.service.RefundTTUMService;
import com.recon.util.CSRFToken;
import com.recon.util.GenerateDLBVoucher;
import com.recon.util.GenerateUCOTTUM;

@Controller
public class RefundTTUMController {

	private static final Logger logger = Logger.getLogger(RefundTTUMController.class);
	
	@Autowired
	RefundTTUMService refundTTUMService;
	
	@RequestMapping(value = "RefundTTUMMatching", method = RequestMethod.GET)
	public ModelAndView RefundTTUMMatchingGet(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
		logger.info("***** RefundTTUMMatchingGet.Get Start ****");
		RefundTTUMBean refundTTUMBean = new RefundTTUMBean();
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("refundTTUMBean",refundTTUMBean);
		modelAndView.setViewName("RefundTTUMMatching");
		
		logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
		return modelAndView;
	}
	@RequestMapping(value = "getRefundDataCount", method = RequestMethod.POST)
	@ResponseBody
	public String getRefundDataCount(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** getRefundDataCount.post Start ****");
		logger.info("getRefundDataCount POST");
         
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		beanObj.setCreatedBy(Createdby);
		
		//ALREADY PROCESSED
		HashMap<String, Object> output = refundTTUMService.getRefundCountAmount(beanObj);
		
		if(output != null && (Boolean)output.get("result"))
		{
			String[] data = output.get("msg").toString().split("\\|");
			if(data.length == 2)
				return "Refund Transaction count is "+data[0]+"\n And  Amount is "+data[1];
			else
				return "Issue while getting count and amount";
			
		}
		else
		{
			return output.get("msg").toString();
		}
		
		
		//return "Done";
	}
	
	@RequestMapping(value = "RefundTTUMMatching", method = RequestMethod.POST)
	@ResponseBody
	public String RefundTTUMMatchingPost(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** RefundTTUMMatchingPost.post Start ****");
		logger.info("RefundTTUMMatchingPost POST");
         
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		beanObj.setCreatedBy(Createdby);
		
		//ALREADY PROCESSED
		HashMap<String, Object> output = refundTTUMService.ValidateRefundProcessing(beanObj);
		
		if(output != null && (Boolean) output.get("result"))
		{
			//VALIDATIONS : INCOMING FILE UPLOADED , PREVIOUS DAY REFUND IS PROCESSED
			output = refundTTUMService.validateRefundTTUM(beanObj);

			if((Boolean)output.get("result"))
			{
				//process refund matching
				boolean executeFlag = refundTTUMService.runRefundTTUMMatching(beanObj);

				if(executeFlag)
				{
					return "Processing Completed Successfully";
				}

				else { return "Failed to process Refund Matching"; }

			}
			else
			{
				return output.get("msg").toString();
			}
		}
		else
		{
			return output.get("msg").toString();//"Matching is already processed \n Please download the reports";
		}
		
		//return "Done";
	}
	
	@RequestMapping(value = "ValidateRefundMatching", method = RequestMethod.POST)
	@ResponseBody
	public String ValidateRefundMatching(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** RefundTTUMMatchingPost.post Start ****");
		logger.info("RefundTTUMMatchingPost POST");
         
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		beanObj.setCreatedBy(Createdby);
		
		//VALIDATIONS : INCOMING FILE UPLOADED , PREVIOUS DAY REFUND IS PROCESSED
		HashMap<String,Object> output = refundTTUMService.ValidateRefundProcessing(beanObj);
		
		if(output != null && !(Boolean)output.get("result"))
		{
			return "success";
		}
		else
		{
			return output.get("msg").toString();
		}
	}
	
	@RequestMapping(value = "DownloadRefundMatching", method = RequestMethod.POST)
	public String DownloadRefundMatching(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj,
			String category,String datepicker,HttpServletRequest request,HttpSession httpSession,Model model) throws Exception {
		logger.info("***** DownloadRefundMatching.Post Start ****");
		logger.info("DownloadRefundMatching POST "+beanObj.getCategory()+" "+beanObj.getFileDate());
		List<Object> Excel_data = new ArrayList<Object>();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		beanObj.setCreatedBy(Createdby);
		
		Excel_data = refundTTUMService.getRefundTTUMProcessData(beanObj);
		
		model.addAttribute("ReportName", "Refund_TTUM_Matching");
		model.addAttribute("Monthly_data", Excel_data);
		logger.info("***** RupayRefundController.DownloadRefundMatching POST End ****");
		return "GenerateRefundTTUMReports";
	}
	
/****************************** REFUND TTUM GENERATION ********************************/
	
	@RequestMapping(value = "RefundTTUMGeneration", method = RequestMethod.GET)
	public ModelAndView RefundTTUMGenerationGet(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
		logger.info("***** RefundTTUMGeneration.Get Start ****");
		RefundTTUMBean refundTTUMBean = new RefundTTUMBean();
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("refundTTUMBean",refundTTUMBean);
		modelAndView.setViewName("RefundTTUMGeneration");
		
		logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "RefundTTUMGeneration", method = RequestMethod.POST)
	@ResponseBody
	public String RefundTTUMGenerationPost(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** RefundTTUMGeneration.post Start ****");
		logger.info("RefundTTUMGeneration POST");
         
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		beanObj.setCreatedBy(Createdby);
		
		//TTUM is already processed
		HashMap<String, Object> output = refundTTUMService.validateRefundTTUMGeneration(beanObj);
		
		if(output != null && !(Boolean) output.get("result"))
		{
			//Matching is PROCESSED or not
			output = refundTTUMService.ValidateRefundProcessing(beanObj);

			if(!(Boolean)output.get("result") && output.get("msg").toString().equalsIgnoreCase("Matching is already processed"))
			{
				//process refund matching
				boolean executeFlag = refundTTUMService.runRefundTTUMGeneration(beanObj);

				if(executeFlag)
				{
					return "Processing Completed Successfully \nPlease download the reports";
				}

				else { return "Failed to process Refund Matching"; }

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
		
		//return "Done";
	}
	
	@RequestMapping(value = "ValidateTTUMGeneration", method = RequestMethod.POST)
	@ResponseBody
	public String ValidateTTUMGeneration(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** DownloadRefundTTUM.post Start ****");
		logger.info("DownloadRefundTTUM POST");
         
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		beanObj.setCreatedBy(Createdby);
		
		//VALIDATIONS : INCOMING FILE UPLOADED , PREVIOUS DAY REFUND IS PROCESSED
		HashMap<String,Object> output = refundTTUMService.validateRefundTTUMGeneration(beanObj);
		
		if(output != null && (Boolean)output.get("result"))
		{
			return "success";
		}
		else
		{
			return "Please process ttum generation";
		}
	}
	
	@RequestMapping(value = "DownloadRefundTTUM", method = RequestMethod.POST)
	@ResponseBody
	public void DownloadRefundTTUM(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj,
			String category,String datepicker,
			HttpServletResponse response,HttpServletRequest request,HttpSession httpSession,Model model) throws Exception {
		logger.info("***** DownloadRefundTTUM.Post Start ****");
		logger.info("DownloadRefundTTUM POST "+beanObj.getCategory()+" "+beanObj.getFileDate());
		List<Object> TTUMData = new ArrayList<Object>();
		List<Object> Excel_data = new ArrayList<Object>();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		beanObj.setCreatedBy(Createdby);
		
		if(beanObj.getCategory().equalsIgnoreCase("VISA"))
		{
			TTUMData = refundTTUMService.getVisaRefundTTUMData(beanObj);

			String fileName = beanObj.getCategory()+"_REFUND_TTUM.txt";
			
			String stPath = System.getProperty("java.io.tmpdir");
			logger.info("TEMP_DIR"+stPath);
			
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getFileDate(), category);
			
			Excel_data.clear();
			Excel_data.add(TTUMData.get(0));
			Excel_data.add(TTUMData.get(1));
			fileName = beanObj.getCategory()+"_REFUND_TTUM_1.xls";
			obj.generateExcelTTUM(stPath, fileName, Excel_data, fileName, stPath);
			
			if(TTUMData.size() == 3)
			{
				Excel_data.clear();
				Excel_data.add(TTUMData.get(0));
				Excel_data.add(TTUMData.get(1));

				fileName = beanObj.getCategory()+"_REFUND_TTUM_2.xls";
				obj.generateExcelTTUM(stPath, fileName, Excel_data, fileName, stPath);

				//	obj.generateDRMTTUM(stPath, fileName, Excel_data,"REFUND");
				obj.generateExcelTTUM(stPath, fileName, Excel_data, fileName, stPath);
				logger.info("File is created");
			}
			
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
		else
		{
			Excel_data = refundTTUMService.getRefundTTUMData(beanObj);


			String fileName = beanObj.getCategory()+"_REFUND_TTUM.txt";

			String stPath = System.getProperty("java.io.tmpdir");
			logger.info("TEMP_DIR"+stPath);

			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(beanObj.getFileDate(), category);
			//	obj.generateDRMTTUM(stPath, fileName, Excel_data,"REFUND");
			obj.generateExcelTTUM(stPath, fileName, Excel_data, fileName, stPath);
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
		
		
		/*model.addAttribute("ReportName", "Refund_TTUM");
		model.addAttribute("data", Excel_data);
		logger.info("***** RupayRefundController.DownloadRefundTTUM POST End ****");
		return "GenerateNFSDailyReport";*/
	}
	
/************************************************** REFUND TTUM KNOCKOFF **********************************************/
	
	@RequestMapping(value = "RefundTTUMKnockoff", method = RequestMethod.GET)
	public ModelAndView RefundTTUMKnockoffGet(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
		logger.info("***** RefundTTUMKnockoff.Get Start ****");
		RefundTTUMBean refundTTUMBean = new RefundTTUMBean();
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("refundTTUMBean",refundTTUMBean);
		modelAndView.setViewName("RefundTTUMKnockoff");
		
		logger.info("***** RefundTTUMController.RefundTTUMKnockoff GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "RefundTTUMKnockoff", method = RequestMethod.POST)
	@ResponseBody
	public String RefundTTUMKnockoffPost(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj,@RequestParam("file") MultipartFile file,
			HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** RefundTTUMKnockoff.post Start ****");
		logger.info("RefundTTUMKnockoff POST");
		logger.info("File name is "+file.getOriginalFilename());
		
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		beanObj.setCreatedBy(Createdby);
		
		//VALIDATIONS : INCOMING FILE UPLOADED , PREVIOUS DAY REFUND IS PROCESSED
		HashMap<String,Object> output = refundTTUMService.validationForKnockoff(beanObj);
		
		if(output != null && (Boolean)output.get("result"))
		{
			if(beanObj.getOperation().equalsIgnoreCase("1"))
			{
				//moving data to MATCH TABLE
				output = refundTTUMService.moveUnmatchedData(file, beanObj);
				if(output != null && output.get("msg") != null)
				{
					return output.get("msg").toString();
				}
				else
				{
					return "Issue while processing";
				}
			}
			else
			{
				output = refundTTUMService.knockoffData(file, beanObj);
					return output.get("msg").toString();
			}
			
		}
		else
		{
			return output.get("msg").toString();
		}
	}
	
	/**** CODING FOR FULL REFUND TTUM &*******/
	@RequestMapping(value = "FullRefundTTUM", method = RequestMethod.GET)
	public ModelAndView FullRefundTTUMGet(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
		logger.info("***** RefundTTUMMatchingGet.Get Start ****");
		RefundTTUMBean refundTTUMBean = new RefundTTUMBean();
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("refundTTUMBean",refundTTUMBean);
		modelAndView.setViewName("FullRefundTTUMGeneration");
		
		logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "FullRefundTTUMGeneration", method = RequestMethod.POST)
	@ResponseBody
	public String FullRefundTTUMGenerationPost(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** RefundTTUMGeneration.post Start ****");
		logger.info("RefundTTUMGeneration POST ");
		logger.info("File date is "+beanObj.getFileDate());
		
		//1. CHECK WHETHER TTUM IS ALREADY PROCESSED OR NOT
		HashMap<String, Object> output = refundTTUMService.checkFullTTUMProcess(beanObj);
		
		if(output != null && (Boolean) output.get("result"))
		{
			//2. PROCESS
			boolean executed = refundTTUMService.runFullRefundTTUMGeneration(beanObj);
			
			if(executed)
			{
				return "TTUM Processing completed. \n Please download the reports";
			}
			else
			{
				return "TTUM Processing failed";
			}
		}
		else
		{
			return output.get("msg").toString();
		}
		
		
	}
	
	@RequestMapping(value = "ValidateFullTTUMGeneration", method = RequestMethod.POST)
	@ResponseBody
	public String ValidateFullTTUMGeneration(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** DownloadRefundTTUM.post Start ****");
		logger.info("DownloadRefundTTUM POST");
         
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		beanObj.setCreatedBy(Createdby);
		
		//VALIDATIONS : INCOMING FILE UPLOADED , PREVIOUS DAY REFUND IS PROCESSED
		HashMap<String,Object> output = refundTTUMService.validateFullTTUMProcess(beanObj);
		
		if(output != null && !(Boolean)output.get("result"))
		{
			return "success";
		}
		else
		{
			return "Please process ttum generation";
		}
	}
		
	/*@RequestMapping(value = "DownloadFullRefundTTUM", method = RequestMethod.POST)
	@ResponseBody
	public void DownloadFullRefundTTUM(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj,
			String category,String datepicker,
			HttpServletResponse response,HttpServletRequest request,HttpSession httpSession,Model model) throws Exception {
		logger.info("***** DownloadRefundTTUM.Post Start ****");
		logger.info("DownloadRefundTTUM POST "+beanObj.getCategory()+" "+beanObj.getFileDate());
		List<Object> Excel_data = new ArrayList<Object>();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		beanObj.setCreatedBy(Createdby);
		
		Excel_data = refundTTUMService.getFullRefundTTUMData(beanObj);
		
		String fileName = beanObj.getCategory()+"_REFUND_TTUM.txt";
		
		String stPath = System.getProperty("java.io.tmpdir");
		logger.info("TEMP_DIR"+stPath);
		
		GenerateUCOTTUM obj = new GenerateUCOTTUM();
		stPath = obj.checkAndMakeDirectory(beanObj.getFileDate(), category);
		obj.generateDRMTTUM(stPath, fileName, Excel_data,"REFUND");
		logger.info("File is created");
		
		
		File file = new File(stPath +File.separator+fileName);
		logger.info("path of zip file "+stPath +File.separator+fileName);
		FileInputStream inputstream = new FileInputStream(file);
		response.setContentLength((int) file.length());
		logger.info("before downloading zip file ");
		response.setContentType("application/txt");
		logger.info("download completed");
		
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"",
				file.getName());
		response.setHeader(headerKey, headerValue);

		OutputStream outStream = response.getOutputStream();
		IOUtils.copy(inputstream, outStream);
		response.flushBuffer();
		
		
	}*/
	
	@RequestMapping(value = "DownloadFullRefundTTUM", method = RequestMethod.POST)
	@ResponseBody
	public void DownloadFullRefundTTUM(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj,
			String category,String datepicker,
			HttpServletResponse response,HttpServletRequest request,HttpSession httpSession,Model model) throws Exception {
		logger.info("***** DownloadRefundTTUM.Post Start ****");
		logger.info("DownloadRefundTTUM POST "+beanObj.getCategory()+" "+beanObj.getFileDate());
		List<Object> Excel_data = new ArrayList<Object>();
		List<Object> TTUMData = new ArrayList<Object>();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		beanObj.setCreatedBy(Createdby);
		
		if(beanObj.getCategory().equalsIgnoreCase("VISA"))
		{

			TTUMData = refundTTUMService.getVisaFullRefundTTUMData(beanObj);

			logger.info("TTUM Size is  "+TTUMData.size());
			String fileName = beanObj.getCategory()+"_REFUND_TTUM.txt";

			String stPath = System.getProperty("java.io.tmpdir");
			logger.info("TEMP_DIR"+stPath);
			
			List<String> data = refundTTUMService.getRefundVoucher(beanObj);
			fileName = "VISA_REFUND_"+beanObj.getFileDate().replace("/", "_")+".txt";
			
			GenerateDLBVoucher vouchObj = new GenerateDLBVoucher();
			stPath = vouchObj.checkAndMakeDirectory(beanObj.getFileDate(), "VISA_REFUND");
			vouchObj.generateTTUMFile(stPath, fileName, data);
			//obj.generateDRMTTUM(stPath, fileName, TTUMData,"REFUND");
			//obj.generateMultipleDRMTTUMFiles(stPath, fileName, 2, TTUMData, stPath);
			//obj.generateMultipleTTUMFiles(stPath, fileName, TTUMData.size(), TTUMData);
			
			
			logger.info("File is created");

			/*** creating excel ***/
			List<String> Column_list = new ArrayList<String>();
			Column_list.add("account_number");
			Column_list.add("part_tran_type");	
			Column_list.add("transaction_amount");	
			Column_list.add("transaction_particular");	

			Excel_data.add(Column_list);
			Excel_data.add(TTUMData.get(0));

			fileName = beanObj.getCategory()+"_refund_ttum.xls";
			refundTTUMService.generateExcelTTUM(stPath, fileName, Excel_data,"Visa_Refund",response);
			logger.info("File is created");
			
		/*	if(TTUMData.size() == 2 && TTUMData.get(1) != null)
			{
				Excel_data.clear();
				Excel_data.add(Column_list);
				Excel_data.add(TTUMData.get(1));

				fileName = beanObj.getCategory()+"_REFUND_TTUM_INTERNATIONAL.xls";
				refundTTUMService.generateExcelTTUM(stPath, fileName, Excel_data,"REFUND",response);
				logger.info("File is created");
			}*/

			File file = new File(stPath +File.separator+"Visa_Refund.zip");
			logger.info("path of zip file "+stPath +File.separator+"Visa_Refund.zip");
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			logger.info("before downloading zip file ");
			response.setContentType("application/txt");
			logger.info("download completed");

			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					file.getName());
			response.setHeader(headerKey, headerValue);

			OutputStream outStream = response.getOutputStream();
			IOUtils.copy(inputstream, outStream);
			response.flushBuffer();
		
		}
		else
		{
			TTUMData = refundTTUMService.getVisaFullRefundTTUMData(beanObj);

			String fileName = beanObj.getCategory()+"_REFUND_TTUM.txt";

			String stPath = System.getProperty("java.io.tmpdir");
			logger.info("TEMP_DIR"+stPath);
			
			List<String> data = refundTTUMService.getRefundVoucher(beanObj);
			fileName = "RUPAY_REFUND_"+beanObj.getFileDate().replace("/", "_")+".txt";
			
			GenerateDLBVoucher vouchObj = new GenerateDLBVoucher();
			stPath = vouchObj.checkAndMakeDirectory(beanObj.getFileDate(), "RUPAY_REFUND");
			vouchObj.generateTTUMFile(stPath, fileName, data);
			//obj.generateDRMTTUM(stPath, fileName, TTUMData,"REFUND");
			//obj.generateMultipleDRMTTUMFiles(stPath, fileName, TTUMData.size(), TTUMData, "RUPAY");
			
			//obj.generateMultipleTTUMFiles(stPath, fileName, TTUMData.size(), TTUMData);
			logger.info("File is created");

			/*** creating excel ***/
			List<String> Column_list = new ArrayList<String>();
			Column_list.add("account_number");
			Column_list.add("part_tran_type");	
			Column_list.add("transaction_amount");	
			Column_list.add("transaction_particular");	
			Column_list.add("cycle");	

			Excel_data.add(Column_list);
			Excel_data.add(TTUMData.get(0));

			fileName = beanObj.getCategory()+"_REFUND_TTUM_RUPAY.xls";
			refundTTUMService.generateExcelTTUM(stPath, fileName, Excel_data,"RUPAY_REFUND",response);
			logger.info("File is created");		
			
			/*fileName = beanObj.getCategory()+"_REFUND_TTUM.xls";
			refundTTUMService.generateExcelTTUM(stPath, fileName, Excel_data,"REFUND",response);*/
			logger.info("File is created");			
			
			File file = new File(stPath +File.separator+"Rupay_Refund.zip");
			logger.info("path of zip file "+stPath +File.separator+"Rupay_Refund.zip");
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			logger.info("before downloading zip file ");
			response.setContentType("application/txt");
			logger.info("download completed");

			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					file.getName());
			response.setHeader(headerKey, headerValue);

			OutputStream outStream = response.getOutputStream();
			IOUtils.copy(inputstream, outStream);
			response.flushBuffer();
		}
		
		/*model.addAttribute("ReportName", "Refund_TTUM");
		model.addAttribute("data", Excel_data);
		logger.info("***** RupayRefundController.DownloadRefundTTUM POST End ****");
		return "GenerateNFSDailyReport";*/
	}
	
/***************** refund cbs upload format ****************/
	@RequestMapping(value = "DownloadRefundVoucher", method = RequestMethod.POST)
	@ResponseBody
	public void DownloadEODVoucher(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj,
			HttpServletResponse response, HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadEODVoucher.POST Start ****");
		logger.info("type of report is "+beanObj.getFileName());
		List<Object> Excel_data = new ArrayList<Object>();
		String fileName = "";
		
	}

	@RequestMapping(value = "FullRefundTTUMRollBack", method = RequestMethod.POST)
	@ResponseBody
	public String FullRefundTTUMRollBack(@ModelAttribute("refundTTUMBean") RefundTTUMBean beanObj,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** FullRefundTTUMRollBack.post Start ****");
		logger.info("FullRefundTTUMRollBack POST ");
		logger.info("File date is "+beanObj.getFileDate());
		
		//1. CHECK WHETHER TTUM IS ALREADY PROCESSED OR NOT
		HashMap<String, Object> output = refundTTUMService.checkFullTTUMProcess(beanObj);
		
		if(output != null && !(Boolean) output.get("result"))
		{
			//2. ROLLBACK
			Boolean executed = refundTTUMService.RefundRollBack(beanObj);
			
			if(executed)
			{
				return "Refund RolledBack Successfully";
			}
			else
			{
				return "Issue while rolling back refund";
			}
			
		}
		else
		{
			return output.get("msg").toString();
		}
		
		
	}
	
}
