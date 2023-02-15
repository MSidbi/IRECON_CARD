package com.recon.model;

import java.util.ArrayList;
import java.util.List;

public class FilterationBean {

private String stFilesAvail;
List<FilterationBean> file_list = new ArrayList<FilterationBean>();
List<FilterationBean> search_params = new ArrayList<FilterationBean>();


private String stFileSelected;
private String stCategory;
private String stFile_id;
private String stTable_name;
private int inseg_tran_id;
private String stFile_headers;
private String stFile_Name;
private int fileId;

private int inRec_Set_Id;

//search parameters
private String stSearch_header;

private String stMerger_Category;
private String stSubCategory;
private String stSearch_pattern;
private String stsearch_Startcharpos;
private String stsearch_Endcharpos;
private String stSearch_padding;
private String stSearch_Condition;
private String stSearch_Datatype;
private String stEntry_by;
private String stFile_date;





public static final String FILENAME = "filename";

public static final String FILE_CATEGORY = "file_category";




public String getStFileSelected() {
	return stFileSelected;
}

public void setStFileSelected(String stFileSelected) {
	this.stFileSelected = stFileSelected;
}

public String getStFilesAvail() {
	return stFilesAvail;
}

public void setStFilesAvail(String stFilesAvail) {
	this.stFilesAvail = (stFilesAvail == null ? null : stFilesAvail.trim() )   ;
}

public List<FilterationBean> getFile_list() {
	return file_list;
}

public void setFile_list(List<FilterationBean> file_list) {
	this.file_list = file_list ;
}

public String getStCategory() {
	return stCategory;
}

public void setStCategory(String stCategory) {
	this.stCategory = stCategory.trim();
}

public String getStFile_id() {
	return stFile_id;
}

public void setStFile_id(String stFile_id) {
	this.stFile_id = stFile_id.trim();
}

public String getStTable_name() {
	return stTable_name;
}

public void setStTable_name(String stTable_name) {
	this.stTable_name = stTable_name ;
}

public int getInseg_tran_id() {
	return inseg_tran_id;
}

public void setInseg_tran_id(int inseg_tran_id) {
	this.inseg_tran_id = inseg_tran_id;
}

public String getStFile_headers() {
	return stFile_headers ;
}

public void setStFile_headers(String stFile_headers) {
	this.stFile_headers = (stFile_headers == null ? null : stFile_headers.trim())  ;
}
public String getStFile_Name() {
	return stFile_Name.trim();
}

public void setStFile_Name(String stFile_Name) {
	this.stFile_Name = (stFile_Name == null ? null : stFile_Name)  ;
}

public String getStSearch_header() {
	return stSearch_header ;
}

public void setStSearch_header(String stSearch_header) {
	this.stSearch_header = (stSearch_header == null ? null : stSearch_header.trim())  ;
}

public String getStSearch_pattern() {
	return stSearch_pattern;
}

public void setStSearch_pattern(String stSearch_pattern) {
	this.stSearch_pattern = (stSearch_pattern ==null ? null : stSearch_pattern.trim()) ;
}

public String getStSearch_padding() {
	return stSearch_padding;
}

public void setStSearch_padding(String stSearch_padding) {
	this.stSearch_padding =  (stSearch_padding == null ? null : stSearch_padding.trim())  ;
}

public List<FilterationBean> getSearch_params() {
	return search_params;
}

public void setSearch_params(List<FilterationBean> search_params) {
	this.search_params = search_params;
}

public String getStEntry_by() {
	return stEntry_by.trim();
}

public void setStEntry_by(String stEntry_by) {
	this.stEntry_by = stEntry_by;
}

public String getStsearch_Startcharpos() {
	return stsearch_Startcharpos;
}

public void setStsearch_Startcharpos(String stsearch_Startcharpos) {
	this.stsearch_Startcharpos = stsearch_Startcharpos;
}

public String getStsearch_Endcharpos() {
	return stsearch_Endcharpos;
}

public void setStsearch_Endcharpos(String stsearch_Endcharpos) {
	this.stsearch_Endcharpos = stsearch_Endcharpos;
}

public String getStSearch_Condition() {
	return stSearch_Condition;
}

public void setStSearch_Condition(String stSearch_Condition) {
	this.stSearch_Condition = stSearch_Condition;
}

public String getStFile_date() {
	return stFile_date;
}

public void setStFile_date(String stFile_date) {
	this.stFile_date = stFile_date;
}

public String getStSearch_Datatype() {
	return stSearch_Datatype;
}

public void setStSearch_Datatype(String stSearch_Datatype) {
	this.stSearch_Datatype = (stSearch_Datatype == null ? null : stSearch_Datatype.trim())  ;
}

public int getFileId() {
	return fileId;
}

public void setFileId(int fileId) {
	this.fileId = fileId;
}

public String getStSubCategory() {
	return stSubCategory;
}

public void setStSubCategory(String stSubCategory) {
	this.stSubCategory = (stSubCategory == null ? null : stSubCategory.trim())  ;
}

public String getStMerger_Category() {
	return stMerger_Category;
}

public void setStMerger_Category(String stMerger_Category) {
	this.stMerger_Category = stMerger_Category.trim();
}

public int getInRec_Set_Id() {
	return inRec_Set_Id;
}

public void setInRec_Set_Id(int inRec_Set_Id) {
	this.inRec_Set_Id = inRec_Set_Id;
}




}
