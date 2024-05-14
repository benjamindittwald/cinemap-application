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

package de.dittwald.cinemap.repository.scene.util;

import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.movie.DummyMovies;
import de.dittwald.cinemap.repository.scene.dto.SceneFlatDto;
import de.dittwald.cinemap.repository.scene.entity.Scene;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class LocalizedSceneDtoMapperTest {

    private DummyMovies dummyMovies;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyMovies = new DummyMovies();
    }

    @Test
    void entityToDto() throws LocaleNotFoundException {
        SceneFlatDto sceneFlatDto = LocalizedSceneDtoMapper.entityToDto(this.dummyMovies.getWolfSceneOne(), "en");
        assertEquals(this.dummyMovies.getWolfSceneOne().getUuid(), sceneFlatDto.uuid());
    }

    @Test
    void dtoToEntity() {
        Scene scene = LocalizedSceneDtoMapper.dtoToEntity(this.dummyMovies.getWolfSceneOneFlatEnDto());
        assertEquals(this.dummyMovies.getWolfSceneOne(), scene);
    }
}