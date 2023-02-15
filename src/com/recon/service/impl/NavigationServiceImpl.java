package com.recon.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.NavigationDao;
import com.recon.model.LoginBean;
import com.recon.model.NavigationBean;
import com.recon.service.NavigationService;

@Component
public class NavigationServiceImpl implements NavigationService {

	@Autowired
	NavigationDao navigationDao;


	@Override
	public List<NavigationBean> viewMenu(LoginBean loginBean) throws Exception {
		try {
			return navigationDao.viewMenu(loginBean);
		} catch (Exception e) {
			
			throw e;
		}
	}

}
