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

import com.recon.dao.RawFileRollbackDao;
import com.recon.dao.SettlementRollbackDao;
import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.service.AdjustmentFileService;
import com.recon.service.ISourceService;
import com.recon.util.CSRFToken;

@Controller
public class RawFileRollBackController {

	//private static final Logger logger = Logger.getLogger(AdjustmentFileController.class);
	private static final Logger logger = Logger.getLogger(RawFileRollBackController.class);
	
	@Autowired ISourceService iSourceService;
	
	@Autowired RawFileRollbackDao Rollbackservice;

	//Adjustment File upload
	@RequestMapping(value = "CashnetRawRollback", method = RequestMethod.GET)
	public ModelAndView NFSSettlementRollbackGet(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** CashnetRawRollback.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("CashnetRawRollback GET");
		String display="";
		 logger.info("in GetHeaderList"+category);
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("category", category);
        modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("CashnetRawRollback");
		
		logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "CashnetRawRollback", method = RequestMethod.POST)
	@ResponseBody
	public String CashnetRawRollbackPost(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,
			Model model,ModelAndView modelAndView,RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** CashnetRawRollback.post Start ****");
		logger.info("Data is "+nfsSettlementBean.getDatepicker()+" 2. "+nfsSettlementBean.getCycle()+" 3. "
				+nfsSettlementBean.getStSubCategory());
		//nfsSettlementBean.setFileName("CASHNET");
		
		// validate whether selected date is latest
		Map<String, Object> output = Rollbackservice.RawFileDateValidate(nfsSettlementBean);
		
		if(output!= null && (Boolean) output.get("result"))
		{
		// validate whether recon is already processed for selected date
			output = Rollbackservice.ReconValidate(nfsSettlementBean);
			if((Boolean) output.get("result"))
			{
				if(Rollbackservice.CashnetRawFileRollback(nfsSettlementBean))
				{
					return "Raw File rollback completed";
				}
				else
				{
					return "Issue while rolling back file";
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

/***** NFS Raw File rollback ******/	
	@RequestMapping(value = "NFSRawRollback", method = RequestMethod.GET)
	public ModelAndView NFSRawRollback(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** NFSRawRollback.Get Start ****");
		NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("NFSRawRollback GET");
		String display="";
		 logger.info("in GetHeaderList"+category);
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("category", category);
        modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);
		modelAndView.setViewName("NFSRawRollback");
		
		logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
		return modelAndView;
	}
	
	@RequestMapping(value = "NFSRawRollback", method = RequestMethod.POST)
	@ResponseBody
	public String NFSRawRollbackPost(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,
			Model model,ModelAndView modelAndView,RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** NFSRawRollback.post Start ****");
		logger.info("Data is "+nfsSettlementBean.getDatepicker()+" 2. "+nfsSettlementBean.getCycle()+" 3. "
				+nfsSettlementBean.getStSubCategory());
		nfsSettlementBean.setFileName("NFS");
		
		// validate whether selected date is latest
		Map<String, Object> output = Rollbackservice.RawFileDateValidate(nfsSettlementBean);
		
		if(output!= null && (Boolean) output.get("result"))
		{
		// validate whether recon is already processed for selected date
			output = Rollbackservice.ReconValidate(nfsSettlementBean);
			if((Boolean) output.get("result"))
			{
				if(Rollbackservice.CashnetRawFileRollback(nfsSettlementBean))
				{
					return "Raw File rollback completed";
				}
				else
				{
					return "Issue while rolling back file";
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
	
}
