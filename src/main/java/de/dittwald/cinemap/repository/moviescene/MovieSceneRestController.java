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
import de.dittwald.cinemap.repository.validation.UuidConstraint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/scenes")
public class MovieSceneRestController {

    private final MovieSceneService movieSceneService;

    public MovieSceneRestController(MovieSceneService movieSceneService) {
        this.movieSceneService = movieSceneService;
    }

    @GetMapping(value = "{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a movie scene",
            description = "Gets a movie and its corresponding movie by its UUID. Responses with status code 404 if " +
                    "the movie scene was not found.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the movie scene"),
            @ApiResponse(responseCode = "404", description = "Movie scene not found"),
            @ApiResponse(responseCode = "400", description = "Given UUID is not a valid UUID")})
    public MovieSceneDto findByUuid(@PathVariable("uuid") @Valid @UuidConstraint String uuid) throws NotFoundException {
        return this.movieSceneService.findByUuid(UUID.fromString(uuid));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all movie scenes", description = "Responses a list with all movies scenes")
    @ApiResponse(responseCode = "200", description = "Found movie scenes")
    public List<MovieSceneDto> findAll() {
        return this.movieSceneService.findAll();
    }

    @DeleteMapping(value = "{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete movie scene",
            description =
                    "Deletes a movie scene with the given uuid.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Movie scene was deleted"),
            @ApiResponse(responseCode = "404", description = "Movie scene not found"),
            @ApiResponse(responseCode = "400", description = "Given UUID is not a valid UUID")})
    public void scene(@PathVariable("uuid") @Valid @UuidConstraint String uuid) throws NotFoundException {
        this.movieSceneService.deleteByUuid(UUID.fromString(uuid));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete all movie scenes",
            description =
                    "Deletes all movie scenes.")
    @ApiResponses(value = @ApiResponse(responseCode = "204", description = "All movie scene were deleted"))
    public void deleteAll() {
        this.movieSceneService.deleteAll();
    }
}
