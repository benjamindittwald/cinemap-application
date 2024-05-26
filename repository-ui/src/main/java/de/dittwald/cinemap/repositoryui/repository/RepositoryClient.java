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

package de.dittwald.cinemap.repositoryui.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dittwald.cinemap.repositoryui.movies.*;
import de.dittwald.cinemap.repositoryui.scenes.Scene;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RepositoryClient {

    private final WebClientConfig webClientConfig;

    public RepositoryClient(WebClientConfig webClientConfig) {
        this.webClientConfig = webClientConfig;
    }

    // Todo: make reactive
    public List<MovieFlat> getAllMovies() {

        ObjectMapper objectMapper = new ObjectMapper();
        List<MovieFlat> movies = new ArrayList<>();

        try {
            objectMapper.readTree(this.webClientConfig.repositoryWebClient()
                    .get()
                    .uri("/api/v1/movies?lang=" + LocaleContextHolder.getLocale().getLanguage())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block()).forEach(node -> {

                try {
                    movies.add(objectMapper.treeToValue(node, MovieFlat.class));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return movies;
    }

    // Todo: make reactive
    public MovieFlat getMovie(UUID movieUuid) {

        return this.webClientConfig.repositoryWebClient()
                .get()
                .uri("/api/v1/movies/%s?lang=%s".formatted(movieUuid, LocaleContextHolder.getLocale().getLanguage()))
                .retrieve()
                .bodyToMono(MovieFlat.class)
                .block();
    }

    // Todo: make reactive
    //    public void createMovieByTmdbId(Movie movie) throws JsonProcessingException {
    //
    //        MovieFlat flatMovie =
    //                new MovieFlat(movie.getUuid(), movie.getTmdbId(), movie.getReleaseYear(), movie.getGenres(),
    //                        movie.getImdbId(),
    //                        movie.getLocalizedMovies().entrySet().stream().findFirst().get().getValue().getLocale(),
    //                        movie.getLocalizedMovies().entrySet().stream().findFirst().get().getValue().getTitle(),
    //                        movie.getLocalizedMovies().entrySet().stream().findFirst().get().getValue().getOverview(),
    //                        movie.getLocalizedMovies().entrySet().stream().findFirst().get().getValue().getTagline(),
    //                        movie.getLocalizedMovies().entrySet().stream().findFirst().get().getValue()
    //                        .getPosterUrl());
    //
    //
    //        this.webClientConfig.repositoryWebClient()
    //                .post()
    //                .uri("/api/v1/movies")
    //                .contentType(MediaType.APPLICATION_JSON)
    //                .accept(MediaType.APPLICATION_JSON)
    //                .body(Mono.just(flatMovie), MovieFlat.class)
    //                .retrieve()
    //                .toBodilessEntity()
    //                .block();
    //
    //        List<MovieLocalizationEntry> localizedMovies = new ArrayList<>();
    //        for (LocalizedMovie localizedMovie : movie.getLocalizedMovies().values()) {
    //            localizedMovies.add(new MovieLocalizationEntry(localizedMovie.getLocale(), localizedMovie.getTitle(),
    //                    localizedMovie.getOverview(), localizedMovie.getTagline(), localizedMovie.getPosterUrl()));
    //        }
    //        MovieLocalization movieLocalization = new MovieLocalization(movie.getUuid(), localizedMovies);
    //
    //        this.webClientConfig.repositoryWebClient()
    //                .put()
    //                .uri(String.format("/api/v1/movies/%s/localizations?override=true", movie.getUuid()))
    //                .contentType(MediaType.APPLICATION_JSON)
    //                .accept(MediaType.APPLICATION_JSON)
    //                .body(Mono.just(movieLocalization), MovieLocalization.class)
    //                .retrieve()
    //                .toBodilessEntity()
    //                .block();
    //    }

    // Todo: make reactive
    public void deleteMovie(UUID movieUuid) {
        this.webClientConfig.repositoryWebClient()
                .delete()
                .uri(String.format("/api/v1/movies/%s", movieUuid))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    // Todo: make reactive
    public void createScene(Scene scene, UUID movieUuid) throws JsonProcessingException {
        this.webClientConfig.repositoryWebClient()
                .post()
                .uri(String.format("/api/v1/movies/%s/scenes", movieUuid))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(scene), Scene.class)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    // Todo: make reactive
    public List<Scene> getScenesForMovie(UUID movieUuid) {

        ObjectMapper objectMapper = new ObjectMapper();
        List<Scene> scenes = new ArrayList<>();

        try {
            objectMapper.readTree(this.webClientConfig.repositoryWebClient()
                    .get()
                    .uri("/api/v1/movies/%s/scenes?lang=%s".formatted(movieUuid,
                            LocaleContextHolder.getLocale().getLanguage()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block()).forEach(node -> {

                try {
                    scenes.add(objectMapper.treeToValue(node, Scene.class));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            });
            return scenes;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Todo: make reactive
    public Scene getScene(UUID sceneUuid, UUID movieUuid) {

        return this.webClientConfig.repositoryWebClient()
                .get()
                .uri("/api/v1/movies/%s/scenes/%s?lang=%s".formatted(movieUuid, sceneUuid,
                        LocaleContextHolder.getLocale().getLanguage()))
                .retrieve()
                .bodyToMono(Scene.class)
                .block();
    }

    // Todo: make reactive
    public void updateScene(UUID movieUuid, Scene scene) {
        this.webClientConfig.repositoryWebClient()
                .put()
                .uri(String.format("/api/v1/movies/%s/scenes/%s", movieUuid, scene.getUuid()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(scene), Scene.class)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    // Todo: Make reactive
    public void deleteScene(UUID sceneUuid, UUID movieUuid) {
        this.webClientConfig.repositoryWebClient()
                .delete()
                .uri(String.format("/api/v1/movies/%s/scenes/%s", movieUuid, sceneUuid))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void createMovieViaTmdbId(int tmdbId) {
        this.webClientConfig.repositoryWebClient()
                .put()
                .uri(String.format("/api/v1/tmdb/%s", tmdbId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

}
