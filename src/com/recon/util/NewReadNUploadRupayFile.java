
package com.recon.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.recon.model.CompareSetupBean;

public class NewReadNUploadRupayFile {

	
	Connection con;
	int file_count=0;
	int upload_count=0;
	ResultSet rs;
	
	Statement st;
	int part_id;
	String man_flag="N",upload_flag="Y";
	
	String insert ="INSERT  INTO RUPAY_RUPAY_RAWDATA (MTI,Function_Code ,Record_Number,Member_Institution_ID_Code,Unique_File_Name,Date_Settlement,Product_Code,Settlement_BIN,File_Category,Version_Number,"
			+ "Entire_File_Reject_Indicator,File_Reject_Reason_Code,Transactions_Count,Run_Total_Amount,"
			+ "Acquirer_Institution_ID_code,Amount_Settlement,Amount_Transaction,Approval_Code,Acquirer_Reference_Data,Case_Number,Currency_Code_Settlement,Currency_Code_Transaction,Conversion_Rate_Settlement,"
			+ "Card_Acceptor_Addi_Addr,Card_Acceptor_Terminal_ID,Card_Acceptor_Zip_Code,DateandTime_Local_Transaction,TXNFunction_Code ,Late_Presentment_Indicator,TXNMTI,Primary_Account_Number,TXNRecord_Number,"
			+ "RGCS_Received_date,Settlement_DR_CR_Indicator,Txn_Desti_Insti_ID_code,Txn_Origin_Insti_ID_code,Card_Holder_UID,Amount_Billing,Currency_Code_Billing,Conversion_Rate_billing,Message_Reason_Code,"
			+ "Fee_DR_CR_Indicator1,Fee_amount1,Fee_Currency1,Fee_Type_Code1,Interchange_Category1,Fee_DR_CR_Indicator2,Fee_amount2,Fee_Currency2,Fee_Type_Code2,Interchange_Category2,Fee_DR_CR_Indicator3,Fee_amount3,"
			+ "Fee_Currency3,Fee_Type_Code3,Interchange_Category3,Fee_DR_CR_Indicator4,Fee_amount4,Fee_Currency4,Fee_Type_Code4,Interchange_Category4,Fee_DR_CR_Indicator5,Fee_amount5,"
			+ "Fee_Currency5,Fee_Type_Code5,Interchange_Category5,"
			+ "MCC_Code,Merchant_Name," //added MCC_Code,Merchant_Name by int6261 date 03/04/2018
			+ "Trl_FUNCTION_CODE,Trl_RECORD_NUMBER,flag,FILEDATE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'))";
	
	String update="update RUPAY_RUPAY_RAWDATA  set Trl_FUNCTION_CODE = ? , TRL_RECORD_NUMBER= ?,TRANSACTIONS_COUNT=? where to_char(CREATEDDATE,'dd-mm-yy')=to_char(sysdate,'dd-mm-yy')";
	String trl_nFunCd=null, trl_nRecNum=null,transactions_count=null; 
	
	public boolean uploadCBSData(String fileName,String filedate,String filePath) {


		String[] filenameSplit = fileName.split("_");
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date filedt = null;
		String fileDate="";
		String flag="";
		
		

		try{
			
			String getFile_count = "Select FILE_COUNT from Main_fileSource where FILE_CATEGORY ='RUPAY' and filename='RUPAY'  and FILE_SUBCATEGORY = 'DOMESTIC'";
			
			//get File Count
			
				try{
				OracleConn conn = new OracleConn();
				con = conn.getconn();
				st = con.createStatement();
				System.out.println("SELECT CASE WHEN exists ("+ getFile_count+") then ("+getFile_count+") else 0 end as FLAG from dual  ");
				rs = st.executeQuery( "SELECT CASE WHEN exists ("+ getFile_count+") then ("+getFile_count+") else 0 end as FLAG from dual  ");
				while(rs.next()) {
					
					file_count = rs.getInt(1);
				}
				
				
				}catch(Exception ex) {
					
					ex.printStackTrace();
					System.out.println(ex.toString());
					
				} finally {
					
					rs.close();
					con.close();
					st.close();
				}
			
			
			
			String  getupld_count = "Select FILE_COUNT from main_file_upload_dtls where CATEGORY ='RUPAY'  and FILE_SUBCATEGORY = 'DOMESTIC' and filedate = to_date('"+filedate+"','dd/mm/yyyy')  ";
			
			//get upload Count
			
			try{
			OracleConn conn = new OracleConn();
			con = conn.getconn();
			st = con.createStatement();
			rs = st.executeQuery(getupld_count);
			while(rs.next()) {
				
				upload_count = rs.getInt(1);
			}
			
			
			}catch(Exception ex) {
				
				ex.printStackTrace();
				System.out.println(ex.toString());
				
			} finally {
				
				rs.close();
				con.close();
				st.close();
			}
			
			
			
			
			
		
			if(file_count > upload_count) {
				
			
			
		
			
		System.out.println(fileName);
		
		
		
			
			filedt = format.parse(filedate);
			flag="UPLOAD_FLAG";
			
			
		
		
		System.out.println(filedt);
		format = new SimpleDateFormat("dd/MM/yyyy");
		fileDate = format.format(filedt);
		
		
		CompareSetupBean setupBean = new CompareSetupBean();
		setupBean.setCategory("RUPAY");
		setupBean.setFileDate(fileDate);
		setupBean.setStFileName("RUPAY");
		
			
			setupBean.setInFileId(5);
	
	
		
			
			
			OracleConn con = new OracleConn();
			if(uploadData(con.getconn(),fileName,fileDate,filePath)){
			
				if(getFileCount(setupBean)>0) {
					
					updateFlag(flag, setupBean);
				}else {
				
					updatefile(setupBean);
				}
			
			}
		
		
		return true;
		
		} else {
			
			System.out.println("Upload Count Exceed.");
			
			return false;
			
		}

		} catch (Exception e) {

			System.out.println("Erro Occured");
			e.printStackTrace();
			
			return false;
		}
	
		
	}
	
	
	public boolean updatefile(CompareSetupBean setupBean) {
		
		try{
		OracleConn conn = new OracleConn();
		
		
		
		
		String query =" insert into Main_File_Upload_Dtls (FILEID,FILEDATE,UPDLODBY,UPLOADDATE,CATEGORY,FILE_SUBCATEGORY,Upload_FLAG,Filter_FLAG,Knockoff_FLAG,Comapre_FLAG,ManualCompare_Flag,MANUPLOAD_FLAG,file_count) "
				+ "values ("+setupBean.getInFileId()+",to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'AUTOMATION',sysdate,'"+setupBean.getCategory()+"','DOMESTIC'"
						+ ",'Y','N','N','N','N','N',1)";
		
		
			con = conn.getconn();
			st = con.createStatement();
			st.executeUpdate(query);
		
			
			
			return true; 
		}catch(Exception ex){
			
	
			System.out.println(ex);
			ex.printStackTrace();
			return false;
		}finally{
			
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
		public boolean updateFlag(String flag, CompareSetupBean setupBean) {
			
			try{
			
				
			OracleConn conn = new OracleConn();
			
			String switchList = "SELECT FILEID,file_category,file_subcategory from  main_filesource where upper(filename)= 'RUPAY' and FILE_CATEGORY='RUPAY' and FILE_SUBCATEGORY ='DOMESTIC'  ";
			
			con = conn.getconn();
			st = con.createStatement();
			
			ResultSet rs = st.executeQuery(switchList);
			
			int rowupdate=0;
			while(rs.next())
			{
			
			String query="Update MAIN_FILE_UPLOAD_DTLS set "+flag+" ='Y',FILE_COUNT = "+(upload_count+1)+"  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"+setupBean.getFileDate()+"','dd/mm/yyyy'),'dd/mm/yyyy') "
					+ " AND CATEGORY = 'RUPAY' AND FileId = "+rs.getString("FILEID")+" "; 
			
			
			con = conn.getconn();
			st = con.createStatement();
			 rowupdate =  st.executeUpdate(query);
			
			}
			
			if(rowupdate>0) {
				
				
				return true;
				
			}else{
				
				return false;
			}
			}catch(Exception ex){
				
				ex.printStackTrace();
				return false;
			}finally {
				
				try {
					st.close();
					con.close();
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	
		}

	
	
	private int getFileCount(CompareSetupBean setupBean) {
		
		try {
				int count = 0;
				OracleConn conn = new OracleConn();
				String query = "Select count (*) from MAIN_FILE_UPLOAD_DTLS  WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
						+ setupBean.getFileDate()
						+ "','dd/mm/yyyy'),'dd/mm/yyyy') "
						+ " AND upper(CATEGORY) = '"+setupBean.getCategory().toUpperCase()+"' AND FILE_SUBCATEGORY='DOMESTIC' AND FileId = "+setupBean.getInFileId() +" ";
	
				con = conn.getconn();
	
				st = con.createStatement();
	
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) {
	
					count = rs.getInt(1);
	
				}
	
				return count;
	
			} catch (Exception ex) {
	
				ex.printStackTrace();
	
				return -1;
	
			} finally {
	
				try {
					st.close();
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			}
			
	}


	public boolean uploadData(Connection con,String filename,String filedate,String filePath ) {
		
		
		
		//String filepath = "\\\\10.144.143.191\\led\\DCRS\\Rupay\\RAW FILES\\07 Sept 17\\International\\011IBKL25900021725002.xml";
		FileInputStream fis;
		//String filename	= "rupay.txt";
		
		int feesize=1;
		try{
			
			
				//fis = new FileInputStream("\\\\10.143.11.50\\led\\DCRS\\RUPAYDOMESTIC\\"+ filename);
			fis = new FileInputStream(filePath);
		
			
		//FileInputStream fis= new FileInputStream(new File(filename));
		 PreparedStatement ps = con.prepareStatement(insert);
		 PreparedStatement updtps = con.prepareStatement(update);
		
		  final Pattern TAG_REGEX = Pattern.compile(">(.+?)</");
		  final Pattern node_REGEX = Pattern.compile("<(.+?)>");
		  Matcher matcher;

		 BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	     String thisLine = null;
	     int count=1;
	     String hdr="",trl="";
	     RupayUtilBean utilBean = new RupayUtilBean(); ;
	     RupayHeaderUtil headerUtil = new RupayHeaderUtil();
	     System.out.println("Process started"+ new Date().getTime());
	     while ((thisLine = br.readLine()) != null) {
	    	  
	    	  System.out.println(thisLine);
	    	  final Matcher nodeMatcher = node_REGEX.matcher(thisLine);
	    	  nodeMatcher.find();
	    	 
	    	  
	    	  
	    	  if(nodeMatcher.group(1).equalsIgnoreCase("Txn")){
	    		  
	    		//  break;
	    		  
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("Hdr")) {
	    		  
	    		  hdr="hdr";
	    		 // break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("/Hdr")) {
	    		  hdr="";
				//	break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nDtTmFlGen")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
				  headerUtil.setnDtTmFlGen(matcher.group(1));
				//  break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nMemInstCd")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
				  headerUtil.setnMemInstCd(matcher.group(1));
				//  break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nUnFlNm")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
				  headerUtil.setnUnFlNm(matcher.group(1));
				//  break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nProdCd")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
				  headerUtil.setnProdCd(matcher.group(1));
				//  break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nSetBIN")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
				  headerUtil.setnSetBIN(matcher.group(1));
				//  break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nFlCatg")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
				  headerUtil.setnFlCatg(matcher.group(1));
				// break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nVerNum")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
				  headerUtil.setnVerNum(matcher.group(1));
				// break;	 
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nAcqInstCd")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
					utilBean.setnAcqInstCd(matcher.group(1));
				//break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nAmtSet")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
				  double amtSet = Integer.parseInt(matcher.group(1));
				  amtSet =  amtSet/100;
				 utilBean.setnAmtSet(String.valueOf(amtSet));
			
				//break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nAmtTxn")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
				  double amtTxn = Double.parseDouble(matcher.group(1));
				  amtTxn =  amtTxn/100;
				utilBean.setnAmtTxn(String.valueOf(amtTxn));
				
			//	break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nApprvlCd")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
					utilBean.setnApprvlCd(matcher.group(1));
				
			//	break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nARD")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
					utilBean.setnARD(matcher.group(1));
				
			//	break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdSet")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
				
					utilBean.setnCcyCdSet(matcher.group(1));
				
			//	break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdTxn")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
					utilBean.setnCcyCdTxn(matcher.group(1));
				
			//	break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nConvRtSet")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
					utilBean.setnConvRtSet(matcher.group(1));
				
			//	break;
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpAddAdrs")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
					utilBean.setnCrdAcpAddAdrs(matcher.group(1));
				
			//	break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcptTrmId")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
					utilBean.setnCrdAcptTrmId(matcher.group(1));
				
			//	break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpZipCd")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
					utilBean.setnCrdAcpZipCd(matcher.group(1));
				
			//	break;
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nDtSet")) {
	    		  
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
				 if(hdr.equalsIgnoreCase("hdr")) {
					headerUtil.setnDtSet(matcher.group(1));
			//		break;
				 }else{
					utilBean.setnDtSet(matcher.group(1));
		//			break;
				 }
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nDtTmLcTxn")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
				  matcher.find();
					utilBean.setnDtTmLcTxn(matcher.group(1));
				
		//		break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nFunCd")) {
	    		  
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
					 if(hdr.equalsIgnoreCase("hdr")) {
					 
					 
						headerUtil.setnFunCd(matcher.group(1));
			//			break;
					 }else if(hdr.equalsIgnoreCase("Trl")){
							trl_nFunCd= matcher.group(1);
							System.out.println(trl_nFunCd);
			//				break;
	    	  		}else{
	    			utilBean.setnFunCd(matcher.group(1));
			//		break;
	    	  			}
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nLtPrsntInd")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnLtPrsntInd(matcher.group(1));
					
			//		break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nMTI")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
					 if(hdr.equalsIgnoreCase("hdr")) {
						 headerUtil.setnMTI(matcher.group(1));
			//			 break;
					 } else{
						 utilBean.setnMTI(matcher.group(1));
			//			break;
					 }
				
				 
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nPAN")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnPAN(matcher.group(1));
			//		break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nRecNum")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();	
				if(hdr.equalsIgnoreCase("hdr")) {
						 headerUtil.setnRecNum(matcher.group(1));
			//			 break;
				}else if(hdr.equalsIgnoreCase("Trl")) {
						 headerUtil.setTrl_nRecNum(matcher.group(1));
						 trl_nRecNum=matcher.group(1);
						 System.out.println(trl_nRecNum);
			//			 break;
				}else {
						 utilBean.setnRecNum(matcher.group(1));
			//			break;
				}
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nRGCSRcvdDt")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnRGCSRcvdDt(matcher.group(1));
			//		break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nSetDCInd")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnSetDCInd(matcher.group(1));
					
			//		break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nTxnDesInstCd")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnTxnDesInstCd(matcher.group(1));
					
			//		break;
				
	    	  }
//	    	 new parameter added by int6261 03/04/2018 
	    	  //as per mail "Changes in Incoming File format for RuPay PoS / e-Comm domestic transactions"
//	    	  
	    	  else if(nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpBussCd")){
	    		  
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnCrdAcpBussCd(matcher.group(1));
	    		  
	    		  
	    	  } else if(nodeMatcher.group(1).equalsIgnoreCase("nCrdAcpNm")){
	    		  
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnCrdAcpNm(matcher.group(1));
	    		  
	    		  
	    	  }
	    	  
	    	  // changes end
	    	  
	    	  
	    	  
	    	  else if (nodeMatcher.group(1).equalsIgnoreCase("nTxnOrgInstCd")) {
	    		  
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnTxnOrgInstCd(matcher.group(1));
					
			//		break;
				
	    	  } else if (nodeMatcher.group(1).equalsIgnoreCase("nUID")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnUID(matcher.group(1));
					
			//		break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nFeeDCInd")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						switch(feesize){
						
						case 1 :
							utilBean.setnFeeDCInd1(matcher.group(1));
							break;
						case 2 :
							System.out.println("setnFeeDCInd2");
							utilBean.setnFeeDCInd2(matcher.group(1));
							break;
						case 3 :
							System.out.println("setnFeeDCInd3");
							utilBean.setnFeeDCInd3(matcher.group(1));
							break;
						case 4 :
							System.out.println("setnFeeDCInd4");
							utilBean.setnFeeDCInd4(matcher.group(1));
							break;
						case 5 :
							System.out.println("setnFeeDCInd5");
							utilBean.setnFeeDCInd5(matcher.group(1));
							break;
						default:
								break;
					}
					//break;
				
	    	  } else if (nodeMatcher.group(1).equalsIgnoreCase("nFeeAmt")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
					 switch(feesize){
						
						case 1 :
							utilBean.setnFeeAmt1(matcher.group(1));
							break;
						case 2 :
							System.out.println("setnFeeAmt2");
							utilBean.setnFeeAmt2(matcher.group(1));
							break;
						case 3 :
							System.out.println("setnFeeAmt3");
							utilBean.setnFeeAmt3(matcher.group(1));
							break;
						case 4 :
							System.out.println("setnFeeAmt4");
							utilBean.setnFeeAmt4(matcher.group(1));
							break;
						case 5 :
							System.out.println("setnFeeAmt5");
							utilBean.setFeeAmt5(matcher.group(1));
							break;
						default:
								break;
					}
					//break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nFeeCcy")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
					 switch(feesize){
						
						case 1 :
							
							utilBean.setnFeeCcy1(matcher.group(1));
							break;
						case 2 :
							System.out.println("nFeeCcy2");
							utilBean.setnFeeCcy2(matcher.group(1));
							break;
						case 3 :
							System.out.println("nFeeCcy3");
							utilBean.setnFeeCcy3(matcher.group(1));
							break;
						case 4 :
							System.out.println("nFeeCcy4");
							utilBean.setnFeeCcy4(matcher.group(1));
							break;
						case 5 :
							System.out.println("nFeeCcy5");
							utilBean.setnFeeCcy5(matcher.group(1));
							break;
						default:
								break;
					}
				//	break;
				
	    	  } else if (nodeMatcher.group(1).equalsIgnoreCase("nFeeTpCd")) {
	    			 matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
					 switch(feesize){
						
						case 1 :
							utilBean.setnFeeTpCd1(matcher.group(1));
							break;
						case 2 :
							utilBean.setnFeeTpCd2(matcher.group(1));
							break;
						case 3 :
							utilBean.setnFeeTpCd3(matcher.group(1));
							break;
						case 4 :
							utilBean.setnFeeTpCd4(matcher.group(1));
							break;
						case 5 :
							utilBean.setnFeeTpCd5(matcher.group(1));
							break;
						default:
								break;
					}
			//		break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nIntrchngCtg")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
					 switch(feesize){
						
						case 1 :
							utilBean.setnIntrchngCtg1(matcher.group(1));
							break;
						case 2 :
							utilBean.setnIntrchngCtg2(matcher.group(1));
							break;
						case 3 :
							utilBean.setnIntrchngCtg3(matcher.group(1));
							break;
						case 4 :
							utilBean.setnIntrchngCtg4(matcher.group(1));
							break;
						case 5 :
							utilBean.setnIntrchngCtg5(matcher.group(1));
							break;
						default:
								break;
					}
			//		break;
				
	    	  } else if (nodeMatcher.group(1).equalsIgnoreCase("/Fee")) {
	    		  feesize=feesize+1;
				//	break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nCaseNum")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnCaseNum(matcher.group(1));
					
				//	break;
				
	    	  } else if (nodeMatcher.group(1).equalsIgnoreCase("nContNum")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnContNum(matcher.group(1));
					
				//	break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nFulParInd")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnFulParInd(matcher.group(1));
					
				//	break;
				
	    	  } else if (nodeMatcher.group(1).equalsIgnoreCase("nProcCd")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnProdCd(matcher.group(1));
					
				//	break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nAmtBil")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnAmtBil(matcher.group(1));
					
				//	break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nCcyCdBil")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnCcyCdBil(matcher.group(1));
					
				//	break;
				
	    	  } else if (nodeMatcher.group(1).equalsIgnoreCase("nConvRtBil")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnConvRtBil(matcher.group(1));
					
				//	break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nMsgRsnCd")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						utilBean.setnMsgRsnCd(matcher.group(1));
					
				//	break;
				
	    	  } else if (nodeMatcher.group(1).equalsIgnoreCase("nRnTtlAmt")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						headerUtil.setnRnTtlAmt(matcher.group(1));
				//		break;
				
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("nTxnCnt")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						headerUtil.setnTxnCnt(matcher.group(1));
						transactions_count=matcher.group(1);
						System.out.println(transactions_count);
				//		break;
				
	    	  } else if (nodeMatcher.group(1).equalsIgnoreCase("Trl")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						hdr="Trl";
						
				//		break;
	    	  }else if (nodeMatcher.group(1).equalsIgnoreCase("/Trl")) {
	    		  matcher = TAG_REGEX.matcher(thisLine);
					 matcher.find();
						hdr="";
					
						
			//	break;
				
	    	  } else if (nodeMatcher.group(1).equalsIgnoreCase("/Txn")) {
	    		  
	    			feesize=1;
					ps.setString(1, headerUtil.getnMTI());
					ps.setString(2, headerUtil.getnFunCd());
					ps.setString(3, headerUtil.getnRecNum());
					ps.setString(4, headerUtil.getnMemInstCd());
					ps.setString(5, headerUtil.getnUnFlNm());
					ps.setString(6, headerUtil.getnDtSet());
					ps.setString(7, headerUtil.getnProdCd());
					ps.setString(8, headerUtil.getnSetBIN());
					ps.setString(9, headerUtil.getnFlCatg());
					ps.setString(10, headerUtil.getnVerNum());
					ps.setString(11, null);
					ps.setString(12, null);
					
					ps.setString(13, headerUtil.getnTxnCnt());
					ps.setString(14, headerUtil.getnRnTtlAmt());
					ps.setString(15, utilBean.getnAcqInstCd());
					ps.setString(16,  utilBean.getnAmtSet());
					ps.setString(17,  utilBean.getnAmtTxn());
					ps.setString(18,  utilBean.getnApprvlCd());
					ps.setString(19,  utilBean.getnARD());
					ps.setString(20,  utilBean.getnCaseNum());
					ps.setString(21,  utilBean.getnCcyCdSet());
					ps.setString(22,  utilBean.getnCcyCdTxn());
					
					ps.setString(23,  utilBean.getnConvRtSet());
					ps.setString(24,  utilBean.getnCrdAcpAddAdrs());
					ps.setString(25,  utilBean.getnCrdAcptTrmId());
					ps.setString(26,  utilBean.getnCrdAcpZipCd());
					ps.setString(27,  utilBean.getnDtTmLcTxn());
					ps.setString(28,  utilBean.getnFunCd());
					ps.setString(29,  utilBean.getnLtPrsntInd());
					ps.setString(30,  utilBean.getnMTI());
					ps.setString(31,  utilBean.getnPAN());
					ps.setString(32,  utilBean.getnRecNum());
					ps.setString(33,  utilBean.getnRGCSRcvdDt());
					
					ps.setString(34,  utilBean.getnSetDCInd());
					ps.setString(35,  utilBean.getnTxnDesInstCd());
					ps.setString(36,  utilBean.getnTxnOrgInstCd());
					ps.setString(37,  utilBean.getnUID());
					ps.setString(38,  utilBean.getnAmtBil());
					ps.setString(39,  utilBean.getnCcyCdBil());
					ps.setString(40,  utilBean.getnConvRtBil());
					ps.setString(41,  utilBean.getnMsgRsnCd());
					
					ps.setString(42,  utilBean.getnFeeDCInd1());
					ps.setString(43,  utilBean.getnFeeAmt1());
					ps.setString(44,  utilBean.getnFeeCcy1());
					ps.setString(45,  utilBean.getnFeeTpCd1());
					ps.setString(46,  utilBean.getnIntrchngCtg1());
					
					ps.setString(47,  utilBean.getnFeeDCInd2());
					ps.setString(48,  utilBean.getnFeeAmt2());
					ps.setString(49,  utilBean.getnFeeCcy2());
					ps.setString(50,  utilBean.getnFeeTpCd2());
					ps.setString(51,  utilBean.getnIntrchngCtg2());
					ps.setString(52,  utilBean.getnFeeDCInd3());
					ps.setString(53,  utilBean.getnFeeAmt3());
					ps.setString(54,  utilBean.getnFeeCcy3());
					ps.setString(55,  utilBean.getnFeeTpCd3());
					ps.setString(56,  utilBean.getnIntrchngCtg3());
					
					ps.setString(57,  utilBean.getnFeeDCInd4());
					ps.setString(58,  utilBean.getnFeeAmt4());
					ps.setString(59,  utilBean.getnFeeCcy4());
					ps.setString(60,  utilBean.getnFeeTpCd4());
					ps.setString(61,  utilBean.getnIntrchngCtg4());
					ps.setString(62,  utilBean.getnFeeDCInd5());
					ps.setString(63,  utilBean.getFeeAmt5());
					ps.setString(64,  utilBean.getnFeeCcy5());
					ps.setString(65,  utilBean.getnFeeTpCd5());
					ps.setString(66,  utilBean.getnIntrchngCtg5());
					ps.setString(67, utilBean.getnCrdAcpBussCd());
					ps.setString(68, utilBean.getnCrdAcpNm());
					ps.setString(69,  headerUtil.getTrl_nFunCd());
					ps.setString(70,  headerUtil.getTrl_nRecNum());
					
					
					ps.setString(71, "D");
					ps.setString(72,filedate);
					
					ps.addBatch();
					
					utilBean = new RupayUtilBean();
					
					
		            count++;
		            
		            if(count == 100)
					{
						count = 1;
					
						ps.executeBatch();
						System.out.println("Executed batch");
						count++;
					}
					
			//	break;
				
	    	  } 
	    	  
					/*switch (nodeMatcher.group(1)) {
					
					case "Txn" :
						
						 break;
					case"Hdr":
						hdr="hdr";
						break;
					case"/Hdr":
						hdr="";
						break;
				
						 //set headers values which are not common
					case "nDtTmFlGen" :
						  matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
						  headerUtil.setnDtTmFlGen(matcher.group(1));
						 break;
					case "nMemInstCd" :
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
						  headerUtil.setnMemInstCd(matcher.group(1));
						 break;
					case "nUnFlNm" :
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
						 headerUtil.setnUnFlNm(matcher.group(1));
						 break;
					case "nProdCd" :
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
						  headerUtil.setnProdCd(matcher.group(1));
						 break;
					case "nSetBIN" :
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
						  headerUtil.setnSetBIN(matcher.group(1));
						 break;
					case "nFlCatg" :
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
						  headerUtil.setnFlCatg(matcher.group(1));
						 break;
					case "nVerNum" :
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
						  headerUtil.setnVerNum(matcher.group(1));
						 break;	 
						 
						 //set Transaction values
					case "nAcqInstCd":
						  matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
							utilBean.setnAcqInstCd(matcher.group(1));
						break;
						
					case "nAmtSet":
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
						  double amtSet = Integer.parseInt(matcher.group(1));
						  amtSet =  amtSet/100;
						 utilBean.setnAmtSet(String.valueOf(amtSet));
					
						break;
					case "nAmtTxn":
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
						  double amtTxn = Double.parseDouble(matcher.group(1));
						  amtTxn =  amtTxn/100;
						 
						  
						utilBean.setnAmtTxn(String.valueOf(amtTxn));
						
						break;	
					case "nApprvlCd":
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
							utilBean.setnApprvlCd(matcher.group(1));
						
						break;
					case "nARD":
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
							utilBean.setnARD(matcher.group(1));
						
						break;
					case "nCcyCdSet":
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
						
							utilBean.setnCcyCdSet(matcher.group(1));
						
						break;
					case "nCcyCdTxn":
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
							utilBean.setnCcyCdTxn(matcher.group(1));
						
						break;
					case "nConvRtSet":
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
							utilBean.setnConvRtSet(matcher.group(1));
						
						break;
					case "nCrdAcpAddAdrs":
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
							utilBean.setnCrdAcpAddAdrs(matcher.group(1));
						
						break;
					case "nCrdAcptTrmId":
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
							utilBean.setnCrdAcptTrmId(matcher.group(1));
						
						break;
					case "nCrdAcpZipCd":
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
							utilBean.setnCrdAcpZipCd(matcher.group(1));
						
						break;
						//common field for transaction and header
					case "nDtSet":
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
						 switch(hdr) {
						 
						 case "hdr":
							 
							headerUtil.setnDtSet(matcher.group(1));
							break;
						default :
							utilBean.setnDtSet(matcher.group(1));
							break;
						 }
					
						break;
					case "nDtTmLcTxn":
						 matcher = TAG_REGEX.matcher(thisLine);
						  matcher.find();
							utilBean.setnDtTmLcTxn(matcher.group(1));
						
						break;
						//common field for transaction and header
					case "nFunCd":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
						 switch(hdr) {
						 
						 case "hdr":
							headerUtil.setnFunCd(matcher.group(1));
							break;
						 case "Trl":
								trl_nFunCd= matcher.group(1);
								System.out.println(trl_nFunCd);
								
								break;
						
							
						default :
							utilBean.setnFunCd(matcher.group(1));
							break;
						 }
					
						break;
					case "nLtPrsntInd":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnLtPrsntInd(matcher.group(1));
						
						break;
						
						//common field for transaction and header
					case "nMTI":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
						 switch(hdr) {
						 
						 case "hdr":
							 headerUtil.setnMTI(matcher.group(1));
							 break;
						default :
							 utilBean.setnMTI(matcher.group(1));
							
							break;
						 }
					
						break;
					case "nPAN":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnPAN(matcher.group(1));
						
						break;
						//common field for transaction and header
					case "nRecNum":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();	
						 switch(hdr) {
						 
						 case "hdr":
							 headerUtil.setnRecNum(matcher.group(1));
							 break;
						 case "Trl":
							 headerUtil.setTrl_nRecNum(matcher.group(1));
							 trl_nRecNum=matcher.group(1);
							 System.out.println(trl_nRecNum);
							 break;
						default :
							 utilBean.setnRecNum(matcher.group(1));
							break;
						 }
					
						break;
					case "nRGCSRcvdDt":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnRGCSRcvdDt(matcher.group(1));
							
						
						break;
					case "nSetDCInd":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnSetDCInd(matcher.group(1));
						
						break;
					case "nTxnDesInstCd":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnTxnDesInstCd(matcher.group(1));
						
						break;
					case "nTxnOrgInstCd":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnTxnOrgInstCd(matcher.group(1));
						
						break;
					case "nUID":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnUID(matcher.group(1));
						
						break;
					case "nFeeDCInd":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							switch(feesize){
							
							case 1 :
								utilBean.setnFeeDCInd1(matcher.group(1));
								break;
							case 2 :
								System.out.println("setnFeeDCInd2");
								utilBean.setnFeeDCInd2(matcher.group(1));
								break;
							case 3 :
								System.out.println("setnFeeDCInd3");
								utilBean.setnFeeDCInd3(matcher.group(1));
								break;
							case 4 :
								System.out.println("setnFeeDCInd4");
								utilBean.setnFeeDCInd4(matcher.group(1));
								break;
							case 5 :
								System.out.println("setnFeeDCInd5");
								utilBean.setnFeeDCInd5(matcher.group(1));
								break;
							default:
									break;
						}
						break;
					case "nFeeAmt":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
						 switch(feesize){
							
							case 1 :
								utilBean.setnFeeAmt1(matcher.group(1));
								break;
							case 2 :
								System.out.println("setnFeeAmt2");
								utilBean.setnFeeAmt2(matcher.group(1));
								break;
							case 3 :
								System.out.println("setnFeeAmt3");
								utilBean.setnFeeAmt3(matcher.group(1));
								break;
							case 4 :
								System.out.println("setnFeeAmt4");
								utilBean.setnFeeAmt4(matcher.group(1));
								break;
							case 5 :
								System.out.println("setnFeeAmt5");
								utilBean.setFeeAmt5(matcher.group(1));
								break;
							default:
									break;
						}
						break;
					case "nFeeCcy":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
						 switch(feesize){
							
							case 1 :
								
								utilBean.setnFeeCcy1(matcher.group(1));
								break;
							case 2 :
								System.out.println("nFeeCcy2");
								utilBean.setnFeeCcy2(matcher.group(1));
								break;
							case 3 :
								System.out.println("nFeeCcy3");
								utilBean.setnFeeCcy3(matcher.group(1));
								break;
							case 4 :
								System.out.println("nFeeCcy4");
								utilBean.setnFeeCcy4(matcher.group(1));
								break;
							case 5 :
								System.out.println("nFeeCcy5");
								utilBean.setnFeeCcy5(matcher.group(1));
								break;
							default:
									break;
						}
						break;
					case "nFeeTpCd":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
						 switch(feesize){
							
							case 1 :
								utilBean.setnFeeTpCd1(matcher.group(1));
								break;
							case 2 :
								utilBean.setnFeeTpCd2(matcher.group(1));
								break;
							case 3 :
								utilBean.setnFeeTpCd3(matcher.group(1));
								break;
							case 4 :
								utilBean.setnFeeTpCd4(matcher.group(1));
								break;
							case 5 :
								utilBean.setnFeeTpCd5(matcher.group(1));
								break;
							default:
									break;
						}
						break;
					case "nIntrchngCtg":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
						 switch(feesize){
							
							case 1 :
								utilBean.setnIntrchngCtg1(matcher.group(1));
								break;
							case 2 :
								utilBean.setnIntrchngCtg2(matcher.group(1));
								break;
							case 3 :
								utilBean.setnIntrchngCtg3(matcher.group(1));
								break;
							case 4 :
								utilBean.setnIntrchngCtg4(matcher.group(1));
								break;
							case 5 :
								utilBean.setnIntrchngCtg5(matcher.group(1));
								break;
							default:
									break;
						}
						break;
						
						
					case "/Fee" : feesize=feesize+1;
						break;
						
						
					case "nCaseNum":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnCaseNum(matcher.group(1));
						
						break;
					case "nContNum":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnContNum(matcher.group(1));
						
						break;
					case "nFulParInd":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnFulParInd(matcher.group(1));
						
						break;
					case "nProcCd":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnProdCd(matcher.group(1));
						
						break;
					case "nAmtBil":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnAmtBil(matcher.group(1));
						
						break;
					case "nCcyCdBil":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnCcyCdBil(matcher.group(1));
						
						break;
					case "nConvRtBil":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnConvRtBil(matcher.group(1));
						
						break;
					case "nMsgRsnCd":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							utilBean.setnMsgRsnCd(matcher.group(1));
						
						break;
					case "nRnTtlAmt":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							headerUtil.setnRnTtlAmt(matcher.group(1));
							break;
					case "nTxnCnt":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							headerUtil.setnTxnCnt(matcher.group(1));
							transactions_count=matcher.group(1);
							System.out.println(transactions_count);
							break;
							
					case "Trl":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							hdr="Trl";
							
					break;
					case "/Trl":
						 matcher = TAG_REGEX.matcher(thisLine);
						 matcher.find();
							hdr="";
						
							
					break;
						
							
					case "/Txn" : 
						feesize=1;
						ps.setString(1, headerUtil.getnMTI());
						ps.setString(2, headerUtil.getnFunCd());
						ps.setString(3, headerUtil.getnRecNum());
						ps.setString(4, headerUtil.getnMemInstCd());
						ps.setString(5, headerUtil.getnUnFlNm());
						ps.setString(6, headerUtil.getnDtSet());
						ps.setString(7, headerUtil.getnProdCd());
						ps.setString(8, headerUtil.getnSetBIN());
						ps.setString(9, headerUtil.getnFlCatg());
						ps.setString(10, headerUtil.getnVerNum());
						ps.setString(11, null);
						ps.setString(12, null);
						
						ps.setString(13, headerUtil.getnTxnCnt());
						ps.setString(14, headerUtil.getnRnTtlAmt());
						ps.setString(15, utilBean.getnAcqInstCd());
						ps.setString(16,  utilBean.getnAmtSet());
						ps.setString(17,  utilBean.getnAmtTxn());
						ps.setString(18,  utilBean.getnApprvlCd());
						ps.setString(19,  utilBean.getnARD());
						ps.setString(20,  utilBean.getnCaseNum());
						ps.setString(21,  utilBean.getnCcyCdSet());
						ps.setString(22,  utilBean.getnCcyCdTxn());
						
						ps.setString(23,  utilBean.getnConvRtSet());
						ps.setString(24,  utilBean.getnCrdAcpAddAdrs());
						ps.setString(25,  utilBean.getnCrdAcptTrmId());
						ps.setString(26,  utilBean.getnCrdAcpZipCd());
						ps.setString(27,  utilBean.getnDtTmLcTxn());
						ps.setString(28,  utilBean.getnFunCd());
						ps.setString(29,  utilBean.getnLtPrsntInd());
						ps.setString(30,  utilBean.getnMTI());
						ps.setString(31,  utilBean.getnPAN());
						ps.setString(32,  utilBean.getnRecNum());
						ps.setString(33,  utilBean.getnRGCSRcvdDt());
						
						ps.setString(34,  utilBean.getnSetDCInd());
						ps.setString(35,  utilBean.getnTxnDesInstCd());
						ps.setString(36,  utilBean.getnTxnOrgInstCd());
						ps.setString(37,  utilBean.getnUID());
						ps.setString(38,  utilBean.getnAmtBil());
						ps.setString(39,  utilBean.getnCcyCdBil());
						ps.setString(40,  utilBean.getnConvRtBil());
						ps.setString(41,  utilBean.getnMsgRsnCd());
						
						ps.setString(42,  utilBean.getnFeeDCInd1());
						ps.setString(43,  utilBean.getnFeeAmt1());
						ps.setString(44,  utilBean.getnFeeCcy1());
						ps.setString(45,  utilBean.getnFeeTpCd1());
						ps.setString(46,  utilBean.getnIntrchngCtg1());
						
						ps.setString(47,  utilBean.getnFeeDCInd2());
						ps.setString(48,  utilBean.getnFeeAmt2());
						ps.setString(49,  utilBean.getnFeeCcy2());
						ps.setString(50,  utilBean.getnFeeTpCd2());
						ps.setString(51,  utilBean.getnIntrchngCtg2());
						ps.setString(52,  utilBean.getnFeeDCInd3());
						ps.setString(53,  utilBean.getnFeeAmt3());
						ps.setString(54,  utilBean.getnFeeCcy3());
						ps.setString(55,  utilBean.getnFeeTpCd3());
						ps.setString(56,  utilBean.getnIntrchngCtg3());
						
						ps.setString(57,  utilBean.getnFeeDCInd4());
						ps.setString(58,  utilBean.getnFeeAmt4());
						ps.setString(59,  utilBean.getnFeeCcy4());
						ps.setString(60,  utilBean.getnFeeTpCd4());
						ps.setString(61,  utilBean.getnIntrchngCtg4());
						ps.setString(62,  utilBean.getnFeeDCInd5());
						ps.setString(63,  utilBean.getFeeAmt5());
						ps.setString(64,  utilBean.getnFeeCcy5());
						ps.setString(65,  utilBean.getnFeeTpCd5());
						ps.setString(66,  utilBean.getnIntrchngCtg5());
						ps.setString(67,  headerUtil.getTrl_nFunCd());
						ps.setString(68,  headerUtil.getTrl_nRecNum());
						
						ps.setString(69, "D");
						ps.setString(70,filedate);
						
						ps.addBatch();
						
						utilBean = new RupayUtilBean();
						
						
			            count++;
			            
			            if(count == 20000)
						{
							count = 1;
						
							ps.executeBatch();
							System.out.println("Executed batch");
							count++;
						}
						
					break;
					default:
						
					break;
					}
					*/
				        
	    	      
	    	    
	     }
	     ps.executeBatch();
	     
	     	updtps.setString(1, trl_nFunCd);
	     	System.out.println(trl_nFunCd);
			updtps.setString(2, trl_nRecNum);
			System.out.println(trl_nRecNum);
			updtps.setString(3, transactions_count);
			System.out.println(transactions_count);
			System.out.println(update);
			updtps.executeUpdate();
		  System.out.println("Process ended"+ new Date().getTime());
		  br.close();
	       ps.close();
	       updtps.close();
	       con.close();
	       return true;
	     
	     
		}catch(Exception ex){
			
			ex.printStackTrace();
			return false;
		}
		
	}


	
	
	public String chkFlag(String flag, CompareSetupBean setupBean) {

		try {

			ResultSet rs = null;
			String flg = "";
			OracleConn conn = new OracleConn();

			String query = "SELECT "
					+ flag
					+ " FROM MAIN_FILE_UPLOAD_DTLS WHERE to_char(filedate,'dd/mm/yyyy') = to_char(to_date('"
					+ setupBean.getFileDate()
					+ "','dd/mm/yyyy'),'dd/mm/yyyy')  " + " AND CATEGORY = '"
					+ setupBean.getCategory() + "' AND FileId = "
					+ setupBean.getInFileId() + " ";

			query = " SELECT CASE WHEN exists (" + query + ") then (" + query
					+ ") else 'N' end as FLAG from dual";

			System.out.println(query);
			con = conn.getconn();
			st = con.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {

				flg = rs.getString(1);
			}

			return flg;

		} catch (Exception ex) {

			ex.printStackTrace();
			return null;

		} finally {

			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	

    
    public static void main(String []args) {
    	

          
		
		ReadNUploadRupayFile readcbs = new ReadNUploadRupayFile();
		
		
			Scanner scanner = new Scanner(System.in);
			System.out.print("Enter a file path: ");
			System.out.flush();
			String filename = scanner.nextLine();
			System.out.print("Enter a file date in (dd/MM/yyyy) format: ");
			String filedate = scanner.nextLine();
			
			File file = new File(filename);
			
			
			System.out.println(file.getName());
			
			if(readcbs.uploadCBSData(file.getName(),filedate,file.getPath()))
			{
				System.out.println("File uploaded successfully");
			}
			else
			{
				System.out.println("File uploading failed");
			}
			
			/*
			File f = new File("\\\\10.143.11.50\\led\\DCRS\\RUPAYDOMESTIC");
			if(!(f.exists())) {
				
				if(f.mkdir()) {
					
					System.out.println("directory created");
				}
			}
			*/
			
			/*if(file.renameTo(new File("\\\\10.143.11.50\\led\\DCRS\\RUPAYDOMESTIC\\" + file.getName()))) {
				
				System.out.println("File Moved Successfully");
				
				readcbs.uploadCBSData(file.getName(),filedate);
				
				System.out.println("Process Completed");
				
			}else {
				
				System.out.println("Error Occured while moving file");
			}*/
			
			//readcbs.uploadCBSData(file.getName(),filedate);
			
			
	    
    }
    
    



}
