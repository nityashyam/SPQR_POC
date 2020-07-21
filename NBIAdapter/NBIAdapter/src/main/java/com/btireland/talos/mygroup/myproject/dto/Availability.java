
package com.btireland.talos.mygroup.myproject.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "targetLaunchPhase",
    "certaintyLevel",
    "preOrderFlag",
    "installationType",
    "wayLeaveRequired",
    "connectionStandard",
    "connectionComplexity",
    "dropType",
    "readyForServiceDate"
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Availability  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement
	@JsonProperty("targetLaunchPhase")	
	private String targetLaunchPhase;
	
	@XmlElement
    @JsonProperty("certaintyLevel")	
    private String certaintyLevel;
	
	@XmlElement
    @JsonProperty("preOrderFlag")
    private Boolean preOrderFlag;
	
	@XmlElement
    @JsonProperty("installationType")
    private String installationType;
	
	@XmlElement
    @JsonProperty("wayLeaveRequired")
    private String wayLeaveRequired;
	
	@XmlElement
    @JsonProperty("connectionStandard")
    private String connectionStandard;
	
	@XmlElement
    @JsonProperty("connectionComplexity")
    private String connectionComplexity;
	
	@XmlElement
    @JsonProperty("dropType")	
    private String dropType;
	
	@XmlElement
    @JsonProperty("readyForServiceDate")
    private String readyForServiceDate;
	
	@XmlElement
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	
    @JsonProperty("targetLaunchPhase")
    public String getTargetLaunchPhase() {
        return targetLaunchPhase;
    }

    @JsonProperty("targetLaunchPhase")
    public void setTargetLaunchPhase(String targetLaunchPhase) {
        this.targetLaunchPhase = targetLaunchPhase;
    }

    @JsonProperty("certaintyLevel")
    public String getCertaintyLevel() {
        return certaintyLevel;
    }

    @JsonProperty("certaintyLevel")
    public void setCertaintyLevel(String certaintyLevel) {
        this.certaintyLevel = certaintyLevel;
    }

    @JsonProperty("preOrderFlag")
    public Boolean getPreOrderFlag() {
        return preOrderFlag;
    }

    @JsonProperty("preOrderFlag")
    public void setPreOrderFlag(Boolean preOrderFlag) {
        this.preOrderFlag = preOrderFlag;
    }

    @JsonProperty("installationType")
    public String getInstallationType() {
        return installationType;
    }

    @JsonProperty("installationType")
    public void setInstallationType(String installationType) {
        this.installationType = installationType;
    }

    @JsonProperty("wayLeaveRequired")
    public String getWayLeaveRequired() {
        return wayLeaveRequired;
    }

    @JsonProperty("wayLeaveRequired")
    public void setWayLeaveRequired(String wayLeaveRequired) {
        this.wayLeaveRequired = wayLeaveRequired;
    }

    @JsonProperty("connectionStandard")
    public String getConnectionStandard() {
        return connectionStandard;
    }

    @JsonProperty("connectionStandard")
    public void setConnectionStandard(String connectionStandard) {
        this.connectionStandard = connectionStandard;
    }

    @JsonProperty("connectionComplexity")
    public String getConnectionComplexity() {
        return connectionComplexity;
    }

    @JsonProperty("connectionComplexity")
    public void setConnectionComplexity(String connectionComplexity) {
        this.connectionComplexity = connectionComplexity;
    }

    @JsonProperty("dropType")
    public String getDropType() {
        return dropType;
    }

    @JsonProperty("dropType")
    public void setDropType(String dropType) {
        this.dropType = dropType;
    }

    @JsonProperty("readyForServiceDate")
    public String getReadyForServiceDate() {
        return readyForServiceDate;
    }

    @JsonProperty("readyForServiceDate")
    public void setReadyForServiceDate(String readyForServiceDate) {
        this.readyForServiceDate = readyForServiceDate;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
