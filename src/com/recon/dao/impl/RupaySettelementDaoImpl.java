package com.recon.dao.impl;

import static com.recon.util.GeneralUtil.GET_FILE_ID;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.recon.dao.ManualFileDao;
import com.recon.dao.RupaySettelementDao;
import com.recon.model.ConfigurationBean;
import com.recon.model.RupaySettlementBean;
import com.recon.model.RupayUploadBean;
import com.recon.util.ReadNCMCDSCRFile;
import com.recon.util.ReadRupayBillingReport;
import com.recon.util.ReadRupayDSCRFile;
import com.recon.util.ReadRupayIntBillingReport;
import com.recon.util.ReadRupayIntDSCRFile;
import com.recon.util.ReadRupayIntInterchangeReport;
import com.recon.util.ReadRupayInterchangeReport;
import com.recon.util.ReadRupaySettlementFile;
import com.recon.util.RupayHeaderUtil;
import com.recon.util.RupayUtilBean;


import static com.recon.util.GeneralUtil.GET_FILE_ID;

@Component
public class RupaySettelementDaoImpl extends JdbcDaoSupport implements RupaySettelementDao {

	private static final String O_ERROR_MESSAGE = "o_error_message";
	
	@Override
	public String uploadRupaySettlementData(List<RupaySettlementBean> list,RupaySettlementBean beanObj) {
		RupaySettlementBean bean=null;
		String result="";
		int maxSrNo=0;
		PreparedStatement ps = null;
		Connection con = null;
		ResultSet rs = null;
		String sql="";
		int[] inserted = null;
		try {
			/*
			 * if(list.size()>0) { RupaySettlementBean b=list.get(0); String
			 * SETTLEMENT_DATE="02-01-2021"; String
			 * queryOfSrNo="select max(a.SR_NO) from RUPAY_SETTLEMENT_DATA a where a.SETTLEMENT_DATE=?"
			 * ; maxSrNo = getJdbcTemplate().queryForObject(queryOfSrNo, new
			 * Object[]{SETTLEMENT_DATE},Integer.class);
			 * 
			 * }
			 */
			
			/*
			 * sql="insert into RUPAY_SETTLEMENT_DATA values('"+bean.getSettlementDate()+
			 * "','"+bean.getBankName()+"','"+bean.getMemberName()+"','"+bean.
			 * getMemberBankPid()+"','"+bean.getDrcr()+"','"+
			 * bean.getSumCr()+"','"+bean.getSumDr()+"','"+bean.getNetSum()+"',"+bean.
			 * getCycle()+",'"+bean.getCreatedBy()+"',sysdate,"+bean.getSrNo()+",to_date('"+
			 * beanObj.getDatepicker()+"','dd/mm/yyyy'))";
			 */
			
		
		sql="insert into RUPAY_SETTLEMENT_DATA values(?,?,?,?,?,?,?,?,?,?,sysdate,?,to_date('"+beanObj.getDatepicker()+"','dd/mm/yyyy'))";
				
		con = getConnection();
		con.setAutoCommit(false);
		ps = con.prepareStatement(sql);
		
		for(int i=0;i<list.size();i++) {
			bean=list.get(i);
			
			ps.setString(1, bean.getSettlementDate());
			ps.setString(2, bean.getBankName());
			ps.setString(3, bean.getMemberName());
			ps.setString(4, bean.getMemberBankPid());
			ps.setString(5, bean.getDrcr());
			ps.setString(6, bean.getSumCr());
			ps.setString(7, bean.getSumDr());
			ps.setString(8, bean.getNetSum());
			ps.setInt(9, bean.getCycle());
			ps.setString(10, beanObj.getCreatedBy());
			ps.setInt(11, bean.getSrNo());
			 ps.addBatch();
		}
		long start = System.currentTimeMillis();
		System.out.println("  start insert batch" + start);
		inserted = ps.executeBatch();
		System.out.println("  executin batch" + inserted);
		long end = System.currentTimeMillis();
		System.out.println("  end insert batch " + end);
		con.commit();
		
		 result="success";
		}catch(Exception e) {
			result="failed";
		e.printStackTrace();
		try {
			con.rollback();
			
		} catch (Exception e2) {
			// TODO: handle exception
		}
	}finally {
		try {
			if(ps!=null) {
			ps.close();
			}
			if(con!=null) {
			con.close();
			}
			} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
		return result;
	}

	@Override
	public HashMap<String, Object> validatePrevFileUpload(RupaySettlementBean beanObj) {
		HashMap<String, Object> validate = new HashMap<String, Object>();
		try
		{
			int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
			System.out.println("File id is "+file_id);
			String checkTotalCount = "select count(*) from main_settlement_file_upload where category = ? and file_subcategory = ? and fileid = ?";
			int totalCount = getJdbcTemplate().queryForObject(checkTotalCount, new Object[] {beanObj.getCategory(),beanObj.getStSubCategory(),file_id},Integer.class);
			if(totalCount >0)
			{
				//check for selected filedate
				String checkForSameDate = "select count(*) from main_settlement_file_upload where fileid = ? and cycle =? and filedate =  to_date(?,'dd/mon/yyyy')";
				int dataCount = getJdbcTemplate().queryForObject(checkForSameDate,new Object[] {file_id,beanObj.getCycle(),beanObj.getDatepicker()},Integer.class);
				if(dataCount >0)
				{
					validate.put("result", true);
					validate.put("msg", "File for selected date is already uploaded !!!");
				}
				else
				{
					validate.put("result", false);
				}
				//COMMENTED PART IS VALIDATION FOR PREVIOUS DAY FILE UPLOAD
				/*else
				{
					String checkPrevFile = "SELECT COUNT(*) FROM MAIN_SETTLEMENT_FILE_UPLOAD WHERE FILEID = ? AND FILEDATE =  TO_DATE(?,'DD/MON/YYYY')-1";
					int Uploadcount = getJdbcTemplate().queryForObject(checkPrevFile, new Object[] {file_id,beanObj.getDatepicker()},Integer.class);
					System.out.println("Upload Count is "+Uploadcount);

					if(Uploadcount<=0)
					{
						validate.put("result", true);
						validate.put("msg", "Previos date file is not uploaded");
					}
					else
						validate.put("result", false);
				}*/
			}
			else
			{
				System.out.println("Its first time upload");
				validate.put("result", false);
			}

		}
		catch(Exception e)
		{
			System.out.println("Exception in RupaySettlementServiceImpl: validatePrevFileUpload "+e);
			validate.put("result", true);
			validate.put("msg", "Exception Occured!!");
		}
		return validate;
	}

	@Override
	public HashMap<String, Object> updateFileSettlement(RupaySettlementBean beanObj,int count) {
		HashMap<String, Object> mapObj = new HashMap<String, Object>();
		int file_id = getJdbcTemplate().queryForObject(GET_FILE_ID, new Object[] { beanObj.getFileName(),beanObj.getCategory(),beanObj.getStSubCategory() },Integer.class);
		System.out.println("File id is "+file_id);
		String insertData = "INSERT INTO MAIN_SETTLEMENT_FILE_UPLOAD(FILEID, FILEDATE, UPLOADBY, UPLOADDATE, CATEGORY, UPLOAD_FLAG, FILE_SUBCATEGORY,CYCLE,PROCESS_FLAG,FILE_COUNT) " + 
				"VALUES('"+file_id+"',TO_DATE('"+beanObj.getDatepicker()+"','DD/MM/YYYY'),'"+beanObj.getCreatedBy()+"',sysdate,'"+beanObj.getCategory()+"','Y','"+beanObj.getStSubCategory()+"',"
						+ "'"+beanObj.getCycle()+"','N',1)";
		System.out.println("insertData======");
		System.out.println(insertData);
		getJdbcTemplate().execute(insertData);
		mapObj.put("entry", true);
		return mapObj;
	}

	@Override
	public HashMap<String, List<RupaySettlementBean>> getTTUMData(String settlementDate) {
		/*
		 * Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
		 * 
		 * PreparedStatement ps2 = null; ResultSet rs2 = null;
		 */
		RupaySettlementBean bean=null;
		HashMap<String, List<RupaySettlementBean>> map=new HashMap<String, List<RupaySettlementBean>>();
		List<RupaySettlementBean> datListWithoutTotal=new ArrayList<RupaySettlementBean>();
		List<RupaySettlementBean> datListWithTotal=new ArrayList<RupaySettlementBean>();
		try {
		String sql="SELECT a.BANK_PID,a.ACCOUNT_NO,b.MEMBER_BANK_PID,b.NET_SUM,b.DRCR,b.SETTLEMENT_DATE,b.CYCLE FROM MASTER_DATA_RUPAY a"
				+ " inner join RUPAY_SETTLEMENT_DATA b "
				+ " on a.BANK_PID=b.MEMBER_BANK_PID where b.MEMBER_BANK_PID !='TOTAL' and b.SETTLEMENT_DATE='"+settlementDate+"' order by b.SR_NO asc";
		datListWithoutTotal=(ArrayList<RupaySettlementBean>) getJdbcTemplate().query(sql,new BeanPropertyRowMapper(RupaySettlementBean.class));
		
		String sqlTotal="SELECT a.BANK_PID,a.ACCOUNT_NO,b.MEMBER_BANK_PID,b.NET_SUM,b.DRCR,b.SETTLEMENT_DATE,b.CYCLE FROM MASTER_DATA_RUPAY a"
				+ " inner join RUPAY_SETTLEMENT_DATA b "
				+ " on a.BANK_PID=b.MEMBER_BANK_PID where b.MEMBER_BANK_PID ='TOTAL' and b.SETTLEMENT_DATE='"+settlementDate+"' order by b.CYCLE asc";
		datListWithTotal=(ArrayList<RupaySettlementBean>) getJdbcTemplate().query(sqlTotal,new BeanPropertyRowMapper(RupaySettlementBean.class));
		
		map.put("datListWithoutTotal", datListWithoutTotal);
		map.put("datListWithTotal", datListWithTotal);
			/*
			 * con = getConnection(); ps = con.prepareStatement(query); rs =
			 * ps.executeQuery(); while(rs.next()) { String bankId=rs.getString(1); String
			 * accountNo=rs.getString(2); String netSum=rs.getString(3); String
			 * drcr=rs.getString(4); bean=new RupaySettlementBean();
			 * bean.setMemberBankPid(bankId); bean.setAccountNo(accountNo);
			 * bean.setNetSum(netSum); bean.setDrcr(drcr); datList.add(bean); }
			 */
		}catch(Exception e) {
			e.printStackTrace();
		} /*
			 * finally { if(rs!=null){ try { rs.close(); } catch (SQLException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } } if(ps!=null){ try {
			 * ps.close(); } catch (SQLException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } } if(con!=null){ try { con.close(); } catch
			 * (SQLException e) { // TODO Auto-generated catch block e.printStackTrace(); }
			 * } }
			 */
		return map;
	}

	public boolean readRupayChargeback(RupayUploadBean beanObj,MultipartFile file)
	{
		logger.info("***** ReadRupay.uploadDomesticData Start ****");
		String insert ="INSERT  INTO RUPAY_CHARGEBACK_RAWDATA (MTI,Function_Code ,Record_Number,Member_Institution_ID_Code,Unique_File_Name,Date_Settlement,Product_Code,Settlement_BIN,File_Category,Version_Number,"
				+ "Entire_File_Reject_Indicator,File_Reject_Reason_Code,Transactions_Count,Run_Total_Amount,"
				+ "Acquirer_Institution_ID_code,Amount_Settlement,Amount_Transaction,Approval_Code,Acquirer_Reference_Data,Case_Number,Currency_Code_Settlement,Currency_Code_Transaction,Conversion_Rate_Settlement,"
				+ "Card_Acceptor_Addi_Addr,Card_Acceptor_Terminal_ID,Card_Acceptor_Zip_Code,DateandTime_Local_Transaction,TXNFunction_Code ,Late_Presentment_Indicator,TXNMTI,Primary_Account_Number,TXNRecord_Number,"
				+ "RGCS_Received_date,Settlement_DR_CR_Indicator,Txn_Desti_Insti_ID_code,Txn_Origin_Insti_ID_code,Card_Holder_UID,Amount_Billing,Currency_Code_Billing,Conversion_Rate_billing,Message_Reason_Code,"
				+ "Fee_DR_CR_Indicator1,Fee_amount1,Fee_Currency1,Fee_Type_Code1,Interchange_Category1,Fee_DR_CR_Indicator2,Fee_amount2,Fee_Currency2,Fee_Type_Code2,Interchange_Category2,Fee_DR_CR_Indicator3,Fee_amount3,"
				+ "Fee_Currency3,Fee_Type_Code3,Interchange_Category3,Fee_DR_CR_Indicator4,Fee_amount4,Fee_Currency4,Fee_Type_Code4,Interchange_Category4,Fee_DR_CR_Indicator5,Fee_amount5,"
				+ "Fee_Currency5,Fee_Type_Code5,Interchange_Category5,"
				//			+ "MCC_Code,Merchant_Name," //added MCC_Code,Merchant_Name by int6261 date 03/04/2018
				+ "Trl_FUNCTION_CODE,Trl_RECORD_NUMBER,flag,FILEDATE,PAN,CREATEDDATE,createdby,CYCLE) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'dd/mm/yyyy'),?,SYSDATE,?,?)";

		String update="update RUPAY_CHARGEBACK_RAWDATA  set Trl_FUNCTION_CODE = ? , TRL_RECORD_NUMBER= ?,TRANSACTIONS_COUNT=? where to_char(CREATEDDATE,'dd-mm-yy')=to_char(sysdate,'dd-mm-yy')";
		String trl_nFunCd=null, trl_nRecNum=null,transactions_count=null; 

		//String filepath = "\\\\10.144.143.191\\led\\DCRS\\Rupay\\RAW FILES\\07 Sept 17\\International\\011IBKL25900021725002.xml";
		FileInputStream fis;
		//String filename	= "rupay.txt";

		int feesize=1;
		try{


			//fis = new FileInputStream("\\\\10.143.11.50\\led\\DCRS\\RUPAYDOMESTIC\\"+ filename);
			//fis = new FileInputStream(filePath);

			Connection con = getConnection();
			//FileInputStream fis= new FileInputStream(new File(filename));
			PreparedStatement ps = con.prepareStatement(insert);
			PreparedStatement updtps = con.prepareStatement(update);

			final Pattern TAG_REGEX = Pattern.compile(">(.+?)</");
			final Pattern node_REGEX = Pattern.compile("<(.+?)>");
			Matcher matcher;

			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			String thisLine = null;
			int count=1;
			String hdr="",trl="";
			RupayUtilBean utilBean = new RupayUtilBean(); ;
			RupayHeaderUtil headerUtil = new RupayHeaderUtil();
			logger.info("Process started"+ new Date().getTime());
			while ((thisLine = br.readLine()) != null) {


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
					// matcher.matches();
					System.out.println(matcher.group(1));
					System.out.println("count ::> "+count);
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
						logger.info(trl_nFunCd);
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
						logger.info(trl_nRecNum);
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
				//    	 new parameter added by int6261 03/04/2018 
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
						logger.info("setnFeeDCInd2");
						utilBean.setnFeeDCInd2(matcher.group(1));
						break;
					case 3 :
						logger.info("setnFeeDCInd3");
						utilBean.setnFeeDCInd3(matcher.group(1));
						break;
					case 4 :
						logger.info("setnFeeDCInd4");
						utilBean.setnFeeDCInd4(matcher.group(1));
						break;
					case 5 :
						logger.info("setnFeeDCInd5");
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
						logger.info("setnFeeAmt2");
						utilBean.setnFeeAmt2(matcher.group(1));
						break;
					case 3 :
						logger.info("setnFeeAmt3");
						utilBean.setnFeeAmt3(matcher.group(1));
						break;
					case 4 :
						logger.info("setnFeeAmt4");
						utilBean.setnFeeAmt4(matcher.group(1));
						break;
					case 5 :
						logger.info("setnFeeAmt5");
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
						logger.info("nFeeCcy2");
						utilBean.setnFeeCcy2(matcher.group(1));
						break;
					case 3 :
						logger.info("nFeeCcy3");
						utilBean.setnFeeCcy3(matcher.group(1));
						break;
					case 4 :
						logger.info("nFeeCcy4");
						utilBean.setnFeeCcy4(matcher.group(1));
						break;
					case 5 :
						logger.info("nFeeCcy5");
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
					logger.info(transactions_count);
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

					String pan = utilBean.getnPAN().trim();
					String Update_Pan="";		
					if(pan.length() <= 16 && pan !=null && pan.trim()!="" && pan.length()>0 ) {
						// System.out.println(pan);
						Update_Pan =  pan.substring(0, 6) +"XXXXXX"+ pan.substring(pan.length()-4);

					}else if (pan.length() >= 16 && pan !=null && pan.trim()!="" && pan.length()>0) {

						Update_Pan =  pan.substring(0, 6) +"XXXXXXXXX"+ pan.substring(pan.length()-4);

					} else {

						Update_Pan =null;
					}
					ps.setString(31,  Update_Pan);
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
					//				ps.setString(67, utilBean.getnCrdAcpBussCd());
					//				ps.setString(68, utilBean.getnCrdAcpNm());
					ps.setString(67,  headerUtil.getTrl_nFunCd());
					ps.setString(68,  headerUtil.getTrl_nRecNum());


					ps.setString(69, "D");
					ps.setString(70,beanObj.getFileDate());
					/// added by int 8624 on 06- MAY
					ps.setString(71, pan);
					ps.setString(72, beanObj.getCreatedBy());
					ps.setString(73, beanObj.getCycle());

					ps.addBatch();

					utilBean = new RupayUtilBean();


					count++;

					if(count == 10000)
					{
						count = 1;

						ps.executeBatch();
						logger.info("Executed batch");
						count++;
					}

					//	break;

				} 
			}
			ps.executeBatch();

			updtps.setString(1, trl_nFunCd);
			logger.info(trl_nFunCd);
			updtps.setString(2, trl_nRecNum);
			logger.info(trl_nRecNum);
			updtps.setString(3, transactions_count);
			logger.info(transactions_count);
			logger.info(update);
			updtps.executeUpdate();
			logger.info("Process ended"+ new Date().getTime());
			br.close();
			ps.close();
			updtps.close();
			con.close();

			logger.info("***** ReadRupay.uploadDomesticData End ****");

			return true;


		}catch(Exception ex){

			logger.error(" error in ReadRupay.uploadDomesticData ", new Exception(" ReadRupay.uploadDomesticData ",ex));
			return false;
		}

	}
	
	public HashMap<String, Object> readRupayFiles(RupayUploadBean beanObj,MultipartFile file)
	{
		HashMap<String, Object> output = null;
		try
		{
			if(beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("DSCR"))
			{
				ReadRupayDSCRFile fileRead = new ReadRupayDSCRFile();
				output = fileRead.fileupload(beanObj, file, getConnection());
				//fileRead.PBGBfileupload(beanObj, file, getConnection()); //pbgb reading block
			}
			else if(beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("INTERCHANGE"))
			{
				ReadRupayInterchangeReport fileRead = new ReadRupayInterchangeReport();
				output = fileRead.fileupload(beanObj, file, getConnection());
			}
			else if(beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("SETTLEMENT"))
			{
				ReadRupaySettlementFile fileRead = new ReadRupaySettlementFile();
				output = fileRead.fileupload(beanObj, file, getConnection());
			}			
			else //if(beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("BILLING"))
			{
				logger.info("Interchange File name");
				ReadRupayBillingReport fileRead = new ReadRupayBillingReport();
				output = fileRead.fileupload(beanObj, file, getConnection());
				//fileRead.PBGBfileupload(beanObj, file, getConnection());
			}
			
			return output;
		}
		catch(Exception e)
		{
			System.out.println("EXception in readDSCR file "+e);
			output.put("result", false);
			output.put("count", 0);
			return output;
		}
	}
	
	public HashMap<String, Object> readRupayIntFiles(RupayUploadBean beanObj,MultipartFile file)
	{
		HashMap<String, Object> output = null;
		try
		{
			if(beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("DSCR"))
			{
				ReadRupayIntDSCRFile fileRead = new ReadRupayIntDSCRFile();
				output = fileRead.fileupload(beanObj, file, getConnection());
			}
			else if(beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("INTERCHANGE"))
			{
				ReadRupayIntInterchangeReport fileRead = new ReadRupayIntInterchangeReport();
				output = fileRead.fileupload(beanObj, file, getConnection());
			}
			else //if(beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("BILLING"))
			{
				logger.info("Interchange File name");
				ReadRupayIntBillingReport fileRead = new ReadRupayIntBillingReport();
				output = fileRead.fileupload(beanObj, file, getConnection());
			}
			
			return output;
		}
		catch(Exception e)
		{
			System.out.println("EXception in readDSCR file "+e);
			output.put("result", false);
			output.put("count", 0);
			return output;
		}
	}
	
	public boolean checkFileUploaded(RupayUploadBean beanObj)
	{
		String tableName = null;
		try
		{
			if(beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC"))
			{
				tableName = "RUPAY_"+beanObj.getFileName()+"_RAWDATA";
			}
			else
			{
				tableName = "RUPAY_INT_"+beanObj.getFileName()+"_RAWDATA";
			}
	
			int recordCount = getJdbcTemplate().queryForObject("SELECT count(1) FROM "+tableName.toLowerCase()+" WHERE filedate = str_TO_DATE(?,'%Y/%m/%d') AND cycle = ?",
					new Object[] {beanObj.getFileDate(),beanObj.getCycle()},Integer.class);
			if(recordCount > 0)
				return true;
			else
				return false;
		}
		catch(Exception e)
		{
			logger.info("Exception in checkFileUploaded "+e );
			return false;
		}
	}

	/***************** VALIDATION FOR PROCESSING ********************/
	
	public HashMap<String , Object> validateRawfiles(RupayUploadBean beanObj)
	{
		HashMap<String , Object> output = new HashMap<String, Object>();
		try
		{
			String tableName = getJdbcTemplate().queryForObject("select tablename from main_filesource where filename = 'RUPAY' AND file_subcategory = ?", 
							new Object[] {beanObj.getSubcategory()},String.class);
			
			/*String checkRawData = "select count(cycle) from " + 
					"(SELECT DISTINCT SUBSTR(UNIQUE_FILE_NAME,3,1) as cycle FROM RUPAY_RUPAY_RAWDATA WHERE FILEDATE =?)";*/
			
			logger.info("Flag is "+beanObj.getSubcategory().substring(0, 1));
			
			int rawCount = getJdbcTemplate().queryForObject("select count(*) from rupay_rupay_rawdata where filedate = str_to_date(?,'%Y/%m/%d') and substring(unique_file_name,3,1) = ? and flag = ?"
						, new Object[] {beanObj.getFileDate(), beanObj.getCycle(),beanObj.getSubcategory().substring(0, 1)},Integer.class);
			
			/*int requiredCycle = getJdbcTemplate().queryForObject("select file_count from main_filesource where filename = 'RUPAY' AND FILE_SUBCATEGORY = ?", 
								new Object [] {beanObj.getSubcategory()},Integer.class);*/
			
			int disputeData = getJdbcTemplate().queryForObject("select count(1) from rupay_network_adjustment where"
						+ " filedate = str_to_date(?,'%Y/%m//%d') and cycle = ?", new Object[]{beanObj.getFileDate(), beanObj.getCycle()},
							Integer.class);
			if(rawCount == 0)
			{
				if(disputeData > 0)
				{
					output.put("result", false);
					output.put("msg", "Raw data is not uploaded for selected date and cycle");
				}
				else
				{
					output.put("result", true);
				}
				
			}
			else
			{
				output.put("result", true);
			}
			
		}
		catch(Exception e)
		{
			logger.info("Exception in RupaySettlementDaoImpl "+e);
			output.put("result", false);
			output.put("msg", "Exception Occurred while validating Raw files");
		}
		return output;
	}
	
	public HashMap<String , Object> validateSettlementFiles(RupayUploadBean beanObj)
	{
		HashMap<String , Object> output = new HashMap<String, Object>();
		try
		{
			int checkBilling = 0, checkInterchange= 0, checkDSCR = 0;
			//int checkdisp = 0, checkncmcdis = 0; //for UCO
			/*int checkCBK = getJdbcTemplate().queryForObject("select count(*) from rupay_chargeback_rawdata where filedate = ? and cycle = ?",
						new Object[] {beanObj.getFileDate(), beanObj.getCycle()},Integer.class);*/
			/*checkdisp = getJdbcTemplate().queryForObject("select count(*) from rupay_network_adjustment where filedate = ? and cycle = ?",
					new Object[] {beanObj.getFileDate(), beanObj.getCycle()},Integer.class);
			
			checkncmcdis = getJdbcTemplate().queryForObject("select count(*) from rupay_NCMC_network_adjustment where filedate = ? and cycle = ?",
					new Object[] {beanObj.getFileDate(), beanObj.getCycle()},Integer.class);*/
			
			if(beanObj.getSubcategory().equalsIgnoreCase("Domestic"))
			{
				checkBilling = getJdbcTemplate().queryForObject("select count(*) from rupay_billing_rawdata where filedate = str_to_Date(?,'%Y/%m/%d') and cycle = ?",
						new Object[] {beanObj.getFileDate(),beanObj.getCycle()},Integer.class);

				/*checkInterchange = getJdbcTemplate().queryForObject("select count(*) from rupay_interchange_rawdata where filedate = ? and cycle = ?", 
						new Object[] {beanObj.getFileDate(), beanObj.getCycle()},Integer.class);*/

				checkDSCR = getJdbcTemplate().queryForObject("select count(*) from rupay_dscr_rawdata where filedate = str_to_Date(?,'%Y/%m/%d') and cycle = ?",
						new Object[] {beanObj.getFileDate(), beanObj.getCycle()},Integer.class);
				
				/*checkNCMCDSCR = getJdbcTemplate().queryForObject("select count(*) from ncmc_dscr_rawdata where filedate = ? and cycle = ?",
						new Object[] {beanObj.getFileDate(), beanObj.getCycle()},Integer.class);*/
			}
			else
			{

				 checkBilling = getJdbcTemplate().queryForObject("select count(*) from rupay_int_billing_rawdata where filedate = str_to_Date(?,'%Y/%m/%d') and cycle = ?",
						new Object[] {beanObj.getFileDate(),beanObj.getCycle()},Integer.class);
				
				/* checkInterchange = getJdbcTemplate().queryForObject("select count(*) from rupay_int_interchange_rawdata where filedate = ? and cycle = ?", 
						new Object[] {beanObj.getFileDate(), beanObj.getCycle()},Integer.class);*/
				
				 checkDSCR = getJdbcTemplate().queryForObject("select count(*) from rupay_int_dscr_rawdata where filedate = str_to_Date(?,'%Y/%m/%d') and cycle = ?",
						new Object[] {beanObj.getFileDate(), beanObj.getCycle()},Integer.class);
				
			}
			
			/*if(checkdisp == 0)
			{
				output.put("result", false);
				output.put("msg", "Rupay Dispute file is not uploaded for selected Date and cycle");
			}
			else if(checkncmcdis == 0)
			{
				output.put("result", false);
				output.put("msg", "NCMC Dispute file is not uploaded for selected Date and cycle");
			}*/
			if(checkBilling == 0)
			{
				output.put("result", false);
				output.put("msg", "NPCI Billing file is not uploaded for selected Date and cycle");
			}
			/*else if(checkInterchange == 0)
			{
				output.put("result", false);
				output.put("msg", "Interchange file is not uploaded for selected Date and cycle");
			}*/
			else if(checkDSCR == 0)
			{
				output.put("result", false);
				output.put("msg", "Rupay DSCR file is not uploaded for selected Date and cycle");
			}			
			else
			{
				output.put("result", true);
			}
			
			
		}
		catch(Exception e)
		{
			logger.info("Exception while validating chargeback and rest 4 files");
			output.put("result", false);
			output.put("msg", "Exception while validating Chargeback and rest 4 files");
		}
		return output;
	}
	
	public boolean processSettlement(RupayUploadBean beanObj)
	{
		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		
		try
		{
			RupaySettlementProc rollBackexe = new RupaySettlementProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			inParams.put("SUBCATEGORY", beanObj.getSubcategory());
			inParams.put("ENTERED_CYCLE", beanObj.getCycle());
			outParams = rollBackexe.execute(inParams);

			if(outParams !=null && outParams.get("msg") != null)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		catch(Exception e)
		{
			logger.info("Exception in processSettlement "+e);
			return false;
		}
	}	
	
	private class RupaySettlementProc extends StoredProcedure{
		private static final String insert_proc = "rupay_settlement_process";
		public RupaySettlementProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
			declareParameter(new SqlParameter("SUBCATEGORY",Types.VARCHAR));
			declareParameter(new SqlParameter("ENTERED_CYCLE",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}
	
	/***************** RUPAY SETTLEMENT REPORT DOWNLOAD CODE **********************/
	public boolean validateSettlementProcess(RupayUploadBean beanObj)
	{
		try
		{
			String checkProcess = "select count(1) from rupay_settlement_report where filedate = str_to_date(?,'%Y/%m/%d') AND CYCLE  = ? and subcategory = ?";
			int processCount = getJdbcTemplate().queryForObject(checkProcess, new Object[] {beanObj.getFileDate(), beanObj.getCycle(), beanObj.getSubcategory()}, Integer.class);
			
			if(processCount > 0)
				return true;
			else 
				return false;
		}
		catch(Exception e)
		{
			logger.info("Exception in validateSettlementProcess "+e);
			return false;
		}
	}
	
	public  List<Object> getSettlementData(RupayUploadBean beanObj)
	{

		List<Object> data = new ArrayList<Object>();
		final List<String> cols  = getColumnList("rupay_dhana_settlement_report");
		
		String getData = "select * from  rupay_dhana_settlement_report where filedate = ?  and cycle = ? and subcategory = ? order by sr_no";
		
		List<Object> settlementData = getJdbcTemplate().query(getData, new Object[] {beanObj.getFileDate(),beanObj.getCycle(),  beanObj.getSubcategory()}, new ResultSetExtractor<List<Object>>(){
			public List<Object> extractData(ResultSet rs)throws SQLException {
				List<Object> beanList = new ArrayList<Object>();
				
				while (rs.next()) {
					Map<String, String> table_Data = new HashMap<String, String>();
					for(String column: cols)
					{
						table_Data.put(column, rs.getString(column.replace(" ", "_")));
					}
					beanList.add(table_Data);
				}
				return beanList;
			}
		});
		
		data.add(cols);
		data.add(settlementData);
		
		return data;
	
	}
	
	public ArrayList<String> getColumnList(String tableName) {

		//String query = "SELECT REPLACE(column_name,'_',' ') FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%' and column_name not in('FILEDATE','SETTLEMENT_DATE','CREATEDDATE','CREATEDBY','FILE_TYPE') ";
		
		String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = Database() AND TABLE_NAME = '"+tableName.toLowerCase()+"' "
				+"and column_name not in('filedate','settlement_date','createddate','createdby','file_type')";
				
		System.out.println(query);


		ArrayList<String> typeList= (ArrayList<String>) getJdbcTemplate().query(query, new RowMapper<String>(){
			public String mapRow(ResultSet rs, int rowNum) 
					throws SQLException {
				return rs.getString(1);
			}
		});

		System.out.println(typeList);
		return typeList;

	}
	
	public ArrayList<String> getTTUMColumnList(String tableName) {

		String query = "SELECT REPLACE(column_name,'_',' ') FROM   all_tab_cols WHERE  table_name = '"+tableName.toUpperCase()+"' and column_name not like '%$%' and column_name not in('FILEDATE','SETTLEMENT_DATE','CREATEDDATE','CREATEDBY','SR_NO')";
		System.out.println(query);


		ArrayList<String> typeList= (ArrayList<String>) getJdbcTemplate().query(query, new RowMapper<String>(){
			public String mapRow(ResultSet rs, int rowNum) 
					throws SQLException {
				return rs.getString(1);
			}
		});

		System.out.println(typeList);
		return typeList;

	}
	
/********************* RUPAY SETTLEMENT TTUM **************************/
	public Boolean validateSettlementTTUM(RupayUploadBean beanObj)
	{
		try
		{
			String checkSettlementTTUM = "Select count(1) from RUPAY_SETTLEMENT_TTUM WHERE FILEDATE = str_to_date(?,'%Y/%m/%d') AND CYCLE = ? and subcategory = ?";
			int getCountTTUM = getJdbcTemplate().queryForObject(checkSettlementTTUM, new Object[] {beanObj.getFileDate(), beanObj.getCycle(), beanObj.getSubcategory()}, Integer.class);
			
			if(getCountTTUM > 0)
			{
				return true;
			}
			else
				return false;
			
		}
		catch(Exception e)
		{
			logger.info("Exception in validateSettlementTTUM "+e);
			return false;
		}
	}
	
	public List<Object> getSettlementTTUMData(RupayUploadBean beanObj)
	{
		List<Object> total_data = new ArrayList<Object>();
		
		/*String getData = "select RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
				+"LPAD(nvl(TRANSACTION_AMOUNT,0),17,' ') as TRANSACTION_AMOUNT,"+
				"rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REMARKS,' '),16,' ') AS REMARKS"
				+",to_char(SYSDATE,'DD/MM/YYYY') AS FILEDATE, CYCLE "
				+ " from  RUPAY_SETTLEMENT_TTUM where FILEDATE = TO_DATE(?,'DD/MM/YYYY') and cycle = ? and subcategory = ? order by sr_no";*/
		
		String getData = "select RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
				/*+"CASE WHEN LENGTH(substr(transaction_amount,instr(TRANSACTION_AMOUNT,'.',1,1)+1,length(transaction_amount))) = 1 "
				+ "THEN "
				+ " LPAD(NVL(TRANSACTION_AMOUNT||'0',0),17,' ') "
				+ "ELSE "
				+ "   LPAD(nvl(TRANSACTION_AMOUNT,0),17,' ') "
				+ "END as TRANSACTION_AMOUNT,"+*/
				+"LPAD(nvl(TRANSACTION_AMOUNT,0),17,' ') AS TRANSACTION_AMOUNT, "
				+"rpad(TRANSACTION_PARTICULAR,30,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REMARKS,' '),16,' ') AS REMARKS"
				+",to_char(SYSDATE,'DD/MM/YYYY') AS FILEDATE, CYCLE "
				+ " from  RUPAY_SETTLEMENT_TTUM where FILEDATE = TO_DATE(?,'DD/MM/YYYY') and cycle = ?"
				+ " and (transaction_amount > 0 OR transaction_amount < 0) and subcategory = ? order by sr_no";
		
		List<Object> settlementData = getJdbcTemplate().query(getData, new Object[] {beanObj.getFileDate(),beanObj.getCycle(), beanObj.getSubcategory()}, new ResultSetExtractor<List<Object>>(){
			public List<Object> extractData(ResultSet rs)throws SQLException {
				List<Object> beanList = new ArrayList<Object>();
				
				while (rs.next()) {
					Map<String, String> table_Data = new HashMap<String, String>();
					table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
					table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
					table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
					table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
					table_Data.put("REMARKS", rs.getString("REMARKS"));
					table_Data.put("FILEDATE", rs.getString("FILEDATE"));
					table_Data.put("CYCLE", rs.getString("CYCLE"));
					
					beanList.add(table_Data);
				}
				return beanList;
			}
		});
		
		//DRM DATA
		/*getData = "select RPAD(ACCOUNT_NUMBER,14,' ') AS ACCOUNT_NUMBER,PART_TRAN_TYPE,"
				+"LPAD(nvl(TRANSACTION_AMOUNT,0),17,0) as TRANSACTION_AMOUNT,"+
				"rpad(TRANSACTION_PARTICULAR,26,' ') as TRANSACTION_PARTICULAR,LPAD(NVL(REMARKS,' '),16,' ') AS REMARKS"
				+",to_char(TO_DATE(FILEDATE,'DD/MON/YYYY'),'DD/MM/YYYY') AS FILEDATE "
				+ " from  RUPAY_DRM_SETTLEMENT_TTUM where FILEDATE = TO_DATE(?,'DD/MM/YYYY') and cycle = ? and subcategory = ? order by sr_no";
		
		List<Object> settlementDRMData = getJdbcTemplate().query(getData, new Object[] {beanObj.getFileDate(),beanObj.getCycle(), beanObj.getSubcategory()}, new ResultSetExtractor<List<Object>>(){
			public List<Object> extractData(ResultSet rs)throws SQLException {
				List<Object> beanList = new ArrayList<Object>();
				
				while (rs.next()) {
					Map<String, String> table_Data = new HashMap<String, String>();
					table_Data.put("ACCOUNT_NUMBER", rs.getString("ACCOUNT_NUMBER"));
					table_Data.put("PART_TRAN_TYPE", rs.getString("PART_TRAN_TYPE"));
					table_Data.put("TRANSACTION_AMOUNT", rs.getString("TRANSACTION_AMOUNT"));
					table_Data.put("TRANSACTION_PARTICULAR", rs.getString("TRANSACTION_PARTICULAR"));
					table_Data.put("REMARKS", rs.getString("REMARKS"));
					table_Data.put("FILEDATE", rs.getString("FILEDATE"));
					
					beanList.add(table_Data);
				}
				return beanList;
			}
		});*/
		
		
		total_data.add(settlementData);
		//total_data.add(settlementDRMData);
		
		
		return total_data;
	
	}
	
	public boolean processSettlementTTUM(RupayUploadBean beanObj)
	{

		Map<String,Object> inParams = new HashMap<>();
		Map<String, Object> outParams = new HashMap<String, Object>();
		
		try
		{
			RupaySettlementTTUMProc rollBackexe = new RupaySettlementTTUMProc(getJdbcTemplate());
			inParams.put("FILEDT", beanObj.getFileDate());
			inParams.put("USER_ID", beanObj.getCreatedBy());
			inParams.put("ENTERED_CYCLE", beanObj.getCycle());
			inParams.put("SUBCATEGORY", beanObj.getSubcategory());
			outParams = rollBackexe.execute(inParams);

			if(outParams !=null && outParams.get("msg") != null)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		catch(Exception e)
		{
			
			logger.info("Exception in processSettlementTTUM "+e);
			return false;
		}
	
	}
	
	private class RupaySettlementTTUMProc extends StoredProcedure{
		private static final String insert_proc = "RUPAY_TTUM_PROCESS";
		public RupaySettlementTTUMProc(JdbcTemplate jdbcTemplate)
		{
			super(jdbcTemplate,insert_proc);
			setFunction(false);
			declareParameter(new SqlParameter("FILEDT",Types.VARCHAR));
			declareParameter(new SqlParameter("USER_ID",Types.VARCHAR));
			declareParameter(new SqlParameter("ENTERED_CYCLE",Types.VARCHAR));
			declareParameter(new SqlParameter("SUBCATEGORY",Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

	}

	/*************** RECTIFICATION LOGIC *********************/
public boolean processRectification(RupayUploadBean beanObj)
{
	
	try
	{
				
		String updateQuery = "update rupay_settlement_report set final_sett_amt = (select final_net from rupay_dscr_rawdata WHERE  UPPER(ISS_BIN) = 'TOTAL' AND CYCLE = '"+beanObj.getCycle()+"' "
				+ "	and subcategOry ='"+beanObj.getSubcategory()+"' and filedate = to_date('"+beanObj.getFileDate()+"','DD/MM/YYYY'))"
					+" where filedate = to_date('"+beanObj.getFileDate()+"','DD/MM/YYYY') "
				+ "AND CYCLE = "+beanObj.getCycle()+" AND ACQ_ISS_BIN = 'TOTAL' and subcategory = '"+beanObj.getSubcategory()+"'";
		
		getJdbcTemplate().execute(updateQuery);
		
		String updateTable = "update rupay_settlement_report_diff set rectified_flag = 'Y' WHERE FILEDATE = to_date('"+beanObj.getFileDate()+"','DD/MM/YYYY')  AND CYCLE = "+beanObj.getCycle()
				+" AND SUBCATEGORY = '"+beanObj.getSubcategory()+"'";
		
		getJdbcTemplate().execute(updateTable);
		
		return true;
		
	}
	catch(Exception e)
	{
		logger.info("Exception in processRectification "+e);
		return false;
	}
}

public boolean validateSettlementDiff(RupayUploadBean beanObj)
{
	
	try
	{
		String checkSettlementTTUM = "select COUNT(*) from rupay_settlement_report_diff where FILEDATE = TO_DATE(?,'DD/MM/YYYY') AND RECTIFIED_FLAG = 'N' AND CYCLE = ? and subcategory = ?";
		int getDiffCount = getJdbcTemplate().queryForObject(checkSettlementTTUM, new Object[] {beanObj.getFileDate(), beanObj.getCycle(), beanObj.getSubcategory()}, Integer.class);
		
		if(getDiffCount > 0)
		{
			return true;
		}
		else
			return false;
		
	}
	catch(Exception e)
	{
		logger.info("Exception in validateSettlementTTUM "+e);
		return false;
	}
}

public HashMap<String, Object> validateDiffAmount(RupayUploadBean beanObj)
{
	HashMap<String, Object> output = new HashMap<String, Object>();
	
	try
	{
		String getDiffQuery = "select t1.FINAL_SETT_AMT-final_net from rupay_settlement_report t1, rupay_dscr_rawdata t2 "
				+ "where t1.acq_iss_bin = 'TOTAL' and UPPER(t2.ISS_BIN) = 'TOTAL' AND t2.CYCLE = ? and t1.CYCLE = ? and t2.subcategOry = ? " + 
				"and t1.subcategOry =? and T1.FILEDATE = to_date(?,'DD/MM/YYYY') AND T1.FILEDATE = T2.FILEDATE";
		
		double getDiffAmt = getJdbcTemplate().queryForObject(getDiffQuery, new Object[] {beanObj.getCycle(), beanObj.getCycle(), beanObj.getSubcategory(), 
					beanObj.getSubcategory(),beanObj.getFileDate()},Double.class);
		
		logger.info("diff amt entered is "+beanObj.getRectAmt().substring(1));
		
		if(getDiffAmt > 0)
		{
			if(getDiffAmt != Double.parseDouble(beanObj.getRectAmt().substring(1)))
			{
				output.put("result", false);
				output.put("msg", "Entered amount and difference amount are not same");

			}
			else
			{
				output.put("result", true);
			}
		}
		else
		{
			//for negative values
			if(getDiffAmt != Double.parseDouble(beanObj.getRectAmt()))
			{
				output.put("result", false);
				output.put("msg", "Entered amount and difference amount are not same");

			}
			else
			{
				output.put("result", true);
			}
		
		}
	}
	catch(Exception e)
	{
		output.put("result", false);
		output.put("msg", "Exception occured while checking difference Amount");
	}
	return output;
}


/***************** NCMC SETTLEMENT ********************/

public boolean checkNCMCFileUploaded(RupayUploadBean beanObj)
{
	String tableName = null;
	try
	{
		if(beanObj.getSubcategory().equalsIgnoreCase("DOMESTIC"))
		{
			tableName = "NCMC_"+beanObj.getFileName()+"_RAWDATA";
		}
		else
		{
			tableName = "NCMC_INT_"+beanObj.getFileName()+"_RAWDATA";
		}

		int recordCount = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM "+tableName+" WHERE FILEDATE = TO_DATE(?,'DD/MM/YYYY') AND CYCLE = ?",
				new Object[] {beanObj.getFileDate(),beanObj.getCycle()},Integer.class);
		if(recordCount > 0)
			return true;
		else
			return false;
	}
	catch(Exception e)
	{
		logger.info("Exception in checkFileUploaded "+e );
		return false;
	}
}

public HashMap<String, Object> readNCMCFiles(RupayUploadBean beanObj,MultipartFile file)
{
	HashMap<String, Object> output = null;
	try
	{
		if(beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("DSCR"))
		{
			ReadNCMCDSCRFile fileRead = new ReadNCMCDSCRFile();
			output = fileRead.fileupload(beanObj, file, getConnection());
			//fileRead.PBGBfileupload(beanObj, file, getConnection()); //pbgb reading block
		}
		/*else if(beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("INTERCHANGE"))
		{
			ReadRupayInterchangeReport fileRead = new ReadRupayInterchangeReport();
			output = fileRead.fileupload(beanObj, file, getConnection());
		}
		else //if(beanObj.getFileName() != null && beanObj.getFileName().equalsIgnoreCase("BILLING"))
		{
			logger.info("Interchange File name");
			ReadRupayBillingReport fileRead = new ReadRupayBillingReport();
			output = fileRead.fileupload(beanObj, file, getConnection());
			fileRead.PBGBfileupload(beanObj, file, getConnection());
		}*/
		
		return output;
	}
	catch(Exception e)
	{
		System.out.println("EXception in readDSCR file "+e);
		output.put("result", false);
		output.put("count", 0);
		return output;
	}
}

public boolean settlementRollback(RupayUploadBean beanObj)
{
	
	try
	{
		String deleteQuery = "delete from rupay_settlement_report where filedate = '"+beanObj.getFileDate()+"'"
				+ " and cycle = '"+beanObj.getCycle()+"'";
		getJdbcTemplate().execute(deleteQuery);
		
		deleteQuery = "delete from rupay_dhana_settlement_report where filedate = '"+beanObj.getFileDate()+"'"
				+ " and cycle = '"+beanObj.getCycle()+"'";
		getJdbcTemplate().execute(deleteQuery);
	}
	catch(Exception e)
	{
		logger.info("Exception while deleting settlement report "+e);
		return false;
	}
	return true;
}

}
