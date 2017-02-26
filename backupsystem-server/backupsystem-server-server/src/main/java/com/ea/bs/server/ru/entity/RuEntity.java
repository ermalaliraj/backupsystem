package com.ea.bs.server.ru.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.ea.bs.api.web.ru.StatusRu;

@Entity
public class RuEntity implements Serializable {

	private static final long serialVersionUID = -4337041024452939917L;

	@Id
	private Long idRu;
	private String name;
	private String description;
	
	@Enumerated(EnumType.STRING)
	private StatusRu status;

	public Long getIdRu() {
		return idRu;
	}

	public void setIdRu(Long idRu) {
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
	
	public Object clone() throws CloneNotSupportedException {
		RuEntity cat = new RuEntity();
		cat.idRu = idRu;
		cat.name = name;
		cat.description = description;
		cat.status = status;
		return cat;
	}
	
	public boolean equals(final Object other) {
        if (!(other instanceof RuEntity))
            return false;
        RuEntity o = (RuEntity) other;
      
        return new EqualsBuilder()
        	.append(idRu, o.idRu)
        	.append(name, o.name)
        	.append(description, o.description)
        	.append(status, o.status)
			.isEquals()
			;
    }
	
    public int hashCode() {
        return new HashCodeBuilder()
        		.append(idRu)
        		.append(name)
        		.append(description)
        		.append(status)
        		.toHashCode();
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
	
	public String toStringShort() {
        return new ToStringBuilder(this, ToStringStyle.NO_FIELD_NAMES_STYLE)
        	.appendSuper(super.toString())
        	.append("idRu", idRu)
        	.append("name", name)
        	.append("description", description)
        	.append("status", status)
        	.toString();
    }

}
