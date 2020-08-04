package com.btireland.talos.prequaladapter.rest;

import com.btireland.talos.prequaladapter.service.PrequalService;
import com.btireland.talos.prequaladapter.soap.SPQRRESPONSE;
import com.btireland.talos.prequaladapter.soap.SPQRRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The controller deals with HTTP related like what kind of content to accept, return (json, xml ...),
 * HTTP codes, HTTP mapping ...
 * All business logic is delegated to the service layer to not mix everything.
 */
@Slf4j
@RestController
@Tag(name = "Prequal Adapter Controller", description = "Controller for fetching spqr response from COFE Prequal")
@RequestMapping("/api/v1/prequaladapter")
public class PrequalAdapterController {

    @Autowired
    PrequalService prequalService;


    @Operation(summary = "spqr details from prequal", description = "find spqr details from Prequal",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content())
            })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SPQRRESPONSE> spqrDetails(@RequestBody SPQRRequest request) {
        log.debug("spqr request {} received", request);
       SPQRRESPONSE spqrResponse = prequalService.getSPQRfromPrequal(request);
        return new ResponseEntity<SPQRRESPONSE>(spqrResponse,HttpStatus.OK);

    }

    @Operation(summary = "spqr details from prequal", description = "find spqr details from Prequal",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content())
            })
    @PostMapping(value="/spqr",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SPQRRESPONSE> fetchSPQRDetails(@RequestBody SPQRRequest request) throws Exception {
        log.debug("spqr request {} received", request);
        SPQRRESPONSE spqrResponse = prequalService.getSPQRDetailsfromPrequal(request);
        return new ResponseEntity<SPQRRESPONSE>(spqrResponse,HttpStatus.OK);

    }




}
