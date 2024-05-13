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

package de.dittwald.cinemap.repository.movie.service;

import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.movie.DummyMovies;
import de.dittwald.cinemap.repository.movie.repository.LocalizedMovieRepository;
import de.dittwald.cinemap.repository.movie.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@WebMvcTest({MovieLocalizationService.class})
@AutoConfigureMockMvc
class MovieLocalizationServiceTest {

    @Autowired
    private MovieLocalizationService movieLocalizationService;

    @MockBean
    private MovieRepository movieRepository;

    @MockBean
    private LocalizedMovieRepository localizedMovieRepository;

    private DummyMovies dummyMovies;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyMovies = new DummyMovies();
    }

    @Test
    void shouldUpdateMovieAndLocalizationsNoOverride() throws NotFoundException {
        when(this.movieRepository.findByUuid(this.dummyMovies.getWolfLocalizationDto().movieUuid())).thenReturn(
                Optional.of(this.dummyMovies.getWolf()));
        when(this.movieRepository.save(this.dummyMovies.getWolf())).thenReturn(any());

        this.movieLocalizationService.update(this.dummyMovies.getWolfLocalizationDto(),
                this.dummyMovies.getWolfLocalizationDto().movieUuid(), false);

        verify(this.movieRepository, times(1)).findByUuid(this.dummyMovies.getWolfLocalizationDto().movieUuid());
        verify(this.movieRepository, times(1)).save(this.dummyMovies.getWolf());
    }

    @Test
    void shouldFailUpdateMovieAndLocalizationsNoOverrideDueToNotFoundMovie() {
        when(this.movieRepository.findByUuid(this.dummyMovies.getWolfLocalizationDto().movieUuid())).thenReturn(
                Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            this.movieLocalizationService.update(this.dummyMovies.getWolfLocalizationDto(),
                    this.dummyMovies.getWolfLocalizationDto().movieUuid(), false);
        });
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
        verify(this.movieRepository, times(1)).findByUuid(this.dummyMovies.getWolfLocalizationDto().movieUuid());
    }

    @Test
    void shouldFindAllLocalizationsForMovieUuid() throws NotFoundException {
        when(this.localizedMovieRepository.findAllByMovieUuid(this.dummyMovies.getWolf().getUuid())).thenReturn(
                Optional.of(List.of(this.dummyMovies.getWolfLocalizedMovieEn(),
                        this.dummyMovies.getWolfLocalizedMovieDe())));
        assertThat(this.movieLocalizationService.getMovieLocalizations(this.dummyMovies.getWolf().getUuid())).isEqualTo(
                this.dummyMovies.getWolfLocalizationDto());

        verify(this.localizedMovieRepository, times(1)).findAllByMovieUuid(this.dummyMovies.getWolf().getUuid());
    }

    @Test
    void shouldFailFindAllLocalizationsForMovieUuidDueToNotFoundLocalizations() throws NotFoundException {
        when(this.localizedMovieRepository.findAllByMovieUuid(this.dummyMovies.getWolf().getUuid())).thenReturn(
                Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            this.movieLocalizationService.getMovieLocalizations(this.dummyMovies.getWolf().getUuid());
        });
        assertThat(exception.getMessage()).isEqualTo("No localized movies found");
    }
}