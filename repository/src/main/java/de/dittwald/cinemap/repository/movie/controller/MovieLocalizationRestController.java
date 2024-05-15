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

import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.movie.service.MovieLocalizationService;
import de.dittwald.cinemap.repository.movie.dto.MovieLocalizationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movies")
@Tag(name = "Movie Localizations")
@Validated
public class MovieLocalizationRestController {

    private final MovieLocalizationService movieLocalizationService;

    public MovieLocalizationRestController(MovieLocalizationService movieLocalizationService) {
        this.movieLocalizationService = movieLocalizationService;
    }

    @GetMapping(value = "{uuid}/localizations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all localizations for given movie",
            description = "Responds with all available localizations for the given movie")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found localizations"),
            @ApiResponse(responseCode = "404", description = "No localizations found")})
    public MovieLocalizationDto getMovieLocalizations(@PathVariable("uuid") UUID uuid) throws NotFoundException {
        return this.movieLocalizationService.getMovieLocalizations(uuid);
    }

    @PutMapping(value = "{uuid}/localizations",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update movie localizations",
            description = "Updates the movie localizations with the given movie uuid.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Movie localizations were updated"),
            @ApiResponse(responseCode = "404", description = "Movie not found."),
            @ApiResponse(responseCode = "400", description = "Invalid movie localizations or UUID given.")})
    public void updateMovie(@Valid @RequestBody MovieLocalizationDto movieLocalizationDto,
                            @PathVariable("uuid") UUID uuid, @RequestParam(defaultValue = "false") String override)
            throws NotFoundException {
        this.movieLocalizationService.update(movieLocalizationDto, uuid, Boolean.parseBoolean(override));
    }

}
