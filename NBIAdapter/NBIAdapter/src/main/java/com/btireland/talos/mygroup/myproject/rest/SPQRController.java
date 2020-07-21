package com.btireland.talos.mygroup.myproject.rest;

import com.btireland.talos.mygroup.myproject.dto.NBIResponse;
import com.btireland.talos.mygroup.myproject.service.SPQRService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name= "NBI SPQR Controller", description = "Controller for NBI SPQR orders")
@RequestMapping("/api/v1/nbi/spqr")
public class SPQRController {

    @Autowired
    SPQRService spqrService;

    @Operation(summary = "Get NBI products using eirCode", description = "Get NBI products using eirCode",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content())
            })
    @GetMapping(value = "/{eirCode}")
    public ResponseEntity<NBIResponse> getNBIAvailableProductsByERCode(@PathVariable("eirCode") String eirCode) {
        NBIResponse nbiResponse = spqrService.getNBIAvailableProductsByERCode(eirCode);
        return new ResponseEntity<NBIResponse>(nbiResponse, HttpStatus.OK);
    }


}
