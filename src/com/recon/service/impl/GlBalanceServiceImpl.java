package com.recon.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.informix.util.stringUtil;
import com.recon.dao.IGlBalanceDao;
import com.recon.dao.impl.GlBalanceDaoImpl;
import com.recon.model.SettlementBean;
import com.recon.service.IglBalanceService;
import com.recon.util.GLBalanceBean;

@Component
public class GlBalanceServiceImpl implements IglBalanceService {

	@Autowired IGlBalanceDao glbalancedao;
	
	@Override
	public String getGLBalance(float closingBal, float totalcashdisp,String filedate,String category,String subcategory,String[] arrsetdt, String[] arritem_settlamnt) {
		
		return glbalancedao.getGLBalance(closingBal, totalcashdisp, filedate, category, subcategory,arrsetdt,arritem_settlamnt) ;
	}

	

	@Override
	public void DeleteFiles(String path) {
		glbalancedao.DeleteFiles(path);
		
	}



	@Override
	public void generate_Reports(SettlementBean settlementBean,
			HttpServletResponse response, String closing_balance,
			String cash_dispense, String filedate, String subCat,String difference) {
	
		if(subCat.equalsIgnoreCase("ISSUER"))
			{
			glbalancedao.generate_Reports(settlementBean, response, closing_balance, cash_dispense, filedate, subCat,difference);
			}
		else if(subCat.equalsIgnoreCase("ACQUIRER")) {
			
			glbalancedao.generate_cashnet_acq_Reports(settlementBean, response, closing_balance, cash_dispense, filedate, subCat,difference);
			
		}
			
	
		
	}



	@Override
	public String checkdispense(String filedate, String subcat,String [] arrsetdt,String category) {
		
		return glbalancedao.checkdispense( filedate,subcat,arrsetdt,category);
	}



	@Override
	public List<GLBalanceBean> prevdispense(String filedate, String subcat,String category) {
		// TODO Auto-generated method stub
		return glbalancedao.prevdispense( filedate,subcat,category);
	}



	@Override
	public void generate_VISAReports(SettlementBean settlementBean,
			HttpServletResponse response, String closing_balance,
			String cash_dispense, String filedate, String subCat,
			String difference) {
		glbalancedao.generate_visaReports(settlementBean, response, closing_balance, cash_dispense, filedate, subCat,difference);
		
	}
	

}
