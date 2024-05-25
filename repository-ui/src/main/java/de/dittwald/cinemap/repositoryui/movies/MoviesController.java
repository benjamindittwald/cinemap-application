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

package de.dittwald.cinemap.repositoryui.movies;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.dittwald.cinemap.repositoryui.repository.RepositoryClient;
import de.dittwald.cinemap.repositoryui.tmdb.TmdbId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
public class MoviesController {

    private final RepositoryClient repositoryClient;

    public MoviesController(RepositoryClient repositoryClient) {
        this.repositoryClient = repositoryClient;
    }

    @GetMapping("/movies")
    public String index(Model model) throws JsonProcessingException {

        List<MovieFlat> movies = new ArrayList<>(this.repositoryClient.getAllMovies());
        Collections.sort(movies);
        model.addAttribute("movies", movies);
        model.addAttribute("tmdbId", new TmdbId());

        return "index";
    }

    @PostMapping("/movies/{movieUuid}")
    public String deleteMovie(@PathVariable UUID movieUuid, Model model) {

        this.repositoryClient.deleteMovie(movieUuid);

        return "redirect:/movies";
    }
}
