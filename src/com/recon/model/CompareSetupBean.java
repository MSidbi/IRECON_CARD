package com.recon.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class CompareSetupBean {

	private String layerCount;
	private String category;
	private String stSubCategory;
	private int inFileId;
	private String stFileName;
	private String stfileType; 
	private String dataseparator;
	private String rddatafrm;
	private String charpatt;
	private String activeFlag;
	private String compareLvl;
	private String remarks;
	//added by INT5779
	private String stTable1_category;
	private String stTable2_category;
	private String stMerger_Category;
	
	private String stTableName;
	
	/* jit -avoid duplicate file name --  start */
	private int ID;
	private int FILEID;
	private int SUB_FILEID;
	private String P_FILE_NAME;
	private String FILEDATE;
	
	
	public String getFILEDATE() {
		return FILEDATE;
	}
	public void setFILEDATE(String fILEDATE) {
		FILEDATE = fILEDATE;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getFILEID() {
		return FILEID;
	}
	public void setFILEID(int fILEID) {
		FILEID = fILEID;
	}
	public int getSUB_FILEID() {
		return SUB_FILEID;
	}
	public void setSUB_FILEID(int sUB_FILEID) {
		SUB_FILEID = sUB_FILEID;
	}
	public String getP_FILE_NAME() {
		return P_FILE_NAME;
	}
	public void setP_FILE_NAME(String p_FILE_NAME) {
		P_FILE_NAME = p_FILE_NAME;
	}
	/* jit -avoid duplicate file name --  end */
	
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	private int compareFile1;
	private int compareFile2;
	private int compareFile3;
	
	private String stRelaxParam1;
	private String stRelaxParam2; 
	
	
	private String compreFileName1;
	private String compreFileName2;
	private String compreFile3;
	private int rec_set_id;
	private String fileType;
	
	private String table_header;
	private String padding;
	private String start_charpos;
	private String condition;
	private String charsize;
	private String filename;
	private String pattern;
	private String entry_by;
	private String entry_date;
	private String match_id;
	private String datatype;
	private String srch_Pattern;
	private String stPadding;
	private String matchCondn;
	
	private String file1match;
	private String file2match;
	
	
	
	private String filter_Flag,	knockoff_Flag,comapre_Flag,manualcompare_Flag,upload_Flag,manupload_flag;
	
	private File dataFile;
	private String fileDate;
	
	private String createdBy;
	
	private String entryBy;
	
		
	
	private String [] file1;
	
	private FileColumnDtls[] fileColumnDtls;
	
	List<FileColumnDtls> columnDtls = new ArrayList<FileColumnDtls>();
	
	List <CompareSetupBean> setup_dtl_list =new  ArrayList<CompareSetupBean>();
	
	
	public String getCompareLvl() {
		return compareLvl;
	}
	public void setCompareLvl(String compareLvl) {
		this.compareLvl = compareLvl;
	}
	public String getLayerCount() {
		return layerCount;
	}
	public void setLayerCount(String layerCount) {
		this.layerCount = layerCount;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public List<CompareSetupBean> getSetup_dtl_list() {
		return setup_dtl_list;
	}
	public void setSetup_dtl_list(List<CompareSetupBean> setup_dtl_list) {
		this.setup_dtl_list = setup_dtl_list;
	}
	public int getCompareFile1() {
		return compareFile1;
	}
	public void setCompareFile1(int compareFile1) {
		this.compareFile1 = compareFile1;
	}
	public int getCompareFile2() {
		return compareFile2;
	}
	public void setCompareFile2(int compareFile2) {
		this.compareFile2 = compareFile2;
	}
	public int getCompareFile3() {
		return compareFile3;
	}
	public void setCompareFile3(int compareFile3) {
		this.compareFile3 = compareFile3;
	}
	public int getInFileId() {
		return inFileId;
	}
	public void setInFileId(int inFileId) {
		this.inFileId = inFileId;
	}
	public String getStFileName() {
		return stFileName;
	}
	public void setStFileName(String stFileName) {
		this.stFileName = stFileName;
	}
	public String getDataseparator() {
		return dataseparator;
	}
	public void setDataseparator(String dataseparator) {
		this.dataseparator = dataseparator;
	}
	public String getRddatafrm() {
		return rddatafrm;
	}
	public void setRddatafrm(String rddatafrm) {
		this.rddatafrm = rddatafrm;
	}
	public String getCharpatt() {
		return charpatt;
	}
	public void setCharpatt(String charpatt) {
		this.charpatt = charpatt;
	}
	public String getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}
	public List<FileColumnDtls> getColumnDtls() {
		return columnDtls;
	}
	public void setColumnDtls(List<FileColumnDtls> columnDtls) {
		this.columnDtls = columnDtls;
	}
	public String [] getFile1() {
		return file1;
	}
	public void setFile1(String [] file1) {
		this.file1 = file1;
	}
	public FileColumnDtls[] getFileColumnDtls() {
		return fileColumnDtls;
	}
	public void setFileColumnDtls(FileColumnDtls[] fileColumnDtls) {
		this.fileColumnDtls = fileColumnDtls;
	}
	public String getEntryBy() {
		return entryBy;
	}
	public void setEntryBy(String entryBy) {
		this.entryBy = entryBy;
	}
	
	public String getCompreFileName1() {
		return compreFileName1;
	}
	public void setCompreFileName1(String compreFileName1) {
		this.compreFileName1 = compreFileName1;
	}
	public String getCompreFileName2() {
		return compreFileName2;
	}
	public void setCompreFileName2(String compreFileName2) {
		this.compreFileName2 = compreFileName2;
	}
	public String getCompreFile3() {
		return compreFile3;
	}
	public void setCompreFile3(String compreFile3) {
		this.compreFile3 = compreFile3;
	}
	public int getRec_set_id() {
		return rec_set_id;
	}
	public void setRec_set_id(int rec_set_id) {
		this.rec_set_id = rec_set_id;
	}
	public String getTable_header() {
		return table_header;
	}
	public void setTable_header(String table_header) {
		this.table_header = table_header;
	}
	public String getPadding() {
		return padding;
	}
	public void setPadding(String padding) {
		this.padding = padding;
	}
	public String getStart_charpos() {
		return start_charpos;
	}
	public void setStart_charpos(String start_charpos) {
		this.start_charpos = start_charpos;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getCharsize() {
		return charsize;
	}
	public void setCharsize(String charsize) {
		this.charsize = charsize;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getEntry_by() {
		return entry_by;
	}
	public void setEntry_by(String entry_by) {
		this.entry_by = entry_by;
	}
	public String getEntry_date() {
		return entry_date;
	}
	public void setEntry_date(String entry_date) {
		this.entry_date = entry_date;
	}
	public String getMatch_id() {
		return match_id;
	}
	public void setMatch_id(String match_id) {
		this.match_id = match_id;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public File getDataFile() {
		return dataFile;
	}
	public void setDataFile(File dataFile) {
		this.dataFile = dataFile;
	}
	public String getFileDate() {
		return fileDate;
	}
	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getStfileType() {
		return stfileType;
	}
	public void setStfileType(String stfileType) {
		this.stfileType = stfileType;
	}
	public String getFilter_Flag() {
		return filter_Flag;
	}
	public void setFilter_Flag(String filter_Flag) {
		this.filter_Flag = filter_Flag;
	}
	public String getKnockoff_Flag() {
		return knockoff_Flag;
	}
	public void setKnockoff_Flag(String knockoff_Flag) {
		this.knockoff_Flag = knockoff_Flag;
	}
	public String getComapre_Flag() {
		return comapre_Flag;
	}
	public void setComapre_Flag(String comapre_Flag) {
		this.comapre_Flag = comapre_Flag;
	}
	public String getManualcompare_Flag() {
		return manualcompare_Flag;
	}
	public void setManualcompare_Flag(String manualcompare_Flag) {
		this.manualcompare_Flag = manualcompare_Flag;
	}
	public String getUpload_Flag() {
		return upload_Flag;
	}
	public void setUpload_Flag(String upload_Flag) {
		this.upload_Flag = upload_Flag;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getManupload_flag() {
		return manupload_flag;
	}
	public void setManupload_flag(String manupload_flag) {
		this.manupload_flag = manupload_flag;
	}
	public String getSrch_Pattern() {
		return srch_Pattern;
	}
	public void setSrch_Pattern(String srch_Pattern) {
		this.srch_Pattern = srch_Pattern;
	}
	public String getStPadding() {
		return stPadding;
	}
	public void setStPadding(String stPadding) {
		this.stPadding = stPadding;
	}
	public String getMatchCondn() {
		return matchCondn;
	}
	public void setMatchCondn(String matchCondn) {
		this.matchCondn = matchCondn;
	}
	public String getStSubCategory() {
		return stSubCategory;
	}
	public void setStSubCategory(String stSubCategory) {
		this.stSubCategory = stSubCategory;
	}
	public String getFile1match() {
		return file1match;
	}
	public void setFile1match(String file1match) {
		this.file1match = file1match;
	}
	public String getFile2match() {
		return file2match;
	}
	public void setFile2match(String file2match) {
		this.file2match = file2match;
	}
	public String getStRelaxParam1() {
		return stRelaxParam1;
	}
	public void setStRelaxParam1(String stRelaxParam1) {
		this.stRelaxParam1 = stRelaxParam1;
	}
	public String getStRelaxParam2() {
		return stRelaxParam2;
	}
	public void setStRelaxParam2(String stRelaxParam2) {
		this.stRelaxParam2 = stRelaxParam2;
	}
	public String getStTable1_category() {
		return stTable1_category;
	}
	public void setStTable1_category(String stTable1_category) {
		this.stTable1_category = stTable1_category;
	}
	public String getStTable2_category() {
		return stTable2_category;
	}
	public void setStTable2_category(String stTable2_category) {
		this.stTable2_category = stTable2_category;
	}
	public String getStMerger_Category() {
		return stMerger_Category;
	}
	public void setStMerger_Category(String stMerger_Category) {
		this.stMerger_Category = stMerger_Category;
	}
	public String getStTableName() {
		return stTableName;
	}
	public void setStTableName(String stTableName) {
		this.stTableName = stTableName;
	}

	
	
}
