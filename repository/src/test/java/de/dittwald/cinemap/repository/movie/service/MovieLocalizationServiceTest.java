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
import de.dittwald.cinemap.repository.util.DummyData;
import de.dittwald.cinemap.repository.movie.repository.MovieLocalizedRepository;
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
    private MovieLocalizedRepository movieLocalizedRepository;

    private DummyData dummyData;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyData = new DummyData();
    }

    @Test
    void shouldUpdateMovieAndLocalizationsNoOverride() throws NotFoundException {
        when(this.movieRepository.findByUuid(this.dummyData.getWolfLocalizationDto().movieUuid())).thenReturn(
                Optional.of(this.dummyData.getWolf()));
        when(this.movieRepository.save(this.dummyData.getWolf())).thenReturn(any());

        this.movieLocalizationService.update(this.dummyData.getWolfLocalizationDto(),
                this.dummyData.getWolfLocalizationDto().movieUuid(), false);

        verify(this.movieRepository, times(1)).findByUuid(this.dummyData.getWolfLocalizationDto().movieUuid());
        verify(this.movieRepository, times(1)).save(this.dummyData.getWolf());
    }

    @Test
    void shouldFailUpdateMovieAndLocalizationsNoOverrideDueToNotFoundMovie() {
        when(this.movieRepository.findByUuid(this.dummyData.getWolfLocalizationDto().movieUuid())).thenReturn(
                Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            this.movieLocalizationService.update(this.dummyData.getWolfLocalizationDto(),
                    this.dummyData.getWolfLocalizationDto().movieUuid(), false);
        });
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
        verify(this.movieRepository, times(1)).findByUuid(this.dummyData.getWolfLocalizationDto().movieUuid());
    }

    @Test
    void shouldFindAllLocalizationsForMovieUuid() throws NotFoundException {
        when(this.movieLocalizedRepository.findAllByMovieUuid(this.dummyData.getWolf().getUuid())).thenReturn(
                Optional.of(List.of(this.dummyData.getWolfLocalizedMovieEn(),
                        this.dummyData.getWolfLocalizedMovieDe())));
        assertThat(this.movieLocalizationService.getMovieLocalizations(this.dummyData.getWolf().getUuid())).isEqualTo(
                this.dummyData.getWolfLocalizationDto());

        verify(this.movieLocalizedRepository, times(1)).findAllByMovieUuid(this.dummyData.getWolf().getUuid());
    }

    @Test
    void shouldFailFindAllLocalizationsForMovieUuidDueToNotFoundLocalizations() throws NotFoundException {
        when(this.movieLocalizedRepository.findAllByMovieUuid(this.dummyData.getWolf().getUuid())).thenReturn(
                Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            this.movieLocalizationService.getMovieLocalizations(this.dummyData.getWolf().getUuid());
        });
        assertThat(exception.getMessage()).isEqualTo("No localized movies found");
        verify(this.movieLocalizedRepository, times(1)).findAllByMovieUuid(this.dummyData.getWolf().getUuid());
    }
}