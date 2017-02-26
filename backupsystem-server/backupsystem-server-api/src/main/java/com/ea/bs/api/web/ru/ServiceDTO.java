package com.ea.bs.api.web.ru;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ServiceDTO implements Serializable{
	
	private static final long serialVersionUID = -1249008531288112848L;
	
	private Long idService;
	private Date startDate;
	private Date stopDate;
	private RuDTO ruDTO;
	
	public Long getIdService() {
		return idService;
	}
	public void setIdService(Long idService) {
		this.idService = idService;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getStopDate() {
		return stopDate;
	}
	public void setStopDate(Date stopDate) {
		this.stopDate = stopDate;
	}
	public RuDTO getRuDTO() {
		return ruDTO;
	}
	public void setRuDTO(RuDTO ruDTO) {
		this.ruDTO = ruDTO;
	}
	
	public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        	.appendSuper(super.toString())
        	.append("idService", idService)
        	.append("startDate", startDate)
        	.append("stopDate", stopDate)
        	.append("ru", ruDTO)
        	.toString();
    }
}

