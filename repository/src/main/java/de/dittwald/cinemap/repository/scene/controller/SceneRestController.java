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

package de.dittwald.cinemap.repository.scene.controller;

import de.dittwald.cinemap.repository.scene.service.SceneService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/scenes")
@Tag(name = "Scene API")
public class SceneRestController {

    private final SceneService sceneService;

    public SceneRestController(SceneService sceneService) {
        this.sceneService = sceneService;
    }

//    @GetMapping(value = "{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "Get a movie scene",
//            description = "Gets a movie and its corresponding movie by its UUID. Responds with status code 404 if " +
//                    "the movie scene was not found.")
//    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the movie scene"),
//            @ApiResponse(responseCode = "404", description = "Movie scene not found"),
//            @ApiResponse(responseCode = "400", description = "Given UUID is not a valid UUID")})
//    public MovieSceneDto findByUuid(@PathVariable("uuid") @Valid @UuidConstraint String uuid) throws NotFoundException {
//        return this.movieSceneService.findByUuid(UUID.fromString(uuid));
//    }
//
//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "Get all movie scenes", description = "Responds a list with all movies scenes")
//    @ApiResponse(responseCode = "200", description = "Found movie scenes")
//    public List<MovieSceneDto> findAll() {
//        return this.movieSceneService.findAll();
//    }
//
//    @DeleteMapping(value = "{uuid}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @Operation(summary = "Delete movie scene",
//            description =
//                    "Deletes a movie scene with the given uuid.")
//    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Movie scene was deleted"),
//            @ApiResponse(responseCode = "404", description = "Movie scene not found"),
//            @ApiResponse(responseCode = "400", description = "Given UUID is not a valid UUID")})
//    public void scene(@PathVariable("uuid") @Valid @UuidConstraint String uuid) throws NotFoundException {
//        this.movieSceneService.deleteByUuid(UUID.fromString(uuid));
//    }
//
//    @DeleteMapping
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @Operation(summary = "Delete all movie scenes",
//            description =
//                    "Deletes all movie scenes.")
//    @ApiResponses(value = @ApiResponse(responseCode = "204", description = "All movie scene were deleted"))
//    public void deleteAll() {
//        this.movieSceneService.deleteAll();
//    }
}
