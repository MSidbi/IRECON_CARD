package com.recon.control;

import java.util.ArrayList;
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

import com.recon.model.Act4Bean;
import com.recon.model.FisdomFileUploadBean;
import com.recon.model.LoginBean;
import com.recon.model.NFSSettlementBean;
import com.recon.service.FisdomFileUploadService;
import com.recon.util.CSRFToken;

@Controller
public class FisdomFileUploadController {
	
	private static final Logger logger = Logger.getLogger(FisdomFileUploadController.class);
	
	@Autowired
	FisdomFileUploadService fisdomFileUploadService;
	
	@RequestMapping(value = "FisdomFileUpload", method = RequestMethod.GET)
	public ModelAndView FisdomFileUploadGet(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
		logger.info("***** FisdomFileUploadController.Get Start ****");
		FisdomFileUploadBean fisdomFileUploadBean = new FisdomFileUploadBean();
		logger.info("FisdomFileUpload GET");
		String display="";
         
         String csrf = CSRFToken.getTokenForSession(request.getSession());
		 
 		modelAndView.addObject("CSRFToken", csrf);
        modelAndView.addObject("fisdomFileUploadBean",fisdomFileUploadBean);
		modelAndView.setViewName("FisdomFileUpload");
		
		logger.info("***** NFSSettlementController.AdjustmentFileUpload GET End ****");
		return modelAndView;
	}

	
	@RequestMapping(value = "FisdomFileUpload", method = RequestMethod.POST)
	@ResponseBody
	public String FisdomFileUploadPost(@ModelAttribute("fisdomFileUploadBean")  FisdomFileUploadBean fisdomFileUploadBean,HttpServletRequest request,
			@RequestParam("file") MultipartFile file, String filename,
			String category,String stSubCategory,String datepicker ,HttpSession httpSession,
			Model model,ModelAndView modelAndView,RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** AdjustmentFileUpload.Post Start ****");
		HashMap<String, Object> output = null;
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		fisdomFileUploadBean.setCreatedBy(Createdby);
		try
		{
			logger.info("data is 1. "+fisdomFileUploadBean.getFileDate()+" 2. "+fisdomFileUploadBean.getFileName());
			//1. VaLIDATE WHETHER FILE IS ALREADY UPLOADED OR NOT
			output = fisdomFileUploadService.validateFileAlreadyUploaded(fisdomFileUploadBean);
			
			//2. UPLOAD FILE
			if(output != null && (Boolean) output.get("result"))
			{
				boolean fileEntry = fisdomFileUploadService.checkTrackingTable(fisdomFileUploadBean);
				
				if(fileEntry)
				{
					output = fisdomFileUploadService.readFiles(fisdomFileUploadBean, file);

					if(output != null )
					{
						return output.get("msg").toString();
					}
					else
					{
						return "Issue while uploading file";
					}
				}
				else
				{
					return "Data is not present But Entry is present in tracking table \n Please remove entry from tracking table";
				}
			}
			else
			{
				return output.get("msg").toString();
			}
			
			
		}
		catch(Exception e)
		{

			logger.info("Exception in NFSSettlementController "+e);
		 return "Error Occurred in reading";
					
		}
		
		
	}
	
	/***** ALL GL UPLOAD *****/
	@RequestMapping(value = "GlFileUpload", method = RequestMethod.GET)
	public ModelAndView GlFileUpload(ModelAndView modelAndView,HttpServletRequest request) throws Exception {
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
		modelAndView.setViewName("UploadGlStatement");
		
		return modelAndView;
	}
	
	@RequestMapping(value = "GlFileUpload", method = RequestMethod.POST)
	@ResponseBody
	public String GlFileUploadPost(@ModelAttribute("actBean")  Act4Bean actBean,HttpServletRequest request,
			@RequestParam("file") MultipartFile file, String filename,
			String category,String stSubCategory,String datepicker ,HttpSession httpSession,
			Model model,ModelAndView modelAndView,RedirectAttributes redirectAttributes) throws Exception {
		logger.info("***** AdjustmentFileUpload.Post Start ****");
		logger.info("category is "+actBean.getCategory()+" subcat is "+actBean.getStSubCategory()+" gl acc is "+actBean.getGlAccount());
		logger.info("filedate is "+actBean.getDatepicker());
		logger.info("file name is "+file.getOriginalFilename());
		HashMap<String, Object> output = null;
		String Createdby = ((LoginBean) httpSession.getAttribute("loginBean")).getUser_id();
		logger.info("Created by is "+Createdby);
		
		try
		{
			String glSelected = actBean.getGlAccount().get(0).substring(0, actBean.getGlAccount().get(0).indexOf("("));
			System.out.println("selected gl "+glSelected);
			System.out.println("filename gl is "+file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf(".")));
			/*
			 * List<String> glAcctList=new ArrayList<String>(); glAcctList.add(glSelected);
			 */
			if(!glSelected.trim().equalsIgnoreCase(file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf(".")).trim()))
			{
				return "File uploaded and GL Selected are different";
			}
			else
			{
				//CHECK WHETHER GL FILE IS ALREADY UPLOADED
				//actBean.setGlAccount(glAcctList);
				boolean uploadFlag = fisdomFileUploadService.validateGLAlreadyUpload(actBean);
				if(!uploadFlag)
				{
				output = fisdomFileUploadService.readGLFiles(actBean, file);

				if(output != null )
				{
					return output.get("msg").toString();
				}
				else
				{
					return "Issue while uploading file";
				}
				}
				else
				{
					return " File is already uploaded ";
				}
			}
		}
		catch(Exception e)
		{
			logger.info("Exception is "+e);
			return "Exception Occurred";
		}
		
	}
}
