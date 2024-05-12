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
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dittwald.cinemap.repositoryui.properties.Properties;
import de.dittwald.cinemap.repositoryui.tmdb.WebClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RepositoryClient {

    final Properties properties;

    private final int timeout = 3000;

    private final WebClientConfig webClientConfig;

    public RepositoryClient(Properties properties, WebClientConfig webClientConfig) {
        this.properties = properties;
        this.webClientConfig = webClientConfig;
    }

    public List<Movie> getAllMovies() throws JsonProcessingException {


        ObjectMapper objectMapper = new ObjectMapper();
        List<Movie> movies = new ArrayList<>();

        try {
            objectMapper.readTree(this.webClientConfig.repositoryWebClient()
                    .get()
                    .uri("/api/v1/movies")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block()).forEach(node -> {

                try {
                    movies.add(objectMapper.treeToValue(node, Movie.class));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return movies;
    }

    public void createMovieByTmdbId(Movie movie) {

        this.webClientConfig.repositoryWebClient()
                .post()
                .uri("/api/v1/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(movie), Movie.class)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

}
