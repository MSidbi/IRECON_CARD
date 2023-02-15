package com.recon.control;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.recon.model.LoginBean;
import com.recon.model.NetworkFileUpdateBean;
import com.recon.service.NetworkFileUpdateService;

@Controller
public class NetworkFileUpdateController {
	
	@Autowired
	NetworkFileUpdateService networkFileUpdateService;
	
	@RequestMapping(value = "NetworkFileUpdate", method = RequestMethod.GET)
	public String getNetworkFileUpdate(Model model,NetworkFileUpdateBean networkbean)
	{

		model.addAttribute("NetworkFileUpdateBean",networkbean);
		return "NetworkFileUpdate";
	}
	
	@RequestMapping(value = "NetworkFileUpdate", method = RequestMethod.POST)
	@ResponseBody
	public String getNetworkFileUpdation(@RequestParam("category")String category,@RequestParam("filedate")String filedate,@RequestParam("subCat")String subCat
			,@RequestParam("fileselected")String fileselected,HttpSession httpsession)
	{
		System.out.println("Hellow");
		try
		{
			NetworkFileUpdateBean networkFileBean = new NetworkFileUpdateBean();
			networkFileBean.setCategory(category);
			networkFileBean.setStsubCategory(subCat);
			networkFileBean.setDatepicker(filedate);
			networkFileBean.setStFileName(fileselected);
			
			
			networkFileBean.setStEntryBy(((LoginBean) httpsession.getAttribute("loginBean")).getUser_id());
			//1. CHECK WHETHER ENTRY IS ALREADY PRESENT
			boolean entry = networkFileUpdateService.checkUploadFlag(networkFileBean);
			//2. IF NOT THEN ADD IT ELSE THROW AND ERROR
			if(entry)
			{
				networkFileUpdateService.EntryForFile(networkFileBean);
				return "Entry made Successfully";
			}
			else
			{
				return "File Already uploaded for mentioned date";
			}
		}
		catch(Exception e)
		{
			return "Exception Occured";
		}
		
	
	}
	@RequestMapping(value = "DeleteNetworkFileEntry", method = RequestMethod.POST)
	@ResponseBody
	public String DeleteNetworkFileEntry(@RequestParam("category")String category,@RequestParam("filedate")String filedate,@RequestParam("subCat")String subCat
			,@RequestParam("fileselected")String fileselected,HttpSession httpsession)
	{
		System.out.println("DeleteNetworkFileEntry");
		try
		{
			NetworkFileUpdateBean networkFileBean = new NetworkFileUpdateBean();
			networkFileBean.setCategory(category);
			networkFileBean.setStsubCategory(subCat);
			networkFileBean.setDatepicker(filedate);
			networkFileBean.setStFileName(fileselected);
			
			
			networkFileBean.setStEntryBy(((LoginBean) httpsession.getAttribute("loginBean")).getUser_id());
			//1. CHECK WHETHER ENTRY IS ALREADY PRESENT
			boolean entry = networkFileUpdateService.checkProcessFlag(networkFileBean);
			//2. IF NOT THEN ADD IT ELSE THROW AND ERROR
			if(entry)
			{
			//	networkFileUpdateService.EntryForFile(networkFileBean);
				networkFileUpdateService.DeleteData(networkFileBean);
				return "Data Deleted Successfully";
			}
			else
			{
				return "File is either Processed or there is no entry of the file for mentioned date";
			}
		}
		catch(Exception e)
		{
			System.out.println("eXCEPTION IS "+e);
			return "Exception Occurred";
		}
		
	
	}
}
