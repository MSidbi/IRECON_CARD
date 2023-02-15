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

import com.recon.dao.DhanalaxmiDao;
import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.SettlementBean;
import com.recon.service.EODReportService;
import com.recon.service.ISettelmentService;
import com.recon.service.ISourceService;
import com.recon.util.CSRFToken;
import com.recon.util.GenerateDLBVoucher;

@Controller
public class DhanalaxmiSettlementController {
	
	
	@Autowired
	DhanalaxmiDao dao;
	
	private static final Logger logger = Logger
			.getLogger(DhanalaxmiSettlementController.class);
	
	private static final String ERROR_MSG = "error_msg";
	
	
	@RequestMapping(value = "DhanalaxmiSettlement", method = RequestMethod.GET)
	public ModelAndView DownloadOneWayReconReports(ModelAndView modelAndView,HttpServletRequest request
			) throws Exception {
		List<String> subcat = new ArrayList<>();

		modelAndView.addObject("subcategory", subcat);
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
		//redirectAttributes.addFlashAttribute("CSRFToken", csrf);
		modelAndView.addObject("CSRFToken", csrf);

		//modelAndView.addObject("SettlementBean", settlementBean);
		modelAndView.setViewName("DhanalaxmiSettlement");

		return modelAndView;

	}
	
	@RequestMapping(value = "DhanalaxmiSettlementValidation", method = RequestMethod.POST)
	@ResponseBody
	public String SettlementValidation(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		HashMap<String, Object>  output ;
		logger.info("Inside DhanalaxmiSettlementValidation Post");
		logger.info("File name is "+beanObj.getFileName());

		output = dao.checkFileProcess(beanObj);


		if(output != null && (Boolean) output.get("result")) {
			return "success"; 
		}
		else { 
			return output.get("msg").toString(); 
		}


		//return "success";

	}
	
	@RequestMapping(value = "DownloadSettlementReport", method = RequestMethod.POST)
	@ResponseBody
	public void DownloadSettlementReport(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,
			HttpServletResponse response) throws Exception {
		logger.info("***** DhanalaxmiSettlementController.DownloadSettlementReport post Start ****");
		logger.info("DownloadSettlementReport POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		List<Object> TTUMData = new ArrayList<Object>();
		beanObj.setCreatedBy(Createdby);
		String fileName = "";
		List<Object> Excel_data = new ArrayList<Object>();
		beanObj.setCategory("SETTLEMENT");
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yy/mm/dd");
			java.util.Date date = sdf.parse(beanObj.getDatepicker());
			sdf = new SimpleDateFormat("dd-MM-yyyy");

			String stnewDate = sdf.format(date);
			
			String TEMP_DIR = System.getProperty("java.io.tmpdir");
			logger.info("new date is "+stnewDate);
			logger.info("TEMP_DIR"+TEMP_DIR);
			
			List<String> data = dao.getSettlementVoucher(beanObj);
			fileName = "SETTLEMENT_"+stnewDate+".txt";
			
			GenerateDLBVoucher vouchObj = new GenerateDLBVoucher();
			String stpath = vouchObj.checkAndMakeDirectory(beanObj.getDatepicker(), "SETTLEMENT");
			vouchObj.generateTTUMFile(stpath, fileName, data);
			
			
			logger.info("File is created");
			
			logger.info("path of zip file "+stpath +File.separator +"SETTLEMENT_"+stnewDate+".txt");
			
			File file = new File(stpath +File.separator+"SETTLEMENT_"+stnewDate+".txt");
			
			
			FileInputStream inputstream = new FileInputStream(file);
			response.setContentLength((int) file.length());
			logger.info("before downloading txt file ");
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
		catch(Exception e)
		{
			logger.info("Exception in DownloadNFSUnmatchedTTUM "+e);
			
		}
	}
	
	@RequestMapping(value = "CbsGLExcep", method = RequestMethod.GET)
	public ModelAndView CbsGLExcepGet(ModelAndView modelAndView,HttpServletRequest request
			) throws Exception {
		List<String> subcat = new ArrayList<>();

		/*modelAndView.addObject("subcategory", subcat);
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
		modelAndView.addObject("CSRFToken", csrf);*/

		//modelAndView.addObject("SettlementBean", settlementBean);
		modelAndView.setViewName("CBSGLExceptionReport");

		return modelAndView;

	}

	@RequestMapping(value = "CbsGLUploadValidation", method = RequestMethod.POST)
	@ResponseBody
	public String CbsGLUploadValidation(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		HashMap<String, Object>  output ;
		logger.info("Inside DhanalaxmiSettlementValidation Post");
		logger.info("File name is "+beanObj.getFileName());

		output = dao.checkCbsGlUpload(beanObj);


		if(output != null && (Boolean) output.get("result")) {
			return "success"; 
		}
		else { 
			return output.get("msg").toString(); 
		}


		//return "success";

	}
	
	
	@RequestMapping(value = "DownloadCBSGLExcepReport", method = RequestMethod.POST)
	public String DownloadCBSGLExcepReport(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean beanObj,
			HttpServletResponse response, HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadCBSGLExcepReport.POST Start ****");
		logger.info("type of report is "+beanObj.getFileName());
		List<Object> Excel_data = new ArrayList<Object>();
		String fileName = "";
		
				//GET DATA FOR REPORT
				Excel_data = dao.getCbsGlExceptionReport(beanObj);
			
			model.addAttribute("ReportName", "GL_CBS_Exception_"+beanObj.getDatepicker());
			model.addAttribute("data", Excel_data);
			logger.info("***** GEnerateDhanaReportController.DownloadEODReport End ****");
			return "GenerateNFSDailyReport";
		
		
	}
}
