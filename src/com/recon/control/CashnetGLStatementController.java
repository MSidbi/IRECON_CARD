package com.recon.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.recon.dao.SettlementDao;
import com.recon.model.CashnetSettlementFile;
import com.recon.model.CompareSetupBean;
import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.RupayUploadBean;
import com.recon.model.SettlementBean;
import com.recon.model.UnMatchedTTUMBean;
import com.recon.service.CashnetGLStatementService;
import com.recon.service.CashnetUnmatchTTUMService;
import com.recon.service.ISourceService;
import com.recon.service.NFSUnmatchTTUMService;
import com.recon.util.CSRFToken;
import com.recon.util.FileDetailsJson;
import com.recon.util.GenerateDLBVoucher;
import com.recon.util.GenerateUCOTTUM;

@Controller
public class CashnetGLStatementController {

	private static final Logger logger = Logger.getLogger(RupaySettlementController.class);
	private static final String ERROR_MSG = "error_msg";

	@Autowired ISourceService iSourceService;

	@Autowired CashnetGLStatementService cashnetGLStatementService;

	@Autowired
	SettlementDao dao;

	@RequestMapping(value = "getCashnetGLStatement", method = RequestMethod.GET)   
	public ModelAndView getCashnetGLStatement(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** getCashnetGLStatement Start Get method  ****");
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
		modelAndView.setViewName("CashnetGLStatement");

		logger.info("***** NFSUnmatchedTTUMController.getNFSUnmatchedTTUM GET End ****");
		return modelAndView;
	}

	@RequestMapping(value = "getCashnetGLStatement", method = RequestMethod.POST) 
	@ResponseBody
	public String processCashnetGLStatement(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			HttpServletRequest request) throws Exception {

		logger.info("processCashnetGLStatement: Entry");
		
		//logger.info("closing balance "+beanObj.getClosingBal());

		//1. check whether gl statetment is already processed
		HashMap<String, Object> output = cashnetGLStatementService.checkGLStatementProcess(beanObj);
		
//		HashMap<String, Object> output = null;
//		output.put("result", true);
//		output.put("msg", "Gl Statement is not processed");

		if(output != null && (Boolean) output.get("result"))
		{

			//2. check eod, recon, settlement processing
			output = cashnetGLStatementService.checkCashnetProcessing(beanObj);

			if(output != null && (Boolean) output.get("result"))
			{

				//3. run procedure
				Boolean processFlag = cashnetGLStatementService.runCashnetStatement(beanObj);

				if(processFlag)
				{
					return "Processing Completed";
				}
				else
				{
					return "Issue while processing";
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

	// CHECK WHETHER TTUM HAS BEEN PROCESSED FOR SELECTED DATE
	@RequestMapping(value = "checkCashnetStatementProcessed", method = RequestMethod.POST)
	@ResponseBody
	public String checkCashnetStatementProcessed(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
			FileDetailsJson dataJson, ModelAndView modelAndView,
			HttpSession httpSession, HttpServletResponse response,
			HttpServletRequest request) {
		try {
			logger.info("checkCashnetStatementProcessed: Entry");
			//1. VALIDATE WHETHER TTUM IS PROCESSED OR NOT
			HashMap<String, Object> output = cashnetGLStatementService.checkGLStatementProcess(beanObj);

			if(output != null && !(Boolean) output.get("result") 
					&& output.get("msg").toString().equalsIgnoreCase("GL Statement is already processed"))
			{
				return "success";
			}
			else
			{
				return "Gl Statement is not processed for selected date";
			}



		} catch (Exception e) {
			logger.info("Exception is "+e);
			return "Exception";

		}
	}

	@RequestMapping(value = "DownloadCashnetGLStatement", method = RequestMethod.POST)
	public String DownloadCashnetGLStatement(@ModelAttribute("unmatchedTTUMBean")  UnMatchedTTUMBean beanObj,HttpServletRequest request,
			HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
		logger.info("***** DownloadCashnetGLStatement.POST Start ****");
		logger.info("DownloadCashnetGLStatement POST");
		
		List<Object> Excel_data = new ArrayList<Object>();
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);


		//GET DATA FOR REPORT
		Excel_data = cashnetGLStatementService.getCashnetGLStatement(beanObj);

//		model.addAttribute("ReportName", "CASHNET_"+beanObj.getStSubCategory()+"_GL_Statement");
		model.addAttribute("ReportName", beanObj.getCategory().toUpperCase()+"_"+beanObj.getStSubCategory()+"_GL_Statement");
		model.addAttribute("data", Excel_data);
		logger.info("***** DownloadCashnetGLStatement Daily POST End ****");
		return "GenerateGLStatements";

	}		

	/****** Statement Rollback **************/
	@RequestMapping(value = "CashnetStatementRollback", method = RequestMethod.POST)
	@ResponseBody
	public String CashnetStatementRollback(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** CashnetGLStatementController.CashnetStatementRollback post Start ****");
		logger.info("CashnetStatementRollback POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby +" localDate is "+beanObj.getLocalDate());
		logger.info("filedate is "+beanObj.getFileDate()+" ttum type is "+beanObj.getTypeOfTTUM());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;


		//1. CHECK WHETHER TTUM IS ALREADY PROCESSED
		HashMap<String, Object> output =  cashnetGLStatementService.checkGLStatementProcess(beanObj);

		if(output != null && !(Boolean)output.get("result") &&
				output.get("msg").toString().equalsIgnoreCase("GL Statement is already processed"))
		{
			//rollback of ttum
			if(cashnetGLStatementService.CashnetStatementRollback(beanObj))
			{
				return "TTUM Rolledback Successfully";
			}
			else
			{
				return "Issue while rolling back";
			}
		}
		else
		{
			return output.get("msg").toString();
		}

	}
	
	
	@RequestMapping(value = "CashnetGLFileUpload", method = RequestMethod.GET)   
	public ModelAndView CashnetGLFileUpload(ModelAndView modelAndView,CompareSetupBean compareSetupBean,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** getCashnetGLStatement Start Get method  ****");
		compareSetupBean.setCreatedBy(((LoginBean) httpSession.getAttribute("loginBean")).getUser_id());
		ArrayList<CompareSetupBean> setupBeanslist = null ; 
		
		
		String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
		//redirectAttributes.addFlashAttribute("CSRFToken", csrf);
		modelAndView.addObject("CSRFToken", csrf);
		
		modelAndView.setViewName("CashnetGLFileUpload1");
		modelAndView.addObject("CompareSetupBean",compareSetupBean);
		

		logger.info("***** NFSUnmatchedTTUMController.CashnetGLFileUpload GET End ****");
		return modelAndView;
	}
	
	
	@RequestMapping(value = "CashnetGLFileUpload1", method = RequestMethod.POST)
	@ResponseBody
	public String CashnetGLFileUpload(@ModelAttribute("CompareSetupBean")  CompareSetupBean setupBean,HttpServletRequest request,HttpSession httpSession,@RequestParam("file") MultipartFile file,
			 String fileDate) throws Exception {
		logger.info("***** CashnetGLStatementController.CashnetGLFileUpload post Start ****");
		logger.info("CashnetGLFileUpload POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		SimpleDateFormat date1=new SimpleDateFormat("dd/MM/yyyy"); 
//		logger.info("Created by is "+Createdby +" localDate is "+beanObj.getLocalDate());
//		logger.info("filedate is "+beanObj.getFileDate()+" ttum type is "+beanObj.getTypeOfTTUM());
//		beanObj.setCreatedBy(Createdby);
		
//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
//		LocalDateTime date = LocalDateTime.now(); 
//		String createdDate = System.getenv()
		boolean executed = false;
		//String fileName = setupBean.getFile;
		
		//String cycle = setupBean.getCategory();


		//1. CHECK WHETHER TTUM IS ALREADY PROCESSED
//		HashMap<String, Object> output =  cashnetGLStatementService.checkGLStatementProcess(beanObj);
//
//		if(output != null && !(Boolean)output.get("result") &&
//				output.get("msg").toString().equalsIgnoreCase("GL Statement is already processed"))
//		{
			//rollback of ttum
//			if(cashnetGLStatementService.CashnetStatementRollback(beanObj))
//			{
//				return "TTUM Rolledback Successfully";
//			}
//			else
//			{
//				return "Issue while rolling back";
//			}
//		}
//		else
//		{
//			return output.get("msg").toString();
//		}
		
		
		String stLine = null;
		CashnetSettlementFile settlementFile = new CashnetSettlementFile();
		
		HashMap<String, Object> output =  dao.checkFileProcessedForCashnetSettlement(setupBean);

		if(output != null && (Boolean)output.get("result")){
		try{
		//File file = new File("E:\\CARDS\\Cashnet Debit Credit\\NEWAQDLB101122_1C (1).txt");
		//File file = new File(filePath);
		BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));			
		//conn.setAutoCommit(false);
		
		System.out.println("File Reading Starts");
		
		int count = 0;
		
		while((stLine = br.readLine()) != null)
		{
			
			 
			if(!stLine.contains("DLB The Dhanalakshmi Bank") && !stLine.contains("356 Indian Rupee (India)") && !stLine.contains("CARD NUMBER") && !stLine.contains("------") && !stLine.contains("DATE") && !stLine.contains("Run Time")){
				count++;
				//System.out.println(stLine);
				
				if(stLine.contains("SUMMARY TOTALS")){
					break;
				}
				
				if(count == 1){
					settlementFile.setCardNumber(stLine.substring(0,20).trim());
					settlementFile.setTranDesc(stLine.substring(22,34).trim());
					settlementFile.setTranAmount1(stLine.substring(40,48).trim());
					settlementFile.setTranAmount2(stLine.substring(71,79).trim());
				}else if(count == 2){
					
					String trimedDate = stLine.substring(2,10).trim();
					String formatedDate = trimedDate.substring(6,8)+"/"+trimedDate.substring(3,5)+"/"+trimedDate.substring(0,2);
					settlementFile.setDate(formatedDate);
					//settlementFile.setDate(stLine.substring(2,10).trim());
					settlementFile.setTime(stLine.substring(12,18).trim());
					settlementFile.setSer(stLine.substring(22,34).trim());
					settlementFile.setTrace(stLine.substring(35,47).trim());
					settlementFile.setRRN(stLine.substring(48,56).trim());
					settlementFile.setAddress(stLine.substring(57,80).trim());
					settlementFile.setCity(stLine.substring(80,93).trim());
					settlementFile.setVnd(stLine.substring(93,97).trim());
					settlementFile.setCycle(setupBean.getCategory());
					
					int a = dao.saveDataToCashnetSettlement(settlementFile, Createdby, fileDate);
					count = 0;
					settlementFile = new CashnetSettlementFile();
				}
			}
		}
		
		}catch(Exception e){
			System.out.println("Exception in CashnetGLFileUpload: "+e);
		}
	}else
	{
		return output.get("msg").toString();
	}
		
 return "File Uploaded Successfully";
	}
	
	
	
	@RequestMapping(value = "checkGLFileUpload", method = RequestMethod.POST)
	@ResponseBody
	public String checkGLFileUpload(@ModelAttribute("CompareSetupBean")  CompareSetupBean setupBean,HttpServletRequest request,HttpSession httpSession,@RequestParam("file") MultipartFile file,
			 String fileDate) throws Exception {
		
		String cycle = "";

		try {
			logger.info("checkCashnetStatementProcessed: Entry");
			//1. VALIDATE WHETHER TTUM IS PROCESSED OR NOT
			HashMap<String, Object> output = dao.checkFileProcessedForCashnetSettlement(setupBean);

			if(output != null && (Boolean) output.get("result") 
					&& output.get("msg").toString().equalsIgnoreCase("GL File is not processed"))
			{
				return "success";
			}
			else
			{
				return "GL File is processed for selected date";
			}



		} catch (Exception e) {
			logger.info("Exception is "+e);
			return "Exception";

		}
	
	}

}
