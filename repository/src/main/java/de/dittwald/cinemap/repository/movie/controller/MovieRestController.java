/*
 * Copyright 2024 Benjamin Dittwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dittwald.cinemap.repository.movie.controller;

import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.exceptions.UuidInUseException;
import de.dittwald.cinemap.repository.movie.service.MovieService;
import de.dittwald.cinemap.repository.movie.dto.MovieFlatDto;
import de.dittwald.cinemap.repository.scene.dto.SceneCreationDto;
import de.dittwald.cinemap.repository.scene.dto.SceneFlatDto;
import de.dittwald.cinemap.repository.scene.service.SceneService;
import de.dittwald.cinemap.repository.util.ConstantStrings;
import de.dittwald.cinemap.repository.validation.Iso6391Constraint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movies")
@Tag(name = "Movie")
@Validated
public class MovieRestController {

    private final MovieService movieService;
    private final SceneService sceneService;

    public MovieRestController(MovieService movieService, SceneService sceneService) {
        this.movieService = movieService;
        this.sceneService = sceneService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all movies",
            description = "Responds a list with all movies localized in the given language")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found movies"),
            @ApiResponse(responseCode = "400", description = "Invalid ISO 639-1 lang given")})
    public List<MovieFlatDto> findAll(@RequestParam(name = ConstantStrings.LOCALE_API_REQUEST_PARAM,
            defaultValue = ConstantStrings.DEFAULT_LOCALE) @Iso6391Constraint String locale)
            throws LocaleNotFoundException {
        return this.movieService.findAll(locale);
    }

    @GetMapping(value = "{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a movie", description = "Gets a movie by its UUID localized in the given language.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the movie"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "400", description = "Invalid UUID or ISO 639-1 lang given")})
    public MovieFlatDto findByUuid(@PathVariable("uuid") String uuid,
                                   @RequestParam(name = ConstantStrings.LOCALE_API_REQUEST_PARAM,
                                           defaultValue = ConstantStrings.DEFAULT_LOCALE) @Iso6391Constraint
                                   String locale) throws NotFoundException, LocaleNotFoundException {
        return this.movieService.findByUuid(UUID.fromString(uuid), locale);
    }

    @PutMapping(value = "{uuid}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update movie", description = "Updates the movie with the given uuid.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Movie was updated"),
            @ApiResponse(responseCode = "404", description = "Movie not found."),
            @ApiResponse(responseCode = "400", description = "Invalid movie or UUID given.")})
    public void updateMovie(@Valid @RequestBody MovieFlatDto movieFlatDto, @PathVariable("uuid") String uuid)
            throws NotFoundException {
        this.movieService.update(movieFlatDto, UUID.fromString(uuid));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Create a new movie", description = "Creates a new movie with the given movie in the body.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Movie was created"),
            @ApiResponse(responseCode = "400", description = "Invalid movie given"),
            @ApiResponse(responseCode = "409", description = "Given UUID is already in use")})
    public void createMovie(@Valid @RequestBody MovieFlatDto movieFlatDto) throws UuidInUseException {
        this.movieService.save(movieFlatDto);
    }

    @DeleteMapping(value = "{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete movie", description = "Deletes a movie with the given uuid.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Movie was deleted"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "400", description = "Given UUID is not a valid UUID")})
    public void deleteMovie(@PathVariable("uuid") String uuid) throws NotFoundException {
        this.movieService.deleteByUuid(UUID.fromString(uuid));
    }


    @PostMapping(value = "{movieUuid}/scenes",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Create a scene",
            description = "Creates a scene with the given parameters. The corresponding movie must exist " +
                    "before the scene gets created.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Scene was created"), @ApiResponse(
            responseCode = "400",
            description = "Invalid movie scene given or given UUID is not a valid UUID"),
            @ApiResponse(responseCode = "409", description = "Given UUID is already in use")})
    public void createScene(@RequestBody @Valid SceneCreationDto sceneCreationDto,
                            @PathVariable("movieUuid") @Valid UUID movieUuid)
            throws NotFoundException, UuidInUseException {
        this.sceneService.save(sceneCreationDto, movieUuid);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete all movies", description = "Deletes all movies and their corresponding scenes.")
    @ApiResponses(value = @ApiResponse(responseCode = "204", description = "All movies and scenes were deleted"))
    public void deleteAllMovies() {
        this.movieService.deleteAll();
    }


    @PutMapping(value = "{movieUuid}/scenes/{sceneUuid}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Updates a scene",
            description = "Updates a scene with the given parameters. The corresponding movie and scene must exist.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Movie scene was created"),
            @ApiResponse(responseCode = "400", description = "Invalid scene given or given UUID is not a valid UUID")})
    public void updateScene(@RequestBody @Valid SceneCreationDto sceneCreationDto,
                            @PathVariable("movieUuid") @Valid UUID movieUuid,
                            @PathVariable("sceneUuid") @Valid UUID sceneUuid) throws NotFoundException {
        this.sceneService.update(sceneCreationDto, movieUuid, sceneUuid);
    }

    @DeleteMapping(value = "{movieUuid}/scenes/{sceneUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete scene", description = "Deletes with given UUID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Scene was deleted."),
            @ApiResponse(responseCode = "404", description = "Scene was not found.")})
    public void deleteMovieScene(@PathVariable("movieUuid") @Valid UUID movieUuid,
                                 @PathVariable("sceneUuid") @Valid UUID sceneUuid) throws NotFoundException {
        this.sceneService.deleteByUuid(sceneUuid);
    }

    @GetMapping(value = "{movieUuid}/scenes/{sceneUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a scene", description = "Gets a scene by its UUID.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the scene"),
            @ApiResponse(responseCode = "404", description = "scene not found")})
    public SceneFlatDto findSceneByUuid(@PathVariable("movieUuid") @Valid UUID movieUuid,
                                        @PathVariable("sceneUuid") @Valid UUID sceneUuid,
                                        @RequestParam(name = ConstantStrings.LOCALE_API_REQUEST_PARAM,
                                                defaultValue = ConstantStrings.DEFAULT_LOCALE) @Iso6391Constraint
                                        @Valid String locale) throws NotFoundException, LocaleNotFoundException {
        return this.sceneService.findByUuid(sceneUuid, locale);
    }

    @GetMapping(value = "{movieUuid}/scenes", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all scenes of a movie", description = "Gets all scenes of a movie")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the scenes"),
            @ApiResponse(responseCode = "404", description = "Movie not found")})
    public List<SceneFlatDto> findAllMovieScenesOfMovie(
            @PathVariable("movieUuid") @Valid UUID movieUuid, @RequestParam(name = ConstantStrings.LOCALE_API_REQUEST_PARAM,
            defaultValue = ConstantStrings.DEFAULT_LOCALE) @Valid @Iso6391Constraint String locale )
            throws NotFoundException, LocaleNotFoundException {
        return this.sceneService.findAllScenesOfMovie(movieUuid, locale);
    }
}
