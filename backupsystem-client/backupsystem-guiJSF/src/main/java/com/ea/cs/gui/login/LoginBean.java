package com.ea.cs.gui.login;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.ea.cs.gui.menu.MenuDTO;
import com.ea.cs.gui.menu.MenuManager;
import com.ea.cs.gui.user.RoleDTO;
import com.ea.cs.gui.user.UserBean;
import com.ea.cs.gui.user.UserDTO;
import com.ea.cs.gui.util.Constants;
import com.ea.cs.gui.util.JSFUtil;

@ManagedBean(name = LoginBean.BEAN_NAME)
@ViewScoped
public class LoginBean implements Serializable {

	private static final long serialVersionUID = -6932638910647469134L;
	public static final String BEAN_NAME = "loginBean";
	private static final Logger logger = Logger.getLogger(LoginBean.class);

	private String username = null;
	private String password = null;
	private String sessionid = null;

	public String executeLogin() {
		String rv = null;
		try {
			logger.info("Login for user: "+username+", pwd: "+password);
			
			String usernameUppercase = this.username.toUpperCase();
			
			HttpSession session = JSFUtil.getHttpSession();
			String sessionId = session.getId();
			logger.info("LOGIN for user:" + usernameUppercase + ", sessione: " + sessionId);
			JSFUtil.getHttpSession().setAttribute(Constants.USER_KEY, BigInteger.ONE);
			
			UserBean ub = JSFUtil.findBean(UserBean.BEAN_NAME);
			
			UserDTO u = new UserDTO();
			u.setName(usernameUppercase);
			u.setSurname("surname");
			u.setIdUser(10001);
			ub.setUserDTO(u);
			
			List<MenuDTO> menuList = getUserMenuFromRole(ub.getMenuPropertiesName(), u.getRoles());
			logger.info("User menu from DB: "+menuList);
			ub.setUsermenu(menuList);
			
			//Set deafult page
			Object menuInSession = JSFUtil.findBean(MenuManager.BEAN_NAME);
			MenuManager mm = (MenuManager) menuInSession;
			mm.setCurrentMenuPage("Ticket PS");
			mm.setCurrentMenuPageFolder("home");

			return "private/index.xhtml?faces-redirect=true";	
		} catch (Exception e) {
			logger.error("Eccezione generale in executeLogin()", e);
			this.username = "";
			this.password = "";
			return rv;
		}
	}

	/**
	 * In a real application the menu structure will get from the join of tables:
	 * APP_ROLE, APP_ROLE_MENU, APP_MENU.
	 * For example scope the following structure:
	 * User (1000)
	 * 	- Home (1001)
	 * 	- Logout (1002)
	 * Configuration (2000)
	 * Remote Unit (3000)
	 */
	private List<MenuDTO> getUserMenuFromRole(String menuPropertiesName, List<RoleDTO> roles) {
		List<MenuDTO> menuList = new ArrayList<MenuDTO>();
		
		MenuDTO user = new MenuDTO(1000, "User");
		final MenuDTO home = new MenuDTO(1001, "Home");
		final MenuDTO logout = new MenuDTO(1002, "Logout");
		MenuDTO config = new MenuDTO(2000, "Config");
		MenuDTO ru = new MenuDTO(3000, "Remote Unit");
		
		List<MenuDTO> userItems = new ArrayList<MenuDTO>();
		userItems.add(home);
		userItems.add(logout);
		user.setSottoMenu(userItems);
		
		menuList.add(user);
		menuList.add(config);
		menuList.add(ru);
		return menuList;
	}

	public String executeLogout() {
		String rv = null;
		try {
			UserBean ub = JSFUtil.findBean(UserBean.BEAN_NAME);

			HttpSession session = JSFUtil.getHttpSession();
			String sessionId = session.getId();
			
			logger.info("Logout con successo per "+ub.getUserDTO().getIdUser()+", sessionId:" + sessionId);
			session.invalidate();
			return "/login.xhtml?faces-redirect=true";
		} catch (Exception e) {
			logger.error("Errore generale (esterno) in executeLogout()", e);
		}
		return rv;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}
	
}
