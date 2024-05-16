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

package de.dittwald.cinemap.repository.scene.controller;

import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.scene.service.SceneService;
import de.dittwald.cinemap.repository.util.ConstantStrings;
import de.dittwald.cinemap.repository.util.DummyData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SceneRestController.class)
@AutoConfigureMockMvc
public class SceneRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SceneService sceneService;

    private DummyData dummyData;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyData = new DummyData();
    }

    @Test
    public void shouldFindAllMovieScenes() throws Exception {
        when(this.sceneService.findAll("en")).thenReturn(List.of(this.dummyData.getWolfSceneOneFlatEnDto()));
        this.mockMvc.perform(get("/api/v1/scenes?" + ConstantStrings.LOCALE_API_REQUEST_PARAM + "=" +
                        ConstantStrings.DEFAULT_LOCALE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Dances with Wolves - Scene One Description"))
                .andExpect(jsonPath("$").isArray());
        verify(this.sceneService, times(1)).findAll(ConstantStrings.DEFAULT_LOCALE);
    }

//    @Test
//    public void shouldFindMovieSceneByUuid() throws Exception {
//        when(this.sceneService.findByUuid(this.dummyData.getWolfSceneOne().getUuid(), "en")).thenReturn(
//                this.dummyData.getWolfSceneOneFlatEnDto());
//        this.mockMvc.perform(get("/api/v1/scenes/" + this.dummyData.getWolfSceneOne().getUuid() + "?" +
//                        ConstantStrings.LOCALE_API_REQUEST_PARAM + "=en"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.uuid").value(this.dummyData.getWolfSceneOneFlatEnDto().uuid().toString()));
//        verify(this.sceneService, times(1)).findByUuid(this.dummyData.getWolfSceneOne().getUuid(), "en");
//    }
//
//    @Test
//    public void shouldNotFindMovieSceneByUuid() throws Exception {
//        when(this.sceneService.findByUuid(this.dummyData.getWolfSceneOne().getUuid(), "en")).thenThrow(
//                NotFoundException.class);
//        this.mockMvc.perform(get("/api/v1/scenes/" + this.dummyData.getWolfSceneOne().getUuid()))
//                .andExpect(status().isNotFound());
//        verify(this.sceneService, times(1)).findByUuid(this.dummyData.getWolfSceneOne().getUuid(), "en");
//    }
//
//    @Test
//    public void shouldDeleteSceneByUuid() throws Exception {
//        doNothing().when(this.sceneService).deleteByUuid(any());
//        this.mockMvc.perform(delete("/api/v1/scenes/" + this.dummyData.getWolfSceneOne().getUuid()))
//                .andExpect(status().isNoContent());
//        verify(this.sceneService, times(1)).deleteByUuid(this.dummyData.getWolfSceneOne().getUuid());
//    }
//
//    @Test
//    public void shouldFailDeleteSceneByUuidDueToSceneNotFound() throws Exception {
//        UUID uuid = UUID.randomUUID();
//        doThrow(NotFoundException.class).when(this.sceneService).deleteByUuid(uuid);
//        this.mockMvc.perform(delete("/api/v1/scenes/" + uuid)).andExpect(status().isNotFound());
//        verify(this.sceneService, times(1)).deleteByUuid(uuid);
//    }

    @Test
    public void shouldDeleteAllMovieScenes() throws Exception {
        doNothing().when(this.sceneService).deleteAll();
        this.mockMvc.perform(delete("/api/v1/scenes")).andExpect(status().isNoContent());
        verify(this.sceneService, times(1)).deleteAll();
    }
}
