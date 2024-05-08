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

package de.dittwald.cinemap.repository.movie;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@WebMvcTest(MovieDtoMapper.class)
class MovieDtoMapperTest {

    private final List<Movie> movies = new ArrayList<>();
    private final List<MovieDto> movieDtos = new ArrayList<>();

    @Autowired
    private MovieDtoMapper movieDtoMapper;

//    @BeforeEach
//    void setUp() {
//
//        movies.add(new Movie(UUID.randomUUID(), Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"),
//                1051896, 1970,  Map.of("deu", "Der mit dem Wolf tanzt TAGLINE", "eng", "Dances with Wolves TAGLINE"),
//                Map.of("deu", "Der mit dem Wolf tanzt OVERVIEW", "eng", "Dances with Wolves OVERVIEW"),
//                Map.of(80, "western", 85, "Thriller"), "https://image.tmdb.org/t/p/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg", "imdbId"));
//
//        movies.add(new Movie(UUID.randomUUID(), Map.of("deu", "Mein Name ist Nobody", "eng", "My Name is Nobody"),
//                1051896, 1970,  Map.of("deu", "Mein Name ist Nobody TAGLINE", "eng", "DMy Name is Nobody TAGLINE"),
//                Map.of("deu", "Mein Name ist Nobody OVERVIEW", "eng", "My Name is Nobody OVERVIEW"),
//                Map.of(80, "western", 85, "Thriller"), "https://image.tmdb.org/t/p/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg", "imdbId"));
//
//        movieDtos.add(
//                new MovieDto(UUID.randomUUID(), Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"),
//                        1051896, 1970,  Map.of("deu", "Der mit dem Wolf tanzt TAGLINE", "eng", "Dances with Wolves TAGLINE"),
//                        Map.of("deu", "Der mit dem Wolf tanzt OVERVIEW", "eng", "Dances with Wolves OVERVIEW"),
//                        Map.of(80, "western", 85, "Thriller"), "https://image.tmdb.org/t/p/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg", "imdbId"));
//        movieDtos.add(new MovieDto(UUID.randomUUID(), Map.of("deu", "Mein Name ist Nobody", "eng", "My Name is Nobody"),
//                1051896, 1970,  Map.of("deu", "Mein Name ist Nobody TAGLINE", "eng", "DMy Name is Nobody TAGLINE"),
//                Map.of("deu", "Mein Name ist Nobody OVERVIEW", "eng", "My Name is Nobody OVERVIEW"),
//                Map.of(80, "western", 85, "Thriller"), "https://image.tmdb.org/t/p/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg", "imdbId"));
//    }
//
//    @Test
//    void shouldConvertMovieToMovieDto() {
//        assertThat(this.movieDtoMapper.movieToMovieDto(movies.getFirst()).tmdbId()).isEqualTo(
//                movies.getFirst().getTmdbId());
//        assertThat(this.movieDtoMapper.movieToMovieDto(movies.getLast()).tmdbId()).isEqualTo(
//                movies.getLast().getTmdbId());
//        assertThat(this.movieDtoMapper.movieToMovieDto(movies.getFirst()).title().get(0)).isEqualTo(
//                movies.getFirst().getTitle().get(0));
//    }
//
//    @Test
//    void shouldConvertDtoMovieToMovie() {
//        assertThat(this.movieDtoMapper.movieDtoToMovie(movieDtos.getFirst()).getTmdbId()).isEqualTo(
//                movieDtos.getFirst().tmdbId());
//        assertThat(this.movieDtoMapper.movieDtoToMovie(movieDtos.getLast()).getTmdbId()).isEqualTo(
//                movieDtos.getLast().tmdbId());
//        assertThat(this.movieDtoMapper.movieDtoToMovie(movieDtos.getFirst()).getTitle().get(0)).isEqualTo(
//                movieDtos.getFirst().title().get(0));
//    }
}