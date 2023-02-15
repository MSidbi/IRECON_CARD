package com.recon.util.chargeback;


import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLWriter {

	
	private DocumentBuilderFactory dbFactory ;
	private DocumentBuilder dBuilder;
	private Document doc ;
	private Element rootElement ;
	private TransformerFactory transformerFactory;
	private Transformer transformer;
	private DOMSource source;
	
	
	private Element hdr;
	private Element txn;
	private Element trl ;
	
	private Element txnBlock;
	
     public XMLWriter(){
    	 try {
    		this.dbFactory = DocumentBuilderFactory.newInstance();
			this.dBuilder = this.dbFactory.newDocumentBuilder();
			this.doc = dBuilder.newDocument();
			this.transformerFactory = TransformerFactory.newInstance();
			this.transformer = this.transformerFactory.newTransformer();
			this.transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			this.source = new DOMSource(this.doc);
			
			
			createRoot();
			this.hdr = this.doc.createElement("Hdr");
			this.rootElement.appendChild(hdr);
			
			
			this.txnBlock = this.doc.createElement("TxnBlock");
		    this.rootElement.appendChild(txnBlock);
		   /* this.txn = this.doc.createElement("Txn");
		    txnBlock.appendChild(txn);*/
		//    createTxn();
		    
		  	this.trl = this.doc.createElement("Trl");
	    	this.rootElement.appendChild(trl);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }
	
     
     public void createRoot(){
         this.rootElement =    this.doc.createElement("File");
         this.doc.appendChild(rootElement);
     }
     
	  public void createHeader(String key, String value){
	 	 	this.hdr.appendChild(createHeaderElements(  key, value));
    	 
	  }
	  
	  public Node createHeaderElements( String name, String value){
		  	Element node = this.doc.createElement(name);
	        node.appendChild(this.doc.createTextNode(value));
	        return node;
	  }
 
	  
	  
	  
	  
	  public void createTxn(){
		  this.txn = this.doc.createElement("Txn");
		  this.txnBlock.appendChild(txn);
	  }
	  
	  public void createBodyChild(  String key, String value){
		  this.txn.appendChild(createBodyChildElements(key,value));
	     }
	  
	  public Node createBodyChildElements(   String name, String value){
		  	Element node = this.doc.createElement(name);
	        node.appendChild(this.doc.createTextNode(value));
	        return node;
	     }
	  
	  public void createTrailer(String key, String value){
	    	this.trl.appendChild(createHeaderElements(  key, value));
	     }
	  
	  public Node createTrailerElements(String name, String value){
		  	Element node = this.doc.createElement(name);
	        node.appendChild(this.doc.createTextNode(value));
	        return node;
	  }
	  
	  public void print() throws TransformerException{
		  StreamResult console = new StreamResult(System.out);
		  this.transformer.transform(source, console);
	  }
	  
	  public void toFile(String path ) throws TransformerException{
		  File file  = new File(path);
		  if(!file.exists()){
			  System.out.println("FILE NOT EXISTS");
			  try {
				System.out.println("ABS PATH "+file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		  }
		  StreamResult strmfile = new StreamResult(file);
		  this.transformer.transform(source, strmfile);
	  }
	  
	  
	  public void clean(){
			this.dBuilder.reset(); ;
			this.transformer.reset(); ;
	  }
	  
	  
 
	  
     
}
