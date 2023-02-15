package com.recon.dao.impl;

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

import com.recon.dao.NavigationDao;
import com.recon.model.LoginBean;
import com.recon.model.NavigationBean;

@Component
public class NavigationDaoImpl extends JdbcDaoSupport implements NavigationDao {

	/**
	 * Input Field Constants
	 */
	private static final String I_USER_ID = "i_user_id";

	/**
	 * Output Field Constants.
	 */
	private static final String O_MENU_CUR = "o_menu_cur";
	private static final String O_ERROR_CODE = "o_error_code";
	private static final String O_ERROR_MESSAGE = "o_error_message";

	@Override
	public List<NavigationBean> viewMenu(LoginBean loginBean) throws Exception {
		try{
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_USER_ID, loginBean.getUser_id());

			ViewMenuProc viewMenuProc = new ViewMenuProc(getJdbcTemplate());
			Map<String, Object> outParams = viewMenuProc.execute(inParams);
			if(Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0 && outParams.get(O_ERROR_MESSAGE) != null){
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}
			return viewMenuProc.getMenu_list();

		}catch(Exception e){
			throw e;
		}
	}

	private class ViewMenuProc extends StoredProcedure{
		private static final String view_menu_proc = "NAVIGATION_MENU";
		List<NavigationBean> menu_list = new ArrayList<NavigationBean>();

		/**
		 * @return the menu_list
		 */
		public List<NavigationBean> getMenu_list() {
			return menu_list;
		}

		public ViewMenuProc(JdbcTemplate jdbcTemplate){
			super(jdbcTemplate, view_menu_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, Types.VARCHAR));
			declareParameter(new SqlOutParameter(O_MENU_CUR, Types.REF_CURSOR, new MenuMapper()));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, Types.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, Types.VARCHAR));
			compile();
		}

		private class MenuMapper implements RowCallbackHandler{

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				NavigationBean navigationBean = new NavigationBean();

				navigationBean.setPage_id(rs.getInt("page_id"));
				navigationBean.setParent_id(rs.getInt("parent_id"));
				navigationBean.setPage_name(rs.getString("page_name"));
				navigationBean.setPage_url(rs.getString("page_url"));
				navigationBean.setPage_level(rs.getInt("page_level"));
				navigationBean.setPage_title(rs.getString("page_title"));

				menu_list.add(navigationBean);
			}

		}
	}
}