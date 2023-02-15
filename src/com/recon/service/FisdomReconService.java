package com.recon.service;

import java.util.HashMap;

import com.recon.model.FisdomFileUploadBean;

public interface FisdomReconService {
	
	HashMap<String, Object> checkFileUploaded(String fileDate);
	
	HashMap<String, Object> checkpreviousReconProcess(String fileDate);
	
	boolean runFisdomRecon(String fileDate, String entryBy);
	
	 HashMap<String, Object> reconAlreadyProcessed(String fileDate);
	 
	 boolean runFisdomTTUM(FisdomFileUploadBean beanObj);
	 
	 boolean validateTTUMProcessed(FisdomFileUploadBean beanObj);
	 
	 
	 boolean checkAndMakeDirectory(FisdomFileUploadBean beanObj);
	 
	 String createTTUMFile(FisdomFileUploadBean beanObj);
	 
	 boolean checkRecordsPresent(FisdomFileUploadBean beanObj);
	 
	 boolean checkTTUMAlreadyProcessed(FisdomFileUploadBean beanObj);

}
