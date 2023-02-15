package com.recon.dao;

import java.util.HashMap;
import java.util.List;

import com.recon.model.RupaySettlementBean;
import com.recon.model.RupayUploadBean;

public interface RupaySettelementDao {

String uploadRupaySettlementData(List<RupaySettlementBean> list,RupaySettlementBean beanObj);	
public HashMap<String, Object> validatePrevFileUpload(RupaySettlementBean beanObj);
public HashMap<String, Object> updateFileSettlement(RupaySettlementBean beanObj,int count);
public HashMap<String, List<RupaySettlementBean>>  getTTUMData(String settlementDate);
boolean settlementRollback(RupayUploadBean beanObj);
}
