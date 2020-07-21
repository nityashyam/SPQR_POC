
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
    "pendingInstallation",
    "previousInstallation"
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderInformation  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlElement
	@JsonProperty("pendingInstallation")
    private Boolean pendingInstallation;
    
	@XmlElement
	@JsonProperty("previousInstallation")
    private Boolean previousInstallation;
    
	@XmlElement
	@JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("pendingInstallation")
    public Boolean getPendingInstallation() {
        return pendingInstallation;
    }

    @JsonProperty("pendingInstallation")
    public void setPendingInstallation(Boolean pendingInstallation) {
        this.pendingInstallation = pendingInstallation;
    }

    @JsonProperty("previousInstallation")
    public Boolean getPreviousInstallation() {
        return previousInstallation;
    }

    @JsonProperty("previousInstallation")
    public void setPreviousInstallation(Boolean previousInstallation) {
        this.previousInstallation = previousInstallation;
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
