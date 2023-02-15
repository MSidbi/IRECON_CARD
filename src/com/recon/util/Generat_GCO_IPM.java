/*package com.recon.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.recon.dao.impl.SettlmentDaoImpl;
import com.recon.service.ISettelmentService;

public class Generat_GCO_IPM {

	*//**
	 * @param args
	 *//*
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Scanner scan=new Scanner(System.in);
		System.out.println("Please enter date in mm/dd/yyyy format : ");
		String outputString=scan.next();
		System.out.println(outputString);
		try{
			SettlmentDaoImpl ob=new SettlmentDaoImpl();
		//	ob.generate_gco(outputString);
		//isettelmentservice.generate_gco(outputString);
		System.out.println("Covert Start time: " +System.currentTimeMillis());
		//ob.generate_ipm(outputString);
		System.out.println("Covert end time: " +System.currentTimeMillis());
		 Utility mfe = new Utility();
	        List<String> files = new ArrayList<String>();
	        File[] file2 = new File("E:/server").listFiles();
	        for (File file : file2) {
	            if (file.isDirectory()) {
	                System.out.println("Directory: " + file.getName());
	               // showFiles(file.listFiles()); // Calls same method again.
	            } else {
	                System.out.println("File: " + file.getAbsolutePath());
	                files.add(file.getAbsolutePath());
	            }
	        }01/21/2018
	        mfe.zipFiles(files);
	        File file = new File("E:/server");
	        File[] files3 = file.listFiles(); 
	        for (File f:files3) 
	        {
	        	if (f.isFile() && f.exists()) 
	            { f.delete();
	System.out.println("successfully deleted");
	            }else{
	System.out.println("cant delete a file due to open or error");
	            	} 
	        
	        } 
	}catch(Exception e)
	{
		e.printStackTrace();
	}}

}
*/