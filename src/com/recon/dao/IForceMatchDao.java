package com.recon.dao;

import java.util.ArrayList;

import com.recon.model.ForceMatchBean;

public interface IForceMatchDao {

	ArrayList<ForceMatchBean> getReconData(String trim, String trim2,
			String trim3, String trim4);

	int getReconDataCount(String trim, String trim4);

	ArrayList<ForceMatchBean> getChngReconData(String trim, String trim2,
			int jtStartIndex, int jtPageSize);

}
