package com.ea.bs.api.web.ru;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class RuDTO implements Serializable{
	
	private static final long serialVersionUID = -1249008531288112848L;
	private long idRu;
	private String name;
	private String description;
	private StatusRu status;
	
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
	public StatusRu getStatus() {
		return status;
	}
	public void setStatus(StatusRu status) {
		this.status = status;
	}
	
	public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        	.appendSuper(super.toString())
        	.append("idRu", idRu)
        	.append("name", name)
        	.append("description", description)
        	.append("status", status)
        	.toString();
    }
	
}

