package com.recon.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.recon.model.CompareSetupBean;
import com.recon.service.ISearchService;
import com.recon.util.FileDetailsJson;
import com.recon.util.SearchData;

@Controller
public class SearchDataController {
	
	@Autowired 
	ISearchService isearchService;
	
	private static final Logger logger = Logger.getLogger(ReconProcess.class);
	
	@RequestMapping(value = "searchData", method = RequestMethod.GET)
	public ModelAndView seeRule(ModelAndView modelAndView) {
		modelAndView.setViewName("SearchData");
		return modelAndView;

	}
	 @RequestMapping(value = "/searchData", method = RequestMethod.POST)
	 @ResponseBody
	 public  String GetHeaderList (@RequestParam("category")String category,String stSubCategory,String card_No,String tran_date,
			 String amount, String dcrs_remarks ,String filename,String filedate,FileDetailsJson dataJson){
		 logger.info("***** ReconProcess.GetHeaderList Start ****");
		
		 JSONObject objJSON = new JSONObject();
		 try{
		 
			 
			 SearchData searchdata = new SearchData(); 
			 searchdata.setCard_No(card_No);
			 searchdata.setAmount(amount);
			 searchdata.setSubCategory(stSubCategory);
			 searchdata.setTran_date(tran_date);
			 searchdata.setFilename(filename);
			 searchdata.setDcrs_remarks(dcrs_remarks);
			 searchdata.setCategory(category);
			 searchdata.setFiledate(filedate);
			 
			 System.out.println( searchdata.getCard_No()+
			 searchdata.getAmount()+
			 searchdata.getSubCategory()+
			 searchdata.getTran_date());
			 
			 
			 logger.info("in GetHeaderList"+category);
			 HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
	         Gson gson = new GsonBuilder().setPrettyPrinting().create();
			 
			 searchdata =  isearchService.returndata(searchdata);
			 objJSON.put("excelheaders", searchdata.getExcelHeaders());
			 
			 objJSON.put("data", searchdata.getData());
			
			 
			  
			 logger.info("***** SEARCHDATA End ****");
			 
		 }catch(Exception e){
			 
			 logger.error(" error in SEARCHDATA", new Exception("SEARCHDATA",e));
			 dataJson.setParams("ERROR", e.getMessage());
			 
		 }
			 
			return objJSON.toString();
			
		 
	 }
	 
	 @RequestMapping(value = "/checkttum", method = RequestMethod.POST)
	 @ResponseBody
	 public  ResponseEntity checkTTUM (@RequestParam("category")String category,String stSubCategory,String card_No,String tran_date,
			 String amount, String dcrs_remarks ,String filename,String filedate,FileDetailsJson dataJson){
		 logger.info("***** ReconProcess.GetHeaderList Start ****");
		
	
		 try{
		 
			 List<String> ttumId = new ArrayList<String>(); 
			 
			 SearchData searchdata = new SearchData(); 
			 searchdata.setCard_No(card_No);
			 searchdata.setAmount(amount);
			 searchdata.setSubCategory(stSubCategory);
			 searchdata.setTran_date(tran_date);
			 searchdata.setFilename(filename);
			 searchdata.setDcrs_remarks(dcrs_remarks);
			 searchdata.setCategory(category);
			 searchdata.setFiledate(filedate);
			 
			 System.out.println( searchdata.getCard_No()+
			 searchdata.getAmount()+
			 searchdata.getSubCategory()+
			 searchdata.getTran_date());
			 
			 
			  ttumId = isearchService.getttumdetails(searchdata);
			 
			 if(ttumId!=null) {
				
				 return new ResponseEntity(ttumId, HttpStatus.OK); 
			 
			 }else {
				 
				 return new ResponseEntity("TTUM NOT GENERATED", HttpStatus.OK); 
			 }
		
			
			 
			   
		 }catch(Exception e){
			 
			 logger.error(" error in SEARCHDATA", new Exception("SEARCHDATA",e));
			 dataJson.setParams("ERROR", e.getMessage());
			 return new ResponseEntity("ERROR OCCURED WHILE FETCHING DETAILS", HttpStatus.OK);
			 
		 }
			 
		
			
		 
	 }

}
