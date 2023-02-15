package com.recon.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import org.apache.log4j.Logger;
import org.apache.log4j.pattern.FileDatePatternConverter;

import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.RupayAdjustmentBean;
import com.recon.service.RupayAdjustntFileUpService;
import com.recon.util.GenerateUCOTTUM;

@Controller
public class RupayNetwrkAdjustController {

@Autowired
RupayAdjustntFileUpService 	rupayAdjustntFileUpService;

private static final Logger logger = Logger.getLogger(RupayNetwrkAdjustController.class);

@RequestMapping(value = "rupayNetworkAdjustment",method = RequestMethod.GET)	
public String rupayNetwrkAdjust() {
		return "rupayNetworkAdjust";
	}

@RequestMapping(value="rupayAdjustmentFileUpload",method=RequestMethod.POST)
@ResponseBody
public String rupayAdjustmentFileUpload(@RequestParam("file") MultipartFile file,String fileDate, String cycle, String network,
		String subcate,
		HttpServletRequest request,
		HttpSession httpSession) throws IOException {
	HashMap<String, Object> output = new HashMap<String, Object>();
	System.out.println("fILE DATE IS "+fileDate);
	String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
	System.out.println("Created by is "+Createdby);
	boolean presentmentFile = false;
	System.out.println("subcategory/................. "+subcate);
	
	//FOR PRESENTMENT FILE
	if(file.getOriginalFilename().contains("IRGCS_Presentment"))
	{
		presentmentFile = true;
	}
	
	
	//1. validate whether file is already uploaded
	
	output = rupayAdjustntFileUpService.validateAdjustmentUpload(fileDate, cycle, network, subcate, presentmentFile);
	
	if(output != null && (Boolean) output.get("result"))
	{
		//2. upload file
		int extn = file.getOriginalFilename().indexOf(".");
		System.out.println("file.getOriginalFilename().substring(extn)    "+file.getOriginalFilename().substring(extn));
		
		
		if(!presentmentFile)
				output = rupayAdjustntFileUpService.rupayAdjustmentFileUpload(fileDate, Createdby, cycle, network, file, subcate);
		else
			output = rupayAdjustntFileUpService.rupayIntPresentFileUpload(fileDate, Createdby, cycle, network, file, subcate);
		
		
		if((Boolean) output.get("result"))
			return "File Uploaded Successfuly \n Count is "+output.get("count");
		else
			return "File Uploading Failed";
	}
	else
	{
		return output.get("msg").toString();
	}
		 
}

/*************** RUpay Adjustment ttum **********************/
@RequestMapping(value = "AdjustmentTTUM",method = RequestMethod.GET)	
public String AdjustmentTTUM(ModelAndView modelAndView) {
		return "GenerateRupayAdjustmentTTUM";
	}

@RequestMapping(value = "RupayAdjustmentProcess", method = RequestMethod.POST)
@ResponseBody
public String NFSAdjTTUMValidation(String fileDate, String adjType,HttpServletRequest request,
		HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
	logger.info("***** RupayAdjustmentProcess.Post Start ****");
	logger.info("ADjtype is "+adjType +" filedate "+fileDate);
	
	String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
	System.out.println("Created by is "+Createdby);
	
	HashMap<String, Object> output = rupayAdjustntFileUpService.validateAdjustmentTTUM(fileDate, adjType);
	
	if(output != null && (Boolean) output.get("result"))
	{
		// run ttum proc
		boolean executed = rupayAdjustntFileUpService.runAdjTTUM(fileDate, adjType, Createdby);
		
		if(executed)
		{
			return "Processing Completed \nPlease download TTUM";
		}
		else
		{
			return "Issue while Processing";
		}
	}
	else
	{
		return output.get("msg").toString();
	}
	
}

@RequestMapping(value = "ValidateDownloadRupayAdjTTUM", method = RequestMethod.POST)
@ResponseBody
public String ValidateDownloadRupayAdjTTUM(String fileDate, String adjType,HttpServletRequest request,
		HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
	logger.info("***** DownloadSettlementreport.POST Start ****");
	logger.info("DownloadAdjTTUM POST");

	List<Object> Excel_data = new ArrayList<Object>();
	String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
	logger.info("Created by is "+Createdby);

	//GETTING , IN DATE FIELD

	HashMap<String, Object> output = rupayAdjustntFileUpService.validateAdjustmentTTUMProcess(fileDate, adjType);

	if(output != null && (Boolean) output.get("result"))
	{
		return "success";
	}
	else
	{
		return "Adjustment TTUM is not processed.\n Please process TTUM";
	}

}	

@RequestMapping(value = "DownloadRupayAdjTTUM", method = RequestMethod.POST)
@ResponseBody
public void DownloadAdjTTUM(String fileDate, String adjType,HttpServletRequest request,
		HttpServletResponse response,HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
	logger.info("***** DownloadSettlementreport.POST Start ****");
	logger.info("DownloadAdjTTUM POST");

	List<Object> Excel_data = new ArrayList<Object>();
	List<Object> TTUMData = new ArrayList<Object>();
	String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
	logger.info("Created by is "+Createdby);
	
	String dateis = request.getParameter("fileDate");
	
	System.out.println(dateis);

	//GETTING , IN DATE FIELD
		//GET DATA FOR REPORT
	TTUMData = rupayAdjustntFileUpService.getAdjTTUM(fileDate, adjType);
		
		String fileName = "RUPAY_DOM_ADJUSTMENT_"+adjType+"_TTUM.txt";
		
		String stPath = System.getProperty("java.io.tmpdir");
		logger.info("TEMP_DIR"+fileName);
		
		GenerateUCOTTUM obj = new GenerateUCOTTUM();
		stPath = obj.checkAndMakeDirectory(fileDate, "RUPAY");
		
		if(!adjType.equalsIgnoreCase("PENALTY") && !adjType.equalsIgnoreCase("FEE"))
			obj.generateMultipleDRMTTUMFiles(stPath, fileName, 1, TTUMData,"RUPAY");  // for generating customer ttum
		else
		{
			fileName = "RUPAY_DOM_ADJ_"+adjType+"_TTUM.txt";
			obj.generateMultipleTTUMFiles(stPath, fileName, 1, TTUMData);
		}
		//}
		
		
		logger.info("File is created");
		
		List<String> Column_list = new ArrayList<String>();
		Column_list.add("ACCOUNT_NUMBER");
		Column_list.add("PART_TRAN_TYPE");	
		Column_list.add("TRANSACTION_AMOUNT");	
		Column_list.add("TRANSACTION_PARTICULAR");	
		Column_list.add("ADJTYPE");	
		
		Excel_data.add(Column_list);
		Excel_data.add(TTUMData);
		
		fileName = "RUPAY_DOM_ADJUSTMENT_"+adjType+"_TTUMS.xls";
		String zipName = "RUPAY_DOM_ADJ_"+
				adjType+"_TTUM.zip";
		obj.generateExcelTTUM(stPath, fileName, Excel_data,"REFUND",zipName);
		logger.info("File is created");
		
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
}	
}
