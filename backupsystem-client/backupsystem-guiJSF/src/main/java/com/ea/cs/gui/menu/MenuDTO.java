package com.ea.cs.gui.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuDTO implements Serializable {

	private static final long serialVersionUID = -2210797062637603024L;

	int idMenu;
	private String titoloMenu;
	private List<MenuDTO> sottoMenu;

	public MenuDTO(int idMenu, String titoloMenu) {
		this.idMenu = idMenu;
		this.titoloMenu = titoloMenu;
		this.sottoMenu = new ArrayList<MenuDTO>();
	}

	public int getIdMenu() {
		return idMenu;
	}
	public void setIdMenu(int idMenu) {
		this.idMenu = idMenu;
	}
	public String getTitoloMenu() {
		return titoloMenu;
	}
	public void setTitoloMenu(String titoloMenu) {
		this.titoloMenu = titoloMenu;
	}
	public List<MenuDTO> getSottoMenu() {
		return sottoMenu;
	}
	public void setSottoMenu(List<MenuDTO> sottoMenu) {
		this.sottoMenu = sottoMenu;
	}

	@Override
	public String toString() {
		StringBuffer description = new StringBuffer("\nidMenu: "+idMenu);
		description.append(", titoloMenu: "+titoloMenu);
		description.append(", sottoMenu: "+sottoMenu);
		return description.toString();
	}

}
