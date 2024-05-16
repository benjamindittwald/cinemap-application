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
import de.dittwald.cinemap.repository.scene.service.SceneLocalizationService;
import de.dittwald.cinemap.repository.util.DummyData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SceneLocalizationRestController.class)
@AutoConfigureMockMvc
class SceneLocalizationRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SceneLocalizationService sceneLocalizationService;

    private DummyData dummyData;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyData = new DummyData();
    }

    @Test
    void shouldGetAllLocalizations() throws Exception {
        when(this.sceneLocalizationService.getSceneLocalizationDto(
                this.dummyData.getWolfSceneOne().getUuid())).thenReturn(
                this.dummyData.getWolfSceneOneLocalizationDto());

        this.mockMvc.perform(get("/api/v1/scenes/" + this.dummyData.getWolfSceneOne().getUuid() + "/localizations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sceneUuid", is(this.dummyData.getWolfSceneOne().getUuid().toString())))
                .andExpect(jsonPath("$.localizations[0].locale", is("en")))
                .andExpect(jsonPath("$.localizations[0].description", is("Dances with Wolves - Scene One Description")))
                .andExpect(jsonPath("$.localizations[1].locale", is("de")))
                .andExpect(jsonPath("$.localizations[1].description",
                        is("Der mit dem Wolf tanzt - Scene One Description")));

        verify(this.sceneLocalizationService, times(1)).getSceneLocalizationDto(
                this.dummyData.getWolfSceneOne().getUuid());
    }

    @Test
    void shouldFailGetAllLocalizationsDueToSceneNotFound() throws Exception {
        when(this.sceneLocalizationService.getSceneLocalizationDto(
                this.dummyData.getWolfSceneOne().getUuid())).thenThrow(NotFoundException.class);

        this.mockMvc.perform(get("/api/v1/scenes/" + this.dummyData.getWolfSceneOne().getUuid() + "/localizations"))
                .andExpect(status().isNotFound());

        verify(this.sceneLocalizationService, times(1)).getSceneLocalizationDto(
                this.dummyData.getWolfSceneOne().getUuid());
    }

    @Test
    void shouldUpdateSceneLocalizationsNoOverride() throws Exception {
        doNothing().when(this.sceneLocalizationService)
                .update(this.dummyData.getWolfSceneOneLocalizationDto(),
                        this.dummyData.getWolfSceneOneLocalizationDto().sceneUuid(), false);

        this.mockMvc.perform(put("/api/v1/scenes/" + this.dummyData.getWolfSceneOneLocalizationDto().sceneUuid() +
                "/localizations").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getValidWolfSceneLocalizationsJson)).andExpect(status().isNoContent());

        verify(this.sceneLocalizationService, times(1)).update(this.dummyData.getWolfSceneOneLocalizationDto(),
                this.dummyData.getWolfSceneOneLocalizationDto().sceneUuid(), false);
    }

    @Test
    void shouldUpdateSceneLocalizationsWithOverride() throws Exception {
        doNothing().when(this.sceneLocalizationService)
                .update(this.dummyData.getWolfSceneOneLocalizationDto(),
                        this.dummyData.getWolfSceneOneLocalizationDto().sceneUuid(), true);

        this.mockMvc.perform(put("/api/v1/scenes/" + this.dummyData.getWolfSceneOneLocalizationDto().sceneUuid() +
                "/localizations?override=true").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getValidWolfSceneLocalizationsJson)).andExpect(status().isNoContent());

        verify(this.sceneLocalizationService, times(1)).update(this.dummyData.getWolfSceneOneLocalizationDto(),
                this.dummyData.getWolfSceneOneLocalizationDto().sceneUuid(), true);
    }

    @Test
    void shouldFailUpdateSceneLocalizationsNoOverrideDueToSceneNotFound() throws Exception {
        doThrow(NotFoundException.class).when(this.sceneLocalizationService)
                .update(this.dummyData.getWolfSceneOneLocalizationDto(),
                        this.dummyData.getWolfSceneOneLocalizationDto().sceneUuid(), false);

        this.mockMvc.perform(put("/api/v1/scenes/" + this.dummyData.getWolfSceneOneLocalizationDto().sceneUuid() +
                "/localizations").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getValidWolfSceneLocalizationsJson)).andExpect(status().isNotFound());

        verify(this.sceneLocalizationService, times(1)).update(this.dummyData.getWolfSceneOneLocalizationDto(),
                this.dummyData.getWolfSceneOneLocalizationDto().sceneUuid(), false);
    }

}