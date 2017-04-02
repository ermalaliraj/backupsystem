package com.ea.cs.gui.menu;

import java.io.Serializable;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.primefaces.component.menuitem.MenuItem;

@ManagedBean(name = MenuManager.BEAN_NAME)
@SessionScoped
public class MenuManager extends AMenuManager implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String BEAN_NAME = "menuManager";
	private static final Logger logger = Logger.getLogger(MenuManager.class);

	public MenuManager() {
	}
	
	@PostConstruct
	protected void init() {
		try {
			super.loadMenu();
		} catch (Exception e) {
			logger.debug("Error loading menu on init() ", e);
		}
	}

	@Override
	protected MenuItem buildMenuItem(MenuDTO m, Properties menuProp) {
		MenuItem item = new MenuItem();
		item.setValue(m.getTitoloMenu());
		item.setAjax(true);
		item.setUpdate(":pagecontent,:topForm,:messageErrorForm"); 
		logger.debug("MenuItem [id:"+m.getIdMenu()+", title: "+m.getTitoloMenu()+", ajax:"+true+"]");
		
		ExpressionFactory factory = FacesContext.getCurrentInstance().getApplication().getExpressionFactory();
		MethodExpression methodExpression = factory.createMethodExpression(
				FacesContext.getCurrentInstance().getELContext()
				, "#{" + BEAN_NAME + ".executeMenuSelection(\"" + m.getIdMenu() + "\")}"
				, String.class
				, new Class[] { String.class });
		item.setActionExpression(methodExpression);
		return item;
	}

	@Override
	protected String getBeanName() {
		return BEAN_NAME;
	}

}
