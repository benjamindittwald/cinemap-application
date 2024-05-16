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

package de.dittwald.cinemap.repository.movie.controller;


import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.exceptions.UuidInUseException;
import de.dittwald.cinemap.repository.util.DummyData;
import de.dittwald.cinemap.repository.movie.service.MovieService;
import de.dittwald.cinemap.repository.scene.service.SceneService;
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
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieRestController.class)
@AutoConfigureMockMvc
public class MovieRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @MockBean
    private SceneService sceneService;

    private DummyData dummyData;


    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyData = new DummyData();
    }

    @Test
    public void shouldReturnStatus404DueToNotCoveredUrlPath() throws Exception {
        this.mockMvc.perform(get("/api/v1/movie-fake")).andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindAllMoviesDefaultLocale() throws Exception {

        when(this.movieService.findAll("en")).thenReturn(
                List.of(this.dummyData.getWolfFlatEnDto(), this.dummyData.getNobodyFlatEnDto()));

        this.mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Dances with Wolves - Title")))
                .andExpect(jsonPath("$[1].title", is("My Name is Nobody - Title")));

        verify(this.movieService, times(1)).findAll("en");
    }

    @Test
    public void shouldFindAllMoviesDe() throws Exception {

        when(this.movieService.findAll("de")).thenReturn(
                List.of(this.dummyData.getWolfFlatDeDto(), this.dummyData.getNobodyFlatDeDto()));

        this.mockMvc.perform(get("/api/v1/movies?lang=de"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Der mit dem Wolf tanzt - Title")))
                .andExpect(jsonPath("$[1].title", is("Mein Name ist Nobody - Title")));

        verify(this.movieService, times(1)).findAll("de");
    }

    @Test
    public void shouldFailFindAllMoviesDueToInvalidLocale() throws Exception {
        this.mockMvc.perform(get("/api/v1/movies?lang=deu")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFindMovieByUuidWithDefaultLocale() throws Exception {
        when(this.movieService.findByUuid(this.dummyData.getWolfFlatEnDto().uuid(), "en")).thenReturn(
                this.dummyData.getWolfFlatEnDto());
        this.mockMvc.perform(get("/api/v1/movies/" + this.dummyData.getWolfFlatEnDto().uuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Dances with Wolves - Title")));
        verify(this.movieService, times(1)).findByUuid(this.dummyData.getWolfFlatEnDto().uuid(), "en");
    }

    @Test
    public void shouldFindMovieByUuidDe() throws Exception {
        when(this.movieService.findByUuid(this.dummyData.getWolfFlatDeDto().uuid(), "de")).thenReturn(
                this.dummyData.getWolfFlatDeDto());
        this.mockMvc.perform(get("/api/v1/movies/" + this.dummyData.getWolfFlatDeDto().uuid() + "?lang=de"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Der mit dem Wolf tanzt - Title")));
        verify(this.movieService, times(1)).findByUuid(this.dummyData.getWolfFlatDeDto().uuid(), "de");
    }

    @Test
    public void shouldFailFindMovieDueToNotExistingUuid() throws Exception {
        when(this.movieService.findByUuid(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"), "en")).thenThrow(
                NotFoundException.class);
        this.mockMvc.perform(get("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d"))
                .andExpect(status().isNotFound());
        verify(this.movieService, times(1)).findByUuid(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"), "en");
    }

    @Test
    public void shouldFailFindMovieDueToInvalidLocale() throws Exception {
        this.mockMvc.perform(get("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d?lang=deu"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailFindByUuidDueToInvalidUuid() throws Exception {
        String invalidUuid = "aa7acd67-4052-a63f-90440c683e6d";
        this.mockMvc.perform(get("/api/v1/movies/" + invalidUuid)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldUpdateMovie() throws Exception {

        doNothing().when(this.movieService)
                .update(this.dummyData.getWolfFlatEnDto(), this.dummyData.getWolfFlatEnDto().uuid());

        this.mockMvc.perform(put("/api/v1/movies/" + this.dummyData.getWolfFlatEnDto().uuid()).contentType(
                        MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getValidWolfEnDtoJson)).andExpect(status().isNoContent());
        verify(this.movieService, times(1)).update(this.dummyData.getWolfFlatEnDto(),
                this.dummyData.getWolfFlatEnDto().uuid());
    }

    @Test
    public void shouldFailToUpdateMovieDueToInvalidLocale() throws Exception {
        this.mockMvc.perform(
                put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.dummyData.getInvalidLocaleWolfEnDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailToUpdateMovieDueToInvalidImdbWebsiteUrl() throws Exception {
        this.mockMvc.perform(
                put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.dummyData.getInvalidPosterUrlWolfEnDtoJson)).andExpect(status().isBadRequest());
    }


    @Test
    public void shouldFailUpdateMovieDueToNotExisingMovie() throws Exception {
        doThrow(new NotFoundException("Movie not found")).when(this.movieService)
                .update(this.dummyData.getWolfFlatEnDto(), this.dummyData.getWolfFlatEnDto().uuid());
        this.mockMvc.perform(
                put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.dummyData.getValidWolfEnDtoJson)).andExpect(status().isNotFound());
        verify(this.movieService, times(1)).update(this.dummyData.getWolfFlatEnDto(),
                this.dummyData.getWolfFlatEnDto().uuid());
    }

    @Test
    public void shouldFailUpdateMovieDueToInvalidUuid() throws Exception {
        String invalidUuid = "aa7acd67-4052-a63f-90440c683e6d";
        this.mockMvc.perform(put("/api/v1/movies/" + invalidUuid).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getValidWolfEnDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailUpdateMovieDueToInvalidJsonUuid() throws Exception {
        this.mockMvc.perform(put("/api/v1/movies/" + this.dummyData.getWolfFlatEnDto().uuid()).contentType(
                        MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getInvalidUuiWolfEnDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCreateMovie() throws Exception {

        doNothing().when(this.movieService).save(this.dummyData.getWolfFlatEnDto());

        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getValidWolfEnDtoJson)).andExpect(status().isNoContent());
        verify(this.movieService, times(1)).save(this.dummyData.getWolfFlatEnDto());
    }

    @Test
    public void shouldFailCreateMovieDueToUuidAlreadyExist() throws Exception {

        doThrow(UuidInUseException.class).when(this.movieService).save(this.dummyData.getWolfFlatEnDto());

        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getValidWolfEnDtoJson)).andExpect(status().isConflict());
        verify(this.movieService, times(1)).save(this.dummyData.getWolfFlatEnDto());
    }

    @Test
    public void shouldFailCreateMovieDueToMissingUuid() throws Exception {
        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getInValidMissingUuidWolfEnDtoJson)).andExpect(status().isBadRequest());
    }


    @Test
    public void shouldFailCreateMovieDueToInvalidUuid() throws Exception {
        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getGetInvalidUuiWolfEnDtoJson())).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailCreateMovieDueToInvalidLocale() throws Exception {
        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getInvalidLocaleWolfEnDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteMovie() throws Exception {
        doNothing().when(this.movieService).deleteByUuid(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"));

        this.mockMvc.perform(delete("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d"))
                .andExpect(status().isNoContent());

        verify(this.movieService, times(1)).deleteByUuid(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"));
    }

    @Test
    public void shouldFailDeleteMovieDueToInvalidUuid() throws Exception {
        String invalidUuid = "aa7acd67-4052-421d-90440c683e6d";
        this.mockMvc.perform(delete("/api/v1/movies/" + invalidUuid)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailDeleteDueToNotExistingMovieUuid() throws Exception {
        doThrow(new NotFoundException("Movie not found")).when(this.movieService)
                .deleteByUuid(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"));
        this.mockMvc.perform(delete("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d"))
                .andExpect(status().isNotFound());
        verify(this.movieService, times(1)).deleteByUuid(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"));
    }


    @Test
    public void shouldCreateNewScene() throws Exception {
        doNothing().when(this.sceneService)
                .save(this.dummyData.getWolfSceneOneFlatEnDto(), this.dummyData.getWolf().getUuid());
        this.mockMvc.perform(post("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes").contentType(
                        MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getValidRequestBodyWolfSceneOneEnJson)).andExpect(status().isCreated());

        verify(this.sceneService, times(1)).save(this.dummyData.getWolfSceneOneFlatEnDto(),
                this.dummyData.getWolf().getUuid());
    }

    @Test
    public void shouldFailCreateNewSceneDueToMovieNotFound() throws Exception {
        doThrow(NotFoundException.class).when(this.sceneService)
                .save(this.dummyData.getWolfSceneOneFlatEnDto(), this.dummyData.getWolf().getUuid());
        this.mockMvc.perform(post("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes").contentType(
                        MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getValidRequestBodyWolfSceneOneEnJson)).andExpect(status().isNotFound());

        verify(this.sceneService, times(1)).save(this.dummyData.getWolfSceneOneFlatEnDto(),
                this.dummyData.getWolf().getUuid());
    }

    @Test
    public void shouldFailCreateNewSceneDueToMovieUuidAlreadyExist() throws Exception {
        doThrow(UuidInUseException.class).when(this.sceneService)
                .save(this.dummyData.getWolfSceneOneFlatEnDto(), this.dummyData.getWolf().getUuid());
        this.mockMvc.perform(post("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes").contentType(
                        MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getValidRequestBodyWolfSceneOneEnJson)).andExpect(status().isConflict());

        verify(this.sceneService, times(1)).save(this.dummyData.getWolfSceneOneFlatEnDto(),
                this.dummyData.getWolf().getUuid());
    }

    @Test
    public void shouldFailCreateSceneDueToInvalidRequestBodyScene() throws Exception {
        this.mockMvc.perform(post("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes").contentType(
                                MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.dummyData.getInvalidSceneLocaleRequestBodyWolfSceneOneEnJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailCreateSceneDueToInvalidRequestBodyMovie() throws Exception {
        this.mockMvc.perform(post("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes").contentType(
                                MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.dummyData.getInvalidSceneLocaleRequestBodyWolfMovieOneEnJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteAllMovies() throws Exception {
        doNothing().when(this.movieService).deleteAll();
        this.mockMvc.perform(delete("/api/v1/movies")).andExpect(status().isNoContent());
        verify(this.movieService, times(1)).deleteAll();
    }

    @Test
    public void shouldUpdateScene() throws Exception {
        doNothing().when(this.sceneService)
                .update(this.dummyData.getWolfSceneOneFlatEnDto(), this.dummyData.getWolf().getUuid(),
                        this.dummyData.getWolfSceneOne().getUuid());

        this.mockMvc.perform(put("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes/" +
                this.dummyData.getWolfSceneOne().getUuid()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getValidRequestBodyWolfSceneOneEnJson)).andExpect(status().isOk());
        verify(this.sceneService, times(1)).update(this.dummyData.getWolfSceneOneFlatEnDto(),
                this.dummyData.getWolf().getUuid(), this.dummyData.getWolfSceneOne().getUuid());
    }

    @Test
    public void shouldFailUpdateSceneDueToMovieOrSceneNotExist() throws Exception {
        doThrow(NotFoundException.class).when(this.sceneService)
                .update(this.dummyData.getWolfSceneOneFlatEnDto(), this.dummyData.getWolf().getUuid(),
                        this.dummyData.getWolfSceneOne().getUuid());

        this.mockMvc.perform(put("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes/" +
                this.dummyData.getWolfSceneOne().getUuid()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyData.getValidRequestBodyWolfSceneOneEnJson)).andExpect(status().isNotFound());

        verify(this.sceneService, times(1)).update(this.dummyData.getWolfSceneOneFlatEnDto(),
                this.dummyData.getWolf().getUuid(), this.dummyData.getWolfSceneOne().getUuid());
    }

    @Test
    public void shouldFailUpdateSceneDueToMovieOrSceneRequestBodyIsInvalidScene() throws Exception {
        this.mockMvc.perform(put("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes/" +
                        this.dummyData.getWolfSceneOne().getUuid()).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.dummyData.getInvalidSceneLocaleRequestBodyWolfSceneOneEnJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailUpdateSceneDueToMovieOrSceneRequestBodyIsInvalidMovie() throws Exception {
        this.mockMvc.perform(put("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes/" +
                        this.dummyData.getWolfSceneOne().getUuid()).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.dummyData.getInvalidSceneLocaleRequestBodyWolfMovieOneEnJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void shouldDeleteScene() throws Exception {
        doNothing().when(this.sceneService).deleteByUuid(this.dummyData.getWolfSceneOne().getUuid());
        this.mockMvc.perform(delete("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes/" +
                this.dummyData.getWolfSceneOne().getUuid())).andExpect(status().isNoContent());
        verify(this.sceneService, times(1)).deleteByUuid(this.dummyData.getWolfSceneOne().getUuid());
    }

    @Test
    public void shouldFailDeleteSceneDueToNotFoundMovieOrScene() throws Exception {
        doThrow(NotFoundException.class).when(this.sceneService)
                .deleteByUuid(this.dummyData.getWolfSceneOne().getUuid());
        this.mockMvc.perform(delete("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes/" +
                this.dummyData.getWolfSceneOne().getUuid())).andExpect(status().isNotFound());
        verify(this.sceneService, times(1)).deleteByUuid(this.dummyData.getWolfSceneOne().getUuid());
    }


    @Test
    public void shouldFindSceneByUuid() throws Exception {
        when(this.sceneService.findByUuid(this.dummyData.getWolfSceneOne().getUuid(), "en")).thenReturn(
                this.dummyData.getWolfSceneOneFlatEnDto());
        this.mockMvc.perform(get("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes/" +
                        this.dummyData.getWolfSceneOne().getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(this.dummyData.getWolfSceneOne().getUuid().toString())));
        verify(this.sceneService, times(1)).findByUuid(this.dummyData.getWolfSceneOne().getUuid(), "en");
    }

    @Test
    public void shouldFailFindSceneByUuidDueToSceneNotFound() throws Exception {
        when(this.sceneService.findByUuid(this.dummyData.getWolfSceneOne().getUuid(), "en")).thenThrow(
                NotFoundException.class);
        this.mockMvc.perform(get("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes/" +
                this.dummyData.getWolfSceneOne().getUuid())).andExpect(status().isNotFound());
        verify(this.sceneService, times(1)).findByUuid(this.dummyData.getWolfSceneOne().getUuid(), "en");
    }

    @Test
    public void shouldFailFindSceneByUuidDueToLocaleNotFound() throws Exception {
        when(this.sceneService.findByUuid(this.dummyData.getWolfSceneOne().getUuid(), "en")).thenThrow(
                LocaleNotFoundException.class);
        this.mockMvc.perform(get("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes/" +
                this.dummyData.getWolfSceneOne().getUuid())).andExpect(status().isNotFound());
        verify(this.sceneService, times(1)).findByUuid(this.dummyData.getWolfSceneOne().getUuid(), "en");
    }

    @Test
    public void shouldFindAllScenesOfMovie() throws Exception {
        when(this.sceneService.findAllScenesOfMovie(this.dummyData.getWolf().getUuid(), "en")).thenReturn(
                List.of(this.dummyData.getWolfSceneOneFlatEnDto()));
        this.mockMvc.perform(get("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uuid", is(this.dummyData.getWolfSceneOneFlatEnDto().uuid().toString())));
        verify(this.sceneService, times(1)).findAllScenesOfMovie(this.dummyData.getWolf().getUuid(), "en");
    }

    @Test
    public void shouldFailFindAllScenesOfMovieDueToMovieNotFound() throws Exception {
        when(this.sceneService.findAllScenesOfMovie(this.dummyData.getWolf().getUuid(), "en")).thenThrow(
                NotFoundException.class);
        this.mockMvc.perform(get("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes"))
                .andExpect(status().isNotFound());
        verify(this.sceneService, times(1)).findAllScenesOfMovie(this.dummyData.getWolf().getUuid(), "en");
    }

    @Test
    public void shouldFailFindAllScenesOfMovieDueToLocaleNotFound() throws Exception {
        when(this.sceneService.findAllScenesOfMovie(this.dummyData.getWolf().getUuid(), "en")).thenThrow(
                LocaleNotFoundException.class);
        this.mockMvc.perform(get("/api/v1/movies/" + this.dummyData.getWolf().getUuid() + "/scenes"))
                .andExpect(status().isNotFound());
        verify(this.sceneService, times(1)).findAllScenesOfMovie(this.dummyData.getWolf().getUuid(), "en");
    }
}