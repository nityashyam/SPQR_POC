package com.btireland.talos.mygroup.myproject.rest;

import com.btireland.talos.mygroup.myproject.facade.ExampleOrderFacade;
import com.btireland.talos.mygroup.myproject.service.ExampleOrderWithoutJPAService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Configure the beans for the REST services tests.
 * If we use @MockBean in every test REST service class, it will create a new spring context each time (costly).
 * We’d better reuse the same spring context for every REST service test class. The solution is to put all the beans we want
 * to mock here. And to annotate the test class with
 * <ul>
 *     <li>@WebMvcTest</li>
 *     <li>@Import(TestRestConfiguration.class)</li>
 * </ul>
 */
@TestConfiguration
public class TestRestConfiguration {

    @Bean
    public ExampleOrderFacade exampleOrderFacade(){
        return Mockito.mock(ExampleOrderFacade.class);
    }
    @Bean
    public ExampleOrderWithoutJPAService exampleOrderWithoutJPAService(){
        return Mockito.mock(ExampleOrderWithoutJPAService.class);
    }
}
