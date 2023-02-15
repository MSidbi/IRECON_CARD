package com.recon.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestClass {

	public static void main(String args[])
	{
		//dd-MMM-yyyy
		
		SimpleDateFormat sdf= new SimpleDateFormat("dd/MMM/yyyy");
		System.out.println(sdf.format(new Date()));
		
	}
	
}
