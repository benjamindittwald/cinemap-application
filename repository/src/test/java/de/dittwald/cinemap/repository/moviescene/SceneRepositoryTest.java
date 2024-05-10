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

import de.dittwald.cinemap.repository.movie.LocalizedId;
import de.dittwald.cinemap.repository.movie.LocalizedMovie;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {SceneRepository.class, MovieRepository.class, LocalizedSceneRepository.class}))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SceneRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16.3-alpine").withInitScript("schema.sql");

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private LocalizedSceneRepository localizedSceneRepository;

    private final UUID setUpMovieUUID = UUID.fromString("132bf117-8bd7-4c95-8821-1f772e23dc26");
    private final UUID getSetUpSceneUUID = UUID.fromString("132bf117-8bd7-4c95-8821-1f772e23dc20");


    @BeforeEach
    void setUp() {
        Movie movie = new Movie();
        movie.setUuid(this.setUpMovieUUID);
        movie.setGenres(Map.of(80, "western", 85, "Thriller"));
        movie.setPoster("https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg");
        movie.setReleaseYear(1970);
        movie.setTmdbId(505);
        movie.setImdbId("imdbID");

        LocalizedMovie lmEn = new LocalizedMovie();
        lmEn.setLocalizedId(new LocalizedId("eng"));
        lmEn.setOverview("Dances with Wolves - Overview");
        lmEn.setTagline("Dances with Wolves - Tagline");
        lmEn.setTitle("Dances with Wolves - Title");
        lmEn.setMovie(movie);
        movie.getLocalizedMovies().put("eng", lmEn);

        LocalizedMovie lmDe = new LocalizedMovie();
        lmDe.setLocalizedId(new LocalizedId("deu"));
        lmDe.setOverview("Der mit dem Wolf tanzt - Overview");
        lmDe.setTagline("Der mit dem Wolf tanzt - Tagline");
        lmDe.setTitle("Der mit dem Wolf tanzt - Title");
        lmDe.setMovie(movie);
        movie.getLocalizedMovies().put("deu", lmDe);

        this.movieRepository.save(movie);

        Scene scene = new Scene();
        scene.setUuid(this.getSetUpSceneUUID);
        scene.setLat(52.51263);
        scene.setLon(13.35943);
        scene.setMovie(movie);

        LocalizedScene lmsEn = new LocalizedScene();
        lmsEn.setLocalizedId(new LocalizedId("eng"));
        lmsEn.setDescription("Dances with Wolves - Scene Description");
        lmsEn.setMovieScene(scene);
        scene.getLocalizedMoviesScenes().put("eng", lmsEn);

        this.sceneRepository.save(scene);
    }

    @Test
    public void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    public void shouldFindOneScenes() {
        List<Scene> movieScenes = this.sceneRepository.findAll();
        assertThat(movieScenes.size()).isEqualTo(1);
        assertThat(movieScenes.getFirst().getMovie().getLocalizedMovies().get("eng").getTitle()).isEqualTo(
                "Dances with Wolves - Title");
        assertThat(movieScenes.getFirst().getMovie().getLocalizedMovies().get("deu").getTitle()).isEqualTo(
                "Der mit dem Wolf tanzt - Title");
    }

    @Test
    public void shouldFindSceneById() {
        Long id = this.sceneRepository.findAll().getFirst().getId();
        assertThat(this.sceneRepository.findById(id).get().getId()).isNotNull();
    }

    @Test
    public void shouldUpdateMovieScene() {
        Scene scene = this.sceneRepository.findByUuid(this.getSetUpSceneUUID).get();

        LocalizedScene lmsEn = new LocalizedScene();
        lmsEn.setLocalizedId(new LocalizedId("deu"));
        lmsEn.setDescription("Der mit dem Wolf tanzt - Scene Description");
        lmsEn.setMovieScene(scene);
        assertThat(this.localizedSceneRepository.count()).isEqualTo(1);
        scene.getLocalizedMoviesScenes().put("deu", lmsEn);
        assertThat(this.localizedSceneRepository.count()).isEqualTo(2);
        this.sceneRepository.save(scene);
    }

    @Test
    public void shouldDeleteMovieScene() {
        assertThat(this.sceneRepository.count()).isEqualTo(1);
        this.sceneRepository.deleteById(this.sceneRepository.findAll().getFirst().getId());
        assertThat(this.sceneRepository.count()).isEqualTo(0);
    }

    @Test
    public void shouldFailPersistMovieSceneDueToAlreadyExistingUuid() {
        Movie movie = new Movie();
        movie.setUuid(UUID.randomUUID());
        movie.setGenres(Map.of(80, "western", 85, "Thriller"));
        movie.setPoster("https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg");
        movie.setReleaseYear(1970);
        movie.setTmdbId(505);
        movie.setImdbId("imdbID");

        LocalizedMovie lmEn = new LocalizedMovie();
        lmEn.setLocalizedId(new LocalizedId("eng"));
        lmEn.setOverview("Dances with Wolves - Overview");
        lmEn.setTagline("Dances with Wolves - Tagline");
        lmEn.setTitle("Dances with Wolves - Title");
        lmEn.setMovie(movie);
        movie.getLocalizedMovies().put("eng", lmEn);

        this.movieRepository.save(movie);

        Scene scene = new Scene();
        scene.setUuid(this.getSetUpSceneUUID);
        scene.setLat(52.51263);
        scene.setLon(13.35943);
        scene.setMovie(movie);

        LocalizedScene lmsEn = new LocalizedScene();
        lmsEn.setLocalizedId(new LocalizedId("eng"));
        lmsEn.setDescription("Dances with Wolves - Scene Description");
        lmsEn.setMovieScene(scene);
        scene.getLocalizedMoviesScenes().put("eng", lmsEn);

        assertThrows(DataIntegrityViolationException.class, () -> {
            this.sceneRepository.save(scene);
        });
    }

    @Test
    public void shouldFindMovieSceneByUuid() {
        assertThat(this.sceneRepository.findByUuid(this.getSetUpSceneUUID).get()).isNotNull();
    }

    @Test
    public void shouldDeleteMovieSceneByUuid() {
        assertThat(this.sceneRepository.count()).isEqualTo(1);
        this.sceneRepository.deleteByUuid(this.getSetUpSceneUUID);
        assertThat(this.sceneRepository.count()).isEqualTo(0);
    }

    @Test
    public void shouldExist() {
        assertThat(this.sceneRepository.existsByUuid(this.sceneRepository.findAll().getFirst().getUuid())).isEqualTo(
                true);
    }

    @Test
    public void shouldNotExist() {
        assertThat(this.sceneRepository.existsByUuid(UUID.randomUUID())).isEqualTo(false);
    }


    // Fixme: [ERROR: missing FROM-clause entry for table "m1_0" Position: 106]
    //    @Test
    //    public void shouldDeleteAllMovieScenesFromMovie() {
    //        this.movieSceneRepository.deleteAllScenesFromMovie(this.movieRepository.findAll().getLast().getUuid());
    //        assertThat(this.movieSceneRepository.findAll()).hasSize(0);
    //    }
}