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
import com.recon.service.RupayUnmatchTTUMService;
import com.recon.service.VisaUnmatchTTUMService;
import com.recon.util.FileDetailsJson;
import com.recon.util.GenerateDLBVoucher;
import com.recon.util.GenerateUCOTTUM;

@Controller
public class RupayUnmatchedTTUMController {
	
	private static final Logger logger = Logger.getLogger(RupaySettlementController.class);
	private static final String ERROR_MSG = "error_msg";
	
	@Autowired ISourceService iSourceService;
	
	@Autowired RupayUnmatchTTUMService rupayTTUMService;
	
	
	@RequestMapping(value = "getRupayUnmatchedTTUM", method = RequestMethod.GET)   
	public ModelAndView getVisaUnmatchedTTUM(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** getVisaUnmatchedTTUM Start Get method  ****");
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
		modelAndView.setViewName("GenerateRupayUnmatchTTUM");
		
		logger.info("***** VisaUnmatchedTTUMController.getVisaUnmatchedTTUM GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "GenerateRupayUnmatchedTTUM", method = RequestMethod.POST)
	@ResponseBody
	public String GenerateVisaUnmatchedTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,HttpServletRequest request,HttpSession httpSession) throws Exception {
		logger.info("***** GenerateUnmatchedTTUM.GenerateVisaUnmatchedTTUM post Start ****");
		logger.info("GenerateVisaUnmatchedTTUM POST");
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby +" localDate is "+beanObj.getLocalDate());
		logger.info("filedate is "+beanObj.getFileDate()+" ttum type is "+beanObj.getTypeOfTTUM());
		beanObj.setCreatedBy(Createdby);
		boolean executed = false;
		
		//1. CHECK WHETHER TTUM IS ALREADY PROCESSED
		HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessed(beanObj);
		
		if(output != null && (Boolean)output.get("result"))
		{
			//2. CHECK WHETHER RECON IS PROCESSED OR NOT
			
			/*if(beanObj.getFileDate() != null && !beanObj.getFileDate().equalsIgnoreCase(""))
			{
				output = rupayTTUMService.checkReconProcessed(beanObj);
				
			}
			else
			{*/
				//CHECK WHETHER RECON DATE IS GREATER THAN SELECTED TRAN DATE
				output = rupayTTUMService.checkReconDateAndTTUMDataPresent(beanObj);
			
			
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
	
	// CHECK WHETHER TTUM HAS BEEN PROCESSED FOR SELECTED DATE
			@RequestMapping(value = "checkRupayTTUMProcessed", method = RequestMethod.POST)
			@ResponseBody
			public String checkTTUMProcessed(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
					FileDetailsJson dataJson, ModelAndView modelAndView,
					HttpSession httpSession, HttpServletResponse response,
					HttpServletRequest request) {
				try {
					logger.info("RupayTTUMController: checkTTUMProcessed: Entry");
					//1. VALIDATE WHETHER TTUM IS PROCESSED OR NOT
					HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessed(beanObj);
					
					if(output != null && !(Boolean) output.get("result") && output.get("msg").toString().equalsIgnoreCase("TTUM is already processed"))
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
			
			
		@RequestMapping(value = "DownloadRupayUnmatchedTTUM", method = RequestMethod.POST)
		@ResponseBody
		public void DownloadUnmatchedTTUM(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,
				HttpServletResponse response, HttpServletRequest request,HttpSession httpSession) throws Exception {
			logger.info("***** VisaUnmatchedTTUMController.DownloadVisaUnmatchedTTUM post Start ****");
			logger.info("DownloadVisaUnmatchedTTUM POST");
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is "+Createdby);
			List<Object> TTUMData = new ArrayList<Object>();
			beanObj.setCreatedBy(Createdby);
			String fileName = "";
			List<Object> Excel_data = new ArrayList<Object>();
			beanObj.setCategory("RUPAY");
			try
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yy/mm/dd");
				java.util.Date date = sdf.parse(beanObj.getLocalDate());
				sdf = new SimpleDateFormat("dd-MM-yyyy");

				String stnewDate = sdf.format(date);
				
				String TEMP_DIR = System.getProperty("java.io.tmpdir");
				logger.info("new date is "+stnewDate);
				logger.info("TEMP_DIR"+TEMP_DIR);
				
				String stpath = TEMP_DIR;//+beanObj.getCategory()+File.separator+stnewDate;
				beanObj.setStPath(stpath);
				
				logger.info("Path is "+stpath);
				
				
				boolean directoryCreated = rupayTTUMService.checkAndMakeDirectory(beanObj);
				
				stpath = stpath+"/RUPAY/"+stnewDate;
				
				List<String> data = rupayTTUMService.getRupayTTUMDataForTXT(beanObj);
				fileName = "RUPAY_"+beanObj.getTypeOfTTUM()+".txt";
				
				GenerateDLBVoucher vouchObj = new GenerateDLBVoucher();
				//String stpath = vouchObj.checkAndMakeDirectory(beanObj.getFileDate(), "NFS");
				vouchObj.generateTTUMFile(stpath, fileName, data);
				
				if(directoryCreated)
				{

					TTUMData = rupayTTUMService.getRupayTTUMData(beanObj);

					/*** CREATE EXCEL FILE****/
					List<String> Column_list = new ArrayList<String>();
					Column_list.add("account_number");
					Column_list.add("part_tran_type");	
					Column_list.add("transaction_amount");	
					Column_list.add("transaction_particular");	

					Excel_data.add(Column_list);
					Excel_data.add(TTUMData.get(0));

					fileName = beanObj.getCategory()+"_"+beanObj.getTypeOfTTUM()+"_TTUM_1.xls";
					rupayTTUMService.generateExcelTTUM(stpath, fileName, Excel_data,"RUPAY-"+beanObj.getTypeOfTTUM()+"-"+stnewDate,response,false);
				}
				logger.info("File is created");
				
				logger.info("path of zip file "+stpath +File.separator +"RUPAY-"+beanObj.getTypeOfTTUM()+"-"+stnewDate+".zip");
				
				File file = new File(stpath +File.separator+"RUPAY-"+beanObj.getTypeOfTTUM()+"-"+stnewDate+".zip");
				
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
			catch(Exception e)
			{
				logger.info("Exception in DownloadVisaUnmatchedTTUM "+e);
				
			}
		}

/*************** ttum Rollback ****************/
@RequestMapping(value = "RupayTTUMRollback", method = RequestMethod.POST)
@ResponseBody
public String RupayTTUMRollback(@ModelAttribute("unmatchedTTUMBean") UnMatchedTTUMBean beanObj,HttpServletRequest request,
		HttpSession httpSession) throws Exception {
	logger.info("***** GenerateUnmatchedTTUM.RupayTTUMRollback post Start ****");
	logger.info("RupayTTUMRollback POST");
	String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
	logger.info("Created by is "+Createdby +" localDate is "+beanObj.getLocalDate());
	logger.info("filedate is "+beanObj.getFileDate()+" ttum type is "+beanObj.getTypeOfTTUM());
	beanObj.setCreatedBy(Createdby);
	boolean executed = false;


	//1. CHECK WHETHER TTUM IS ALREADY PROCESSED
	HashMap<String, Object> output = rupayTTUMService.checkTTUMProcessed(beanObj);

	if(output != null && !(Boolean)output.get("result") )
	{
		if(output.get("msg").equals("TTUM is already processed"))
		{
			//rollback of ttum
			if(rupayTTUMService.RupayTtumRollback(beanObj))
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
	else
	{
		return "TTUM is not processed";
	}
}
		
	}
