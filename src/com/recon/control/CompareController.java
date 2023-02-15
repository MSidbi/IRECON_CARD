package com.recon.control;

import static com.recon.util.GeneralUtil.ERROR_MSG;
import static com.recon.util.GeneralUtil.SUCCESS_MSG;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.recon.model.CompareBean;
import com.recon.model.GenerateTTUMBeanold;
import com.recon.model.LoginBean;
import com.recon.service.CompareService;
import com.recon.service.GenerateTTUMService;

@Controller
public class CompareController {

	@Autowired
	CompareService compareService;
	
	
	@RequestMapping(value="compareData", method=RequestMethod.GET)
	public String getComparePage(Model model, CompareBean compareBean)
	{
		model.addAttribute("CompareBean",compareBean);
		return "CompareData";
	}
	
	@RequestMapping(value="comapareData" ,method=RequestMethod.POST)
	public String compareData(@ModelAttribute("CompareBean") CompareBean compareBean, RedirectAttributes redirectAttributes,HttpSession httpsession,Model model)
	{
		try
		{
			compareBean.setStEntryBy(((LoginBean) httpsession.getAttribute("loginBean")).getUser_id());
			System.out.println("file date is "+compareBean.getStFile_date());
			List<String> table_list = new ArrayList<>();
			table_list.add(compareBean.getStTable1());
			table_list.add(compareBean.getStTable2());
			int  i =1;// compareService.moveData(table_list,compareBean.getStFile_date());
			if(i==1)
			{
				// compare logic
				//compareService.updateMatchedRecords(table_list,compareBean.getStFile_date());
				//System.out.println("completed matching of records");
				//compareService.moveToRecon(table_list,compareBean.getStFile_date());
				///compareService.TTUMRecords(table_list,compareBean.getStFile_date());
				
			}
			
		//	redirectAttributes.addFlashAttribute(SUCCESS_MSG, "Filteration Completed Successfully.");
			return "CompareData";
		}
		catch(Exception e)
		{
			redirectAttributes.addFlashAttribute(ERROR_MSG, "Configuration already Exists.");
			return "CompareData";
		}
		
		
	}
	
}
