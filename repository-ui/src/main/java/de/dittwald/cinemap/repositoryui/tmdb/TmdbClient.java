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
import de.dittwald.cinemap.repositoryui.movies.LocalizedMovie;
import de.dittwald.cinemap.repositoryui.movies.Movie;
import de.dittwald.cinemap.repositoryui.properties.ConfigConstants;
import de.dittwald.cinemap.repositoryui.properties.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class TmdbClient {

    private final Properties properties;

    private final WebClientConfig webClientConfig;

    public TmdbClient(Properties properties, WebClientConfig webClientConfig) {
        this.properties = properties;
        this.webClientConfig = webClientConfig;
    }

    // Todo: API call error handling and make reactive
    public Movie getMovieDetails(int id) throws URISyntaxException, MalformedURLException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Movie movie = new Movie();

        JsonNode movieNode = objectMapper.readTree(this.webClientConfig.tmdbWebClient()
                .get()
                .uri(id + "?language=en-Us")
                .headers(h -> h.setBearerAuth(this.properties.getTmdbApiReadToken()))
                .retrieve()
                .bodyToMono(String.class)
                .block());

        JsonNode translationsNode = objectMapper.readTree(this.webClientConfig.tmdbWebClient()
                .get()
                .uri(id + "/translations")
                .headers(h -> h.setBearerAuth(this.properties.getTmdbApiReadToken()))
                .retrieve()
                .bodyToMono(String.class)
                .block());

        JsonNode imagesNode = objectMapper.readTree(this.webClientConfig.tmdbWebClient()
                .get()
                .uri(id + "/images")
                .headers(h -> h.setBearerAuth(this.properties.getTmdbApiReadToken()))
                .retrieve()
                .bodyToMono(String.class)
                .block());

        movie.setImdbId(movieNode.has("imdb_id") ? movieNode.get("imdb_id").asText() : null);
        movie.setReleaseYear(
                movieNode.has("release_date") ? LocalDate.parse(movieNode.get("release_date").asText()).getYear() :
                        null);
        movie.setTmdbId(id);
        movie.setUuid(UUID.randomUUID());

        if (movieNode.has("genres") && !movieNode.get("genres").isNull()) {
            Map<Integer, String> genres = new HashMap<>();
            for (JsonNode genre : movieNode.get("genres")) {
                genres.put(genre.get("id").asInt(), genre.get("name").asText());
            }
            movie.setGenres(genres);
        }

        for (JsonNode translation : translationsNode.get("translations")) {
            JsonNode dataNode = translation.get("data");
            LocalizedMovie localizedMovie = new LocalizedMovie();
            localizedMovie.setTitle(dataNode.has("title") ? dataNode.get("title").asText() : null);
            localizedMovie.setOverview(dataNode.has("overview") ? dataNode.get("overview").asText() : null);
            localizedMovie.setTagline(dataNode.has("tagline") ? dataNode.get("tagline").asText() : null);

            for (JsonNode imageNode : imagesNode.get("posters")) {
                if (!imageNode.get("iso_639_1").isNull() && StringUtils.equals(imageNode.get("iso_639_1").asText(),
                        translation.get("iso_639_1").asText())) {
                    localizedMovie.setPosterUrl(imageNode.has("file_path") && (imageNode.get("file_path") != null) ?
                            new URI(String.format("%s/w300%s", ConfigConstants.TMDB_IMAGE_BASE_URL,
                                    imageNode.get("file_path").asText())).toURL() :
                            new URI(String.format("%s/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg",
                                    ConfigConstants.TMDB_IMAGE_BASE_URL)).toURL());
                }
            }
            localizedMovie.setLocale(translation.has("iso_639_1") ? translation.get("iso_639_1").asText() : null);
            movie.getLocalizedMovies().put(localizedMovie.getLocale(), localizedMovie);
        }

        return movie;
    }
}
