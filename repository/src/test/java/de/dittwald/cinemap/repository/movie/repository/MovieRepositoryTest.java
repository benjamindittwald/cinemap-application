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

import de.dittwald.cinemap.repository.util.DummyData;
import de.dittwald.cinemap.repository.movie.entity.Movie;
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
import java.util.*;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {MovieRepository.class, MovieLocalizedRepository.class}))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MovieRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16.3-alpine").withInitScript("schema.sql");

    @Autowired
    private MovieRepository movieRepository;

    private DummyData dummyData;

    @BeforeEach
    public void setUp() throws URISyntaxException, MalformedURLException {
        this.dummyData = new DummyData();
        this.movieRepository.save(this.dummyData.getWolf());
    }

    @Test
    public void connectionEstablished() {
        assertThat(postgres.isCreated()).isTrue();
        assertThat(postgres.isRunning()).isTrue();
    }

    @Test
    public void shouldFindTwoMovies() {
        assertEquals(this.movieRepository.findAll().size(), 1);

        Movie movie = this.movieRepository.findAll().get(0);

        assertEquals(movie.getLocalizedMovies().get("en").getTitle(), "Dances with Wolves - Title");
        assertEquals(movie.getLocalizedMovies().get("de").getTitle(), "Der mit dem Wolf tanzt - Title");
    }

    @Test
    public void shouldFindMovieByUuid() {
        assertThat(this.movieRepository.findByUuid(this.dummyData.getWolf().getUuid()).get().getUuid()).isEqualTo(
                this.dummyData.getWolf().getUuid());
    }

    @Test
    public void shouldPersistMovie() {
        assertThat(this.movieRepository.count()).isEqualTo(1);
        this.movieRepository.save(this.dummyData.getNobody());
        assertThat(this.movieRepository.count()).isEqualTo(2);
    }

    @Test
    public void shouldDeleteMovie() {
        assertThat(this.movieRepository.count()).isEqualTo(1);
        this.movieRepository.delete(this.movieRepository.findByUuid(this.dummyData.getWolf().getUuid()).get());
        assertThat(this.movieRepository.count()).isEqualTo(0);
    }

    // Fixme: java.lang.UnsupportedOperationException
    //    @Test
    //    public void shouldUpdateMovie() {
    //        Movie movie = this.movieRepository.findByUuid(this.dummyMovies.getWolf().getUuid()).get();
    //
    //        LocalizedMovie lmEn = new LocalizedMovie();
    //        lmEn.setLocalizedId(new LocalizedId("fra"));
    //        lmEn.setOverview("Danse avec les loups - Overview");
    //        lmEn.setTagline("Danse avec les loups - Tagline");
    //        lmEn.setTitle("Danse avec les loups - Title");
    //        lmEn.setMovie(movie);
    //        movie.getLocalizedMovies().put("fra", lmEn);
    //
    //        this.movieRepository.save(movie);
    //
    //        assertThat(this.movieRepository.findByUuid(this.dummyMovies.getWolf().getUuid())
    //                .get()
    //                .getLocalizedMovies()
    //                .get("fra")
    //                .getTitle()).isEqualTo("Danse avec les loups - Title");
    //    }

    @Test
    public void shouldDeleteByUuid() {
        assertThat(this.movieRepository.count()).isEqualTo(1);
        this.movieRepository.deleteByUuid(this.dummyData.getWolf().getUuid());
        assertThat(this.movieRepository.count()).isEqualTo(0);
    }

    @Test
    public void shouldFailDeleteByUuidDueToNotFound() {
        assertThat(this.movieRepository.count()).isEqualTo(1);
        this.movieRepository.deleteByUuid(this.dummyData.getWolf().getUuid());
        assertThat(this.movieRepository.count()).isEqualTo(0);
    }

    @Test
    public void shouldExist() {
        assertThat(this.movieRepository.existsByUuid(this.dummyData.getWolf().getUuid())).isEqualTo(true);
    }

    @Test
    public void shouldNotExist() {
        assertThat(
                this.movieRepository.existsByUuid(UUID.fromString("132bf117-8bd7-4c95-8821-1f772e23dc21"))).isEqualTo(
                false);
    }
}