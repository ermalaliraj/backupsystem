package com.ea.cs.gui.menu;


import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

import com.ea.cs.gui.user.UserBean;
import com.ea.cs.gui.util.JSFUtil;

public abstract class AMenuManager {

	private static final Logger logger = Logger.getLogger(AMenuManager.class);
	
	private MenuModel menu = null;
	private Properties menuProp = new Properties();
	private String currentMenuPageFolder = null;
	private String currentMenuPageLabel = null;

	protected abstract String getBeanName();
	protected abstract MenuItem buildMenuItem(MenuDTO m, Properties menuProp);
	protected abstract void init();

	public AMenuManager() {
	}
	
	private Submenu buildSubMenu(MenuDTO menu, Properties menuProp) {
		Submenu submenu = new Submenu();
		String menuid = String.valueOf(menu.getIdMenu());
		String mlab = null;
		if (menuProp.containsKey("menu.label." + menuid)) {
			mlab = menuProp.getProperty("menu.label." + menuid);
		} else {
			mlab = menu.getTitoloMenu();
		}
		submenu.setLabel(mlab);
		
		for (MenuDTO subm : menu.getSottoMenu()) {
			if (subm.getSottoMenu() != null && subm.getSottoMenu().size() > 0) {
				submenu.getChildren().add(buildSubMenu(subm, menuProp));
			} else {
				MenuItem item = buildMenuItem(subm, menuProp);
				submenu.getChildren().add(item);
			}
		}
		return submenu;
	}

	public String getCurrentMenuPage() {
		return this.currentMenuPageLabel;
	}

	public void setCurrentMenuPage(String page) {
		this.currentMenuPageLabel = page;
	}

	private void logChangeMenu(String message) {
		Thread thread = Thread.currentThread();
		FacesContext fCtx = FacesContext.getCurrentInstance();
		String sessionId = "NO_SESSION";
		if (fCtx != null) {
			HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
			if (session != null) {
				sessionId = session.getId();
			}
		}
		logger.debug("SESSION [" + sessionId + "] THREAD [" + thread.getId() + "-" + thread.getName() + "] " + message);
	}

	public String executeMenuSelection(String menuid) throws Exception {
		String ret = null;
		String mact = null;
		String mpag = null;
		String mlab = null;
		
		try {
			if (menuProp.containsKey("menu.action." + menuid)) {
				mact = menuProp.getProperty("menu.action." + menuid);
				mlab = menuProp.getProperty("menu.label." + menuid);
			}
			if (mact != null && !"".equalsIgnoreCase(mact)) {
				FacesContext fc = FacesContext.getCurrentInstance();
				ExpressionFactory factory = fc.getApplication().getExpressionFactory();
				MethodExpression methodExpression = factory.createMethodExpression(FacesContext.getCurrentInstance().getELContext(), "#{" + mact + "}", String.class, new Class[] {});
				ret = (String) methodExpression.invoke(fc.getELContext(), null);
			}
			if (menuProp.containsKey("menu.pagefolder." + menuid)) {
				mpag = menuProp.getProperty("menu.pagefolder." + menuid);
			}
			if (mpag != null && !"".equalsIgnoreCase(mpag)) {
				this.currentMenuPageFolder = mpag;
				this.currentMenuPageLabel = mlab;
			}
			// if (ret == null) {
			// ret = "/private/pages/" + mpag + "/main.xhtml";
			// }
			this.logChangeMenu("SELECTED MENU ["+menuid+"], page [" + mpag + "], label [" + mlab + "], action [" + mact + "]");
			if (ret != null) {
				this.logChangeMenu("MENU action returned: " + ret);
			}
		} catch (Exception e) {
			this.logChangeMenu("ERROR EXECUTING MENU ID:" + menuid);
			logger.error("Errore selezionando menuId: " + menuid, e);
		}
		return ret;
	}

	public MenuModel getMenuModel() throws Exception {
		return menu;
	}

	public String getCurrentMenuPageFolder() {
		if (this.currentMenuPageFolder == null) {
			UserBean ub = JSFUtil.findBean(UserBean.BEAN_NAME);
			this.currentMenuPageFolder = ub.getHomepageFolder();
			this.logChangeMenu("GET CURRENT (was NULL) MENU PAGE FOLDER [" + this.currentMenuPageFolder + "]");
		}
		return currentMenuPageFolder;
	}

	public void setCurrentMenuPageFolder(String currentMenuPageFolder) {
		this.logChangeMenu("SET CURRENT MENU PAGE FOLDER [" + currentMenuPageFolder + "]");
		this.currentMenuPageFolder = currentMenuPageFolder;
	}

	public void loadMenu() throws Exception {
		UserBean ub = JSFUtil.findBean(UserBean.BEAN_NAME);
		List<MenuDTO> menudto = ub.getUsermenu();
		String menuPropertiesName = ub.getMenuPropertiesName();
		menuPropertiesName = "config/" + menuPropertiesName + ".properties";
		
		try {
			//logger.debug("Loading menu. classloader: "+this.getClass().getClassLoader());
			menuProp.load(this.getClass().getClassLoader().getResourceAsStream(menuPropertiesName));
//			FileInputStream in = new FileInputStream(menuPropertiesName);
//			menuProp.load(in);
		} catch (Exception e) {
			logChangeMenu("File "+menuPropertiesName+" with menu details not found on Classloader path.");
			throw new Exception("File "+menuPropertiesName+" with Menu details not found!");
		}
		this.menu = new DefaultMenuModel();
		String menuStr = "";
		for (MenuDTO m : menudto) {
			menuStr += m.getTitoloMenu() +"["+m.getIdMenu()+"] ";
			if (m.getSottoMenu() != null && m.getSottoMenu().size() > 0) {
				this.menu.addSubmenu(buildSubMenu(m, menuProp));
			} else {
				this.menu.addMenuItem(buildMenuItem(m, menuProp));
			}
		}
		
		logChangeMenu("loadMenu() - full MENUBAR: "+menuStr);
	}

	public void refreshMenu() throws Exception {
		logChangeMenu("Refreshing menu... (check why?)");
		loadMenu();
	}
}
