/*
 * Copyright 2024 Benjamin Dittwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dittwald.cinemap.repository.movie;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieRestController {

    private final MovieService movieService;

    public MovieRestController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all movies")
    @ApiResponse(responseCode = "200", description = "Found movies")
    public List<MovieDto> findAll() {
        return this.movieService.findAll();
    }

    @GetMapping(
            value = "{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a movie by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the movie"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public MovieDto findById(@PathVariable("id") Long id) throws NotFoundException {
        return this.movieService.findById(id);
    }

    @PutMapping(
            value = "{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update movie with given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movie was updated"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public MovieDto updateMovie(@Valid @RequestBody MovieDto movieDto, @PathVariable("id") Long id) throws NotFoundException {
        return this.movieService.update(movieDto);
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new movie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movie was created"),
            @ApiResponse(responseCode = "400", description = "Invalid movie given")
    })
    public MovieDto createMovie(@Valid @RequestBody MovieInputDto movieInputDto) {
        return this.movieService.save(movieInputDto);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete movie with given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movie was deleted"),
            @ApiResponse(responseCode = "404", description = "Movie not found")
    })
    public void deleteMovie(@PathVariable("id") Long id) throws NotFoundException {
        this.movieService.deleteById(id);
    }
}
