
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
    "availability",
    "eligibleProducts",
    "inHomeServices",
    "CPE"
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceDetails  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement
	@JsonProperty("availability")
    private Availability availability;
	
	@XmlElement
    @JsonProperty("eligibleProducts")
    private List<EligibleProduct> eligibleProducts = null;
	
	@XmlElement
    @JsonProperty("inHomeServices")
    private List<String> inHomeServices = null;
	
	@XmlElement
    @JsonProperty("CPE")
    private List<String> cPE = null;
	
	@XmlElement
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("availability")
    public Availability getAvailability() {
        return availability;
    }

    @JsonProperty("availability")
    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    @JsonProperty("eligibleProducts")
    public List<EligibleProduct> getEligibleProducts() {
        return eligibleProducts;
    }

    @JsonProperty("eligibleProducts")
    public void setEligibleProducts(List<EligibleProduct> eligibleProducts) {
        this.eligibleProducts = eligibleProducts;
    }

    @JsonProperty("inHomeServices")
    public List<String> getInHomeServices() {
        return inHomeServices;
    }

    @JsonProperty("inHomeServices")
    public void setInHomeServices(List<String> inHomeServices) {
        this.inHomeServices = inHomeServices;
    }

    @JsonProperty("CPE")
    public List<String> getCPE() {
        return cPE;
    }

    @JsonProperty("CPE")
    public void setCPE(List<String> cPE) {
        this.cPE = cPE;
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
