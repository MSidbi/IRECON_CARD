package com.recon.control;

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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.dao.SettlementRollbackDao;
import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.service.AdjustmentFileService;
import com.recon.service.ISourceService;
import com.recon.util.CSRFToken;

@Controller
public class RollBackController {

	//private static final Logger logger = Logger.getLogger(AdjustmentFileController.class);
	private static final Logger logger = Logger.getLogger(RollBackController.class);
	
	@Autowired ISourceService iSourceService;
	
	@Autowired SettlementRollbackDao Rollbackservice;

	/*//Adjustment File upload
	@RequestMapping(value = "NFSSettlementRollback", method = RequestMethod.GET)
	public ModelAndView NFSSettlementRollbackGet(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** AdjustmentFileUpload.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("nfsFileUpload GET");
		String display="";
		 logger.info("in GetHeaderList"+category);
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("category", category);
        modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("AdjustmentFileUpload");
		
		logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
		return modelAndView;
	}*/
	
	@RequestMapping(value = "NFSSettlementRollback", method = RequestMethod.POST)
	@ResponseBody
	public String NFSSettlementRollbackPost(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,
			Model model,ModelAndView modelAndView,RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** SettlementRollback.post Start ****");
		logger.info("Data is "+nfsSettlementBean.getDatepicker()+" 2. "+nfsSettlementBean.getCycle()+" 3. "
				+nfsSettlementBean.getFileName());
		
		// check settlement is processed or not
		Map<String, Object> output = new HashMap<>();
		output = Rollbackservice.NFSvalidateSettlementProcess(nfsSettlementBean);
		
		if(output != null && (Boolean) output.get("result"))
		{
			//rollback call
			Boolean flag = Rollbackservice.NFSSettlementRollback(nfsSettlementBean);
			
			if(flag)
			{
				return "Rollback Completed";
			}
			else
			{
				return "RollBack not completed";
			}
		}
		else
		{
			return output.get("msg").toString();
		}
		
		
	}
	
	@RequestMapping(value = "NFSVoucherRollBack", method = RequestMethod.POST)
	@ResponseBody
	public String NFSVoucherRollBackPost(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,
			Model model,ModelAndView modelAndView,RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** NFSVoucherRollBack.post Start ****");
		logger.info("Data is "+nfsSettlementBean.getDatepicker());
		
		// check settlement is processed or not
		Map<String, Object> output = new HashMap<>();
		output = Rollbackservice.NFSSettVoucherValidation(nfsSettlementBean);
		
		if(output != null && (Boolean) output.get("result"))
		{
			//rollback call
			Boolean flag = Rollbackservice.NFSSettVoucherRollback(nfsSettlementBean);
			
			if(flag)
			{
				return "Rollback Completed";
			}
			else
			{
				return "RollBack not completed";
			}
		}
		else
		{
			return output.get("msg").toString();
		}
		
		
}

/*************************	NFS NTSL UPLOAD ROLLBACK ***************************/	
	@RequestMapping(value = "NTSLRollback", method = RequestMethod.GET)
	public ModelAndView NTSLRollbackGet(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** NTSLRollback.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NTSLRollback GET");
		String display="";
		 logger.info("in GetHeaderList"+category);
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("category", category);
        modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("NTSLFileRollback");
		
		logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "NTSLRollback", method = RequestMethod.POST)
	@ResponseBody
	public String NTSLRollback(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,
			Model model,ModelAndView modelAndView,RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** NTSLRollback.post Start ****");
		logger.info("Data is "+nfsSettlementBean.getDatepicker());
		
		// check settlement is processed or not
		Map<String, Object> output = new HashMap<>();
		output = Rollbackservice.NtslValidation(nfsSettlementBean);
		
		if(output != null && (Boolean) output.get("result"))
		{
			//rollback call
			Boolean flag = Rollbackservice.nfsNTSLRollback(nfsSettlementBean);
			
			if(flag)
			{
				return "Rollback Completed";
			}
			else
			{
				return "RollBack not completed";
			}
		}
		else
		{
			return output.get("msg").toString();
		}
		
		
}
}
