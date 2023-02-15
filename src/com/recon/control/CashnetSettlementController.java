package com.recon.control;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
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

import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.service.CashnetSettlementService;
import com.recon.service.ISourceService;
import com.recon.service.impl.CashnetSettlementServiceImpl;
import com.recon.util.CSRFToken;

@Controller
public class CashnetSettlementController {

	private static final Logger logger = Logger.getLogger(CashnetSettlementController.class);
	
	@Autowired ISourceService iSourceService;
	
	@Autowired CashnetSettlementService cashnetSettlementService;
	
	@RequestMapping(value = "CashnetInterchange", method = RequestMethod.GET)
	public ModelAndView CashnetInterchange(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
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
		modelAndView.setViewName("GenerateCashnetInterchange");

		logger.info("***** NFSSettlementController.nfsFileUpload GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "ProcessCashnetInterchange", method = RequestMethod.POST)
	@ResponseBody
	public String processInterchange(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadCashnetInterchange.POST Start ****");
		logger.info("DownloadCashnetInterchange POST");
		List<Object> Excel_data = new ArrayList<Object>();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		//nfsSettlementBean.setCategory("CASHNET");
		if(nfsSettlementBean.getTimePeriod().equalsIgnoreCase("MONTHLY"))
		{
			if(nfsSettlementBean.getDatepicker().contains(","));
			nfsSettlementBean.setDatepicker((nfsSettlementBean.getDatepicker().replace(",", "")));
			String fileDt = nfsSettlementBean.getDatepicker();
			String lastDate = fileDt;
			System.out.println("File date is "+nfsSettlementBean.getDatepicker());
			fileDt = "01/"+fileDt;
			System.out.println("Filedate is "+fileDt);
			try
			{			
				LocalDate lastDayOfMonth = LocalDate.parse(fileDt, DateTimeFormatter.ofPattern("dd/M/yyyy")).with(TemporalAdjusters.lastDayOfMonth());
				System.out.println("lastDayOfMonth "+lastDayOfMonth);
				lastDate = lastDayOfMonth.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
				System.out.println("TEstdate is "+lastDate);
				//setting proper date format
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/M/yyyy");
				LocalDate t = LocalDate.parse(fileDt,formatter);
				System.out.println("t "+t);
				fileDt = t.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

			}
			catch(Exception e)
			{
				System.out.println("Exception is "+e);
				return "Error while getting last Date";
			}
			
			
			HashMap<String, Object> result = cashnetSettlementService.validationForMonthlyInterchange(nfsSettlementBean);
			if(result != null && (Boolean)result.get("result"))
			{
				

				nfsSettlementBean.setDatepicker(fileDt);
				nfsSettlementBean.setToDate(lastDate);

				//2. CALLING PROCEDURE
				boolean executed = cashnetSettlementService.runCashnetInterchange(nfsSettlementBean);

				if(executed)
				{
					return "Processing Successfully Completed";
				}
				else
				{
					return "Processing Failed";
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
		else
		{
			HashMap<String, Object> result = cashnetSettlementService.validationForDailyInterchange(nfsSettlementBean);
			if(result != null && (Boolean)result.get("result"))
			{
				if(nfsSettlementBean.getDatepicker().contains(","));
				nfsSettlementBean.setDatepicker((nfsSettlementBean.getDatepicker().replace(",", "")));
				
				//2. CALLING PROCEDURE
				boolean executed = cashnetSettlementService.runCashnetDailyInterchange(nfsSettlementBean);

				if(executed)
				{
					return "Processing Successfully Completed";
				}
				else
				{
					return "Processing Failed";
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
	}
	
	@RequestMapping(value = "ValidateDownloadInterchangeReport", method = RequestMethod.POST)
	@ResponseBody
	public String ValidateDownloadInterchangeReport(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
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
			HashMap<String,Object> checkData = null;
			if(nfsSettlementBean.getTimePeriod().equalsIgnoreCase("MONTHLY"))
			{
				//CHECK WHETHER INTERCHANGE IS PROCESSED OR NOT
				 checkData = cashnetSettlementService.checkInterchangeProcess(nfsSettlementBean);
			}
			else
			{
				//CHECK WHETHER INTERCHANGE IS PROCESSED OR NOT
				 checkData = cashnetSettlementService.checkDailyInterchangeProcess(nfsSettlementBean);
			}
		
			if(checkData != null && (Boolean)checkData.get("result"))
			{
				return "success";
				
			}
			else
			{
				return checkData.get("msg").toString();
			}
	}
	
	@RequestMapping(value = "DownloadInterchangeReport", method = RequestMethod.POST)
	public String DownloadInterchangeReport(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadSettlementreport.POST Start ****");
		//logger.info("Data "+filename+" "+category+" "+stSubCategory+" "+datepicker+" "+cycle);
		logger.info("NFSSettlement POST");
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
			
			if(nfsSettlementBean.getTimePeriod().equalsIgnoreCase("MONTHLY"))
			{
				//CHECK WHETHER INTERCHANGE IS PROCESSED OR NOT
				HashMap<String,Object> checkData = cashnetSettlementService.checkInterchangeProcess(nfsSettlementBean);

				if(checkData != null && (Boolean) checkData.get("result"))
				{	//GET DATA FOR REPORT
					Excel_data = cashnetSettlementService.getInterchangeData(nfsSettlementBean);
				}
				model.addAttribute("ReportName", "Cashnet_Monthly_Settlement");
				model.addAttribute("Monthly_data", Excel_data);
				logger.info("***** NFSSettlementController.NFSSettlementProcess Daily POST End ****");
				return "GenerateNFSMonthlyReport";
			}
			else
			{

				//CHECK WHETHER INTERCHANGE IS PROCESSED OR NOT
				HashMap<String,Object> checkData = cashnetSettlementService.checkDailyInterchangeProcess(nfsSettlementBean);

				if(checkData != null && (Boolean) checkData.get("result"))
				{	//GET DATA FOR REPORT
					Excel_data = cashnetSettlementService.getDailyInterchangeData(nfsSettlementBean);
				}
				model.addAttribute("ReportName", "Cashnet_Daily_Settlement");
				model.addAttribute("Monthly_data", Excel_data);
				logger.info("***** NFSSettlementController.NFSSettlementProcess Daily POST End ****");
				return "GenerateNFSMonthlyReport";
			
			}
		
	}
/************************** CASHNET TTUM CODING**********************************************/	
	@RequestMapping(value = "CashnetInterchangeTTUM", method = RequestMethod.GET)
	public ModelAndView CashnetInterchangeTTUM(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** CashnetInterchangeTTUM.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("CashnetInterchangeTTUM GET");
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
		modelAndView.setViewName("GenerateCashnetInterchangeTTUM");

		logger.info("***** CashnetSettlementController.CashnetInterchangeTTUM GET End ****");
		return modelAndView;
	}
	@RequestMapping(value = "ProcessCashnetInterchangeTTUM", method = RequestMethod.POST)
	@ResponseBody
	public String processInterchangeTTUM(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** ProcessCashnetInterchangeTTUM.POST Start ****");
		logger.info("ProcessCashnetInterchangeTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		
			HashMap<String, Object> result = cashnetSettlementService.validateForInterchangeTTUM(nfsSettlementBean);
			if(result != null && (Boolean)result.get("result"))
			{
				if(nfsSettlementBean.getDatepicker().contains(","));
				nfsSettlementBean.setDatepicker((nfsSettlementBean.getDatepicker().replace(",", "")));
				if(nfsSettlementBean.getDatepicker().equalsIgnoreCase("MONTHLY"))
				{
					String fileDt = nfsSettlementBean.getDatepicker();
					fileDt = "01/"+fileDt;
					nfsSettlementBean.setDatepicker(fileDt);
				}

				//2. CALLING PROCEDURE
				boolean executed = cashnetSettlementService.runInterchangeTTUM(nfsSettlementBean);

				if(executed)
				{
					return "Processing Successfully Completed";
				}
				else
				{
					return "Processing Failed";
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
	@RequestMapping(value = "ValidateDownloadInterchangeTTUM", method = RequestMethod.POST)
	@ResponseBody
	public String ValidateDownloadInterchangeTTUM(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadInterchangeTTUM.POST Start ****");
		//logger.info("Data "+filename+" "+category+" "+stSubCategory+" "+datepicker+" "+cycle);
		logger.info("DownloadInterchangeTTUM POST");
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
			
			
				//CHECK WHETHER INTERCHANGE IS PROCESSED OR NOT
				HashMap<String,Object> checkData = cashnetSettlementService.checkTTUMProcess(nfsSettlementBean);

				if(checkData != null && (Boolean) checkData.get("result"))
				{
					return "success";
				}
				else
				{
					return checkData.get("msg").toString();
				}
			
		
	}
	@RequestMapping(value = "DownloadInterchangeTTUM", method = RequestMethod.POST)
	public String DownloadInterchangeTTUM(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadInterchangeTTUM.POST Start ****");
		//logger.info("Data "+filename+" "+category+" "+stSubCategory+" "+datepicker+" "+cycle);
		logger.info("DownloadInterchangeTTUM POST");
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
			
					Excel_data = cashnetSettlementService.getTTUMData(nfsSettlementBean);
				
				model.addAttribute("ReportName", "Cashnet_"+nfsSettlementBean.getTimePeriod()+"_TTUM");
				model.addAttribute("data", Excel_data);

				logger.info("***** CashnetSettlemetnController.NFSSettlementProcess Daily POST End ****");
				return "GenerateNFSDailyReport";
			
		
	}
/*********************** ADJUSTMENT FILE UPLOAD MODULE ****************/
	@RequestMapping(value = "CashnetAdjustmentFileUpload", method = RequestMethod.GET)
	public ModelAndView CashnetAdjustmentFileUpload(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** AdjustmentFileUpload.Get Start ****");
		NFSSettlementBean beanObj = new NFSSettlementBean();
		logger.info("nfsFileUpload GET");
		String display="";
		 logger.info("in GetHeaderList"+category);
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("category", category);
        modelAndView.addObject("nfsSettlementBean",beanObj);
		modelAndView.setViewName("CashnetAdjustmentFileUpload");
		
		logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "CashnetAdjustmentFileUpload", method = RequestMethod.POST)
	@ResponseBody
	public String CashnetAdjustmentFileUploadPost(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			@RequestParam("file") MultipartFile file, HttpSession httpSession,
			Model model,ModelAndView modelAndView,RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** CashnetAdjustmentFileUploadPost.Post Start ****");
		HashMap<String, Object> output = null;
		try
		{
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is "+Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			// VALIDATE IF FILE IS ALREADY UPLOADED
			nfsSettlementBean.setFileName(file.getOriginalFilename());
			
			output = cashnetSettlementService.CheckAdjustmentFileUpload(nfsSettlementBean);
			
			if(output != null && (Boolean) output.get("result"))
			{
				output = cashnetSettlementService.uploadAdjustmentFile(nfsSettlementBean, file);

				if(output != null && (Boolean) output.get("result"))
				{
					return "File uploaded Successfully. \n Record count is "+output.get("count").toString(); 
				}
				else
				{
					return output.get("count").toString();
				}
			}
			else
			{
				return output.get("msg").toString();
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
	
/***** CASHNET SETTLEMENT REPORT ***/
	@RequestMapping(value = "CashnetSettlement", method = RequestMethod.GET)
	public ModelAndView NFSSettlement(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
		logger.info("***** CashnetSettlement.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NFSSettlement GET");
		 
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("category", "CASHNET");
        modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("GenerateCashnetSettlement");
		
		logger.info("***** NFSSettlementController.NFSSettlement GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "CashnetSettlement", method = RequestMethod.POST)
	@ResponseBody
	public String CashnetSettlementValidation(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj
			,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** NFSSettlementProcess.Post Start ****");
		logger.info("data is "+beanObj.getDatepicker()+" "+beanObj.getCycle());
		logger.info("File Name is "+beanObj.getFileName());
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		//CHECK WHETHER SETTLEMENT IS ALREADY PROCESSED
		output = cashnetSettlementService.CheckSettlementProcess(beanObj);
		
		if(beanObj.getFileName().equalsIgnoreCase("REPORT"))
		{

			if(output != null && !(Boolean) output.get("result") && output.get("msg").toString().equalsIgnoreCase("Settlement Report is not processed"))
			{
				//CHECK WHETHER ADJUSTMENT FILE AND RAW FILE IS UPLOADED
				output = cashnetSettlementService.CheckAdjNRawFileUpload(beanObj);

				if(output != null && (Boolean)output.get("result"))
				{

					boolean executeFlag = cashnetSettlementService.runCashnetSettlement(beanObj);

					if(executeFlag)
					{
						return "Settlement Report Processed Successfully.\nPlease download reports";
					}
					else
					{
						return "Issue while processing Settlement";
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
			if(output != null && (Boolean) output.get("result"))
			{
				//CHECK WHETHER voucher is already processed
				output = cashnetSettlementService.CheckSettlementVoucher(beanObj);
				
				if(output != null && !(Boolean)output.get("result") && output.get("msg").toString().equalsIgnoreCase("Settlement Voucher is not processed"))
				{

					boolean executeFlag = cashnetSettlementService.runCashnetSettlementVouch(beanObj);

					if(executeFlag)
					{
						return "Settlement Voucher Processed Successfully.\nPlease download Voucher";
					}
					else
					{
						return "Issue while processing Settlement";
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
		
	}
	
	@RequestMapping(value = "CashnetProcessValidation", method = RequestMethod.POST)
	@ResponseBody
	public String CashnetProcessValidation(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		HashMap<String, Object>  output ;
		logger.info("Inside CashnetProcessValidation Post");
		logger.info("File name is "+beanObj.getFileName());
		if(beanObj.getFileName().equalsIgnoreCase("REPORT"))
		{
			output = cashnetSettlementService.CheckSettlementProcess(beanObj);

			if(output != null && (Boolean) output.get("result"))
			{
				return "success";
			}
			else
			{
				return output.get("msg").toString();
			}
		}
		else
		{
			output = cashnetSettlementService.CheckSettlementVoucher(beanObj);
			if(output != null && (Boolean) output.get("result"))
			{
				return "success";
			}
			else
			{
				return output.get("msg").toString();
			}
		}
	
	}
	
	@RequestMapping(value = "DownloadCashnetSettReport", method = RequestMethod.POST)
	public String DownloadCashnetSettReport(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadCashnetSettReport.POST Start ****");
		List<Object> Excel_data = new ArrayList<Object>();
		
		if(beanObj.getFileName().equalsIgnoreCase("REPORT"))
		{
				//GET DATA FOR REPORT
				Excel_data = cashnetSettlementService.getCashnetSettlementReport(beanObj);
			
			model.addAttribute("ReportName", "Cashnet_Settlement_"+beanObj.getDatepicker()+"_Cycle"+beanObj.getCycle());
			model.addAttribute("data", Excel_data);
			logger.info("***** NFSSettlementController.NFSSettlementProcess Daily POST End ****");
			return "GenerateNFSDailyReport";
		}
		else
		{
			//GET DATA FOR REPORT
			Excel_data = cashnetSettlementService.getCashnetSettVoucher(beanObj);

			model.addAttribute("ReportName", "Cashnet_SettVoucher_"+beanObj.getDatepicker());
			model.addAttribute("data", Excel_data);
			logger.info("***** NFSSettlementController.NFSSettlementProcess Daily POST End ****");
			return "GenerateNFSDailyReport";
		}
		
	}
	
	@RequestMapping(value = "CashnetSettRollback", method = RequestMethod.POST)
	@ResponseBody
	public String CashnetSettRollbackPost(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj
			,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** CashnetSettRollbackPost.Post Start ****");
		logger.info("data is "+beanObj.getDatepicker()+" "+beanObj.getCycle());
		logger.info("File Name is "+beanObj.getFileName());
		HashMap<String, Object> output = new HashMap<String, Object>();
		
		//CHECK WHETHER SETTLEMENT IS ALREADY PROCESSED
		output = cashnetSettlementService.CheckSettlementProcess(beanObj);
		
		if(beanObj.getFileName().equalsIgnoreCase("REPORT"))
		{

			if(output != null && (Boolean) output.get("result") && output.get("msg").toString().equalsIgnoreCase("Settlement Report is already processed"))
			{
				//check if voucher is processed
				output = cashnetSettlementService.CheckSettlementVoucher(beanObj);
				if(output != null && !(Boolean) output.get("result")){
					//rollback report
					if(cashnetSettlementService.CashnetSettlementRollback(beanObj))
					{
						return "Rollback completed";
					}
					else
					{
						return "Issue while rolling back Settlement";
					}
					
				}
				else
				{
					return "Please rollback Voucher first";
				}
				
				
			}
			else
			{
				return output.get("msg").toString();
			}
		}
		else
		{
				//CHECK WHETHER voucher is already processed
				output = cashnetSettlementService.CheckSettlementVoucher(beanObj);
				
				if(output != null && (Boolean)output.get("result"))
				{
					//rollback report
					if(cashnetSettlementService.CashnetSettlementRollback(beanObj))
					{
						return "Rollback completed";
					}
					else
					{
						return "Issue while rolling back Voucher";
					}
				}
				else
				{
					return output.get("msg").toString();
				}
			
		}
		
	}
}
