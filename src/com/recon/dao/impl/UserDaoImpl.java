package com.recon.dao.impl;

import static com.recon.model.UserBean.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;

import com.recon.dao.UserDao;
import com.recon.model.UserBean;

@Component
public class UserDaoImpl extends JdbcDaoSupport implements UserDao {

	/**
	 * Input Field Constansts
	 */
	private static final String I_USER_ID="i_user_id";
	private static final String I_USER_NAME="i_user_name";
	//private static final String I_USER_TYPE="i_user_type";
	private static final String I_USER_STATUS="i_user_status";
	private static final String I_ENTRY_BY="i_entry_by";
	private static final String I_UPDT_BY="i_updt_by";

	/**
	 * Output Constants
	 */
	private static final String O_USER_COUNT="o_user_count";
	private static final String O_USER_CUR="o_user_cur";
	private static final String O_SESSION_ID="o_session_id";
	private static final String O_ERROR_CODE="o_error_code";
	private static final String O_ERROR_MESSAGE="o_error_message";

	@Override
	public List<UserBean> viewUser(UserBean userBean) throws Exception {
		try{

			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_USER_ID, userBean.getUser_id());

			ViewUserListProc viewUserListProc = new ViewUserListProc(getJdbcTemplate());
			Map<String, Object> outParams = viewUserListProc.execute(inParams);

			if(outParams.get(O_ERROR_MESSAGE) != null && Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0){
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}
			return viewUserListProc.getUserList();
		}catch(Exception e){
			throw e;
		}
	}

	private class ViewUserListProc extends StoredProcedure{
		private static final String view_user_list_proc = "VIEW_USER_LIST";
		List<UserBean> userList = new ArrayList<UserBean>(); 

		/**
		 * @return the userList
		 */
		public List<UserBean> getUserList() {
			return userList;
		}

		public ViewUserListProc(JdbcTemplate jdbcTemplate){
			super(jdbcTemplate, view_user_list_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_USER_CUR, Types.REF_CURSOR, new UserRowMapper()));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
		
		private class UserRowMapper implements RowCallbackHandler{

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				UserBean userBean = new UserBean();

				userBean.setUser_id(rs.getString(USER_ID));
				userBean.setUser_name(rs.getString(USER_NAME));
				userBean.setUser_status(rs.getString(USER_STATUS));
				userBean.setLast_login(rs.getString(LAST_LOGIN));
				userBean.setEntry_dt(rs.getString(ENTRY_DT));
				userBean.setEntry_by(rs.getString(ENTRY_BY));
				userBean.setUpdt_dt(rs.getString(UPDT_DT));
				userBean.setUpdt_by(rs.getString(UPDT_BY));

				userList.add(userBean);
			}
		}
	}

	@Override
	public UserBean viewUserDetail(UserBean userBean) throws Exception {
		try{

			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_USER_ID, userBean.getUser_id());

			ViewUserDetailProc viewUserDetailProc = new ViewUserDetailProc(getJdbcTemplate());
			Map<String, Object> outParams = viewUserDetailProc.execute(inParams);

			if(outParams.get(O_ERROR_MESSAGE) != null && Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0){
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}

			return viewUserDetailProc.getUserList().get(0);

		}catch(Exception e){
			throw e;
		}
	}
	
	private class ViewUserDetailProc extends StoredProcedure{
		private static final String view_user_detail_proc = "VIEW_USER_DETAIL";
		List<UserBean> userList = new ArrayList<UserBean>(); 

		/**
		 * @return the userList
		 */
		public List<UserBean> getUserList() {
			return userList;
		}

		public ViewUserDetailProc(JdbcTemplate jdbcTemplate){
			super(jdbcTemplate, view_user_detail_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_USER_CUR, Types.REF_CURSOR, new UserRowMapper()));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

		private class UserRowMapper implements RowCallbackHandler{

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				UserBean userBean = new UserBean();

				userBean.setUser_id(rs.getString("user_id"));
				userBean.setUser_name(rs.getString("user_name"));
				userBean.setUser_status(rs.getString("user_status"));
				userBean.setLast_login(rs.getString("last_login"));
				userBean.setEntry_dt(rs.getString("entry_dt"));
				userBean.setUpdt_by(rs.getString("updt_by"));

				userList.add(userBean);
			}
		}
	}

	@Override
	public void modifyUser(UserBean userBean) throws Exception {
		try{
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_USER_ID, userBean.getUser_id());
			inParams.put(I_USER_NAME, userBean.getUser_name());
			inParams.put(I_USER_STATUS, userBean.getUser_status());
			inParams.put(I_UPDT_BY, userBean.getUpdt_by());

			ModifyUserProc modifyUserProc = new ModifyUserProc(getJdbcTemplate());
			Map<String, Object> outParams = modifyUserProc.execute(inParams);
			if(outParams.get(O_ERROR_MESSAGE) != null && Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0){
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}
		}catch(Exception e){
			throw e;
		}
	}

	private class ModifyUserProc extends StoredProcedure{
		private static final String modify_user_proc = "MODIFY_USER";

		public ModifyUserProc(JdbcTemplate jdbcTemplate){
			super(jdbcTemplate, modify_user_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(I_USER_NAME, Types.VARCHAR));
			//declareParameter(new SqlParameter(I_USER_TYPE, Types.VARCHAR));
			declareParameter(new SqlParameter(I_USER_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(I_UPDT_BY, Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
	}

	@Override
	public void addUser(UserBean userBean) throws Exception {
		try{
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_USER_ID, userBean.getUser_id());
			inParams.put(I_USER_NAME, userBean.getUser_name());
			inParams.put(I_USER_STATUS, userBean.getUser_status());
			inParams.put(I_ENTRY_BY, userBean.getEntry_by());

			AddUserProc addUserProc = new AddUserProc(getJdbcTemplate());
			Map<String, Object> outParams = addUserProc.execute(inParams);
			if(outParams.get(O_ERROR_MESSAGE) != null && Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0){
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}
		}catch(Exception e){
			throw e;
		}
	}

	private class AddUserProc extends StoredProcedure{
		private static final String add_user_proc = "ADD_USER";

		public AddUserProc(JdbcTemplate jdbcTemplate){
			super(jdbcTemplate, add_user_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, Types.VARCHAR));
			declareParameter(new SqlParameter(I_USER_NAME, Types.VARCHAR));
			declareParameter(new SqlParameter(I_USER_STATUS, Types.VARCHAR));
			declareParameter(new SqlParameter(I_ENTRY_BY, Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
	}

	@Override
	public void deleteUser(UserBean userBean) throws Exception {
		try{
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_USER_ID, userBean.getUser_id());

			DeleteUserProc deleteUserProc = new DeleteUserProc(getJdbcTemplate());
			Map<String, Object> outParams = deleteUserProc.execute(inParams);
			if(outParams.get(O_ERROR_MESSAGE) != null && Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0){
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}
		}catch(Exception e){
			throw e;
		}
	}

	private class DeleteUserProc extends StoredProcedure{
		private static final String delete_user_proc = "DELETE_USER";

		public DeleteUserProc(JdbcTemplate jdbcTemplate){
			super(jdbcTemplate, delete_user_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
	}

	@Override
	public List<UserBean> liveUser(UserBean userBean) throws Exception {
		try{
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_USER_ID, userBean.getUser_id());

			LiveUserProc liveUserProc = new LiveUserProc(getJdbcTemplate());
			Map<String, Object> outParams = liveUserProc.execute(inParams);

			if(outParams.get(O_ERROR_MESSAGE) != null && Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0){
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}

			return liveUserProc.getUser_list();
		}catch(Exception e){
			throw e;
		}
	}
		
	
	private class LiveUserProc extends StoredProcedure{
		private static final String live_user_proc = "LIVE_USER";
		List<UserBean> user_list = new ArrayList<UserBean>();
		
		/**
		 * @return the user_list
		 */
		public List<UserBean> getUser_list() {
			return user_list;
		}

		public LiveUserProc(JdbcTemplate jdbcTemplate){
			super(jdbcTemplate, live_user_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_USER_CUR, Types.REF_CURSOR, new UserMapper()));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
		
		private class UserMapper implements RowCallbackHandler{

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				UserBean userBean = new UserBean();
				userBean.setUser_id(rs.getString("user_id"));
				userBean.setUser_name(rs.getString("user_name"));
				userBean.setIn_time(rs.getString("in_time"));
				userBean.setIp_address(rs.getString("ip_address"));

				user_list.add(userBean);
			}
		}
	}

	@Override
	public void endUserSession(UserBean userBean) throws Exception {
		try{
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_USER_ID, userBean.getUser_id());

			EndUserSessionProc endUserSessionProc = new EndUserSessionProc(getJdbcTemplate());
			Map<String, Object> outParams = endUserSessionProc.execute(inParams);

			if(outParams.get(O_ERROR_MESSAGE) != null && Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0){
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}

		}catch(Exception e){
			throw e;
		}
	}
	
	private class EndUserSessionProc extends StoredProcedure{
		private static final String end_user_session ="LOG_OUT";
		
		public EndUserSessionProc(JdbcTemplate jdbcTemplate){
			super(jdbcTemplate, end_user_session);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
	}

	@Override
	public Map<String, Object> currentUserStatus(UserBean userBean) throws Exception {
		try{
			Map<String, Object> user_map = new HashMap<String, Object>();
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_USER_ID, userBean.getUser_id());

			CurrentUserStatusFunc currentUserStatusFunc = new CurrentUserStatusFunc(getJdbcTemplate());
			Map<String, Object> outParams = currentUserStatusFunc.execute(inParams);

			if(outParams.get(O_ERROR_MESSAGE) != null && Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0){
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}

			user_map.put("count", String.valueOf(outParams.get(O_USER_COUNT)));
			user_map.put("session_id", String.valueOf(outParams.get(O_SESSION_ID)));
			return user_map;
		}catch(Exception e){
			throw e;
		}
	}

	private class CurrentUserStatusFunc extends StoredProcedure{
		private static final String current_user_status = "USER_STATUS";

		public CurrentUserStatusFunc(JdbcTemplate jdbcTemplate){
			super(jdbcTemplate, current_user_status);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_USER_COUNT, Types.INTEGER));
			declareParameter(new SqlOutParameter(O_SESSION_ID, Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}
	}

	@Override
	public List<UserBean> userLog(UserBean userBean) throws Exception {
		try{
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_USER_ID, userBean.getUser_id());

			UserLogProc userLogProc = new UserLogProc(getJdbcTemplate());
			Map<String, Object> outParams = userLogProc.execute(inParams);

			if(outParams.get(O_ERROR_MESSAGE) != null && Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0){
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}
			return userLogProc.getUser_log_list();
		}catch(Exception e){
			throw e;
		}
	}
	
	private class UserLogProc extends StoredProcedure{
		private static final String user_log_proc = "VIEW_USER_LOG";
		private List<UserBean> user_log_list = new ArrayList<UserBean>();

		/**
		 * @return the user_log_list
		 */
		public List<UserBean> getUser_log_list() {
			return user_log_list;
		}

		public UserLogProc(JdbcTemplate jdbcTemplate){
			super(jdbcTemplate, user_log_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_USER_CUR, Types.REF_CURSOR, new UserLogMapper()));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

		private class UserLogMapper implements RowCallbackHandler{

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				UserBean userBean = new UserBean();
				userBean.setUser_id(rs.getString("user_id"));
				userBean.setIn_time(rs.getString("in_time"));
				userBean.setOut_time(rs.getString("out_time"));
				userBean.setIp_address(rs.getString("ip_address"));

				user_log_list.add(userBean);
			}
		}
	}
	
	

}