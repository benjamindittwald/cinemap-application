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
import de.dittwald.cinemap.repository.movie.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest(includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, classes = {MovieSceneRepository.class, MovieRepository.class}))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MovieSceneServiceTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2-alpine")
            .withInitScript("schema.sql");

    @Autowired
    private MovieSceneRepository movieSceneRepository;

    @Autowired
    private MovieRepository movieRepository;

    private List<Movie> movies;
    private List<MovieScene> moviesScenes;

    @BeforeEach
    void setUp() {
        movies = new ArrayList<>();

        Movie wolve = new Movie(Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"), "https://www" +
                ".imdb" +
                ".com/title/tt0099348/?ref_=ext_shr_lnk");
        Movie nobody = new Movie(Map.of("deu", "Mein Name is Nobody", "eng", "My Name Is Nobody"), "https://www.imdb" +
                ".com/title/tt0070215/?ref_=ext_shr_lnk");

        this.moviesScenes = new ArrayList<>();
        this.moviesScenes.add(new MovieScene(13404954L, 52520008L, Map.of("deu", "Der mit dem Wolf tanzt Szene 1", "eng",
                "Dances with Wolves scene 1"), wolve));
        this. moviesScenes.add(new MovieScene(13404954L, 52520008L, Map.of("deu", "Der mit dem Wolf tanzt Szene 2", "eng",
                "Dances with Wolves scene 2"), wolve));

        wolve.addMovieScene(moviesScenes.getFirst());
        wolve.addMovieScene(moviesScenes.getLast());
        this.movies.add(wolve);
        this.movies.add(nobody);

        this.movieRepository.saveAll(movies);
    }

    @Test
    public void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    public void shouldFindTwoMovieScenes() {
        List<MovieScene> movieScenes = this.movieSceneRepository.findAll();
        assertThat(movieScenes.size()).isEqualTo(2);
        assertThat(movieScenes.getFirst().getMovie().getTitle().get("deu")).isEqualTo("Der mit dem Wolf tanzt");
        assertThat(movieScenes.getFirst().getDescription().get("deu")).isEqualTo("Der mit dem Wolf tanzt Szene 1");
    }
}