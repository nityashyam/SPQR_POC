package com.btireland.talos.mygroup.myproject.rest;

import com.btireland.talos.mygroup.myproject.dto.ExampleOrderDTO;
import com.btireland.talos.mygroup.myproject.service.ExampleOrderWithoutJPAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * The controller deals with HTTP related like what kind of content to accept, return (json, xml ...),
 * HTTP codes, HTTP mapping ...
 * All business logic is delegated to the service layer to not mix everything.
 */
@Slf4j
@RestController
@Tag(name= "Example Order Controller without JPA", description = "Controller for example objects that does not interact with a database")
@RequestMapping("/api/v1/exampleorderswithoutjpa")
public class ExampleOrderWithoutJPAController {

    private ExampleOrderWithoutJPAService exampleOrderWithoutJPAService;

    public ExampleOrderWithoutJPAController(ExampleOrderWithoutJPAService exampleOrderWithoutJPAService) {
        this.exampleOrderWithoutJPAService = exampleOrderWithoutJPAService;
    }

    @Operation(summary = "transfer an order", description = "transfer an order to another service. Check that the body is valid")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<ExampleOrderDTO> transferOrder(@RequestBody @Valid ExampleOrderDTO order) {
        ExampleOrderDTO orderDTO =  exampleOrderWithoutJPAService.transferOrder(order);
        // Use the entity model from Spring HATEOAS to add link in the generated JSON
        EntityModel<ExampleOrderDTO> hateoasOrderDTO = new EntityModel<>(orderDTO);
        hateoasOrderDTO.add(linkTo(ExampleOrderWithoutJPAController.class).slash(orderDTO.getId()).withSelfRel());
        return hateoasOrderDTO;
    }

    @Operation(summary = "find an order by id", description = "find an order by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content())
            })
    @GetMapping(value = "/{id}")
    public EntityModel<ExampleOrderDTO> findById(@PathVariable("id") Long id) {
        log.debug("a test message with id {} received", id);
        ExampleOrderDTO orderDTO = exampleOrderWithoutJPAService.findById(id);

        // Use the entity model from Spring HATEOAS to add link in the generated JSON
        EntityModel<ExampleOrderDTO> hateoasOrderDTO = new EntityModel<>(orderDTO);
        hateoasOrderDTO.add(linkTo(ExampleOrderWithoutJPAController.class).slash(orderDTO.getId()).withSelfRel());
        return hateoasOrderDTO;
    }

    @Operation(summary = "notify a change on an order", description = "notify a change on an order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content())
            })
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<ExampleOrderDTO> notifyChange(@PathVariable("id") Long id, @RequestBody @Valid ExampleOrderDTO order) {
        ExampleOrderDTO orderDTO = exampleOrderWithoutJPAService.notifyChange(id, order);

        // Use the entity model from Spring HATEOAS to add link in the generated JSON
        return new EntityModel<>(orderDTO).add(linkTo(ExampleOrderWithoutJPAController.class).slash(orderDTO.getId()).withSelfRel());
    }

}
