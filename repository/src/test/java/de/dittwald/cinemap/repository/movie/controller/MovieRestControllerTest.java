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


import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.movie.DummyMovies;
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

    private DummyMovies dummyMovies;


    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyMovies = new DummyMovies();
    }

    @Test
    public void shouldReturnStatus404DueToNotCoveredUrlPath() throws Exception {
        this.mockMvc.perform(get("/api/v1/movie-fake")).andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindAllMoviesDefaultLocale() throws Exception {

        when(this.movieService.findAll("en")).thenReturn(
                List.of(this.dummyMovies.getWolfFlatEnDto(), this.dummyMovies.getNobodyFlatEnDto()));

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
                List.of(this.dummyMovies.getWolfFlatDeDto(), this.dummyMovies.getNobodyFlatDeDto()));

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
        when(this.movieService.findByUuid(this.dummyMovies.getWolfFlatEnDto().uuid(), "en")).thenReturn(
                this.dummyMovies.getWolfFlatEnDto());
        this.mockMvc.perform(get("/api/v1/movies/" + this.dummyMovies.getWolfFlatEnDto().uuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Dances with Wolves - Title")));
        verify(this.movieService, times(1)).findByUuid(this.dummyMovies.getWolfFlatEnDto().uuid(), "en");
    }

    @Test
    public void shouldFindMovieByUuidDe() throws Exception {
        when(this.movieService.findByUuid(this.dummyMovies.getWolfFlatDeDto().uuid(), "de")).thenReturn(
                this.dummyMovies.getWolfFlatDeDto());
        this.mockMvc.perform(get("/api/v1/movies/" + this.dummyMovies.getWolfFlatDeDto().uuid() + "?lang=de"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Der mit dem Wolf tanzt - Title")));
        verify(this.movieService, times(1)).findByUuid(this.dummyMovies.getWolfFlatDeDto().uuid(), "de");
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
                .update(this.dummyMovies.getWolfFlatEnDto(), this.dummyMovies.getWolfFlatEnDto().uuid());

        this.mockMvc.perform(put("/api/v1/movies/" + this.dummyMovies.getWolfFlatEnDto().uuid()).contentType(
                        MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyMovies.getValidWolfEnDtoJson)).andExpect(status().isNoContent());
        verify(this.movieService, times(1)).update(this.dummyMovies.getWolfFlatEnDto(),
                this.dummyMovies.getWolfFlatEnDto().uuid());
    }

    @Test
    public void shouldFailToUpdateMovieDueToInvalidLocale() throws Exception {
        this.mockMvc.perform(
                put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.dummyMovies.getInvalidLocaleWolfEnDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailToUpdateMovieDueToInvalidImdbWebsiteUrl() throws Exception {
        this.mockMvc.perform(
                put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.dummyMovies.getInvalidPosterUrlWolfEnDtoJson)).andExpect(status().isBadRequest());
    }


    @Test
    public void shouldFailUpdateMovieDueToNotExisingMovie() throws Exception {
        doThrow(new NotFoundException("Movie not found")).when(this.movieService)
                .update(this.dummyMovies.getWolfFlatEnDto(), this.dummyMovies.getWolfFlatEnDto().uuid());
        this.mockMvc.perform(
                put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.dummyMovies.getValidWolfEnDtoJson)).andExpect(status().isNotFound());
        verify(this.movieService, times(1)).update(this.dummyMovies.getWolfFlatEnDto(),
                this.dummyMovies.getWolfFlatEnDto().uuid());
    }

    @Test
    public void shouldFailUpdateMovieDueToInvalidUuid() throws Exception {
        String invalidUuid = "aa7acd67-4052-a63f-90440c683e6d";
        this.mockMvc.perform(put("/api/v1/movies/" + invalidUuid).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyMovies.getValidWolfEnDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailUpdateMovieDueToInvalidJsonUuid() throws Exception {
        this.mockMvc.perform(put("/api/v1/movies/" + this.dummyMovies.getWolfFlatEnDto().uuid()).contentType(
                        MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyMovies.getInvalidUuiWolfEnDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCreateMovie() throws Exception {

        doNothing().when(this.movieService).save(this.dummyMovies.getWolfFlatEnDto());

        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyMovies.getValidWolfEnDtoJson)).andExpect(status().isNoContent());
        verify(this.movieService, times(1)).save(this.dummyMovies.getWolfFlatEnDto());
    }

    @Test
    public void shouldFailCreateMovieDueToMissingUuid() throws Exception {
        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyMovies.getInValidMissingUuidWolfEnDtoJson)).andExpect(status().isBadRequest());
    }


    @Test
    public void shouldFailCreateMovieDueToInvalidUuid() throws Exception {
        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyMovies.getGetInvalidUuiWolfEnDtoJson())).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailCreateMovieDueToInvalidLocale() throws Exception {
        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.dummyMovies.getInvalidLocaleWolfEnDtoJson)).andExpect(status().isBadRequest());
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

    //
    //    @Test
    //    public void shouldCreateNewMovieScene() throws Exception {
    //        when(this.movieSceneService.save(this.movieSceneOnlyDto,
    //                UUID.fromString("b2989ce9-eddc-4772-b32c-5c26cb255a9e"))).thenReturn(this.movieSceneDtos
    //                .getFirst());
    //        this.mockMvc.perform(post("/api/v1/movies/b2989ce9-eddc-4772-b32c-5c26cb255a9e/scenes").contentType(
    //                                MediaType.APPLICATION_JSON)
    //                        .accept(MediaType.APPLICATION_JSON)
    //                        .characterEncoding("UTF-8")
    //                        .content(this.movieSceneOnlyDtoJson))
    //                .andExpect(status().isCreated())
    //                .andExpect(jsonPath("$.description.eng").value("Dances with Wolves scene 1"));
    //
    //        verify(this.movieSceneService, times(1)).save(this.movieSceneOnlyDto,
    //                UUID.fromString("b2989ce9-eddc-4772-b32c-5c26cb255a9e"));
    //    }
    //
    //    @Test
    //    public void shouldFailToCreateNewMovieSceneDueToMovieNotExist() throws Exception {
    //        UUID uuid = UUID.randomUUID();
    //        when(this.movieSceneService.save(this.movieSceneOnlyDto, uuid)).thenThrow(NotFoundException.class);
    //        this.mockMvc.perform(post("/api/v1/movies/" + uuid + "/scenes").contentType(MediaType.APPLICATION_JSON)
    //                .accept(MediaType.APPLICATION_JSON)
    //                .characterEncoding("UTF-8")
    //                .content(this.movieSceneOnlyDtoJson)).andExpect(status().isNotFound());
    //
    //        verify(this.movieSceneService, times(1)).save(this.movieSceneOnlyDto, uuid);
    //    }
    //
    //    @Test
    //    public void shouldFailToCreateNewMovieSceneDueToUuidAlreadyExist() throws Exception {
    //        UUID uuid = UUID.randomUUID();
    //        when(this.movieSceneService.save(this.movieSceneOnlyDto, uuid)).thenThrow(UuidInUseException.class);
    //        this.mockMvc.perform(post("/api/v1/movies/" + uuid + "/scenes").contentType(MediaType.APPLICATION_JSON)
    //                .accept(MediaType.APPLICATION_JSON)
    //                .characterEncoding("UTF-8")
    //                .content(this.movieSceneOnlyDtoJson)).andExpect(status().isBadRequest());
    //
    //        verify(this.movieSceneService, times(1)).save(this.movieSceneOnlyDto, uuid);
    //    }
    //
    //    @Test
    //    public void shouldFailToCreateNewMoveSceneDueToPathVariableMovieUuidInvalid() throws Exception {
    //        String invalidUuid = "b2989ce9-eddc-b32c-5c26cb255a9e";
    //        this.mockMvc.perform(post("/api/v1/movies/" + invalidUuid + "/scenes").contentType(MediaType
    //        .APPLICATION_JSON)
    //                .accept(MediaType.APPLICATION_JSON)
    //                .characterEncoding("UTF-8")
    //                .content(this.movieSceneOnlyDtoJson)).andExpect(status().isBadRequest());
    //    }
    //
    @Test
    public void shouldDeleteAllMovies() throws Exception {
        doNothing().when(this.movieService).deleteAll();
        this.mockMvc.perform(delete("/api/v1/movies")).andExpect(status().isNoContent());
        verify(this.movieService, times(1)).deleteAll();
    }
    //
    //    @Test
    //    public void shouldUpdateMovieScene() throws Exception {
    //        UUID movieUuid = this.movieDtos.getFirst().uuid();
    //        UUID sceneUuid = this.movieSceneOnlyDto.uuid();
    //        when(this.movieSceneService.update(this.movieSceneOnlyDto, movieUuid, sceneUuid)).thenReturn(
    //                this.movieSceneDtos.getFirst());
    //        this.mockMvc.perform(
    //                put("/api/v1/movies/" + movieUuid + "/scenes/" + sceneUuid).contentType(MediaType
    //                .APPLICATION_JSON)
    //                        .accept(MediaType.APPLICATION_JSON)
    //                        .characterEncoding("UTF-8")
    //                        .content(this.movieSceneOnlyDtoJson)).andExpect(status().isOk());
    //        verify(this.movieSceneService, times(1)).update(this.movieSceneOnlyDto, movieUuid, sceneUuid);
    //    }
    //
    //    @Test
    //    public void shouldFailToUpdateMovieSceneDueToMovieOrSceneDoesNotExist() throws Exception {
    //        UUID movieUuid = this.movieDtos.getFirst().uuid();
    //        UUID sceneUuid = this.movieSceneOnlyDto.uuid();
    //        when(this.movieSceneService.update(this.movieSceneOnlyDto, movieUuid, sceneUuid)).thenThrow(
    //                NotFoundException.class);
    //        this.mockMvc.perform(
    //                put("/api/v1/movies/" + movieUuid + "/scenes/" + sceneUuid).contentType(MediaType
    //                .APPLICATION_JSON)
    //                        .accept(MediaType.APPLICATION_JSON)
    //                        .characterEncoding("UTF-8")
    //                        .content(this.movieSceneOnlyDtoJson)).andExpect(status().isNotFound());
    //        verify(this.movieSceneService, times(1)).update(this.movieSceneOnlyDto, movieUuid, sceneUuid);
    //    }
    //
    //    @Test
    //    public void shouldDeleteMovieScene() throws Exception {
    //        UUID movieSceneUuid = this.movieSceneOnlyDto.uuid();
    //        UUID movieUuid = this.movieDtos.getFirst().uuid();
    //        doNothing().when(this.movieSceneService).deleteByUuid(movieSceneUuid);
    //        this.mockMvc.perform(delete("/api/v1/movies/" + movieUuid + "/scenes/" + movieSceneUuid))
    //                .andExpect(status().isNoContent());
    //        verify(this.movieSceneService, times(1)).deleteByUuid(movieSceneUuid);
    //    }
    //
    //    @Test
    //    public void shouldFailDeleteMovieSceneDueToMovieSceneNotFound() throws Exception {
    //        UUID movieSceneUuid = this.movieSceneOnlyDto.uuid();
    //        UUID movieUuid = this.movieDtos.getFirst().uuid();
    //        doThrow(new NotFoundException("Movie scene not found")).when(this.movieSceneService)
    //                .deleteByUuid(movieSceneUuid);
    //        this.mockMvc.perform(delete("/api/v1/movies/" + movieUuid + "/scenes/" + movieSceneUuid))
    //                .andExpect(status().isNotFound());
    //        verify(this.movieSceneService, times(1)).deleteByUuid(movieSceneUuid);
    //    }
    //
    //    @Test
    //    public void shouldFindMovieSceneByUuid() throws Exception {
    //        UUID movieSceneUuid = this.movieSceneOnlyDto.uuid();
    //        UUID movieUuid = this.movieDtos.getFirst().uuid();
    //        when(this.movieSceneService.findByUuid(movieSceneUuid)).thenReturn(this.movieSceneDtos.getFirst());
    //        this.mockMvc.perform(get("/api/v1/movies/" + movieUuid + "/scenes/" + movieSceneUuid))
    //                .andExpect(status().isOk())
    //                .andExpect(jsonPath("$.uuid", is(movieSceneUuid.toString())));
    //        verify(this.movieSceneService, times(1)).findByUuid(movieSceneUuid);
    //    }
    //
    //    @Test
    //    public void shouldFailFindMovieSceneByUuidDueToNotExistingUuid() throws Exception {
    //        UUID movieSceneUuid = this.movieSceneOnlyDto.uuid();
    //        UUID movieUuid = this.movieDtos.getFirst().uuid();
    //        when(this.movieSceneService.findByUuid(movieSceneUuid)).thenThrow(NotFoundException.class);
    //        this.mockMvc.perform(get("/api/v1/movies/" + movieUuid + "/scenes/" + movieSceneUuid))
    //                .andExpect(status().isNotFound());
    //        verify(this.movieSceneService, times(1)).findByUuid(movieSceneUuid);
    //    }
    //
    //    @Test
    //    public void shouldFindAllMovieScenesOfMovie() throws Exception {
    //        UUID movieUuid = this.movieDtos.getFirst().uuid();
    //        when(this.movieSceneService.findAllScenesOfMovie(movieUuid)).thenReturn(this.movieSceneDtos);
    //        this.mockMvc.perform(get("/api/v1/movies/" + movieUuid + "/scenes"))
    //                .andExpect(status().isOk())
    //                .andExpect(jsonPath("$[0].uuid", is(this.movieSceneDtos.getFirst().uuid().toString())));
    //        verify(this.movieSceneService, times(1)).findAllScenesOfMovie(movieUuid);
    //    }
}