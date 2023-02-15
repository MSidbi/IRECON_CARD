package com.recon.dao;

import java.util.List;
import java.util.Map;

import com.recon.util.JsonBean;
import com.recon.util.SearchData;

public interface DisputeDao {

	SearchData returndata(SearchData searchdata) throws Exception;



	boolean updateForceMatchData(String category, String stSubCategory,
			String file_name, String filedate, String insert) throws Exception;



	Map<String, Object> returndata1(JsonBean searchdata, int jtStartIndex,
			int jtPageSize);



	boolean updateDispute(JsonBean searchdata);



	

}
