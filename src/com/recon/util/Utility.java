package com.recon.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Utility {
	public static long generateRandom() {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();

		// first not 0 digit
		sb.append(random.nextInt(9) + 1);

		// rest of 11 digits
		for (int i = 0; i < 10; i++) {
			sb.append(random.nextInt(10));
		}

		return Long.valueOf(sb.toString()).longValue();
	}
	public static long generateRandom2() {
		Random random = new Random();
		StringBuilder sb = new StringBuilder();

		// first not 0 digit
		sb.append(random.nextInt(9) + 1);

		// rest of 11 digits
		for (int i = 0; i <5; i++) {
			sb.append(random.nextInt(5));
		}

		return Long.valueOf(sb.toString()).longValue();
	}
	
	public static String get_mod(String input)
	{
		int i=0;
		 //Scanner scan=new Scanner(System.in);
		 Utility mod=new Utility();
			//System.out.println("Enter 22 digit number ::");
			//String number=scan.next();
			int count=1;
			boolean mod_val=mod.luhnVerify(input);
			if(mod_val)
			{
				System.out.println("Valid");
				if(count==1)
				{
				for(i=0;i<=9;i++)
				{
					boolean mod_val1=mod.luhnVerify(input+i);
					if(mod_val1)
					{
						System.out.println("Valid 2--->>"+input+i);
						break;
					}
								}
				}
			}else{
				System.out.println("Invalid..Continue");
				for(i=0;i<=9;i++)
				{
					boolean mod_val1=mod.luhnVerify(input+i);
					if(mod_val1)
					{
						System.out.println("Valid 2 :--->>"+input+i);
						break;
					}
								}
			}
			return input+i;
	}
	
	public static boolean luhnVerify(String str) {
		  int sum = 0;
		  int value;
		  int idx = str.length(); // Start from the end of string
		  boolean alt = false;

		  while(idx-- > 0) {
		    // Get value. Throws error if it isn't a digit
		    value = Integer.parseInt(str.substring(idx, idx + 1));
		    if (alt) {
		      value *= 2;
		      if (value > 9) value -= 9;
		    }
		    sum += value;
		    alt = !alt;  //Toggle alt-flag
		  }
		  return (sum % 10) == 0;
		}

	
	public void zipFiles(List<String> files){
        
        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        FileInputStream fis = null;
        try {
            fos = new FileOutputStream("D:/GCO.zip");
            zipOut = new ZipOutputStream(new BufferedOutputStream(fos));
            for(String filePath:files){
                File input = new File(filePath);
                fis = new FileInputStream(input);
                ZipEntry ze = new ZipEntry(input.getName());
                System.out.println("Zipping the file: "+input.getName());
                zipOut.putNextEntry(ze);
                byte[] tmp = new byte[4*1024];
                int size = 0;
                while((size = fis.read(tmp)) != -1){
                    zipOut.write(tmp, 0, size);
                }
                zipOut.flush();
                fis.close();
            }
            zipOut.close();
            System.out.println("Done... Zipped the files...");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
            try{
                if(fos != null) fos.close();
            } catch(Exception ex){
                 
            }
        }
    }
public static String appnd_zeros(String num)
{
	
int num_size=12;
	if(num.length()==num_size)
	{
		System.out.println("Inside if");
	}
	else{
		for(int i=1;i<num_size;i++)
		{
			num="0"+num;
			if(num.length()==num_size)
			{
				System.out.println("Inside parent if :"+num);
				return num;
			}
			
		}}
	return num;
}

public static String auto_append(String input)
{
	int num_size=12;
	
	if(input.length()==num_size)
	{
		System.out.println("Inside if");
	}
	else{
		for(int i=1;i<num_size;i++)
		{
			input="0"+input;
			if(input.length()==num_size)
			{
				System.out.println("Inside parent if :"+input);
				return input;
			}
		}}
	return input;
}

public static String auto_append3(String input)
{
	int num_size=9;
	
	if(input.length()==num_size)
	{
		System.out.println("Inside if");
	}
	else{
		for(int i=1;i<num_size;i++)
		{
			input="0"+input;
			if(input.length()==num_size)
			{
				System.out.println("Inside parent if :"+input);
				return input;
			}
		}}
	return input;
}

public static String auto_append12(String input)
{
	int num_size=10;
	
	if(input.length()==num_size)
	{
		System.out.println("Inside if");
	}
	else{
		for(int i=1;i<num_size;i++)
		{
			input="0"+input;
			if(input.length()==num_size)
			{
				System.out.println("Inside parent if :"+input);
				return input;
			}
		}}
	return input;
}
public static String auto_append2(String input)
{
	int num_size=15;
	
	if(input.length()==num_size)
	{
		System.out.println("Inside if");
	}
	else{
		for(int i=1;i<num_size;i++)
		{
			input="0"+input;
			if(input.length()==num_size)
			{
				System.out.println("Inside parent if :"+input);
				return input;
			}
		}}
	return input;
}

public static String appnd_space(String input)
{
	int num_size=25;
	if(input.length()==num_size)
	{
		System.out.println("Inside if");
	}
	else{
		for(int i=1;i<num_size;i++)
		{
			input=input+" ";
			if(input.length()==num_size)
			{
				System.out.println("Inside parent if :"+input);
				return input;
			}
		}
	}
	return input;
}




	public static int convertToJulian(String unformattedDate) {
		/* Unformatted Date: ddmmyyyy */
		int result_jd = 0;
		int resultJulian = 0;
		if (unformattedDate.length() > 0) {
			/* Days of month */
			int[] monthValues = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30,
					31 };

			String dayS, monthS, yearS;
			dayS = unformattedDate.substring(0, 2);
			monthS = unformattedDate.substring(2, 4);
			yearS = unformattedDate.substring(4, 8);

			/* Convert to Integer */
			int day = Integer.valueOf(dayS);
			int month = Integer.valueOf(monthS);
			int year = Integer.valueOf(yearS);

			// Leap year check
			if (year % 4 == 0) {
				monthValues[1] = 29;
			}
			// Start building Julian date
			String julianDate = "1";
			// last two digit of year: 2012 ==> 12
			julianDate += yearS.substring(2, 4);

			int julianDays = 0;
			for (int i = 0; i < month - 1; i++) {
				julianDays += monthValues[i];
			}
			julianDays += day;

			if (String.valueOf(julianDays).length() <= 1) {
				julianDate += "00"+day+"";
			}
			else
			{
			if (String.valueOf(julianDays).length() < 2) {
				julianDate += "00";
			}
			if (String.valueOf(julianDays).length() < 3) {
				julianDate += "0";
			}
			}

			julianDate += String.valueOf(julianDays);
			resultJulian = Integer.valueOf(julianDate);
			String julian_date = String.valueOf(resultJulian);
			String sub_jul = julian_date.substring(2, 6);
			//System.out.println("3 Digit julian date--> " + sub_jul);
			result_jd = Integer.parseInt(sub_jul);
		}
		return result_jd;
	}

	public static int convertToJulian2(String unformattedDate) {
		/* Unformatted Date: ddmmyyyy */
		int result_jd = 0;
		int resultJulian = 0;
		if (unformattedDate.length() > 0) {
			/* Days of month */
			int[] monthValues = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30,
					31 };

			String dayS, monthS, yearS;
			dayS = unformattedDate.substring(0, 2);
			monthS = unformattedDate.substring(2, 4);
			yearS = unformattedDate.substring(4, 8);

			/* Convert to Integer */
			int day = Integer.valueOf(dayS);
			int month = Integer.valueOf(monthS);
			int year = Integer.valueOf(yearS);

			// Leap year check
			if (year % 4 == 0) {
				monthValues[1] = 29;
			}
			// Start building Julian date
			String julianDate = "1";
			// last two digit of year: 2012 ==> 12
			julianDate += yearS.substring(2, 4);

			int julianDays = 0;
			for (int i = 0; i < month - 1; i++) {
				julianDays += monthValues[i];
			}
			julianDays += day;

			if (String.valueOf(julianDays).length() <= 1) {
				julianDate += "00"+day+"";
			}
			else
			{
			if (String.valueOf(julianDays).length() < 2) {
				julianDate += "00";
			}
			if (String.valueOf(julianDays).length() < 3) {
				julianDate += "0";
			}
			}

			julianDate += String.valueOf(julianDays);
			resultJulian = Integer.valueOf(julianDate);
			String julian_date = String.valueOf(resultJulian);
			String sub_jul = julian_date.substring(1, 6);
			//System.out.println("3 Digit julian date--> " + sub_jul);
			result_jd = Integer.parseInt(sub_jul);
		}
		return result_jd;
	}
	public static String dateConveter_mmddyy(String dateval) throws ParseException
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date varDate = dateFormat.parse(dateval);
		dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		System.out.println("Date :" + dateFormat.format(varDate));
		return dateFormat.format(varDate);
	}
	public static String dateConveter_ddmonyyyy(String dateval) throws ParseException
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		java.util.Date varDate = dateFormat.parse(dateval);
		dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
		System.out.println("Date :" + dateFormat.format(varDate));
		return dateFormat.format(varDate);
	}
	
}