package com.recon.dao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.recon.model.SettlementBean;
import com.recon.util.GLBalanceBean;

public interface IGlBalanceDao {

		public String getGLBalance(float closingBal, float totalcashdisp,String filedate,String category,String subcategory, String[] arrsetdt, String[] arritem_settlamnt);

		public void generate_Reports(SettlementBean settlementBean,
				HttpServletResponse response, String closing_balance,
				String cash_dispense, String filedate, String subCat, String difference);

		public void DeleteFiles(String path);

		void generate_cashnet_acq_Reports(SettlementBean settlementBeanObj,
				HttpServletResponse response, String closing_balance,
				String cash_dispense, String filedate, String subCat,String difference);

		public String checkdispense(String filedate, String subcat,String [] arrsetdt, String category);

		public List<GLBalanceBean> prevdispense(String filedate, String subcat, String category);

		public void generate_visaReports(SettlementBean settlementBean,
				HttpServletResponse response, String closing_balance,
				String cash_dispense, String filedate, String subCat,
				String difference);
		
		
	
}
