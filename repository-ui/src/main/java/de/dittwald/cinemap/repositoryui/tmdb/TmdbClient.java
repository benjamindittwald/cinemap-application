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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dittwald.cinemap.repositoryui.movies.Movie;
import de.dittwald.cinemap.repositoryui.properties.ConfigConstants;
import de.dittwald.cinemap.repositoryui.properties.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class TmdbClient {

    private final Properties properties;

    private final WebClientConfig webClientConfig;

    public TmdbClient(Properties properties, WebClientConfig webClientConfig) {
        this.properties = properties;
        this.webClientConfig = webClientConfig;
    }

    public Movie getMovieTmdb(int id) {
        ObjectMapper objectMapper = new ObjectMapper();
        Movie movie = new Movie();

        try {
            JsonNode node = objectMapper.readTree(this.webClientConfig.tmdbWebClient().get()
                    .uri(id + "?language=en-Us")
                    .headers(h -> h.setBearerAuth(this.properties.getTmdbApiReadToken()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block());

            movie.setPoster(((node.has("poster_path") && node.get("poster_path") == null) ?
                    ConfigConstants.TMDB_IMAGE_BASE_URL + "/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg" :
                    ConfigConstants.TMDB_IMAGE_BASE_URL + "/w300" + node.get("poster_path").asText()));


            Map<String, String> titleMap = new HashMap<>();
            titleMap.put("eng", node.has("title") ? node.get("title").asText() : "N/A");
            movie.setTitle(titleMap);

            Map<String, String> overviewMap = new HashMap<>();
            overviewMap.put("eng", node.has("overview") ? node.get("overview").asText() : "N/A");
            movie.setOverview(overviewMap);

            Map<String, String> taglineMap = new HashMap<>();
            taglineMap.put("eng", node.has("tagline") ? node.get("tagline").asText() : "N/A");
            movie.setTagline(taglineMap);

            movie.setImdbId(node.has("imdb_id") ? node.get("imdb_id").asText() : null);
            movie.setReleaseYear(node.has("release_date") ?
                    LocalDate.parse(node.get("release_date").asText()).getYear() : 42);
            movie.setTmdbId(id);

            // Todo: Genres, i18n

            return movie;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
