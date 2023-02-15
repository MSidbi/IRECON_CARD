package com.recon.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.recon.model.NFSFeeGstBean;
import com.recon.model.NFSSuspectTxnBean;

public interface NfSFeeGstService {
	public String nfsFeeGstReportDownload(NFSFeeGstBean beanObj);
	public List<Object> getnfsFeeGstData(NFSFeeGstBean beanObj,String reportType);
	public String processData(NFSFeeGstBean beanObj);
	public void generateExcelTTUM(String stPath, String FileName,List<Object> ExcelData,HttpServletResponse response );
	public String checkIfSuspectTxnProcess(String network,String date);
	 public List<Object> getnfsSuspextTxnData(NFSSuspectTxnBean NFSSuspectTxnBean);
	 public String processNFSSuspectTxn(String network, String date);
	 
	 HashMap<String, Object> validateNFSSettProcess(String network, String fromDate, String toDate);
}
