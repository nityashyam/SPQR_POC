
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
    "products",
    "addOns"
})

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EligibleProduct implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlElement
	@JsonProperty("products")
    private String products;
	
	@XmlElement
    @JsonProperty("addOns")
    private List<String> addOns = null;
	
	@XmlElement
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("products")
    public String getProducts() {
        return products;
    }

    @JsonProperty("products")
    public void setProducts(String products) {
        this.products = products;
    }

    @JsonProperty("addOns")
    public List<String> getAddOns() {
        return addOns;
    }

    @JsonProperty("addOns")
    public void setAddOns(List<String> addOns) {
        this.addOns = addOns;
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
