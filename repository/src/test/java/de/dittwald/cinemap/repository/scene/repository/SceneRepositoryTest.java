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

package de.dittwald.cinemap.repository.scene.repository;

import de.dittwald.cinemap.repository.movie.entity.LocalizedId;
import de.dittwald.cinemap.repository.movie.repository.MovieRepository;
import de.dittwald.cinemap.repository.scene.entity.LocalizedScene;
import de.dittwald.cinemap.repository.scene.entity.Scene;
import de.dittwald.cinemap.repository.util.DummyData;
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
import java.net.URISyntaxException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {SceneRepository.class, MovieRepository.class, SceneLocalizedRepository.class}))
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
    private SceneLocalizedRepository sceneLocalizedRepository;

    private DummyData dummyData;

    @BeforeEach
    void setUp() throws URISyntaxException, MalformedURLException {
        dummyData = new DummyData();
        this.movieRepository.save(this.dummyData.getWolf());
        this.sceneRepository.save(this.dummyData.getWolfSceneOne());
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
        assertThat(movieScenes.getFirst().getMovie().getLocalizedMovies().get("en").getTitle()).isEqualTo(
                "Dances with Wolves - Title");
        assertThat(movieScenes.getFirst().getMovie().getLocalizedMovies().get("de").getTitle()).isEqualTo(
                "Der mit dem Wolf tanzt - Title");
    }

    @Test
    public void shouldFindSceneById() {
        Long id = this.sceneRepository.findAll().getFirst().getId();
        assertThat(this.sceneRepository.findById(id).get().getId()).isNotNull();
    }

    @Test
    public void shouldUpdateScene() {
        Scene scene = this.sceneRepository.findByUuid(this.dummyData.getWolfSceneOne().getUuid()).get();

        LocalizedScene lmsEn = scene.getLocalizedScenes().get("de");
        lmsEn.setDescription("Der mit dem Wolf tanzt - Scene One New Description");
        assertThat(this.sceneLocalizedRepository.count()).isEqualTo(2);
        scene.getLocalizedScenes().put("de", lmsEn);
        this.sceneRepository.save(scene);
        assertThat(this.sceneLocalizedRepository.count()).isEqualTo(2);
        assertThat(this.sceneRepository.findByUuid(this.dummyData.getWolfSceneOne().getUuid())
                .get()
                .getLocalizedScenes()
                .get("de")
                .getDescription()).isEqualTo("Der mit dem Wolf tanzt - Scene One New Description");
    }

    @Test
    public void shouldDeleteMovieScene() {
        assertThat(this.sceneRepository.count()).isEqualTo(1);
        this.sceneRepository.deleteById(this.sceneRepository.findAll().getFirst().getId());
        assertThat(this.sceneRepository.count()).isEqualTo(0);
    }

    @Test
    public void shouldFailPersistMovieSceneDueToAlreadyExistingUuid() {

        this.movieRepository.save(this.dummyData.getNobody());

        Scene scene = new Scene();
        scene.setUuid(this.dummyData.getWolfSceneOne().getUuid());
        scene.setLat(52.51263);
        scene.setLon(13.35943);
        scene.setMovie(this.dummyData.getWolf());

        LocalizedScene lmsEn = new LocalizedScene();
        lmsEn.setLocalizedId(new LocalizedId("en"));
        lmsEn.setDescription("Dances with Wolves - Scene One Description");
        lmsEn.setScene(scene);
        scene.getLocalizedScenes().put("en", lmsEn);

        assertThrows(DataIntegrityViolationException.class, () -> {
            this.sceneRepository.save(scene);
        });
    }

    @Test
    public void shouldFindMovieSceneByUuid() {
        assertThat(this.sceneRepository.findByUuid(this.dummyData.getWolfSceneOne().getUuid()).get()).isNotNull();
    }

    @Test
    public void shouldDeleteMovieSceneByUuid() {
        assertThat(this.sceneRepository.count()).isEqualTo(1);
        this.sceneRepository.deleteByUuid(this.dummyData.getWolfSceneOne().getUuid());
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