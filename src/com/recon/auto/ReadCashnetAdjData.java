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

public class ReadCashnetAdjData {

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
			File file = new File("D:\\BHAGYASHREE\\DHANALAXMI DATA\\DHANALAXMI\\CASHNET\\SETTLEMENT\\NEW ADJ\\150522.xls");
			
			
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
						  
						  if(b.text().equals("ArbitrationFee"))
						  {
							  if(bankCount == 0)
							  {
								  System.out.println(b.text());
								  bankCount++;
								  continue;
							  }
							  else
							  {
								  break OUTER;
							  }
						  }
						  else if(b.text().equals("ENFee"))
						  {
							  PreArb_Count++;
							  LastCol++;
							  continue;
						  }
						  else if(b.text().equalsIgnoreCase("Back   Print"))
						  {
							  break OUTER;
						  }
						  
						  if(PreArb_Count > 0 && LastCol < 4)
						  {
							  LastCol++;
							  continue;
						  }
						  else if(LastCol == 4)
						  {
							  System.out.println(b.text());
						  }
						  /****** Reading main fields****************/
						  
						  if(bankCount == 1)
						  {
							  if(colCount%25 == 0)
							  {
								  skipCount++;
								  colCount++;
							  }
							  else if(skipCount == 0)
							  {
								  System.out.println(b.text());
								  colCount++;
							  }
							  else if(skipCount == 1 && b.text().equals("0"))
							  {
								  colCount = 1;
								  skipCount = 0;
							  }
						  }
						  
						/*  for(Element c : tdContents)
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
							  else
							  {
								  continue INNER;
							  }
							
						  }*/
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
