package com.recon.service;

import java.util.ArrayList;

import com.recon.model.ForceMatchBean;
import com.recon.model.SettlementTypeBean;

public interface IForceMatchService {

	ArrayList<ForceMatchBean> getReconData(String trim, String trim2,
			String trim3, String trim4);

	ArrayList<ForceMatchBean> getChngReconData(String trim, String trim2,
			int jtStartIndex, int jtPageSize);

	int getReconDataCount(String trim, String trim4);

}
