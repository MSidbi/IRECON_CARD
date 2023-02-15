package com.recon.service.impl.rupayoffline;

import java.io.InputStream;



import javax.xml.parsers.SAXParserFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recon.dao.rupayoffline.RupayOfflineUploadDao;
import com.recon.service.rupayoffline.RupayOfflineUploadService;
import com.recon.util.rupayoffline.MyHandler;

import javax.xml.parsers.SAXParser;


@Service
public class RupayOfflineUploadServiceImpl implements RupayOfflineUploadService {

	@Autowired
	RupayOfflineUploadDao dao;
	
	@Override
	public void readOfflineXml(InputStream file) throws Exception {
		 
		 SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
		 try {
		        SAXParser saxParser = saxParserFactory.newSAXParser();
		        MyHandler handler = new MyHandler();
		        handler.setRupayOfflineUploadDao(dao);
		        saxParser.parse(file, handler);
		          
		    } catch ( Exception e) {
		        throw e;
		    }
	}

}
