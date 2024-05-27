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
import org.springframework.validation.BindingResult;
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

        model.addAttribute("movie", this.repositoryClient.getMovie(movieUuid));
        model.addAttribute("scenes", this.repositoryClient.getScenesForMovie(movieUuid));

        return "scenes";
    }

    @GetMapping("{movieUuid}/scenes/{sceneUuid}")
    public String showSceneEditPage(@PathVariable("movieUuid") UUID movieUuid,
                                    @PathVariable("sceneUuid") UUID sceneUuid, Model model) {

        model.addAttribute("movie", this.repositoryClient.getMovie(movieUuid));
        model.addAttribute("scene", this.repositoryClient.getScene(sceneUuid, movieUuid));

        return "scene_form";
    }

    @PostMapping("{movieUuid}/scenes/add")
    public String addScene(@PathVariable("movieUuid") UUID movieUuid, @Valid @ModelAttribute Scene scene, BindingResult result, Model model)
            throws JsonProcessingException {

        if (result.hasErrors()) {
            scene = new Scene();
            scene.setUuid(UUID.randomUUID());
            scene.setLocale(LocaleContextHolder.getLocale().getLanguage());
            model.addAttribute("operation", "add");
            model.addAttribute("movie", this.repositoryClient.getMovie(movieUuid));
            return "scene_form";
        }

        this.repositoryClient.createScene(scene, movieUuid);

        return "redirect:/movies/%s/scenes".formatted(movieUuid);
    }

    @PostMapping("{movieUuid}/scenes/edit")
    public String editScene(@PathVariable("movieUuid") UUID movieUuid, @Valid @ModelAttribute Scene scene,
                            Model model) {


        this.repositoryClient.updateScene(movieUuid, scene);

        return "redirect:/movies/%s/scenes".formatted(movieUuid);
    }

    @GetMapping("{movieUuid}/scenes/form")
    public String showCreateSceneForm(@PathVariable("movieUuid") UUID movieUuid,
                                      @RequestParam(name = "sceneUuid", required = false) UUID sceneUuid, Model model) {

        Scene scene;
        if (sceneUuid == null) {
            scene = new Scene();
            scene.setUuid(UUID.randomUUID());
            scene.setLocale(LocaleContextHolder.getLocale().getLanguage());
            model.addAttribute("operation", "add");
        } else {
            scene = this.repositoryClient.getScene(sceneUuid, movieUuid);
            model.addAttribute("operation", "edit");
        }

        model.addAttribute("scene", scene);
        model.addAttribute("movie", this.repositoryClient.getMovie(movieUuid));

        return "scene_form";
    }

//    @PostMapping("{movieUuid}/scenes")
//    public String createScene(@PathVariable("movieUuid") @Valid UUID movieUuid, @Valid @ModelAttribute Scene scene,
//                              BindingResult bindingResult, Model model) throws JsonProcessingException {
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("newScene", scene);
//            model.addAttribute("movie", this.repositoryClient.getMovie(movieUuid));
//            model.addAttribute("scenes", this.repositoryClient.getScenesForMovie(movieUuid));
//            return "scenes";
//        }
//
//        this.repositoryClient.createScene(scene, movieUuid);
//
//        return "redirect:/movies/%s/scenes".formatted(movieUuid);
//    }

    @PostMapping("{movieUuid}/scenes/{sceneUuid}")
    public String deleteScene(@PathVariable("movieUuid") UUID movieUuid, @PathVariable("sceneUuid") UUID sceneUuid,
                              Model model) {
        this.repositoryClient.deleteScene(sceneUuid, movieUuid);
        return "redirect:/movies/%s/scenes".formatted(movieUuid);
    }
}
