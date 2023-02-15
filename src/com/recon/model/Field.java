package com.recon.model;

public class Field implements Comparable<Field>{

	public Field()
	{	}
	public Field(
			int fieldNumber,
			String name,
			int maxSize,
			String fieldValue,
			boolean isPrefix,
			char extraChar,
			boolean isSizeFix,
			String extendFormat,
			String field_processor
			)
	{
		this.maxSize = maxSize;
		this.name = name;
		this.fieldNumber = fieldNumber;
		this.fieldValue = fieldValue;
		this.isPrefix = isPrefix;
		this.extraChar = extraChar;
		this.isSizeFix = isSizeFix;
		this.extendFormat = extendFormat;
		this.field_processor = field_processor;
	}
	private int fieldSize;
	private int maxSize;
	private String name;
	private int fieldNumber;
	private String fieldValue;
	private boolean isPrefix;
	private char extraChar;	
	private boolean isSizeFix; 
	private String extendFormat;
	private String field_processor;
	
	
	
	public String getExtendFormat() {
		return extendFormat;
	}
	public void setExtendFormat(String extendFormat) {
		this.extendFormat = extendFormat;
	}
	
	public int getFieldSize() {
		return fieldSize;
	}
	public void setFieldSize(int fieldSize) {
		this.fieldSize = fieldSize;
	}
	public int getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getFieldNumber() {
		return fieldNumber;
	}
	public void setFieldNumber(int fieldNumber) {
		this.fieldNumber = fieldNumber;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	public boolean isPrefix() {
		return isPrefix;
	}
	public void setPrefix(boolean isPrefix) {
		this.isPrefix = isPrefix;
	}
	public char getExtraChar() {
		return extraChar;
	}
	public void setExtraChar(char extraChar) {
		this.extraChar = extraChar;
	}	
	public boolean getIsSizeFix() {
		return isSizeFix;
	}
	public void setIsSizeFix(boolean isSizeFix) {
		this.isSizeFix = isSizeFix;
	}	
        @Override
	public int compareTo(Field field) {
		return new Integer(this.fieldNumber).compareTo(field.fieldNumber);		
	}
		public String getField_processor() {
			return field_processor;
		}
		public void setField_processor(String field_processor) {
			this.field_processor = field_processor;
		}
}
