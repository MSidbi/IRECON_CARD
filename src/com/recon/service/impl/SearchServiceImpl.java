package com.recon.service.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.ISearchDAO;
import com.recon.service.ISearchService;
import com.recon.util.SearchData;

@Component
public class SearchServiceImpl implements ISearchService {

	@Autowired
	ISearchDAO searchdao;
	
	@Override
	public SearchData returndata(SearchData searchData) {
		// TODO Auto-generated method stub
		return searchdao.returndata(searchData) ;
	}

	@Override
	public ArrayList<String> getttumdetails(SearchData searchdata) {
		// TODO Auto-generated method stub
		return searchdao.getttumdetails( searchdata) ;
	}

	
}
