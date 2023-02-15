package com.recon.service.impl;

import java.util.ArrayList;

import com.recon.util.struct;
import com.recon.service.To_List;

public class To_List_Impl implements To_List {

	@Override
	public ArrayList<String> to_block(String message_data) {
		
		int file_pointer = 0;
		/*String[] introduction = message_data.split(":");*/
		ArrayList<String> blocked_data = new ArrayList<String> (); 
		//ArrayList<String> blocked_data1 = new ArrayList<String> (10000); 
		while (file_pointer <= message_data.length()) {
			try {
				blocked_data.add(message_data.substring(file_pointer,
						file_pointer + 1012));
				
			} catch (Exception e) {
				blocked_data.add(message_data.substring(file_pointer,message_data.length()));;
			}
			file_pointer += 1014;
		}
	  
		//blocked_data1=blocked_data;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < blocked_data.size(); i++) {
			sb.append(blocked_data.get(i));
			
		}
		//System.out.println("End of loop");
		
		return vbs_unpack(sb.toString()) ;
	}
	
	
	
	
	public ArrayList<String> vbs_unpack(String vbs_data){
		int vbs_pointer = 0;
		ArrayList line_data = new ArrayList();
		 
	try {
	 
		while (vbs_pointer < vbs_data.length()) {
				int[] a;
				int temp =0;
				a = struct.unpack(vbs_data.substring(vbs_pointer, vbs_pointer + 4).getBytes());
				vbs_pointer += 4;
				for (int aaa : a) {
					temp = aaa;
				}
				try {
					//System.out.println("vbs_pointer["+vbs_pointer+"] ::> [vbs_pointer+temp]= ["+vbs_pointer+" + ["+temp+"]");
					line_data.add(vbs_data.substring(vbs_pointer, vbs_pointer + temp));
				} catch (Exception e) {
					line_data.add(vbs_data.substring(vbs_pointer));
				}
				vbs_pointer += temp;
		}
		 
	} catch (Exception e) {
		e.printStackTrace();
	}
	

		return line_data;
	}


 
}
