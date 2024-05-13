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

package de.dittwald.cinemap.repository.movie.util;

import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.movie.DummyMovies;
import de.dittwald.cinemap.repository.movie.dto.MovieFlatDto;
import de.dittwald.cinemap.repository.movie.entity.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class MovieFlatDtoMapperTest {

    private DummyMovies dummyMovies;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyMovies = new DummyMovies();
    }

    @Test
    void shouldMapEntityToDto() throws LocaleNotFoundException {
        MovieFlatDto movieFlatDto = LocalizedMovieDtoMapper.entityToDto(this.dummyMovies.getWolf(),
                this.dummyMovies.getWolf().getLocalizedMovies().get("en").getLocalizedId().getLocale());
        assertEquals(movieFlatDto.uuid(), this.dummyMovies.getWolf().getUuid());
        assertEquals(this.dummyMovies.getWolfFlatEnDto(), movieFlatDto);
    }

    @Test
    void shouldMapDtoToEntity() {
        Movie movie = LocalizedMovieDtoMapper.dtoToEntity(this.dummyMovies.getWolfFlatEnDto());
        assertEquals(this.dummyMovies.getWolf(), movie);
    }

//    @Test
//    void shouldMapCompleteMovieDtoToEntity() {
//        Movie entity = LocalizedMovieDtoMapper.completeMovieDtoToEntity(this.dummyMovies.getWolfCompleteDto());
//        assertEquals(this.dummyMovies.getWolf(), entity);
//    }
//
//    @Test
//    void shouldMapEntityToCompleteMovieDto() {
//        CompleteMovieDto dto = LocalizedMovieDtoMapper.entityToCompleteDto(this.dummyMovies.getWolf());
//        assertEquals(this.dummyMovies.getWolfCompleteDto(), dto);
//    }
}