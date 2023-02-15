package com.recon.service.impl;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

//import sun.org.mozilla.javascript.internal.regexp.SubString;


import com.recon.util.OracleConn;
import com.recon.util.TransactionUtility;
import com.recon.service.ReadImpData;
import com.recon.model.Message;
import com.recon.model.Field;

public class ReadImpDataImpl extends Message implements ReadImpData{
	
	
	List<Integer> bitIntArr = new ArrayList<Integer>();
	
	 private static JSONArray Jcsv = new JSONArray(); ;
	 private static  ReadImpData  ReadImpData;
	 private ReadImpDataImpl(){}
	 
	 public static ReadImpData getInstance(){
		 
		 if(ReadImpData == null) ReadImpData = new  ReadImpDataImpl();
		 
		 return ReadImpData;
	 }
	 
	 
	 public static JSONArray getJcsv(){
		 return Jcsv;
	 }
	 

	@Override
	public void _read1251(ArrayList<String> line_data,String filename,String filedate) throws Exception {
	 
		OracleConn conn=new OracleConn();
		Connection con=conn.getconn();
		int count=0;
		 PreparedStatement ps=null;
		 String DE12=null;
		for (int xxx = 0; xxx < line_data.size() ; xxx++) { ///line_data.size()
			  
			count=count+1;
			int msg_len = (line_data.get(xxx).toString().length());
			 
			try {
				String Del1 = line_data.get(xxx).toString().substring(0, 4);	
				String Del2 =  line_data.get(xxx).toString().substring(4, 20)  ;
				ArrayList bits = TransactionUtility.toBinary_list(Del2.getBytes()); // Del2.getBytes()
																					// tempBuff.toString().getBytes()
				setDataString( line_data.get(xxx).toString().substring(20, msg_len));
				for (int bit = 1; bit < bits.size(); bit++) {
					if ((boolean) bits.get(bit).equals('1')) {
						bitIntArr.add(bit + 1);
					}
				}
				//System.out.println(bitIntArr);
				Fields.clear();
				if (checkBitMap(1)) {
					addField(new Field(1, "Bitmap secondary", 8, null, false, '-',
							true, "FIXED", null));
				}
				if (checkBitMap(2)) {
					addField(new Field(2, "PAN", 0, null, false, '-', true,
							"LLVAR", "PAN"));
				}
				if (checkBitMap(3)) {
					addField(new Field(3, "Processing code", 6, null, false, '-',
							true, "FIXED", null));
				}
				if (checkBitMap(4)) {
					addField(new Field(4, "Amount transaction", 12, null, false,
							'-', true, "FIXED", null));
				}
				if (checkBitMap(5)) {
					addField(new Field(5, "Amount-Reconciliation", 12, null, false,
							'-', true, "FIXED", null));
				}
				if (checkBitMap(6)) {
					addField(new Field(6, "Amount-Cardholder billing", 12, null,
							false, '-', true, "FIXED", null));
				}
				if (checkBitMap(9)) {
					addField(new Field(9, "Conversion rate-Reconciliation", 8,
							null, false, '-', true, "FIXED", null));
				}
				if (checkBitMap(10)) {
					addField(new Field(10, "Conversion rate-Cardholder billing", 8,
							null, false, '-', true, "FIXED", null));
				}
				if (checkBitMap(12)) {
					addField(new Field(12, "Date/Time local transaction", 12, null,
							false, '-', true, "FIXED", null));
				}

				if (checkBitMap(14)) {
					addField(new Field(14, " Expiration date", 4, null, false, '-',
							true, "FIXED", null));
				}
				if (checkBitMap(22)) {
					addField(new Field(22, "Point of service data code", 12, null,
							false, '-', true, "FIXED", null));
				}
				if (checkBitMap(23)) {
					addField(new Field(23, "Card sequence number", 3, null, false,
							'-', true, "FIXED", null));
				}
				if (checkBitMap(24)) {
					addField(new Field(24, "Function code", 3, null, false, '-',
							true, "FIXED", null));
				}

				if (checkBitMap(25)) {
					addField(new Field(25, "Message reason code", 4, null, false,
							'-', true, "FIXED", null));
				}

				if (checkBitMap(26)) {
					addField(new Field(26, "Card acceptor business code", 4, null,
							false, '-', true, "FIXED", null));
				}

				if (checkBitMap(30)) {
					addField(new Field(30, "Amounts-original", 24, null, false,
							'-', true, "FIXED", null));
				}

				if (checkBitMap(31)) {
					addField(new Field(31, "Acquirer reference data", 23, null,
							false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(32)) {
					addField(new Field(32, "Acquiring institution ID code", 0,
							null, false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(33)) {
					addField(new Field(33, "Forwarding institution ID code", 0,
							null, false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(37)) {
					addField(new Field(37, "Retrieval reference number", 12, null,
							false, '-', true, "FIXED", null));
				}

				if (checkBitMap(38)) {
					addField(new Field(38, "Approval code", 6, null, false, '-',
							true, "FIXED", null));
				}

				if (checkBitMap(40)) {
					addField(new Field(40, "Service code", 3, null, false, '-',
							true, "FIXED", null));
				}

				if (checkBitMap(41)) {
					addField(new Field(41, " Card acceptor terminal ID", 8, null,
							false, '-', true, "FIXED", null));
				}

				if (checkBitMap(42)) {
					addField(new Field(42, " Card acceptor Id", 15, null, false,
							'-', true, "FIXED", null));
				}

				if (checkBitMap(43)) {
					addField(new Field(43, "Card acceptor name/location", 0, null,
							false, '-', true, "LLVAR", "DE43"));
				}

				if (checkBitMap(48)) {
					addField(new Field(48, "Additional data", 0, null, false, '-',
							true, "LLLVAR", "PDS"));
				}

				if (checkBitMap(49)) {
					addField(new Field(49, "Currency code Transaction", 3, null,
							false, '-', true, "FIXED", null));
				}

				if (checkBitMap(50)) {
					addField(new Field(50, "Currency code- Reconciliation", 3,
							null, false, '-', true, "FIXED", null));
				}

				if (checkBitMap(51)) {
					addField(new Field(51, "Currency code- Cardholder billing", 3,
							null, false, '-', true, "FIXED", null));
				}

				if (checkBitMap(54)) {
					addField(new Field(54, "Amounts- additional", 0, null, false,
							'-', true, "LLLVAR", null));
				}

				if (checkBitMap(55)) {
					addField(new Field(55, "ICC system related data", 255, null,
							false, '-', true, "LLLVAR", "ICC"));
				}

				if (checkBitMap(62)) {
					addField(new Field(62, "Additional data 2", 0, null, false,
							'-', true, "LLLVAR", "PDS"));
				}

				if (checkBitMap(63)) {
					addField(new Field(63, "Transaction lifecycle Id", 16, null,
							false, '-', true, "LLLVAR", null));
				}

				if (checkBitMap(71)) {
					addField(new Field(71, "Message number", 8, null, false, '-',
							true, "FIXED", null));
				}

				if (checkBitMap(72)) {
					addField(new Field(72, "Data record", 0, null, false, '-',
							true, "LLLVAR", null));
				}

				if (checkBitMap(73)) {
					addField(new Field(73, "Date- Action", 6, null, false, '-',
							true, "FIXED", null));
				}

				if (checkBitMap(93)) {
					addField(new Field(93,
							"Transaction destination institution ID", 0, null,
							false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(94)) {
					addField(new Field(94, "Transaction originator institution ID",
							0, null, false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(95)) {
					addField(new Field(95, "Card issuer reference data", 10, null,
							false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(100)) {
					addField(new Field(100, "Receiving institution ID", 11, null,
							false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(111)) {
					addField(new Field(111,
							"Amount, currency conversion assignment", 0, null,
							false, '-', true, "LLLVAR", null));
				}

				if (checkBitMap(123)) {
					addField(new Field(123, "Additional data 3", 0, null, false,
							'-', true, "LLLVAR", "PDS"));
				}

				if (checkBitMap(124)) {
					addField(new Field(124, "Additional data 4", 0, null, false,
							'-', true, "LLLVAR", "PDS"));
				}

				if (checkBitMap(125)) {
					addField(new Field(125, "Additional data 5", 0, null, false,
							'-', true, "LLLVAR", "PDS"));
				}

				if (checkBitMap(127)) {
					addField(new Field(127, " Network data", 0, null, false, '-',
							true, "LLLVAR", null));
				}
				scanAllFieldValues(Del1,con,count,  ps,filename,filedate);
			
			} catch (StringIndexOutOfBoundsException e) {
				 break;
			}
		}
		ps.executeBatch();
	}
	
	public boolean checkBitMap(int i) {
		return bitIntArr.contains(i);
	}
	
	
	@Override
	public void printMessage() throws Exception {
	}
	
	@SuppressWarnings("unchecked")
	public void scanAllFieldValues(String msgType,Connection con,int count,PreparedStatement ps,String filename,String filedate) throws Exception {

		//int count=0;
		Collections.sort(Fields);
		int message_pointer = 0;
		
		Map<String, Object> key_value = new HashMap<String, Object>();
		try {
			 
			int length_size = 0;
			for (Field field : Fields) {
				Runtime runtime = Runtime.getRuntime();

			//System.out.println("Free mem"+runtime.freeMemory());
				 
				String data = new String(getDataString().substring(message_pointer).getBytes(), "ISO-8859-15") ;
				 
				int field_length = field.getMaxSize();
				 
				if (field.getIsSizeFix()) {
					if (field.getExtendFormat() == null) { 
					} else {

						if (field.getExtendFormat().equals("LLVAR")) {
							/******/
							length_size = 2;
						}
						if (field.getExtendFormat().equals("LLLVAR")) {
							/******/
							length_size = 3;
						}

						if (field.getExtendFormat().equals("LLL+VAR")) {
							length_size = 0;
						}

						if (field.getExtendFormat().equals("FIXED")) {
							length_size = 0;
						}
					}
				}

				if (length_size > 0) {
					String field_len_raw = data.substring(0,length_size) ;
					field_length = (Integer.parseInt(field_len_raw.trim()));

				}  else{
					
				}
		String field_data = data.substring(length_size, length_size+ field_length);
		message_pointer +=  (field_length + length_size);
				if (field.getField_processor() == "PDS") {
					try {
						key_value.put(
								"DE" + field.getFieldNumber(),field_data);
						key_value
								.putAll(TransactionUtility._get_pds_fields(field_data));
					} catch (Exception e) {
					}

				} else if (field.getField_processor() == "DE43") {

					try {
						key_value.put(
								"DE43",
								data.substring(length_size, length_size
										+ field_length));
						key_value
								.putAll(TransactionUtility._get_de43_fields(field_data));
					} catch (Exception e) {
					}

				} else if (field.getField_processor() == "ICC") {

				try {
					key_value.put(
							"ICC",
							data.substring(length_size, length_size
									+ field_length));

					key_value.putAll(	TransactionUtility._get_icc_fields(field_data));
				} catch (Exception e) {
				}
				} else {
			try {
				key_value.put(
						"DE" + field.getFieldNumber(),
						field_data);
			} catch (Exception e) {
				// TODO: handle exception
			}
				}
				bitIntArr.clear();
			}
		} catch (Exception ex) {
		ex.printStackTrace();	 
		}
		key_value.put(
				"MTI",
				msgType);
		
		JSONObject jObj = new JSONObject(); 
		String DE12=null;
				jObj.putAll(key_value);
				//System.out.println(count);
				//System.out.println(key_value);
				String DE5=(String) key_value.get("DE5");
				String DE9=(String) key_value.get("DE9");
					String PDS0105 = (String) key_value.get("PDS0105");
					String DE100=(String) key_value.get("DE100");
					String PDS0165=(String) key_value.get("PDS0165");
					String DE43_COUNTRY=(String) key_value.get("DE43_COUNTRY");
					String DE37=(String) key_value.get("DE37");
					String DE43_ADDRESS=(String) key_value.get("DE43_ADDRESS");
					String DE38=(String) key_value.get("DE38");
					String DE33=(String) key_value.get("DE33");
					String DE32=(String) key_value.get("DE32");
					String DE40=(String) key_value.get("DE40");
					String DE41=(String) key_value.get("DE41");
					String DE42=(String) key_value.get("DE42");
					String DE43_SUBURB=(String) key_value.get("DE43_SUBURB");
					String DE63=(String) key_value.get("DE63");
					String PDS0159=(String) key_value.get("PDS0159");
					String PDS0158=(String) key_value.get("PDS0158");
					String PDS0191=(String) key_value.get("PDS0191");
					String PDS0177=(String) key_value.get("PDS0177");
					String DE93=(String) key_value.get("DE93");
					String DE26=(String) key_value.get("DE26");
					String DE25=(String) key_value.get("DE25");
					String DE43=(String) key_value.get("PDS0105");
					String DE24=(String) key_value.get("DE24");
					String DE94=(String) key_value.get("DE94");
					String DE49=(String) key_value.get("DE49");
					
					String DE43_NAME=(String) key_value.get("DE43_NAME");
					String DE43_POSTCODE=(String) key_value.get("DE43_POSTCODE");
					String PDS0211=(String) key_value.get("PDS0211");
					String DE71=(String) key_value.get("DE71");
					String DE48=(String) key_value.get("DE48");
					String DE22=(String) key_value.get("DE22");
					String PDS0023=(String) key_value.get("PDS0023");
					String DE4=(String) key_value.get("DE4");
					String DE3=(String) key_value.get("DE3");
					String DE31=(String) key_value.get("DE31");
					String DE2=(String) key_value.get("DE2");
					String DE50=(String) key_value.get("DE50");
					String DE51=(String) key_value.get("DE51");
					String PDS0146=(String) key_value.get("PDS0146");
					//String DE9=(String) key_value.get("DE9");
					String PDS0002=(String) key_value.get("PDS0002");
					String PDS0148=(String) key_value.get("PDS0148");
					String MTI=(String) key_value.get("MTI");
					String PDS0003=(String) key_value.get("PDS0003");
					String DE12_val=(String) key_value.get("DE12");
					//Addded by int8624
					String fPan = null;
					if(DE12_val==null)
					{
						DE12="";
					}
					else{
					 DE12=DE12_val.substring(0,6);
					}
					
					String concat_address=DE43_NAME+","+DE43_ADDRESS+","+DE43_SUBURB+","+DE43_POSTCODE;
					String query="insert into MASTERCARD_POS_RAWDATA(msgtype,pan,processing_code,amount,AMOUNT_RECON,CONV_RATE_RECON,date_val,expire_date,data_code,card_seq_num,funcation_code,msg_res_code, "    
							+ "card_acc_code,amount_org,aquierer_ref_no,fi_id_code,retrv_ref_no,approval_code,service_code,card_acc_term_id,card_acc_id_code,additional_data ,"
							+ "currency_code_tran,currency_code_recon,TRAN_LIFECYCLE_ID,msg_num,date_action,tran_dest_id_code,tran_org_id_code,CARD_ISS_REF_DATA,recv_inst_idcode, "
							+ "terminal_type,elec_com_indic,processing_mode,currency_exponent,business_act,settlement_ind,CARD_ACCP_NAME_LOC,header_type,file_name,FILEDATE,FPAN )  "
															+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,(to_date(?,'MM/DD/YYYY')),?)";
			        ps = con.prepareStatement(query);
			        ps.setString(1, MTI);
					//ps.setString(2, DE2);
					
			        if(DE2!= null) {
					String pan = DE2;
					//added by int8624
					fPan = pan;
					String Update_Pan="";		
					
					if(pan.length() <= 16 && pan != null && pan.trim()!="" && pan.length()>0 ) {
         				  // System.out.println(pan);
         				    Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);
         				   
         			   }else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {
         				   
         				    Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);
         				   
         			   } else {
         				   
         				   Update_Pan =null;
         			   }
					
					ps.setString(2, Update_Pan);
					
					
					} else {
						
						ps.setString(2, DE2);
					} 
					
					ps.setString(3, DE3);
					ps.setString(4,DE4);
					ps.setString(5,DE5);
					ps.setString(6,DE9);
					ps.setString(7, DE12);
					ps.setString(8,(String) key_value.get("DE14"));
					ps.setString(9, DE22);
					ps.setString(10, (String) key_value.get("DE23"));
					ps.setString(11,DE24 );
					ps.setString(12, DE25);
					ps.setString(13,DE26 );
					ps.setString(14,(String) key_value.get("DE30"));
					ps.setString(15,DE31);
					ps.setString(16,DE33);
					ps.setString(17, DE37);
					ps.setString(18,DE38);
					ps.setString(19, DE40);
					ps.setString(20, DE41 );
					ps.setString(21, DE42 );
					ps.setString(22, DE48);
					ps.setString(23, DE49);
					ps.setString(24,DE50);
					ps.setString(25,DE63);
					ps.setString(26,DE71);
					ps.setString(27,(String) key_value.get("DE73"));
					ps.setString(28,DE93);
					ps.setString(29,DE94);
					ps.setString(30,(String) key_value.get("DE95"));
					ps.setString(31,DE100);
					ps.setString(32,PDS0023);
					ps.setString(33,(String) key_value.get("PDS0052"));
					ps.setString(34,(String) key_value.get("PDS0122"));
					ps.setString(35,PDS0148);
					ps.setString(36,PDS0158);
					ps.setString(37,PDS0165);
					//ps.setString(35,DE43);
					if(DE43_SUBURB==null)
					{
						ps.setString(38,"");
					}
					else
					{
					ps.setString(38,concat_address);
					}
					if(MTI.equalsIgnoreCase("1240"))
					{
						ps.setString(39,"FINANCIAL");
					}
					else if(MTI.equalsIgnoreCase("1442"))
					{
						ps.setString(39,"CHARGEBACK");
					}
					else if(MTI.equalsIgnoreCase("1644"))
					{
						ps.setString(39,"ADMINISTRATIVE");
					}
					else if(MTI.equalsIgnoreCase("1740"))
					{
						ps.setString(39,"FEE_COLLECTION");
					}
					ps.setString(40,filename);
					ps.setString(41,filedate);
					ps.setString(42,fPan);
					int success = ps.executeUpdate();
					//ps.setString(35,DE43);
				/*	ps.addBatch();
					//count=count+1;
					if (count >= 15) {
						try {
							
							count = 0;
							int success[] = ps.executeBatch();
							System.out.println("batch exc" + count);
							// ps.close();
						} catch (Exception e) {

							e.printStackTrace();
							
							// continue;
						}

					}*/
					
				
					ps.close();
		Jcsv.add(jObj);
	}
	@Override
	public void _read125100(ArrayList<String> line_data,String filename) throws Exception {
		int rowCount=0; 
		OracleConn conn=new OracleConn();
		Connection con=conn.getconn();
		SXSSFWorkbook workbook1 = new SXSSFWorkbook(1000);
		
	    SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet("REPORT");
		int count=0;
		 PreparedStatement ps=null;
		 String DE12=null;
		 SXSSFRow header = sheet.createRow(0);
	        
	        header.createCell(0).setCellValue("MSGTYPE");
			header.createCell(1).setCellValue("PAN");
			header.createCell(2).setCellValue("PROCESSING_CODE");
			header.createCell(3).setCellValue("AMOUNT");
			header.createCell(4).setCellValue("AMOUNT_RECON");
			header.createCell(5).setCellValue("CONV_RATE_RECON");
			header.createCell(6).setCellValue("DATE_VAL");
			header.createCell(7).setCellValue("EXPIRE_DATE");
			header.createCell(8).setCellValue("DATA_CODE");
			header.createCell(9).setCellValue("CARD_SEQ_NUM");
			header.createCell(10).setCellValue("FUNCATION_CODE");
			header.createCell(11).setCellValue("MSG_RES_CODE");
			header.createCell(12).setCellValue("CARD_ACC_CODE");
			header.createCell(13).setCellValue("AMOUNT_ORG");
			header.createCell(14).setCellValue("AQUIERER_REF_NO");
			header.createCell(15).setCellValue("FI_ID_CODE");
			header.createCell(16).setCellValue("RETRV_REF_NO");
			header.createCell(17).setCellValue("APPROVAL_CODE");
			header.createCell(18).setCellValue("SERVICE_CODE");
			header.createCell(19).setCellValue("CARD_ACC_TERM_ID");
			header.createCell(20).setCellValue("CARD_ACC_ID_CODE");
			header.createCell(21).setCellValue("ADDITIONAL_DATA");
			header.createCell(22).setCellValue("CURRENCY_CODE_TRAN");
			header.createCell(23).setCellValue("CURRENCY_CODE_RECON");
			header.createCell(24).setCellValue("TRAN_LIFECYCLE_ID");
			header.createCell(25).setCellValue("MSG_NUM");
			header.createCell(26).setCellValue("DATE_ACTION");
			header.createCell(27).setCellValue("TRAN_DEST_ID_CODE");
			header.createCell(28).setCellValue("TRAN_ORG_ID_CODE");
			header.createCell(29).setCellValue("CARD_ISS_REF_DATA");
			header.createCell(30).setCellValue("RECV_INST_IDCODE");
			header.createCell(31).setCellValue("TERMINAL_TYPE");
			header.createCell(32).setCellValue("ELEC_COM_INDIC");
			header.createCell(33).setCellValue("PROCESSING_MODE");
			header.createCell(34).setCellValue("CURRENCY_EXPONENT");
			header.createCell(35).setCellValue("BUSINESS_ACT");
			header.createCell(36).setCellValue("SETTLEMENT_IND");
			header.createCell(37).setCellValue("CARD_ACCP_NAME_LOC");
			
		for (int xxx = 0; xxx < line_data.size() ; xxx++) { ///line_data.size()
			 rowCount++; 
			count=count+1;
			int msg_len = (line_data.get(xxx).toString().length());
			 
			try {
				String Del1 = line_data.get(xxx).toString().substring(0, 4);	
				String Del2 =  line_data.get(xxx).toString().substring(4, 20)  ;
				ArrayList bits = TransactionUtility.toBinary_list(Del2.getBytes()); // Del2.getBytes()
																					// tempBuff.toString().getBytes()
				setDataString( line_data.get(xxx).toString().substring(20, msg_len));
				for (int bit = 1; bit < bits.size(); bit++) {
					if ((boolean) bits.get(bit).equals('1')) {
						bitIntArr.add(bit + 1);
					}
				}
				//System.out.println(bitIntArr);
				Fields.clear();
				if (checkBitMap(1)) {
					addField(new Field(1, "Bitmap secondary", 8, null, false, '-',
							true, "FIXED", null));
				}
				if (checkBitMap(2)) {
					addField(new Field(2, "PAN", 0, null, false, '-', true,
							"LLVAR", "PAN"));
				}
				if (checkBitMap(3)) {
					addField(new Field(3, "Processing code", 6, null, false, '-',
							true, "FIXED", null));
				}
				if (checkBitMap(4)) {
					addField(new Field(4, "Amount transaction", 12, null, false,
							'-', true, "FIXED", null));
				}
				if (checkBitMap(5)) {
					addField(new Field(5, "Amount-Reconciliation", 12, null, false,
							'-', true, "FIXED", null));
				}
				if (checkBitMap(6)) {
					addField(new Field(6, "Amount-Cardholder billing", 12, null,
							false, '-', true, "FIXED", null));
				}
				if (checkBitMap(9)) {
					addField(new Field(9, "Conversion rate-Reconciliation", 8,
							null, false, '-', true, "FIXED", null));
				}
				if (checkBitMap(10)) {
					addField(new Field(10, "Conversion rate-Cardholder billing", 8,
							null, false, '-', true, "FIXED", null));
				}
				if (checkBitMap(12)) {
					addField(new Field(12, "Date/Time local transaction", 12, null,
							false, '-', true, "FIXED", null));
				}

				if (checkBitMap(14)) {
					addField(new Field(14, " Expiration date", 4, null, false, '-',
							true, "FIXED", null));
				}
				if (checkBitMap(22)) {
					addField(new Field(22, "Point of service data code", 12, null,
							false, '-', true, "FIXED", null));
				}
				if (checkBitMap(23)) {
					addField(new Field(23, "Card sequence number", 3, null, false,
							'-', true, "FIXED", null));
				}
				if (checkBitMap(24)) {
					addField(new Field(24, "Function code", 3, null, false, '-',
							true, "FIXED", null));
				}

				if (checkBitMap(25)) {
					addField(new Field(25, "Message reason code", 4, null, false,
							'-', true, "FIXED", null));
				}

				if (checkBitMap(26)) {
					addField(new Field(26, "Card acceptor business code", 4, null,
							false, '-', true, "FIXED", null));
				}

				if (checkBitMap(30)) {
					addField(new Field(30, "Amounts-original", 24, null, false,
							'-', true, "FIXED", null));
				}

				if (checkBitMap(31)) {
					addField(new Field(31, "Acquirer reference data", 23, null,
							false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(32)) {
					addField(new Field(32, "Acquiring institution ID code", 0,
							null, false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(33)) {
					addField(new Field(33, "Forwarding institution ID code", 0,
							null, false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(37)) {
					addField(new Field(37, "Retrieval reference number", 12, null,
							false, '-', true, "FIXED", null));
				}

				if (checkBitMap(38)) {
					addField(new Field(38, "Approval code", 6, null, false, '-',
							true, "FIXED", null));
				}

				if (checkBitMap(40)) {
					addField(new Field(40, "Service code", 3, null, false, '-',
							true, "FIXED", null));
				}

				if (checkBitMap(41)) {
					addField(new Field(41, " Card acceptor terminal ID", 8, null,
							false, '-', true, "FIXED", null));
				}

				if (checkBitMap(42)) {
					addField(new Field(42, " Card acceptor Id", 15, null, false,
							'-', true, "FIXED", null));
				}

				if (checkBitMap(43)) {
					addField(new Field(43, "Card acceptor name/location", 0, null,
							false, '-', true, "LLVAR", "DE43"));
				}

				if (checkBitMap(48)) {
					addField(new Field(48, "Additional data", 0, null, false, '-',
							true, "LLLVAR", "PDS"));
				}

				if (checkBitMap(49)) {
					addField(new Field(49, "Currency code Transaction", 3, null,
							false, '-', true, "FIXED", null));
				}

				if (checkBitMap(50)) {
					addField(new Field(50, "Currency code- Reconciliation", 3,
							null, false, '-', true, "FIXED", null));
				}

				if (checkBitMap(51)) {
					addField(new Field(51, "Currency code- Cardholder billing", 3,
							null, false, '-', true, "FIXED", null));
				}

				if (checkBitMap(54)) {
					addField(new Field(54, "Amounts- additional", 0, null, false,
							'-', true, "LLLVAR", null));
				}

				if (checkBitMap(55)) {
					addField(new Field(55, "ICC system related data", 255, null,
							false, '-', true, "LLLVAR", "ICC"));
				}

				if (checkBitMap(62)) {
					addField(new Field(62, "Additional data 2", 0, null, false,
							'-', true, "LLLVAR", "PDS"));
				}

				if (checkBitMap(63)) {
					addField(new Field(63, "Transaction lifecycle Id", 16, null,
							false, '-', true, "LLLVAR", null));
				}

				if (checkBitMap(71)) {
					addField(new Field(71, "Message number", 8, null, false, '-',
							true, "FIXED", null));
				}

				if (checkBitMap(72)) {
					addField(new Field(72, "Data record", 0, null, false, '-',
							true, "LLLVAR", null));
				}

				if (checkBitMap(73)) {
					addField(new Field(73, "Date- Action", 6, null, false, '-',
							true, "FIXED", null));
				}

				if (checkBitMap(93)) {
					addField(new Field(93,
							"Transaction destination institution ID", 0, null,
							false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(94)) {
					addField(new Field(94, "Transaction originator institution ID",
							0, null, false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(95)) {
					addField(new Field(95, "Card issuer reference data", 10, null,
							false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(100)) {
					addField(new Field(100, "Receiving institution ID", 11, null,
							false, '-', true, "LLVAR", null));
				}

				if (checkBitMap(111)) {
					addField(new Field(111,
							"Amount, currency conversion assignment", 0, null,
							false, '-', true, "LLLVAR", null));
				}

				if (checkBitMap(123)) {
					addField(new Field(123, "Additional data 3", 0, null, false,
							'-', true, "LLLVAR", "PDS"));
				}

				if (checkBitMap(124)) {
					addField(new Field(124, "Additional data 4", 0, null, false,
							'-', true, "LLLVAR", "PDS"));
				}

				if (checkBitMap(125)) {
					addField(new Field(125, "Additional data 5", 0, null, false,
							'-', true, "LLLVAR", "PDS"));
				}

				if (checkBitMap(127)) {
					addField(new Field(127, " Network data", 0, null, false, '-',
							true, "LLLVAR", null));
				}
				scanAllFieldValues1(Del1,con,count,  ps,filename,workbook1,sheet,rowCount);
			
			} catch (StringIndexOutOfBoundsException e) {
				 break;
			}
		}
		FileOutputStream outputStream = new FileOutputStream("D://POS.xlsx");
	    workbook1.write(outputStream);
		
	}
	
	/*public boolean checkBitMap1(int i) {
		return bitIntArr.contains(i);
	}*/
	
	
	
	@SuppressWarnings("unchecked")
	public void scanAllFieldValues1(String msgType,Connection con,int count,PreparedStatement ps,String filename,SXSSFWorkbook workbook1,SXSSFSheet sheet,int rowCount ) throws Exception {

		System.out.println(rowCount);
		Collections.sort(Fields);
		int message_pointer = 0;
		
		
	 
        
        
		
		Map<String, Object> key_value = new HashMap<String, Object>();
		try {
			 
			int length_size = 0;
			for (Field field : Fields) {
				 
				String data = new String(getDataString().substring(message_pointer).getBytes(), "ISO-8859-15") ;
				//System.out.println("Data"+data);
				 
				int field_length = field.getMaxSize();
				//System.out.println("field_length"+field_length);
				if (field.getIsSizeFix()) {
					if (field.getExtendFormat() == null) { 
					} else {

						if (field.getExtendFormat().equals("LLVAR")) {
							/******/
							length_size = 2;
						}
						if (field.getExtendFormat().equals("LLLVAR")) {
							/******/
							length_size = 3;
						}

						if (field.getExtendFormat().equals("LLL+VAR")) {
							length_size = 0;
						}

						if (field.getExtendFormat().equals("FIXED")) {
							length_size = 0;
						}
					}
				}

				if (length_size > 0) {
					String field_len_raw = data.substring(0,length_size) ;
					field_length = (Integer.parseInt(field_len_raw.trim()));

				}  else{
					
				}
		String field_data = data.substring(length_size, length_size+ field_length);
		message_pointer +=  (field_length + length_size);
				if (field.getField_processor() == "PDS") {
					try {
						key_value.put(
								"DE" + field.getFieldNumber(),field_data);
						key_value
								.putAll(TransactionUtility._get_pds_fields(field_data));
					} catch (Exception e) {
					}

				} else if (field.getField_processor() == "DE43") {

					try {
						key_value.put(
								"DE43",
								data.substring(length_size, length_size
										+ field_length));
						key_value
								.putAll(TransactionUtility._get_de43_fields(field_data));
					} catch (Exception e) {
					}

				} else if (field.getField_processor() == "ICC") {

				try {
					key_value.put(
							"ICC",
							data.substring(length_size, length_size
									+ field_length));

					key_value.putAll(	TransactionUtility._get_icc_fields(field_data));
				} catch (Exception e) {
				}
				} else {
			try {
				key_value.put(
						"DE" + field.getFieldNumber(),
						field_data);
			} catch (Exception e) {
				// TODO: handle exception
			}
				}
				bitIntArr.clear();
			}
		} catch (Exception ex) {
		ex.printStackTrace();	 
		}
		key_value.put(
				"MTI",
				msgType);
		
		SXSSFRow aRow = (SXSSFRow) sheet.createRow(rowCount++);  
		JSONObject jObj = new JSONObject(); 
		String DE12=null;
				jObj.putAll(key_value);
				//System.out.println(count);
				//System.out.println(key_value);
				String DE5=(String) key_value.get("DE5");
				String DE9=(String) key_value.get("DE9");
					String PDS0105 = (String) key_value.get("PDS0105");
					String DE100=(String) key_value.get("DE100");
					String PDS0165=(String) key_value.get("PDS0165");
					String DE43_COUNTRY=(String) key_value.get("DE43_COUNTRY");
					String DE37=(String) key_value.get("DE37");
					String DE43_ADDRESS=(String) key_value.get("DE43_ADDRESS");
					String DE38=(String) key_value.get("DE38");
					String DE33=(String) key_value.get("DE33");
					String DE32=(String) key_value.get("DE32");
					String DE40=(String) key_value.get("DE40");
					String DE41=(String) key_value.get("DE41");
					String DE42=(String) key_value.get("DE42");
					String DE43_SUBURB=(String) key_value.get("DE43_SUBURB");
					String DE63=(String) key_value.get("DE63");
					String PDS0159=(String) key_value.get("PDS0159");
					String PDS0158=(String) key_value.get("PDS0158");
					String PDS0191=(String) key_value.get("PDS0191");
					String PDS0177=(String) key_value.get("PDS0177");
					String DE93=(String) key_value.get("DE93");
					String DE26=(String) key_value.get("DE26");
					String DE25=(String) key_value.get("DE25");
					String DE43=(String) key_value.get("PDS0105");
					String DE24=(String) key_value.get("DE24");
					String DE94=(String) key_value.get("DE94");
					String DE49=(String) key_value.get("DE49");
					
					String DE43_NAME=(String) key_value.get("DE43_NAME");
					String DE43_POSTCODE=(String) key_value.get("DE43_POSTCODE");
					String PDS0211=(String) key_value.get("PDS0211");
					String DE71=(String) key_value.get("DE71");
					String DE48=(String) key_value.get("DE48");
					String DE22=(String) key_value.get("DE22");
					String PDS0023=(String) key_value.get("PDS0023");
					String DE4=(String) key_value.get("DE4");
					String DE3=(String) key_value.get("DE3");
					String DE31=(String) key_value.get("DE31");
					String DE2=(String) key_value.get("DE2");
					String DE50=(String) key_value.get("DE50");
					String DE51=(String) key_value.get("DE51");
					String PDS0146=(String) key_value.get("PDS0146");
					//String DE9=(String) key_value.get("DE9");
					String PDS0002=(String) key_value.get("PDS0002");
					String PDS0148=(String) key_value.get("PDS0148");
					String MTI=(String) key_value.get("MTI");
					String PDS0003=(String) key_value.get("PDS0003");
					String DE12_val=(String) key_value.get("DE12");
					String DE23=(String) key_value.get("DE23");
					String DE30=(String) key_value.get("DE30");
					String DE73=(String) key_value.get("DE73");
					String ICC=(String) key_value.get("ICC");
					String DE95=(String) key_value.get("DE95");
					String PDS0052=(String) key_value.get("PDS0052");
					String PDS0122=(String) key_value.get("PDS0122");
					String DE14=(String) key_value.get("DE14");
					if(DE12_val==null)
					{
						DE12="";
					}
					else{
					 DE12=DE12_val.substring(0,6);
					}
					String concat_address=DE43_NAME+","+DE43_ADDRESS+","+DE43_SUBURB+","+DE43_POSTCODE;
					aRow.createCell(0).setCellValue(MTI);
					aRow.createCell(1).setCellValue(DE2);
					aRow.createCell(2).setCellValue(DE3);
					aRow.createCell(3).setCellValue(DE4);
					aRow.createCell(4).setCellValue(DE5);
					aRow.createCell(5).setCellValue(DE9);
					aRow.createCell(6).setCellValue(DE12);
					aRow.createCell(7).setCellValue(DE14);
					aRow.createCell(8).setCellValue(DE22);
					aRow.createCell(9).setCellValue(DE23);
					aRow.createCell(10).setCellValue(DE24);
					aRow.createCell(11).setCellValue(DE25);
					aRow.createCell(12).setCellValue(DE26);
					aRow.createCell(13).setCellValue(DE30);
					aRow.createCell(14).setCellValue(DE31);
					aRow.createCell(15).setCellValue(DE33);
					aRow.createCell(16).setCellValue(DE37);
					aRow.createCell(17).setCellValue(DE38);
					aRow.createCell(18).setCellValue(DE40);
					aRow.createCell(19).setCellValue(DE41);
					aRow.createCell(20).setCellValue(DE42);
					aRow.createCell(21).setCellValue(DE48);
					aRow.createCell(22).setCellValue(DE49);
					aRow.createCell(23).setCellValue(DE50);
					aRow.createCell(24).setCellValue(DE63);
					aRow.createCell(25).setCellValue(DE71);
					aRow.createCell(26).setCellValue("");
					aRow.createCell(27).setCellValue(DE93);
					aRow.createCell(28).setCellValue(DE94);
					aRow.createCell(29).setCellValue("");
					aRow.createCell(30).setCellValue(DE100);
					aRow.createCell(31).setCellValue(PDS0023);
					aRow.createCell(32).setCellValue(PDS0052);
					aRow.createCell(33).setCellValue("");
					aRow.createCell(34).setCellValue(PDS0148);
					aRow.createCell(35).setCellValue(PDS0158);
					aRow.createCell(36).setCellValue(PDS0165);
					aRow.createCell(37).setCellValue(concat_address);
					/*aRow.createCell(38).setCellValue("o");
					aRow.createCell(41).setCellValue("o");*/
					//rowCount++;
					//ps.setString(35,DE43);
				/*	ps.addBatch();
					//count=count+1;
					if (count >= 15) {
						try {
							
							count = 0;
							int success[] = ps.executeBatch();
							System.out.println("batch exc" + count);
							// ps.close();
						} catch (Exception e) {

							e.printStackTrace();
							
							// continue;
						}

					}*/
					
				
					
		//Jcsv.add(jObj);
		
		
	}
	
}
