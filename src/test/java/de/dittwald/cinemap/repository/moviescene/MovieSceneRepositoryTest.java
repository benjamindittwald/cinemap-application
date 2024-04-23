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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest(includeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, classes = {MovieSceneRepository.class, MovieRepository.class}))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MovieSceneRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2-alpine")
            .withInitScript("schema.sql");

    @Autowired
    private MovieSceneRepository movieSceneRepository;

    @Autowired
    private MovieRepository movieRepository;


    @BeforeEach
    void setUp() {
        List<MovieScene> moviesScenes;

        Movie wolve = new Movie(Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"), "https://www" +
                ".imdb" +
                ".com/title/tt0099348/?ref_=ext_shr_lnk");
        Movie nobody = new Movie(Map.of("deu", "Mein Name is Nobody", "eng", "My Name Is Nobody"), "https://www.imdb" +
                ".com/title/tt0070215/?ref_=ext_shr_lnk");

        moviesScenes = new ArrayList<>();
        moviesScenes.add(new MovieScene(13404954L, 52520008L, Map.of("deu", "Der mit dem Wolf tanzt Szene 1",
                "eng",
                "Dances with Wolves scene 1"), wolve));
        moviesScenes.add(new MovieScene(13404954L, 52520008L, Map.of("deu", "Der mit dem Wolf tanzt Szene 2",
                "eng",
                "Dances with Wolves scene 2"), wolve));

        this.movieRepository.save(wolve);
        this.movieRepository.save(nobody);
        this.movieSceneRepository.saveAll(moviesScenes);
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

    @Test
    public void shouldFindMovieSceneById() {
        List<MovieScene> movieScenes = this.movieSceneRepository.findAll();
        assertThat(this.movieSceneRepository.findById(movieScenes.getFirst().getId()).get().getId()).isEqualTo(movieScenes.getFirst().getId());
    }

    @Test
    public void shouldPersistMovieScene() {
        Movie movie = this.movieRepository.findAll().getFirst();
        MovieScene movieScene = new MovieScene(13434954L, 52534008L, Map.of("deu", "Der mit dem Wolf tanzt Szene 3",
                "eng",
                "Dances with Wolves scene 3"), movie);

        assertThat(this.movieSceneRepository.count()).isEqualTo(2L);
        this.movieSceneRepository.save(movieScene);
        assertThat(this.movieSceneRepository.count()).isEqualTo(3L);
    }

    @Test
    public void shouldUpdateMovieScene() {
        MovieScene movieScene = this.movieSceneRepository.findAll().getFirst();
        assertThat(movieScene.getDescription().get("eng")).isEqualTo("Dances with Wolves scene 1");
        assertThat(this.movieSceneRepository.count()).isEqualTo(2L);
        movieScene.setDescription(new HashMap<>(Map.of("eng", "A new description")));
        assertThat(this.movieSceneRepository.count()).isEqualTo(2L);
        this.movieSceneRepository.save(movieScene);
        assertThat(this.movieSceneRepository.findAll().getLast().getDescription().get("eng")).isEqualTo("A new " +
                "description");
    }

    @Test
    public void shouldDeleteMovieScene() {
        assertThat(this.movieSceneRepository.count()).isEqualTo(2L);
        this.movieSceneRepository.deleteById(this.movieSceneRepository.findAll().getFirst().getId());
        assertThat(this.movieSceneRepository.count()).isEqualTo(1L);
    }
}