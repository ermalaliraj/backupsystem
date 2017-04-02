package com.ea.cs.gui.util;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ea.cs.gui.user.UserBean;

public class JSFUtil implements Serializable {

	private static final long serialVersionUID = 1L;

	public static HttpSession getHttpSession() {
		return (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
	}

	public static HttpServletRequest getHttpRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

	@SuppressWarnings("unchecked")
	public static <T> T findBean(String beanName) {
		FacesContext context = FacesContext.getCurrentInstance();
		return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
	}

	public static String getMessageResourceString(String key, String[] params) {
		FacesContext context = FacesContext.getCurrentInstance();

		Locale l = Locale.ITALIAN;
		UserBean ub = findBean(UserBean.BEAN_NAME);
		if (ub != null) {
			l = ub.getLocale();
		}

		String text = null;
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "lbl");

		try {
			text = bundle.getString(key);
		} catch (MissingResourceException e) {
			text = "?? key " + key + " not found ??";
		}
		if (params != null) {
			MessageFormat mf = new MessageFormat(text, l);
			text = mf.format(params, new StringBuffer(), null).toString();
		}
		return text;
	}

	public static void addInfoMessage(String id, String key, String[] params) {
		FacesContext.getCurrentInstance().addMessage(id
					, new FacesMessage(FacesMessage.SEVERITY_INFO
					, getMessageResourceString(key, params)
					, null));
	}

	public static void addWarningMessage(String id, String key, String[] params) {
		FacesContext.getCurrentInstance().addMessage(id
				, new FacesMessage(FacesMessage.SEVERITY_WARN, getMessageResourceString(key, params)
				, null));
	}

	public static void addErrorMessage(String id, String key, String[] params) {
		FacesContext.getCurrentInstance().addMessage(id
				, new FacesMessage(FacesMessage.SEVERITY_ERROR, getMessageResourceString(key, params)
				, null));
	}

	public static void addErrorMessage(String id, String key, String[] params, String message) {
		FacesContext.getCurrentInstance().addMessage(id
				, new FacesMessage(FacesMessage.SEVERITY_ERROR, "<html>" + getMessageResourceString(key, params) + ":<br />" + message + "</html>"
				, null));
	}

//	public static void addPopupMessage(String id, String key, String[] params) {
//		SystemMessageBean rb = findBean(SystemMessageBean.BEAN_NAME);
//		rb.setSysMessage(getMessageResourceString(key, params));
//
//		RequestContext requestContext = RequestContext.getCurrentInstance();
//		requestContext.execute("popupSystemMessage.show();");
//	}
}
