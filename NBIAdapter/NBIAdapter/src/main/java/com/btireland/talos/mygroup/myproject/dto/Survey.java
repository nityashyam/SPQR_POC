
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
    "surveyRequired"
})

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Survey  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlElement
	@JsonProperty("surveyRequired")
    private Boolean surveyRequired;
    
	@XmlElement
	@JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("surveyRequired")
    public Boolean getSurveyRequired() {
        return surveyRequired;
    }

    @JsonProperty("surveyRequired")
    public void setSurveyRequired(Boolean surveyRequired) {
        this.surveyRequired = surveyRequired;
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
