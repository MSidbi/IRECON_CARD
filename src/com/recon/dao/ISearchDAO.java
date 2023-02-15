package com.recon.dao;

import java.util.ArrayList;

import com.recon.util.SearchData;

public interface ISearchDAO {

	
	public SearchData returndata(SearchData searchData) ;

	public ArrayList<String> getttumdetails(SearchData searchdata);
}
