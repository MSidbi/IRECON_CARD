package com.recon.util.chargeback;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	public static Double getDaysFromTwoDates(String dateBeforeString,String dateAfterString){
		
		//public static void main(String args[]){
		double daysBetween = 0;
		SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
		// String dateBeforeString = "31/01/2014";
		// String dateAfterString = "20/02/2014";

		 try {
		       Date dateBefore = myFormat.parse(dateBeforeString);
		       Date dateAfter = myFormat.parse(dateAfterString);
		       long difference = dateAfter.getTime() - dateBefore.getTime();
		       daysBetween = (difference / (1000*60*60*24));
	               /* You can also convert the milliseconds to days using this method
	                * float daysBetween = 
	                *         TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS)
	                */
		       System.out.println("Number of Days between dates: "+daysBetween);
		 } catch (Exception e) {
		       e.printStackTrace();
		 }
		 
		return daysBetween;
		
	}

}
