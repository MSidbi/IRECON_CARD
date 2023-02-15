package com.recon.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
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
import org.springframework.web.servlet.ModelAndView;

import com.recon.model.CompareSetupBean;
import com.recon.model.LoginBean;
import com.recon.model.RupayUploadBean;
import com.recon.model.SettlementBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.ISourceService;
import com.recon.service.RupayTTUMService;
import com.recon.util.FileDetailsJson;
import com.recon.util.GenerateUCOTTUM;

@Controller
public class RupayTTUMController {
	
	private static final Logger logger = Logger.getLogger(RupaySettlementController.class);
	private static final String ERROR_MSG = "error_msg";
	
	@Autowired ISourceService iSourceService;
	
	@Autowired RupayTTUMService rupayTTUMService;
	
	@RequestMapping(value = "GenerateUnmatchedTTUM", method = RequestMethod.GET)   
	public ModelAndView GenerateUnmatchedTTUM(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
        //modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		String display="";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean(); 
		 logger.info("in GetHeaderList"+category);
		 beanObj.setCategory(category);
         subcat = iSourceService.getSubcategories(category);
         modelAndView.addObject("subcategory",subcat );
 		modelAndView.addObject("display",display);
 		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateUnmatchedTTUM");
		
		logger.info("***** RupayTTUMController.GenerateUnmatchedTTUM GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "GenerateUnmatchedTTUM", method = RequestMethod.POST)
	@ResponseBody
	public String GenerateUnmatchedTTUMPost(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.GenerateUnmatchedTTUM post Start ****");
		logger.info("GenerateUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby +" localDate is "+beanObj.getLocalDate());
		logger.info("filedate is "+beanObj.getFileDate()+" ttum type is "+beanObj.getTypeOfTTUM());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		
		//1. CHECK WHETHER TTUM IS ALREADY PROCESSED
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessed(beanObj);
		
		if(output != null && !(Boolean)output.get("result"))
		{
			//2. CHECK WHETHER RECON IS PROCESSED OR NOT
			
			if(beanObj.getFileDate() != null && !beanObj.getFileDate().equalsIgnoreCase(""))
			{
				output = rupayTTUMService.checkReconProcessed(beanObj);
				
			}
			else
			{
				//CHECK WHETHER RECON DATE IS GREATER THAN SELECTED TRAN DATE
				output = rupayTTUMService.checkTranReconDate(beanObj);
			}
			
			
			if(output != null && (Boolean)output.get("result"))
			{
				executed = rupayTTUMService.runTTUMProcess(beanObj);
			}
			else
			{
				return output.get("msg").toString();
			}

			if(executed)
			{
				return "Processing Completed Successfully! \n Please download Report";
			}
			else
			{
				return "Issue while processing!";
			}
		}
		else
		{
			if(output != null)
			{
				return output.get("msg").toString();
			}
			else
			{
				return "Issue while validating TTUM Process";
			}
				
		}
	}
	
	// CHECK WHETHER RECON HAS BEEN PROCESSED FOR SELECTED DATE
		@RequestMapping(value = "checkTTUMProcessed", method = RequestMethod.POST)
		@ResponseBody
		public String checkTTUMProcessed(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
				FileDetailsJson dataJson, ModelAndView modelAndView,
				HttpSession httpSession, HttpServletResponse response,
				HttpServletRequest request) {
			try {
				logger.info("RupayTTUMController: checkTTUMProcessed: Entry");
				//1. VALIDATE WHETHER TTUM IS PROCESSED OR NOT
				HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessed(beanObj);
				
				if(output != null && (Boolean) output.get("result"))
				{
					return "success";
				}
				else
				{
					return "TTUM is not processed for selected date";
				}
				
				
				
			} catch (Exception e) {
				logger.info("Exception is "+e);
				return "Exception";
				
			}
		}
		
		
	@RequestMapping(value = "DownloadUnmatchedTTUM", method = RequestMethod.POST)
	@ResponseBody
	public void DownloadUnmatchedTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletResponse response, HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.DownloadUnmatchedTTUM post Start ****");
		logger.info("DownloadUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		List<Object> TTUMData = new ArrayList<Object>();
		beanObj.setCreatedBy(Createdby);
		String fileName = "";
		try
		{
		if((beanObj.getFileDate() == null || beanObj.getFileDate().equalsIgnoreCase("")) && beanObj.getLocalDate() != null)
			beanObj.setFileDate(beanObj.getLocalDate());
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
		java.util.Date date = sdf.parse(beanObj.getFileDate());
		List<Object> Excel_data = new ArrayList<Object>();
		sdf = new SimpleDateFormat("dd-MM-yyyy");

		String stnewDate = sdf.format(date);
		
		String TEMP_DIR = System.getProperty("java.io.tmpdir");
		logger.info("new date is "+stnewDate);
		logger.info("TEMP_DIR"+TEMP_DIR);
		
		//ADDED BY INT8624 ON 18-FEB-2022
		logger.info("1 "+!TEMP_DIR.substring(TEMP_DIR.length()-1).equalsIgnoreCase("\\/"));
		if(!TEMP_DIR.substring(TEMP_DIR.length()-1).equalsIgnoreCase("\\/"))
		{
			logger.info(!TEMP_DIR.substring(TEMP_DIR.length()-1).equalsIgnoreCase("\\/"));
			TEMP_DIR = TEMP_DIR+File.separator;
		}
		
		
		String stpath = TEMP_DIR;//+beanObj.getCategory()+File.separator+stnewDate;
		beanObj.setStPath(stpath);
		
		logger.info("Path is "+stpath);
		
		
		boolean directoryCreated = rupayTTUMService.checkAndMakeDirectory(beanObj);
		
			
		if(directoryCreated)
		{
			stpath = stpath+beanObj.getCategory()+File.separator+stnewDate;
			logger.info("new path is "+stpath);
			logger.info("filedate is "+beanObj.getFileDate());
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			//DRM FORMAT
			if(beanObj.getCategory().equalsIgnoreCase("NFS"))
			{
				if(beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER"))  ///NIH TTUM
				{
					TTUMData = rupayTTUMService.getNIHTTUMData(beanObj);
					fileName = beanObj.getCategory()+"_NIH_MicroATM_TTUM.txt";
					List<Object> DRMData = (List<Object>) TTUMData.get(0);

					obj.generateMultipleDRMTTUMFiles(stpath, fileName, 1, TTUMData, "NFS");

					fileName = beanObj.getCategory()+"_NIH_TTUM.txt";
					List<Object> NIH = (List<Object>) TTUMData.get(1);
					obj.generateTTUMFile(stpath, fileName, NIH);
				}
				else if(beanObj.getStSubCategory().equalsIgnoreCase("ISSUER")) // ALL TTUM
				{

					TTUMData = rupayTTUMService.getTTUMData(beanObj);
					fileName = beanObj.getCategory()+"_"+beanObj.getTypeOfTTUM()+"_TTUM.txt";
					if(beanObj.getCategory().equalsIgnoreCase("NFS"))
						obj.generateDRMTTUM(stpath, fileName, TTUMData, "NFS");
					else
						obj.generateDRMTTUM(stpath, fileName, TTUMData, beanObj.getTypeOfTTUM());

					//stpath = rupayTTUMService.createTTUMFile(beanObj); // SPACE WALA FORMAT TTUM
					logger.info("Path returned is "+stpath);
				
				}
				
			}
			else if(beanObj.getStSubCategory().equalsIgnoreCase("ISSUER") && beanObj.getCategory().equalsIgnoreCase("VISA"))
			{
				TTUMData = rupayTTUMService.getVisaTTUMData(beanObj);
				fileName = beanObj.getCategory()+"_"+beanObj.getTypeOfTTUM()+"_TTUM.txt";
				
				if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED") || beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON2"))
					obj.generateMultipleDRMTTUMFiles(stpath, fileName, 2, TTUMData, "VISA");
				else
					obj.generateMultipleTTUMFiles(stpath, fileName, 2, TTUMData);

				//stpath = rupayTTUMService.createTTUMFile(beanObj); // SPACE WALA FORMAT TTUM
				logger.info("Path returned is "+stpath);
			}
			else if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
			{
				TTUMData = rupayTTUMService.getRupayTTUMData(beanObj);
				fileName = beanObj.getCategory()+"_"+beanObj.getTypeOfTTUM()+"_TTUM.txt";
				
				if(beanObj.getTypeOfTTUM().equalsIgnoreCase("UNMATCHED") || beanObj.getTypeOfTTUM().equalsIgnoreCase("UNRECON2"))
					obj.generateMultipleDRMTTUMFiles(stpath, fileName, 2, TTUMData, "RUPAY");
				else
					obj.generateMultipleTTUMFiles(stpath, fileName, 2, TTUMData);

				//stpath = rupayTTUMService.createTTUMFile(beanObj); // SPACE WALA FORMAT TTUM
				logger.info("Path returned is "+stpath);
			}
		}
		/*** CREATE EXCEL FILE****/
		List<String> Column_list = new ArrayList<String>();
		Column_list.add("ACCOUNT_NUMBER");
		Column_list.add("PART_TRAN_TYPE");	
		Column_list.add("TRANSACTION_AMOUNT");	
		Column_list.add("TRANSACTION_PARTICULAR");	
		
		Excel_data.add(Column_list);
		Excel_data.add(TTUMData);
		
		if(beanObj.getCategory().equalsIgnoreCase("NFS"))// && beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER"))
		{
			if(beanObj.getStSubCategory().equalsIgnoreCase("ACQUIRER"))
			{
				Excel_data.clear();
				Excel_data.add(Column_list);
				Excel_data.add(TTUMData.get(0));
				fileName = beanObj.getCategory()+"_"+beanObj.getAcqtypeOfTTUM()+"_TTUM_1.xls";
				rupayTTUMService.generateExcelTTUM(stpath, fileName, Excel_data,"REFUND",response, false);

				Excel_data.clear();
				Excel_data.add(Column_list);
				Excel_data.add(TTUMData.get(1));

				fileName = beanObj.getCategory()+"_"+beanObj.getAcqtypeOfTTUM()+"_TTUM_2.xls";
				rupayTTUMService.generateExcelTTUM(stpath, fileName, Excel_data,"REFUND",response, true);
			}
			else
			{
				fileName = beanObj.getCategory()+"_"+beanObj.getTypeOfTTUM()+"_TTUM.xls";
				rupayTTUMService.generateExcelTTUM(stpath, fileName, Excel_data,"REFUND",response,true);
			
			}
		
		}
		else if(beanObj.getCategory().equalsIgnoreCase("VISA"))
		{

			Excel_data.clear();
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData.get(0));
			fileName = beanObj.getCategory()+"_"+beanObj.getTypeOfTTUM()+"_TTUM_1.xls";
			rupayTTUMService.generateExcelTTUM(stpath, fileName, Excel_data,beanObj.getTypeOfTTUM(),response,false);
			
			Excel_data.clear();
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData.get(1));
			
			fileName = beanObj.getCategory()+"_"+beanObj.getTypeOfTTUM()+"_TTUM_2.xls";
			rupayTTUMService.generateExcelTTUM(stpath, fileName, Excel_data,beanObj.getTypeOfTTUM(),response, true);
		
		
		}
		else if(beanObj.getCategory().equalsIgnoreCase("RUPAY"))
		{
			rupayTTUMService.generateRupayExcelTTUM(stpath, Excel_data, "EXCEL_TTUM", response);
		}
		else
		{
			fileName = beanObj.getCategory()+"_"+beanObj.getTypeOfTTUM()+"_TTUM.xls";
			rupayTTUMService.generateExcelTTUM(stpath, fileName, Excel_data,"REFUND",response, true);
		}
		logger.info("File is created");
		
		File file = new File(stpath +File.separator+"EXCEL_TTUMS.zip");
		logger.info("path of zip file "+stpath +File.separator +"EXCEL_TTUMS.zip");
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
		catch(Exception e)
		{
			logger.info("Exception in DownloadUnmatchedTTUM "+e);
			
		}
	}
	
	
	/// NFS TTUM CODING
	@RequestMapping(value = "GenerateNFSTTUM", method = RequestMethod.GET)   
	public ModelAndView GenerateNFSTTUM(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** GenerateNFSTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
        //modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		String display="";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean(); 
		 logger.info("in GetHeaderList"+category);
		 beanObj.setCategory(category);
         subcat = iSourceService.getSubcategories(category);
         modelAndView.addObject("subcategory",subcat );
 		modelAndView.addObject("display",display);
 		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateNFSTTUM");
		
		logger.info("***** RupayTTUMController.GenerateNFSTTUM GET End ****");
		return modelAndView;
	}
	

	@RequestMapping(value = "RupayInternationalTTUM", method = RequestMethod.GET)   
	public ModelAndView RupayInternationalTTUM(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** RupayInternationalTTUM Start Get method  ****");
		modelAndView.addObject("category", category);
        //modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		String display="";
		List<String> subcat = new ArrayList<>();
		UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean(); 
		 logger.info("in GetHeaderList"+category);
		 beanObj.setCategory(category);
         subcat = iSourceService.getSubcategories(category);
         modelAndView.addObject("subcategory",subcat );
 		modelAndView.addObject("display",display);
 		modelAndView.addObject("unmatchedTTUMBean", beanObj);
		modelAndView.setViewName("GenerateRupayInternationalTTUM");
		
		logger.info("***** RupayTTUMController.GenerateUnmatchedTTUM GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "ProcessRupayInternationalTTUM", method = RequestMethod.POST)
	@ResponseBody
	public String ProcessRupayInternationalTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** ProcessRupayInternationalTTUM post Start ****");
		logger.info("ProcessRupayInternationalTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby +" localDate is "+beanObj.getLocalDate());
		logger.info("filedate is "+beanObj.getFileDate()+" ttum type is "+beanObj.getTypeOfTTUM());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		
		//1. CHECK WHETHER TTUM IS ALREADY PROCESSED
		HashMap<String, Object> output = rupayTTUMService.checkInternationalTTUMProcessed(beanObj);
		
		if(output != null && !(Boolean)output.get("result"))
		{
			//2. CHECK WHETHER RECON IS PROCESSED OR NOT for surcharge
			//output = rupayTTUMService.checkReconProcessed(beanObj);
			
			//if(output != null && (Boolean)output.get("result"))
			{
				executed = rupayTTUMService.runInternationalTTUMProcess(beanObj);
			}
			/*else
			{
				return output.get("msg").toString();
			}*/

			if(executed)
			{
				return "Processing Completed Successfully! \n Please download Report";
			}
			else
			{
				return "Issue while processing!";
			}
		}
		else
		{
			if(output != null)
			{
				return output.get("msg").toString();
			}
			else
			{
				return "Issue while validating TTUM Process";
			}
				
		}
	}
	
	// CHECK WHETHER RECON HAS BEEN PROCESSED FOR SELECTED DATE
			@RequestMapping(value = "checkIntTTUMProcessed", method = RequestMethod.POST)
			@ResponseBody
			public String checkIntTTUMProcessed(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
					FileDetailsJson dataJson, ModelAndView modelAndView,
					HttpSession httpSession, HttpServletResponse response,
					HttpServletRequest request) {
				try {
					logger.info("RupayTTUMController: checkIntTTUMProcessed: Entry");
					//1. VALIDATE WHETHER TTUM IS PROCESSED OR NOT
					HashMap<String, Object> output = rupayTTUMService.checkInternationalTTUMProcessed(beanObj);
					
					if(output != null && (Boolean) output.get("result"))
					{
						return "success";
					}
					else
					{
						return "TTUM is not processed for selected date";
					}
					
					
					
				} catch (Exception e) {
					logger.info("Exception is "+e);
					return "Exception";
					
				}
			}

			@RequestMapping(value = "DownloadInternationalTTUM", method = RequestMethod.POST)
			@ResponseBody
			public void DownloadInternationalTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
					HttpServletResponse response, HttpServletRequest request,HttpSession httpSession) throws Exception {
				logger.info("***** GenerateUnmatchedTTUM.DownloadInternationalTTUM post Start ****");
				logger.info("DownloadInternationalTTUM POST");
				List<Object> TTUMData = new ArrayList<Object>();
				String stnewDate = null;
				String fileName = "TTUM";
				
				if((beanObj.getFileDate() == null || beanObj.getFileDate().equalsIgnoreCase("")) && beanObj.getLocalDate() != null)
					beanObj.setFileDate(beanObj.getLocalDate());
				
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
					java.util.Date date = sdf.parse(beanObj.getFileDate());
					List<Object> Excel_data = new ArrayList<Object>();
					sdf = new SimpleDateFormat("dd-MM-yyyy");
					stnewDate = sdf.format(date);
				
				
				String TEMP_DIR = System.getProperty("java.io.tmpdir");
				logger.info("new date is "+stnewDate);
				logger.info("TEMP_DIR"+TEMP_DIR);
				
				//ADDED BY INT8624 ON 18-FEB-2022
				logger.info("1 "+!TEMP_DIR.substring(TEMP_DIR.length()-1).equalsIgnoreCase("\\/"));
				if(!TEMP_DIR.substring(TEMP_DIR.length()-1).equalsIgnoreCase("\\/"))
				{
					logger.info(!TEMP_DIR.substring(TEMP_DIR.length()-1).equalsIgnoreCase("\\/"));
					TEMP_DIR = TEMP_DIR+File.separator;
				}
				
				
				String stpath = TEMP_DIR;//+beanObj.getCategory()+File.separator+stnewDate;
				beanObj.setStPath(stpath);
				
				logger.info("Path is "+stpath);
				
				boolean directoryCreated = rupayTTUMService.checkAndMakeDirectory(beanObj);
				
					
				if(directoryCreated)
				{
					stpath = stpath+beanObj.getCategory()+File.separator+stnewDate;
					logger.info("new path is "+stpath);
					logger.info("filedate is "+beanObj.getFileDate());
					
					TTUMData = rupayTTUMService.getInternationalTTUMData(beanObj);
					
					fileName = "RUPAY_"+beanObj.getTypeOfTTUM()+"_TTUM.txt";
					
					rupayTTUMService.generateInternationalTTUMFile(stpath, fileName, TTUMData);
					
					
				}
				
				File file = new File(stpath +File.separator+fileName);
				logger.info("path of zip file "+stpath +File.separator +fileName);
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
			
			@RequestMapping(value = "NIHReport", method = RequestMethod.GET)   
			public ModelAndView NIHReport(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
				logger.info("***** GenerateUnmatchedTTUM Start Get method  ****");
				modelAndView.addObject("category", category);
		        //modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
				String display="";
				List<String> subcat = new ArrayList<>();
				UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean(); 
				 logger.info("in GetHeaderList"+category);
				 beanObj.setCategory(category);
		         subcat = iSourceService.getSubcategories(category);
		         modelAndView.addObject("subcategory",subcat );
		 		modelAndView.addObject("display",display);
		 		modelAndView.addObject("unmatchedTTUMBean", beanObj);
				modelAndView.setViewName("NIHReportDownload");
				
				logger.info("***** RupayTTUMController.GenerateUnmatchedTTUM GET End ****");
				return modelAndView;
			}
			
			// CHECK WHETHER RECON HAS BEEN PROCESSED FOR SELECTED DATE
			@RequestMapping(value = "checkNIHData", method = RequestMethod.POST)
			@ResponseBody
			public String checkNIHData(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
					FileDetailsJson dataJson, ModelAndView modelAndView,
					HttpSession httpSession, HttpServletResponse response,
					HttpServletRequest request) {
				try {
					logger.info("RupayTTUMController: checkNIHData: Entry");
					//1. VALIDATE WHETHER TTUM IS PROCESSED OR NOT
					HashMap<String, Object> output = rupayTTUMService.checkNIHRecords(beanObj);
					
					if(output != null && (Boolean) output.get("result"))
					{
						return "success";
					}
					else
					{
						return output.get("msg").toString();
					}
					
					
					
				} catch (Exception e) {
					logger.info("Exception is "+e);
					return "Exception";
					
				}
			}

			@RequestMapping(value = "DownloadNIHReport", method = RequestMethod.POST)
			public String DownloadNIHReport(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
					HttpServletResponse response, HttpServletRequest request,HttpSession httpSession,Model model) throws Exception {
				logger.info("***** DownloadNIHReport.POST Start ****");
					//logger.info("Data "+filename+" "+category+" "+stSubCategory+" "+datepicker+" "+cycle);
					logger.info("NFSSettlement POST");
					List<Object> Excel_data = new ArrayList<Object>();
					String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
					logger.info("Created by is "+Createdby);
					beanObj.setCreatedBy(Createdby);
					
							//GET DATA FOR REPORT
					    Excel_data = rupayTTUMService.getNIHReport(beanObj);
						
						model.addAttribute("ReportName", beanObj.getTypeOfTTUM()+"_NIH_"+beanObj.getLocalDate());
						model.addAttribute("data", Excel_data);
						logger.info("***** NFSSettlementController.NFSSettlementProcess Daily POST End ****");
						return "GenerateNFSDailyReport";
						
						}
			
			@RequestMapping(value = "GenerateCardtoCardTTUM", method = RequestMethod.GET)   
			public ModelAndView GenerateCardtoCardTTUM(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
				logger.info("***** GenerateCardtoCardTTUM Start Get method  ****");
				modelAndView.addObject("category", category);
		        //modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
				String display="";
				List<String> subcat = new ArrayList<>();
				UnMatchedTTUMBean beanObj = new UnMatchedTTUMBean(); 
				 logger.info("in GetHeaderList"+category);
				 beanObj.setCategory(category);
		         subcat = iSourceService.getSubcategories(category);
		         modelAndView.addObject("subcategory",subcat );
		 		modelAndView.addObject("display",display);
		 		modelAndView.addObject("unmatchedTTUMBean", beanObj);
				modelAndView.setViewName("GenerateCardToCardTTUM");
				
				logger.info("***** RupayTTUMController.GenerateCardtoCardTTUM GET End ****");
				return modelAndView;
			}
			
			@RequestMapping(value = "ProcessCardToCardTTUM", method = RequestMethod.POST)
			@ResponseBody
			public String ProcessCardToCardTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,HttpServletRequest request,HttpSession httpSession) throws Exception {
				logger.info("***** ProcessCardToCardTTUM post Start ****");
				logger.info("ProcessCardToCardTTUM POST");
				String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
				logger.info("Created by is "+Createdby +" localDate is "+beanObj.getLocalDate());
				logger.info("filedate is "+beanObj.getFileDate()+" ttum type is "+beanObj.getTypeOfTTUM());
				beanObj.setCreatedBy(Createdby);
				boolean executed = false;
				
				//1. CHECK WHETHER TTUM IS ALREADY PROCESSED
				HashMap<String, Object> output = rupayTTUMService.checkCardToCardTTUMProcessed(beanObj);
				
				if(output != null && !(Boolean)output.get("result"))
				{
					//2. CHECK WHETHER RECON IS PROCESSED OR NOT for surcharge
					//output = rupayTTUMService.checkReconProcessed(beanObj);
					
					//if(output != null && (Boolean)output.get("result"))
					{
						executed = rupayTTUMService.runCardToCardTTUMProcess(beanObj);
					}
					/*else
					{
						return output.get("msg").toString();
					}*/

					if(executed)
					{
						return "Processing Completed Successfully! \n Please download Report";
					}
					else
					{
						return "Issue while processing!";
					}
				}
				else
				{
					if(output != null)
					{
						return output.get("msg").toString();
					}
					else
					{
						return "Issue while validating TTUM Process";
					}
						
				}
			}
			
			// CHECK WHETHER RECON HAS BEEN PROCESSED FOR SELECTED DATE
					@RequestMapping(value = "checkCTCTTUMProcessed", method = RequestMethod.POST)
					@ResponseBody
					public String checkCtCTTUMProcessed(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
							FileDetailsJson dataJson, ModelAndView modelAndView,
							HttpSession httpSession, HttpServletResponse response,
							HttpServletRequest request) {
						try {
							logger.info("RupayTTUMController: checkCtCTTUMProcessed: Entry");
							//1. VALIDATE WHETHER TTUM IS PROCESSED OR NOT
							HashMap<String, Object> output = rupayTTUMService.checkCardToCardTTUMProcessed(beanObj);
							
							if(output != null && (Boolean) output.get("result"))
							{
								return "success";
							}
							else
							{
								return "TTUM is not processed for selected date";
							}
							
							
							
						} catch (Exception e) {
							logger.info("Exception is "+e);
							return "Exception";
							
						}
					}

					@RequestMapping(value = "DownloadCardToCardTTUM", method = RequestMethod.POST)
					@ResponseBody
					public void DownloadCardToCardTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
							HttpServletResponse response, HttpServletRequest request,HttpSession httpSession) throws Exception {
						logger.info("***** GenerateUnmatchedTTUM.DownloadInternationalTTUM post Start ****");
						logger.info("DownloadInternationalTTUM POST");
						List<Object> TTUMData = new ArrayList<Object>();
						String stnewDate = null;
						String fileName = "TTUM";
						
						if((beanObj.getFileDate() == null || beanObj.getFileDate().equalsIgnoreCase("")) && beanObj.getLocalDate() != null)
							beanObj.setFileDate(beanObj.getLocalDate());
						
							SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
							java.util.Date date = sdf.parse(beanObj.getFileDate());
							List<Object> Excel_data = new ArrayList<Object>();
							sdf = new SimpleDateFormat("dd-MM-yyyy");
							stnewDate = sdf.format(date);
						
						
						String TEMP_DIR = System.getProperty("java.io.tmpdir");
						logger.info("new date is "+stnewDate);
						logger.info("TEMP_DIR"+TEMP_DIR);
						
						//ADDED BY INT8624 ON 18-FEB-2022
						logger.info("1 "+!TEMP_DIR.substring(TEMP_DIR.length()-1).equalsIgnoreCase("\\/"));
						if(!TEMP_DIR.substring(TEMP_DIR.length()-1).equalsIgnoreCase("\\/"))
						{
							logger.info(!TEMP_DIR.substring(TEMP_DIR.length()-1).equalsIgnoreCase("\\/"));
							TEMP_DIR = TEMP_DIR+File.separator;
						}
						
						
						String stpath = TEMP_DIR;//+beanObj.getCategory()+File.separator+stnewDate;
						beanObj.setStPath(stpath);
						
						logger.info("Path is "+stpath);
						
						boolean directoryCreated = rupayTTUMService.checkAndMakeDirectory(beanObj);
						
							
						if(directoryCreated)
						{
							stpath = stpath+beanObj.getCategory()+File.separator+stnewDate;
							logger.info("new path is "+stpath);
							logger.info("filedate is "+beanObj.getFileDate());
							
							TTUMData = rupayTTUMService.getCardToCardTTUMData(beanObj);
							
							fileName = "CARDTOCARD_"+beanObj.getTypeOfTTUM()+"_TTUM.txt";
							
							GenerateUCOTTUM obj = new GenerateUCOTTUM();
							obj.generateDRMTTUM(stpath, fileName, TTUMData, "CARDTOCARD");
							
							
						}
						
						File file = new File(stpath +File.separator+fileName);
						logger.info("path of zip file "+stpath +File.separator +fileName);
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
}
