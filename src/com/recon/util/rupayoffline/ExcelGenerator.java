package com.recon.util.rupayoffline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.recon.model.rupayoffline.XmlModel;

public class ExcelGenerator {

	
	
	private	SXSSFWorkbook book;
	private	SXSSFSheet sheet;
	private SXSSFRow row;
	
	ExcelGenerator( ){
		this.book = new SXSSFWorkbook(1000);
		this.sheet = this.book.createSheet("SHEET1");
		this.row = this.sheet.createRow(0);
	}
	
	void createHeader(int i){
		String head [] = { "mti","funcd","RECNUM","dttmflgen","meminstcd","unflnm","dtset","prodcd","setbin","flcatg","vernum","acqinstcd","actncd","amtset","amttxn","ard",
				"ccycdset","ccycdtxn","convrtset","crdacpbusscd","crdacpcity","crdacpctrycd","crdacpidcd","crdacploc","crdacpnm","crdacpstnm","crdacpttrmid","crdacpzipcd",
				"dttmlctxn","iccdata","mertelnum","pan","poscondcd","posdatacd","posentmode","proccd","servcd","setdcind","txndesinstcd","txnorginstcd","feedcind","feeamt","feeccy","feetpcd","intrchngctg"};
		
		for ( int headCell=0; headCell < head.length ; headCell++) {
			this.row.createCell(headCell).setCellValue(head[headCell]);
		}
	}
	
	void createRow(List<XmlModel> rawdata, int i){
 
		this.row = this.sheet.createRow(i);
		for(XmlModel bin : rawdata){
		this.row.createCell(0).setCellValue(  bin.getMti() );
		this.row.createCell(1).setCellValue(  bin.getFuncd() );
		this.row.createCell(2).setCellValue(  bin.getRecNum() );
		this.row.createCell(3).setCellValue(  bin.getDtTmFlGen() );
		this.row.createCell(4).setCellValue(  bin.getMemInstCd() );
		this.row.createCell(5).setCellValue(  bin.getUnFlNm() );
		this.row.createCell(6).setCellValue(  bin.getDtSet() );
		this.row.createCell(7).setCellValue(  bin.getProdCd() );
		this.row.createCell(8).setCellValue(  bin.getSetBIN() ); 
		this.row.createCell(9).setCellValue(  bin.getFlCatg() );
		this.row.createCell(10).setCellValue(  bin.getVerNum() );
		this.row.createCell(11).setCellValue(  bin.getAcqInstCd() );
		this.row.createCell(12).setCellValue(  bin.getActnCd() );
		this.row.createCell(13).setCellValue(  bin.getAmtSet() );
		this.row.createCell(14).setCellValue(  bin.getAmtTxn() );
		this.row.createCell(15).setCellValue(  bin.getARD() );
		this.row.createCell(16).setCellValue(  bin.getCcyCdSet() );
		this.row.createCell(17).setCellValue(  bin.getCcyCdTxn() );
		this.row.createCell(18).setCellValue(  bin.getConvRtSet() );
		this.row.createCell(19).setCellValue(  bin.getCrdAcpBussCd() );
		this.row.createCell(20).setCellValue(  bin.getCrdAcpCity() );
		this.row.createCell(21).setCellValue(  bin.getCrdAcpCtryCd() ); 
		this.row.createCell(22).setCellValue(  bin.getCrdAcpIDCd() );
		this.row.createCell(23).setCellValue(  bin.getCrdAcpLoc() );
		this.row.createCell(24).setCellValue(  bin.getCrdAcpNm() ); 
		this.row.createCell(25).setCellValue(  bin.getCrdAcpStNm() );
		this.row.createCell(26).setCellValue(  bin.getCrdAcptTrmId() );
		this.row.createCell(27).setCellValue(  bin.getCrdAcpZipCd() );
		this.row.createCell(28).setCellValue(  bin.getDtTmLcTxn() );
		this.row.createCell(29).setCellValue(  "null" ); //iccdata
		this.row.createCell(30).setCellValue(  bin.getMerTelNum() );
		this.row.createCell(31).setCellValue(  bin.getPAN() );
		this.row.createCell(32).setCellValue(  bin.getPosCondCd() );
		this.row.createCell(33).setCellValue(  bin.getPosDataCd());
		this.row.createCell(34).setCellValue(  bin.getPosEntMode() ); 
		this.row.createCell(35).setCellValue(  bin.getProcCd() );
		this.row.createCell(36).setCellValue(  bin.getServCd() );
		this.row.createCell(37).setCellValue(  bin.getSetDCInd() );
		this.row.createCell(38).setCellValue(  bin.getTxnDesInstCd() );
		this.row.createCell(39).setCellValue(  bin.getTxnOrgInstCd() );
		this.row.createCell(40).setCellValue(  bin.getFeeDCInd() );
		this.row.createCell(41).setCellValue(  bin.getFeeAmt() );
		this.row.createCell(42).setCellValue(  bin.getFeeCcy() );
		this.row.createCell(43).setCellValue(  bin.getFeeTpCd() );
		this.row.createCell(44).setCellValue(  bin.getNtrchngCtg() );
		}
	}
	
	void createCell(int key, String value){
		this.row.createCell(key).setCellValue(value);
	}
	
	void write(String path){
	OutputStream strm = null;
	try {
		 	strm = new FileOutputStream(new File(path));
			this.book.write(strm);
			this.book.close();
			strm.flush();
			strm.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
		System.out.println("adding xls file");

	}
	
	
}





