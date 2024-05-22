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

import com.fasterxml.jackson.core.JsonProcessingException;
import de.dittwald.cinemap.repositoryui.movies.Movie;
import de.dittwald.cinemap.repositoryui.repository.RepositoryClient;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
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

        // Todo: Error handling
        Movie movie = null;
        try {
            movie = this.tmdbClient.getMovieDetails(tmdbId.getId());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        movie.setUuid(UUID.randomUUID());
        try {
            this.repositoryClient.createMovieByTmdbId(movie);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/movies";
    }
}
