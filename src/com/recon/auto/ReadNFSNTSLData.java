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

public class ReadNFSNTSLData {

	public static void main(String[] abc)
	{
		String bankName= null;
		String Ignoredescription = null;
		int bankCount = 0;
		int count = 1,cellCount = 4;
		int totalcount = 0;
		
		try
		{

			File file = new File("D:\\BHAGYASHREE\\cashnet_iss.xls");
			
			
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
					  Elements thContents = a.getElementsByTag("th");
					  Elements tdContents = a.getElementsByTag("td");
					  for(Element b : thContents)
					  {
						  if(b.text().startsWith("Daily Settlement Statement"))
						  {
							  if(bankCount == 0)
							  {
								  System.out.println(thContents.text());
								  bankCount++;
							  }
							  else
							  {
								  break OUTER;
							  }
						  }
						  /****** Reading main fields****************/
						  for(Element c : tdContents)
						  {
							  if(bankCount == 1)
							  {
								  // INSERT IN RAW TABLE
								  if(count == 1 && c.text().equalsIgnoreCase(""))
								  {
									  continue;
								  }
								  else 
								  {
									  if(count == 1)
									  {
											  if(totalcount == 0)
											  {
												  Ignoredescription = c.text();
												  System.out.println(c.text());
												  totalcount++;
											  }
											  else
											  {
												 // ps.setString(count, c.text());
												  System.out.println(c.text());
												  totalcount++;
											  }
											  count++;
										  
									  }
									  else
									  {
										// ps.setString(count, c.text());
										  System.out.println(c.text());
										 count++;
									  }

								  }

								  if(count == cellCount+1)
								  {
									  System.out.println(".......................");
								  }

							  }
							
						  }
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
