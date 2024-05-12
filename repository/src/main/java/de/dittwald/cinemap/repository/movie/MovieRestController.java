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

package de.dittwald.cinemap.repository.movie;

import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.scene.SceneService;
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
@Tag(name = "Movie API")
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
    public List<MovieDto> findAll(@RequestParam(name = "lang", defaultValue = "en") @Iso6391Constraint String locale) {
        return this.movieService.findAll(locale);
    }

    @GetMapping(value = "{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a movie", description = "Gets a movie by its UUID localized in the given language.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the movie"),
            @ApiResponse(responseCode = "404", description = "Movie not found"),
            @ApiResponse(responseCode = "400", description = "Invalid UUID or ISO 639-1 lang given")})
    public MovieDto findByUuid(@PathVariable("uuid") String uuid,
                               @RequestParam(name = "lang", defaultValue = "en") @Iso6391Constraint String locale)
            throws NotFoundException {
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
    public void updateMovie(@Valid @RequestBody MovieDto movieDto, @PathVariable("uuid") String uuid)
            throws NotFoundException {
        this.movieService.update(movieDto, UUID.fromString(uuid));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Create a new movie", description = "Creates a new movie with the given movie in the body.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Movie was created"),
            @ApiResponse(responseCode = "400", description = "Invalid movie given")})
    public void createMovie(@Valid @RequestBody MovieDto movieDto) {
        this.movieService.save(movieDto);
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
    //
    //    @PostMapping(value = "{movieUuid}/scenes",
    //            consumes = MediaType.APPLICATION_JSON_VALUE,
    //            produces = MediaType.APPLICATION_JSON_VALUE)
    //    @ResponseStatus(HttpStatus.CREATED)
    //    @Operation(summary = "Create a new movie scene",
    //            description = "Creates a new movie scene with the given parameters. The corresponding move must
    //            exist " +
    //                    "before the movie scene creation. Please note that the description property of a movie is " +
    //                    "represented by a Map<String, String>. The map key is expecting an ISO 639 language code
    //                    and the " +
    //                    "value the description in the named language.")
    //    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Movie scene was created"),
    //    @ApiResponse(
    //            responseCode = "400",
    //            description = "Invalid movie scene given or given UUID is not a valid UUID")})
    //    public MovieSceneDto createMovieScene(@RequestBody @Valid MovieSceneOnlyDto movieSceneOnlyDto,
    //                                          @PathVariable("movieUuid") @Valid @UuidConstraint String movieUuid)
    //            throws NotFoundException, UuidInUseException {
    //        return this.movieSceneService.save(movieSceneOnlyDto, UUID.fromString(movieUuid));
    //    }
    //
    //    @DeleteMapping
    //    @ResponseStatus(HttpStatus.NO_CONTENT)
    //    @Operation(summary = "Delete all movies", description = "Deletes all movies and their corresponding scenes.")
    //    @ApiResponses(value = @ApiResponse(responseCode = "204", description = "All movies and scenes were deleted"))
    //    public void deleteAllMovies() {
    //        this.movieService.deleteAll();
    //    }
    //
    //    @PutMapping(value = "{movieUuid}/scenes/{sceneUuid}",
    //            consumes = MediaType.APPLICATION_JSON_VALUE,
    //            produces = MediaType.APPLICATION_JSON_VALUE)
    //    @ResponseStatus(HttpStatus.OK)
    //    @Operation(summary = "Updates a scene",
    //            description = "Updates a scene with the given parameters. The corresponding movie and scene must
    //            exist. " +
    //                    "Please note that the description property of a movie is represented by a Map<String,
    //                    String>. " +
    //                    "The map key is expecting an ISO 639 language code and the value the description in the
    //                    named " +
    //                    "language.")
    //    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Movie scene was created"),
    //            @ApiResponse(responseCode = "400", description = "Invalid scene given or given UUID is not a valid
    //            UUID")})
    //    public MovieSceneDto updateMovieScene(@RequestBody @Valid MovieSceneOnlyDto movieSceneOnlyDto,
    //                                          @PathVariable("movieUuid") @Valid @UuidConstraint String movieUuid,
    //                                          @PathVariable("sceneUuid") @Valid @UuidConstraint String sceneUuid)
    //            throws NotFoundException {
    //        return this.movieSceneService.update(movieSceneOnlyDto, UUID.fromString(movieUuid), UUID.fromString
    //        (sceneUuid));
    //    }
    //
    //    @DeleteMapping(value = "{movieUuid}/scenes/{sceneUuid}")
    //    @ResponseStatus(HttpStatus.NO_CONTENT)
    //    @Operation(summary = "Delete scene", description = "Deletes with given UUID.")
    //    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Scene was deleted."),
    //            @ApiResponse(responseCode = "404", description = "Scene was not found.")})
    //    public void deleteMovieScene(@PathVariable("movieUuid") @Valid @UuidConstraint String movieUuid,
    //                                 @PathVariable("sceneUuid") @Valid @UuidConstraint String sceneUuid)
    //            throws NotFoundException {
    //        this.movieSceneService.deleteByUuid(UUID.fromString(sceneUuid));
    //    }
    //
    //    @GetMapping(value = "{movieUuid}/scenes/{sceneUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    //    @Operation(summary = "Get a movie",
    //            description = "Gets a movie by its UUID. Responds with status code 404 if the movie was not found.")
    //    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the movie"),
    //            @ApiResponse(responseCode = "404", description = "Movie not found")})
    //    public MovieSceneDto findMovieSceneByUuid(@PathVariable("movieUuid") @Valid @UuidConstraint String movieUuid,
    //                                              @PathVariable("sceneUuid") @Valid @UuidConstraint String sceneUuid)
    //            throws NotFoundException {
    //        return this.movieSceneService.findByUuid(UUID.fromString(sceneUuid));
    //    }
    //
    //    @GetMapping(value = "{movieUuid}/scenes", produces = MediaType.APPLICATION_JSON_VALUE)
    //    @Operation(summary = "Get all scenes of a movie", description = "Gets all scenes of a movie")
    //    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the scenes"),
    //            @ApiResponse(responseCode = "404", description = "Movie not found")})
    //    public List<MovieSceneDto> findAllMovieScenesOfMovie(
    //            @PathVariable("movieUuid") @Valid @UuidConstraint String movieUuid) throws NotFoundException {
    //        return this.movieSceneService.findAllScenesOfMovie(UUID.fromString(movieUuid));
    //    }
}
