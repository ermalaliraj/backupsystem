package com.ea.bs.ru.config;

@Deprecated
public class ConfigDescriptor {
	
	private long idRu;
	private String name;
	private String description;
	private boolean enabled;
	
	private boolean cryptedData;
	
	public long getIdRu() {
		return idRu;
	}
	public void setIdRu(long idRu) {
		this.idRu = idRu;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isCryptedData() {
		return cryptedData;
	}
	public void setCryptedData(boolean cryptedData) {
		this.cryptedData = cryptedData;
	}
	
}
