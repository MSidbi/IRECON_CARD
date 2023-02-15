package com.recon.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.dao.RawFileRollbackDao;
import com.recon.model.NFSSettlementBean;
import com.recon.service.ISourceService;
import com.recon.util.CSRFToken;
@Controller
public class VisaRawRollBackController {
	
private static final Logger logger = Logger.getLogger(RawFileRollBackController.class);
	
	@Autowired ISourceService iSourceService;
	
	@Autowired RawFileRollbackDao Rollbackservice;
	
	
	@RequestMapping(value = "VisaRawRollback", method = RequestMethod.GET)
	public ModelAndView VisaRollbackGet(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
		logger.info("***** CashnetRawRollback.Get Start ****");
		/*NFSSettlementBean nfsSettlementBean = new NFSSettlementBean();
		logger.info("CashnetRawRollback GET");
		String display="";
		 logger.info("in GetHeaderList"+category);
         String csrf = CSRFToken.getTokenForSession(request.getSession());
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("category", category);
        modelAndView.addObject("nfsSettlementBean",nfsSettlementBean);*/
		modelAndView.setViewName("VisaRawRollback");
		
		logger.info("*****VisaRawRollBackController.AdjustmentFileUpload GET End ****");
		return modelAndView;
	}
	
	
	
	@RequestMapping(value = "VisaRawRollback", method = RequestMethod.POST)
	@ResponseBody
	public String VisaRawRollbackPost(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			HttpSession httpSession,
			Model model,ModelAndView modelAndView,RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** CashnetRawRollback.post Start ****");
		System.out.println("" +nfsSettlementBean.getDatepicker());
		logger.info("Data is "+nfsSettlementBean.getDatepicker()+" 2. "+nfsSettlementBean.getStSubCategory()+" 3. ");
		
		//nfsSettlementBean.setFileName("CASHNET");
		// validate whether selected date is latest
		Map<String, Object> output =    Rollbackservice.VisaRawFileDateValidate(nfsSettlementBean);
		
		if(output!= null && (Boolean) output.get("result"))
		{
		// validate whether recon is already processed for selected date
			output = Rollbackservice.ReconValidate(nfsSettlementBean);
			if((Boolean) output.get("result"))
			{
				if(Rollbackservice.VisaRawFileRollback(nfsSettlementBean) ) 
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
