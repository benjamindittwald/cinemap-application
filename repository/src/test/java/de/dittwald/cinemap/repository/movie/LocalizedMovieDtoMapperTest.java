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

package de.dittwald.cinemap.repository.movie;

import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class MovieDtoMapperTest {

    private DummyMovies dummyMovies;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyMovies = new DummyMovies();
    }

    @Test
    void entityToDto() throws LocaleNotFoundException {
        MovieDto movieDto = LocalizedMovieDtoMapper.entityToDto(this.dummyMovies.getWolf(),
                this.dummyMovies.getWolf().getLocalizedMovies().get("en").getLocalizedId().getLocale());
        assertEquals(movieDto.uuid(), this.dummyMovies.getWolf().getUuid());
        assertEquals(movieDto.locale(),
                this.dummyMovies.getWolf().getLocalizedMovies().get("en").getLocalizedId().getLocale());
        assertEquals(movieDto.overview(),
                this.dummyMovies.getWolf().getLocalizedMovies().get("en").getOverview());
    }

    @Test
    void dtoToEntity() throws MalformedURLException, URISyntaxException {
        Movie movie = LocalizedMovieDtoMapper.dtoToEntity(this.dummyMovies.getWolfEnDto());
        assertEquals(movie.getUuid(), this.dummyMovies.getWolfEnDto().uuid());
        assertEquals(movie.getLocalizedMovies().get("en").getTitle(), this.dummyMovies.getWolfEnDto().title());
        assertEquals(movie.getLocalizedMovies().get("en").getLocalizedId().getLocale(),
                this.dummyMovies.getWolfEnDto().locale());
    }
}