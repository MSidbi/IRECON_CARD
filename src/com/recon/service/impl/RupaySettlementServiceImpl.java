package com.recon.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.recon.dao.impl.RupaySettelementDaoImpl;
import com.recon.model.RupaySettlementBean;
import com.recon.model.RupayUploadBean;
import com.recon.service.RupaySettlementService;

@Service
public class RupaySettlementServiceImpl implements RupaySettlementService  {

@Autowired	
RupaySettelementDaoImpl	rupayDao;

private static final Logger logger = Logger.getLogger(RupaySettlementServiceImpl.class);

	@Override
	public HashMap<String, Object> uploadExcelFile(RupaySettlementBean beanObj,MultipartFile file) throws Exception {
		HashMap<String, Object> mapObj = new HashMap<String, Object>();
		try {
		Path tempDir=Files.createTempDirectory("");
		File tempFile=tempDir.resolve(file.getOriginalFilename()).toFile();
		file.transferTo(tempFile);
		Workbook workbook=WorkbookFactory.create(tempFile);
		Sheet sheet=workbook.getSheetAt(0);
		String fileName=file.getOriginalFilename();
		String extention=FilenameUtils.getExtension(fileName);
		FormulaEvaluator objFormulaEvaluator=null;
		if(extention.equals("xls")) {
			objFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
		}else if(extention.equals("xlsm") || extention.equals("xlsx")) {
			
			objFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
		}else {
			objFormulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
		}
		int g = sheet.getPhysicalNumberOfRows();
		List<Row> rowlist = new ArrayList<>();
		String target="";
		if (g == 0) {
			target = "no record";
		}
		
		for (int y = 0; y <= sheet.getLastRowNum(); y++) {
			Row xlsxRow1 = sheet.getRow(y);
			rowlist.add(xlsxRow1);
		}
		String tempSettlementDate="";
		String settlementDate="";
		
		String tempBankName="";
		String bankName="";
		
		String tempMemberName="";
		String memberName="";
		
		String tempMemBankPid="";
		String memBankPid="";
		
		String bankType="";
		
		String tempDRCR="";
		String DRCR="";
		
		String sumCr="";
		String sumDr="";
		String netSum="";
		int count=0;
		int srNo = 1;
		RupaySettlementBean data=null;
		List<RupaySettlementBean> dataList=new ArrayList<RupaySettlementBean>();
		for (int i = 1; i < rowlist.size(); i++) {
			int j = 0;
			Row rw = rowlist.get(i);
			DataFormatter formatter = new DataFormatter();

			objFormulaEvaluator.evaluate(rw.getCell(j));
			tempSettlementDate = formatter.formatCellValue(rw.getCell(j), objFormulaEvaluator);
            if(!tempSettlementDate.equals("")) {
            	settlementDate=tempSettlementDate;
            }
			
			objFormulaEvaluator.evaluate(rw.getCell(j++));
			tempBankName = formatter.formatCellValue(rw.getCell(j++), objFormulaEvaluator);
			if(!tempBankName.equals("")) {
				bankName=tempBankName;
            }
				
			objFormulaEvaluator.evaluate(rw.getCell(j));
			tempMemberName = formatter.formatCellValue(rw.getCell(j), objFormulaEvaluator);
		    if(!tempMemberName.equals("")) {
		    	memberName=tempMemberName;
            }
			
		    objFormulaEvaluator.evaluate(rw.getCell(j++));
		    tempMemBankPid = formatter.formatCellValue(rw.getCell(j++), objFormulaEvaluator);
			 if(!(tempMemBankPid.equals("") || tempMemBankPid.equalsIgnoreCase("Total"))) {
				 memBankPid=tempMemBankPid;
	        }
			 
			 objFormulaEvaluator.evaluate(rw.getCell(j));
			 bankType = formatter.formatCellValue(rw.getCell(j), objFormulaEvaluator);
				
		     objFormulaEvaluator.evaluate(rw.getCell(j++));
		     tempDRCR = formatter.formatCellValue(rw.getCell(j++), objFormulaEvaluator);
			 if(!(tempDRCR.equals("") ||tempDRCR.equalsIgnoreCase("Total"))) {
				 DRCR=tempDRCR;
	         }	
				
			 objFormulaEvaluator.evaluate(rw.getCell(j));
			 sumCr = formatter.formatCellValue(rw.getCell(j), objFormulaEvaluator);
				
			 objFormulaEvaluator.evaluate(rw.getCell(j++));
			 sumDr = formatter.formatCellValue(rw.getCell(j++), objFormulaEvaluator);
				
			objFormulaEvaluator.evaluate(rw.getCell(j));
			netSum = formatter.formatCellValue(rw.getCell(j), objFormulaEvaluator);
               
				count++;
                
                if(count==4) {
                	count=0;
                	data=new RupaySettlementBean();
                	data.setSettlementDate(settlementDate);
                	data.setBankName(bankName);
                	data.setMemberName(memberName);
                	data.setMemberBankPid(memBankPid);
                	data.setDrcr(DRCR);
                	data.setSumCr(sumCr);
                	data.setSumDr(sumDr);
                	data.setNetSum(netSum);
                	data.setCycle(beanObj.getCycle());
                	data.setSrNo(srNo++);
                	dataList.add(data);
                }else if(bankName.equalsIgnoreCase("Total")) {
                	data=new RupaySettlementBean();
                	data.setSettlementDate(settlementDate);
                	data.setBankName(bankName);
                	data.setMemberName(memberName);
                	data.setMemberBankPid("TOTAL");
                	data.setDrcr(DRCR);
                	data.setSumCr(sumCr);
                	data.setSumDr(sumDr);
                	data.setNetSum(netSum);
                	data.setCycle(beanObj.getCycle());
                	data.setSrNo(srNo++);
                	dataList.add(data);
                }
                
		}
		String result=rupayDao.uploadRupaySettlementData(dataList,beanObj);
		if(result.equalsIgnoreCase("success")) {
		mapObj.put("result", true);
		rupayDao.updateFileSettlement(beanObj,dataList.size());
		}else {
			mapObj.put("entry", false);
		}
		
		//Supplier<Stream<Row>> rowStreamSupplier=uploadUtil.getRowStreamSupplier(sheet);
		
		//Row headerRow=rowStreamSupplier.get().findFirst().get();
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			mapObj.put("result", false);
			mapObj.put("msg", e.getMessage());
		}
		return mapObj;
	}

	@Override
	public HashMap<String, Object> validatePrevFileUpload(RupaySettlementBean beanObj) {

		HashMap<String, Object> validate = rupayDao.validatePrevFileUpload(beanObj);
	return validate;
	}

	@Override
	public void generateRupaySettlmentTTum(String settlementDate,HttpServletResponse response) {
		HashMap<String, List<RupaySettlementBean>> map=rupayDao.getTTUMData(settlementDate);
		Path tempDir;
		RupaySettlementBean bean=null;
		FileWriter fw=null;
		try {
			tempDir = Files.createTempDirectory("");
		String fileName="RupaySettlementTTUM_"+settlementDate+".txt";
		File tempFile=tempDir.resolve(fileName).toFile();
		tempFile.createNewFile();
		fw=new FileWriter(tempFile);
		PrintWriter out=new PrintWriter(fw);
		List<RupaySettlementBean> datListWithTotal=map.get("datListWithTotal");
		List<RupaySettlementBean> datListWithoutTotal=map.get("datListWithoutTotal");
		
		for(int i=0;i<datListWithTotal.size();i++) {
			bean=datListWithTotal.get(i);
			String settlmentDate=bean.getSettlementDate();
			String[] arr=settlmentDate.split("-");
			String formattedSettDate=" "+arr[0]+" "+arr[1]+" "+arr[2];
			String tmpDrcr=bean.getDrcr();
			String k=bean.getNetSum();
			
			String fomattedNetSum=formatNetAmount(k);
			if(bean.getMemberBankPid().equals("TOTAL")) {
				if(k.contains("-")) {
					fomattedNetSum="-"+fomattedNetSum;
				}
			}
			String drOrCr=tmpDrcr.substring(0,1);
			String identifier="_"+bean.getCycle();
			Formatter creditLine = new Formatter();
			
            creditLine.format("%-16s", bean.getAccountNo());

            creditLine.format("%3s", "INR999");
            creditLine.format("%5s", "");  
            creditLine.format("%1s", drOrCr);
            creditLine.format("%16s", fomattedNetSum);
            creditLine.format("%14s", "RuPay sett for");
            creditLine.format("%7s", formattedSettDate);
            creditLine.format("%2s", identifier);
            creditLine.format("%63s", "");
            creditLine.format("%20s", fomattedNetSum);
            creditLine.format("%3s", "INR");
            creditLine.format("%127s", "");
            creditLine.format("%1s", "@");      

            creditLine.format(System.lineSeparator());

            fw.write(creditLine.toString());

            creditLine.close();

		}
		
		for(int i=0;i<datListWithoutTotal.size();i++) {
			bean=datListWithoutTotal.get(i);
			String settlmentDate=bean.getSettlementDate();
			String[] arr=settlmentDate.split("-");
			String formattedSettDate=" "+arr[0]+" "+arr[1]+" "+arr[2];
			String tmpDrcr=bean.getDrcr();
			//String format =String.format("%0%d", "16");
			//String res=String.format(format, bean.getAccountNo());
			String k=bean.getNetSum();
			
			String fomattedNetSum=formatNetAmount(k);
			if(bean.getMemberBankPid().equals("TOTAL")) {
				if(k.contains("-")) {
					fomattedNetSum="-"+fomattedNetSum;
				}
			}
			
				/*
				 * char[] zeroes=new char[16]; Arrays.fill(zeroes, '0'); DecimalFormat df=new
				 * DecimalFormat(String.valueOf(zeroes)); String fomattedNetSum=df.format(d);
				 */
			
			String drOrCr=tmpDrcr.substring(0,1);
			String identifier="_"+bean.getCycle();
			Formatter creditLine = new Formatter();
			
            creditLine.format("%-16s", bean.getAccountNo());

            creditLine.format("%3s", "INR999");
            creditLine.format("%5s", "");  
            creditLine.format("%1s", drOrCr);
            creditLine.format("%16s", fomattedNetSum);
            creditLine.format("%14s", "RuPay sett for");
            creditLine.format("%7s", formattedSettDate);
            creditLine.format("%2s", identifier);
            creditLine.format("%63s", "");
            creditLine.format("%20s", fomattedNetSum);
            creditLine.format("%3s", "INR");
            creditLine.format("%127s", "");
            creditLine.format("%1s", "@");      

            creditLine.format(System.lineSeparator());

            fw.write(creditLine.toString());

            creditLine.close();

            
			
				/*
				 * out.println(bean.getAccountNo()+"INR999"+"      "+drOrCr+bean.getNetSum()
				 * +"RuPay sett for"+" "+arr[0]+" "+arr[1]+" "+arr[2]+"            " +
				 * "                  "+bean.getNetSum()+"INR");
				 */
		}
		try {
			fw.flush();
			 fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		InputStream  inputStream=new BufferedInputStream(new FileInputStream(tempFile));
		String mimeType=URLConnection.guessContentTypeFromStream(inputStream);
		if(mimeType==null) {
			mimeType="application/octet-stream";
		}
		response.setContentType(mimeType);
		response.setContentLength((int)tempFile.length());
		response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s\"" ,tempFile.getName()));
		FileCopyUtils.copy(inputStream, response.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

public static String formatNetAmount(String netAmount) {
	String orignal=netAmount;
	String newOne="";
    String tmp="";
	if(orignal.contains(".")) {
		if(orignal.contains("-")) {
			tmp=orignal.replace("-", "");
		}else {
			tmp=orignal;
		}
		String[] arr=tmp.split("\\.");
		String format=String.format("%014d", Integer.parseInt(arr[0]));
		
		newOne=format+"."+arr[1];
		
	}else {
		String format=String.format("%014d", Integer.parseInt(orignal));
		newOne=format;
		System.out.println("newOne   "+newOne);
	}
	return newOne;
}

/********************** ADDED BY INT8624 FOR RUPAY FILE UPLOAD ****************************/
public boolean readFile(RupayUploadBean beanObj,MultipartFile file)
{
	HashMap<String, Object> output = null;
	boolean flag = false;
	try
	{
		logger.info("File name is "+beanObj.getFileName());
		if(beanObj.getFileName()!= null && beanObj.getFileName().equalsIgnoreCase("CHARGEBACK"))
		{
			logger.info("Chargeback File name");
			flag = rupayDao.readRupayChargeback(beanObj, file);
			logger.info("ReadFlag "+flag);
		}
		else
		{
			logger.info("other File name");
			output = rupayDao.readRupayFiles(beanObj, file);
		}
		
		
		if(output != null)
		{
			flag = (Boolean)output.get("result");
		}
		
		return flag;
	}
	catch(Exception e)
	{
		logger.info("Exception in ReadFile "+e);
		return false;
	}
//	return readFlag;
}

public boolean readIntFile(RupayUploadBean beanObj,MultipartFile file)
{
	HashMap<String, Object> output = null;
	boolean flag = false;
	try
	{
		logger.info("File name is "+beanObj.getFileName());
		if(beanObj.getFileName()!= null && beanObj.getFileName().equalsIgnoreCase("CHARGEBACK"))
		{
			logger.info("Chargeback File name");
			flag = rupayDao.readRupayChargeback(beanObj, file);
			logger.info("ReadFlag "+flag);
		}
		else
		{
			logger.info("other File name");
			output = rupayDao.readRupayIntFiles(beanObj, file);
		}
		
		
		if(output != null)
		{
			flag = (Boolean)output.get("result");
		}
		
		return flag;
	}
	catch(Exception e)
	{
		logger.info("Exception in ReadFile "+e);
		return false;
	}
//	return readFlag;
}


public boolean checkFileUploaded(RupayUploadBean beanObj)
{
	return rupayDao.checkFileUploaded(beanObj);
	
}

public HashMap<String , Object> validateRawfiles(RupayUploadBean beanObj)
{
	HashMap<String , Object> output = rupayDao.validateRawfiles(beanObj);
	
	return output;
}

public HashMap<String , Object> validateSettlementFiles(RupayUploadBean beanObj){
	
	HashMap<String , Object> output = rupayDao.validateSettlementFiles(beanObj);
	
	return output;
}

public boolean processSettlement(RupayUploadBean beanObj)
{
	return rupayDao.processSettlement(beanObj);
}
/***************** RUPAY SETTLEMENT REPORT DOWNLOAD CODE **********************/
public boolean validateSettlementProcess(RupayUploadBean beanObj)
{
	return rupayDao.validateSettlementProcess(beanObj);
}
public  List<Object> getSettlementData(RupayUploadBean beanObj)
{
	return rupayDao.getSettlementData(beanObj);
}

public Boolean validateSettlementTTUM(RupayUploadBean beanObj)
{
	return rupayDao.validateSettlementTTUM(beanObj);
}

public List<Object> getSettlementTTUMData(RupayUploadBean beanObj)
{
	return rupayDao.getSettlementTTUMData(beanObj);
}

public boolean processSettlementTTUM(RupayUploadBean beanObj)
{
	return rupayDao.processSettlementTTUM(beanObj);
}

public boolean validateSettlementDiff(RupayUploadBean beanObj)
{
	return rupayDao.validateSettlementDiff(beanObj);
}
public boolean processRectification(RupayUploadBean beanObj)
{
	return rupayDao.processRectification(beanObj);
}
public HashMap<String, Object> validateDiffAmount(RupayUploadBean beanObj)
{
	return rupayDao.validateDiffAmount(beanObj);
}
public boolean readNCMCFile(RupayUploadBean beanObj,MultipartFile file)
{
	HashMap<String, Object> output = null;
	boolean flag = false;
	try
	{
		logger.info("File name is "+beanObj.getFileName());
			logger.info("other File name");
			output = rupayDao.readNCMCFiles(beanObj, file);
		
		
		if(output != null)
		{
			flag = (Boolean)output.get("result");
		}
		
		return flag;
	}
	catch(Exception e)
	{
		logger.info("Exception in ReadFile "+e);
		return false;
	}
//	return readFlag;
}

@Override
public boolean checkNCMCFileUploaded(RupayUploadBean beanObj) {
	// TODO Auto-generated method stub
	return rupayDao.checkNCMCFileUploaded(beanObj);
}

@Override
public boolean settlementRollback(RupayUploadBean beanObj)
{
	return rupayDao.settlementRollback(beanObj);
}
}
