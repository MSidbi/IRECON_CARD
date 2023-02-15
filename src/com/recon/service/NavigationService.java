package com.recon.service;

import java.util.ArrayList;
import java.util.List;

import com.recon.model.LoginBean;
import com.recon.model.NavigationBean;

public interface NavigationService {
	
	public List<NavigationBean> viewMenu(LoginBean loginBean) throws Exception; 
}
