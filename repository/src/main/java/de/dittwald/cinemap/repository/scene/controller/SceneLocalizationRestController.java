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

import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.scene.dto.SceneLocalizationDto;
import de.dittwald.cinemap.repository.scene.service.SceneLocalizationService;
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
@RequestMapping("/api/v1/scenes")
@Tag(name = "Scene Localizations")
@Validated
public class SceneLocalizationRestController {

    private final SceneLocalizationService sceneLocalizationService;

    public SceneLocalizationRestController(SceneLocalizationService sceneLocalizationService) {
        this.sceneLocalizationService = sceneLocalizationService;
    }

    @GetMapping(value = "/{sceneUuid}/localizations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all localizations for given scene",
            description = "Responds with all available localizations for the given scene")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found localizations"),
            @ApiResponse(responseCode = "404", description = "No localizations movie or scene found")})
    public SceneLocalizationDto getSceneLocalizations(@PathVariable("sceneUuid") UUID sceneUuid)
            throws NotFoundException {
        return this.sceneLocalizationService.getSceneLocalizationDto(sceneUuid);
    }

    @PutMapping(value = "/{sceneUuid}/localizations",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update scene localizations",
            description = "Updates the scene localizations with the given scene uuid.")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Scene localizations were updated"),
            @ApiResponse(responseCode = "404", description = "Movie or scene not found."),
            @ApiResponse(responseCode = "400", description = "Invalid request body or UUIDs given")})
    public void updateSceneLocalizations(@Valid @RequestBody SceneLocalizationDto sceneLocalizationDto,
                                         @PathVariable("sceneUuid") UUID sceneUuid,
                                         @RequestParam(defaultValue = "false") String override)
            throws NotFoundException {
        this.sceneLocalizationService.update(sceneLocalizationDto, sceneUuid, Boolean.parseBoolean(override));
    }
}
