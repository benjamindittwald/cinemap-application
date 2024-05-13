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

package de.dittwald.cinemap.repository.movie.repository;

import de.dittwald.cinemap.repository.movie.DummyMovies;
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

@Testcontainers
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {LocalizedMovieRepository.class, MovieRepository.class}))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LocalizedMovieRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16.2-alpine").withInitScript("schema.sql");

    @Autowired
    private LocalizedMovieRepository localizedMovieRepository;

    @Autowired
    private MovieRepository movieRepository;

    private DummyMovies dummyMovies;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyMovies = new DummyMovies();
        this.movieRepository.save(this.dummyMovies.getWolf());
        this.movieRepository.save(this.dummyMovies.getNobody());
    }

    @Test
    void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    void shouldFindTwoLocalizedMovies() {
        assertThat(localizedMovieRepository.findAll()).hasSize(4);
    }

    @Test
    void shouldFindTwoLocalizedMoviesForWolf() {
        assertThat(localizedMovieRepository.findAllByMovieUuid(this.dummyMovies.getWolf().getUuid()).get()).hasSize(2);
    }

    @Test
    public void shouldPersistLocalizedMoviesViaMovieSave() {

        // Remove Nobody before testing
        this.movieRepository.deleteByUuid(this.dummyMovies.getNobody().getUuid());

        assertThat(this.movieRepository.count()).isEqualTo(1);
        assertThat(this.localizedMovieRepository.count()).isEqualTo(2);

        this.movieRepository.save(this.dummyMovies.getNobody());

        assertThat(this.movieRepository.count()).isEqualTo(2);
        assertThat(this.localizedMovieRepository.count()).isEqualTo(4);
    }

    @Test
    public void shouldDeleteAllLocalizedMoviesWhenDeleteSetUpMovie() {
        assertThat(this.movieRepository.count()).isEqualTo(2);
        assertThat(this.localizedMovieRepository.count()).isEqualTo(4);
        this.movieRepository.deleteAll();
        assertThat(this.movieRepository.count()).isEqualTo(0);
        assertThat(this.localizedMovieRepository.count()).isEqualTo(0);
    }
}