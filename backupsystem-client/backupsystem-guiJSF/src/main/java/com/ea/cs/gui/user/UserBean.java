package com.ea.cs.gui.user;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.ea.cs.gui.menu.MenuDTO;

@ManagedBean(name = UserBean.BEAN_NAME)
@SessionScoped
public class UserBean implements Serializable {

	private static final long serialVersionUID = 603665593160504761L;
	public static final String BEAN_NAME = "userBean";

	private String menuPropertiesName = "menu-bs";
	private String homepageFolder = "home";
	private List<MenuDTO> usermenu = null;
	private UserDTO userDTO = null;
	private Locale locale;

	@PostConstruct
	public void init() {
		locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	}

	public Locale getLocale() {
		return locale;
	}

	public String getLanguage() {
		return locale.getLanguage();
	}

	public void setLanguage(String language) {
		locale = new Locale(language);
		FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
	}

	public String getHomepageFolder() {
		return homepageFolder;
	}

	public void setHomepageFolder(String homepageFolder) {
		this.homepageFolder = homepageFolder;
	}

	public List<MenuDTO> getUsermenu() {
		return usermenu;
	}

	public void setUsermenu(List<MenuDTO> usermenu) {
		this.usermenu = usermenu;
	}

	public UserDTO getUserDTO() {
		return userDTO;
	}

	public void setUserDTO(UserDTO userDTO) {
		this.userDTO = userDTO;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getMenuPropertiesName() {
		return menuPropertiesName;
	}

	public void setMenuPropertiesName(String menuPropertiesName) {
		this.menuPropertiesName = menuPropertiesName;
	}
	
}
