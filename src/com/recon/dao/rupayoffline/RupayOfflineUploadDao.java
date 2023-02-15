package com.recon.dao.rupayoffline;

import java.util.List;

import com.recon.model.rupayoffline.XmlModel;

public interface RupayOfflineUploadDao {

	
	public void saveOfflineXml(List<XmlModel> rawdata) throws Exception;
	
}
