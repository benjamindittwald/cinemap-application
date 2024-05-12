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

package de.dittwald.cinemap.repositoryui.tmdb;

import de.dittwald.cinemap.repositoryui.movies.Movie;
import de.dittwald.cinemap.repositoryui.movies.RepositoryClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class TmdbController {

    private final TmdbClient tmdbClient;
    private final RepositoryClient repositoryClient;

    public TmdbController(TmdbClient tmdbClient, RepositoryClient repositoryClient) {
        this.tmdbClient = tmdbClient;
        this.repositoryClient = repositoryClient;
    }

    @PostMapping("/tmdb")
    public String createMovieByTmdbId(@Valid @ModelAttribute TmdbId tmdbId) {

        Movie movie = this.tmdbClient.getMovieTmdb(tmdbId.getId());
        movie.setUuid(UUID.randomUUID());
        this.repositoryClient.createMovieByTmdbId(movie);

        return "index";
    }
}
