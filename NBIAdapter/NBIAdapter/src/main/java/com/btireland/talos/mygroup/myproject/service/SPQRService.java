package com.btireland.talos.mygroup.myproject.service;

import com.btireland.talos.mygroup.myproject.dto.NBIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SPQRService {

    @Autowired
    RestTemplate restTemplate;


    public NBIResponse getNBIAvailableProductsByERCode(String eirCode){
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);
        ResponseEntity<NBIResponse> response =
                restTemplate.getForEntity("http://localhost:8085/eligibility", NBIResponse.class);
        NBIResponse nbiResponse = response.getBody();
        //logger.info("response received");

        return nbiResponse;
    }


}
