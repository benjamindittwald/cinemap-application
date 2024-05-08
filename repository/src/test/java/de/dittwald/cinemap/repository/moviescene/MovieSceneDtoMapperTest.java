/*
 * Copyright 2024 Benjamin Dittwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dittwald.cinemap.repository.moviescene;

import de.dittwald.cinemap.repository.movie.Movie;
import de.dittwald.cinemap.repository.movie.MovieDto;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@WebMvcTest(MovieSceneDtoMapper.class)
class MovieSceneDtoMapperTest {

    @Autowired
    private MovieSceneDtoMapper movieSceneDtoMapper;

    @Test
    public void shouldMapMovieSceneToMovieSceneDto() {
        Movie wolf = new Movie(UUID.randomUUID(), Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"),
                1051896, 1970,  Map.of("deu", "Der mit dem Wolf tanzt TAGLINE", "eng", "Dances with Wolves TAGLINE"),
                Map.of("deu", "Der mit dem Wolf tanzt OVERVIEW", "eng", "Dances with Wolves OVERVIEW"),
                Map.of(80, "western", 85, "Thriller"), "https://image.tmdb.org/t/p/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg", "imdbId");
        MovieScene movieSceneFromWolf = new MovieScene(UUID.randomUUID(), 13404954L, 52520008L,
                Map.of("deu", "Der mit dem Wolf tanzt Szene 1", "eng", "Dances with Wolves scene 1"), wolf);

        MovieSceneDto movieSceneDto = this.movieSceneDtoMapper.movieSceneToMovieSceneDto(movieSceneFromWolf);

        assertEquals(movieSceneDto.uuid(), movieSceneFromWolf.getUuid());
        assertEquals(movieSceneDto.description(), movieSceneFromWolf.getDescription());
        assertEquals(movieSceneDto.lat(), movieSceneFromWolf.getLat());
        assertEquals(movieSceneDto.lon(), movieSceneFromWolf.getLon());
        assertEquals(movieSceneDto.movie().tmdbId(), movieSceneFromWolf.getMovie().getTmdbId());
    }

    @Test
    public void shouldMapMovieSceneDtoToMovieScene() {
        MovieDto wolfDto = new MovieDto(UUID.randomUUID(), Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"),
                1051896, 1970,  Map.of("deu", "Der mit dem Wolf tanzt TAGLINE", "eng", "Dances with Wolves TAGLINE"),
                Map.of("deu", "Der mit dem Wolf tanzt OVERVIEW", "eng", "Dances with Wolves OVERVIEW"),
                Map.of(80, "western", 85, "Thriller"), "https://image.tmdb.org/t/p/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg", "imdbId");
        MovieSceneDto movieSceneDtoFromWolf = new MovieSceneDto(UUID.randomUUID(), 13404954L, 52520008L,
                Map.of("deu", "Der mit dem Wolf tanzt Szene 1", "eng", "Dances with Wolves scene 1"), wolfDto);

        MovieScene movieScene = this.movieSceneDtoMapper.movieSceneDtoToMovieScene(movieSceneDtoFromWolf);

        assertEquals(movieScene.getUuid(), movieSceneDtoFromWolf.uuid());
        assertEquals(movieScene.getDescription(), movieSceneDtoFromWolf.description());
        assertEquals(movieScene.getLat(), movieSceneDtoFromWolf.lat());
        assertEquals(movieScene.getLon(), movieSceneDtoFromWolf.lon());
        assertEquals(movieScene.getMovie().getTmdbId(), movieSceneDtoFromWolf.movie().tmdbId());
    }

    @Test
    public void shouldMapMovieSceneOnlyDtoToMovieSceene() {
        MovieSceneOnlyDto movieSceneOnly = new MovieSceneOnlyDto(UUID.randomUUID(), 13404954L, 52520008L,
                Map.of("deu", "Der mit dem Wolf tanzt Szene 1", "eng", "Dances with Wolves scene 1"));

        MovieScene movieScene = this.movieSceneDtoMapper.movieSceneOnlyDtoToMovieScene(movieSceneOnly);

        assertEquals(movieScene.getUuid(), movieSceneOnly.uuid());
        assertEquals(movieScene.getDescription(), movieSceneOnly.description());
        assertEquals(movieScene.getLat(), movieSceneOnly.lat());
        assertEquals(movieScene.getLon(), movieSceneOnly.lon());
        assertNull(movieScene.getMovie());
    }
}