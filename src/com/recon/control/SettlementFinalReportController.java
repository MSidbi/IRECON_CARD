package com.recon.control;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.recon.model.CompareSetupBean;
import com.recon.model.GenerateTTUMBean;
import com.recon.model.SettlementBean;
import com.recon.model.Settlement_FinalBean;
import com.recon.service.ISourceService;
import com.recon.util.GenerateSettleTTUMBean;





@Controller
public class SettlementFinalReportController {
	
	private static final Logger logger = Logger.getLogger(SettlementFinalReportController.class);
	@Autowired ISourceService iSourceService;
	
	//@Autowired ISettlement_FinalService  isettlementFinalService;
	
	
	@RequestMapping(value = "settlementFinalReport", method = RequestMethod.GET)
	public ModelAndView seeRule(ModelAndView modelAndView,@RequestParam("category")String category,SettlementBean settlementBean) {
		logger.info("***** ReconProcess.ReconProcess1 Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>(); 
		String display="";
		logger.info("RECON PROCESS GET");
		
		
		 List<String> subcat = new ArrayList<>();
		 
		 logger.info("in GetHeaderList"+category);
		 
         try {
			subcat = iSourceService.getSubcategories(category);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         if(category.equals("ONUS") || category.equals("AMEX") ||category.equals("CARDTOCARD") ||category.equals("WCC") ) {
        	 
        	 display="none";
         }
         
		
         modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display",display);
		modelAndView.addObject("SettlementBean", settlementBean);
		modelAndView.setViewName("settlementFinalReport");
		logger.info("***** ReconProcess.ReconProcess1 End ****");
		return modelAndView;

	}
	
	@RequestMapping(value = "settlementFinalReport", method = RequestMethod.POST)
	public String downloadSettlement(ModelAndView modelAndView,Model model,SettlementBean settlementBean) {
		logger.info("***** ReconProcess.ReconProcess1 Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>(); 
		String display="";
		logger.info("RECON PROCESS GET");
		
		Settlement_FinalBean ReportBean = new Settlement_FinalBean(); 
		 List<String> subcat = new ArrayList<>();
		 String filedate =settlementBean.getDatepicker();
		 System.out.println(settlementBean.getNet_settl_amt());
		 String  net_settl_amt =settlementBean.getNet_settl_amt();
		 System.out.println(net_settl_amt);
		 
		 ReportBean.setFileDate(filedate);
		
		 
		 System.out.println(ReportBean.getFileDate());
		 
		 
		 
		 //logger.info("in GetHeaderList"+category);
		 
      /*   try {
			subcat = iSourceService.getSubcategories(category);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}isettlementFinalService
         if(category.equals("ONUS") || category.equals("AMEX") ||category.equals("CARDTOCARD") ||category.equals("WCC") ) {
        	 
        	 display="none";
         }
         */
		 ReportBean = iSourceService.getReportDetails( settlementBean);
		 ReportBean.setNet_sett_AMOUNT_Amt(net_settl_amt );
		 
		 model.addAttribute("ReportBean",ReportBean);
		 model.addAttribute("filedate",filedate);
       //  modelAndView.addObject("category", category);
		//modelAndView.addObject("subcategory",subcat );
		//modelAndView.addObject("display",display);
		//modelAndView.setViewName("settlementFinalReport");
		logger.info("***** ReconProcess.ReconProcess1 End ****");
		return "SettlementFinalReport";

	}
	
	
	@RequestMapping(value="generateSettlTTUM", method=RequestMethod.GET)
	public ModelAndView getComparePage(ModelAndView modelAndView, GenerateTTUMBean generateTTUMBean,@RequestParam("category")String category,Model model,SettlementBean settlementBean) throws Exception
	{
		logger.info("***** ReconProcess.ReconProcess1 Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>(); 
		String display="";
		logger.info("RECON PROCESS GET");
		
		
		 List<String> subcat = new ArrayList<>();
		 
		 logger.info("in GetHeaderList"+category);
		 
         try {
			subcat = iSourceService.getSubcategories(category);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         if(category.equals("ONUS") || category.equals("AMEX") ||category.equals("CARDTOCARD") ||category.equals("WCC") ) {
        	 
        	 display="none";
         }
         
		
         modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display",display);
		modelAndView.addObject("SettlementBean", settlementBean);
		modelAndView.setViewName("SettlementTTUM");
		logger.info("***** ReconProcess.ReconProcess1 End ****");
		return modelAndView;

	

	}
	
	
	@RequestMapping(value="generateSettlTTUM", method=RequestMethod.POST)
	public String generateTTUM(ModelAndView modelAndView, GenerateTTUMBean generateTTUMBean,@RequestParam("category")String category,Model model,SettlementBean settlementBean) throws Exception
	{
		logger.info("***** ReconProcess.ReconProcess1 Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>(); 
		
		String display="";
		logger.info("RECON PROCESS GET");
		
		Settlement_FinalBean ReportBean = new Settlement_FinalBean(); 
		 List<String> subcat = new ArrayList<>();
		 String filedate =settlementBean.getDatepicker();
		 System.out.println(settlementBean.getNet_settl_amt());
		 String  net_settl_amt =settlementBean.getNet_settl_amt();
		 System.out.println(net_settl_amt);
		 
		 ReportBean.setFileDate(filedate);
		
		 
		 System.out.println(ReportBean.getFileDate());
		 
		 
		 
		
		 List<GenerateSettleTTUMBean> beanlist  = iSourceService.generateSettlTTUM(settlementBean);
		// ReportBean.setNet_sett_AMOUNT_Amt(net_settl_amt );
		 
		 model.addAttribute("generate_ttum",beanlist);
       //  modelAndView.addObject("category", category);
		//modelAndView.addObject("subcategory",subcat );
		//modelAndView.addObject("display",display);
		//modelAndView.setViewName("settlementFinalReport");
		logger.info("***** ReconProcess.ReconProcess1 End ****");
		return "SettlementTTUMREPORT";

	}
	
	
/*	@RequestMapping(value = "generateSettlTTUM", method = RequestMethod.GET)
	public ModelAndView generateTTUM(ModelAndView modelAndView,@RequestParam("category")String category,SettlementBean settlementBean) {
		logger.info("***** ReconProcess.ReconProcess1 Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>(); 
		String display="";
		logger.info("RECON PROCESS GET");
		
		
		 List<String> subcat = new ArrayList<>();
		 
		 logger.info("in GetHeaderList"+category);
		 
         try {
			subcat = iSourceService.getSubcategories(category);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         if(category.equals("ONUS") || category.equals("AMEX") ||category.equals("CARDTOCARD") ||category.equals("WCC") ) {
        	 
        	 display="none";
         }
         
		
         modelAndView.addObject("category", category);
		modelAndView.addObject("subcategory",subcat );
		modelAndView.addObject("display",display);
		modelAndView.addObject("SettlementBean", settlementBean);
		modelAndView.setViewName("settlementFinalReport");
		logger.info("***** ReconProcess.ReconProcess1 End ****");
		return modelAndView;

	}*/

}
