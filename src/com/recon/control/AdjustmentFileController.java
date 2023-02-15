package com.recon.control;

import java.util.HashMap;
import java.util.List;

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

import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.service.AdjustmentFileService;
import com.recon.service.ISourceService;
import com.recon.util.CSRFToken;

@Controller
public class AdjustmentFileController {

	//private static final Logger logger = Logger.getLogger(AdjustmentFileController.class);
	private static final Logger logger = Logger.getLogger(AdjustmentFileController.class);
	
	@Autowired ISourceService iSourceService;
	
	@Autowired AdjustmentFileService adjService;

	//Adjustment File upload
	@RequestMapping(value = "AdjustmentFileUpload", method = RequestMethod.GET)
	public ModelAndView AdjustmentFileUploadGet(ModelAndView modelAndView,@RequestParam("category")String category,HttpServletRequest request) throws Exception {
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
	}
	
	@RequestMapping(value = "AdjustmentFileUpload", method = RequestMethod.POST)
	@ResponseBody
	public String AdjustmentFileUploadPost(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request,
			@RequestParam("file") MultipartFile file, String filename,
			String category,String stSubCategory,String datepicker ,HttpSession httpSession,
			Model model,ModelAndView modelAndView,RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** AdjustmentFileUpload.Post Start ****");
		HashMap<String, Object> output = null;
		try
		{
			logger.info("RECON PROCESS GET");
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is "+Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			logger.info("VALUES ARE "+nfsSettlementBean+" "+category+" "+stSubCategory+datepicker);
			//1. VALIDATION FOR PREVIOUS FILE UPLOADED
			nfsSettlementBean.setCategory(category+"_ADJUSTMENT");
			nfsSettlementBean.setStSubCategory("-");
			nfsSettlementBean.setFileName(filename);
			HashMap<String, Object> result = adjService.validateAdjustmentFileUpload(nfsSettlementBean);

			if(result != null && (boolean)result.get("result"))
			{
				output = adjService.uploadAdjustmentFile(nfsSettlementBean, file);
				logger.info("***** AdjustmentFileController.AdjustmentFileUpload Post End ****");
				if((boolean)output.get("result"))
				{
					return "File Uploaded Successfully \n Count is "+(Integer)output.get("count");
				}
				else
				{
					if((Integer)output.get("count") == -1)
					{
						return "AdjSettlementDate Column has date different than selected Date";
					}
					else
					{
						return "Error while Uploading file";
					}
				}
			}
			else
			{
				return result.get("msg").toString();
			}
		}
		catch(Exception e)
		{

			logger.info("Exception in NFSSettlementController "+e);
			if(output != null  && !(boolean)output.get("result") )
			{	
				return "Error Occured at Line "+(Integer)output.get("count");
			}
			else
				return "Error Occurred in reading";
		}
		
		
	}
	
//adjustment file rollback
	@RequestMapping(value = "AdjustmentFileRollback", method = RequestMethod.POST)
	@ResponseBody
	public String AdjustmentFileRollback(@ModelAttribute("nfsSettlementBean")  NFSSettlementBean nfsSettlementBean,HttpServletRequest request
			,HttpSession httpSession,
			Model model,ModelAndView modelAndView,RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** AdjustmentFileUpload.Post Start ****");
		HashMap<String, Object> output = null;
		
			logger.info("RECON PROCESS GET");
			String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
			logger.info("Created by is "+Createdby);
			nfsSettlementBean.setCreatedBy(Createdby);
			logger.info("VALUES ARE "+nfsSettlementBean.getCategory()+"2.  "+nfsSettlementBean.getDatepicker()+
					"3. "+nfsSettlementBean.getFileName());
			nfsSettlementBean.setCategory("NFS_ADJUSTMENT");
			nfsSettlementBean.setStSubCategory("-");
			nfsSettlementBean.setFileName("NTSL-NFS");
			//1. check whether adjustment file is uploaded
			HashMap<String, Object> result = adjService.validateAdjustmentFileUpload(nfsSettlementBean);
			
			
			if(result != null && ! (Boolean) result.get("result"))
			{
				if(result.get("msg").toString().equals("File is already uploaded!!!"))
				{
				//2 rollback adjustment
				if(adjService.AdjustmentRollback(nfsSettlementBean))
				{
					return "Rollback is completed";
				}
				else
				{
					return "Issue while rolling back Adjustment File";
				}
				}
				else
					return output.get("msg").toString();
			}
			else
			{
				return "Adjustment File is not uploaded";
			}
		
	}
}
