package com.recon.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionUtility   {


    
    public static String convertHexToAscii(String hexInput) {
        String binConv = "";
        String asciiTemp = "";
        String bitmapAscii = "";
        if (hexInput.length() % 8 != 0) {

            return bitmapAscii;
        }
        int val = 0;
       // String binStr = "";
        for (int temp = 0; temp < hexInput.length(); temp = temp + 2) {
            binConv = hexInput.substring(val, val + 2);
            int check = Integer.parseInt(binConv, 16);
            char strA = (char) check;
            asciiTemp = asciiTemp + strA;
            val = val + 2;
        }
        bitmapAscii = asciiTemp;
        return bitmapAscii;
    }
    
    final protected static char[] HEXARRAY = "0123456789abcdef".toCharArray();

    public static String encodeHexString( byte[] bytes ) {
        char[] hexChars = new char[bytes.length * 8];
        for (int j = 0; j < bytes.length; j++) {
        	int  v= bytes[j] & 0xFF;
            hexChars[j * 2] = HEXARRAY[v >> 4];
            hexChars[j * 2 + 1] = HEXARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    
    
    public static int  encodeHexInt( byte[] bytes ) {
    	        char[] hexChars = new char[bytes.length * 8];
    	        for (int j = 0; j < bytes.length; j++) {
    	        	int  v= bytes[j] & 0xFF;
    	            hexChars[j * 2] = HEXARRAY[v >> 4];
    	            hexChars[j * 2 + 1] = HEXARRAY[v & 0x0F];
    	        }
    	        return Integer.parseInt(new String(hexChars)) ;
    	    }

    public static String generateHex(String bitMap) throws Exception {

        String hexa = "";
        String tempHex = "";
        if (bitMap.length() % 8 != 0) {
        }
        try {
            int count = bitMap.length() / 4;
            int temp = 0;
            String partOfMap = "";
            for (int i = 0; i < count; i++) {
                partOfMap = bitMap.substring(temp, temp + 4);

                if (partOfMap.equals("0000")) {
                    tempHex = tempHex + "0";
                } else if (partOfMap.equals("0001")) {
                    tempHex = tempHex + "1";
                } else if (partOfMap.equals("0010")) {
                    tempHex = tempHex + "2";
                } else if (partOfMap.equals("0011")) {
                    tempHex = tempHex + "3";
                } else if (partOfMap.equals("0100")) {
                    tempHex = tempHex + "4";
                } else if (partOfMap.equals("0101")) {
                    tempHex = tempHex + "5";
                } else if (partOfMap.equals("0110")) {
                    tempHex = tempHex + "6";
                } else if (partOfMap.equals("0111")) {
                    tempHex = tempHex + "7";
                } else if (partOfMap.equals("1000")) {
                    tempHex = tempHex + "8";
                } else if (partOfMap.equals("1001")) {
                    tempHex = tempHex + "9";
                } else if (partOfMap.equals("1010")) {
                    tempHex = tempHex + "A";
                } else if (partOfMap.equals("1011")) {
                    tempHex = tempHex + "B";
                } else if (partOfMap.equals("1100")) {
                    tempHex = tempHex + "C";
                } else if (partOfMap.equals("1101")) {
                    tempHex = tempHex + "D";
                } else if (partOfMap.equals("1110")) {
                    tempHex = tempHex + "E";
                } else if (partOfMap.equals("1111")) {
                    tempHex = tempHex + "F";
                }
                temp = temp + 4;
            }

        } catch (Exception exp) {
            throw exp;
        }
        hexa = tempHex;
        return hexa;
    }

    public static String getAsciiToBinary(String asciiString) {
        char[] charArray = asciiString.toCharArray();
        String binaryString = "";
        for (char c : charArray) {
            String sb = new String(Integer.toBinaryString(c));
            if (sb.length() == 1) {
                sb = "0000000" + sb;
            } else if (sb.length() == 2) {
                sb = "000000" + sb;
            } else if (sb.length() == 3) {
                sb = "00000" + sb;
            } else if (sb.length() == 4) {
                sb = "0000" + sb;
            } else if (sb.length() == 5) {
                sb = "000" + sb;
            } else if (sb.length() == 6) {
                sb = "00" + sb;
            } else if (sb.length() == 7) {
                sb = "0" + sb;
            }
            binaryString = binaryString + sb;
        }
        return binaryString;
    }
    
    /** Added 09/07/2015 **/
    public String padLeft(String str, int iPadLen, char cPaddingChar) {
        return String.format("%1$" + iPadLen + "s", str).replace(' ',
                cPaddingChar);
    }

    public String padRight(String str, int iPadLen, char cPaddingChar) {
        return String.format("%1$-" + iPadLen + "s", str).replace(' ',
                cPaddingChar);
    }

    
  public static  String toBinary( byte[] bytes )
    {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }
    
  
 public static  ArrayList<String> toBinary_list( byte[] bytes )
  {
	 	//System.out.println(encodeHexString(bytes));
      ArrayList bin = new ArrayList();
      for( int i = 0; i < Byte.SIZE * bytes.length; i++ ){  
     	 bin.add(( bytes[i / Byte.SIZE]  << i %  Byte.SIZE & 0x80) == 0 ? '0' : '1');
      }
      return bin;
  }
 
 
public static int little2big(int i) {
	    return (i&0xff)<<24 | (i&0xff00)<<8 | (i&0xff0000)>>8 | (i>>24)&0xff;
	}
  



public static  Map<String, Object> _get_pds_fields(String field_data){
	 int field_pointer = 0;
	 Map<String, Object> key_value = new HashMap<String, Object>();
	
	while(field_pointer < field_data.length()){
		StringBuffer pds_field_tag = new StringBuffer(); 
		
		 pds_field_tag.append(field_data.substring(field_pointer,field_pointer+4 ))  ;
				 //[field_pointer:field_pointer+4];
		 
		 int pds_field_length = Integer.parseInt(field_data.substring(field_pointer+4, field_pointer+7)) ;
				 						//[field_pointer+4:field_pointer+7]
		 
		 key_value.put("PDS" + pds_field_tag, field_data.substring(field_pointer+7, field_pointer+7+pds_field_length));
	 		//[field_pointer+7:field_pointer+7+pds_field_length]		 
		 
		 field_pointer += 7+pds_field_length;
	}
	return key_value;
}
 


public static  Map<String, Object> _get_de43_fields(String de43_field){
	 Map<String, Object> key_value = new HashMap<String, Object>();
	
	try {
	
		
		//System.out.println(de43_field);
	   
	if(!de43_field.matches("\\w.*")) return null ;
		
		
	String [] ds = de43_field.split("\\\\");
	
		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(ds[3]);
		while (m.find()) {
			
			key_value.put("DE43_POSTCODE", m.group());
		}
		
		int len = ds[3].length();
		
		key_value.put("DE43_STATE", ds[3].substring(len-6, len-3));
		
		key_value.put("DE43_COUNTRY",ds[3].substring(len-3, len));
		
		key_value.put("DE43_NAME", ds[0]);
	
		key_value.put("DE43_ADDRESS", ds[1]);
		
		key_value.put("DE43_SUBURB", ds[2]);

	
	

	

 
} catch (IllegalStateException e) {
	e.printStackTrace();
	return null ;
}
	return key_value;
}
  
  


public static Map<String, Object> _get_icc_fields(String field_data){
	 
	/*
    Get de55 fields from message

    :param field_data: the field containing de55
    :return: dictionary of de55 key values
             key is tag+tagid
    */
	  Map<String, Object> key_value = new HashMap<String, Object>();
  try{
	
	   
	   ArrayList<String>  TWO_BYTE_TAG_PREFIXES  = new ArrayList<String>();
	   TWO_BYTE_TAG_PREFIXES.add("9f");
	   TWO_BYTE_TAG_PREFIXES.add( "5f");

	   int field_pointer = 0;
	    

	    while (field_pointer < field_data.length() ){
	    	 // get the tag id (one byte)
	        String field_tag = field_data.substring(field_pointer,field_pointer+1);  //[field_pointer:field_pointer+1]
	        
	        if(TWO_BYTE_TAG_PREFIXES.contains(encodeHexString(field_tag.getBytes()).trim())){
	        	field_tag = field_data.substring(field_pointer,field_pointer+2); //[field_pointer:field_pointer+2]
	        	field_pointer += 2;
	        }else{
	        	field_pointer += 1;
	        }
	        
	        String field_tag_display =encodeHexString(field_tag.getBytes()).trim(); 
	        String field_length_raw =  field_data.substring(field_pointer,field_pointer+1) ; //[field_pointer:field_pointer+1]
	        int field_length =   Integer.parseInt(toBinary(field_length_raw.getBytes()),2);
	        
	    String de_field_data = field_data.substring(field_pointer+1,field_pointer+field_length+1);    
	    String de_field_data_display = encodeHexString(de_field_data.getBytes());
	    key_value.put("TAG"+field_tag_display, de_field_data_display);
	    //System.out.println("TAG"+field_tag_display + " = "+de_field_data_display);
	    
	        field_pointer += 1+field_length;
	    	
	    }
	  //System.out.println(key_value);
  }catch(Exception e){}
    return key_value;
}
    

public static String removeSpace(String data){
	//System.out.println("data ::>"+data);
	return data.replace("\\s+", "");
}

 
/*  public static  ArrayList<String> toBinary_list( byte[] bytes ){
	  ArrayList bin = new ArrayList();
	  try {
		    
		  for(int i=1; i<bytes.length;i++){
			 
			  String s1 = String.format("%8s", Integer.toBinaryString(bytes[i] & 0x80)).replace(' ', '0');

			  
			  bin.add(s1);

		  }
		  System.out.println(bin);
		  
		  
	  } catch (Exception ex) {
            Logger.getLogger(TransactionUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
	  return bin;
    }*/
 
}
