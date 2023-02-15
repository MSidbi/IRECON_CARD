package com.recon.control;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.model.AddNewSolBean;
import com.recon.model.LoginBean;
import com.recon.model.ManualKnockoffBean;
import com.recon.model.NFSSettlementBean;
import com.recon.service.ISourceService;
import com.recon.service.NFSSettlementCalService;
import com.recon.service.NFSSettlementService;
import com.recon.util.CSRFToken;

@Controller
public class MISReportController {
	
	/*private static final Logger logger = Logger.getLogger(MISReportController.class);
	private static final String ERROR_MSG = "error_msg";
	
	@Autowired ISourceService iSourceService;
	
	@Autowired NFSSettlementService nfsSettlementService;
	
	@Autowired NFSSettlementCalService nfsSettlementCalService;

	@RequestMapping(value = "nfsFileUpload", method = RequestMethod.GET)
	public ModelAndView nfsFileUploadGet(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** nfsFileUpload.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("nfsFileUpload GET");
		String display="";
		 logger.info("in GetHeaderList"+category);
		
        List<String> subcat = iSourceService.getSubcategories(category);
         if(category.equals("ONUS") || category.equals("AMEX") ||category.equals("CARDTOCARD") ||category.equals("WCC") ) {
        	 
        	 display="none";
         }
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory",subcat );
        modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("NFSSettlementFileUpload");
		
		logger.info("***** NFSSettlementController.nfsFileUpload GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "nfsFileUpload", method = RequestMethod.POST)
	@ResponseBody
	public String nfsFileUploadPost(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			@RequestParam("file") MultipartFile file, String filename,
			String category,String stSubCategory,String datepicker ,HttpSession httpSession,
			Model model,ModelAndView modelAndView,RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** nfsFileUpload.post Start ****");
		HashMap<String, Object> output = null;
		try
		{
			logger.info("RECON PROCESS GET");
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is "+Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			logger.info("VALUES ARE "+nfsSettlementBean+" "+category+" "+stSubCategory+datepicker);
			//1. VALIDATION FOR PREVIOUS FILE UPLOADED
			nfsSettlementBean.setCategory(category+"_SETTLEMENT");
		
			HashMap<String, Object> result = nfsSettlementService.validatePrevFileUpload(nfsSettlementBean);
			
			//2. UPLOADING FILE
			if(result != null && !(boolean)result.get("result"))
			{
				if(nfsSettlementBean.getFileName().equalsIgnoreCase("DFS") || nfsSettlementBean.getFileName().equalsIgnoreCase("JCB-UPI"))
				{
					output = nfsSettlementService.uploadDFSRawData(nfsSettlementBean, file);
				}

				logger.info("***** NFSSettlementController.nfsFileUpload POST End ****");
				if((boolean)output.get("result"))
				{
					return "File Uploaded Successfully \n Count is "+(Integer)output.get("count");
				}
				else
				{
					return "Error while Uploading file";
				}
			}
			else
			{
				return result.get("msg").toString();
			}
			
		}
		catch(Exception e)
		{
			logger.info("Exception in NFSSettlementController "+e);
			if(output != null  && !(boolean)output.get("result") )
			{	
				return "Error Occured at Line "+(Integer)output.get("count");
			}
			else
				return "Error Occurred in reading";

		}
	}
	
	@RequestMapping(value = "NTSLFileUpload", method = RequestMethod.GET)
	public ModelAndView ntslFileUploadGet(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** NTSLFileUpload.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NTSLFileUpload GET");
		String display="";
		 logger.info("in GetHeaderList"+category);
		
        List<String> subcat = iSourceService.getSubcategories(category);
         if(category.equals("ONUS") || category.equals("AMEX") ||category.equals("CARDTOCARD") ||category.equals("WCC") ) {
        	 
        	 display="none";
         }
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory",subcat );
        modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("NTSLFileUpload");
		
		logger.info("***** NFSSettlementController.NTSLFileUpload GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "NTSLFileUpload", method = RequestMethod.POST)
	@ResponseBody
	public String ntslFileUploadPost(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			@RequestParam("file") MultipartFile file,HttpSession httpSession,
			RedirectAttributes redirectAttributes) throws Exception {
		
		logger.info("***** NTSLFileUpload.Post Start ****");
		HashMap<String, Object> mapObj = new HashMap<String, Object>();
		logger.info("Data is "+nfsSettlementBean);
	//	logger.info("Data "+filename+" "+category+" "+stSubCategory+" "+datepicker+" "+cycle+" "+timePeriod);
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		
		if(file.getOriginalFilename().contains("_") && nfsSettlementBean.getFileName().contains("NFS"))
		{//VALIDATING UPLOADED FILE AND CYCLE SELECTED
			logger.info("Time period is "+nfsSettlementBean.getTimePeriod());

			String fileName = file.getOriginalFilename();
			logger.info("FileName is "+fileName);
			String[] fileNames = fileName.split("_");
			if(fileNames.length >1)
			{
				String cycle = fileNames[1].substring(0,1);

				logger.info("Cycle is: "+cycle);
				nfsSettlementBean.setCycle(Integer.parseInt(cycle));
			}
		}
		else
		{
			nfsSettlementBean.setCycle(1);
		}
		
		if(nfsSettlementBean.getTimePeriod()!= null && nfsSettlementBean.getTimePeriod().equalsIgnoreCase("Daily"))
		{
			//nfsSettlementBean.setFileName(filename);
			nfsSettlementBean.setCategory("NFS_SETTLEMENT");
			try {
				//1. VALIDATION FOR CHECKING UPLOADED DATE 
				HashMap<String, Object> result = nfsSettlementService.validatePrevFileUpload(nfsSettlementBean);

				if(result != null && !(boolean)result.get("result"))
				{
					//2. UPLOADING FILE
					mapObj = nfsSettlementService.uploadNTSLFile(nfsSettlementBean, file);

					logger.info("***** NFSSettlementController.NTSLFileUpload GET End ****");

					if((boolean)mapObj.get("result"))
					{
						return "NTSL Readed Successfully! \n Total Count is "+(Integer)mapObj.get("count");
					}
					else
					{
						return "Exception on line "+(Integer)mapObj.get("count")+" while reading NTSL file";
					}
				}
				else
				{
					return result.get("msg").toString(); 
				}
			}
			catch(Exception e)
			{

				logger.info("Exception in NFSSettlementController "+e);
				if(mapObj != null  && !(boolean)mapObj.get("result") )
				{	
					return "Error Occured at Line "+(Integer)mapObj.get("count");
				}
				else
					return "Error Occurred in reading";


			}
		}
		else
		{
			//MONTHLY UPLOAD LOGIC
			nfsSettlementBean.setCategory("MONTHLY_SETTLEMENT");
			//nfsSettlementBean.setFileName(filename);

			//VALIDATING WHETHER FILE IS UPLOADED FOR SELECTED MONTH
			mapObj = nfsSettlementService.checkMonthlyNTSLUploaded(nfsSettlementBean);
			if(mapObj != null)
			{
				boolean result = (boolean)mapObj.get("result");

				if(result)
				{
					//UPLOADING FILE
					mapObj = nfsSettlementService.uploadMonthlyNTSLFile(nfsSettlementBean, file);

					if((boolean)mapObj.get("result"))
					{
						return "NTSL Readed Successfully! \n Total Count is "+(Integer)mapObj.get("count");
					}
					else
					{
						return "Exception on line "+(Integer)mapObj.get("count")+" while reading NTSL file";
					}

				}
				else{
					return mapObj.get("msg").toString();
				}
			}
			else
			{
				logger.info("Exception while validation");
				return "Exception while uploading";
			}
		}
	}
	
	@RequestMapping(value = "NFSSettlement", method = RequestMethod.GET)
	public ModelAndView NFSSettlement(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** NFSSettlement.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NFSSettlement GET");
		String display="";
		 logger.info("in GetHeaderList"+category);
		 
		
        List<String> subcat = iSourceService.getSubcategories(category);
         if(category.equals("ONUS") || category.equals("AMEX") ||category.equals("CARDTOCARD") ||category.equals("WCC") ) {
        	 
        	 display="none";
         }
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory",subcat );
        modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("GenerateNFSSettlement");
		
		logger.info("***** NFSSettlementController.NFSSettlement GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "NFSSettlementValidation", method = RequestMethod.POST)
	@ResponseBody
	public String NFSMonthlyValidation(String fileDate,String stSubCategory,String timePeriod,String cycle,String filename,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** NFSSettlementProcess.Post Start ****");
		String lastDate = fileDate;
		logger.info("NFSMonthlyValidation POST");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
		logger.info("filename is "+filename);
		nfsSettlementBean.setFileName(filename);
		nfsSettlementBean.setStSubCategory(stSubCategory);
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		nfsSettlementBean.setCycle(Integer.parseInt(cycle));
		HashMap<String, Object> result;

		if(timePeriod != null && timePeriod.equalsIgnoreCase("Monthly"))
		{

			System.out.println("File date is "+fileDate);
			fileDate = "01/"+fileDate;
			System.out.println("Filedate is "+fileDate);

			try
			{			
				LocalDate lastDayOfMonth = LocalDate.parse(fileDate, DateTimeFormatter.ofPattern("dd/M/yyyy")).with(TemporalAdjusters.lastDayOfMonth());
				System.out.println("lastDayOfMonth "+lastDayOfMonth);
				
				  SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); String
				testDate = formatter.format(lastDayOfMonth);
				System.out.println("TEst Date "+testDate);
				 
				lastDate = lastDayOfMonth.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
				System.out.println("TEstdate is "+lastDate);
				//setting proper date format
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/M/yyyy");
				LocalDate t = LocalDate.parse(fileDate,formatter);
				System.out.println("t "+t);
				fileDate = t.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

			}
			catch(Exception e)
			{
				System.out.println("Exception is "+e);
				return "Error while getting last Date";
			}
			
			nfsSettlementBean.setDatepicker(fileDate);
			nfsSettlementBean.setToDate(lastDate);
			
			nfsSettlementBean.setStSubCategory(stSubCategory);
			//1. VALIDATION Whether settlement is processed for complete month
			result = nfsSettlementService.checkNFSMonthlyProcess(nfsSettlementBean);

			if(result != null && (Boolean)result.get("result"))
			{
				return "success";
			}
			else
			{
				if(result == null)
				{
					return "Error";
				}
				else
				{
					if(!result.get("msg").toString().equalsIgnoreCase(""))
					{
						System.out.println(result.get("msg").toString());
						return result.get("msg").toString();
					}
					else
					{
						return "Problem Occurred!";
					}
				}
			}
		}
		else
		{
			//For Daily Settlement
			nfsSettlementBean.setDatepicker(fileDate);
			if(nfsSettlementBean.getFileName().equals("NTSL-NFS"))
			{
				result = nfsSettlementService.ValidateDailySettProcess(nfsSettlementBean);
			}
			else
			{
				// check whether NFS raw file is uploaded and it is not already processed
				result = nfsSettlementService.ValidateOtherSettProcess(nfsSettlementBean);
			}
			
			if(result != null && (Boolean)result.get("result"))
			{
				boolean executed = false;
				//return "success";
				if(nfsSettlementBean.getFileName().contains("NFS"))
				{
					executed = nfsSettlementCalService.runNFSDailyProc(nfsSettlementBean);
				}
				else if(nfsSettlementBean.getFileName().contains("PBGB"))
				{
					executed = nfsSettlementCalService.runPBGBDailyProc(nfsSettlementBean);
				}
				else
				{
					executed = nfsSettlementCalService.runDFSJCBDailyProc(nfsSettlementBean);
				}
				
				if(executed)
				{
					return "Settlement Processed Successfully \n Please download report";
				}
				else
				{
					return "Settlement Processing Failed";
				}
			}
			else
			{

				if(result == null)
				{
					return "Error";
				}
				else
				{
					if(!result.get("msg").toString().equalsIgnoreCase(""))
					{
						System.out.println(result.get("msg").toString());
						return result.get("msg").toString();
					}
					else
					{
						return "Problem Occurred!";
					}
				}
			
			}
		}
		//return "success";
	
	}
	
	@RequestMapping(value = "DownloadSettlementreport", method = RequestMethod.POST)
	public String NFSMonthlySettlementProcess(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadSettlementreport.POST Start ****");
		//logger.info("Data "+filename+" "+category+" "+stSubCategory+" "+datepicker+" "+cycle);
		logger.info("NFSSettlement POST");
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
//		nfsSettlementBean.setFileName(filename);
		List<Object> Excel_data = new ArrayList<Object>();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		
			//GETTING , IN DATE FIELD
			logger.info("File Name is "+nfsSettlementBean.getFileName());
			boolean executed = false;
			
			if(nfsSettlementBean.getDatepicker().contains(","));
			nfsSettlementBean.setDatepicker((nfsSettlementBean.getDatepicker().replace(",", "")));
			
			if(nfsSettlementBean.getFileName().contains("NFS"))
			{
				executed = nfsSettlementCalService.runNFSDailyProc(nfsSettlementBean);
			}
			else
			{
				executed = nfsSettlementCalService.runDFSJCBDailyProc(nfsSettlementBean);
			}
			
			if(executed)
			{
				//GET DATA FOR REPORT
				Excel_data = nfsSettlementCalService.getDailySettlementReport(nfsSettlementBean);
			}
			model.addAttribute("ReportName", "Settlement_Cycle"+nfsSettlementBean.getCycle());
			model.addAttribute("data", Excel_data);
			logger.info("***** NFSSettlementController.NFSSettlementProcess Daily POST End ****");
			return "GenerateNFSDailyReport";
		
	}

*//*********************** nfs settlement download ***************//*
	@RequestMapping(value = "NFSSettProcessValidation", method = RequestMethod.POST)
	@ResponseBody
	public String NFSSettProcessValidation(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		HashMap<String, Object>  output ;
		logger.info("Inside NFSSettProcessValidation Post");
		
			output = nfsSettlementService.CheckSettlementProcess(nfsSettlementBean);
		
		if(output != null && (Boolean) output.get("result"))
		{
			return "success";
		}
		else
		{
			return output.get("msg").toString();
		}
	
	
	}
	
	@RequestMapping(value = "DownloadSettreport", method = RequestMethod.POST)
	public String DownloadSettreport(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadSettreport.POST Start ****");
		//logger.info("Data "+filename+" "+category+" "+stSubCategory+" "+datepicker+" "+cycle);
		logger.info("NFSSettlement POST");
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
//		nfsSettlementBean.setFileName(filename);
		List<Object> Excel_data = new ArrayList<Object>();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		
			//GETTING , IN DATE FIELD
			logger.info("File Name is "+nfsSettlementBean.getFileName());
			boolean executed = false;
			
			if(nfsSettlementBean.getDatepicker().contains(","));
			nfsSettlementBean.setDatepicker((nfsSettlementBean.getDatepicker().replace(",", "")));
			
			
				//GET DATA FOR REPORT
				Excel_data = nfsSettlementCalService.getDailySettlementReport(nfsSettlementBean);
			
			model.addAttribute("ReportName", nfsSettlementBean.getFileName()+"_Settlement_Cycle"+nfsSettlementBean.getCycle());
			model.addAttribute("data", Excel_data);
			logger.info("***** NFSSettlementController.NFSSettlementProcess Daily POST End ****");
			return "GenerateNFSDailyReport";
		
	}
	
*//****************** ends here *******************//*	
	
	@RequestMapping(value = "SkipSettlement", method = RequestMethod.POST)
	@ResponseBody
	public String skipSettlement(String fileDate,String stSubCategory,String timePeriod,String cycle,String filename,@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		
		logger.info("Inside skipSettlement: POST");
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
		nfsSettlementBean.setFileName(filename);
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		nfsSettlementBean.setDatepicker(fileDate);
		
		HashMap<String, Object> result = nfsSettlementCalService.skipSettlement(nfsSettlementBean);
		
		if(result != null && (Boolean)result.get("result"))
		{
			return "Record Updated Successfully";
		}
		else
		{

			if(result == null)
			{
				return "Error";
			}
			else
			{
				if(!result.get("msg").toString().equalsIgnoreCase(""))
				{
					System.out.println(result.get("msg").toString());
					return result.get("msg").toString();
				}
				else
				{
					return "Problem Occurred!";
				}
			}
			
		}
		
		
		
	}

//METHOD FOR DAILY INCOME CALCULATION
	@RequestMapping(value = "NFSInterchange", method = RequestMethod.GET)
	public ModelAndView NFSInterchange(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** NFSInterchange.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NFSSettlement GET");
		String display="";
		 logger.info("in GetHeaderList"+category);
		 
		
        List<String> subcat = iSourceService.getSubcategories(category);
         if(category.equals("ONUS") || category.equals("AMEX") ||category.equals("CARDTOCARD") ||category.equals("WCC") ) {
        	 
        	 display="none";
         }
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory",subcat );
        modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("GenerateNFSInterchange");
		
		logger.info("***** NFSSettlementController.NFSInterchange GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "NFSInterchangeValidation", method = RequestMethod.POST)
	@ResponseBody
	public String NFSInterchangeValidation(String fileDate,String stSubCategory,String timePeriod,String filename,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** NFSInterchangeValidation.Post Start ****");
		String lastDate = fileDate;
		logger.info("NFSInterchangeValidation POST");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
		logger.info("filename is "+filename);
		nfsSettlementBean.setFileName(filename);
		nfsSettlementBean.setStSubCategory(stSubCategory);
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		//nfsSettlementBean.setCycle(Integer.parseInt(cycle));

		if(timePeriod != null && timePeriod.equalsIgnoreCase("Monthly"))
		{

			System.out.println("File date is "+fileDate);
			fileDate = "01/"+fileDate;
			System.out.println("Filedate is "+fileDate);

			try
			{			
				LocalDate lastDayOfMonth = LocalDate.parse(fileDate, DateTimeFormatter.ofPattern("dd/M/yyyy")).with(TemporalAdjusters.lastDayOfMonth());
				System.out.println("lastDayOfMonth "+lastDayOfMonth);
				
				  SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); String
				testDate = formatter.format(lastDayOfMonth);
				System.out.println("TEst Date "+testDate);
				 
				lastDate = lastDayOfMonth.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
				System.out.println("TEstdate is "+lastDate);
				//setting proper date format
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/M/yyyy");
				LocalDate t = LocalDate.parse(fileDate,formatter);
				System.out.println("t "+t);
				fileDate = t.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

			}
			catch(Exception e)
			{
				System.out.println("Exception is "+e);
				return "Error while getting last Date";
			}
			
			nfsSettlementBean.setDatepicker(fileDate);
			nfsSettlementBean.setToDate(lastDate);
			
			nfsSettlementBean.setStSubCategory(stSubCategory);
			//1. VALIDATION Whether settlement is processed for complete month
			HashMap<String, Object> result = nfsSettlementService.checkNFSMonthlyProcess(nfsSettlementBean);

			if(result != null && (Boolean)result.get("result"))
			{
				return "success";
			}
			else
			{
				if(result == null)
				{
					return "Error";
				}
				else
				{
					if(!result.get("msg").toString().equalsIgnoreCase(""))
					{
						System.out.println(result.get("msg").toString());
						return result.get("msg").toString();
					}
					else
					{
						return "Problem Occurred!";
					}
				}
			}
		}
		else
		{
			//For Daily Settlement
			nfsSettlementBean.setDatepicker(fileDate);
			HashMap<String, Object> result = nfsSettlementService.ValidateDailyInterchangeProcess(nfsSettlementBean);
			
			if(result != null && (Boolean)result.get("result"))
			{
				return "success";
			}
			else
			{

				if(result == null)
				{
					return "Error";
				}
				else
				{
					if(!result.get("msg").toString().equalsIgnoreCase(""))
					{
						System.out.println(result.get("msg").toString());
						return result.get("msg").toString();
					}
					else
					{
						return "Problem Occurred!";
					}
				}
			
			}
		}
		//return "success";
	
	}
	
	@RequestMapping(value = "DownloadInterchangereport", method = RequestMethod.POST)
	public String DownloadInterchangereport(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadSettlementreport.POST Start ****");
		//logger.info("Data "+filename+" "+category+" "+stSubCategory+" "+datepicker+" "+cycle);
		logger.info("NFSSettlement POST");
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
//		nfsSettlementBean.setFileName(filename);
		List<Object> Excel_data = new ArrayList<Object>();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		
		if(nfsSettlementBean.getTimePeriod().equalsIgnoreCase("MONTHLY"))
		{
			if(nfsSettlementBean.getDatepicker().contains(","));
			nfsSettlementBean.setDatepicker((nfsSettlementBean.getDatepicker().replace(",", "")));
			String fileDate = nfsSettlementBean.getDatepicker();
			String lastDate = fileDate;
			System.out.println("File date is "+nfsSettlementBean.getDatepicker());
			fileDate = "01/"+fileDate;
			System.out.println("Filedate is "+fileDate);

			try
			{			
				LocalDate lastDayOfMonth = LocalDate.parse(fileDate, DateTimeFormatter.ofPattern("dd/M/yyyy")).with(TemporalAdjusters.lastDayOfMonth());
				System.out.println("lastDayOfMonth "+lastDayOfMonth);
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				String testDate = formatter.format(lastDayOfMonth);
				System.out.println("TEst Date "+testDate);
				lastDate = lastDayOfMonth.toString();
				System.out.println("Last Date is "+lastDate);
				lastDate = lastDate.replace("-", "/");		
				lastDate = lastDayOfMonth.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
				System.out.println("TEstdate is "+lastDate);
				//setting proper date format
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/M/yyyy");
				LocalDate t = LocalDate.parse(fileDate,formatter);
				System.out.println("t "+t);
				fileDate = t.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

			}
			catch(Exception e)
			{
				System.out.println("Exception is "+e);
				return "Error while getting last Date";
			}
			nfsSettlementBean.setDatepicker(fileDate);
			nfsSettlementBean.setToDate(lastDate);
			
			
			nfsSettlementBean.setCategory("MONTHLY_SETTLEMENT");
			//nfsSettlementBean.setFileName("NTSL-NFS");
			
				//2. CALLING PROCEDURE
			boolean executed = nfsSettlementCalService.runNFSMonthlyProc(nfsSettlementBean);

			
			if(executed)
			{
				//4. REPORT DOWNLOADING
				Excel_data = nfsSettlementCalService.getInterchangeData(nfsSettlementBean);
			}
			model.addAttribute("Monthly_data", Excel_data);
			model.addAttribute("ReportName", nfsSettlementBean.getFileName()+"SETTLEMENT_REPORT");

			logger.info("***** NFSSettlementController.NFSSettlementProcess monthly POST End ****");
			return "GenerateNFSMonthlyReport";
		}
		else
		{
			//GETTING , IN DATE FIELD
			logger.info("File Name is "+nfsSettlementBean.getFileName());
			boolean executed = false;
			
			if(nfsSettlementBean.getDatepicker().contains(","));
			nfsSettlementBean.setDatepicker((nfsSettlementBean.getDatepicker().replace(",", "")));
			
			if(nfsSettlementBean.getFileName().contains("NFS"))
			{
				executed = nfsSettlementCalService.runDailyInterchangeProc(nfsSettlementBean);
			
			 * } else { executed =
			 * nfsSettlementCalService.runDFSJCBDailyProc(nfsSettlementBean); }
			 
			
			if(executed)
			{
				//GET DATA FOR REPORT
				Excel_data = nfsSettlementCalService.getDailyInterchangeData(nfsSettlementBean);
			}
			model.addAttribute("ReportName", "Settlement");
			model.addAttribute("Monthly_data", Excel_data);
			logger.info("***** NFSSettlementController.NFSSettlementProcess Daily POST End ****");
			return "GenerateNFSMonthlyReport";
		}
	}

//RECTIFYING SETTLEMENT AMOUNT
	@RequestMapping(value = "SettlementRectify", method = RequestMethod.POST)
	@ResponseBody
	public String SettlementRectify(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,
			String category,String datepicker,String stSubCategory,String timePeriod,String cycle,String fileName,String rectAmt,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** SettlementRectify.post Start ****");
		HashMap<String, Object> output = null;
		try
		{
			logger.info("SettlementRectify : Post");
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is "+Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			logger.info("VALUES ARE "+nfsSettlementBean+" "+category+" "+stSubCategory+datepicker);
			//1. VALIDATION FOR PREVIOUS FILE UPLOADED
			nfsSettlementBean.setCategory(category+"_SETTLEMENT");
		
			HashMap<String, Object> result = nfsSettlementService.validateSettDifference(nfsSettlementBean);
			
			//2. UPLOADING FILE
			if(result != null && (boolean)result.get("result"))
			{
				return "Amount is Rectified !";
			}
			else
			{
				return result.get("msg").toString();
			}
			
		}
		catch(Exception e)
		{
			logger.info("Exception in NFSSettlementController "+e);
			
				return "Error Occurred in getting Difference";

		}
	}
	*//***************** Adding cooperative Bank*********************//*
	@RequestMapping(value = "addCooperativeBank", method = RequestMethod.GET)
	public ModelAndView addCooperativeBank(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** nfsFileUpload.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("nfsFileUpload GET");
		String display="";
		 logger.info("in GetHeaderList"+category);
		
        List<String> subcat = iSourceService.getSubcategories(category);
         if(category.equals("ONUS") || category.equals("AMEX") ||category.equals("CARDTOCARD") ||category.equals("WCC") ) {
        	 
        	 display="none";
         }
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory",subcat );
        modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("AddCoOperativeBank");
		
		logger.info("***** NFSSettlementController.nfsFileUpload GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "addCooperativeBank", method = RequestMethod.POST)
	@ResponseBody
	public String addCooperativeBankPost(ModelAndView modelAndView,String bankName,String accNumber,HttpServletRequest request) throws Exception {
		logger.info("***** addCooperativeBank.POST Start ****");
		Boolean checkFlag = nfsSettlementService.addCooperativeBank(bankName, accNumber);
		if(checkFlag)
			return "Record Added Successfully!";
		else
			return "Failed to Add Record!";
		
	}
*//******************** ADDING NODAL SOL *********************************//*
	@RequestMapping(value = "addNodalSol", method = RequestMethod.GET)
	public ModelAndView addNodalSol(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
		logger.info("***** addNodalSol.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("addNodalSol GET");
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("AddNewSol");
		
		logger.info("***** NFSSettlementController.addNodalSol GET End ****");
		return modelAndView;
	}
	@RequestMapping(value = "getNodalDetails", method = RequestMethod.POST)
	@ResponseBody
	public List<String> getNodalDetails(String  state,HttpServletRequest request) throws Exception {
		logger.info("***** getNodalDetails.Post Start ****");
		List<String> data  = new ArrayList<String>();
		data = nfsSettlementService.getNodalData(state);
		logger.info("data length "+data.size());
		return data;
	}
	
	@RequestMapping(value = "saveNodalDetails", method = RequestMethod.POST)
	@ResponseBody
	public String saveNodalDetails(@ModelAttribute("addNewSolBean")  AddNewSolBean addNewSolBean,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** saveNodalDetails.Post Start ****");
		logger.info("addNewSolBean "+addNewSolBean.getSolId());
		logger.info("addNewSolBean "+addNewSolBean.getState());
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		addNewSolBean.setCreatedBy(Createdby);
		boolean insertFlag = nfsSettlementService.SaveNodalDetails(addNewSolBean);
		
		if(insertFlag)
		{
			return "Record saved successfully!";
		}
		else
		{
			return "Issue while saving the data";
		}
	}*/
}
