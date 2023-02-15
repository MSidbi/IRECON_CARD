package com.recon.auto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ReadSwitchFile2 {

	public static void main(String[] abc)
	{
		String bankName= null;
		String Ignoredescription = null;
		int bankCount = 0;
		int count = 1,cellCount = 4;
		int totalcount = 0, colCount = 1, skipCount = 0, PreArb_Count = 0, LastCol = 0;
		
		try
		{
			System.out.println("Starting reading ");
			File file = new File("D:\\BHAGYASHREE\\DHANALAXMI DATA\\IDBI DCRS FILES\\IDBI DCRS FILES\\15-05-2022\\Downloads\\SWITCH\\downloadFile(3).xls");
			
			
     	   /*Path tempDir = Files.createTempDirectory(""); 
      	  File tempFile = tempDir.resolve(file.getName()).toFile();
      	  //file.transferTo(tempFile);
      	 */
      	  String content = Jsoup.parse(file,"UTF-8").toString(); 
			  org.jsoup.nodes.Document html = Jsoup.parse(content);
			  if (content != null) 
			  { 
				  Elements contents = html.getElementsByTag("tbody");
				  

				  System.out.println("********************** Reading tbody tags ****************");
				  
				  OUTER:  for(Element a : contents)
				  {
					  //code starts from here
					  Elements thContents = a.getElementsByTag("tr");
					  Elements tdContents = a.getElementsByTag("td");
					//INNER:  for(Element b : thContents)
					//  INNER:  for(Element b : thContents)
					  INNER:  for(Element b : tdContents)
					  {
						  System.out.println("b is "+b.text());
						  
					  }



				  }
				// coop_ps.executeBatch();
				
              long end = System.currentTimeMillis();
             
			  }
			//delete the file from temp folder
			 // FileUtils.forceDelete(file);
        
		}
		catch (Exception e) {
            e.printStackTrace();
     }


	} 
}
