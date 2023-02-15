package com.recon.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;


@Component
public class ConfigurationBean {

	
	private String stFileName;
	private String stCategory;
	private String stSubCategory; 
	private String stCheckTable;
	private String stHeader;
	private String stSearch_Pattern;
	private String stPadding;
	private int inChar_Position;
	private int inFileId;
	private String stEntry_By;
	private int inStart_Char_Position;
	private int inEnd_char_position;
	
	//sushant Date :13/04/2017
	private String fileLocation;
	private String filePath;
	private String ftpUser;
	private String ftpPwd;
	private String  tableName;
	private String dataSeparator;
	private int ftpPort;
	private String activeFlag;
	//sushant Date :08/05/2017
	private int rdDataFrm;
	private String charpatt;
	private String fileType;
	private String condition;
	private String knock_offFlag;
	private String classify_flag;
	private String knockoff_col;
	private String knockoff_OrgVal;
	private String knockoff_comprVal;

	private String knockoff_header;
	private String knockoffSrch_Pattern;
	private String knockoff_stPadding;
	private int knockoffStart_Char_Pos;
	private int knockoffEnd_char_pos;
	private String knockoff_condition;
	private String prev_tblFlag;
	private String prev_table;
	
	List<ConfigurationBean> clasify_dtl_list = new ArrayList<ConfigurationBean>();
	
	
	
	
	public String getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(String activeFlag) {
		this.activeFlag = activeFlag;
	}
	
	
	
	
	public String getKnockoff_condition() {
		return knockoff_condition;
	}
	public void setKnockoff_condition(String knockoff_condition) {
		this.knockoff_condition = knockoff_condition;
	}
	public String getKnockoff_header() {
		return knockoff_header;
	}
	public void setKnockoff_header(String knockoff_header) {
		this.knockoff_header = knockoff_header;
	}
	public String getKnockoffSrch_Pattern() {
		return knockoffSrch_Pattern;
	}
	public void setKnockoffSrch_Pattern(String knockoffSrch_Pattern) {
		this.knockoffSrch_Pattern = knockoffSrch_Pattern;
	}
	public String getKnockoff_stPadding() {
		return knockoff_stPadding;
	}
	public void setKnockoff_stPadding(String knockoff_stPadding) {
		this.knockoff_stPadding = knockoff_stPadding;
	}
	public int getKnockoffStart_Char_Pos() {
		return knockoffStart_Char_Pos;
	}
	public void setKnockoffStart_Char_Pos(int knockoffStart_Char_Pos) {
		this.knockoffStart_Char_Pos = knockoffStart_Char_Pos;
	}
	public int getKnockoffEnd_char_pos() {
		return knockoffEnd_char_pos;
	}
	public void setKnockoffEnd_char_pos(int knockoffEnd_char_pos) {
		this.knockoffEnd_char_pos = knockoffEnd_char_pos;
	}
	public String getKnockoff_col() {
		return knockoff_col;
	}
	public void setKnockoff_col(String knockoff_col) {
		this.knockoff_col = knockoff_col;
	}
	public String getKnockoff_OrgVal() {
		return knockoff_OrgVal;
	}
	public void setKnockoff_OrgVal(String knockoff_OrgVal) {
		this.knockoff_OrgVal = knockoff_OrgVal;
	}
	public String getKnockoff_comprVal() {
		return knockoff_comprVal;
	}
	public void setKnockoff_comprVal(String knockoff_comprVal) {
		this.knockoff_comprVal = knockoff_comprVal;
	}
	public String getStEntry_By() {
		return stEntry_By;
	}
	public void setStEntry_By(String stEntry_By) {
		this.stEntry_By = stEntry_By;
	}
	public int getInFileId() {
		return inFileId;
	}
	public void setInFileId(int inFileId) {
		this.inFileId = inFileId;
	}
	List<ConfigurationBean> comp_dtl_list = new ArrayList<ConfigurationBean>();
	
	
	public List<ConfigurationBean> getComp_dtl_list() {
		return comp_dtl_list;
	}
	public void setComp_dtl_list(List<ConfigurationBean> comp_dtl_list) {
		this.comp_dtl_list = comp_dtl_list;
	}
	public String getStFileName() {
		return stFileName;
	}
	public void setStFileName(String stFileName) {
		this.stFileName = stFileName;
	}
	public String getStCategory() {
		return stCategory;
	}
	public void setStCategory(String stCategory) {
		this.stCategory = stCategory;
	}
	public String getStCheckTable() {
		return stCheckTable;
	}
	public void setStCheckTable(String stCheckTable) {
		this.stCheckTable = stCheckTable;
	}
	public String getStHeader() {
		return stHeader;
	}
	public void setStHeader(String stHeader) {
		this.stHeader = stHeader;
	}
	public String getStSearch_Pattern() {
		return stSearch_Pattern;
	}
	public void setStSearch_Pattern(String stSearch_Pattern) {
		this.stSearch_Pattern = stSearch_Pattern;
	}
	public String getStPadding() {
		return stPadding;
	}
	public void setStPadding(String stPadding) {
		this.stPadding = stPadding;
	}
	public int getInChar_Position() {
		return inChar_Position;
	}
	public void setInChar_Position(int inChar_Position) {
		this.inChar_Position = inChar_Position;
	}
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFtpUser() {
		return ftpUser;
	}
	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}
	public String getFtpPwd() {
		return ftpPwd;
	}
	public void setFtpPwd(String ftpPwd) {
		this.ftpPwd = ftpPwd;
	}
	public String getFileLocation() {
		return fileLocation;
	}
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	public int getFtpPort() {
		return ftpPort;
	}
	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getDataSeparator() {
		return dataSeparator;
	}
	public void setDataSeparator(String dataSeparator) {
		this.dataSeparator = dataSeparator;
	}
	public int getInStart_Char_Position() {
		return inStart_Char_Position;
	}
	public void setInStart_Char_Position(int inStart_Char_Position) {
		this.inStart_Char_Position = inStart_Char_Position;
	}
	public int getInEnd_char_position() {
		return inEnd_char_position;
	}
	public void setInEnd_char_position(int inEnd_char_position) {
		this.inEnd_char_position = inEnd_char_position;
	}
	public int getRdDataFrm() {
		return rdDataFrm;
	}
	public void setRdDataFrm(int rdDataFrm) {
		this.rdDataFrm = rdDataFrm;
	}
	public String getCharpatt() {
		return charpatt;
	}
	public void setCharpatt(String charpatt) {
		this.charpatt = charpatt;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getKnock_offFlag() {
		return knock_offFlag;
	}
	public void setKnock_offFlag(String knock_offFlag) {
		this.knock_offFlag = knock_offFlag;
	}
	public List<ConfigurationBean> getClasify_dtl_list() {
		return clasify_dtl_list;
	}
	public void setClasify_dtl_list(List<ConfigurationBean> clasify_dtl_list) {
		this.clasify_dtl_list = clasify_dtl_list;
	}
	public String getStSubCategory() {
		return stSubCategory;
	}
	public void setStSubCategory(String stSubCategory) {
		this.stSubCategory = stSubCategory;
	}
	public String getClassify_flag() {
		return classify_flag;
	}
	public void setClassify_flag(String classify_flag) {
		this.classify_flag = classify_flag;
	}
	public String getPrev_tblFlag() {
		return prev_tblFlag;
	}
	public void setPrev_tblFlag(String prev_tblFlag) {
		this.prev_tblFlag = prev_tblFlag;
	}
	public String getPrev_table() {
		return prev_table;
	}
	public void setPrev_table(String prev_table) {
		this.prev_table = prev_table;
	}
	
	
}
