package com.ea.bs.server.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@XmlRootElement(name="ServerConfigDescriptor")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerConfigDescriptor {
	
	private String backupPath;
	private boolean cryptedData;
	private byte[] keyStringSalt;
	
	public String getBackupPath() {
		return backupPath;
	}
	public void setBackupPath(String backupPath) {
		this.backupPath = backupPath;
	}
	public boolean isCryptedData() {
		return cryptedData;
	}
	public void setCryptedData(boolean cryptedData) {
		this.cryptedData = cryptedData;
	}
	public byte[] getKeyStringSalt() {
		return keyStringSalt;
	}
	public void setKeyStringSalt(byte[] keyStringSalt) {
		this.keyStringSalt = keyStringSalt;
	}
	
	public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        	.appendSuper(super.toString())
        	.append("backupPath", backupPath)
        	.append("cryptedData", cryptedData)
			.append("keyStringSalt.length", ((keyStringSalt != null) ? keyStringSalt.length : 0))
        	.toString();
    }
}
