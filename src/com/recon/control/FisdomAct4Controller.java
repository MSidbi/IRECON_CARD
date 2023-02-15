package com.recon.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.recon.model.Act4Bean;
import com.recon.model.CompareSetupBean;
import com.recon.model.FisdomACT4Detail;
import com.recon.model.FisdomFileUploadBean;
import com.recon.model.LoginBean;
import com.recon.model.VisaUploadBean;
import com.recon.service.FisdomAct4Service;
import com.recon.service.ISourceService;
import com.recon.util.CSRFToken;
import com.recon.util.FileDetailsJson;

@Controller
public class FisdomAct4Controller {
	
	private static final Logger logger = Logger.getLogger(FisdomAct4Controller.class);
	
	@Autowired
	FisdomAct4Service fisdomAct4Service;
	
	@Autowired ISourceService iSourceService;

	@RequestMapping(value = "Act4Report", method = RequestMethod.GET)
	public ModelAndView FisdomFileUploadGet(ModelAndView modelAndView,
			HttpServletRequest request) throws Exception {
         String csrf = CSRFToken.getTokenForSession(request.getSession());
         String display="";
       // List<String> glAccounts = fisdomAct4Service.getGlAccount(category);
        Act4Bean actBean = new Act4Bean();
        List<String> subcat = new ArrayList<>();
		 
		// logger.info("in GetHeaderList"+category);
		 
      //  subcat = iSourceService.getSubcategories(category);
		/*
		 * if(category.equals("ONUS") || category.equals("AMEX")
		 * ||category.equals("CARDTOCARD") ||category.equals("WCC") ) {
		 * 
		 * display="none"; }
		 */
        
 		modelAndView.addObject("CSRFToken", csrf);
 		// modelAndView.addObject("category", category);
 		modelAndView.addObject("subcategory",subcat );
      //  modelAndView.addObject("glAccount",glAccounts);
        modelAndView.addObject("display",display);
        modelAndView.addObject("actBean",actBean);
		modelAndView.setViewName("ProcessACT4");
		
		return modelAndView;
	}
	
	@RequestMapping(value = "Act4Validation", method = RequestMethod.POST)
	 @ResponseBody
	 public  String Act4Validation (@ModelAttribute("actBean")  Act4Bean actBean,ModelAndView modelAndView,
			 HttpSession httpSession)/*@RequestParam("filedate") String filedate,@RequestParam("subCat") String subCat ,
			@RequestParam("glAccount") String glAccount)*/
	 {
		logger.info("Inside Act4Validation");
		logger.info("actbean category is "+actBean.getCategory()+" date is "+actBean.getDatepicker()+" subcategory is "+actBean.getStSubCategory());
		// check whether act4 is processed or not
		HashMap<String, Object> output = fisdomAct4Service.checkAct4Process(actBean);
		if(output != null &&  (Boolean) output.get("result"))
		{
			return "success";
		}
		else
		{
			return "Act4 is not processed \n Please process Act4 first!";
		}
		
	 }
	
	@RequestMapping(value = "processAct4Report", method = RequestMethod.POST)
	 @ResponseBody
	 public  String processAct4Report (@ModelAttribute("actBean")  Act4Bean actBean,ModelAndView modelAndView,
			 HttpSession httpSession)/*@RequestParam("filedate") String filedate,@RequestParam("subCat") String subCat ,
			@RequestParam("glAccount") String glAccount)*/
	 {
		logger.info("actbean category is "+actBean.getCategory()+" date is "+actBean.getDatepicker()+" subcategory is "+actBean.getStSubCategory());
		 modelAndView.addObject("category", actBean.getCategory());
		/* modelAndView.addObject("subcategory",subCat );
		 modelAndView.addObject("glAccount",glAccount);*/
		 String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is "+Createdby);
		actBean.setCreatedby(Createdby);	
		//checck whether act4 is already processed
		HashMap<String, Object> output = fisdomAct4Service.checkAct4Process(actBean);
		
		if(output != null && !(Boolean) output.get("result"))
		{ 
			//CHECK WHETHER RECON IS PROCESSED FOR SELECTED DATE
			
			//run procedure
		 
		 boolean runFlag = fisdomAct4Service.runAct4Report(actBean);		
		 
		 if(runFlag)
			 return "Processing completed";
		 else
			 return "Processing Failed";
		}
		else
		{
			return output.get("msg").toString();
		}
	 }
	
	 @RequestMapping(value = "runAct4Report", method = RequestMethod.POST)
	 @ResponseBody
	 public  ModelAndView runAct4Report (@ModelAttribute("actBean")  Act4Bean actBean,ModelAndView modelAndView,
			 HttpSession httpSession)/*@RequestParam("filedate") String filedate,@RequestParam("subCat") String subCat ,
			@RequestParam("glAccount") String glAccount)*/
	 {
		logger.info("actbean category is "+actBean.getCategory()+" date is "+actBean.getDatepicker()+" subcategory is "+actBean.getStSubCategory());
		 modelAndView.addObject("category", actBean.getCategory());
		 modelAndView.addObject("subcategory", actBean.getStSubCategory());
		/* modelAndView.addObject("subcategory",subCat );
		 modelAndView.addObject("glAccount",glAccount);*/
		 
		 //getting credit data
		 List<FisdomACT4Detail> fisdomAct4CrLst = fisdomAct4Service.getACT4CreditData(actBean);
		 //getting debit data
		 List<FisdomACT4Detail> fisdomAct4DrLst = fisdomAct4Service.getACT4DebitData(actBean);
		 
		 String total_credit = fisdomAct4Service.getTotal_credit(actBean.getDatepicker());
		 
		 
		 modelAndView.addObject("fisdomAct4CrLst",fisdomAct4CrLst);
		 modelAndView.addObject("fisdomAct4DrLst",fisdomAct4DrLst);
		 modelAndView.addObject("total_Credit",total_credit);
		 
		 modelAndView.setViewName("DisplayACT4");

		 return modelAndView;
	 }
	 
	 @RequestMapping(value = "getGlAccount", method = RequestMethod.GET)
	 @ResponseBody
	 public   List<String> getGlAccount (@ModelAttribute("category") String category,@ModelAttribute("subcategory") String subcategory,
			 ModelAndView modelAndView,
			 HttpSession httpSession)
	 {
		logger.info("Inside Act4Validation");
		
		  List<String> glAccounts = fisdomAct4Service.getGlAccount(category,subcategory);
		  return glAccounts;
		
	 }
	 
	 @RequestMapping(value = "getDetails", method = RequestMethod.POST)
	 @ResponseBody
	 public  Map Act4Validation (@RequestParam("description") String description,@RequestParam("action") String action,
			 @RequestParam("fileDate") String fileDate,@RequestParam("category") String category,@RequestParam("subCategory") String subCategory, ModelAndView modelAndView,
			 HttpSession httpSession)/*@RequestParam("filedate") String filedate,@RequestParam("subCat") String subCat ,
			@RequestParam("glAccount") String glAccount)*/
	 {
		 Map map=new HashMap<String, Object>();
		 
		logger.info("Inside Act4Validation");
		logger.info("description is "+description+" action "+action);
		List<Object> output=null;
		if(category.equalsIgnoreCase("Fisdom")) {
		output = fisdomAct4Service.getFisdomAct4Data(description, action, fileDate);
		}else if(category.equalsIgnoreCase("Nfs")) {
		output = fisdomAct4Service.getNfsAct4Data(description, action, fileDate);	
		}
		map.put("cols", output.get(0));
		map.put("data", output.get(1));
		
		
			return map;
	 }
	 
	 @RequestMapping(value = "downloadFisdomAct4", method = RequestMethod.POST)
	 //@ResponseBody
	 public  String downloadFisdomAct4 (@ModelAttribute("actBean")  Act4Bean actBean,Model model,
			 HttpSession httpSession)/*@RequestParam("filedate") String filedate,@RequestParam("subCat") String subCat ,
			@RequestParam("glAccount") String glAccount)*/
	 {
		logger.info("actbean category is "+actBean.getCategory()+" date is "+actBean.getDatepicker()+" subcategory is "+actBean.getStSubCategory());
		logger.info("gl acc "+actBean.getGlAccount());
		/* modelAndView.addObject("subcategory",subCat );
		 modelAndView.addObject("glAccount",glAccount);*/
		 
		 List<Object> allData = fisdomAct4Service.getAct4Data(actBean.getCategory(), actBean.getDatepicker(), actBean.getStSubCategory());
		 
		 
		 model.addAttribute("ReportName", "Fisdom_act4_Report");
		 model.addAttribute("Gl_Description", " Reconciliation of FISDOM A/c No."+actBean.getGlAccount()+" as of "+actBean.getDatepicker());
			model.addAttribute("Monthly_data", allData);
			
		//	allData = fisdomAct4Service.getAct4CreditDebitData(actBean);
			
			model.addAttribute("Act4Data", allData);
			
			return "GenerateFisdomACT4Report";

	 }
	 
	 @RequestMapping(value = "extractMatchedTxnData", method = RequestMethod.POST)
		//@ResponseBody
		public String extractMatchedTxnData(HttpServletResponse response,Model model,
				HttpServletRequest request,HttpSession httpSession) throws Exception {
			
			String itemType=request.getParameter("itemType");
			String category=request.getParameter("categoryMt");
			String subCategory=request.getParameter("subCategoryMt");
			String fileDate=request.getParameter("fileDateMt");
			System.out.println("itemType    ...."+itemType);
			System.out.println("category    ...."+category);
			System.out.println("subCategory    ...."+subCategory);
			System.out.println("fileDate    ...."+fileDate);
			
			Act4Bean actBean = new Act4Bean();
			actBean.setCategory(category);
			actBean.setStSubCategory(subCategory);
			actBean.setDatepicker(fileDate);
			
			
			List<Object> allData = fisdomAct4Service.getAct4MatchedData(actBean);
			
			 model.addAttribute("ReportName", "Fisdom_act4_Report");
			 model.addAttribute("Gl_Description", " Reconciliation of FISDOM A/c No."+actBean.getGlAccount()+" as of "+actBean.getDatepicker());
				model.addAttribute("Monthly_data", allData);
				
				model.addAttribute("ReportName", "Act4_Matched_data");
				model.addAttribute("data", allData);
				logger.info("***** FisdomAct4Controller.extractMatchedTxnData Daily POST End ****");
				return "GenerateNFSDailyReport";
			
		}
}
