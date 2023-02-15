package com.recon.service.impl;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.GenerateRupayTTUMDao;
import com.recon.model.GenerateTTUMBean;
import com.recon.service.GenerateRupayTTUMService;

@Component
public class GenerateRupayTTUMServiceImpl implements GenerateRupayTTUMService {

	@Autowired 
	GenerateRupayTTUMDao generateTTUMDao;
	
	@Override
	public void getTTUMSwitchRecords(GenerateTTUMBean generateTTUMBeanObj)throws Exception
	{
		generateTTUMDao.getTTUMSwitchRecords(generateTTUMBeanObj);
	}
	
	@Override
	public List<List<GenerateTTUMBean>> generateSwitchTTUM(GenerateTTUMBean generateTTUMBean,int inRec_Set_Id)throws Exception
	{
		return generateTTUMDao.generateSwitchTTUM(generateTTUMBean,inRec_Set_Id);
	}
	
	@Override
	public List<List<GenerateTTUMBean>> generateCBSTTUM(GenerateTTUMBean generateTTUMBeanObj,List<GenerateTTUMBean> Data)throws Exception
	{
		return generateTTUMDao.generateCBSTTUM(generateTTUMBeanObj,Data);
	}
	
	@Override
	public void TTUMRecords(GenerateTTUMBean generateTTUMBeanObj)throws Exception
	{
		generateTTUMDao.TTUMRecords(generateTTUMBeanObj);
	}
	
	@Override
	public void TTUM_forDPart(GenerateTTUMBean generatettumBeanObj)throws Exception
	{
		generateTTUMDao.TTUM_forDPart(generatettumBeanObj);
	}
	
	@Override
	public List<GenerateTTUMBean> getCandDdifference(GenerateTTUMBean generateTTUMBeanObj)throws Exception
	{
		return generateTTUMDao.getCandDdifference(generateTTUMBeanObj);		
	}
	
	@Override
	public List<List<GenerateTTUMBean>> getMatchedRecordsTTUM(GenerateTTUMBean generateTTUMbean)throws Exception
	{
		return generateTTUMDao.getMatchedRecordsTTUM(generateTTUMbean);
	}
	
	@Override
	public void getReportCRecords(GenerateTTUMBean generateTTUMBeanObj)throws Exception
	{
		generateTTUMDao.getReportCRecords(generateTTUMBeanObj);
	}
	
	@Override
	public List<List<GenerateTTUMBean>> GenerateRupayTTUM(GenerateTTUMBean generateTTUMBean,int inRec_Set_Id)throws Exception
	{
		return generateTTUMDao.GenerateRupayTTUM(generateTTUMBean, inRec_Set_Id);
	}
	
	@Override
	public void getRupayTTUMRecords(GenerateTTUMBean generateTTUMBeanObj)throws Exception
	{
		generateTTUMDao.getRupayTTUMRecords(generateTTUMBeanObj);
	}
	
	@Override
	public void getReportERecords(GenerateTTUMBean generatettumBeanObj)throws Exception
	{
		
		
		if(generateTTUMDao.IssurttumAlreadyGenrated(generatettumBeanObj) ){
			
			generateTTUMDao.cleanAlreadyProcessedSURTTUMRecords(generatettumBeanObj);
			
		}
		
		
		
		generateTTUMDao.getReportERecords(generatettumBeanObj);
		
		
	}
	
	@Override
	public void getSurchargeRecords(GenerateTTUMBean generateTTUMBeanObj)throws Exception
	{
		generateTTUMDao.getSurchargeRecords(generateTTUMBeanObj);
	}
	
	@Override
	public List<List<GenerateTTUMBean>> generateCBSSurchargeTTUM(GenerateTTUMBean generateTTUMBeanObj)throws Exception
	{
		return generateTTUMDao.generateCBSSurchargeTTUM(generateTTUMBeanObj);
	}
	
	@Override
	public List<List<GenerateTTUMBean>> LevyCharges(List<List<GenerateTTUMBean>> Data,GenerateTTUMBean generatettumBeanObj)throws Exception
	{
		return generateTTUMDao.LevyCharges(Data, generatettumBeanObj);
	}
	
	@Override
	public List<List<GenerateTTUMBean>> getMatchedIntTxn(GenerateTTUMBean generateTTUMBeanObj,int inRec_Set_Id)throws Exception
	{
		return generateTTUMDao.getMatchedIntTxn(generateTTUMBeanObj, inRec_Set_Id);
	}
	
	@Override
	public String getLatestFileDate(GenerateTTUMBean generateTTUMBean)
	{
		return generateTTUMDao.getLatestFileDate(generateTTUMBean);
	}
	public void generateTTUM(List<List<GenerateTTUMBean>> generatettum_list)throws Exception
	{
		
         
       
    	List<GenerateTTUMBean> ttum_data =  generatettum_list.get(1);
    	Date date = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmm");
		
		String strDate = sdf.format(date);
		String stfileName = "";
		 if(!generatettum_list.get(0).get(0).getStDate().equals(""))
			{
			 	stfileName = generatettum_list.get(0).get(0).getStCategory()+"_REV_"
						/*+generatettum_list.get(0).get(0).getStDate()+"_"*/+strDate;
				/*response.setHeader("Content-disposition", "attachment; filename="+generatettum_list.get(0).get(0).getStCategory()+"_REV_"
							+generatettum_list.get(0).get(0).getStDate()+"_"+strDate+".xls");*/
			}
			else
			{
				stfileName = generatettum_list.get(0).get(0).getStCategory()+"_REV_"
						/*+generatettum_list.get(0).get(0).getStDate()+"_"*/+strDate;
				/*response.setHeader("Content-disposition", "attachment; filename="+generatettum_list.get(0).get(0).getStCategory()+"_REV_"
						+generatettum_list.get(0).get(0).getStDate()+"_"+strDate+".xls");*/
			}
		 
		 FileOutputStream fos = new FileOutputStream("D:\\"+stfileName+".txt");
		 Formatter creditLine = new Formatter();
		 Formatter DebitLine = new Formatter();
	         DecimalFormat f = new DecimalFormat("##.00");
	        Iterator<GenerateTTUMBean> itr = ttum_data.iterator();
	        while(itr.hasNext())
	        {
	        	GenerateTTUMBean beanObj = itr.next();
	        	
	        	DebitLine.format("%-16s", beanObj.getStDebitAcc());
	        	DebitLine.format("%-3s", "INR");
	        	DebitLine.format("%-8s", "098");
	        	DebitLine.format("%-1s", "D");
	        	DebitLine.format("%17s",f.format(Double.parseDouble(beanObj.getStAmount())));
	        	if(beanObj.getStTran_particulars().length()>30)
	        		DebitLine.format("%-30s", beanObj.getStTran_particulars().substring(0, 30));
	            else
	            	DebitLine.format("%-30s", beanObj.getStTran_particulars());
	        	DebitLine.format("%-5s", "");
	        	DebitLine.format("%-20s", beanObj.getStCard_Number());
	        	DebitLine.format("%-38s", " ");
	        	DebitLine.format("%17s", f.format(Double.parseDouble(beanObj.getStAmount())));
	        	DebitLine.format("%-3s", "INR");
	        	DebitLine.format("%-412s","" );
	        	//DebitLine.format("%-30s", beanObj.getStRemark());
	        	DebitLine.format("%-30s","CHRG");
	        	DebitLine.format("%-338s", "");
	        	DebitLine.format(System.getProperty("line.separator"));
	        	
	        	 
	        	
	        	
	     //   creditLine.format("%-17s", creditAccNoString);
	        creditLine.format("%-16s", beanObj.getStCreditAcc());
            creditLine.format("%-3s", "INR");
            creditLine.format("%-8s", "098");
            creditLine.format("%-1s", "C");
            creditLine.format("%17s",f.format(Double.parseDouble(beanObj.getStAmount())));
            //System.out.println("beanObj.getStTran_particulars() "+beanObj.getStTran_particulars());
            if(beanObj.getStTran_particulars().length()>30)
            	creditLine.format("%-30s", beanObj.getStTran_particulars().substring(0, 30));
            else
            	creditLine.format("%-30s", beanObj.getStTran_particulars());
            creditLine.format("%-5s", "");
            creditLine.format("%-20s", beanObj.getStCard_Number());
            creditLine.format("%-38s", " ");
            creditLine.format("%17s", f.format(Double.parseDouble(beanObj.getStAmount())));
            creditLine.format("%-3s", "INR");
            creditLine.format("%-128s", "");
            creditLine.format("%-1s", "@");
            creditLine.format("%-283s", "");
            //creditLine.format("%-412s","" );
            creditLine.format("%-30s", beanObj.getStRemark());
            creditLine.format("%-338s", "");
            
        //    creditLine.format("%14s", f.format(sheet.getRow(j).getCell(1).getNumericCellValue()));
        //    creditLine.format(ttumbean.getComment()+" ");
//            creditLine.format("%6s", (int) sheet.getRow(j).getCell(0).getNumericCellValue());
//            creditLine.format("%-1s", "-");
//            creditLine.format(empName);
//            creditLine.format("%-66s", costCd);
//            creditLine.format("%14s",f.format(sheet.getRow(j).getCell(1).getNumericCellValue()));
//            creditLine.format("%-130s", "INR");
//            creditLine.format("%-632s", "@");
//            creditLine.format("%1s", " ");
            creditLine.format(System.getProperty("line.separator"));

            
//            System.out.println(creditLine);
          

	        }
	        fos.write(DebitLine.toString().getBytes());
	        fos.write(creditLine.toString().getBytes());
	        creditLine.close();
	        DebitLine.close();
		
	}

	@Override
	public void getFailedCBSRecords(GenerateTTUMBean generateTTUMBeanObj) throws Exception
	{
		generateTTUMDao.getFailedCBSRecords(generateTTUMBeanObj);
	}

	@Override
	public List<List<GenerateTTUMBean>> generateDisputeTTUM(
			GenerateTTUMBean ttumBean) throws Exception {
		return generateTTUMDao.generateDisputeTTUM(ttumBean);
	}

	@Override
	public List<List<GenerateTTUMBean>> generateVisaDisputeTTUM(
			GenerateTTUMBean ttumBean) throws Exception {
		// TODO Auto-generated method stub
		return generateTTUMDao.generateVisaDisputeTTUM(ttumBean);
	}
}
