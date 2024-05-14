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

import de.dittwald.cinemap.repository.movie.DummyMovies;
import de.dittwald.cinemap.repository.movie.repository.MovieLocalizedRepository;
import de.dittwald.cinemap.repository.movie.repository.MovieRepository;
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

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {SceneLocalizedRepository.class, SceneRepository.class, MovieRepository.class}))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SceneLocalizedRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16.3-alpine").withInitScript("schema.sql");

    @Autowired
    private SceneLocalizedRepository sceneLocalizedRepository;

    @Autowired
    private SceneRepository sceneRepository;

    @Autowired
    private MovieRepository movieRepository;

    private DummyMovies dummyMovies;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyMovies = new DummyMovies();
        this.movieRepository.save(this.dummyMovies.getWolf());
        this.sceneRepository.save(this.dummyMovies.getWolfSceneOne());
    }

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void findAllBySceneUuid() {
        assertThat(this.sceneLocalizedRepository.findAllBySceneUuid(this.dummyMovies.getWolfSceneOne().getUuid())
                .get()
                .size()).isEqualTo(2);
    }

    @Test
    void shouldDeleteAllLocalizedScenesWhenDeleteSetUpScene() {
        assertThat(this.sceneRepository.count()).isEqualTo(1);
        assertThat(this.sceneLocalizedRepository.count()).isEqualTo(2);
        this.sceneRepository.deleteAll();
        assertThat(this.sceneRepository.count()).isEqualTo(0);
        assertThat(this.sceneLocalizedRepository.count()).isEqualTo(0);
    }
}