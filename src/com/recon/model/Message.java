package com.recon.model;

import java.util.ArrayList;
import java.util.List;


public abstract class Message {

	public Message()
	{
		 
		Fields = new ArrayList<Field>();
		
		 
	}
	
	public List<Field> Fields  ;
	private int binaryBitMap;
	private String dataString;
	
	public String getDataString() {
		return dataString;
	}

	public void setDataString(String dataString) {
		this.dataString = dataString;
	}

	public int getBinaryBitMap() {
		return binaryBitMap;
	}

	public void setBinaryBitMap(int binaryBitMap) {
		this.binaryBitMap = binaryBitMap;
	}	
	
	public Field getFieldbyNumber(int no)
	{
		Field field = null;
		for(Field f : Fields)
			if(no == f.getFieldNumber())
				field = f;
		return field;
	}
	
	public void addField(Field field) throws Exception
	{
//                System.out.println(">>>> Field to add:  " + field.getName() + "##" + field.getFieldNumber());
		int count = 0;
		for(Field f : Fields)
			if(field.getFieldNumber() == f.getFieldNumber())
				count++;		
		if(count > 0){
			//throw new Exception("This message already inserted : " + field.getFieldNumber() + " : " + field.getName()); 
		}
				else{
			Fields.add(field);	
		}
			//                        System.out.println(">>>>> Field added");
				
	}
	public abstract void printMessage() throws Exception;
		
}
