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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(MovieSceneDtoMapper.class)
class MovieSceneDtoMapperTest {
    @Autowired
    private MovieSceneDtoMapper movieSceneDtoMapper;

    @BeforeEach
    void setUp() {

    }

    @Test
    public void shouldMapMovieSceneToMovieSceneDto() {
        Movie wolve = new Movie(Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"), "https://www" +
                ".imdb" +
                ".com/title/tt0099348/?ref_=ext_shr_lnk");
        MovieScene movieSceneFromWolve = new MovieScene( 13404954L, 52520008L, Map.of("deu", "Der mit dem Wolf tanzt Szene 1",
                "eng",
                "Dances with Wolves scene 1"), wolve);

        MovieSceneDto movieSceneDto = this.movieSceneDtoMapper.movieSceneToMovieSceneDto(movieSceneFromWolve);

        assertEquals(movieSceneDto.id(), movieSceneFromWolve.getId());
        assertEquals(movieSceneDto.description(), movieSceneFromWolve.getDescription());
        assertEquals(movieSceneDto.lat(), movieSceneFromWolve.getLat());
        assertEquals(movieSceneDto.lon(), movieSceneFromWolve.getLon());
        assertEquals(movieSceneDto.movie().imdbWebsiteUrl(), movieSceneFromWolve.getMovie().getImdbWebsiteUrl());
    }

    @Test
    public void shouldMapMovieSceneDtoToMovieScene() {
        MovieDto wolveDto = new MovieDto(null,Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"), "https://www" +
                ".imdb" +
                ".com/title/tt0099348/?ref_=ext_shr_lnk");
        MovieSceneDto movieSceneDtoFromWolve = new MovieSceneDto(null, 13404954L, 52520008L, Map.of("deu", "Der mit dem Wolf tanzt Szene 1",
                "eng",
                "Dances with Wolves scene 1"), wolveDto);

        MovieScene movieScene = this.movieSceneDtoMapper.movieSceneDtoToMovieScene(movieSceneDtoFromWolve);

        assertEquals(movieScene.getId(), movieSceneDtoFromWolve.id());
        assertEquals(movieScene.getDescription(), movieSceneDtoFromWolve.description());
        assertEquals(movieScene.getLat(), movieSceneDtoFromWolve.lat());
        assertEquals(movieScene.getLon(), movieSceneDtoFromWolve.lon());
        assertEquals(movieScene.getMovie().getImdbWebsiteUrl(), movieSceneDtoFromWolve.movie().imdbWebsiteUrl());
    }
}