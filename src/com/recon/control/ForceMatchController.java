package com.recon.control;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.recon.model.ForceMatchBean;
import com.recon.model.ForceMatchDataJson;
import com.recon.model.LoginBean;
import com.recon.model.ReconDataJson;
import com.recon.model.SettlementTypeBean;
import com.recon.service.IForceMatchService;
import com.recon.service.ISettelmentService;


@Controller
public class ForceMatchController {
	
	
	private static final String ERROR_MSG = "error_msg";
	private static final String SUCCESS_MSG = "success_msg";
	private static final Logger logger = Logger.getLogger(SettlementController.class);
	
	@Autowired IForceMatchService forceMatchService;
	
	@RequestMapping(value = "ForceMatch", method = RequestMethod.GET)
	public ModelAndView ReconProcess(ModelAndView modelAndView) {
		System.out.println("RECON PROCESS GET");
		modelAndView.setViewName("ForceMatch");
		return modelAndView;

	}
	
	
	@RequestMapping(value="/ShowData", method = RequestMethod.GET)
    public String showJtableReconData(@RequestParam(value="tbl") String table,@RequestParam ("date") String date , @RequestParam ("matchType") String matchType
    		,HttpServletRequest request,LoginBean loginBean,RedirectAttributes redirectAttributes,Model model,HttpSession session) throws Exception {
	
		String split_table[]=table.split("_");
		 
		 String concat_table=split_table[0]+"_"+split_table[2];
		
	 try{
		 loginBean.setUser_id(((LoginBean) request.getSession().getAttribute("loginBean")).getUser_id().trim());
		 
		 session.setAttribute("tbl", table);
		 session.setAttribute("date", date);
		 
		
			 
			 ArrayList<ForceMatchBean> dataList =  forceMatchService.getReconData(table.trim(),"",date.trim(),"");
			 
			 System.out.println(dataList.size());
			 model.addAttribute("table",table.trim());
			 model.addAttribute("dataList",dataList);
			 
			 if(concat_table.trim().contains("SWITCH")) {
				 
				 return "viewSwitchunReconData";
				 
			 } if(concat_table.trim().contains("CBS")) {
				 
				 return "viewCBSReconData";
				 
			 }else{
				 
				 return "Login.do";
			 }
        
			
			// return "ViewPWDRepo";
        
	 }catch(Exception e){
		 
		 logger.error(e.getMessage());
			redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
			return "redirect:Login.do";
		 
	 }
    }
	
	
	
	
	@RequestMapping(value="/GetUnrecontableData", method = RequestMethod.POST)
	@ResponseBody
    public  ForceMatchDataJson GetJtableData(@ModelAttribute ("forceMatchBean") ForceMatchBean forceMatchBean,  HttpServletRequest request,ForceMatchDataJson dataJson,LoginBean loginBean,int jtStartIndex, int jtPageSize,RedirectAttributes redirectAttributes,Model model,HttpSession session) throws Exception {
	 try{
		 
		 loginBean.setUser_id(((LoginBean) request.getSession().getAttribute("loginBean")).getUser_id().trim());
		 HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
         Gson gson = new GsonBuilder().setPrettyPrinting().create();
		 String table,searchValue,type,date;
		 table=(String) session.getAttribute("tbl");
		 date=(String) session.getAttribute("date");
		
			 
			 ArrayList<ForceMatchBean> dataList =  forceMatchService.getChngReconData(table.trim(),date.trim(), jtStartIndex, jtPageSize);
			 JSONROOT.put("Records", dataList);
			 
			 
			 
			 String jsonArray = gson.toJson(JSONROOT);
			 
			 int totalRecordcount = forceMatchService.getReconDataCount(table.trim(), date.trim());
			 
	       //  System.out.println(totalRecordcount);
	         dataJson.setParams("OK", dataList,totalRecordcount);
	         dataJson.setTotalRecordCount( totalRecordcount);
      
			// return "ViewPWDRepo";
        
	 }catch(Exception e){
		 
		 logger.error(e.getMessage());
		 dataJson.setParams("ERROR", e.getMessage());
		 
	 }
	 return dataJson;
    }
	
	//editUnreconData
	
	@RequestMapping(value="/editUnreconData", method = RequestMethod.POST)
    public ResponseEntity<Integer> editsave(@ModelAttribute ("settlementTypeBean") SettlementTypeBean settlementTypeBean,
    		HttpServletRequest request,LoginBean loginBean,RedirectAttributes redirectAttributes,Model model) throws Exception {
	
		 int result=200;
	 try{
		 
		/* System.out.println("pan:"+settlementTypeBean.getPan());
		 System.out.println("pan:"+settlementTypeBean.getrEMARKS());
		 System.out.println(settlementTypeBean.gettERMID());
		 System.out.println(settlementTypeBean.gettRACE());
		 System.out.println(settlementTypeBean.getSetltbl());*/
		 
		 // result = isettelmentservice.updateRecord(settlementTypeBean);
	//	System.out.println(request.getParameter("data"));
		 loginBean.setUser_id(((LoginBean) request.getSession().getAttribute("loginBean")).getUser_id().trim());
		// result = isettelmentservice.updateRecord(settlementTypeBean);
		
        return new ResponseEntity<>(result, HttpStatus.OK);
			// return "ViewPWDRepo";
        
	 }catch(Exception e){
		 
		 logger.error(e.getMessage());
			redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
			 return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		 
	 }
    }
	

}
