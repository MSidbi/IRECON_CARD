package com.recon.dao.impl.rupayoffline;

import java.util.List;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import com.recon.dao.rupayoffline.RupayOfflineUploadDao;
import com.recon.model.rupayoffline.XmlModel;

@Component
public class RupayOfflineUploadDaoImpl extends JdbcDaoSupport implements RupayOfflineUploadDao {

	@Override
	public void saveOfflineXml(List<XmlModel> rawdata) throws Exception {
	 
			try {
				
				String query = "  INSERT INTO RUPAY_NCMC_RAWDATA (  mti, funcd, RECNUM, dttmflgen, meminstcd, unflnm, dtset, prodcd, " +
						"        setbin, flcatg, vernum, acqinstcd, actncd, amtset, amttxn, ard," +
						"        ccycdset, ccycdtxn, convrtset, crdacpbusscd, crdacpcity, crdacpctrycd," +
						"        crdacpidcd, crdacploc, crdacpnm, crdacpstnm, crdacpttrmid, crdacpzipcd," +
						"          dttmlctxn, iccdata, mertelnum,  pan, poscondcd, posdatacd," +
						"        posentmode, proccd, servcd, setdcind, txndesinstcd, txnorginstcd," +
						"        feedcind, feeamt, feeccy, feetpcd, intrchngctg  " +
						"        )values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)" ;
			} catch (Exception e) {
					throw e;
			}
		
	}

}
