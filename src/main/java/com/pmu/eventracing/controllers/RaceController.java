package com.pmu.eventracing.controllers;

import com.pmu.eventracing.dtos.RaceDTO;
import com.pmu.eventracing.entities.Race;
import com.pmu.eventracing.services.IRaceService;
import io.swagger.annotations.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Race controller handle all HTTP operations related to races
 */
@RestController
@Api(tags = "Race Management")
@RequestMapping("/api/v1/races")
@Validated
public class RaceController {

    private IRaceService raceService;

    public RaceController(IRaceService raceService) {
        this.raceService = raceService;
    }

    /**
     * This function create a race based on the given data received from the user
     *
     * @param raceDTO - The race's data
     * @return - Response entity with different possible status
     * @throws Exception - If there is a problem while the creation of the race
     */
    @ApiOperation(value = "Create a new race", notes = "Create a new race with provided details")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Race created successfully"),
            @ApiResponse(code = 400, message = "Invalid request body")
    })
    @PostMapping
    public ResponseEntity<RaceDTO> createRace(@ApiParam(value = "Race details", required = true) @RequestBody @Valid RaceDTO raceDTO) throws Exception {
        // Create the race with the given infos
        Race createdRace = raceService.createRace(raceDTO.toRaceEntity());

        return ResponseEntity.status(HttpStatus.CREATED).body(RaceDTO.fromRaceEntity(createdRace));
    }

    /**
     * This function handle the exposition of all the stored races in the database
     *
     * @return - Response entity with all the races
     */
    @ApiOperation(value = "Get all races", notes = "Get a list of all races")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of races"),
            @ApiResponse(code = 404, message = "No races found")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<RaceDTO>>> getAllRaces(Pageable pageable) {
        Page<Race> racesPage = raceService.getAllRaces(pageable);

        if (!racesPage.hasContent()) {
            return ResponseEntity.notFound().build();
        }

        // Add links for each RaceDTO in the current page
        List<EntityModel<RaceDTO>> raceEntities = racesPage.getContent().stream()
                .map(race -> {
                    RaceDTO raceDTO = RaceDTO.fromRaceEntity(race);
                    return EntityModel.of(raceDTO,
                            WebMvcLinkBuilder.linkTo(RaceController.class).slash(race.getId()).withSelfRel());
                })
                .collect(Collectors.toList());

        // Create pagination metadata
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                racesPage.getSize(),
                racesPage.getNumber(),
                racesPage.getTotalElements(),
                racesPage.getTotalPages());

        // Add links for pagination
        PagedModel<EntityModel<RaceDTO>> raceCollection = PagedModel.of(raceEntities, metadata);
        raceCollection.add(WebMvcLinkBuilder.linkTo(RaceController.class).withSelfRel());
        raceCollection.add(WebMvcLinkBuilder.linkTo(RaceController.class).withRel("allRaces"));

        return ResponseEntity.ok().body(raceCollection);
    }

    /**
     * This function handle the search of a race with a given id
     *
     * @param id - The race id
     * @return - Response entity with the found data
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Get a single race by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved race"),
            @ApiResponse(code = 404, message = "Race not found")
    })
    public ResponseEntity<EntityModel<RaceDTO>> getRaceById(@PathVariable Long id) {
        // Search the race with the given id
        Race race = raceService.getRaceById(id);

        // If there is no race, a 404 is returned
        if (race == null) {
            return ResponseEntity.notFound().build();
        }

        // Construct the result with the HATEOAS standard
        RaceDTO raceDTO = RaceDTO.fromRaceEntity(race);
        EntityModel<RaceDTO> raceEntity = EntityModel.of(raceDTO,
                WebMvcLinkBuilder.linkTo(RaceController.class).slash(id).withSelfRel());

        return ResponseEntity.ok().body(raceEntity);
    }
}
