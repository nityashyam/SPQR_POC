
package com.btireland.talos.mygroup.myproject.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
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
    "orderId",
    "buildingDetails",
    "eligibilityDetails"
})

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NBIResponse implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement
	@JsonProperty("orderId")
    private String orderId;
	
	@XmlElement
	@JsonProperty("buildingDetails")
    private BuildingDetails buildingDetails;
    
	@XmlElement
	@JsonProperty("eligibilityDetails")
    private List<EligibilityDetail> eligibilityDetails = null;
    
	@XmlElement
	@JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("orderId")
    public String getOrderId() {
        return orderId;
    }

    @JsonProperty("orderId")
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @JsonProperty("buildingDetails")
    public BuildingDetails getBuildingDetails() {
        return buildingDetails;
    }

    @JsonProperty("buildingDetails")
    public void setBuildingDetails(BuildingDetails buildingDetails) {
        this.buildingDetails = buildingDetails;
    }

    @JsonProperty("eligibilityDetails")
    public List<EligibilityDetail> getEligibilityDetails() {
        return eligibilityDetails;
    }

    @JsonProperty("eligibilityDetails")
    public void setEligibilityDetails(List<EligibilityDetail> eligibilityDetails) {
        this.eligibilityDetails = eligibilityDetails;
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
