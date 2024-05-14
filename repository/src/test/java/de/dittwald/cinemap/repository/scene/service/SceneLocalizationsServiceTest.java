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

package de.dittwald.cinemap.repository.scene.service;

import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.movie.DummyMovies;
import de.dittwald.cinemap.repository.movie.repository.MovieRepository;
import de.dittwald.cinemap.repository.scene.repository.SceneLocalizedRepository;
import de.dittwald.cinemap.repository.scene.repository.SceneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@WebMvcTest({SceneLocalizationService.class})
@AutoConfigureMockMvc
public class SceneLocalizationsServiceTest {

    @Autowired
    private SceneLocalizationService sceneLocalizationService;

    @MockBean
    private SceneRepository sceneRepository;

    @MockBean
    private SceneLocalizedRepository sceneLocalizedRepository;

    @MockBean
    private MovieRepository movieRepository;

    private DummyMovies dummyMovies;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyMovies = new DummyMovies();
    }

    @Test
    void shouldUpdateSceneAndLocalizationsNoOverride() throws NotFoundException {
        when(this.movieRepository.existsByUuid(this.dummyMovies.getWolf().getUuid())).thenReturn(true);
        when(this.sceneRepository.findByUuid(this.dummyMovies.getWolfSceneOne().getUuid())).thenReturn(
                Optional.of(this.dummyMovies.getWolfSceneOne()));

        this.sceneLocalizationService.update(this.dummyMovies.getWolfSceneOneLocalizationDto(),
                this.dummyMovies.getWolf().getUuid(), this.dummyMovies.getWolfSceneOne().getUuid(), false);

        verify(this.movieRepository, times(1)).existsByUuid(this.dummyMovies.getWolf().getUuid());
        verify(this.sceneRepository, times(1)).save(this.dummyMovies.getWolfSceneOne());
    }

    @Test
    void shouldFailUpdateSceneAndLocalizationsNoOverrideDueToNotFoundMovie() {
        when(this.movieRepository.existsByUuid(this.dummyMovies.getWolf().getUuid())).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            this.sceneLocalizationService.update(this.dummyMovies.getWolfSceneOneLocalizationDto(),
                    this.dummyMovies.getWolf().getUuid(), this.dummyMovies.getWolfSceneOne().getUuid(), false);
        });
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
        verify(this.movieRepository, times(1)).existsByUuid(this.dummyMovies.getWolfLocalizationDto().movieUuid());
    }

    @Test
    void shouldFailUpdateSceneAndLocalizationsNoOverrideDueToNotFoundScene() {
        when(this.movieRepository.existsByUuid(this.dummyMovies.getWolf().getUuid())).thenReturn(true);
        when(this.sceneRepository.findByUuid(this.dummyMovies.getWolfSceneOne().getUuid())).thenReturn(
                Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            this.sceneLocalizationService.update(this.dummyMovies.getWolfSceneOneLocalizationDto(),
                    this.dummyMovies.getWolf().getUuid(), this.dummyMovies.getWolfSceneOne().getUuid(), false);
        });
        assertThat(exception.getMessage()).isEqualTo("Scene not found");
        verify(this.movieRepository, times(1)).existsByUuid(this.dummyMovies.getWolfLocalizationDto().movieUuid());
        verify(this.sceneRepository, times(1)).findByUuid(this.dummyMovies.getWolfSceneOne().getUuid());
    }

    @Test
    void shouldFindAllLocalizationsForMovieUuid() throws NotFoundException {
        when(this.movieRepository.existsByUuid(this.dummyMovies.getWolf().getUuid())).thenReturn(true);
        when(this.sceneLocalizedRepository.findAllBySceneUuid(this.dummyMovies.getWolfSceneOne().getUuid())).thenReturn(
                Optional.of(List.of(this.dummyMovies.getWolfLsOneEn(), this.dummyMovies.getWolfLsOneDe())));

        assertThat(this.sceneLocalizationService.getSceneLocalizationDto(this.dummyMovies.getWolfSceneOne().getUuid(),
                this.dummyMovies.getWolf().getUuid())).isEqualTo(this.dummyMovies.getWolfSceneOneLocalizationDto());

        verify(this.movieRepository, times(1)).existsByUuid(this.dummyMovies.getWolf().getUuid());
        verify(this.sceneLocalizedRepository, times(1)).findAllBySceneUuid(
                this.dummyMovies.getWolfSceneOne().getUuid());
    }

    @Test
    void shouldFailFindAllLocalizationsForMovieUuidDueToNotFoundMovie() throws NotFoundException {
        when(this.movieRepository.existsByUuid(this.dummyMovies.getWolf().getUuid())).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            this.sceneLocalizationService.getSceneLocalizationDto(this.dummyMovies.getWolfSceneOne().getUuid(),
                    this.dummyMovies.getWolf().getUuid());
        });
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
        verify(this.movieRepository, times(1)).existsByUuid(this.dummyMovies.getWolf().getUuid());
    }

    @Test
    void shouldFailFindAllLocalizationsForMovieUuidDueToNotFoundScene() throws NotFoundException {
        when(this.movieRepository.existsByUuid(this.dummyMovies.getWolf().getUuid())).thenReturn(true);
        when(this.sceneLocalizedRepository.findAllBySceneUuid(this.dummyMovies.getWolfSceneOne().getUuid())).thenReturn(
                Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            this.sceneLocalizationService.getSceneLocalizationDto(this.dummyMovies.getWolfSceneOne().getUuid(),
                    this.dummyMovies.getWolf().getUuid());
        });

        assertThat(exception.getMessage()).isEqualTo("No localized scenes found");
        verify(this.movieRepository, times(1)).existsByUuid(this.dummyMovies.getWolf().getUuid());
        verify(this.sceneLocalizedRepository, times(1)).findAllBySceneUuid(
                this.dummyMovies.getWolfSceneOne().getUuid());
    }

}
