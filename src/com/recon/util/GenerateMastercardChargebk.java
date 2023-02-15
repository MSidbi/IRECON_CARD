package com.recon.util;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractExcelView;

import com.recon.model.Mastercard_chargeback;

public class GenerateMastercardChargebk extends AbstractExcelView {

	private static final Logger logger = Logger
			.getLogger(GenerateMastercardChargebk.class);

	@Override
	protected void buildExcelDocument(Map<String, Object> map,
			HSSFWorkbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			List<List<Mastercard_chargeback>> generatettum_list1 = (List<List<Mastercard_chargeback>>) map
					.get("report1");

			Boolean isEmpty = true;
			// List<String> ExcelHeaders =
			// generatettum_list1.get(0).get(0).getStExcelHeader();
			// String test=generatettum_list1.get(0).get(0).getStEnd_Date();
			List<Mastercard_chargeback> TTUM_Data = generatettum_list1.get(1);

			Date date = new Date();

			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyhhmm");

			String strDate = sdf.format(date);
			System.out.println(strDate);

			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			response.setHeader("Content-disposition",
					"attachment; filename=Report.xlsx");
			// create a new Excel sheet
			OutputStream outStream = response.getOutputStream();

			XSSFWorkbook wb = new XSSFWorkbook();

			SXSSFWorkbook workbook1 = new SXSSFWorkbook(wb, 1000);

			SXSSFSheet sheet = (SXSSFSheet) workbook1.createSheet("REPORT1");

			SXSSFRow header = sheet.createRow(0);
			/*
			 * SXSSFRow header3 = sheet1.createRow(0); SXSSFRow header4 =
			 * sheet2.createRow(0);
			 */

			header.createCell(0).setCellValue("MICROFILM");
			header.createCell(1).setCellValue("REF_ID");
			header.createCell(2).setCellValue("SETTLEMENT_AMOUNT");
			header.createCell(3).setCellValue("SETTLEMENT_CURRENCY");
			header.createCell(4).setCellValue("TXN_AMOUNT");
			header.createCell(5).setCellValue("TXN_CURRENCY");
			header.createCell(6).setCellValue("REASON");
			header.createCell(7).setCellValue("DOCUMENTATION ");
			header.createCell(8).setCellValue("REMARKS");

			/*
			 * CellStyle style = workbook1.createCellStyle(); Font font =
			 * workbook1.createFont(); font.setFontName("Arial");
			 * font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			 * font.setColor(HSSFColor.GREEN.index); style.setFont(font);
			 */

			CellStyle numberStyle = workbook1.createCellStyle();
			numberStyle.setDataFormat(workbook1.getCreationHelper()
					.createDataFormat().getFormat("0.00"));

			// create header row
			/*
			 * SXSSFRow header = (SXSSFRow)sheet.createRow(0); SXSSFRow header1
			 * = (SXSSFRow)sheet1.createRow(0); SXSSFRow header2 =
			 * (SXSSFRow)sheet2.createRow(0);
			 */

			int inRowCount = 1;

			if (TTUM_Data.size() != 0) {
				//inRowCount = 1;

				for (int i = 0; i < TTUM_Data.size(); i++) {
					try {
						SXSSFRow header2 = (SXSSFRow) sheet
								.createRow(inRowCount);
						
						Mastercard_chargeback generateTTUMBeanObj = new Mastercard_chargeback();
						generateTTUMBeanObj = TTUM_Data.get(i);

						int j = 0;
						// aRow.createCell(j).setCellValue(stTTUM_DRecords.get(j));
						header2.createCell(j).setCellValue(
								generateTTUMBeanObj.getMicrofilm());
						header2.createCell(++j).setCellValue(
								generateTTUMBeanObj.getRef_id());
						header2.createCell(++j).setCellValue(
								generateTTUMBeanObj.getSettlement_amount());
						header2.createCell(++j).setCellValue(
								generateTTUMBeanObj.getSettlement_currency());
						header2.createCell(++j).setCellValue(
								generateTTUMBeanObj.getTxn_amount());
						header2.createCell(++j).setCellValue(
								generateTTUMBeanObj.getTxn_currency());
						header2.createCell(++j).setCellValue(
								generateTTUMBeanObj.getReason());

						header2.createCell(++j).setCellValue(
								generateTTUMBeanObj.getDocumentation());

						header2.createCell(++j).setCellValue(
								generateTTUMBeanObj.getRemarks());

						// }
						inRowCount++;
						/*
						 * header2 = sheet.createRow(inRowCount); int k = 0;
						 * //CR Entry //for(int j = 0;j<ExcelHeaders.size() ;
						 * j++)
						 * 
						 * //aRow.createCell(j).setCellValue(stTTUM_DRecords.get(
						 * j));
						 * header2.createCell(k).setCellValue(generateTTUMBeanObj
						 * .getStCreditAcc());
						 * //header2.createCell(k).setCellValue("HELL");
						 * //System
						 * .out.println("generateTTUMBeanObj.getStCreditAcc() "
						 * +generateTTUMBeanObj.getStCreditAcc());
						 * header2.createCell(++k).setCellValue("INR");
						 * header2.createCell(++k).setCellValue("999");
						 * header2.createCell(++k).setCellValue("C");
						 * header2.createCell
						 * (++k).setCellValue(generateTTUMBeanObj
						 * .getStAmount());
						 * header2.createCell(++k).setCellValue(
						 * generateTTUMBeanObj.getStTran_particulars());
						 * header2.
						 * createCell(++k).setCellValue(generateTTUMBeanObj
						 * .getStCard_Number());
						 * header2.createCell(++k).setCellValue("INR");
						 * header2.createCell
						 * (++k).setCellValue(generateTTUMBeanObj
						 * .getStAmount());
						 * header2.createCell(++k).setCellValue(
						 * generateTTUMBeanObj.getStRemark());
						 * 
						 * inRowCount++;
						 */

					} catch (Exception e) {

						e.printStackTrace();
						logger.error(e.getMessage());
					}
				}

			} else {
				SXSSFRow aRow = (SXSSFRow) sheet.createRow(1);
				aRow.createCell(1).setCellValue("No Records Found.");
			}

			// response.getOutputStream().flush();

			workbook1.write(outStream);
			outStream.close();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

}
