package com.recon.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.recon.model.SettlementBean;
import com.recon.util.GLBalanceBean;


public interface IglBalanceService {

	public String getGLBalance(float closing_balance, float cash_dispense,String filedate,String category,String subcategory, String[] arrsetdt, String[] arritem_settlamnt);
	


	public void generate_Reports(SettlementBean settlementBean,HttpServletResponse response, String closing_balance, String cash_dispense, String filedate, String subCat, String difference);
	

	public void DeleteFiles(String string);



	public String checkdispense(String filedate, String subcat,String [] arrsetdt, String category);
	
	public List<GLBalanceBean> prevdispense(String filedate, String subcat, String category);



	public void generate_VISAReports(SettlementBean settlementBean,
			HttpServletResponse response, String closing_balance,
			String cash_dispense, String filedate, String subCat,
			String difference);
}
