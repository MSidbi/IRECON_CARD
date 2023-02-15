package com.recon.service;

import java.util.HashMap;
import java.util.List;

import com.recon.model.Act4Bean;
import com.recon.model.FisdomACT4Detail;
import com.recon.model.VisaUploadBean;

public interface FisdomAct4Service {
	
	List<String>  getGlAccount(String category, String subcategory);
	
	boolean runAct4Report(Act4Bean beanObj);
	
	List<FisdomACT4Detail> getACT4CreditData(Act4Bean beanObj);
	
	List<FisdomACT4Detail> getACT4DebitData(Act4Bean beanObj);
	
	String getTotal_credit(String filedt);
	
	HashMap<String,Object> checkAct4Process(Act4Bean beanObj);
	
	List<Object> getFisdomAct4Data(String description,String action, String fileDate);
	List<Object> getNfsAct4Data(String description,String action, String fileDate);
	
	List<Object> getAct4Data(String category, String fileDate, String subcategory);
	
	List<Object> getAct4MatchedData(Act4Bean beanObj);
	
	//List<Object> getAct4CreditDebitData(Act4Bean beanObj);

}
