package com.ea.cs.gui.user;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class RoleDTO implements Serializable {

	private static final long serialVersionUID = -4096988983429669259L;

	public int idRole;
	public String description;

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof RoleDTO)){
            return false;
		}
		RoleDTO o = (RoleDTO) other;
        return new EqualsBuilder()
        	.append(idRole, o.idRole)
			.isEquals();
	}

	@Override
	public int hashCode() {
		 return new HashCodeBuilder()
		 	.append(idRole)
		 	.hashCode();
	}

	public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        	.appendSuper(super.toString())
        	.append("idRole", idRole)
        	.append("description", description)
        	.toString();
    }
}
