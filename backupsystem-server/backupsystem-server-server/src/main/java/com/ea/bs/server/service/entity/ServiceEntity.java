package com.ea.bs.server.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.ea.bs.server.ru.entity.RuEntity;

@Entity
public class ServiceEntity implements Serializable {

	private static final long serialVersionUID = -4337041024452939917L;

	@Id
	private Long idService;
	
	@OneToOne
	@JoinColumn(name = "idRu")
	private RuEntity ru;
    
	public Long getIdService() {
		return idService;
	}

	public void setIdService(Long idService) {
		this.idService = idService;
	}

	public RuEntity getRu() {
		return ru;
	}

	public void setRu(RuEntity ru) {
		this.ru = ru;
	}

	public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        	.appendSuper(super.toString())
        	.append("idService", idService)
        	.append("ru", ru)
        	.toString();
    }
	
	public String toStringShort() {
        return new ToStringBuilder(this, ToStringStyle.NO_FIELD_NAMES_STYLE)
        	.appendSuper(super.toString())
        	.append("idService", idService)
        	.append("ru", ru)
        	.toString();
    }

}
