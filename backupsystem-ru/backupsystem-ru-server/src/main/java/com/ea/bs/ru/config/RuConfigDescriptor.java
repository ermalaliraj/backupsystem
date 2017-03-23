package com.ea.bs.ru.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@XmlRootElement(name="RuConfigDescriptor")
@XmlAccessorType(XmlAccessType.FIELD)
public class RuConfigDescriptor {
	
	private long idRu;
	private String name;
	private String description;
	private boolean enabled;
	private boolean cryptedData;
	private boolean backupSent;
	private String pkcs11ImplementationPath;
	
	private String pathWithFilesToBeSend;
	private long maxNrFilesPerMsg;
	private long maxSizeBytePerFile;
	private long sendFilesTimeout;
	private long sendFilesFirstFire;
	private String sendFilesTimerInfo;
	
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
	public boolean isBackupSent() {
		return backupSent;
	}
	public void setBackupSent(boolean backupSent) {
		this.backupSent = backupSent;
	}
	public String getPkcs11ImplementationPath() {
		return pkcs11ImplementationPath;
	}
	public void setPkcs11ImplementationPath(String pkcs11ImplementationPath) {
		this.pkcs11ImplementationPath = pkcs11ImplementationPath;
	}
	public String getPathWithFilesToBeSend() {
		return pathWithFilesToBeSend;
	}
	public void setPathWithFilesToBeSend(String pathWithFilesToBeSend) {
		this.pathWithFilesToBeSend = pathWithFilesToBeSend;
	}
	public long getMaxNrFilesPerMsg() {
		return maxNrFilesPerMsg;
	}
	public void setMaxNrFilesPerMsg(long maxNrFilesPerMsg) {
		this.maxNrFilesPerMsg = maxNrFilesPerMsg;
	}
	public long getMaxSizeBytePerFile() {
		return maxSizeBytePerFile;
	}
	public void setMaxSizeBytePerFile(long maxSizeBytePerFile) {
		this.maxSizeBytePerFile = maxSizeBytePerFile;
	}
	public long getSendFilesTimeout() {
		return sendFilesTimeout;
	}
	public void setSendFilesTimeout(long sendFilesTimeout) {
		this.sendFilesTimeout = sendFilesTimeout;
	}
	public long getSendFilesFirstFire() {
		return sendFilesFirstFire;
	}
	public void setSendFilesFirstFire(long sendFilesFirstFire) {
		this.sendFilesFirstFire = sendFilesFirstFire;
	}
	public String getSendFilesTimerInfo() {
		return sendFilesTimerInfo;
	}
	public void setSendFilesTimerInfo(String sendFilesTimerInfo) {
		this.sendFilesTimerInfo = sendFilesTimerInfo;
	}
	
	public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        	.appendSuper(super.toString())
        	.append("idRu", idRu)
        	.append("name", name)
        	.append("description", description)
        	.append("enabled", enabled)
        	.append("cryptedData", cryptedData)
        	.append("backupSent", backupSent)
        	.append("pkcs11ImplementationPath", pkcs11ImplementationPath)
        	
        	.append("pathWithFilesToBeSend", pathWithFilesToBeSend)
        	.append("maxNrFilesPerMsg", maxNrFilesPerMsg)
        	.append("maxSizeBytePerFile", maxSizeBytePerFile)
        	.append("sendFilesTimeout", sendFilesTimeout)
        	.append("sendFilesFirstFire", sendFilesFirstFire)
        	.append("sendFilesTimerInfo", sendFilesTimerInfo)
        	.toString();
    }
}
