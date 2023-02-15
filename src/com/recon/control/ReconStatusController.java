package com.recon.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.model.CompareSetupBean;
import com.recon.model.LoginBean;
import com.recon.model.ReconStatusBean;
import com.recon.service.ReconStatusService;


@Controller
public class ReconStatusController {
	
	@Autowired
	ReconStatusService reconstatusService;

@RequestMapping(value = "GetReconStatus", method = RequestMethod.GET)
public String getReconStatusPage(Model model)
{
	System.out.println("hello inside GetReconStatus");
	List<String> cate_list = reconstatusService.getAllCategories(); 
	
	model.addAttribute("ReconStatusBean",new ReconStatusBean());
	model.addAttribute("cate_list",cate_list);
	
	return "ReconStatus";
	
}


@RequestMapping(value = "ViewReconStatus", method = RequestMethod.GET)
public String  compareData(@RequestParam("category")String category,@RequestParam("subcate")String subcategory,
		 RedirectAttributes redirectAttributes,HttpSession httpsession,Model model,HttpSession httpSession)
{
	
	try
	{
		ReconStatusBean reconStatusBean = new ReconStatusBean();
		reconStatusBean.setStCategory(category);
		reconStatusBean.setStSubCategory(subcategory);
		//reconStatusBean.setStEntryBy(((LoginBean) httpSession.getAttribute("loginBean")).getUser_id());
		
		List<CompareSetupBean> setupBeans = reconstatusService.getlastUploadDetails(reconStatusBean);
		
		model.addAttribute("setupBeans",setupBeans);
		
		
		return "UploadedFileDetails";
		
	}
	catch(Exception e)
	{

		return "redirect:Login.do";
	}
}


@RequestMapping(value = "DownloadReconStatus",method = RequestMethod.POST)
public String getReconStatusReport(ReconStatusBean reconStatusBean,Model model)
{
	
	try
	{
		if(!reconStatusBean.getStSubCategory().equals("-"))
		{
			reconStatusBean.setStMergerCategory(reconStatusBean.getStCategory()+"_"+reconStatusBean.getStSubCategory().substring(0, 3));
		}
		else
			reconStatusBean.setStMergerCategory(reconStatusBean.getStCategory());
		
		//reconStatusBean.setStEntryBy(((LoginBean) httpSession.getAttribute("loginBean")).getUser_id());
		
		List<CompareSetupBean> setupBeans = reconstatusService.getReconStatusReport(reconStatusBean);
		
		String stFileName = reconStatusBean.getStMergerCategory()+"Status";
		
		
		Map<String, Object> map = new HashMap<String, Object>();
		model.addAttribute("fileName",stFileName);
				
		model.addAttribute("setupBeans",setupBeans);
		
		return "generateReconStatusReport";
		
	
		
	}
	catch(Exception e)
	{
		return "";
	}
	
	
}

}
