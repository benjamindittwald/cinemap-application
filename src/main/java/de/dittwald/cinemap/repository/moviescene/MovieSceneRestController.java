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

package de.dittwald.cinemap.repository.moviescene;

import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.exceptions.UuidInUseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/moviescenes")
public class MovieSceneRestController {

    private final MovieSceneService movieSceneService;

    public MovieSceneRestController(MovieSceneService movieSceneService) {
        this.movieSceneService = movieSceneService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new movie scene",
            description = "Creates a new movie scene with the given parameters.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Movie scene was created"),
            @ApiResponse(responseCode = "400", description = "Invalid movie scene given")})
    public MovieSceneDto create(@RequestBody @Valid MovieSceneDto movieSceneDto)
            throws NotFoundException, UuidInUseException {
        return this.movieSceneService.save(movieSceneDto);
    }

    // Update

    // Delete

    // FindByUUID

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all movie scenes", description = "Responses a list with all movies scenes")
    @ApiResponse(responseCode = "200", description = "Found movies")
    public List<MovieSceneDto> findAll() {
        return this.movieSceneService.findAll();
    }

}
