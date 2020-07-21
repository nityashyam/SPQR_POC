package com.btireland.talos.mygroup.myproject.rest;

import com.btireland.talos.mygroup.myproject.dto.ExampleOrderDTO;
import com.btireland.talos.mygroup.myproject.facade.ExampleOrderFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Slf4j
@RestController
@Tag(name= "Example Order Controller", description = "CRUD Controller for example objects")
@RequestMapping("/api/v1/exampleorders")
public class ExampleOrderController {

    private ExampleOrderFacade exampleOrderFacade;
    private PagedResourcesAssembler<ExampleOrderDTO> pagedResourcesAssembler;

    public ExampleOrderController(ExampleOrderFacade exampleOrderFacade, PagedResourcesAssembler<ExampleOrderDTO> pagedResourcesAssembler) {
        this.exampleOrderFacade = exampleOrderFacade;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Operation(summary = "create an order", description = "create an order. Check that the body is valid")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ExampleOrderDTO create(@RequestBody @Valid ExampleOrderDTO order) {
        return exampleOrderFacade.create(order);
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
        ExampleOrderDTO orderDTO = exampleOrderFacade.findById(id);

        // Use the entity model from Spring HATEOAS to add link in the generated JSON
        EntityModel<ExampleOrderDTO> hateoasOrderDTO = new EntityModel<>(orderDTO);
        hateoasOrderDTO.add(linkTo(ExampleOrderController.class).slash(orderDTO.getId()).withSelfRel());
        return hateoasOrderDTO;
    }

    @Operation(summary = "update an order", description = "update an order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content())
            })
    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<ExampleOrderDTO> update(@PathVariable("id") Long id, @RequestBody @Valid ExampleOrderDTO order) {
        ExampleOrderDTO orderDTO = exampleOrderFacade.update(id, order);
        return new EntityModel<>(orderDTO);
    }

    @Operation(summary = "delete an order", description = "delete an order",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found", content = @Content())
            })
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        exampleOrderFacade.delete(id);
    }

    /**
     * If you want all the orders with the default page size, call /api/v1/exampleorders?filters.
     * If you want to filter the orders returned, use the RSQL syntax, for example :
     * <ul>
     *     <li>/api/v1/exampleorders?filters=extRef=='ref*'</li>
     * </ul>
     *
     * @param filters  RSQL filter
     * @param pageable Pageable is a Spring data object. It will be fetch from the request parameter page, size and sort
     * @return return a list of orders with the page information and links to first/prev/next/last page
     */
    @Operation(summary = "find orders", description = "find orders by specifying RSQL criteria, paginate the result",
            parameters = {
                    @Parameter(name = "filters", description = "filters respecting a RSQL format",
                            example = "filters=externalReference==ref*;type=in=(PFIB,CFIB)"),
                    @Parameter(name = "pageable", hidden = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            })
    @PageableAsQueryParam()
    @GetMapping()
    public PagedModel<EntityModel<ExampleOrderDTO>> findAll(@RequestParam String filters, Pageable pageable) {
        Page<ExampleOrderDTO> orderDTOs = exampleOrderFacade.findAll(filters, pageable);
        return pagedResourcesAssembler.toModel(orderDTOs);
    }
}
