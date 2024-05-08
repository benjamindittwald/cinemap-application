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
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {MovieSceneRepository.class, MovieRepository.class}))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MovieSceneRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16.2-alpine").withInitScript("schema.sql");

    @Autowired
    private MovieSceneRepository movieSceneRepository;

    @Autowired
    private MovieRepository movieRepository;


    @BeforeEach
    void setUp() {
        List<MovieScene> moviesScenes;

        Movie wolf = new Movie(UUID.randomUUID(), Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"),
                1051896, 1970,  Map.of("deu", "Der mit dem Wolf tanzt TAGLINE", "eng", "Dances with Wolves TAGLINE"),
                Map.of("deu", "Der mit dem Wolf tanzt OVERVIEW", "eng", "Dances with Wolves OVERVIEW"),
                Map.of(80, "western", 85, "Thriller"), "https://image.tmdb.org/t/p/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg", "imdbId");
        Movie nobody = new Movie(UUID.randomUUID(), Map.of("deu", "Mein Name ist Nobody", "eng", "My Name is Nobody"),
                1051896, 1970,  Map.of("deu", "Mein Name ist Nobody TAGLINE", "eng", "DMy Name is Nobody TAGLINE"),
                Map.of("deu", "Mein Name ist Nobody OVERVIEW", "eng", "My Name is Nobody OVERVIEW"),
                Map.of(80, "western", 85, "Thriller"), "https://image.tmdb.org/t/p/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg", "imdbId");

        moviesScenes = new ArrayList<>();
        moviesScenes.add(new MovieScene(UUID.randomUUID(), 13404954L, 52520008L,
                Map.of("deu", "Der mit dem Wolf " + "tanzt Szene 1", "eng", "Dances with Wolves scene 1"), wolf));
        moviesScenes.add(new MovieScene(UUID.randomUUID(), 13404954L, 52520008L,
                Map.of("deu", "Der mit dem Wolf " + "tanzt Szene 2", "eng", "Dances with Wolves scene 2"), wolf));
        moviesScenes.add(new MovieScene(UUID.randomUUID(), 13404954L, 52520008L,
                Map.of("deu", "Der mit dem Wolf " + "tanzt Szene 3", "eng", "Dances with Wolves scene 3"), nobody));

        this.movieRepository.save(wolf);
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
        assertThat(movieScenes.size()).isEqualTo(3);
        assertThat(movieScenes.getFirst().getMovie().getTitle().get("deu")).isEqualTo("Der mit dem Wolf tanzt");
        assertThat(movieScenes.getFirst().getDescription().get("deu")).isEqualTo("Der mit dem Wolf tanzt Szene 1");
    }

    @Test
    public void shouldFindMovieSceneById() {
        List<MovieScene> movieScenes = this.movieSceneRepository.findAll();
        assertThat(this.movieSceneRepository.findById(movieScenes.getFirst().getId()).get().getId()).isEqualTo(
                movieScenes.getFirst().getId());
    }

    @Test
    public void shouldPersistMovieScene() {
        Movie movie = this.movieRepository.findAll().getFirst();
        MovieScene movieScene = new MovieScene(UUID.randomUUID(), 13434954L, 52534008L,
                Map.of("deu", "Der mit dem " + "Wolf tanzt Szene 3", "eng", "Dances with Wolves scene 3"), movie);

        assertThat(this.movieSceneRepository.count()).isEqualTo(3);
        this.movieSceneRepository.save(movieScene);
        assertThat(this.movieSceneRepository.count()).isEqualTo(4);
    }

    @Test
    public void shouldUpdateMovieScene() {
        MovieScene movieScene = this.movieSceneRepository.findAll().getFirst();
        assertThat(movieScene.getDescription().get("eng")).isEqualTo("Dances with Wolves scene 1");
        assertThat(this.movieSceneRepository.count()).isEqualTo(3);
        movieScene.setDescription(new HashMap<>(Map.of("eng", "A new description")));
        assertThat(this.movieSceneRepository.count()).isEqualTo(3);
        this.movieSceneRepository.save(movieScene);
        assertThat(this.movieSceneRepository.findAll().getLast().getDescription().get("eng")).isEqualTo(
                "A new " + "description");
    }

    @Test
    public void shouldDeleteMovieScene() {
        assertThat(this.movieSceneRepository.count()).isEqualTo(3);
        this.movieSceneRepository.deleteById(this.movieSceneRepository.findAll().getFirst().getId());
        assertThat(this.movieSceneRepository.count()).isEqualTo(2);
    }

    @Test
    public void shouldFailPersistMovieSceneDueToAlreadyExistingUuid() {
        UUID uuid = UUID.randomUUID();

        Movie wolf = this.movieRepository.findAll().getFirst();

        MovieScene movieScene_1 = new MovieScene(uuid, 13404954L, 52520008L,
                Map.of("deu", "Der mit dem " + "Wolf tanzt Szene 1", "eng", "Dances with Wolves scene 1"), wolf);
        MovieScene movieScene_2 = new MovieScene(uuid, 13404954L, 52520008L,
                Map.of("deu", "Der mit dem " + "Wolf tanzt Szene 2", "eng", "Dances with Wolves scene 2"), wolf);

        this.movieSceneRepository.save(movieScene_1);
        assertThrows(DataIntegrityViolationException.class, () -> {
            this.movieSceneRepository.save(movieScene_2);
        });
    }

    @Test
    public void shouldFindMovieSceneByUuid() {
        MovieScene movieScene = this.movieSceneRepository.findAll().getFirst();
        assertThat(this.movieSceneRepository.findByUuid(movieScene.getUuid()).get()).isEqualTo(movieScene);
    }

    @Test
    public void shouldDeleteMovieSceneByUuid() {
        MovieScene movieScene = this.movieSceneRepository.findAll().getFirst();
        assertThat(this.movieSceneRepository.count()).isEqualTo(3);
        this.movieSceneRepository.deleteByUuid(movieScene.getUuid());
        assertThat(this.movieSceneRepository.count()).isEqualTo(2);
    }

    @Test
    public void shouldExist() {
        assertThat(this.movieSceneRepository.existsByUuid(
                this.movieSceneRepository.findAll().getFirst().getUuid())).isEqualTo(true);
    }

    @Test
    public void shouldNotExist() {
        assertThat(this.movieSceneRepository.existsByUuid(UUID.randomUUID())).isEqualTo(false);
    }

    @Test
    public void shouldFindAllMovieScenesFromMovie() {
        assertThat(this.movieSceneRepository.findAllScenesOfMovie(this.movieRepository.findAll().getFirst().getUuid())
                .get()).hasSize(2);
    }

    @Test
    public void shouldDeleteAllGivenScenesWithDeleteAll() {
        Optional<List<MovieScene>> movieScenes = this.movieSceneRepository.findAllScenesOfMovie(this.movieRepository.findAll().getFirst().getUuid());
        movieScenes.ifPresent(this.movieSceneRepository::deleteAll);
        assertThat(this.movieSceneRepository.count()).isEqualTo(1);
    }


    // Fixme: [ERROR: missing FROM-clause entry for table "m1_0" Position: 106]
//    @Test
//    public void shouldDeleteAllMovieScenesFromMovie() {
//        this.movieSceneRepository.deleteAllScenesFromMovie(this.movieRepository.findAll().getLast().getUuid());
//        assertThat(this.movieSceneRepository.findAll()).hasSize(0);
//    }
}