package com.recon.util.rupayoffline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.recon.dao.rupayoffline.RupayOfflineUploadDao;
import com.recon.model.rupayoffline.XmlModel;

public class MyHandler extends DefaultHandler {

	//private String key;
	private String value;
	private X2J x2j = X2J.getInstance();
	private RupayOfflineUploadDao dao ;
	ExcelGenerator excel;
	int cell =0;	
	int row=0;
	
	public MyHandler(){
		this.excel = new  ExcelGenerator();
		this.excel.createHeader(row);
		row +=1;
		System.out.println("Header Created");
	}
 
	
	public void setRupayOfflineUploadDao(RupayOfflineUploadDao inject){
		this.dao = inject;
	}
	
	
	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		this.value = new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
	boolean allset =	x2j.getValue(qName, this.value);
	System.out.println(qName + " "+this.value);
	
	if(allset){
		try {
			System.out.println( x2j.model.size());
			dao.saveOfflineXml( x2j.model);
			excel.createRow(x2j.model, row);
			row +=1;
			cell =0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		x2j.model.clear();
	}
	}
	
	
	@Override
	public void endDocument() throws SAXException {
		x2j.model.clear();
		excel.write(FILEPATH.filepath);
	}

}

class X2J {
	
	Map<String ,String> data = new HashMap<String,String>();
	List<XmlModel> model = new ArrayList<XmlModel>();
	private static X2J x2j;
	private X2J(){}
	
	public static X2J getInstance(){
		
		if(x2j == null){
			x2j = new X2J();	
		}
		return x2j;
	}
	
	boolean getValue(String Key, String Value) {
		 
	 	if(!Key.trim().equalsIgnoreCase("") && !Value.trim().equalsIgnoreCase("") ){
	 		data.put(Key, Value);
	 	}
		
	 	if(Key.equalsIgnoreCase("Txn") && Value.trim().equalsIgnoreCase("") ){
	 		SetModel mode= new SetModel();
	 		model.add(mode.mapToBin(data))  	;	
	 		
	 		return true;
	 		
	 	}
		return false;
}
}



class SetModel {
	XmlModel mapToBin(Map<String ,String> data) {
	XmlModel model = new XmlModel();
	for(Map.Entry<String, String> cell : data.entrySet() ){
		
		switch (cell.getKey().toString().substring(1)  ) { 
		case "MTI" : 
			 model.setMti(cell.getValue())  ; 
			 break;
			case "FunCd" : 
			 model.setFuncd(cell.getValue())  ; 
			 break;
			case "RecNum" : 
			 model.setRecNum(cell.getValue())  ; 
			 break;
			case "DtTmFlGen" : 
			 model.setDtTmFlGen(cell.getValue())  ; 
			 break;
			case "MemInstCd" : 
			 model.setMemInstCd(cell.getValue())  ; 
			 break;
			case "UnFlNm" : 
			 model.setUnFlNm(cell.getValue())  ; 
			 break;
			case "DtSet" : 
			 model.setDtSet(cell.getValue())  ; 
			 break;
			case "ProdCd" : 
			 model.setProdCd(cell.getValue())  ; 
			 break;
			case "SetBIN" : 
			 model.setSetBIN(cell.getValue())  ; 
			 break;
			case "FlCatg" : 
			 model.setFlCatg(cell.getValue())  ; 
			 break;
			case "VerNum" : 
			 model.setVerNum(cell.getValue())  ; 
			 break;
			case "AcqInstCd" : 
			 model.setAcqInstCd(cell.getValue())  ; 
			 break;
			case "ActnCd" : 
			 model.setActnCd(cell.getValue())  ; 
			 break;
			case "AmtSet" : 
			 model.setAmtSet(cell.getValue())  ; 
			 break;
			case "AmtTxn" : 
			 model.setAmtTxn(cell.getValue())  ; 
			 break;
			case "ARD" : 
			 model.setARD(cell.getValue())  ; 
			 break;
			case "CcyCdSet" : 
			 model.setCcyCdSet(cell.getValue())  ; 
			 break;
			case "CcyCdTxn" : 
			 model.setCcyCdTxn(cell.getValue())  ; 
			 break;
			case "ConvRtSet" : 
			 model.setConvRtSet(cell.getValue())  ; 
			 break;
			case "CrdAcpBussCd" : 
			 model.setCrdAcpBussCd(cell.getValue())  ; 
			 break;
			case "CrdAcpCity" : 
			 model.setCrdAcpCity(cell.getValue()) ; 
			 break;
			case "CrdAcpCtryCd" : 
			 model.setCrdAcpCtryCd(cell.getValue())  ; 
			 break;
			case "CrdAcpIDCd" : 
			 model.setCrdAcpIDCd(cell.getValue())  ; 
			 break;
			case "CrdAcpLoc" : 
			 model.setCrdAcpLoc(cell.getValue())  ; 
			 break;
			case "CrdAcpNm" : 
			 model.setCrdAcpNm(cell.getValue())  ; 
			 break;
			case "CrdAcpStNm" : 
			 model.setCrdAcpStNm(cell.getValue())  ; 
			 break;
			case "CrdAcptTrmId" : 
			 model.setCrdAcptTrmId(cell.getValue())  ; 
			 break;
			case "CrdAcpZipCd" : 
			 model.setCrdAcpZipCd(cell.getValue())  ; 
			 break;
			case "DtTmLcTxn" : 
			 model.setDtTmLcTxn(cell.getValue())  ; 
			 break;
			case "ICCData" : 
			 model.setICCData(cell.getValue())  ; 
			 break;
			case "MerTelNum" : 
			 model .setMerTelNum(cell.getValue()) ; 
			 break;
			case "PAN" : 
			 model.setPAN(cell.getValue())  ; 
			 break;
			case "PosCondCd" : 
			 model.setPosCondCd(cell.getValue())  ; 
			 break;
			case "PosDataCd" : 
			 model.setPosDataCd(cell.getValue())  ; 
			 break;
			case "PosEntMode" : 
			 model.setPosEntMode(cell.getValue())  ; 
			 break;
			case "ProcCd" : 
			 model.setProcCd(cell.getValue())  ; 
			 break;
			case "ServCd" : 
			 model.setServCd(cell.getValue())  ; 
			 break;
			case "SetDCInd" : 
			 model.setSetDCInd(cell.getValue())  ; 
			 break;
			case "TxnDesInstCd" : 
			 model.setTxnDesInstCd(cell.getValue())  ; 
			 break;
			case "TxnOrgInstCd" : 
			 model.setTxnOrgInstCd(cell.getValue())  ; 
			 break;
			case "FeeDCInd" : 
			 model.setFeeDCInd(cell.getValue())  ; 
			 break;
			case "FeeAmt" : 
			 model.setFeeAmt(cell.getValue())  ; 
			 break;
			case "FeeCcy" : 
			 model.setFeeCcy(cell.getValue())  ; 
			 break;
			case "FeeTpCd" : 
			 model.setFeeTpCd(cell.getValue())  ; 
			 break;
			case "IntrchngCtg" : 
			 model.setNtrchngCtg(cell.getValue())  ; 
			 break;
		}
		
		
	}
		return model;
	}
	
	
	
	
 

}
