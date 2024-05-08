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

import de.dittwald.cinemap.repository.moviescene.MovieSceneRepository;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {MovieRepository.class, MovieSceneRepository.class}))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MovieRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16.2-alpine").withInitScript("schema.sql");

    @Autowired
    private MovieRepository movieRepository;

    List<Movie> movies;

    @BeforeEach
    public void setUp() {
        Map<String, String> mutableWolfTitle = new HashMap<>();
        mutableWolfTitle.put("deu", "Der mit dem Wolf tanzt");
        mutableWolfTitle.put("eng", "Dances with Wolves");
        movies = new ArrayList<>();
        movies.add(new Movie(UUID.randomUUID(), mutableWolfTitle, 1051896, 1970,
                Map.of("deu", "Der mit dem Wolf tanzt TAGLINE", "eng", "Dances with Wolves TAGLINE"),
                Map.of("deu", "Der mit dem Wolf tanzt OVERVIEW", "eng", "Dances with Wolves OVERVIEW"),
                Map.of(80, "western", 85, "Thriller"), "https://image.tmdb.org/t/p/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg",
                "imdbId"));
        movies.add(
                new Movie(UUID.randomUUID(), Map.of("deu", "Mein Name ist Nobody", "eng", "My Name is Nobody"), 1051896,
                        1970, Map.of("deu", "Mein Name ist Nobody TAGLINE", "eng", "DMy Name is Nobody TAGLINE"),
                        Map.of("deu", "Mein Name ist Nobody OVERVIEW", "eng", "My Name is Nobody OVERVIEW"),
                        Map.of(80, "western", 85, "Thriller"),
                        "https://image.tmdb.org/t/p/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg", "imdbId"));
        this.movieRepository.saveAll(movies);
    }

    @Test
    public void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    public void shouldFindTwoMovies() {
        assertEquals(this.movieRepository.findAll().size(), 2);
        assertEquals(movies.getFirst().getTitle().get("eng"), "Dances with Wolves");
        assertEquals(movies.getFirst().getTitle().get("deu"), "Der mit dem Wolf tanzt");
        assertEquals(movies.getLast().getTitle().get("deu"), "Mein Name ist Nobody");
        assertEquals(movies.getLast().getTitle().get("eng"), "My Name is Nobody");
    }

    @Test
    public void shouldFindMovieById() {
        List<Movie> movies = this.movieRepository.findAll();
        assertThat(this.movieRepository.findById(movies.getFirst().getId()).get().getId()).isEqualTo(
                movies.getFirst().getId());
    }

    @Test
    public void shouldPersistMovie() {
        Movie movie = new Movie(UUID.randomUUID(),
                Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity is Still My Name"), 1051896, 1970,
                Map.of("deu", "Der Kleine und der müde Joe TAGLINE", "eng", "Trinity is Still My Name TAGLINE"),
                Map.of("deu", "Der Kleine und der müde Joe OVERVIEW", "eng", "Trinity is Still My Name OVERVIEW"),
                Map.of(80, "western", 85, "Thriller"), "https://image.tmdb.org/t/p/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg",
                "imdbId");
        this.movieRepository.save(movie);
        List<Movie> movies = this.movieRepository.findAll();
        assertThat(movies.size()).isEqualTo(3);
        assertThat(movies.getLast().getTitle().get("deu")).isEqualTo(movie.getTitle().get("deu"));
    }

    @Test
    public void shouldDeleteMovie() {
        assertThat(this.movieRepository.count()).isEqualTo(2);
        this.movieRepository.delete(this.movies.getFirst());
        assertThat(this.movieRepository.count()).isEqualTo(1);
        assertThat(this.movieRepository.findAll().getFirst().getTitle().get("deu")).isEqualTo("Mein Name ist Nobody");
    }

    // Fixme: java.lang.UnsupportedOperationException
//    @Test
//    public void shouldUpdateMovie() {
//        List<Movie> movies = this.movieRepository.findAll();
//        Movie movie = this.movieRepository.findByUuid(movies.getFirst().getUuid()).get();
//        Map<String, String> newTitle = new HashMap<>(movie.getTitle());
//        newTitle.put("fra", "Danse avec les loups");
//        movie.setTitle(newTitle);
//        this.movieRepository.save(movie);
//        assertThat(this.movieRepository.findById(this.movies.getFirst().getId()).get().getTitle().get("fra")).isEqualTo(
//                "Danse avec les loups");
//    }

    @Test
    public void shouldFindMoviebyUuid() {
        Movie movie = this.movieRepository.findAll().getFirst();
        assertThat(this.movieRepository.findByUuid(movie.getUuid()).get()).isEqualTo(movie);
    }

    @Test
    public void shouldDeleteByUuid() {
        Movie movie = this.movieRepository.findAll().getFirst();
        assertThat(this.movieRepository.count()).isEqualTo(2);
        this.movieRepository.deleteByUuid(movie.getUuid());
        assertThat(this.movieRepository.count()).isEqualTo(1);
    }

    @Test
    public void shouldFailDeleteByUuidDueToNotFound() {
        this.movieRepository.deleteByUuid(UUID.randomUUID());
    }

    @Test
    public void shouldExist() {
        assertThat(this.movieRepository.existsByUuid(this.movieRepository.findAll().getFirst().getUuid())).isEqualTo(
                true);
    }

    @Test
    public void shouldNotExist() {
        assertThat(this.movieRepository.existsByUuid(UUID.randomUUID())).isEqualTo(false);
    }
}