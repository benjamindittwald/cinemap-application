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

package de.dittwald.cinemap.repositoryui.scenes;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.dittwald.cinemap.repositoryui.repository.RepositoryClient;
import jakarta.validation.Valid;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/movies")
public class ScenesController {

    private final RepositoryClient repositoryClient;

    public ScenesController(RepositoryClient repositoryClient) {
        this.repositoryClient = repositoryClient;
    }

    @GetMapping("{movieUuid}/scenes")
    public String showScenesListPage(@PathVariable("movieUuid") UUID movieUuid, Model model) {

        Scene scene = new Scene();
        scene.setLocale(LocaleContextHolder.getLocale().getLanguage());
        scene.setUuid(UUID.randomUUID());

        model.addAttribute("newScene", scene);
        model.addAttribute("movie", this.repositoryClient.getMovie(movieUuid));
        model.addAttribute("scenes", this.repositoryClient.getSceneForMovie(movieUuid));

        return "scenes";
    }

    @GetMapping("{movieUuid}/scenes/{sceneUuid}")
    public String showSceneEditPage(@PathVariable("movieUuid") UUID movieUuid, @PathVariable("sceneUuid") UUID sceneUuid,
                                    @Valid @ModelAttribute Scene scene,  Model model) {

        model.addAttribute("movie", this.repositoryClient.getMovie(movieUuid));
        model.addAttribute("scene", scene);

        return "scenes_edit";
    }

    @PostMapping("{movieUuid}/scenes")
    public String createScene(@PathVariable("movieUuid") UUID movieUuid, @Valid @ModelAttribute Scene scene)
            throws JsonProcessingException {

        this.repositoryClient.createScene(scene, movieUuid);

        return "redirect:/movies/%s/scenes".formatted(movieUuid);
    }

    @PostMapping("{movieUuid}/scenes/{sceneUuid}")
    public String deleteScene(@PathVariable("movieUuid") UUID movieUuid, @PathVariable("sceneUuid") UUID sceneUuid,
                              Model model) {
        this.repositoryClient.deleteScene(sceneUuid, movieUuid);
        return "redirect:/movies/%s/scenes".formatted(movieUuid);
    }
}
