package com.recon.dao;

import java.util.ArrayList;
import java.util.List;

import com.recon.model.LoginBean;
import com.recon.model.NavigationBean;

public interface NavigationDao {

	public List<NavigationBean> viewMenu(LoginBean loginBean) throws Exception;
}
