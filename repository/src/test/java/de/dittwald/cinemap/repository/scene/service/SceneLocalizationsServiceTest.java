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
import de.dittwald.cinemap.repository.util.DummyData;
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

    private DummyData dummyData;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyData = new DummyData();
    }

    @Test
    void shouldUpdateSceneAndLocalizationsNoOverride() throws NotFoundException {
        when(this.sceneRepository.findByUuid(this.dummyData.getWolfSceneOne().getUuid())).thenReturn(
                Optional.of(this.dummyData.getWolfSceneOne()));

        this.sceneLocalizationService.update(this.dummyData.getWolfSceneOneLocalizationDto(),
                this.dummyData.getWolfSceneOne().getUuid(), false);

        verify(this.sceneRepository, times(1)).save(this.dummyData.getWolfSceneOne());
    }


    @Test
    void shouldFailUpdateSceneAndLocalizationsNoOverrideDueToNotFoundScene() {
        when(this.sceneRepository.findByUuid(this.dummyData.getWolfSceneOne().getUuid())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            this.sceneLocalizationService.update(this.dummyData.getWolfSceneOneLocalizationDto(),
                    this.dummyData.getWolfSceneOne().getUuid(), false);
        });
        assertThat(exception.getMessage()).isEqualTo("Scene not found");
        verify(this.sceneRepository, times(1)).findByUuid(this.dummyData.getWolfSceneOne().getUuid());
    }

    @Test
    void shouldFindAllLocalizationsForMovieUuid() throws NotFoundException {
        when(this.sceneLocalizedRepository.findAllBySceneUuid(this.dummyData.getWolfSceneOne().getUuid())).thenReturn(
                Optional.of(List.of(this.dummyData.getWolfLsOneEn(), this.dummyData.getWolfLsOneDe())));

        assertThat(this.sceneLocalizationService.getSceneLocalizationDto(
                this.dummyData.getWolfSceneOne().getUuid())).isEqualTo(this.dummyData.getWolfSceneOneLocalizationDto());

        verify(this.sceneLocalizedRepository, times(1)).findAllBySceneUuid(this.dummyData.getWolfSceneOne().getUuid());
    }

    @Test
    void shouldFailFindAllLocalizationsForMovieUuidDueToNotFoundScene() throws NotFoundException {
        when(this.sceneLocalizedRepository.findAllBySceneUuid(this.dummyData.getWolfSceneOne().getUuid())).thenReturn(
                Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            this.sceneLocalizationService.getSceneLocalizationDto(this.dummyData.getWolfSceneOne().getUuid());
        });

        assertThat(exception.getMessage()).isEqualTo("No localized scenes found");
        verify(this.sceneLocalizedRepository, times(1)).findAllBySceneUuid(this.dummyData.getWolfSceneOne().getUuid());
    }

}
