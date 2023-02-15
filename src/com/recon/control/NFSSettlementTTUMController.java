package com.recon.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.SettlementBean;
import com.recon.service.ISourceService;
import com.recon.service.NFSSettlementService;
import com.recon.service.NFSSettlementTTUMService;
import com.recon.util.CSRFToken;
import com.recon.util.GenerateUCOTTUM;

@Controller
public class NFSSettlementTTUMController {

	private static final Logger logger = Logger.getLogger(NFSSettlementTTUMController.class);

	@Autowired
	NFSSettlementTTUMService NFSttumService;

	@Autowired
	NFSSettlementService nfsSettlementService;

	@Autowired ISourceService iSourceService;

	@RequestMapping(value = "NFSSettlementTTUM", method = RequestMethod.GET)
	public ModelAndView NFSSettlementTTUM(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
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
		modelAndView.setViewName("NFSSettlementTTUM");

		logger.info("***** NFSSettlementController.nfsFileUpload GET End ****");
		return modelAndView;
	}
	@RequestMapping(value = "NFSMonthlySettValidation", method = RequestMethod.POST)
	@ResponseBody
	public String NFSMonthlyValidation(String fileDate,String stSubCategory,String timePeriod,String cycle,String filename,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** NFSSettlementProcess.Post Start ****");
		String lastDate = fileDate;
		logger.info("NFSMonthlyValidation POST");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();

		logger.info("filename is "+filename);
		nfsSettlementBean.setFileName(filename);
		nfsSettlementBean.setStSubCategory(stSubCategory);
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		//	nfsSettlementBean.setCycle(Integer.parseInt(cycle));
		if(timePeriod != null && timePeriod.equalsIgnoreCase("Monthly"))
		{	
			if(stSubCategory.equalsIgnoreCase("ACQUIRER"))
			{
				logger.info("File date is "+fileDate);
				fileDate = "01/"+fileDate;
				logger.info("Filedate is "+fileDate);
				nfsSettlementBean.setDatepicker(fileDate);
				nfsSettlementBean.setStSubCategory(stSubCategory);
				nfsSettlementBean.setCategory("MONTHLY_SETTLEMENT");

				HashMap<String, Object> result = NFSttumService.validateMonthlySettlement(nfsSettlementBean);

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
				return "Development is in Progress!!!";
			}
		}
		else
		{
			//CODING FOR DAILY INTERCHANGE TTUM DOWNLOAD
			logger.info("File date is "+fileDate);
			/*
			 * fileDate = "01/"+fileDate; logger.info("Filedate is "+fileDate);
			 */
			nfsSettlementBean.setDatepicker(fileDate);
			nfsSettlementBean.setStSubCategory(stSubCategory);
			nfsSettlementBean.setCategory("NFS_SETTLEMENT");

			HashMap<String, Object> result = NFSttumService.validateDailyInterchange(nfsSettlementBean);

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

	@RequestMapping(value = "DownloadSettlementTTUM", method = RequestMethod.POST)
	@ResponseBody
	public void DownloadSettlementTTUM(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpServletResponse response,HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadSettlementTTUM.POST Start ****");
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

			nfsSettlementBean.setCategory("MONTHLY_SETTLEMENT");
			//nfsSettlementBean.setFileName("NTSL-NFS");
			if(nfsSettlementBean.getDatepicker().contains(","));
			nfsSettlementBean.setDatepicker((nfsSettlementBean.getDatepicker().replace(",", "")));

			//2. CALLING PROCEDURE
			nfsSettlementBean.setDatepicker("01/"+nfsSettlementBean.getDatepicker());
			boolean executed = NFSttumService.runNFSMonthlyTTUM(nfsSettlementBean);


			if(executed)
			{
				//4. REPORT DOWNLOADING
				Excel_data = NFSttumService.getMonthlyTTUMData(nfsSettlementBean);
			}
			/*model.addAttribute("ReportName", "TTUM");
			model.addAttribute("data", Excel_data);

			logger.info("***** NFSSettlementController.NFSSettlementProcess monthly POST End ****");
			return "GenerateNFSDailyReport";*/
			
			String fileName = "NFS_MONTHLY_TTUM.txt";
			
			String stPath = System.getProperty("java.io.tmpdir");
			logger.info("TEMP_DIR"+stPath);
			
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker(), "NFS");
			
			obj.generateMultipleTTUMFiles(stPath, fileName, 2, Excel_data);
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
		else
		{
			//CODE FOR DAILY TTUM Download
			if(nfsSettlementBean.getDatepicker().contains(","));
			nfsSettlementBean.setDatepicker((nfsSettlementBean.getDatepicker().replace(",", "")));

			//check whether ttum is process. If yes then get only data
			boolean CheckProcess = NFSttumService.checkDailyInterchangeTTUMProcess(nfsSettlementBean);
			
			if(!CheckProcess)
			{//2. CALLING PROCEDURE
				NFSttumService.runDailyInterchangeTTUM(nfsSettlementBean);
			}

				//4. REPORT DOWNLOADING
				Excel_data = NFSttumService.getDailyInterchangeTTUMData(nfsSettlementBean);

			/*	model.addAttribute("ReportName", "TTUM");
			model.addAttribute("data", Excel_data);

			logger.info("***** NFSSettlementController.NFSSettlementProcess Daily POST End ****");
			return "GenerateNFSDailyReport";*/
		
				String fileName = "NFS_Daily_TTUM_"+nfsSettlementBean.getCycle()+".txt";
				
				String stPath = System.getProperty("java.io.tmpdir");
				logger.info("TEMP_DIR"+stPath);
				
				GenerateUCOTTUM obj = new GenerateUCOTTUM();
				stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker(), "NFS");
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
	}

	//.................. CODING FOR SETTLEMENT VOUCHER DOWNLOAD...................
	@RequestMapping(value = "NFSSettVoucher", method = RequestMethod.GET)
	public ModelAndView NFSSettlementVoucher(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** NFSSettVoucher.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NFSSettVoucher GET");
		String display="";
		logger.info("in GetHeaderList"+category);

		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		//modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("GenerateNFSSettVoucher");

		logger.info("***** NFSSettlementController.NFSSettlement GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = "NFSSettVoucherValidation", method = RequestMethod.POST)
	@ResponseBody
	public String NFSSettVoucherValidation(String fileDate,String stSubCategory,String timePeriod,String cycle,String filename,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** NFSSettVoucherValidation.Post Start ****");
		String lastDate = fileDate;
		logger.info("NFSSettVoucherValidation POST");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
		logger.info("filename is "+filename);
		nfsSettlementBean.setFileName(filename);
		nfsSettlementBean.setStSubCategory(stSubCategory);
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		nfsSettlementBean.setCycle(Integer.parseInt(cycle));
		logger.info("cycle is "+cycle);
		HashMap<String, Object> result;

		//For Daily Settlement
		nfsSettlementBean.setDatepicker(fileDate);
		
		if(nfsSettlementBean.getFileName().contains("PBGB"))
		{
			result = nfsSettlementService.CheckSettlementProcess(nfsSettlementBean);
			
		}
		else
		{
			result = nfsSettlementService.ValidateForSettVoucher(nfsSettlementBean);
		}

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

		//return "success";

	}

	/*@RequestMapping(value = "DownloadSettVoucher", method = RequestMethod.POST)
	@ResponseBody
	public void DownloadSettVoucher(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadSettlementreport.POST Start ****");
		//logger.info("Data "+filename+" "+category+" "+stSubCategory+" "+datepicker+" "+cycle);
		logger.info("NFSSettlement POST");
		nfsSettlementBean.setCategory("NFS_SETTLEMENT");
		//			nfsSettlementBean.setFileName(filename);
		
		if(!nfsSettlementBean.getFileName().equalsIgnoreCase("NTSL-NFS"))
		{
			nfsSettlementBean.setCycle(1);
		}
		
		List<Object> Excel_data = new ArrayList<Object>();
		List<Object> TTUMData = new ArrayList<Object>();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);

		//GETTING , IN DATE FIELD
		logger.info("File Name is "+nfsSettlementBean.getFileName());
		boolean executed = false;

		if(nfsSettlementBean.getDatepicker().contains(","));
		nfsSettlementBean.setDatepicker((nfsSettlementBean.getDatepicker().replace(",", "")));

		boolean checkProcFlag = nfsSettlementService.checkSettVoucherProcess(nfsSettlementBean);
		executed = NFSttumService.runSettlementVoucher(nfsSettlementBean);

		if(executed)
		{
			//GET DATA FOR REPORT
			TTUMData = NFSttumService.getSettlementVoucher(nfsSettlementBean);
		}
		
		String fileName = "SETTLEMENT_VOUCHER_"+nfsSettlementBean.getCycle()+".txt";
		
		String stPath = System.getProperty("java.io.tmpdir");
		logger.info("TEMP_DIR"+stPath);
		
		GenerateUCOTTUM obj = new GenerateUCOTTUM();
		stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker(), nfsSettlementBean.getCategory());
		obj.generateTTUMFile(stPath, fileName, TTUMData);
		logger.info("File is created");
		
		List<String> Column_list = new ArrayList<String>();
		Column_list.add("ACCOUNT_NUMBER");
		Column_list.add("PART_TRAN_TYPE");	
		Column_list.add("TRANSACTION_AMOUNT");	
		Column_list.add("TRANSACTION_PARTICULAR");	
		
		Excel_data.add(Column_list);
		List<Object> SettData = new ArrayList<Object>();
		SettData.add(TTUMData);
		Excel_data.add(SettData);
		
		fileName = "SETTLEMENT_VOUCHER_"+nfsSettlementBean.getCycle()+".xls";
		String zipName = "SETTLEMENT_VOUCHER_"+nfsSettlementBean.getCycle()+".zip";
		
		obj.generateExcelTTUM(stPath, fileName, Excel_data,"REFUND",zipName);
		logger.info("File is created");
		
		//File file = new File(stPath +File.separator+"EXCEL_TTUMS.zip");
		File file = new File(stPath +File.separator+zipName);
		logger.info("path of zip file "+stPath +File.separator+zipName);
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

	}	*/
	

	/*************** coding for ADJ TTUM***************/		
	@RequestMapping(value = "NFSAdjustmentTTUM", method = RequestMethod.GET)
	public ModelAndView NFSAdjustmentTTUM(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** NFSSettlement.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NFSSettlement GET");
		String display="";
		logger.info("in GetHeaderList"+category);

		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		//modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("GenerateNFSAdjustmentTTUM");

		logger.info("***** NFSSettlementController.NFSSettlement GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = "NFSAdjustmentProcess", method = RequestMethod.POST)
	@ResponseBody
	public String NFSAdjTTUMValidation(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** NFSAdjTTUMValidation.Post Start ****");
		/*String lastDate = fileDate;
		logger.info("NFSAdjTTUMValidation POST");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		nfsSettlementBean.setCategory("NFS_ADJUSTMENT");
		logger.info("filename is "+filename);
		nfsSettlementBean.setFileName(filename);
		nfsSettlementBean.setStSubCategory(stSubCategory);
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);
		nfsSettlementBean.setCycle(Integer.parseInt(cycle));
		//For Daily Settlement
		nfsSettlementBean.setDatepicker(fileDate);*/
		logger.info("ADjtype is "+nfsSettlementBean.getAdjType());
		nfsSettlementBean.setCategory("NFS_ADJUSTMENT");
		HashMap<String, Object> result = nfsSettlementService.ValidateForAdjTTUM(nfsSettlementBean);
		if(result != null && (Boolean)result.get("result"))
		{
			//return "success";
				boolean executed = NFSttumService.runAdjTTUM(nfsSettlementBean);
				if(executed)
				{
					return "Adjustment TTUM Processing Completed.\n Please Download the Reports";
				}
				else
				{
					return "Exception while processing TTUM";
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

		//return "success";

	}
	@RequestMapping(value = "ValidateDownloadAdjTTUM", method = RequestMethod.POST)
	@ResponseBody
	public String ValidateDownloadAdjTTUM(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadSettlementreport.POST Start ****");
		//logger.info("Data "+filename+" "+category+" "+stSubCategory+" "+datepicker+" "+cycle);
		logger.info("DownloadAdjTTUM POST");

		nfsSettlementBean.setCategory(nfsSettlementBean.getFileName().split("-")[1]+"_ADJUSTMENT");
		//			nfsSettlementBean.setFileName(filename);
		List<Object> Excel_data = new ArrayList<Object>();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);

		//GETTING , IN DATE FIELD
		logger.info("File Name is "+nfsSettlementBean.getFileName());
		boolean executed = false;

		if(nfsSettlementBean.getDatepicker().contains(","));
		nfsSettlementBean.setDatepicker((nfsSettlementBean.getDatepicker().replace(",", "")));

		boolean checkProcFlag = nfsSettlementService.checkAdjTTUMProcess(nfsSettlementBean);

		if(checkProcFlag)
		{
			return "success";
		}
		else
		{
			return "Adjustment TTUM is not processed.\n Please process TTUM";
		}

	}	
	
	@RequestMapping(value = "DownloadAdjTTUM", method = RequestMethod.POST)
	@ResponseBody
	public void DownloadAdjTTUM(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpServletResponse response,HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadSettlementreport.POST Start ****");
		//logger.info("Data "+filename+" "+category+" "+stSubCategory+" "+datepicker+" "+cycle);
		logger.info("DownloadAdjTTUM POST");

		nfsSettlementBean.setCategory(nfsSettlementBean.getFileName().split("-")[1]+"_ADJUSTMENT");
		//			nfsSettlementBean.setFileName(filename);
		List<Object> Excel_data = new ArrayList<Object>();
		List<Object> TTUMData = new ArrayList<Object>();
		List<Object> PBGB_TTUMData = new ArrayList<Object>();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		nfsSettlementBean.setCreatedBy(Createdby);

		//GETTING , IN DATE FIELD
		logger.info("File Name is "+nfsSettlementBean.getFileName());
		boolean executed = false;

		if(nfsSettlementBean.getDatepicker().contains(","));
		nfsSettlementBean.setDatepicker((nfsSettlementBean.getDatepicker().replace(",", "")));

			//GET DATA FOR REPORT
			TTUMData = NFSttumService.getAdjTTUM(nfsSettlementBean);
			
			PBGB_TTUMData = NFSttumService.getPBGBAdjTTUM(nfsSettlementBean);
			
			/*model.addAttribute("ReportName", nfsSettlementBean.getStSubCategory()+"_Adjustment_TTUM_"+nfsSettlementBean.getCycle());
			model.addAttribute("Monthly_data", Excel_data);
			logger.info("***** NFSSettlementController.DownloadSettlementreport POST End ****");
			return "GenerateNFSMonthlyReport";*/
			
			String fileName = nfsSettlementBean.getStSubCategory().substring(0, 3)+"_ADJUSTMENT_"+nfsSettlementBean.getAdjType()+"_TTUM.txt";
			
			String stPath = System.getProperty("java.io.tmpdir");
			logger.info("TEMP_DIR"+stPath);
			
			GenerateUCOTTUM obj = new GenerateUCOTTUM();
			stPath = obj.checkAndMakeDirectory(nfsSettlementBean.getDatepicker(), nfsSettlementBean.getCategory());
			/*List<Object> data = (List<Object>)TTUMData.get(1);
			List<Object> GLAcc = new ArrayList<Object>();
			GLAcc.add(data);
			obj.generateMultipleTTUMFiles(stPath, fileName, 2, GLAcc );  //GENERATING ALL ACC TO ACC TTUM'S
			data = (List<Object>)TTUMData.get(0);
			GLAcc.clear();
			if(data.size()>0)
			{*/
				//fileName = nfsSettlementBean.getStSubCategory()+"_Adjustment_Acceptance_TTUM.txt";
			if(!nfsSettlementBean.getAdjType().equalsIgnoreCase("PENALTY") && !nfsSettlementBean.getAdjType().equalsIgnoreCase("FEE"))
			{
				obj.generateMultipleDRMTTUMFiles(stPath, fileName, 1, TTUMData,"NFS");  // for generating customer ttum
				fileName = "PBGB_"+nfsSettlementBean.getStSubCategory().substring(0, 3)+"_ADJUSTMENT_"+nfsSettlementBean.getAdjType()+"_TTUM.txt";
				obj.generateMultipleTTUMFiles(stPath, fileName, 1, PBGB_TTUMData);
			}
			else
			{
				fileName = nfsSettlementBean.getStSubCategory().substring(0, 3)+"_ADJ_"+nfsSettlementBean.getAdjType()+"_TTUM.txt";
				obj.generateMultipleTTUMFiles(stPath, fileName, 1, TTUMData);
				
				fileName = "PBGB_"+nfsSettlementBean.getStSubCategory().substring(0, 3)+"_ADJUSTMENT_"+nfsSettlementBean.getAdjType()+"_TTUM.txt";
				obj.generateMultipleTTUMFiles(stPath, fileName, 1, PBGB_TTUMData);
			}
			//}
			
			
			logger.info("File is created");
			
			/*** creating excel ***/
			List<String> Column_list = new ArrayList<String>();
			Column_list.add("ACCOUNT_NUMBER");
			Column_list.add("PART_TRAN_TYPE");	
			Column_list.add("TRANSACTION_AMOUNT");	
			Column_list.add("TRANSACTION_PARTICULAR");	
			Column_list.add("ADJTYPE");	
			
			Excel_data.add(Column_list);
			Excel_data.add(TTUMData);
			
			fileName = nfsSettlementBean.getStSubCategory().substring(0,3)+"_ADJUSTMENT_"+nfsSettlementBean.getAdjType()+"_TTUMS.xls";
			String zipName = nfsSettlementBean.getStSubCategory().substring(0,3)+"_ADJ_"+
					nfsSettlementBean.getAdjType()+"_TTUM.zip";
			obj.generateExcelTTUM(stPath, fileName, Excel_data,"REFUND",zipName);
			logger.info("File is created");
			
			
			//File file = new File(stPath +File.separator+fileName);
			//File file = new File(stPath +File.separator+"EXCEL_TTUMS.zip");
			File file = new File(stPath +File.separator+zipName);
			logger.info("path of zip file "+stPath +File.separator+zipName);
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


	/********************** settlement TTUM *************************/
	@RequestMapping(value = "NFSSettlementVouc", method = RequestMethod.GET)
	public ModelAndView NFSSettlement(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** NFSSettVoucher.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NFSSettVoucher GET");
		String display="";
		logger.info("in GetHeaderList"+category);

		String csrf = CSRFToken.getTokenForSession(request.getSession());
		modelAndView.addObject("CSRFToken", csrf);
		modelAndView.addObject("category", category);
		//modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("GenerateNFSSettlementTTUM");

		logger.info("***** NFSSettlementController.NFSSettlement GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "NFSSettlementProcess", method = RequestMethod.POST)
	@ResponseBody
	public String NFSSettlementProcess(@ModelAttribute("nfsSettlementBean") NFSSettlementBean SettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** NFSSettlementProcess.Post Start ****");
		
		
		//check if REport is processed and difference is rectified
		HashMap<String, Object> output = nfsSettlementService.ValidateForSettVoucher(SettlementBean);

		if(output != null && (Boolean) output.get("result"))
		{
			// execute Voucher
			boolean executed = NFSttumService.runSettlementVoucher(SettlementBean);
			
			if(executed)
			{
				return "Voucher Processed Successfully.\n Download Report";
			}
			else
			{
				return "Exception while processing Settlement Report";
			}
			
		}
		else
		{
			return output.get("msg").toString();
		}

		//return "success";

	}

	@RequestMapping(value = "NFSSettlementTTUMValidation", method = RequestMethod.POST)
	@ResponseBody
	public String NFSSettlementValidation(@ModelAttribute("nfsSettlementBean") NFSSettlementBean SettlementBean,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** NFSSettlementValidation.Post Start ****");
		HashMap<String, Object> output;

		//check if already processed
			output = nfsSettlementService.ValidateForSettVoucher(SettlementBean);

		if(output != null && !(Boolean)output.get("result") && output.get("msg").toString().equalsIgnoreCase("Settlement Voucher is already processed"))
		{
			return "success";
		}
		else
		{
			return "Settlement Voucher is not Processed";
		}

		//return "success";

	}

	@RequestMapping(value = "DownloadNFSSettlement", method = RequestMethod.POST)
	public String DownloadSettlement(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadSettlement.DownloadSettlement.Get Start ****");
		String display="";
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		List<Object> Excel_data = new ArrayList<Object>();
		
		Excel_data = NFSttumService.getSettlementVoucher(nfsSettlementBean);
		
		model.addAttribute("ReportName", "NFS_Settlement_Voucher");
		model.addAttribute("data", Excel_data);
		
		logger.info("***** VisaSettlementController.DownloadVisaSettlementReport POST End ****");
		return "GenerateVisaSettlementReport";
		
	}	
}
