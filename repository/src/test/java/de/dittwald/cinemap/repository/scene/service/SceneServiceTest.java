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

import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.exceptions.UuidInUseException;
import de.dittwald.cinemap.repository.util.DummyData;
import de.dittwald.cinemap.repository.movie.repository.MovieRepository;
import de.dittwald.cinemap.repository.scene.repository.SceneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@WebMvcTest({SceneService.class})
@AutoConfigureMockMvc
class SceneServiceTest {

    @Autowired
    private SceneService sceneService;

    @MockBean
    private SceneRepository sceneRepository;

    @MockBean
    private MovieRepository movieRepository;

    private DummyData dummyData;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyData = new DummyData();
    }

    @Test
    void shouldFindMovieSceneByUuid() throws LocaleNotFoundException, NotFoundException {
        when(this.sceneRepository.findByUuid(this.dummyData.getWolfSceneOne().getUuid())).thenReturn(
                Optional.of(this.dummyData.getWolfSceneOne()));
        assertThat(this.sceneService.findByUuid(this.dummyData.getWolfSceneOne().getUuid(), "en")).isEqualTo(
                this.dummyData.getWolfSceneOneFlatEnDto());
        verify(this.sceneRepository, times(1)).findByUuid(this.dummyData.getWolfSceneOne().getUuid());
    }

    @Test
    void shouldFailFindMovieSceneByUuidAndShouldThrowException() {
        UUID uuid = UUID.randomUUID();
        when(this.sceneRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> this.sceneService.findByUuid(uuid, "en"));

        assertThat(exception.getMessage()).isEqualTo("Scene not found");
        verify(this.sceneRepository, times(1)).findByUuid(uuid);
    }

    @Test
    void shouldUpdateScene() throws NotFoundException {
        when(this.movieRepository.findByUuid(this.dummyData.getWolf().getUuid())).thenReturn(
                Optional.of(this.dummyData.getWolf()));
        when(this.sceneRepository.findByUuid(this.dummyData.getWolfSceneOne().getUuid())).thenReturn(
                Optional.of(this.dummyData.getWolfSceneOne()));
        this.sceneService.update(this.dummyData.getWolfSceneOneCreationEnDto(), this.dummyData.getWolf().getUuid(),
                this.dummyData.getWolfSceneOne().getUuid());
        verify(this.movieRepository, times(1)).findByUuid(this.dummyData.getWolf().getUuid());
        verify(this.sceneRepository, times(1)).findByUuid(this.dummyData.getWolfSceneOne().getUuid());
    }

    @Test
    void shouldUpdateSceneDueToMovieNotFound() {
        when(this.movieRepository.findByUuid(this.dummyData.getWolf().getUuid())).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class,
                () -> this.sceneService.update(this.dummyData.getWolfSceneOneCreationEnDto(),
                        this.dummyData.getWolf().getUuid(), this.dummyData.getWolfSceneOne().getUuid()));
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
        verify(this.movieRepository, times(1)).findByUuid(this.dummyData.getWolf().getUuid());
    }

    @Test
    void shouldUpdateSceneDueToSceneNotFound() {
        when(this.movieRepository.findByUuid(this.dummyData.getWolf().getUuid())).thenReturn(
                Optional.of(this.dummyData.getWolf()));
        when(this.sceneRepository.findByUuid(this.dummyData.getWolfSceneOne().getUuid())).thenReturn(
                Optional.empty());
        Exception exception = assertThrows(NotFoundException.class,
                () -> this.sceneService.update(this.dummyData.getWolfSceneOneCreationEnDto(),
                        this.dummyData.getWolf().getUuid(), this.dummyData.getWolfSceneOne().getUuid()));
        assertThat(exception.getMessage()).isEqualTo("Scene not found");
        verify(this.movieRepository, times(1)).findByUuid(this.dummyData.getWolf().getUuid());
        verify(this.sceneRepository, times(1)).findByUuid(this.dummyData.getWolfSceneOne().getUuid());
    }

    @Test
    void shouldSaveScene() throws UuidInUseException, NotFoundException {
        when(this.movieRepository.findByUuid(this.dummyData.getWolf().getUuid())).thenReturn(Optional.of(this.dummyData.getWolf()));
        when(this.sceneRepository.existsByUuid(this.dummyData.getWolfSceneOne().getUuid())).thenReturn(false);

        this.sceneService.save(this.dummyData.getWolfSceneOneCreationEnDto(), this.dummyData.getWolf().getUuid());

        verify(this.movieRepository, times(1)).findByUuid(this.dummyData.getWolf().getUuid());
        verify(this.sceneRepository, times(1)).existsByUuid(this.dummyData.getWolfSceneOne().getUuid());
    }

    @Test
    void shouldFailSaveSceneDueToMovieNotFound() {
        when(this.movieRepository.findByUuid(this.dummyData.getWolf().getUuid())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> this.sceneService.save(this.dummyData.getWolfSceneOneCreationEnDto(),
                        this.dummyData.getWolf().getUuid()));

        assertThat(exception.getMessage()).isEqualTo("Movie not found");

        verify(this.movieRepository, times(1)).findByUuid(this.dummyData.getWolf().getUuid());
    }

    @Test
    void shouldFailSaveSceneDueToSceneAlreadyExists() {
        when(this.movieRepository.findByUuid(this.dummyData.getWolf().getUuid())).thenReturn(Optional.of(this.dummyData.getWolf()));
        when(this.sceneRepository.existsByUuid(this.dummyData.getWolfSceneOne().getUuid())).thenReturn(true);

        Exception exception = assertThrows(UuidInUseException.class,
                () -> this.sceneService.save(this.dummyData.getWolfSceneOneCreationEnDto(),
                        this.dummyData.getWolf().getUuid()));

        assertThat(exception.getMessage()).isEqualTo("UUID already in use");

        verify(this.movieRepository, times(1)).findByUuid(this.dummyData.getWolf().getUuid());
        verify(this.sceneRepository, times(1)).existsByUuid(this.dummyData.getWolfSceneOne().getUuid());
    }

    @Test
    void shouldFindAllMovieScenes() throws LocaleNotFoundException {
        when(this.sceneRepository.findAll()).thenReturn(
                List.of(this.dummyData.getWolfSceneOne(), this.dummyData.getWolfSceneTwo()));
        assertThat(this.sceneService.findAll("en").size()).isEqualTo(2);
        verify(this.sceneRepository, times(1)).findAll();
    }

    @Test
    void shouldFailFindAllMovieScenesDueTwoLocaleNotFound() {
        when(this.sceneRepository.findAll()).thenReturn(
                List.of(this.dummyData.getWolfSceneOne(), this.dummyData.getWolfSceneTwo()));

        Exception exception = assertThrows(LocaleNotFoundException.class, () -> this.sceneService.findAll("nl"));

        assertThat(exception.getMessage()).isEqualTo(
                "Locale \"nl\" not found for scene %s".formatted(this.dummyData.getWolfSceneOne()));

        verify(this.sceneRepository, times(1)).findAll();
    }

    @Test
    void shouldDeleteByUuid() throws NotFoundException {
        UUID uuid = UUID.randomUUID();
        when(this.sceneRepository.existsByUuid(uuid)).thenReturn(true);
        when(this.sceneRepository.existsByUuid(uuid)).thenReturn(true);
        this.sceneService.deleteByUuid(uuid);
        verify(this.sceneRepository, times(1)).existsByUuid(uuid);
        verify(this.sceneRepository, times(1)).deleteByUuid(uuid);
    }

    @Test
    void shouldFailDeleteByUuidAndShouldThrowException() {
        UUID uuid = UUID.randomUUID();
        when(this.sceneRepository.existsByUuid(uuid)).thenReturn(false);
        Exception exception = assertThrows(NotFoundException.class, () -> this.sceneService.deleteByUuid(uuid));
        assertThat(exception.getMessage()).isEqualTo("Scene not found");
        verify(this.sceneRepository, times(1)).existsByUuid(uuid);
    }

    @Test
    void shouldDeleteAllMovieScenes() {
        doNothing().when(this.sceneRepository).deleteAll();
        this.sceneService.deleteAll();
        verify(this.sceneRepository, times(1)).deleteAll();
    }

    @Test
    void shouldFindAllSceneOfMovie() throws NotFoundException, LocaleNotFoundException {
        when(this.movieRepository.existsByUuid(this.dummyData.getWolf().getUuid())).thenReturn(true);
        when(this.sceneRepository.findAllScenesOfMovie(this.dummyData.getWolf().getUuid())).thenReturn(
                Optional.of(List.of(this.dummyData.getWolfSceneOne())));

        assertThat(this.sceneService.findAllScenesOfMovie(this.dummyData.getWolf().getUuid(), "en")).isEqualTo(
                List.of(this.dummyData.getWolfSceneOneFlatEnDto()));

        verify(this.movieRepository, times(1)).existsByUuid(this.dummyData.getWolf().getUuid());
        verify(this.sceneRepository, times(1)).findAllScenesOfMovie(this.dummyData.getWolf().getUuid());
    }

    @Test
    void shouldFailFindAllSceneOfMovieDueToNotFoundMovie() throws NotFoundException, LocaleNotFoundException {
        when(this.movieRepository.existsByUuid(this.dummyData.getWolf().getUuid())).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class,
                () -> this.sceneService.findAllScenesOfMovie(this.dummyData.getWolf().getUuid(), "en"));

        verify(this.movieRepository, times(1)).existsByUuid(this.dummyData.getWolf().getUuid());
    }
}