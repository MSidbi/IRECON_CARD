package com.recon.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.model.CompareSetupBean;
import com.recon.model.SettlementBean;
import com.recon.service.IglBalanceService;
import com.recon.util.GLBalanceBean;

@Controller
public class GlBalanceController {
	
	private static final String ERROR_MSG = "error_msg";
	private static final String SUCCESS_MSG = "success_msg";
	private static final Logger logger = Logger.getLogger(GlBalanceController.class);
	
	@Autowired IglBalanceService glbalanceservice;

	@RequestMapping(value = "GlBalancing", method = RequestMethod.GET)
	public ModelAndView GLbalance(ModelAndView modelAndView,@RequestParam("category")String category) throws Exception {
		logger.info("***** GlbalanceProcess1 Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>(); 
		String display="";
		logger.info("RECON PROCESS GET");
		
		
		 List<String> subcat = new ArrayList<>();
		 
		 logger.info("in GetHeaderList"+category);
		 
		 modelAndView.addObject("category", category);
		modelAndView.setViewName("GlBalance");
		logger.info("***** ReconProcess.ReconProcess1 End ****");
		return modelAndView;

	}
	
	@RequestMapping(value = "GlBalancing", method = RequestMethod.POST)
	@ResponseBody
	public String getGLbalance(ModelAndView modelAndView,RedirectAttributes redirectAttributes,@RequestParam("category")String category
,String closing_balance,String cash_dispense,String filedate,String subCat,String difference,String [] item_setdt,String [] item_settlamnt,HttpServletRequest request,HttpServletResponse response) throws Exception {
		logger.info("***** GlbalanceProcess1 Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>(); 
		String display="";
		logger.info("GL BALANCE posT");
		SettlementBean settlementBean = new SettlementBean();
		String msg ="";
		
		String [] arrsetdt = request.getParameterValues("item_setdt[]");
		String [] arritem_settlamnt  = request.getParameterValues("item_settlamnt[]");
		
		System.out.println( "NEW"+item_setdt +  item_settlamnt);
		
		System.out.println(arrsetdt);
		System.out.println(arritem_settlamnt);
		
		System.out.println("closing_balance"+closing_balance);
		System.out.println("cash_dispense"+cash_dispense);
		
	float closingbal =	Float.parseFloat(closing_balance);
	float 	cashdispense=	Float.parseFloat(cash_dispense);
		 
			msg = glbalanceservice.getGLBalance(closingbal,cashdispense, filedate, category, subCat,arrsetdt,arritem_settlamnt);
		
		
		
		 logger.info("in GetHeaderList"+category);
		
		logger.info("***** GLBalance process End ****");
		return msg;

	}
	
	@RequestMapping(value = "checkDispense", method = RequestMethod.POST)
	@ResponseBody
	public String checkDispense(ModelAndView modelAndView,RedirectAttributes redirectAttributes,@RequestParam("dispense_amount")String cash_dispense,String filedate,String subCat,String difference,
			String item_setdt,String item_settlamnt,String category,HttpServletRequest request,HttpServletResponse response) throws Exception {
		logger.info("***** GlbalanceProcess1 Start ****");
		
		String [] arrsetdt = request.getParameterValues("item_setdt[]");
		String [] arritem_settlamnt  = request.getParameterValues("item_settlamnt[]");
		
		System.out.println(arrsetdt);
		System.out.println(arritem_settlamnt);
		
		System.out.println(item_setdt);
		System.out.println(item_settlamnt);
		
		item_setdt =  request.getParameter("item_setdt");
		
		item_settlamnt = request.getParameter("item_settlamnt");
		
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>(); 
		String display="";
		logger.info("GL BALANCE posT");
		SettlementBean settlementBean = new SettlementBean();
		String msg ="";
		
		System.out.println("cash_dispense"+subCat);
		
		
		 String dispense =  glbalanceservice.checkdispense(filedate,subCat,arrsetdt,category);
		 
		 /*if(category.equals("VISA")) {
			 
			 return cash_dispense;
		 }*/
		
		
		 logger.info("in cashdispense"+cash_dispense);
		
		logger.info("***** GLBalance process End ****");
		return dispense;

	}
	//PrevDispense
	
	
	@RequestMapping(value = "PrevDispense", method = RequestMethod.POST)
	@ResponseBody
	public List<GLBalanceBean> PrevDispense(ModelAndView modelAndView,RedirectAttributes redirectAttributes,@RequestParam("filedate")String filedate,String subCat,String category,
HttpServletRequest request,HttpServletResponse response) throws Exception {
		logger.info("***** GlbalanceProcess1 Start ****");
		List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>(); 
		String display="";
		logger.info("GL BALANCE posT");
		SettlementBean settlementBean = new SettlementBean();
		String msg ="";
		
		System.out.println("cash_dispense"+subCat);
		
		
		List<GLBalanceBean> dispense =  glbalanceservice.prevdispense(filedate,subCat,category);
		
		System.out.println(dispense);
		
		 logger.info("in cashdispense"+filedate);
		
		logger.info("***** GLBalance process End ****");
		return dispense ;

	}
	
	@RequestMapping(value = "GLReportDownload", method = RequestMethod.POST)
	@ResponseBody
	public void getdownload(ModelAndView modelAndView,RedirectAttributes redirectAttributes,HttpServletRequest request,HttpServletResponse response) throws Exception {
		
	
		ServletContext context = request.getServletContext();
		SettlementBean settlementBean = new SettlementBean();
		
	try {
		
		String category=  request.getParameter("rectyp");
		String closing_balance=  request.getParameter("closing_balance");
		String cash_dispense=  request.getParameter("cash_dispense");	
		String difference = request.getParameter("difference");
		
		System.out.println("closing_balance"+closing_balance);
		System.out.println("cash_dispense"+cash_dispense);
		String filedate=  request.getParameter("fileDate");
		String subCat=  request.getParameter("stSubCategory");
		//String category = request.getParameter("rectype");
		
		System.out.println("GLReportDownload download" +filedate);
		settlementBean.setCategory(category);
		settlementBean.setStsubCategory(subCat);
		settlementBean.setDatepicker(filedate);
		

		if(category.equals("CASHNET")) {
			
			glbalanceservice.generate_Reports(settlementBean,response,closing_balance,cash_dispense,filedate,subCat,difference);
		
		}else if(category.equals("VISA")) {
			
			glbalanceservice.generate_VISAReports(settlementBean,response,closing_balance,cash_dispense,filedate,subCat,difference);
		
		}
		

	} catch (Exception e) {
		System.out.println("Exception in downloadReports " + e);
		// return e.getMessage();
		redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
	}
		
		
		
	}
	
}
