package com.recon.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.recon.util.SearchData;

public interface ISearchService {

	public SearchData returndata(SearchData searchData) ;

	public ArrayList<String> getttumdetails(SearchData searchdata);
}
