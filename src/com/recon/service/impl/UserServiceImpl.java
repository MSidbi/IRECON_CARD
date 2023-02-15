package com.recon.service.impl;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.UserDao;
import com.recon.model.UserBean;
import com.recon.service.UserService;


@Component
@SuppressWarnings({"unchecked"})
public class UserServiceImpl implements UserService {

	@Autowired
	UserDao userDao;
	
	@Override
	public List<UserBean> viewUser(UserBean userBean) throws Exception {
		return userDao.viewUser(userBean);
	}

	@Override
	public UserBean viewUserDetail(UserBean userBean) throws Exception {
		return userDao.viewUserDetail(userBean);
	}

	@Override
	public void modifyUser(UserBean userBean) throws Exception {
		userDao.modifyUser(userBean);
	}

	@Override
	public void addUser(UserBean userBean) throws Exception {
		userDao.addUser(userBean);
	}

	@Override
	public void deleteUser(UserBean userBean) throws Exception {
		userDao.deleteUser(userBean);
	}

	@Override
	public List<UserBean> liveUser(UserBean userBean) throws Exception {
		return userDao.liveUser(userBean);
	}

	@Override
	public void endUserSession(UserBean userBean) throws Exception {
		userDao.endUserSession(userBean);
	}

	@Override
	public String liveUserJSON(UserBean userBean) throws Exception {
		try{
			JSONObject jsonObject = new JSONObject();
			List<UserBean> userList = userDao.liveUser(userBean);
			JSONArray jsonArray = new JSONArray();
			for(UserBean bean : userList){
				JSONObject object = new JSONObject();
				object.put("user_id", bean.getUser_id());
				object.put("user_name", bean.getUser_name());
				object.put("in_time", bean.getIn_time());
				object.put("ip_address", bean.getIp_address());
				jsonArray.add(object);
			}
			jsonObject.put("user_dets", jsonArray);
			return jsonObject.toString();

		}catch(Exception e){
			throw e;
		}
	}

	@Override
	public Map<String, Object> currentUserStatus(UserBean userBean) throws Exception {
		return userDao.currentUserStatus(userBean);
	}

	@Override
	public List<UserBean> userLog(UserBean userBean) throws Exception {
		return userDao.userLog(userBean);
	}

}