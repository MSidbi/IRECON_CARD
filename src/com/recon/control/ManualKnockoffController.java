package com.recon.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import com.recon.dao.ManualKnockoffDao;
import com.recon.model.CompareSetupBean;
import com.recon.model.FileSourceBean;
import com.recon.model.ManualKnockoffBean;
import com.recon.service.ISourceService;
import com.recon.util.CSRFToken;
import com.recon.util.FileDetailsJson;

@Controller
public class ManualKnockoffController {
	
	private static final Logger logger = Logger.getLogger(ManualKnockoffController.class);
	
	@Autowired ISourceService iSourceService;
	
	@Autowired ManualKnockoffDao manualKnockoffDao;
	
	@RequestMapping(value = "manualKnockoff", method = RequestMethod.GET)
	public ModelAndView ReconProcess1(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** ManualKnockoff.Get Start ****");
		ManualKnockoffBean manualKnockoffBean = new ManualKnockoffBean();
		String display="";
		logger.info("RECON PROCESS GET");
		 
		 logger.info("in GetHeaderList"+category);
		
        List<String> subcat = iSourceService.getSubcategories(category);
         if(category.equals("ONUS") || category.equals("AMEX") ||category.equals("CARDTOCARD") ||category.equals("WCC") ) {
        	 
        	 display="none";
         }
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
         modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory",subcat );
       // modelAndView.addObject("fileNames",fileNames );
		//modelAndView.addObject("display",display);
		modelAndView.addObject("manualKnockoffBean",manualKnockoffBean);
		modelAndView.setViewName("ManualKnockoff");
		
		logger.info("***** ReconProcess.manualKnockoff GET End ****");
		return modelAndView;

	}
	
	@RequestMapping(value = "manualKnockoff", method = RequestMethod.POST)
	@ResponseBody
	public String manualKnockoff(@ModelAttribute("manualKnockoffBean")  ManualKnockoffBean manualKnockoffBean,HttpServletRequest request,
			@RequestParam("file") MultipartFile file, String filename,
			String fileType,String category,String stSubCategory,String fileDate ,HttpSession httpSession,
			Model model,ModelAndView modelAndView,RedirectAttributes redirectAttributes) throws Exception {


		logger.info("***** ManualKnockoff.POST Execution Start ****");
		logger.info("bean Data "+manualKnockoffBean.getCategory()+" "+manualKnockoffBean.getStSubCategory()+" "+manualKnockoffBean.getDatepicker());
		//		logger.info("Data is "+category +" "+ fileDate +" "+stSubCategory+" "+selectedFile);
		//1. VALIDATE WHETHER RECON HAS BEEN PROCESSED FOR SELECTED DATE
		boolean reconStatus = manualKnockoffDao.checkreconStatus(manualKnockoffBean.getCategory(),manualKnockoffBean.getStSubCategory(),manualKnockoffBean.getDatepicker());
		logger.info("recon is processed ? "+reconStatus);


		//2. READ UPLOADED DAT FILE AND UPDATE RECORDS
		if(reconStatus)
		{
			logger.info("Performing knockoff");
			//1. KNOCKOFF OF SWITCH FILE
			manualKnockoffDao.knockoffAllData(manualKnockoffBean,file);
		}
		else
		{
			return "Recon is not processed for selected date. ";

		}

		int updatedCount = manualKnockoffDao.getUpdatedRecordCount(manualKnockoffBean);
		logger.info("count is "+updatedCount);

		return updatedCount+" Records Updated";
	}
	
	
	@RequestMapping(value = "manulRollBack", method = RequestMethod.GET)
	public ModelAndView manulRollBackView(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** manulRollBackView.Get Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>(); 
		String display="";
		logger.info("manulRollBackView GET");
		
		 
         List<String> subcat = iSourceService.getSubcategories(category);
         if(category.equals("ONUS") || category.equals("AMEX") ||category.equals("WCC") ) {
        	 
        	 display="none";
         }
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
         modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display",display);
		modelAndView.setViewName("ManualRollBack");
		logger.info("***** manulRollBackView.Get End ****");
		return modelAndView;

	}
	
	@RequestMapping(value = "manulRollBack", method = RequestMethod.POST)
	@ResponseBody
	public String manulRollBack(@RequestParam("category")String category,FileDetailsJson dataJson, ModelAndView modelAndView,CompareSetupBean setupBean,HttpSession httpSession,
			@RequestParam("filedate") String filedate,@RequestParam("subCat") String subCat) throws Exception {

		logger.info("***** manulRollBack.POST Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		boolean rollbackPossible = false;
		Map<String, Object> output = new HashMap<String, Object>();
		String display="";
		logger.info("manulRollBackView GET");
		logger.info("selected category is "+category);
		logger.info("selected sub category is "+subCat);
		logger.info("selected filedate is "+filedate);
		//CHECK WHETHER SELECTED DATE IS THE LAST RECON DATE
		if(category.equalsIgnoreCase("MASTERCARD") && subCat.equalsIgnoreCase("ACQUIRER"))
		{
			logger.info("ITS MASTERCARD ACQUIRER");
			output = manualKnockoffDao.validateMastercardAcqRollBack(category, subCat, filedate);
		}
		else
		{
			output = manualKnockoffDao.validateRollBackDate(category, subCat, filedate);
			
		}
		logger.info("output is "+output);
		if(output != null && output.get("result") != null)
		{
			logger.info("getting data from output Map");
			rollbackPossible = (boolean) output.get("result");
		}
		System.out.println("Rollbak is possible ? "+rollbackPossible);
		
		if(rollbackPossible)
		{
			//ROLLBACK CODE STARTS HERE
			String message = manualKnockoffDao.ManualRollBack(category, subCat, filedate);
			logger.info("rollback process message "+message);
			logger.info("***** manulRollBack.manulRollBack POST End ****");
			return message;
		}
		else
		{
			if(output != null && output.get("msg") != null)
			{
				logger.info("***** manulRollBack.manulRollBack POST End ****");
				return output.get("msg").toString();
			}
			else {
				logger.info("***** manulRollBack.manulRollBack POST End ****");
				return "RollBack cannot be processed";
			}
		}

		

	
	}

}
