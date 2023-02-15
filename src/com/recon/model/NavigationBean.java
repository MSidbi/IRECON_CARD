package com.recon.model;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class NavigationBean {

	private String user_id;
	private int page_id;
	private int parent_id;
	private String page_name;
	private String page_url;
	private String page_title;
	private int page_level;
	
	private List<NavigationBean> menu;

	/**
	 * @return the user_id
	 */
	public String getUser_id() {
		return user_id;
	}

	/**
	 * @param user_id the user_id to set
	 */
	public void setUser_id(String user_id) {
		if(user_id == null){
			user_id = "";
		}
		this.user_id = user_id.trim();
	}

	/**
	 * @return the page_id
	 */
	public int getPage_id() {
		return page_id;
	}

	/**
	 * @param page_id the page_id to set
	 */
	public void setPage_id(int page_id) {
		this.page_id = page_id;
	}

	/**
	 * @return the parent_id
	 */
	public int getParent_id() {
		return parent_id;
	}

	/**
	 * @param parent_id the parent_id to set
	 */
	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}

	/**
	 * @return the page_name
	 */
	public String getPage_name() {
		return page_name;
	}

	/**
	 * @param page_name the page_name to set
	 */
	public void setPage_name(String page_name) {
		if(page_name == null){
			page_name = "";
		}
		this.page_name = page_name.trim();
	}

	/**
	 * @return the page_url
	 */
	public String getPage_url() {
		return page_url;
	}

	/**
	 * @param page_url the page_url to set
	 */
	public void setPage_url(String page_url) {
		if(page_url == null){
			page_url = "";
		}
		this.page_url = page_url.trim();
	}

	/**
	 * @return the page_title
	 */
	public String getPage_title() {
		return page_title;
	}

	/**
	 * @param page_title the page_title to set
	 */
	public void setPage_title(String page_title) {
		if(page_title == null){
			page_title = "";
		}
		this.page_title = page_title.trim();
	}

	/**
	 * @return the page_level
	 */
	public int getPage_level() {
		return page_level;
	}

	/**
	 * @param page_level the page_level to set
	 */
	public void setPage_level(int page_level) {
		this.page_level = page_level;
	}

	/**
	 * @return the menu_list
	 */
	public List<NavigationBean> getMenu() {
		return menu;
	}

	/**
	 * @param menu_list the menu_list to set
	 */
	public void setMenu(List<NavigationBean> menu) {
		this.menu= menu;
	}
}
