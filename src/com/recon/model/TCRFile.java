package com.recon.model;
import java.util.ArrayList;
import java.util.List;


public class TCRFile {

	public List<String> TCR050Format()
	{
		List<String> DataElements = new ArrayList<String>();
		String DE1 = "Transaction Code|1|2";	
		DataElements.add(DE1);
		String DE2 = "Transaction Code Qualifier|3";
		DataElements.add(DE2);
		String DE3 = "Transaction Component Sequence Number|4";
		DataElements.add(DE3);
		String DE4 = "Account Number|5|20";
		DataElements.add(DE4);
		String DE5 = "Account Number Extension|21|23";
		DataElements.add(DE5);
		String DE6 = "Floor Limit Indicator|24";
		DataElements.add(DE6);
		String DE7 = "CRB/Exception File Indicator|25";
		DataElements.add(DE7);
		String DE8 = "Positive Cardholder Authorization Service (PCAS) Indicator|26";
		DataElements.add(DE8);
		String DE9 = "Acquirer Reference Number|27|49";
		DataElements.add(DE9);
		String DE10 = "Acquirer's Business ID|50|57";
		DataElements.add(DE10);
		String DE11 = "Purchase Date|58|61";
		DataElements.add(DE11);
		String DE12 = "Destination Amount|62|73";
		DataElements.add(DE12);
		String DE13 = "Destination Currency Code|74|76";
		DataElements.add(DE13);
		String DE14 = "Source Amount|77|88";
		DataElements.add(DE14);
		String DE15 = "Source Currency Code|89|91";
		DataElements.add(DE15);
		String DE16 = "Merchant Name|92|116";
		DataElements.add(DE16);
		String DE17 = "Merchant City|117|129";
		DataElements.add(DE17);
		String DE18 ="Merchant Country Code|130|132";
		DataElements.add(DE18);
		String DE19 = "Merchant Category Code|133|136";
		DataElements.add(DE19);
		String DE20 = "Merchant ZIP Code|137|141";
		DataElements.add(DE20);
		String DE21 = "Merchant State|142|144";
		DataElements.add(DE21);
		String DE22 = "Requested Payment Service|145";
		DataElements.add(DE22);
		String DE23 = "Number of Payment Forms|146";
		DataElements.add(DE23);
		String DE24 = "Usage Code|147";
		DataElements.add(DE24);
		String DE25 = "Reason Code|148|149";
		DataElements.add(DE25);
		String DE26 = "Settlement Flag|150";
		DataElements.add(DE26);
		String DE27 = "Authorization Characteristics Indicator|151";
		DataElements.add(DE27);
		String DE28 = "Authorization Code|152|157";
		DataElements.add(DE28);
		String DE29 = "POS Terminal Capability|158";
		DataElements.add(DE29);
		String DE30 = "Reserved|159";
		DataElements.add(DE30);
		String DE31 = "Cardholder ID Method|160";
		DataElements.add(DE31);
		String DE32 = "Collection-Only Flag|161";
		DataElements.add(DE32);
		String DE33 = "POS Entry Mode|162|163";
		DataElements.add(DE33);
		String DE34 = "Central Processing Date|164|167";
		DataElements.add(DE34);
		String DE35 = "Reimbursement Attribute|168";
		DataElements.add(DE35);
		
		
		return DataElements;
	}

	public List<String> TCR10FeeCollectionFormat()
	{
		List<String> DataElements = new ArrayList<String>();
		String	DE1 = "Transaction Code|1|2";
		String DE2 = "Transaction Code Qualifier|3"; 
		String DE3 = "Transaction Component Sequence Number|4"; 
		String DE4 = "Destination BIN|5|10";
		String DE5 = "Source BIN|11|16";
		String DE6 = "Reason Code|17|20";
		String DE7 = "Country Code|21|23";
		String DE8 = "Event Date (MMDD)|24|27";
		String DE9 = "Account Number|28|43";
		String DE10 = "Account Number Extension|44|46";
		String DE11 = "Destination Amount|47|58";
		String DE12 = "Destination Currency Code|59|61";
		String DE13 = "Source Amount|62|73";
		String DE14 = "Source Currency Code|74|76";
		String DE15 = "Message Text|77|146";
		String DE16 = "Settlement Flag|147"; 
		String DE17 = "Transaction Identifier|148|162";
		String DE18 = "Reserved|163"; 
		String DE19 = "Central Processing Date (YDDD)|164|167";
		String DE20 = "Reimbursement Attribute|168"; 

		DataElements.add(DE1);
		DataElements.add(DE2);
		DataElements.add(DE3);
		DataElements.add(DE4);
		DataElements.add(DE5);
		DataElements.add(DE6);
		DataElements.add(DE7);
		DataElements.add(DE8);
		DataElements.add(DE9);
		DataElements.add(DE10);
		DataElements.add(DE11);
		DataElements.add(DE12);
		DataElements.add(DE13);
		DataElements.add(DE14);
		DataElements.add(DE15);
		DataElements.add(DE16);
		DataElements.add(DE17);
		DataElements.add(DE18);
		DataElements.add(DE19);
		DataElements.add(DE20);

		return DataElements;
	}
	
	public List<String> TCR20FundDisbursement()
	{
		List<String> DataElements = new ArrayList<String>();
		String	DE1 = "Transaction Code|1|2 " ;
		String DE2 = "Transaction Code Qualifier| 3   " ;
		String DE3 = "Transaction Component Sequence Number|4  " ;
		String DE4 = "Destination BIN| 5 |10 " ;
		String DE5 = "Source BIN| 11 |16 " ;
		String DE6 = "Reason Code| 17 |20 " ;
		String DE7 = "Country Code|21 |23 " ;
		String DE8 = "Event Date (MMDD)| 24 |27 " ;
		String DE9 = "Account Number| 28 |43 " ;
		String DE10 = "Account Number Extension| 44 |46 " ;
		String DE11 = "Destination Amount| 47 |58 " ;
		String DE12 = "Destination Currency Code| 59 |61 " ;
		String DE13 = "Source Amount| 62 |73 " ;
		String DE14 = "Source Currency Code| 74 |76 " ;
		String DE15 = "Message Text| 77 |146 " ;
		String DE16 = "Settlement Flag| 147  " ;
		String DE17 = "Transaction Identifier| 148 |162 " ;
		String DE18 = "Reserved| 163" ;
		String DE19 = "Central Processing Date (YDDD)| 164 |167 " ;
		String DE20 = "Reimbursement Attribute| 168  " ;
		
		DataElements.add(DE1);
		DataElements.add(DE2);
		DataElements.add(DE3);
		DataElements.add(DE4);
		DataElements.add(DE5);
		DataElements.add(DE6);
		DataElements.add(DE7);
		DataElements.add(DE8);
		DataElements.add(DE9);
		DataElements.add(DE10);
		DataElements.add(DE11);
		DataElements.add(DE12);
		DataElements.add(DE13);
		DataElements.add(DE14);
		DataElements.add(DE15);
		DataElements.add(DE16);
		DataElements.add(DE17);
		DataElements.add(DE18);
		DataElements.add(DE19);
		DataElements.add(DE20);
		
		return DataElements;
	}
	
public List<String> TCR0501()
{
	List<String> DataElements = new ArrayList<String>();
	
	String	DE1 = "Transaction Code|1|2 ";
	String DE2 = "Transaction Code Qualifier | 3 ";
	String DE3 = "Transaction Component Sequence Number | 4 ";
	String DE4 = "Business Format Code | 5 ";
	String DE5 = "Token Assurance Level | 6|7 ";
	String DE6 = "Reserved | 8|16 ";
	String DE7 = "Chargeback Reference Number | 17|22 ";
	String DE8 = "Documentation Indicator | 23 ";
	String DE9 = "Member Message Text | 24|73 ";
	String DE10 = "Special Condition Indicators | 74|75 ";
	String DE11 = "Fee Program Indicator | 76|78 ";
	String DE12 = "Issuer Charge | 79 ";
	String DE13 = "Reserved|80 ";
	String DE14 = "Card Acceptor ID | 81|95 ";
	String DE15 = "Terminal ID|96|103 ";
	String DE16 = "National Reimbursement Fee|104|115 ";
	String DE17 = "Mail/Phone/Electronic Commerce and Payment Indicator | 116";
	String DE18 = "Special Chargeback Indicator | 117";
	String DE19 = "Interface Trace Number | 118 |123 ";
	String DE20 = "Acceptance Terminal Indicator | 124";
	String DE21 = "Prepaid Card Indicator | 125";
	String DE22 = "Service Development Field | 126";
	String DE23 = "AVS Response Code | 127";
	String DE24 = "Authorization Source Code | 128";
	String DE25 = "Purchase Identifier Format | 129";
	String DE26 = "Account Selection | 130";
	String DE27 = "Installment Payment Count | 131 |132 ";
	String DE28 = "Purchase Identifier | 133 |157 ";
	String DE29 = "Cashback | 158 |166 ";
	String DE30 = "Chip Condition Code | 167";
	String DE31 = "POS Environment | 168";

	
	DataElements.add(DE1);
	DataElements.add(DE2);
	DataElements.add(DE3);
	DataElements.add(DE4);
	DataElements.add(DE5);
	DataElements.add(DE6);
	DataElements.add(DE7);
	DataElements.add(DE8);
	DataElements.add(DE9);
	DataElements.add(DE10);
	DataElements.add(DE11);
	DataElements.add(DE12);
	DataElements.add(DE13);
	DataElements.add(DE14);
	DataElements.add(DE15);
	DataElements.add(DE16);
	DataElements.add(DE17);
	DataElements.add(DE18);
	DataElements.add(DE19);
	DataElements.add(DE20);
	DataElements.add(DE21);
	DataElements.add(DE22);
	DataElements.add(DE23);
	DataElements.add(DE24);
	DataElements.add(DE25);
	DataElements.add(DE26);
	DataElements.add(DE27);
	DataElements.add(DE28);
	DataElements.add(DE29);
	DataElements.add(DE30);
	DataElements.add(DE31);
	
	return DataElements;
}
	

public List<String> V22200()
{
	List<String> DataElements = new ArrayList<String>();
	String DE1 = "Record Type|1|6";
	String DE2 = "Issuer-Acquirer Indicator|7";
	String DE3 = "MVV Code|8|17";
	String DE4 = "Remote Terminal Indicator|18";
	String DE5 = "Charge Indicator|19";
	String DE6 = "Product ID|20|21";
	String DE7 = "Business Application Identifier|22|23";
	String DE8 = "Source of Funds|24";
	String DE9 = "Product Subtype|25|26";
	String DE10 = "Account Funding Source|27";
	String DE11 = "Affiliate BIN|28|37";
	String DE12 = "Settlement Date|38|43";
	String DE13 = "Transaction Identifier|44|58";
	String DE14 = "Validation Code|59|62";
	String DE15 = "Retrieval Reference Number|63|74";
	String DE16 = "Trace Number|75|80";
	String DE17 = "Batch Number|81|84";
	String DE18 = "Request Message Type|85|88";
	String DE19 = "Response Code|89|90";
	String DE20 = "Processing Code|91|96";
	String DE21 = "Card Number|97|115 ";
	String DE22 = "Transaction Amount|116|127";
	//String DE22 = "Transaction Amount|116|126";
	String DE23 = "Currency Code, Transaction Amount|128|130";
	
	
	DataElements.add(DE1);
	DataElements.add(DE2);
	DataElements.add(DE3);
	DataElements.add(DE4);
	DataElements.add(DE5);
	DataElements.add(DE6);
	DataElements.add(DE7);
	DataElements.add(DE8);
	DataElements.add(DE9);
	DataElements.add(DE10);
	DataElements.add(DE11);
	DataElements.add(DE12);
	DataElements.add(DE13);
	DataElements.add(DE14);
	DataElements.add(DE15);
	DataElements.add(DE16);
	DataElements.add(DE17);
	DataElements.add(DE18);
	DataElements.add(DE19);
	DataElements.add(DE20);
	DataElements.add(DE21);
	DataElements.add(DE22);
	DataElements.add(DE23);
	
	return DataElements;

}


public List<String> V22201()

{
	
	List<String> DataElements = new ArrayList<String>();
	String DE1 = "Record Type|1|6";
	String DE2 = "PAN Token|7|25";
	String DE3 = "Token Assurance Level|26|27";
	String DE4 = "Token Requester ID|28|38";
	String DE5 = "Filler|39|130";

	DataElements.add(DE1);
	DataElements.add(DE2);
	DataElements.add(DE3);
	DataElements.add(DE4);
	DataElements.add(DE5);
	
	return DataElements;
}

public List<String> V22210()
{
	List<String> DataElements = new ArrayList<String>();
	String DE1 = "Record Type|1|6";
	String DE2 = "Local Transaction Date|7|10";
	String DE3 = "Local Transaction Time|11|16";
	String DE4 = "GIV Flag|17";
	String DE5 = "GIV Flag Previous|18";
	String DE6 = "Acquiring Institution ID|19|29";
	String DE7 = "Acquirer Business ID|30|37";
	String DE8 = "Source Station ID|38|43";
	String DE9 = "Destination Station ID|44|49";
	String DE10 = "Message Reason Code|50|53";
	String DE11 = "STIP Reason Code|54|57";
	String DE12 = "Authorization ID Resp. Code|58|63";
	String DE13 = "Network ID|64|67";
	String DE14 = "Advice Source Flag|68";
	String DE15 = "Advice Transaction Flag|69";
	String DE16 = "BASE I Bill Flag|70";
	String DE17 = "Track Data Indicator|71";
	String DE18 = "Reimbursement Attribute|72";
	String DE19 = "Spend Qualified Indicator|73";
	String DE20 = "Reserved|74|80";
	String DE21 = "PVS Performed Indicator|81";
	String DE22 = "Transmission Date|82|85";
	String DE23 = "Transmission Time|86|91";
	String DE24 = "Transaction Other Amount|92|103";
	String DE25 = "Downgrade Reason Code|104|105";
	String DE26 = "Authorization Characteristics Indicator|106";
	String DE27 = "Response Message Type|107|110";
	String DE28 = "Card Sequence Number|111|113";
	String DE29 = "Card Expiration Date|114|117";
	String DE30 = "CVV Result Code|118";
	String DE31 = "Settlement Service Requested|119";
	String DE32 = "Settlement Service Selected|120";
	String DE33 = "IRF Option|121";
	String DE34 = "Mail/Telephone or Electronic Commerce Indicator|122";
	String DE35 = "Merchant Volume Indicator|123|124";
	String DE36 = "DCC Indicator|125";
	String DE37 = "Fee Program Indicator|126-128";
	String DE38 = "Filler|129|130";
	
	DataElements.add(DE1);
	DataElements.add(DE2);
	DataElements.add(DE3);
	DataElements.add(DE4);
	DataElements.add(DE5);
	DataElements.add(DE6);
	DataElements.add(DE7);
	DataElements.add(DE8);
	DataElements.add(DE9);
	DataElements.add(DE10);
	DataElements.add(DE11);
	DataElements.add(DE12);
	DataElements.add(DE13);
	DataElements.add(DE14);
	DataElements.add(DE15);
	DataElements.add(DE16);
	DataElements.add(DE17);
	DataElements.add(DE18);
	DataElements.add(DE19);
	DataElements.add(DE20);
	DataElements.add(DE21);
	DataElements.add(DE22);
	DataElements.add(DE23);
	DataElements.add(DE24);
	DataElements.add(DE25);
	DataElements.add(DE26);
	DataElements.add(DE27);
	DataElements.add(DE28);
	DataElements.add(DE29);
	DataElements.add(DE30);
	DataElements.add(DE31);
	DataElements.add(DE32);
	DataElements.add(DE33);
	DataElements.add(DE34);
	DataElements.add(DE35);
	DataElements.add(DE36);
	DataElements.add(DE37);
	DataElements.add(DE38);
	
	return DataElements;
	
}

public List<String> V22220()
{
	List<String> DataElements = new ArrayList<String>();
	String DE1 = "Record Type|1|6";
	String DE2 = "POS Condition Code|7|8";
	String DE3 = "POS Entry Mode|9|11";
	String DE4 = "POS Terminal Type|12|13";
	String DE5 = "POS Terminal Entry Capability|14";
	String DE6 = "Merchant's Type|15|18";
	String DE7 = "Card Acceptor Terminal|19|26";
	String DE8 = "Card Acceptor ID|27|41";
	String DE9 = "Card Acceptor Name|42|66";
	String DE10 = "Card Acceptor City|67|79";
	String DE11 = "Card Acceptor Country|80|81";
	String DE12 = "Geo State Code|82|83";
	String DE13 = "Geo ZIP Code ZIP Five|84|88";
	String DE14 = "Geo ZIP Code ZIP Four|89|92";
	String DE15 = "Geo Country Code|93|95";
	String DE16 = "Acquiring Institution Country Code|96|98";
	String DE17 = "PAN Extended Country Code|99|101";
	String DE18 = "Forwarding Institution ID|102|112";
	String DE19 = "Forwarding Institution Country Code|113|115";
	String DE20 = "Customer Identification Method|116";
	String DE21 = "Issuer Affiliate BIN|117|126";
	String DE22 = "Filler|127|129";
	String DE23 = "Recurring Payment Indicator flag|130";
	
	DataElements.add(DE1);
	DataElements.add(DE2);
	DataElements.add(DE3);
	DataElements.add(DE4);
	DataElements.add(DE5);
	DataElements.add(DE6);
	DataElements.add(DE7);
	DataElements.add(DE8);
	DataElements.add(DE9);
	DataElements.add(DE10);
	DataElements.add(DE11);
	DataElements.add(DE12);
	DataElements.add(DE13);
	DataElements.add(DE14);
	DataElements.add(DE15);
	DataElements.add(DE16);
	DataElements.add(DE17);
	DataElements.add(DE18);
	DataElements.add(DE19);
	DataElements.add(DE20);
	DataElements.add(DE21);
	DataElements.add(DE22);
	DataElements.add(DE23);
	
	return DataElements;
}

public List<String> V22260()
{
	List<String> DataElements = new ArrayList<String>();
	String DE1 = "Record Type|1|6";
	String DE2 = "Rate Table Date|7|10";
	String DE3 = "Transaction Amount|11|22";
	String DE4 = "Transaction Currency Code|23|25";
	String DE5 = "Settlement Amount|26|37";
	String DE6 = "Settlement Currency Code|38|40";
	String DE7 = "Cardholder Billing Amount|41|52";
	String DE8 = "Cardholder Billing Currency Code|53|55";
	String DE9 = "Cardholder Billing Other Amount|56|67";
	String DE10 = "Filler|68|85";
	String DE11 = "Optional Issuer Fee – Settlement Currency|86|94";
	String DE12 = "Optional Issuer Fee – Cardholder Billing Currency|95|103";
	String DE13 = "Filler|104|121";
	String DE14 = "Optional Issuer ISA Amount in Settlement Currency|122|130";
	
	
	DataElements.add(DE1);
	DataElements.add(DE2);
	DataElements.add(DE3);
	DataElements.add(DE4);
	DataElements.add(DE5);
	DataElements.add(DE6);
	DataElements.add(DE7);
	DataElements.add(DE8);
	DataElements.add(DE9);
	DataElements.add(DE10);
	DataElements.add(DE11);
	DataElements.add(DE12);
	DataElements.add(DE13);
	DataElements.add(DE14);
	
	return DataElements;

}

public List<String> TCR0505Format()
{
	List<String> DataElements = new ArrayList<String>();
	String DE1 = "Transaction Id|4|19";	
	DataElements.add(DE1);
	return DataElements;
}

}
