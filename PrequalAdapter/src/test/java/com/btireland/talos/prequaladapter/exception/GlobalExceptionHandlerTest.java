package com.btireland.talos.prequaladapter.exception;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp(){
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("check that ResourceNotFound is translated into 204 HTTP codes for DELETE method")
    void handleResourceNotFoundExceptionForDeleteMethod() {
        ResponseEntity<String> expected = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        HttpServletRequest request = new MockHttpServletRequest("DELETE", "/api/v1/exampleorders/1");
        ResourceNotFoundException exception = new ResourceNotFoundException();

        ResponseEntity<String> actual = exceptionHandler.handleResourceNotFoundException(request, exception);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("check that ResourceNotFound is translated into 404 HTTP codes for other methods than DELETE")
    @ValueSource(strings = {"POST", "GET", "PUT"})
    void handleResourceNotFoundException(String method) {
        ResponseEntity<String> expected = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        HttpServletRequest request = new MockHttpServletRequest(method, "/api/v1/exampleorders/1");
        ResourceNotFoundException exception = new ResourceNotFoundException();

        ResponseEntity<String> actual = exceptionHandler.handleResourceNotFoundException(request, exception);
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}