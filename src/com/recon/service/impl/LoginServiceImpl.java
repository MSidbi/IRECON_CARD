package com.recon.service.impl;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.LoginDao;
import com.recon.model.LoginBean;
import com.recon.model.ProcessDtlBean;
import com.recon.service.LoginService;
import com.recon.util.demo;

@Component
public class LoginServiceImpl implements LoginService {

	@Autowired
	LoginDao loginDao;
	
	Logger logger =Logger.getLogger(LoginServiceImpl.class);
	
	@SuppressWarnings({ "unused", "rawtypes" })
	@Override
	public void validateUser(LoginBean loginBean) throws Exception {
		try {
			logger.info("***** LoginServiceImpl.validateUser Start ****");
			CharSequence cs1 = "int";
			CharSequence cs2 = "INT";
			String username_db = "";
			String usr_typ_db = "";
			final String contextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
			final String connectionURL = "ldap://10.144.18.75";
			final String connectionName = "CN=UID,CN=Users,DC=IDBIBANK,DC=ad";
			final String authentication = null;
			final String protocol = null;
			String user_id = loginBean.getUser_id();
			final String password = loginBean.getPassword();
			final String MEMBER_OF = "memberOf";
			final String[] attrIdsToSearch = new String[] { MEMBER_OF };
			final String SEARCH_BY_SAM_ACCOUNT_NAME = "(sAMAccountName=%s)";
			final String SEARCH_GROUP_BY_GROUP_CN = "(&(objectCategory=group)(cn={0}))";
			String userBase = "DC=IDBIBANK,DC=ad";
			String email = "";

			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
			env.put(Context.PROVIDER_URL, connectionURL);
			env.put(Context.SECURITY_PRINCIPAL, user_id + "@IDBIBANK.ad");
			env.put(Context.SECURITY_CREDENTIALS, password);

			if (authentication != null) {
				env.put(Context.SECURITY_AUTHENTICATION, authentication);
			}
			if (protocol != null) {
				env.put(Context.SECURITY_PROTOCOL, protocol);
			}

			InitialDirContext context = new InitialDirContext(env);
			String filter = String.format(SEARCH_BY_SAM_ACCOUNT_NAME, user_id);
			SearchControls constraints = new SearchControls();
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
			constraints.setReturningAttributes(attrIdsToSearch);
			NamingEnumeration results = context.search(userBase, filter, constraints);
			// Fail if no entries found
			if (results == null || !results.hasMore()) {
				throw new Exception("Invalid Username and/or Password.");
			}

			// Get result for the first entry found
			SearchResult result = (SearchResult) results.next();
			NameParser parser = context.getNameParser("");
			Name contextName = parser.parse(context.getNameInNamespace());
			Name baseName = parser.parse(userBase);

			Name entryName = parser.parse(new CompositeName(result.getName()).get(0));

			// Get the entry's attributes
			Attributes attrs = result.getAttributes();
			Attribute attr = attrs.get(attrIdsToSearch[0]);

			NamingEnumeration e = attr.getAll();
			if (!e.hasMore()) {
				throw new Exception("Employee details unavailable.");
			}

			logger.info("***** LoginServiceImpl.validateUser End ****");
		}catch(AuthenticationException e){
			demo.logSQLException(e, "LoginServiceImpl.validateUser");
			logger.error(" error in LoginServiceImpl.validateUser", new Exception("LoginServiceImpl.validateUser",e));
			throw new AuthenticationException("Invalid Username and/or Password.");
		}catch (Exception e) {
			demo.logSQLException(e, "LoginController.validateUser");
			logger.error(" error in LoginServiceImpl.validateUser", new Exception("LoginServiceImpl.validateUser",e));
			throw e;
		}
	}

	@Override
	public LoginBean getUserDetail(LoginBean loginBean) throws Exception {
		try{
			return loginDao.getUserDetail(loginBean);
		}catch(Exception e){
			throw e;
		}
	}

	@Override
	public void invalidateUser(LoginBean loginBean) throws Exception {
		try{
			loginDao.invalidateUser(loginBean);
		}catch(Exception e){
			throw e;
		}
		
	}
	
	@Override
	public void closeSession(LoginBean loginBean) throws Exception {
		try{
			loginDao.closeSession(loginBean);
		}catch(Exception e){
			throw e;
		}
		
	}

	
	@Override
	public boolean checkIp(LoginBean loginBean) throws Exception{
		return loginDao.checkIp(loginBean);
	}
	
	@Override
	public Map<String, Object> getAllSession(LoginBean loginBean) throws Exception{
			return loginDao.getAllSession(loginBean);
	}

	@Override
	public List<ProcessDtlBean> getProcessdtls(String Flag) {
		
		return loginDao.getProcessdtls(Flag);
	}


}
