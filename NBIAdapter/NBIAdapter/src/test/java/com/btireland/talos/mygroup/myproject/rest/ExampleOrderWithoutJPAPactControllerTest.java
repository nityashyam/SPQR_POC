package com.btireland.talos.mygroup.myproject.rest;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.IgnoreNoPactsToVerify;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import com.btireland.talos.mygroup.myproject.service.ExampleOrderWithoutJPAService;
import com.btireland.talos.mygroup.myproject.tag.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Check that pacts provided by consumers are dealt with correctly
 */
@WebMvcTest
@ActiveProfiles("test")
@Import(TestRestConfiguration.class)
@IntegrationTest
@Provider("NBIAdapter-provider")
@PactBroker()
@IgnoreNoPactsToVerify
class ExampleOrderWithoutJPAPactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExampleOrderWithoutJPAService exampleOrderService;

    @BeforeEach
    public void setupMocks() {

    }
    @BeforeEach
    void setupMockMvc(PactVerificationContext context) {
        if (context != null){
            context.setTarget(new MockMvcTestTarget(mockMvc));
        }
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void testTemplate(PactVerificationContext context) {
        if (context != null){
            context.verifyInteraction();
        }
    }

}