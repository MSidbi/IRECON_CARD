package com.recon.util.chargeback;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.recon.model.Settlement_FinalBean;





public class SettlementFinalReportExcel extends AbstractExcelView {

	@Override
	protected void buildExcelDocument(Map<String, Object> map,HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)throws Exception {
		

	//	List<List<Settlement_FinalReportBean>> generatettum_list = (List<List<Settlement_FinalReportBean>>) map.get("generate_ttum");
		Boolean isEmpty = true;
		//List<String> ExcelHeaders = generatettum_list.get(0).get(0).getStExcelHeader();
	//	List<Settlement_FinalReportBean> TTUM_Data = generatettum_list.get(1);
		Date date = new Date();
		 Settlement_FinalBean reportBean =   (Settlement_FinalBean) map.get("ReportBean");
		 
		 String filedate = (String) map.get("filedate");
		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmm");
		
		String strDate = sdf.format(date);
		 System.out.println(strDate);
		 
		 sdf = new SimpleDateFormat("dd-MMM-yyyy");
		 Date fdate =sdf.parse(filedate);
		 sdf = new SimpleDateFormat("ddMMYYYY");
		 sdf.format(fdate);
		 System.out.println("file_date"+sdf.format(fdate));
		 
		 reportBean.setFileDate(sdf.format(fdate));
		// Date fdate= new 
		 
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename=Settlement_report.xls");
		
		
		OutputStream outStream = response.getOutputStream();
		
		//Settlement_FinalBean  reportBean  = new  Settlement_FinalBean();
		
		
		
		  workbook = new HSSFWorkbook();
         HSSFSheet sheet = workbook.createSheet("Report");  
		
	

		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("0.00"));

		// create header row
		HSSFRow header = sheet.createRow(0);
		
		
		
		
		
		sheet= createsheet(sheet,reportBean); 
		
		
		
		workbook.write(outStream);
        outStream.close();
       // generatettum_list.clear();
		response.getOutputStream().flush();
		
		
		
	
		
	}

	private HSSFSheet createsheet(HSSFSheet sheet,Settlement_FinalBean reportBean) {
		//HSSFSheet usersheet = sheet;
		
		HSSFRow header = sheet.createRow(0);
		
		
			header.createCell(0).setCellValue("Sr No"); 
			header.createCell(1).setCellValue("ID");
			header.createCell(2).setCellValue("BUSINESSDATE"); 
			header.createCell(3).setCellValue("DESCRIPTION"); 
			header.createCell(4).setCellValue("DEBIT_TXN_CNT"); 
			header.createCell(5).setCellValue("DEBIT"); 
			header.createCell(6).setCellValue("CREDIT_TXN_CNT"); 
			header.createCell(7).setCellValue("CREDIT"); 
			header.createCell(8).setCellValue("ACCOUNT_NO"); 
			
			HSSFRow row1 = sheet.createRow(1);
			
			HSSFRow row2 = sheet.createRow(2);
			HSSFRow row3 = sheet.createRow(3);
			HSSFRow row4 = sheet.createRow(4);
			HSSFRow row5 = sheet.createRow(5);
			HSSFRow row6 = sheet.createRow(6);
			HSSFRow row7 = sheet.createRow(7);
			HSSFRow row8 = sheet.createRow(8);
			HSSFRow row9 = sheet.createRow(9);
			HSSFRow row10 = sheet.createRow(10);
			HSSFRow row11 = sheet.createRow(11);
			HSSFRow row12 = sheet.createRow(12);
			HSSFRow row13 = sheet.createRow(13);
			HSSFRow row14 = sheet.createRow(14);
			HSSFRow row15 = sheet.createRow(15);
			HSSFRow row16 = sheet.createRow(16);
			HSSFRow row17 = sheet.createRow(17);
			HSSFRow row18 = sheet.createRow(18);
			HSSFRow row19 = sheet.createRow(19);
			HSSFRow row20 = sheet.createRow(20);
			HSSFRow row21 = sheet.createRow(21);
			HSSFRow row22 = sheet.createRow(22);
			HSSFRow row23 = sheet.createRow(23);
			HSSFRow row24 = sheet.createRow(24);
			HSSFRow row25 = sheet.createRow(25);
			HSSFRow row26 = sheet.createRow(26);
			HSSFRow row27 = sheet.createRow(27);
			HSSFRow row28 = sheet.createRow(28);
			HSSFRow row29 = sheet.createRow(29);
			HSSFRow row30 = sheet.createRow(30);
			HSSFRow row31 = sheet.createRow(31);
			HSSFRow row32 = sheet.createRow(32);
			HSSFRow row33 = sheet.createRow(33);
			HSSFRow row34 = sheet.createRow(34);
			HSSFRow row35 = sheet.createRow(35);
			HSSFRow row36 = sheet.createRow(36);
			HSSFRow row37 = sheet.createRow(37);
			HSSFRow row38 = sheet.createRow(38);
			HSSFRow row39 = sheet.createRow(39);
			HSSFRow row40 = sheet.createRow(40);
			HSSFRow row41 = sheet.createRow(41);
			HSSFRow row42 = sheet.createRow(42);
			HSSFRow row43 = sheet.createRow(43);
			HSSFRow row44 = sheet.createRow(44);
			HSSFRow row45 = sheet.createRow(45);
			//ADDED BY INT8624 FOR ADDING NEW ROW AS PER USER
			HSSFRow row46 = sheet.createRow(46);
			
			row1.createCell(0).setCellValue("1");
			row1.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row1.createCell(2).setCellValue(reportBean.getFileDate());
			row1.createCell(3).setCellValue("Acquirer BI Approved Fee");
			row1.createCell(4).setCellValue("0.00");
			row1.createCell(5).setCellValue("0.00");
			row1.createCell(6).setCellValue(reportBean.getAcq_BI_App_Fee_Cnt());
			row1.createCell(7).setCellValue(reportBean.getAcq_BI_App_Fee_Amt());
			row1.createCell(8).setCellValue("99948430010013");
			
			
			row2.createCell(0).setCellValue("2");
			row2.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row2.createCell(2).setCellValue(reportBean.getFileDate());
			row2.createCell(3).setCellValue("Acquirer BI Decline Approved Fee");
			row2.createCell(4).setCellValue("0.00");
			row2.createCell(5).setCellValue("0.00");
			row2.createCell(6).setCellValue(reportBean.getAcq_BI_Dec_App_Fee_Cnt());
			row2.createCell(7).setCellValue(reportBean.getAcq_BI_Dec_App_Fee_Amt());
			row2.createCell(8).setCellValue("99948430010013");
			
			row3.createCell(0).setCellValue("3");
			row3.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row3.createCell(2).setCellValue(reportBean.getFileDate());
			row3.createCell(3).setCellValue("Acquirer MS Approved Fee");
			row3.createCell(4).setCellValue("0.00");
			row3.createCell(5).setCellValue("0.00");
			row3.createCell(6).setCellValue(reportBean.getAcq_MS_Appr_Fee_Cnt());
			row3.createCell(7).setCellValue(Float.parseFloat(reportBean.getAcq_MS_Appr_Fee_Amt()));
			row3.createCell(8).setCellValue("99948430010013");
			
			row4.createCell(0).setCellValue("4");
			row4.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row4.createCell(2).setCellValue(reportBean.getFileDate());
			row4.createCell(3).setCellValue("Acquirer MS Decline Approved Fee");
			row4.createCell(4).setCellValue("0.00");
			row4.createCell(5).setCellValue("0.00");
			row4.createCell(6).setCellValue(reportBean.getAcq_MS_Dec_App_Fee_Cnt());
			row4.createCell(7).setCellValue(reportBean.getAcq_MS_Dec_App_Fee_Amt());
			row4.createCell(8).setCellValue("99948430010013");
			
			row5.createCell(0).setCellValue("5");
			row5.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row5.createCell(2).setCellValue(reportBean.getFileDate());
			row5.createCell(3).setCellValue("Acquirer PC Approved Fee");
			row5.createCell(4).setCellValue("0.00");
			row5.createCell(5).setCellValue("0.00");
			row5.createCell(6).setCellValue(reportBean.getAcq_PC_App_Fee_Cnt());
			row5.createCell(7).setCellValue(reportBean.getAcq_PC_App_Fee_Amt());
			row5.createCell(8).setCellValue("99948430010013");
			
			row6.createCell(0).setCellValue("6");
			row6.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row6.createCell(2).setCellValue(reportBean.getFileDate());
			row6.createCell(3).setCellValue("Acquirer PC Decline Approved Fee");
			row6.createCell(4).setCellValue("0.00");
			row6.createCell(5).setCellValue("0.00");
			row6.createCell(6).setCellValue(reportBean.getAcq_PC_Dec_App_Fee_Cnt());
			row6.createCell(7).setCellValue(reportBean.getAcq_PC_Dec_App_Fee_Amt());
			row6.createCell(8).setCellValue("99948430010013");
			
			
			row7.createCell(0).setCellValue("7");
			row7.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row7.createCell(2).setCellValue(reportBean.getFileDate());
			row7.createCell(3).setCellValue("CCard Acquirer WDL Approved Fee");
			row7.createCell(4).setCellValue("0.00");
			row7.createCell(5).setCellValue("0.00");
			row7.createCell(6).setCellValue(reportBean.getCCard_Acq_WDL_App_Fee_Cnt());
			row7.createCell(7).setCellValue(reportBean.getCCard_Acq_WDL_App_Fee_Amt());
			row7.createCell(8).setCellValue("99948430010013");
			
			
			row8.createCell(0).setCellValue("8");
			row8.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row8.createCell(2).setCellValue(reportBean.getFileDate());
			row8.createCell(3).setCellValue("Acquirer WDL Decline Approved Fee");
			row8.createCell(4).setCellValue("0.00");
			row8.createCell(5).setCellValue("0.00");
			row8.createCell(6).setCellValue(reportBean.getAcq_WDL_Dec_App_Fee_Cnt());
			row8.createCell(7).setCellValue(reportBean.getAcq_WDL_Dec_App_Fee_Amt());
			row8.createCell(8).setCellValue("99948430010013");
			
			
			//

			row9.createCell(0).setCellValue("9");
			row9.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row9.createCell(2).setCellValue(reportBean.getFileDate());
			row9.createCell(3).setCellValue("CC Acquirer WDL Decline Approved Fee");
			row9.createCell(4).setCellValue("0.00");
			row9.createCell(5).setCellValue("0.00");
			row9.createCell(6).setCellValue(reportBean.getCC_Acq_WDL_Dec_App_Fee_Cnt());
			row9.createCell(7).setCellValue(reportBean.getCC_Acq_WDL_Dec_App_Fee_Amt());
			row9.createCell(8).setCellValue("99948430010013");
			
			
			row10.createCell(0).setCellValue("10");
			row10.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row10.createCell(2).setCellValue(reportBean.getFileDate());
			row10.createCell(3).setCellValue("CCard Acquirer BI Decline Approved Fee");
			row10.createCell(4).setCellValue("0.00");
			row10.createCell(5).setCellValue("0.00");
			row10.createCell(6).setCellValue(reportBean.getCCard_Acq_BI_Dec_App_Fee_Cnt());
			row10.createCell(7).setCellValue(reportBean.getCCard_Acq_BI_Dec_App_Fee_Amt());
			row10.createCell(8).setCellValue("99948430010013");
			
			
			
			
			row11.createCell(0).setCellValue("11");
			row11.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row11.createCell(2).setCellValue(reportBean.getFileDate());
			row11.createCell(3).setCellValue("CCard Acquirer BI Approved Fee");
			row11.createCell(4).setCellValue("0.00");
			row11.createCell(5).setCellValue("0.00");
			row11.createCell(6).setCellValue(reportBean.getCCard_Acq_BI_App_Fee_Cnt());
			row11.createCell(7).setCellValue(reportBean.getCCard_Acq_BI_App_Fee_Amt());
			row11.createCell(8).setCellValue("99948430010013");
			
			
			row12.createCell(0).setCellValue("12");
			row12.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row12.createCell(2).setCellValue(reportBean.getFileDate());
			row12.createCell(3).setCellValue("Issuer BI Approved Fee");
			row12.createCell(4).setCellValue(reportBean.getIss_BI_App_Fee_Cnt());
			row12.createCell(5).setCellValue(reportBean.getIss_BI_App_Fee_Amt());
			row12.createCell(6).setCellValue("0.00");
			row12.createCell(7).setCellValue("0.00");
			row12.createCell(8).setCellValue("99987750010076");
			
			row13.createCell(0).setCellValue("13");
			row13.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row13.createCell(2).setCellValue(reportBean.getFileDate());
			row13.createCell(3).setCellValue("Issuer BI Decline Approved Fee");
			row13.createCell(4).setCellValue(reportBean.getIss_BI_Dec_App_Fee_Cnt());
			row13.createCell(5).setCellValue(reportBean.getIss_BI_Dec_App_Fee_Amt());
			row13.createCell(6).setCellValue("0.00");
			row13.createCell(7).setCellValue("0.00");
			row13.createCell(8).setCellValue("99987750010017");
			
			
			row14.createCell(0).setCellValue("14");
			row14.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row14.createCell(2).setCellValue(reportBean.getFileDate());
			row14.createCell(3).setCellValue("Issuer MS Approved Fee");
			row14.createCell(4).setCellValue(reportBean.getIss_MS_App_Fee_Cnt() );
			row14.createCell(5).setCellValue(reportBean.getIss_MS_App_Fee_Amt());
			row14.createCell(6).setCellValue("0.00");
			row14.createCell(7).setCellValue("0.00");
			row14.createCell(8).setCellValue("99987750010076");
			
			
			row15.createCell(0).setCellValue("15");
			row15.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row15.createCell(2).setCellValue(reportBean.getFileDate());
			row15.createCell(3).setCellValue("Issuer MS Decline Approved Fee");
			row15.createCell(4).setCellValue(reportBean.getIss_MS_Dec_App_Fee_Cnt() );
			row15.createCell(5).setCellValue(reportBean.getIss_MS_Dec_App_Fee_Amt());
			row15.createCell(6).setCellValue("0.00");
			row15.createCell(7).setCellValue("0.00");
			row15.createCell(8).setCellValue("99987750010017");
			
			
			row16.createCell(0).setCellValue("16");
			row16.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row16.createCell(2).setCellValue(reportBean.getFileDate());
			row16.createCell(3).setCellValue("Issuer PC Approved Fee");
			row16.createCell(4).setCellValue(reportBean.getIss_PC_App_Fee_Cnt());
			row16.createCell(5).setCellValue(reportBean.getIss_PC_App_Fee_Amt());
			row16.createCell(6).setCellValue("0.00");
			row16.createCell(7).setCellValue("0.00");
			row16.createCell(8).setCellValue("99987750010076");
			
			
			row17.createCell(0).setCellValue("17");
			row17.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row17.createCell(2).setCellValue(reportBean.getFileDate());
			row17.createCell(3).setCellValue("Issuer PC Decline Approved Fee");
			row17.createCell(4).setCellValue(reportBean.getIss_PC_Dec_App_Fee_Cnt());
			row17.createCell(5).setCellValue(reportBean.getIss_PC_Dec_App_Fee_Amt());
			row17.createCell(6).setCellValue("0.00");
			row17.createCell(7).setCellValue("0.00");
			row17.createCell(8).setCellValue("99987750010017");
			
			row18.createCell(0).setCellValue("18");
			row18.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row18.createCell(2).setCellValue(reportBean.getFileDate());
			row18.createCell(3).setCellValue("Issuer WDL Decline Approved Fee");
			row18.createCell(4).setCellValue(reportBean.getIss_WDL_Dec_App_Fee_Cnt());
			row18.createCell(5).setCellValue(reportBean.getIss_WDL_Dec_App_Fee_Amt());
			row18.createCell(6).setCellValue("0.00");
			row18.createCell(7).setCellValue("0.00");
			row18.createCell(8).setCellValue("99987750010017");
			
			
			row19.createCell(0).setCellValue("19");
			row19.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row19.createCell(2).setCellValue(reportBean.getFileDate());
			row19.createCell(3).setCellValue("Acquirer Pre-arbitration Details");
			row19.createCell(4).setCellValue(reportBean.getAcq_Pre_Arb_Dec_Dtls_Cnt());
			row19.createCell(5).setCellValue(reportBean.getAcq_Pre_Arb_Dec_Dtls_Amt());
			row19.createCell(6).setCellValue("0.00");
			row19.createCell(7).setCellValue("0.00");
			row19.createCell(8).setCellValue("99977850010030");
			
			row20.createCell(0).setCellValue("20");
			row20.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row20.createCell(2).setCellValue(reportBean.getFileDate());
			row20.createCell(3).setCellValue("Issuer Pre-Arbitration Decline Details");
			row20.createCell(4).setCellValue(reportBean.getIss_Pre_Arb_Dec_Dtls_Cnt());
			row20.createCell(5).setCellValue(reportBean.getIss_Pre_Arb_Dec_Dtls_Amt());
			row20.createCell(6).setCellValue("0.00");
			row20.createCell(7).setCellValue("0.00");
			row20.createCell(8).setCellValue("99936200010012");
			
			
			row21.createCell(0).setCellValue("21");
			row21.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row21.createCell(2).setCellValue(reportBean.getFileDate());
			row21.createCell(3).setCellValue("Acquirer Chargeback Details");
			row21.createCell(4).setCellValue(reportBean.getAcq_Charg_Dtls_Cnt());
			row21.createCell(5).setCellValue(reportBean.getAcq_Charg_Dtls_Amt());
			row21.createCell(6).setCellValue("0.00");
			row21.createCell(7).setCellValue("0.00");
			row21.createCell(8).setCellValue("99977850010029");
			
			
			row22.createCell(0).setCellValue("22");
			row22.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row22.createCell(2).setCellValue(reportBean.getFileDate());
			row22.createCell(3).setCellValue("Acquirer Credit Adjustment Details");
			row22.createCell(4).setCellValue(reportBean.getAcq_Cred_Adj_Dtls_Cnt());
			row22.createCell(5).setCellValue(reportBean.getAcq_Cred_Adj_Dtls_Amt());
			row22.createCell(6).setCellValue("0.00");
			row22.createCell(7).setCellValue("0.00");
			row22.createCell(8).setCellValue("99934450010084");
			
			row23.createCell(0).setCellValue("23");
			row23.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row23.createCell(2).setCellValue(reportBean.getFileDate());
			row23.createCell(3).setCellValue("Acquirer Representment Details");
			row23.createCell(4).setCellValue("0.00");
			row23.createCell(5).setCellValue("0.00");
			row23.createCell(6).setCellValue(reportBean.getAcq_Repr_Dtls_Cnt());
			row23.createCell(7).setCellValue(reportBean.getAcq_Repr_Dtls_Amt());
			row23.createCell(8).setCellValue("99977850010029");
			
			
			row24.createCell(0).setCellValue("24");
			row24.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row24.createCell(2).setCellValue(reportBean.getFileDate());
			row24.createCell(3).setCellValue("Acquirer Pre-Arbitration Decline Details");
			row24.createCell(4).setCellValue(reportBean.getAcq_Pre_Arb_Dec_Dtls_Cnt());
			row24.createCell(5).setCellValue(reportBean.getAcq_Pre_Arb_Dec_Dtls_Amt());
			row24.createCell(6).setCellValue("00.00");
			row24.createCell(7).setCellValue("00.00");
			row24.createCell(8).setCellValue("99977850010030");
			
			row25.createCell(0).setCellValue("25");
			row25.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row25.createCell(2).setCellValue(reportBean.getFileDate());
			row25.createCell(3).setCellValue("Issuer Credit Adjustment Details");
			row25.createCell(4).setCellValue("0.00");
			row25.createCell(5).setCellValue("0.00");
			row25.createCell(6).setCellValue(reportBean.getIss_Cre_Adj_Dtls_Cnt());
			row25.createCell(7).setCellValue(reportBean.getIss_Cre_Adj_Dtls_Amt());
			row25.createCell(8).setCellValue("99936200010011");
			
			
			row26.createCell(0).setCellValue("26");
			row26.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row26.createCell(2).setCellValue(reportBean.getFileDate());
			row26.createCell(3).setCellValue("Issuer Pre-arbitration Details");
			row26.createCell(4).setCellValue("0.00");
			row26.createCell(5).setCellValue("0.00");
			row26.createCell(6).setCellValue(reportBean.getIss_Pre_arb_Dtls_Cnt());
			row26.createCell(7).setCellValue(reportBean.getIss_Pre_arb_Dtls_Amt());
			row26.createCell(8).setCellValue("99936200010012");
			
			row27.createCell(0).setCellValue("27");
			row27.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row27.createCell(2).setCellValue(reportBean.getFileDate());
			row27.createCell(3).setCellValue("Issuer Chargeback Details");
			row27.createCell(4).setCellValue("0.00");
			row27.createCell(5).setCellValue("0.00");
			row27.createCell(6).setCellValue(reportBean.getIss_Charge_Dtls_Cnt());
			row27.createCell(7).setCellValue(reportBean.getIss_Charge_Dtls_Amt());
			row27.createCell(8).setCellValue("99936200010011");
			
			
			row28.createCell(0).setCellValue("28");
			row28.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row28.createCell(2).setCellValue(reportBean.getFileDate());
			row28.createCell(3).setCellValue("Issuer Representment Details");
			row28.createCell(4).setCellValue(reportBean.getIss_Repr_Dtls_Cnt());
			row28.createCell(5).setCellValue(reportBean.getIss_Repr_Dtls_Amt());
			row28.createCell(6).setCellValue("0.00");
			row28.createCell(7).setCellValue("0.00");
			
			row28.createCell(8).setCellValue("99936200010011");
			
			
			row29.createCell(0).setCellValue("29");
			row29.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row29.createCell(2).setCellValue(reportBean.getFileDate());
			row29.createCell(3).setCellValue("Cashnet ATM Interchange Paid ( GST on issuer )");
			row29.createCell(4).setCellValue(reportBean.getCash_ATM_inter_Paid_GST_Cnt());
			row29.createCell(5).setCellValue(reportBean.getCash_ATM_inter_Paid_GST_Amt());
			row29.createCell(6).setCellValue("00.00");
			row29.createCell(7).setCellValue("00.00");
			//row29.createCell(8).setCellValue("99972000010017");
			row29.createCell(8).setCellValue("999072000010027"); //modified by INT8624
			
			row30.createCell(0).setCellValue("30");
			row30.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row30.createCell(2).setCellValue(reportBean.getFileDate());
			row30.createCell(3).setCellValue("Cashnet Shared Network Acquiring Settlement");
			
			row30.createCell(4).setCellValue("00.00");
			row30.createCell(5).setCellValue("00.00");
			row30.createCell(6).setCellValue(reportBean.getCash_Sha_Net_Acq_Sett_Cnt());
			row30.createCell(7).setCellValue(reportBean.getCash_Sha_Net_Acq_Sett_Amt());
			row30.createCell(8).setCellValue("99978000010204");
			
			
			row31.createCell(0).setCellValue("31");
			row31.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row31.createCell(2).setCellValue(reportBean.getFileDate());
			row31.createCell(3).setCellValue("TDS on Issuer Switch Expenses");
			row31.createCell(4).setCellValue("0.00");
			row31.createCell(5).setCellValue("0.00");
			row31.createCell(6).setCellValue(reportBean.getTDS_on_Iss_Swtch_Exp_Cnt());
			
		//	double Exp_amt =(Float.parseFloat(reportBean.getTDS_on_Iss_Swtch_Exp_Cnt())*0.015);
				
			double Exp_amt =(Float.parseFloat(reportBean.getTDS_on_Iss_Swtch_Exp_Cnt())*0.02); //CHANGES MADE BY INT8624 ON 21 JUNE
			
			System.out.println("TDS_amount"+Exp_amt);
			row31.createCell(7).setCellValue(String.format("%.2f", Exp_amt));
			row31.createCell(8).setCellValue("99934600010067");
			
			//Need to change
			row32.createCell(0).setCellValue("32");
			row32.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row32.createCell(2).setCellValue(reportBean.getFileDate());
			row32.createCell(3).setCellValue("Cashnet Shared network A/c For Service Tax on Acquirer");
			row32.createCell(4).setCellValue("00");
			row32.createCell(5).setCellValue("00");
			row32.createCell(6).setCellValue(reportBean.getCash_Sha_Network_Serv_Tax_Cnt());
			row32.createCell(7).setCellValue(reportBean.getCash_Sha_Network_Serv_Tax_Amt());
			//row32.createCell(8).setCellValue("99936000010001");
			row32.createCell(8).setCellValue("999036000010024");//MODIFIED BY INT8624
			
			
			row33.createCell(0).setCellValue("33");
			row33.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row33.createCell(2).setCellValue(reportBean.getFileDate());
			row33.createCell(3).setCellValue("Acquirer WDL null Approved Fee");
			row33.createCell(4).setCellValue("0.00");
			row33.createCell(5).setCellValue("0.00");
			row33.createCell(6).setCellValue(reportBean.getAcq_WDL_Appr_Fee_Cnt());
			row33.createCell(7).setCellValue(reportBean.getAcq_WDL_Appr_Fee_Amt());
			row33.createCell(8).setCellValue("99948430010013");
			
			
			row34.createCell(0).setCellValue("34");
			row34.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row34.createCell(2).setCellValue(reportBean.getFileDate());
			row34.createCell(3).setCellValue("Cashnet Shared network Switch Expense");
			row34.createCell(4).setCellValue(reportBean.getCash_Sha_net_Swt_Exp_Cnt());
			row34.createCell(5).setCellValue(reportBean.getCash_Sha_net_Swt_Exp_Amt());
			row34.createCell(6).setCellValue("0.00");
			row34.createCell(7).setCellValue("0.00");
			
			row34.createCell(8).setCellValue("99987750010017");
			
			row35.createCell(0).setCellValue("35");
			row35.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row35.createCell(2).setCellValue(reportBean.getFileDate());
			row35.createCell(3).setCellValue("Issuer WDL null Approved Fee");
			row35.createCell(4).setCellValue(reportBean.getIss_WDL_Approved_Fee_Cnt());
			row35.createCell(5).setCellValue(reportBean.getIss_WDL_Approved_Fee_Amt());
			row35.createCell(6).setCellValue("0.00");
			row35.createCell(7).setCellValue("0.00");
			row35.createCell(8).setCellValue("99987750010076");
			
			row36.createCell(0).setCellValue("36");
			row36.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row36.createCell(2).setCellValue(reportBean.getFileDate());
			row36.createCell(3).setCellValue("Cashnet Shared Network Issuing Settlement");
			row36.createCell(4).setCellValue(reportBean.getCash_Sha_Net_Iss_Sett_Cnt());
			row36.createCell(5).setCellValue(reportBean.getCash_Sha_Net_Iss_Sett_Amt());
			row36.createCell(6).setCellValue("0.00");
			row36.createCell(7).setCellValue("0.00");
			row36.createCell(8).setCellValue("99937200010020");
			
			row37.createCell(0).setCellValue("37");
			row37.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row37.createCell(2).setCellValue(reportBean.getFileDate());
			row37.createCell(3).setCellValue("Net settlement AMOUNT");
			row37.createCell(4).setCellValue("0.00");
			row37.createCell(5).setCellValue("0.00");
			row37.createCell(6).setCellValue(reportBean.getNet_sett_AMOUNT_Cnt());
			row37.createCell(7).setCellValue(reportBean.getNet_sett_AMOUNT_Amt());
			row37.createCell(8).setCellValue("99977850010001");
			
			
			row38.createCell(0).setCellValue("38");
			row38.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row38.createCell(2).setCellValue(reportBean.getFileDate());
			row38.createCell(3).setCellValue("Issuer Dedit Adjustment Details");
			row38.createCell(4).setCellValue(reportBean.getIss_Debit_Adj_Dtls_Cnt());
			row38.createCell(5).setCellValue(reportBean.getIss_Debit_Adj_Dtls_Amt());
			row38.createCell(6).setCellValue("00.00");
			row38.createCell(7).setCellValue("00.00");
			
			row38.createCell(8).setCellValue("99936200010011");
			
			row39.createCell(0).setCellValue("39");
			row39.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row39.createCell(2).setCellValue(reportBean.getFileDate());
			row39.createCell(3).setCellValue("Acquirer Dedit Adjustment Details");
			row39.createCell(4).setCellValue(reportBean.getAcq_Debit_Adj_Dtls_Cnt());
			row39.createCell(5).setCellValue(reportBean.getAcq_Debit_Adj_Dtls_Amt());
			row39.createCell(6).setCellValue("00.00");
			row39.createCell(7).setCellValue("00.00");
			row39.createCell(8).setCellValue("99977850010029");
			
			row40.createCell(0).setCellValue("40");
			row40.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row40.createCell(2).setCellValue(reportBean.getFileDate());
			row40.createCell(3).setCellValue("Iss  Penalty date "+reportBean.getFileDate());
			row40.createCell(4).setCellValue("00.00");
			row40.createCell(5).setCellValue("00.00");
			row40.createCell(6).setCellValue("00.00");
			row40.createCell(7).setCellValue(reportBean.getIss_penalty());
			row40.createCell(8).setCellValue("99936200010011");
			
			
			row41.createCell(0).setCellValue("41");
			row41.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row41.createCell(2).setCellValue(reportBean.getFileDate());
			row41.createCell(3).setCellValue("Acq  Pre-arb Accpt Penalty");
			row41.createCell(4).setCellValue("00.00");
			row41.createCell(5).setCellValue("00.00");
			row41.createCell(6).setCellValue("00.00");
			row41.createCell(7).setCellValue("00.00");
			row41.createCell(8).setCellValue("99977900010154");
			
			/*Iss  Penalty date 19112019
			Acq  Pre-arb Accpt Penalty
			Acq  Penalty date 18112019*/

			
			row42.createCell(0).setCellValue("42");
			row42.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row42.createCell(2).setCellValue(reportBean.getFileDate());
			row42.createCell(3).setCellValue("Acq  Penalty date");
			row42.createCell(4).setCellValue("00.00");
			row42.createCell(5).setCellValue(reportBean.getAcq_penalty());
			row42.createCell(6).setCellValue("00.00");
			row42.createCell(7).setCellValue("00.00");
			row42.createCell(8).setCellValue("99977850010029");
			
			
			
			
			
			row43.createCell(0).setCellValue("43");
			row43.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row43.createCell(2).setCellValue(reportBean.getFileDate());
			row43.createCell(3).setCellValue("Iss  Pre-arb Accpt");
			row43.createCell(4).setCellValue("00.00");
			row43.createCell(5).setCellValue("00.00");
			row43.createCell(6).setCellValue("00.00");
			row43.createCell(7).setCellValue("00.00");
			row43.createCell(8).setCellValue("99936200010012");
			
			
			row44.createCell(0).setCellValue("44");
			row44.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row44.createCell(2).setCellValue(reportBean.getFileDate());
			row44.createCell(3).setCellValue("COMPENSATION  PAID ACQ");
			row44.createCell(4).setCellValue("00.00");
			row44.createCell(5).setCellValue("00.00");
			row44.createCell(6).setCellValue("00.00");
			row44.createCell(7).setCellValue("00.00");
			row44.createCell(8).setCellValue("99987750010123");
			
			System.out.println("Credit AMOUNT:"+Float.parseFloat(reportBean.getAcq_BI_App_Fee_Amt())+" + " +" +" + " + " +" + " + Float.parseFloat(reportBean.getAcq_BI_Dec_App_Fee_Amt())  +" + " +  Float.parseFloat(reportBean.getAcq_MS_Appr_Fee_Amt()) +" + " + Float.parseFloat(reportBean.getAcq_MS_Dec_App_Fee_Amt()) 
					+" + " + Float.parseFloat(reportBean.getAcq_PC_App_Fee_Amt()) +" + " + Float.parseFloat(reportBean.getAcq_PC_Dec_App_Fee_Amt()) +" + " + Float.parseFloat(reportBean.getCCard_Acq_WDL_App_Fee_Amt()) +" + " + Float.parseFloat(reportBean.getAcq_WDL_Dec_App_Fee_Amt())
					 +" + " + Float.parseFloat(reportBean.getCC_Acq_WDL_Dec_App_Fee_Amt()) +" + " + Float.parseFloat(reportBean.getCCard_Acq_BI_Dec_App_Fee_Amt()) +" + " + Float.parseFloat(reportBean.getCCard_Acq_BI_App_Fee_Amt()) +" + " + Float.parseFloat(reportBean.getIss_Cre_Adj_Dtls_Amt())
					 +" + " + Float.parseFloat(reportBean.getIss_Pre_arb_Dtls_Amt()) +" + " + Float.parseFloat(reportBean.getIss_Charge_Dtls_Amt()) +" + " + Float.parseFloat(reportBean.getCash_Sha_Net_Acq_Sett_Amt()) 
					 +" + " + Float.parseFloat(reportBean.getTDS_on_Iss_Swtch_Exp_Amt()) +" + " + Float.parseFloat(reportBean.getCash_Sha_Network_Serv_Tax_Amt()) +" + " + Float.parseFloat(reportBean.getAcq_WDL_Appr_Fee_Amt()) +" + " + reportBean.getNet_sett_AMOUNT_Amt()
					 +" + " + Float.parseFloat(reportBean.getIss_penalty()) +" + " + Float.parseFloat(reportBean.getAcq_Repr_Dtls_Amt()));
			
			
			double Credit_amount = Double.parseDouble(reportBean.getAcq_BI_App_Fee_Amt()) +  Double.parseDouble(reportBean.getAcq_BI_Dec_App_Fee_Amt())  +  Double.parseDouble(reportBean.getAcq_MS_Appr_Fee_Amt()) + Double.parseDouble(reportBean.getAcq_MS_Dec_App_Fee_Amt()) 
					+ Double.parseDouble(reportBean.getAcq_PC_App_Fee_Amt()) + Double.parseDouble(reportBean.getAcq_PC_Dec_App_Fee_Amt()) + Double.parseDouble(reportBean.getCCard_Acq_WDL_App_Fee_Amt()) + Double.parseDouble(reportBean.getAcq_WDL_Dec_App_Fee_Amt())
					 + Double.parseDouble(reportBean.getCC_Acq_WDL_Dec_App_Fee_Amt()) + Double.parseDouble(reportBean.getCCard_Acq_BI_Dec_App_Fee_Amt()) + Double.parseDouble(reportBean.getCCard_Acq_BI_App_Fee_Amt()) + Double.parseDouble(reportBean.getIss_Cre_Adj_Dtls_Amt())
					 + Double.parseDouble(reportBean.getIss_Pre_arb_Dtls_Amt()) + Double.parseDouble(reportBean.getIss_Charge_Dtls_Amt()) + Double.parseDouble(reportBean.getCash_Sha_Net_Acq_Sett_Amt()) 
					 + Double.parseDouble(reportBean.getTDS_on_Iss_Swtch_Exp_Amt()) + Double.parseDouble(reportBean.getCash_Sha_Network_Serv_Tax_Amt()) + Double.parseDouble(reportBean.getAcq_WDL_Appr_Fee_Amt()) + Double.parseDouble(reportBean.getNet_sett_AMOUNT_Amt()
					) + Double.parseDouble(reportBean.getIss_penalty()) + Double.parseDouble(reportBean.getAcq_Repr_Dtls_Amt());
			
			System.out.println(Credit_amount);
			
			System.out.println("Debit Amount:" +Float.parseFloat(reportBean.getIss_BI_App_Fee_Amt()) +" + " +  Float.parseFloat(reportBean.getIss_BI_Dec_App_Fee_Amt()) +" + " +  Float.parseFloat(reportBean.getIss_MS_App_Fee_Amt()) +" + " +  Float.parseFloat(reportBean.getIss_MS_Dec_App_Fee_Amt()) +" + " +  Float.parseFloat(reportBean.getIss_PC_App_Fee_Amt()) +" + " +  Float.parseFloat(reportBean.getIss_PC_Dec_App_Fee_Amt()
					) +" + " +  Float.parseFloat(reportBean.getIss_WDL_Dec_App_Fee_Amt()) +" + " +  Float.parseFloat(reportBean.getAcq_Pre_Arb_Dec_Dtls_Amt()) +" + " +  Float.parseFloat(reportBean.getIss_Pre_Arb_Dec_Dtls_Amt()) +" + " +  Float.parseFloat(reportBean.getAcq_Charg_Dtls_Amt()) +" + " +  Float.parseFloat(reportBean.getAcq_Cred_Adj_Dtls_Amt()) +" + " +  Float.parseFloat(reportBean.getAcq_Pre_Arb_Dec_Dtls_Amt()
							) +" + " +  Float.parseFloat(reportBean.getIss_Repr_Dtls_Amt()) +" + " +  Float.parseFloat(reportBean.getCash_ATM_inter_Paid_GST_Amt()) +" + " +  Float.parseFloat(reportBean.getCash_Sha_net_Swt_Exp_Amt()) +" + " +  Float.parseFloat(reportBean.getIss_WDL_Approved_Fee_Amt()) +" + " +  Float.parseFloat(reportBean.getCash_Sha_Net_Iss_Sett_Amt()) +" + " +  Float.parseFloat(reportBean.getIss_Debit_Adj_Dtls_Amt()
							) +" + " +  Float.parseFloat(reportBean.getAcq_Debit_Adj_Dtls_Amt()));
			
			double Debit_amount = Double.parseDouble(reportBean.getIss_BI_App_Fee_Amt()) +  Double.parseDouble(reportBean.getIss_BI_Dec_App_Fee_Amt()) +  Double.parseDouble(reportBean.getIss_MS_App_Fee_Amt()) +  Double.parseDouble(reportBean.getIss_MS_Dec_App_Fee_Amt()) +  Double.parseDouble(reportBean.getIss_PC_App_Fee_Amt()) +  Double.parseDouble(reportBean.getIss_PC_Dec_App_Fee_Amt()
					) +  Double.parseDouble(reportBean.getIss_WDL_Dec_App_Fee_Amt()) +  Double.parseDouble(reportBean.getAcq_Pre_Arb_Dec_Dtls_Amt()) +  Double.parseDouble(reportBean.getIss_Pre_Arb_Dec_Dtls_Amt()) +  Double.parseDouble(reportBean.getAcq_Charg_Dtls_Amt()) +  Double.parseDouble(reportBean.getAcq_Cred_Adj_Dtls_Amt()) +  Double.parseDouble(reportBean.getAcq_Pre_Arb_Dec_Dtls_Amt()
							) +  Double.parseDouble(reportBean.getIss_Repr_Dtls_Amt()) +  Double.parseDouble(reportBean.getCash_ATM_inter_Paid_GST_Amt()) +  Double.parseDouble(reportBean.getCash_Sha_net_Swt_Exp_Amt()) +  Double.parseDouble(reportBean.getIss_WDL_Approved_Fee_Amt()) +  Double.parseDouble(reportBean.getCash_Sha_Net_Iss_Sett_Amt()) +  Double.parseDouble(reportBean.getIss_Debit_Adj_Dtls_Amt()
							) +  Double.parseDouble(reportBean.getAcq_Debit_Adj_Dtls_Amt()) + Double.parseDouble(reportBean.getAcq_penalty());
					
					/* Float.parseFloat(reportBean.getIss_BI_App_Fee_Amt()) + Float.parseFloat(reportBean.getIss_BI_Dec_App_Fee_Amt()) + Float.parseFloat(reportBean.getIss_MS_App_Fee_Amt()) + Float.parseFloat(reportBean.getIss_MS_Dec_App_Fee_Amt()) + Float.parseFloat(reportBean.getIss_PC_App_Fee_Amt()
					) + Float.parseFloat(reportBean.getIss_PC_Dec_App_Fee_Amt()) + Float.parseFloat(reportBean.getIss_WDL_Dec_App_Fee_Amt()) + Float.parseFloat(reportBean.getAcq_Pre_Arb_Dec_Dtls_Amt()) + Float.parseFloat(reportBean.getIss_Pre_Arb_Dec_Dtls_Amt()) + Float.parseFloat(
							reportBean.getAcq_Charg_Dtls_Amt()) + Float.parseFloat(reportBean.getAcq_Cred_Adj_Dtls_Amt()) + Float.parseFloat(reportBean.getIss_Repr_Dtls_Amt()) + Float.parseFloat(reportBean.getCash_ATM_inter_Paid_GST_Amt()) + Float.parseFloat(reportBean.getCash_Sha_net_Swt_Exp_Amt()
							) + Float.parseFloat(reportBean.getIss_WDL_Approved_Fee_Amt()) + Float.parseFloat(reportBean.getCash_Sha_Net_Iss_Sett_Amt()) + Float.parseFloat(reportBean.getIss_Debit_Adj_Dtls_Amt()) + Float.parseFloat(reportBean.getAcq_penalty());
*/
			
			System.out.println(Debit_amount );
			System.out.println(Credit_amount);
			
			
			
			System.out.println(Double.toHexString(Debit_amount));
			System.out.println(Double.toHexString(Credit_amount));
			row45.createCell(0).setCellValue("45");
			row45.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row45.createCell(2).setCellValue(reportBean.getFileDate());
			row45.createCell(3).setCellValue("Total Amount");
			row45.createCell(4).setCellValue("00.00");
			row45.createCell(5).setCellValue(String.format("%.2f",Debit_amount));
			row45.createCell(6).setCellValue("00.00");
			row45.createCell(7).setCellValue(String.format("%.2f",Credit_amount));
			row45.createCell(8).setCellValue("99987750010123");
			
			//ADDED BY INT8624 FOR ADDING NEW ROW AS PER USER
			row46.createCell(0).setCellValue("46");
			row46.createCell(1).setCellValue("SETTL"+reportBean.getFileDate());
			row46.createCell(2).setCellValue(reportBean.getFileDate());
			row46.createCell(3).setCellValue("Difference");
			row46.createCell(4).setCellValue("00.00");
			row46.createCell(5).setCellValue(String.format("%.2f",(Credit_amount-Debit_amount)));
			row46.createCell(6).setCellValue("00.00");
			row46.createCell(7).setCellValue("00.00");
			row46.createCell(8).setCellValue("");
		return sheet;
	}

}
