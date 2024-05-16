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

import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.scene.dto.SceneFlatDto;
import de.dittwald.cinemap.repository.scene.service.SceneService;
import de.dittwald.cinemap.repository.util.ConstantStrings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/scenes")
@Tag(name = "Scene")
public class SceneRestController {

    private final SceneService sceneService;

    public SceneRestController(SceneService sceneService) {
        this.sceneService = sceneService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all scenes",
            description = "Responds a list with all scenes in the given localization or the corresponding fallback " +
                    "localization.")
    @ApiResponse(responseCode = "200", description = "Found movie scenes")
    public List<SceneFlatDto> findAll(@RequestParam(name = ConstantStrings.LOCALE_API_REQUEST_PARAM,
            defaultValue = ConstantStrings.DEFAULT_LOCALE) String locale) throws LocaleNotFoundException {
        return this.sceneService.findAll(locale);
    }

//    @GetMapping(value = "{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "Get a scene",
//            description = "Gets a scene and its corresponding movie by its UUID in the given localization or the " +
//                    "corresponding fallback localization.")
//    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the movie scene"),
//            @ApiResponse(responseCode = "404", description = "Movie scene not found"),
//            @ApiResponse(responseCode = "400", description = "Given UUID is not a valid UUID")})
//    public SceneFlatDto findByUuid(@PathVariable("uuid") @Valid UUID uuid,
//                                   @RequestParam(name = ConstantStrings.LOCALE_API_REQUEST_PARAM,
//                                           defaultValue = ConstantStrings.DEFAULT_LOCALE) String locale)
//            throws NotFoundException, LocaleNotFoundException {
//        return this.sceneService.findByUuid(uuid, locale);
//    }


//    @DeleteMapping(value = "{uuid}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @Operation(summary = "Delete scene", description = "Deletes a scene with the given uuid.")
//    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Scene was deleted"),
//            @ApiResponse(responseCode = "404", description = "Scene not found"),
//            @ApiResponse(responseCode = "400", description = "Given UUID is not a valid UUID")})
//    public void scene(@PathVariable("uuid") @Valid UUID uuid) throws NotFoundException {
//        this.sceneService.deleteByUuid(uuid);
//    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete all scenes", description = "Deletes all scenes.")
    @ApiResponses(value = @ApiResponse(responseCode = "204", description = "All scene were deleted"))
    public void deleteAll() {
        this.sceneService.deleteAll();
    }
}
