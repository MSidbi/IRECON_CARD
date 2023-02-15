package com.recon.model;

import java.util.List;

import com.informix.util.stringUtil;

public class CompareBean {

	private String stTable1;
	private String stTable2;
	private String stCategory;
	private String stSubCategory;
	private String stTable1_SubCategory;
	private String stTable2_SubCategory;
	private String stMergeCategory;
	private String stMatched_file1;
	private String stMatched_file2;
	private List<String> stTableList;
	private String stMatchTable1_header;
	private String stMatchTable2_header;
	private String stMatchTable1_Padding;
	private String stMatchTable2_Padding;
	private String stMatchTable1_startcharpos;
	private String stMatchTable2_startcharpos;
	/*private String stMatchTable1_endcharpos;
	private String stMatchTable2_endcharpos;*/
	private String stMatchTable1_charSize;
	private String stMatchTable2_charSize;
	private String stMatchTable1_Datatype;
	private String stMatchTable2_Datatype;
	private String stMatchTable1_DatePattern;
	private String stMatchTable2_DatePattern;
	private String stEntryBy;
	private String stFile_date;
	private int inrec_set_id;
	private String stRecon_Column;
	private String stRecon_Datatype;
	private String stTable1_Category;
	private String stTable2_Category;
	
	public String getStTable1() {
		return stTable1;
	}
	public void setStTable1(String stTable1) {
		this.stTable1 = stTable1;
	}
	public String getStTable2() {
		return stTable2;
	}
	public void setStTable2(String stTable2) {
		this.stTable2 = stTable2;
	}
	public List<String> getStTableList() {
		return stTableList;
	}
	public void setStTableList(List<String> stTableList) {
		this.stTableList = stTableList;
	}
	public String getStMatchTable1_header() {
		return stMatchTable1_header;
	}
	public void setStMatchTable1_header(String stMatchTable1_header) {
		this.stMatchTable1_header = stMatchTable1_header;
	}
	public String getStMatchTable2_header() {
		return stMatchTable2_header;
	}
	public void setStMatchTable2_header(String stMatchTable2_header) {
		this.stMatchTable2_header = stMatchTable2_header;
	}
	public String getStMatchTable1_Padding() {
		return stMatchTable1_Padding;
	}
	public void setStMatchTable1_Padding(String stMatchTable1_Padding) {
		this.stMatchTable1_Padding = stMatchTable1_Padding;
	}
	public String getStMatchTable2_Padding() {
		return stMatchTable2_Padding;
	}
	public void setStMatchTable2_Padding(String stMatchTable2_Padding) {
		this.stMatchTable2_Padding = stMatchTable2_Padding;
	}
	public String getStMatchTable1_startcharpos() {
		return stMatchTable1_startcharpos;
	}
	public void setStMatchTable1_startcharpos(String stMatchTable1_startcharpos) {
		this.stMatchTable1_startcharpos = stMatchTable1_startcharpos;
	}
	public String getStMatchTable2_startcharpos() {
		return stMatchTable2_startcharpos;
	}
	public void setStMatchTable2_startcharpos(String stMatchTable2_startcharpos) {
		this.stMatchTable2_startcharpos = stMatchTable2_startcharpos;
	}
	public String getStMatchTable1_charSize() {
		return stMatchTable1_charSize;
	}
	public void setStMatchTable1_charSize(String stMatchTable1_charSize) {
		this.stMatchTable1_charSize = stMatchTable1_charSize;
	}
	public String getStMatchTable2_charSize() {
		return stMatchTable2_charSize;
	}
	public void setStMatchTable2_charSize(String stMatchTable2_charSize) {
		this.stMatchTable2_charSize = stMatchTable2_charSize;
	}
	public String getStMatchTable1_Datatype() {
		return stMatchTable1_Datatype;
	}
	public void setStMatchTable1_Datatype(String stMatch_Datatype) {
		this.stMatchTable1_Datatype = stMatch_Datatype;
	}
	public String getStRecon_Column() {
		return stRecon_Column;
	}
	public void setStRecon_Column(String stRecon_Column) {
		this.stRecon_Column = stRecon_Column;
	}
	public String getStMatchTable1_DatePattern() {
		return stMatchTable1_DatePattern;
	}
	public void setStMatchTable1_DatePattern(String stMatchTable1_DatePattern) {
		this.stMatchTable1_DatePattern = stMatchTable1_DatePattern;
	}
	public String getStMatchTable2_DatePattern() {
		return stMatchTable2_DatePattern;
	}
	public void setStMatchTable2_DatePattern(String stMatchTable2_DatePattern) {
		this.stMatchTable2_DatePattern = stMatchTable2_DatePattern;
	}
	public String getStEntryBy() {
		return stEntryBy;
	}
	public void setStEntryBy(String stEntryBy) {
		this.stEntryBy = stEntryBy;
	}
	public String getStRecon_Datatype() {
		return stRecon_Datatype;
	}
	public void setStRecon_Datatype(String stRecon_Datatype) {
		this.stRecon_Datatype = stRecon_Datatype;
	}
	public String getStFile_date() {
		return stFile_date;
	}
	public void setStFile_date(String stFile_date) {
		this.stFile_date = stFile_date;
	}
	public String getStMatchTable2_Datatype() {
		return stMatchTable2_Datatype;
	}
	public void setStMatchTable2_Datatype(String stMatchTable2_Datatype) {
		this.stMatchTable2_Datatype = stMatchTable2_Datatype;
	}
	public int getInrec_set_id() {
		return inrec_set_id;
	}
	public void setInrec_set_id(int inrec_set_id) {
		this.inrec_set_id = inrec_set_id;
	}
	public String getStCategory() {
		return stCategory;
	}
	public void setStCategory(String stCategory) {
		this.stCategory = stCategory;
	}
	public String getStSubCategory() {
		return stSubCategory;
	}
	public void setStSubCategory(String stSubCategory) {
		this.stSubCategory = stSubCategory;
	}
	public String getStMergeCategory() {
		return stMergeCategory;
	}
	public void setStMergeCategory(String stMergeCategory) {
		this.stMergeCategory = stMergeCategory;
	}
	public String getStMatched_file1() {
		return stMatched_file1;
	}
	public void setStMatched_file1(String stMatched_file1) {
		this.stMatched_file1 = stMatched_file1;
	}
	public String getStMatched_file2() {
		return stMatched_file2;
	}
	public void setStMatched_file2(String stMatched_file2) {
		this.stMatched_file2 = stMatched_file2;
	}
	public String getStTable1_SubCategory() {
		return stTable1_SubCategory;
	}
	public void setStTable1_SubCategory(String stTable1_SubCategory) {
		this.stTable1_SubCategory = stTable1_SubCategory;
	}
	public String getStTable2_SubCategory() {
		return stTable2_SubCategory;
	}
	public void setStTable2_SubCategory(String stTable2_SubCategory) {
		this.stTable2_SubCategory = stTable2_SubCategory;
	}
	public String getStTable1_Category() {
		return stTable1_Category;
	}
	public void setStTable1_Category(String stTable1_Category) {
		this.stTable1_Category = stTable1_Category;
	}
	public String getStTable2_Category() {
		return stTable2_Category;
	}
	public void setStTable2_Category(String stTable2_Category) {
		this.stTable2_Category = stTable2_Category;
	}
	
	

}
