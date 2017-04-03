package com.ea.cs.gui.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import com.ea.cs.gui.util.JSFUtil;

@ManagedBean(name = RuController.BEAN_NAME)
@SessionScoped
public class RuController implements Serializable {

	private static final long serialVersionUID = 2101815348090102456L;
	private static final Logger logger = Logger.getLogger(RuController.class);
	public static final String BEAN_NAME = "ruController";

	private List<RuBean> ruList;
	private RuBean ruSelected;
	
	private String name;
	private String ip;
	private String description;
	
	private String newRuName;
	private String newRuIp;
	private String newRuDescription;

	@PostConstruct
	public void init() {
		resetSearch();
		search();
	}

	private void resetSearch() {
		name = null;
		ip = null;
		description = null;
		ruSelected = null;
		ruList = new ArrayList<RuBean>();
	}

	public void search() {
		try {
			logger.debug("Search with filters: name: "+name+", ip: "+ip+", desc: "+description+", ruSelected: "+ruSelected);
		} catch (Exception e) {
			logger.error("Errore ricercaFase1()", e);
			JSFUtil.addErrorMessage(null, "generic_error", new String[] { e.getMessage() });
		}
	}
	
	public void saveRu(ActionEvent ae) {
		try {
			//ruSelected = (RuBean) ae.getComponent().getAttributes().get("item");
			logger.debug("Save Ru: newRuName: "+newRuName+", newRuIp: "+newRuIp+", newRuDescription: "+newRuDescription);
			
			RuBean r = new RuBean();
			r.setName(newRuName);
			r.setIp(newRuIp);
			r.setDescription(newRuDescription);
			r.setEnable(false);
			ruList.add(r);			
		} catch (Exception e) {
			logger.error("Errore ricercaFase1()", e);
			JSFUtil.addErrorMessage(null, "generic_error", new String[] { e.getMessage() });
		}
	}

	public void openPopupDetailListener(ActionEvent ae) {
		try{
			
			logger.info("Open popup for RU: "+ruSelected);
		
		} catch (Exception e) {
			JSFUtil.addErrorMessage(null, "generic_error", new String[] { e.getMessage() });
			logger.error("Errore generale in openPopupDetailListener", e);
		}
	}

	public List<RuBean> getRuList() {
		return ruList;
	}

	public void setRuList(List<RuBean> ruList) {
		this.ruList = ruList;
	}

	public RuBean getRuSelected() {
		return ruSelected;
	}

	public void setRuSelected(RuBean ruSelected) {
		this.ruSelected = ruSelected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNewRuName() {
		return newRuName;
	}

	public void setNewRuName(String newRuName) {
		this.newRuName = newRuName;
	}

	public String getNewRuIp() {
		return newRuIp;
	}

	public void setNewRuIp(String newRuIp) {
		this.newRuIp = newRuIp;
	}

	public String getNewRuDescription() {
		return newRuDescription;
	}

	public void setNewRuDescription(String newRuDescription) {
		this.newRuDescription = newRuDescription;
	}

}
