package com.recon.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import com.recon.model.CompareSetupBean;
import com.recon.model.LoginBean;
import com.recon.model.NFSFeeGstBean;
import com.recon.model.NFSSettlementBean;
import com.recon.model.NFSSuspectTxnBean;
import com.recon.model.SettlementBean;
import com.recon.service.NfSFeeGstService;
import com.recon.service.impl.NfSFeeGstServiceImpl;
import com.recon.util.FileDetailsJson;

@Controller
public class NFSFeeGstController {

@Autowired	
NfSFeeGstService  nfSFeeGstService;	

private static final Logger logger = Logger.getLogger(NFSFeeGstController.class);

@RequestMapping(value = "NFSFeeGSTReport",method=RequestMethod.GET)	
public ModelAndView nfsFeeGstReport(ModelAndView modelAndView,HttpServletRequest request) {
	modelAndView.addObject("NFSFeeGstBean", new NFSFeeGstBean());
	modelAndView.setViewName("NFSFeeGST");
	return modelAndView;
}


@RequestMapping(value = "ValidateNFSFeeReport", method = RequestMethod.POST)
@ResponseBody
public String ValidateNFSFeeReport(@ModelAttribute("NFSFeeGstBean")NFSFeeGstBean NFSFeeGstBean,HttpServletRequest request,
		HttpSession httpSession,RedirectAttributes redirectAttributes,Model model) throws Exception {
	logger.info("***** DownloadSettlementreport.POST Start ****");
	//logger.info("Data "+filename+" "+category+" "+stSubCategory+" "+datepicker+" "+cycle);
	logger.info("DownloadAdjTTUM POST");

	List<Object> Excel_data = new ArrayList<Object>();
	String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
	logger.info("Created by is "+Createdby);
	//GETTING , IN DATE FIELD
	boolean executed = false;

	HashMap<String, Object> output = nfSFeeGstService.validateNFSSettProcess(NFSFeeGstBean.getNetwork(),
			NFSFeeGstBean.getFromDate(), NFSFeeGstBean.getToDate());

	
	if(output != null && (Boolean)output.get("result"))
	{
		return "success";
	}
	else
	{
		return output.get("msg").toString();
	}

}	



@RequestMapping(value="nfsFeeGstReportDownload",method = RequestMethod.POST)
@ResponseBody
public void nfsFeeGstReportDownload(@ModelAttribute("NFSFeeGstBean")NFSFeeGstBean NFSFeeGstBean,Model model,HttpServletResponse response) throws IOException {
	System.out.println("neeetwrk  "+NFSFeeGstBean.getNetwork()+"    from date  "+NFSFeeGstBean.getFromDate()+ "  to date  "+NFSFeeGstBean.getToDate() );
	String TEMP_DIR = System.getProperty("java.io.tmpdir");
	String stPath = TEMP_DIR+"FeeGstData";
	String resp="";
	resp=nfSFeeGstService.processData(NFSFeeGstBean);
	if(resp =="success") {
	File directory = new File(stPath);
	if(!directory.exists())
	{
		directory.mkdir();
	}
	
	String report1 = "FeeGstReport1.xls";;
	String report2 = "FeeGstReport2.xls";;
	NFSFeeGstBean.setStPath(stPath);
	List<Object> listData1=new ArrayList<Object>();
	List<Object> listData2=new ArrayList<Object>();
	
	listData1=nfSFeeGstService.getnfsFeeGstData(NFSFeeGstBean, "REPORT1");
	
	listData2=nfSFeeGstService.getnfsFeeGstData(NFSFeeGstBean, "REPORT2");
	
	
	
	nfSFeeGstService.generateExcelTTUM(stPath, report1, listData1, response);
	nfSFeeGstService.generateExcelTTUM(stPath, report2, listData2, response);
	
	File file = new File(stPath + File.separator + "Fee_Gst_Report.zip");
	FileInputStream inputstream = new FileInputStream(file);
	response.setContentLength((int) file.length());
	response.setContentType("application/txt");
	String headerKey = "Content-Disposition";
	String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
	response.setHeader(headerKey, headerValue);

	OutputStream outStream = response.getOutputStream();
	IOUtils.copy(inputstream, outStream);
	response.flushBuffer();
	}
	/*
	 * model.addAttribute("ReportName", "FeeGstReport"); model.addAttribute("data",
	 * listData); return "GenerateNFSDailyReport";
	 */
	 
	//return "NFSFeeGST";
}

@RequestMapping(value = "NFSSuspectedTxnReport",method=RequestMethod.GET)	
public ModelAndView NFSSuspectedTxnReport(ModelAndView modelAndView,HttpServletRequest request) {
	modelAndView.addObject("NFSSuspectTxnBean", new NFSSuspectTxnBean());
	modelAndView.setViewName("NFSSuspectedTxnReport");
	return modelAndView;
}	

@RequestMapping(value = "checkIfSuspectTxnProcess",method = RequestMethod.POST)
@ResponseBody
public String checkIfSuspectTxnProcess(@RequestParam("network")String network,@RequestParam("date")String date,HttpServletRequest request,FileDetailsJson dataJson,HttpServletResponse response) {
	System.out.println("in checkIfSuspectTxnProcess..");
	System.out.println("network   "+network);
	System.out.println("date   "+date);
	String res=nfSFeeGstService.checkIfSuspectTxnProcess(network, date);
	System.out.println("res   "+res);
	return res;
}

@RequestMapping(value = "processNFSSuspectTxn",method =RequestMethod.POST)
@ResponseBody
public String processNFSSuspectTxn(@RequestParam("network")String network,@RequestParam("date")String date) {
	String check=nfSFeeGstService.checkIfSuspectTxnProcess(network, date);
	String res="";
	if(!check.equalsIgnoreCase("success")) {
		System.out.println("in ifff...");
	res=nfSFeeGstService.processNFSSuspectTxn(network, date);
	}else {
		res=check;
	}
	return res;
}

@RequestMapping(value="nfsSuspectTxnReportDownload",method = RequestMethod.POST)
public String nfsSuspectTxnReportDownload(@ModelAttribute("NFSSuspectTxnBean")NFSSuspectTxnBean NFSSuspectTxnBean,Model model) {
	List<Object> listData=new ArrayList<Object>();
	listData=nfSFeeGstService.getnfsSuspextTxnData(NFSSuspectTxnBean);
	model.addAttribute("ReportName", "Suspect_Txn_Report");
	model.addAttribute("data", listData);
	return "GenerateNFSDailyReport";
}
	
}
