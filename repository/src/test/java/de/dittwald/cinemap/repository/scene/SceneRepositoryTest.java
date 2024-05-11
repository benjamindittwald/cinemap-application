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

package de.dittwald.cinemap.repository.scene;

import de.dittwald.cinemap.repository.movie.*;
import de.dittwald.cinemap.repository.scene.LocalizedScene;
import de.dittwald.cinemap.repository.scene.LocalizedSceneRepository;
import de.dittwald.cinemap.repository.scene.Scene;
import de.dittwald.cinemap.repository.scene.SceneRepository;
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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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

    private DummyMovies dummyMovies;

    @BeforeEach
    void setUp() throws URISyntaxException, MalformedURLException {
        dummyMovies = new DummyMovies();
        this.movieRepository.save(this.dummyMovies.getWolf());
        this.sceneRepository.save(this.dummyMovies.getScene());
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
        Scene scene = this.sceneRepository.findByUuid(this.dummyMovies.getScene().getUuid()).get();

        LocalizedScene lmsEn = new LocalizedScene();
        lmsEn.setLocalizedId(new LocalizedId("deu"));
        lmsEn.setDescription("Der mit dem Wolf tanzt - Scene Description");
        lmsEn.setScene(scene);
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
    public void shouldFailPersistMovieSceneDueToAlreadyExistingUuid() throws URISyntaxException, MalformedURLException {

        this.movieRepository.save(this.dummyMovies.getNobody());

        Scene scene = new Scene();
        scene.setUuid(this.dummyMovies.getScene().getUuid());
        scene.setLat(52.51263);
        scene.setLon(13.35943);
        scene.setMovie(this.dummyMovies.getWolf());

        LocalizedScene lmsEn = new LocalizedScene();
        lmsEn.setLocalizedId(new LocalizedId("eng"));
        lmsEn.setDescription("Dances with Wolves - Scene Description");
        lmsEn.setScene(scene);
        scene.getLocalizedMoviesScenes().put("eng", lmsEn);

        assertThrows(DataIntegrityViolationException.class, () -> {
            this.sceneRepository.save(scene);
        });
    }

    @Test
    public void shouldFindMovieSceneByUuid() {
        assertThat(this.sceneRepository.findByUuid(this.dummyMovies.getScene().getUuid()).get()).isNotNull();
    }

    @Test
    public void shouldDeleteMovieSceneByUuid() {
        assertThat(this.sceneRepository.count()).isEqualTo(1);
        this.sceneRepository.deleteByUuid(this.dummyMovies.getScene().getUuid());
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
}