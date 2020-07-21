
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
    "id",
    "buildingAddress",
    "interventionArea",
    "location",
    "survey",
    "multiplePremises",
    "addresses"
})

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BuildingDetails  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement
	@JsonProperty("id")
    private long id;
	
	@XmlElement
    @JsonProperty("buildingAddress")
    private String buildingAddress;
	
	@XmlElement
    @JsonProperty("interventionArea")
    private Boolean interventionArea;
	
	@XmlElement
    @JsonProperty("location")
    private Location location;
	
	@XmlElement
    @JsonProperty("survey")
    private Survey survey;
	
	@XmlElement
    @JsonProperty("multiplePremises")
    private Boolean multiplePremises;
	
	@XmlElement
    @JsonProperty("addresses")
    private List<Address> addresses = null;
	
	@XmlElement
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(long id) {
        this.id = id;
    }

    @JsonProperty("buildingAddress")
    public String getBuildingAddress() {
        return buildingAddress;
    }

    @JsonProperty("buildingAddress")
    public void setBuildingAddress(String buildingAddress) {
        this.buildingAddress = buildingAddress;
    }

    @JsonProperty("interventionArea")
    public Boolean getInterventionArea() {
        return interventionArea;
    }

    @JsonProperty("interventionArea")
    public void setInterventionArea(Boolean interventionArea) {
        this.interventionArea = interventionArea;
    }

    @JsonProperty("location")
    public Location getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(Location location) {
        this.location = location;
    }

    @JsonProperty("survey")
    public Survey getSurvey() {
        return survey;
    }

    @JsonProperty("survey")
    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    @JsonProperty("multiplePremises")
    public Boolean getMultiplePremises() {
        return multiplePremises;
    }

    @JsonProperty("multiplePremises")
    public void setMultiplePremises(Boolean multiplePremises) {
        this.multiplePremises = multiplePremises;
    }

    @JsonProperty("addresses")
    public List<Address> getAddresses() {
        return addresses;
    }

    @JsonProperty("addresses")
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
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
