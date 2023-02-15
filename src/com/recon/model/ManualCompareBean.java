package com.recon.model;

import java.util.ArrayList;
import java.util.List;

public class ManualCompareBean {
	
	String category;
	int file_Id;
	String stFile_Name;
	String file_Header;
	String stPadding;
	String stChar_Pos;
	String stChar_Size;
	String stSearch_Pattern;
	String stDataPattern;
	String stDatatype;
	String refFileId;
	String refFileHdr;
	int comp_File;
	String stComp_File;
	int man_File;
	String stMan_file;
	String condition;
	String entryBy;
	List<ManualCompareBean> comp_dtl_list = new ArrayList<ManualCompareBean>();
	List<ManualFileColumnDtls> columnDtls = new ArrayList<ManualFileColumnDtls>();
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public int getFile_Id() {
		return file_Id;
	}
	public void setFile_Id(int file_Id) {
		this.file_Id = file_Id;
	}
	public String getFile_Header() {
		return file_Header;
	}
	public void setFile_Header(String file_Header) {
		this.file_Header = file_Header;
	}
	public String getStPadding() {
		return stPadding;
	}
	public void setStPadding(String stPadding) {
		this.stPadding = stPadding;
	}
	public String getStChar_Pos() {
		return stChar_Pos;
	}
	public void setStChar_Pos(String stChar_Pos) {
		this.stChar_Pos = stChar_Pos;
	}
	public String getStChar_Size() {
		return stChar_Size;
	}
	public void setStChar_Size(String stChar_Size) {
		this.stChar_Size = stChar_Size;
	}
	public String getStDataPattern() {
		return stDataPattern;
	}
	public void setStDataPattern(String stDataPattern) {
		this.stDataPattern = stDataPattern;
	}
	public String getStDatatype() {
		return stDatatype;
	}
	public void setStDatatype(String stDatatype) {
		this.stDatatype = stDatatype;
	}
	public List<ManualCompareBean> getComp_dtl_list() {
		return comp_dtl_list;
	}
	public void setComp_dtl_list(List<ManualCompareBean> comp_dtl_list) {
		this.comp_dtl_list = comp_dtl_list;
	}
	public List<ManualFileColumnDtls> getColumnDtls() {
		return columnDtls;
	}
	public void setColumnDtls(List<ManualFileColumnDtls> columnDtls) {
		this.columnDtls = columnDtls;
	}
	public int getComp_File() {
		return comp_File;
	}
	public void setComp_File(int comp_File) {
		this.comp_File = comp_File;
	}
	public String getStComp_File() {
		return stComp_File;
	}
	public void setStComp_File(String stComp_File) {
		this.stComp_File = stComp_File;
	}
	public int getMan_File() {
		return man_File;
	}
	public void setMan_File(int man_File) {
		this.man_File = man_File;
	}
	public String getStMan_file() {
		return stMan_file;
	}
	public void setStMan_file(String stMan_file) {
		this.stMan_file = stMan_file;
	}
	public String getStFile_Name() {
		return stFile_Name;
	}
	public void setStFile_Name(String stFile_Name) {
		this.stFile_Name = stFile_Name;
	}
	public String getRefFileId() {
		return refFileId;
	}
	public void setRefFileId(String refFileId) {
		this.refFileId = refFileId;
	}
	public String getStSearch_Pattern() {
		return stSearch_Pattern;
	}
	public void setStSearch_Pattern(String stSearch_Pattern) {
		this.stSearch_Pattern = stSearch_Pattern;
	}
	public String getRefFileHdr() {
		return refFileHdr;
	}
	public void setRefFileHdr(String refFileHdr) {
		this.refFileHdr = refFileHdr;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getEntryBy() {
		return entryBy;
	}
	public void setEntryBy(String entryBy) {
		this.entryBy = entryBy;
	}
 

}
