package com.recon.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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
import com.recon.service.EODReportService;
import com.recon.service.ISettelmentService;
import com.recon.service.ISourceService;
import com.recon.util.CSRFToken;
import com.recon.util.GenerateDLBVoucher;

@Controller
public class GenerateDhanaReportsController {
	
	@Autowired
	ISourceService iSourceService;
	
	@Autowired
	ISettelmentService isettelmentservice;
	
	@Autowired
	EODReportService eodService;
	
	private static final Logger logger = Logger
			.getLogger(SettlementController.class);
	private static final String ERROR_MSG = "error_msg";
	
	// ADDED BY INT5779 AS ON 14TH MARCH 2018 FOR DOWNLOADING REPORTS
		@RequestMapping(value = "DownloadDhanaReports", method = RequestMethod.GET)
		public ModelAndView getdownloadPage(ModelAndView modelAndView,HttpServletRequest request,
				SettlementBean settlementBean,
				@RequestParam("category") String category) throws Exception {
			List<String> subcat = new ArrayList<>();

			System.out.println("in GetHeaderList" + category);

			subcat = iSourceService.getSubcategories(category);

			modelAndView.addObject("category", category);
			modelAndView.addObject("subcategory", subcat);
			String csrf = CSRFToken.getTokenForSession(request.getSession());
			 
			//redirectAttributes.addFlashAttribute("CSRFToken", csrf);
			modelAndView.addObject("CSRFToken", csrf);

			modelAndView.addObject("SettlementBean", settlementBean);
			modelAndView.setViewName("DownloadDhanaReports");

			return modelAndView;

		}
		
		@RequestMapping(value = "DownloadDhanaReports", method = RequestMethod.POST)
		public void downloadDhanaReports(
				@ModelAttribute("SettlementBean") SettlementBean SettlementBean,
				HttpServletResponse response, HttpServletRequest request,
				RedirectAttributes redirectAttributes) {

			String TEMP_DIR = System.getProperty("java.io.tmpdir");
			ServletContext context = request.getServletContext();
			try {
				logger.info("INSIDE DOWNLOAD DHANA REPORTS");
				// DELETING FILES FROM DRIVE
				/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				java.util.Date date = sdf.parse(SettlementBean.getDatepicker());

				sdf = new SimpleDateFormat("dd-MM-yyyy");

				String stnewDate = sdf.format(date);*/
				//INT 8624
				String stpath = TEMP_DIR+File.separator+SettlementBean.getCategory();
				System.out.println("stpath is "+stpath);
				isettelmentservice.DeleteFiles(stpath);
				SettlementBean.setStPath(stpath);

				if (!SettlementBean.getStsubCategory().equals("-")) {
					SettlementBean.setStMergerCategory(SettlementBean.getCategory()
							+ "_"
							+ SettlementBean.getStsubCategory().substring(0, 3));
				} else
					SettlementBean
							.setStMergerCategory(SettlementBean.getCategory());
				
				String stFileName = isettelmentservice.generate_Dhana_Reports(SettlementBean);

				File file = new File(SettlementBean.getStPath() +File.separator+stFileName);
				logger.info("path of zip file "+SettlementBean.getStPath() +File.separator +stFileName);
				FileInputStream inputstream = new FileInputStream(file);
				response.setContentLength((int) file.length());
				logger.info("before downloading zip file ");
				response.setContentType("application/zip");
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

				// DELETING FILES FROM DRIVE
				/*java.util.Date varDate = null;
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
				try {
					varDate = dateFormat.parse(SettlementBean.getDatepicker());
					dateFormat = new SimpleDateFormat("MM/dd/yyyy");
					System.out.println("Date :" + dateFormat.format(varDate));
					SettlementBean.setDatepicker(dateFormat.format(varDate));
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
*/

			} catch (Exception e) {
				logger.info("Exception in downloadReports " + e);
				System.out.println("Exception in downloadReports " + e);
				redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
			}

		}
		
		@RequestMapping(value = "EODReport", method = RequestMethod.GET)
		public ModelAndView getEODReport(ModelAndView modelAndView,HttpServletRequest request,
				SettlementBean settlementBean) throws Exception {
			List<String> subcat = new ArrayList<>();

			modelAndView.addObject("subcategory", subcat);
			String csrf = CSRFToken.getTokenForSession(request.getSession());
			 
			//redirectAttributes.addFlashAttribute("CSRFToken", csrf);
			modelAndView.addObject("CSRFToken", csrf);

			modelAndView.addObject("SettlementBean", settlementBean);
			modelAndView.setViewName("GenerateEODReport");

			return modelAndView;

		}
		
		@RequestMapping(value = "ProcessEODReport", method = RequestMethod.POST)
		@ResponseBody
		public String EODReportProcess(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj
				,HttpServletRequest request,
				HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
			logger.info("***** NFSSettlementProcess.Post Start ****");
			logger.info("data is "+beanObj.getDatepicker()+" "+beanObj.getCycle());
			logger.info("File Name is "+beanObj.getFileName());
			HashMap<String, Object> output = new HashMap<String, Object>();
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is "+Createdby);
			
			beanObj.setCreatedBy(Createdby);
			
			//CHECK WHETHER EOD is ALREADY PROCESSED
			output = eodService.checkEODReportProcess(beanObj);
			
			if(beanObj.getFileName().equalsIgnoreCase("REPORT"))
			{

				if(output != null && !(Boolean) output.get("result"))
				{
					//CHECK WHETHER CBS Raw file is uploaded
					output = eodService.checkCBSFileUpload(beanObj);
					
					if(output != null && (Boolean)output.get("result"))
					{

						output = eodService.runEODReport(beanObj);

						if(output!= null && (Boolean) output.get("result"))
						{
							return "EOD Report is processed. \n Please download Report";
						}
						else
						{
							return "Issue while processing EOD Report";
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
				//voucher process
				
				return "Development under process";
			}
			
		}
		
		@RequestMapping(value = "EODReportProcessValidation", method = RequestMethod.POST)
		@ResponseBody
		public String EODReportProcessValidation(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj,HttpServletRequest request,
				HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
			HashMap<String, Object>  output ;
			logger.info("Inside CashnetProcessValidation Post");
			logger.info("File name is "+beanObj.getFileName());
			/*if(beanObj.getFileName().equalsIgnoreCase("REPORT"))
			{*/
				output = eodService.checkEODReportProcess(beanObj);

				if(output != null && (Boolean) output.get("result"))
				{
					return "success";
				}
				else
				{
					return output.get("msg").toString();
				}
			/*}
			else
			{
				
				return "Please click on download";
			}*/
		
		}
		
		@RequestMapping(value = "DownloadEODReport", method = RequestMethod.POST)
		public String DownloadEODReport(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj,
				HttpServletResponse response, HttpServletRequest request,
				HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
			logger.info("***** DownloadCashnetSettReport.POST Start ****");
			logger.info("type of report is "+beanObj.getFileName());
			List<Object> Excel_data = new ArrayList<Object>();
			String fileName = "";
			
					//GET DATA FOR REPORT
					Excel_data = eodService.getEODReport(beanObj);
				
				model.addAttribute("ReportName", "EOD_Report_"+beanObj.getDatepicker());
				model.addAttribute("data", Excel_data);
				logger.info("***** GEnerateDhanaReportController.DownloadEODReport End ****");
				return "GenerateNFSDailyReport";
			
			
		}
		
		@RequestMapping(value = "DownloadEODVoucher", method = RequestMethod.POST)
		@ResponseBody
		public void DownloadEODVoucher(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj,
				HttpServletResponse response, HttpServletRequest request,
				HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
			logger.info("***** DownloadEODVoucher.POST Start ****");
			logger.info("type of report is "+beanObj.getFileName());
			List<Object> Excel_data = new ArrayList<Object>();
			String fileName = "";
			
				String TEMP_DIR = System.getProperty("java.io.tmpdir");
				String stpath = TEMP_DIR+"\\EOD\\";
				System.out.println("stpath is "+stpath);
				isettelmentservice.DeleteFiles(stpath);
				
				//GET DATA FOR VOUCHER
				List<String> data = eodService.getEODVoucher(beanObj);
				
				fileName = "EOD_REPORT"+beanObj.getDatepicker().replace("/", "_")+".txt";
				
				GenerateDLBVoucher vouchObj = new GenerateDLBVoucher();
				String stPath = vouchObj.checkAndMakeDirectory(beanObj.getDatepicker(), "EOD");
				vouchObj.generateTTUMFile(stPath, fileName, data);
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
		
/****************** ONE WAY RECON REPORT LOGIC *******************/
		@RequestMapping(value = "DownloadOneWayReconReports", method = RequestMethod.GET)
		public ModelAndView DownloadOneWayReconReports(ModelAndView modelAndView,HttpServletRequest request,
				SettlementBean settlementBean
				) throws Exception {
			List<String> subcat = new ArrayList<>();

			modelAndView.addObject("subcategory", subcat);
			String csrf = CSRFToken.getTokenForSession(request.getSession());
			 
			//redirectAttributes.addFlashAttribute("CSRFToken", csrf);
			modelAndView.addObject("CSRFToken", csrf);

			modelAndView.addObject("SettlementBean", settlementBean);
			modelAndView.setViewName("GenerateOneWayReconReport");

			return modelAndView;

		}
		
		@RequestMapping(value = "OneWayReportValidation", method = RequestMethod.POST)
		@ResponseBody
		public String OneWayReportValidation(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj,HttpServletRequest request,
				HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
			HashMap<String, Object>  output ;
			logger.info("Inside OneWayReportValidation Post");
			logger.info("File name is "+beanObj.getFileName());
			
				output = eodService.checkAllReconProcess(beanObj);

				if(output != null && (Boolean) output.get("result"))
				{
					return "success";
				}
				else
				{
					return output.get("msg").toString();
				}
		
		}
		
		@RequestMapping(value = "DownloadOneWayReconReport", method = RequestMethod.POST)
		public String DownloadOneWayReconReport(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj,HttpServletRequest request,
				HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
			logger.info("***** DownloadOneWayReconReport.POST Start ****");
			List<Object> Excel_data = new ArrayList<Object>();
			
			//if(beanObj.getFileName().equalsIgnoreCase("REPORT"))
			{
					//GET DATA FOR REPORT
					Excel_data = eodService.getOneWayReconReport(beanObj);
				
				model.addAttribute("ReportName", "Switch-GL-Summary_Report_"+beanObj.getDatepicker());
				model.addAttribute("data", Excel_data);
				logger.info("***** GEnerateDhanaReportController.DownloadOneWayReconReport End ****");
				//return "GenerateOneWayReconReport";
				return "GenerateReconReport";
			}
			/*else
			{
				//GET DATA FOR REPORT
				Excel_data = cashnetSettlementService.getCashnetSettVoucher(beanObj);

				model.addAttribute("ReportName", "Cashnet_SettVoucher_"+beanObj.getDatepicker());
				model.addAttribute("data", Excel_data);
				logger.info("***** NFSSettlementController.NFSSettlementProcess Daily POST End ****");
				return "GenerateNFSDailyReport";
			}*/
			
		}

}
