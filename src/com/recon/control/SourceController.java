package com.recon.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.recon.model.CompareSetupBean;
import com.recon.model.ConfigurationBean;
import com.recon.model.LoginBean;
import com.recon.service.ISourceService;
import com.recon.util.FileDetailsJson;

@Controller
public class SourceController {
	
	private static final String ERROR_MSG = "error_msg";
	private static final String SUCCESS_MSG = "success_msg";
	private static final Logger logger = Logger.getLogger(SourceController.class);
	
	@Autowired ISourceService iSourceService;
	
	@RequestMapping(value="AddSourceConfiguration" , method = RequestMethod.GET)
	public String CompareConfiguration(Model model, ConfigurationBean configBean)
	{
		List<ConfigurationBean> comp_dtl_list = new ArrayList<ConfigurationBean>();
		/** Add new Objects if less or none are available. */
		for (int i = comp_dtl_list.size() + 1; i <= 1; i++) {
			comp_dtl_list.add(new ConfigurationBean());
		}
		
		List <String> tbl_list = iSourceService.gettable_list();
		
		configBean.setComp_dtl_list(comp_dtl_list);
		
		model.addAttribute("ConfigBean",configBean);
		model.addAttribute("tbl_list", tbl_list);
		model.addAttribute("comp_list",comp_dtl_list);
		return "FileConfiguration";
	}
	
	
	@RequestMapping(value="addConfiguration" , method = RequestMethod.POST)
	public String CompareConfig(@ModelAttribute("ConfigBean") ConfigurationBean configBean , Model model, RedirectAttributes redirectAttributes,HttpSession httpSession)
	{
		int inFileId = 0;
		boolean chktbl= false;
		
		
		try
		{	
			//setting user id
			configBean.setStEntry_By(((LoginBean) httpSession.getAttribute("loginBean")).getUser_id());
			
			
			chktbl = iSourceService.chkTblExistOrNot(configBean);
			
			if(chktbl){

			//1. Create File Id for all tables
			inFileId = iSourceService.getFileId(configBean);
			
			configBean.setInFileId(inFileId+1);
			
			boolean result = iSourceService.addFileSource(configBean);
			
			
			if(result) {
				
				
				//2. Entry in tables (PLACE THIS METHOD AT THE END)
				//	iSourceService.addConfigParams(configBean);
					
					redirectAttributes.addFlashAttribute(SUCCESS_MSG, "Configuration Completed Successfully.");
				
			}else {
				
				redirectAttributes.addFlashAttribute(ERROR_MSG, "Configuration not Completed.");
				
			}
			
			}else {
				
				
				redirectAttributes.addFlashAttribute(ERROR_MSG, "Configuration already exist.");
			}
			
		
			
			return "redirect:AddSourceConfiguration.do";

		}
		catch(Exception e)
		{
			//System.out.println("Exception is "+e);
			 logger.error(e.getMessage());
				redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
				return "redirect:Login.do";
			
		}
		//return "Configuration";
	}

	

	
	
	
	@RequestMapping(value = "FileSource", method = RequestMethod.GET)
	public ModelAndView fileSource(ModelAndView modelAndView) {
		modelAndView.setViewName("FileSource");
		return modelAndView;
	}
	
	
		
		@RequestMapping(value = "ViewFileDtls", method = RequestMethod.GET)
	public String viewfileSource(Model model, ConfigurationBean configBean) {
		
			model.addAttribute("ConfigBean", configBean);
			
		return "ViewFileSource";
				
	}
	
	
	@RequestMapping(value = "UpdateFileDtls", method = RequestMethod.GET)
	public String fileDetlmngr(Model model , ConfigurationBean configBean, HttpSession httpSession) {
		try {
			
			

			//System.out.println(((LoginBean) httpSession.getAttribute("loginBean")).getUser_id());

			//List<FTPBean> ftpFileList =iSourceService.getFileList();
			//model.addAttribute("ftpFileList", ftpFileList);
			model.addAttribute("ConfigBean", configBean);
			
			return "UpdtFileDetails";
		
		} catch (Exception e) {
			
			logger.error(e.getMessage());
			//model.addAttribute("ftpFileList", null);
			return "UpdtFileDetails";
		}
	}
	
	
	 @RequestMapping(value = "/searchFileList", method = RequestMethod.GET)
	    public String view(Model model) throws Exception {
		// ConfigurationBean configBean  = new ConfigurationBean();
		List<ConfigurationBean> configBeanlist = 	iSourceService.getFileDetails();
		
		System.out.println(configBeanlist);
	       
		//model.addAttribute("configBean", configBean);
		model.addAttribute("configBeanlist", configBeanlist);
	        return "gridHelp";
	    }
	 
	 @RequestMapping(value="/viewHeader", method = RequestMethod.GET)
	    public String viewheader(Model model,@RequestParam("fileId")int fileId,HttpServletRequest request,LoginBean loginBean,RedirectAttributes redirectAttributes) throws Exception {
		// ConfigurationBean configBean  = new ConfigurationBean();
		 try{
		 loginBean.setUser_id(((LoginBean) request.getSession().getAttribute("loginBean")).getUser_id().trim());
		 String[] headers= 	(iSourceService.getHeaderList(fileId)).split(",");
		 List<String> headerlist = new ArrayList<String>();
		 for(String header : headers ){
			 
			 headerlist.add(header);
		 }
		
		 
		 
		
		System.out.println(headerlist);
	       
		//model.addAttribute("configBean", configBean);
		model.addAttribute("headerlist", headerlist);
	        return "Headers";
		 }catch(Exception e){
			 
			 logger.error(e.getMessage());
				redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
				return "redirect:Login.do";
			 
		 }
	    }
	 
	 @RequestMapping(value = "/saveFileData", method = RequestMethod.POST)
		public String saveFileData(@ModelAttribute("ConfigBean") ConfigurationBean configBean, BindingResult bindingResult, Model model, HttpServletRequest request, HttpSession httpSession,RedirectAttributes redirectAttributes) {
			try {

				System.out.println("1");
				//loginValidator.validate(loginBean, bindingResult);
				if (bindingResult.hasErrors()) {
					return "Login";
				}
				 boolean result =false;
				System.out.println(configBean.getInFileId());
				result =iSourceService.updateFileDetails(configBean);
				
				if(result) {
				
					redirectAttributes.addFlashAttribute(SUCCESS_MSG, "Record Updated successfully!!");
				
				}else{
					
					redirectAttributes.addFlashAttribute(ERROR_MSG, "Record not Updated !!");
					
				}
				
				
				return "redirect:UpdateFileDtls.do";
			} catch (Exception e) {
				httpSession.invalidate();
				//logger.error(e.getMessage());
				model.addAttribute(ERROR_MSG, e.getMessage().toString());
				redirectAttributes.addFlashAttribute(ERROR_MSG, "Error occurred while updating a record!!");
				return "redirect:UpdtFileDetails.do";
			}
		}
	
	 //MenuConfigureType.do
	 @RequestMapping(value="MenuConfigureType" , method = RequestMethod.GET)
		public ModelAndView  MenuConfigureType(ModelAndView modelView, ConfigurationBean configBean)
		{
		 modelView.setViewName("MenuConfigureType");
			return modelView;
		}
	

	 @RequestMapping(value="ConfigureType" , method = RequestMethod.GET)
		public String addConfiguretyp(Model model, ConfigurationBean configBean) throws Exception
		{
			List<ConfigurationBean> comp_dtl_list = new ArrayList<ConfigurationBean>();
			List<ConfigurationBean> clasify_dtl_list = new ArrayList<ConfigurationBean>();
			/** Add new Objects if less or none are available. */
			for (int i = comp_dtl_list.size() + 1; i <= 1; i++) {
				comp_dtl_list.add(new ConfigurationBean());
			}
			for (int i = clasify_dtl_list.size() + 1; i <= 1; i++){
				
				clasify_dtl_list.add(new ConfigurationBean());
			}
			
			configBean.setComp_dtl_list(comp_dtl_list);
			configBean.setClasify_dtl_list(clasify_dtl_list);
			
			List<ConfigurationBean> configBeanlist = 	iSourceService.getFileDetails();
			
			model.addAttribute("configBeanlist", configBeanlist);
			
			model.addAttribute("ConfigBean",configBean);
			model.addAttribute("comp_list",comp_dtl_list);
			model.addAttribute("classify_list",clasify_dtl_list);
			return "ConfigureType";
		}
	 
	 
	 @RequestMapping(value="ViewConfigureType" , method = RequestMethod.GET)
		public String ViewConfigureType(LoginBean loginBean,Model model, ConfigurationBean configBean,HttpServletRequest request,RedirectAttributes redirectAttributes)
		{
		 try {
			 loginBean.setUser_id(((LoginBean) request.getSession().getAttribute("loginBean")).getUser_id().trim());	
		 List<ConfigurationBean> configBeanlist = 	iSourceService.getFileDetails();
		       
			
		 	model.addAttribute("configBeanlist", configBeanlist);
			
			model.addAttribute("ConfigBean",configBean);
	
			return "ViewConfigureType";
		 }catch(Exception e) {
			 logger.error(e.getMessage());
				redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
				return "redirect:Login.do";
			 
		 }
		}
	 
	 //ConfigureType
	 @RequestMapping(value = "/ViewConfigureTypeDtls", method = RequestMethod.GET)
	    public String viewConfigureTypeDtls(LoginBean loginBean,Model model, @RequestParam("fileId")int fileId,@RequestParam("category")String category,@RequestParam(value="subcat",required = false) String subcat ,HttpServletRequest request,RedirectAttributes redirectAttributes) throws Exception {
		// ConfigurationBean configBean  = new ConfigurationBean();
		 try {
			 loginBean.setUser_id(((LoginBean) request.getSession().getAttribute("loginBean")).getUser_id().trim());
			 System.out.println("ViewConfigureTypeDtls");
			 List<ConfigurationBean> comparedtllist = 	iSourceService.getCompareDetails(fileId, category,subcat);
			
			 System.out.println(comparedtllist);
		       
			
			 model.addAttribute("comparedtllist", comparedtllist);
		     return "ConfigureTypeDtls";
		 } catch (Exception e) {
				logger.error(e.getMessage());
				redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
				return "redirect:Login.do";
			}
	    }
	 
	 //ViewKnockoffDtls
	 @RequestMapping(value = "/ViewKnockoffDtls", method = RequestMethod.GET)
	    public String ViewKnockoffDtls(LoginBean loginBean,Model model, @RequestParam("fileId")int fileId,@RequestParam("category")String category ,@RequestParam(value="subcat", required = false ) String subcat,HttpServletRequest request,RedirectAttributes redirectAttributes) throws Exception {
		// ConfigurationBean configBean  = new ConfigurationBean();
		 
		 try {
			 
			 loginBean.setUser_id(((LoginBean) request.getSession().getAttribute("loginBean")).getUser_id().trim());
			 System.out.println("ViewConfigureTypeDtls");
			List<ConfigurationBean> knockofflist = 	iSourceService.getknockoffDetails(fileId, category,subcat);
			List<ConfigurationBean> knockoffcrtlist = 	iSourceService.getknockoffcrt(fileId, category,subcat);
			//
			
			System.out.println(knockofflist);
			System.out.println(knockoffcrtlist);
		       
			
			model.addAttribute("knockofflist", knockofflist);
			model.addAttribute("knockoffcrtlist", knockoffcrtlist);
		        return "KnockoffDtls";
		 }catch (Exception e) {
				logger.error(e.getMessage());
				redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
				return "redirect:Login.do";
			}
	    }
		 
	
	 
	 
	 @RequestMapping(value = "/GetHeaderList", method = RequestMethod.POST)
	 @ResponseBody
	 public String GetHeaderList (@RequestParam("fileId")int fileId){
		
		 
		 System.out.println("in GetHeaderList"+fileId);
		 
		String hdrlist = iSourceService.getHeaderList(fileId);
		 
		 return hdrlist;
		 
		 
		 
	 }

	 @RequestMapping(value = "/saveClassification", method = RequestMethod.POST)
		public String saveclassification(@ModelAttribute("ConfigBean") ConfigurationBean configBean, BindingResult bindingResult, Model model, HttpServletRequest request, HttpSession httpSession,RedirectAttributes redirectAttributes) {
			try {

				System.out.println("1");
				//System.out.println(configBean);
				//loginValidator.validate(loginBean, bindingResult);
				configBean.setStEntry_By(((LoginBean) httpSession.getAttribute("loginBean")).getUser_id());
				 boolean result =false;
				System.out.println(configBean.getInFileId());
				result = iSourceService.addConfigParams(configBean);
				
				if(result) {
				
					redirectAttributes.addFlashAttribute(SUCCESS_MSG, "Details saved successfully!!");
				
				}else{
					
					redirectAttributes.addFlashAttribute(ERROR_MSG, "Error Ocuured While saving Details!!");
					
				}
				
				
				return "redirect:ConfigureType.do";
			} catch (Exception e) {
				httpSession.invalidate();
				//logger.error(e.getMessage());
				 logger.error(e.getMessage());
					redirectAttributes.addFlashAttribute(ERROR_MSG, e.getMessage());
					return "redirect:Login.do";
			}
		}
	
	 @RequestMapping(value = "/getFiledetails", method = RequestMethod.POST)
	 @ResponseBody
	 public  FileDetailsJson GetHeaderList (@RequestParam("category")String category,@RequestParam("subcat")String subcat,FileDetailsJson dataJson){
		
		 try{
		 JSONObject objJSON = new JSONObject();
			List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>(); 
			 
			 System.out.println("in GetHeaderList"+category);
			 HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
	         Gson gson = new GsonBuilder().setPrettyPrinting().create();
			 
			 setupBeans =  iSourceService.getFileList(category,subcat);
			 JSONROOT.put("Records", setupBeans);
			 String jsonArray = gson.toJson(JSONROOT);
			 dataJson.setParams("OK", setupBeans,0);
			 
			 objJSON.put("setupBeans", setupBeans);
		 }catch(Exception e){
			 
			 logger.error(e.getMessage());
			 dataJson.setParams("ERROR", e.getMessage());
			 
		 }
			 
			 
			return dataJson;
			
		 
	 }
	 
	 
	 @RequestMapping(value = "/getFileTypedetails", method = RequestMethod.POST)
	 @ResponseBody
	 public  FileDetailsJson GetTypeList (@RequestParam("category")String category,@RequestParam("subcat")String subcat,@RequestParam("tablename")String tablename,FileDetailsJson dataJson){
		
		 try{
		 JSONObject objJSON = new JSONObject();
			List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>(); 
			 
			 System.out.println("in GetHeaderList"+category);
			 HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
	         Gson gson = new GsonBuilder().setPrettyPrinting().create();
			 
			 setupBeans =  iSourceService.getFileTypeList(category,subcat,tablename);
			 JSONROOT.put("Records", setupBeans);
			 String jsonArray = gson.toJson(JSONROOT);
			 dataJson.setParams("OK", setupBeans,0);
			 
			 objJSON.put("setupBeans", setupBeans);
		 }catch(Exception e){
			 
			 logger.error(e.getMessage());
			 dataJson.setParams("ERROR", e.getMessage());
			 
		 }
			 
			 
			return dataJson;
			
		 
	 }
	 @RequestMapping(value = "/getSubCategorydetails", method = RequestMethod.POST)
	 @ResponseBody
	 public  FileDetailsJson getSubCategory (@RequestParam("category")String category,FileDetailsJson dataJson){
		
		 try{
		 JSONObject objJSON = new JSONObject();
//			List<CompareSetupBean> setupBeans = new ArrayList<CompareSetupBean>();
		 List<String> subcat = new ArrayList<>();
			 
			 System.out.println("in GetHeaderList"+category);
			 HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
	         Gson gson = new GsonBuilder().setPrettyPrinting().create();
			 
	         subcat = iSourceService.getSubcategories(category);
			// setupBeans =  iSourceService.getFileList(category,subcat);
	         
			/* JSONROOT.put("Records", setupBeans);
			 String jsonArray = gson.toJson(JSONROOT);
			 dataJson.setParams("OK", setupBeans,0);
			 
			 objJSON.put("setupBeans", setupBeans);*/
	         JSONROOT.put("Subcategories", subcat);
			 String jsonArray = gson.toJson(JSONROOT);
			// dataJson.setParams("OK", subcat,0);
			 dataJson.setParams("OK", 0 , subcat);
			 
			 objJSON.put("Subcats", subcat);
		 }catch(Exception e){
			 
			 logger.error(e.getMessage());
			 dataJson.setParams("ERROR", e.getMessage());
			 
		 }
			 
			 
			return dataJson;
			
		 
	 }
	
	
}
