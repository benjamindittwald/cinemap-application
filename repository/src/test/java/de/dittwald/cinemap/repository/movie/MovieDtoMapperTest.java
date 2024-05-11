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

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@WebMvcTest(MovieDtoMapper.class)
class MovieDtoMapperTest {

    @Autowired
    private MovieDtoMapper movieDtoMapper;

    private DummyMovies dummyMovies;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyMovies = new DummyMovies();
    }

    @Test
    void shouldConvertMovieToMovieDto() {
        MovieDto movieDto = this.movieDtoMapper.movieToMovieDto(this.dummyMovies.getWolf());
        assertThat(movieDto.uuid()).isEqualTo(this.dummyMovies.getWolf().getUuid());
        assertThat(movieDto.localizedMovies().get("eng")).isEqualTo(
                this.dummyMovies.getWolf().getLocalizedMovies().get("eng"));
    }

        @Test
        void shouldConvertDtoMovieToMovie() {
            assertThat(this.movieDtoMapper.movieDtoToMovie(this.dummyMovies.getWolfDto()).getTmdbId()).isEqualTo(
                    this.dummyMovies.getWolfDto().tmdbId());
        }
}