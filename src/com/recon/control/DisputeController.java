package com.recon.control;

import java.sql.SQLException;
import java.util.HashMap;




import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.recon.model.CompareSetupBean;
import com.recon.service.DisputeService;
import com.recon.util.FileDetailsJson;
import com.recon.util.JsonBean;
import com.recon.util.SearchData;
import com.recon.util.demo;



@Controller
public class DisputeController {
	
	@Autowired
	DisputeService disputeService;
	
	static Lock l = new ReentrantLock();
	int count = 0;
	
	private static final Logger logger = Logger.getLogger(DisputeController.class);
	
	@RequestMapping(value = "Dispute", method = RequestMethod.GET)
	public ModelAndView Configuration(ModelAndView modelAndView) {
		modelAndView.setViewName("DisputeData");
		
		return modelAndView;

	}
	
	
	@RequestMapping(value = "SearchDispute1", method = RequestMethod.POST)
	@ResponseBody
	public JsonBean SearchDispute1(JsonBean searchdata,String category,String stSubCategory,String file_name,String filedate, int jtStartIndex,
			int jtPageSize, HttpServletRequest request) {
		try {
			count = 0;
			HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			// SearchData searchdata = new SearchData(); 
			 searchdata.setCategory(category);
	         searchdata.setSubCategory(stSubCategory);
	         searchdata.setFilename(file_name);
	         //searchdata.setDcrs_remarks(dcrs_remarks);
	         searchdata.setFiledate(filedate);	         
	         
			Map<String, Object> viewMap = disputeService.returndata1(searchdata, jtStartIndex, jtPageSize);
			
			List<JsonBean> list = (List<JsonBean>) viewMap.get("search_list");
			int totalRecords = Integer.parseInt(String.valueOf(viewMap.get("search_count")));
			
			//List<JsonBean> list = disputeService.returndata1(searchdata,jtStartIndex, jtPageSize);
			//model.addAttribute("prolist", list);
			
			//List<SearchData> list = (List<SearchData>) viewMap.get("delivery_list");
		//	int totalRecords = list.size();
			
			/*JSONROOT.put("Records1", list);
			String jsonArray = gson.toJson(JSONROOT);
			System.out.println(jsonArray);*/
			searchdata.setParams("OK", list, totalRecords);

		} catch (Exception e) {
			e.printStackTrace();
			//searchdata.setParams("ERROR", e.getMessage());
		}
		return searchdata;
	}

	
	@RequestMapping(value = "UpdateDispute1", method = RequestMethod.POST)
/*	public JsonBean UpdateDispute1(@RequestBody JsonBean searchdata, HttpServletRequest request) {*/
	@ResponseBody	
	public String UpdateDispute1(String category,String subCategory,String filename,String tran_date,String amount,
 			String card_No,String filedate,String approvalCode,String arn,String termid,String trace,String local_time,
 			String foracid,String uniqIdentifier) {
		JsonBean searchdata=new JsonBean();
		JSONObject objJSON = new JSONObject();
		try {
			searchdata.setCategory(category);
			searchdata.setSubCategory(subCategory);
			searchdata.setFilename(filename);
			searchdata.setFilename(filename);
			searchdata.setTran_date(tran_date);
			searchdata.setAmount(amount);
			searchdata.setCard_No(card_No);
			searchdata.setFiledate(filedate);
			searchdata.setApprovalCode(approvalCode);
			searchdata.setArn(arn);
			searchdata.setTermid(termid);
			searchdata.setTrace(trace);
			searchdata.setLocal_time(local_time);
			searchdata.setForacid(foracid);
			searchdata.setUniqIdentifier(uniqIdentifier);
			
			//HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
			//Gson gson = new GsonBuilder().setPrettyPrinting().create();
			l.lock();
			boolean value = disputeService.updateDispute(searchdata);
			System.out.println(value);
			l.unlock();
			if(value){
				count++;
				//searchdata.setParams("OK", "Record Updated");
			}
			System.out.println("Count == "+count);
			objJSON.put("count",count);
	} catch (Exception e) {
		e.printStackTrace();
		searchdata.setParams("ERROR", e.getMessage());
	}
	return objJSON.toString();
}
	
	
	 @RequestMapping(value = "/SearchDispute", method = RequestMethod.POST)
	 @ResponseBody
	 public  String SearchDispute (@RequestParam("category")String category,@RequestParam("stSubCategory")String stSubCategory,
			 @RequestParam("file_name")String file_name,@RequestParam("filedate")String filedate, 
			 FileDetailsJson dataJson){
		 
		 logger.info("***** DisputeController.SearchDispute Start ****");
			
		 JSONObject objJSON = new JSONObject();
		 try{
		 
			 
			 SearchData searchdata = new SearchData(); 
			 
			 logger.info("in GetHeaderList"+category);
			 HashMap<String, Object> JSONROOT = new HashMap<String, Object>();
	         Gson gson = new GsonBuilder().setPrettyPrinting().create();
			 
	         searchdata.setCategory(category);
	         searchdata.setSubCategory(stSubCategory);
	         searchdata.setFilename(file_name);
	         //searchdata.setDcrs_remarks(dcrs_remarks);
	         searchdata.setFiledate(filedate);
	         
	         searchdata =  disputeService.returndata(searchdata);
	         
			 objJSON.put("excelheaders", searchdata.getExcelHeaders());			 
			 objJSON.put("data", searchdata.getData());
			 objJSON.put("matchHeadList", searchdata.getMatchHeadList());
			 
			  
			 logger.info("***** DisputeController.SearchDispute End ****");
			 
		 }catch(Exception e){
			 
			 logger.error(" error in DisputeController.SearchDispute", new Exception("DisputeController.SearchDispute",e));
			 dataJson.setParams("ERROR", e.getMessage());
			 
		 }
			 
			return objJSON.toString();
	 }
	 
	 @RequestMapping(value = "/updateForceMatchstatus", method = RequestMethod.POST)
	 @ResponseBody
	 public  String updateForceMatchstatus (@RequestParam("category")String category,@RequestParam("stSubCategory")String stSubCategory,
			 @RequestParam("file_name")String file_name,@RequestParam("insert")String insert,@RequestParam("filedate")String filedate, 
			FileDetailsJson dataJson) throws Exception{
		 String msg="";
		 logger.info("***** DisputeController.updateForceMatchstatus Start ****");
		 try{
			 
			 logger.info("category=="+category);
			 logger.info("stSubCategory=="+stSubCategory);
			 logger.info("file_name=="+file_name);
			 logger.info("insert=="+insert);
			 logger.info("filedate=="+filedate);
			// logger.info("dcrs_remarks=="+dcrs_remarks);
			 
		 
			 if(disputeService.updateForceMatchData(category,stSubCategory,file_name,filedate,insert)){
				 msg="Force Match has been successfully done.";
				 logger.info("msg=="+msg);
			 }else{
				 msg="Force Match not done properly.";
				 logger.info("msg=="+msg);
			 }
			 
		 logger.info("***** DisputeController.updateForceMatchstatus End ****");
		 
	 }catch(Exception e){
		 demo.logSQLException(e, "DisputeController.updateForceMatchstatus");
		 logger.error(" error in DisputeController.updateForceMatchstatus", new Exception("DisputeController.updateForceMatchstatus",e));
		 dataJson.setParams("ERROR", e.getMessage());		
		return "Exception";
		 
	 }
		 
		return msg;
 }
	 
	 
	 
	 @RequestMapping(value = "/getDcrsRemarks", method = RequestMethod.POST)
	 @ResponseBody
	 public  String getDcrsRemarks (@RequestParam("category")String category,@RequestParam("stSubCategory")String stSubCategory,
			 @RequestParam("file_name")String file_name){
		 
		 
				return file_name;
		 
		 
	 }

}
