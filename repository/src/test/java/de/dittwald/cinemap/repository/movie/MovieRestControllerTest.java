/*
 * Copyright 2024 Benjamin Dittwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dittwald.cinemap.repository.movie;

import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.exceptions.UuidInUseException;
import de.dittwald.cinemap.repository.moviescene.MovieSceneDto;
import de.dittwald.cinemap.repository.moviescene.MovieSceneOnlyDto;
import de.dittwald.cinemap.repository.moviescene.MovieSceneService;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
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
    private MovieSceneService movieSceneService;

    private List<MovieDto> movieDtos;

    private List<MovieSceneDto> movieSceneDtos;

    private String movieSceneOnlyDtoJson;

    private MovieSceneOnlyDto movieSceneOnlyDto;

    @BeforeEach
    void setUp() {
        this.movieDtos = new ArrayList<>();
        this.movieDtos.add(new MovieDto(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"),
                Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"), 1051896, 1970,
                Map.of("deu", "Der mit dem Wolf tanzt TAGLINE", "eng", "Dances with Wolves TAGLINE"),
                Map.of("deu", "Der mit dem Wolf tanzt OVERVIEW", "eng", "Dances with Wolves OVERVIEW"),
                Map.of(80, "western", 85, "Thriller"), ArrayUtils.toObject(Base64.getDecoder().decode("Imadummyfile")),
                "imdbId"));
        this.movieDtos.add(
                new MovieDto(UUID.randomUUID(), Map.of("deu", "Mein Name ist Nobody", "eng", "My Name is Nobody"),
                        1051896, 1970,
                        Map.of("deu", "Mein Name ist Nobody TAGLINE", "eng", "DMy Name is Nobody TAGLINE"),
                        Map.of("deu", "Mein Name ist Nobody OVERVIEW", "eng", "My Name is Nobody OVERVIEW"),
                        Map.of(80, "western", 85, "Thriller"), ArrayUtils.toObject("Test".getBytes()), "imdbId"));

        this.movieSceneDtos = new ArrayList<>();
        this.movieSceneDtos.add(
                new MovieSceneDto(UUID.fromString("b2989ce9-eddc-4772-b32c-5c26cb255a9e"), 13404954L, 52520008L,
                        Map.of("deu", "Der mit dem Wolf tanzt Szene 1", "eng", "Dances with Wolves scene 1"),
                        this.movieDtos.getFirst()));
        this.movieSceneDtos.add(new MovieSceneDto(UUID.randomUUID(), 13404954L, 52520008L,
                Map.of("deu", "Der mit dem Wolf tanzt Szene 2", "eng", "Dances with Wolves scene 2"),
                this.movieDtos.getFirst()));

        this.movieSceneOnlyDto =
                new MovieSceneOnlyDto(this.movieSceneDtos.getFirst().uuid(), this.movieSceneDtos.getFirst().lon(),
                        this.movieSceneDtos.getFirst().lat(), this.movieSceneDtos.getFirst().description());

        this.movieSceneOnlyDtoJson = """
                {
                    "uuid": "b2989ce9-eddc-4772-b32c-5c26cb255a9e",
                    "lon": "13404954",
                    "lat": "52520008",
                    "description":
                        {
                            "deu":"Der mit dem Wolf tanzt Szene 1",
                            "eng":"Dances with Wolves scene 1"
                        }
                }
                """;
    }

    @Test
    public void shouldReturnStatus404DueToNotCoveredUrlPath() throws Exception {
        this.mockMvc.perform(get("/api/v1/movie-fake")).andExpect(status().isNotFound());
    }

    @Test
    public void shouldFindAllMovies() throws Exception {

        when(this.movieService.findAll()).thenReturn(movieDtos);

        this.mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title.deu", is("Der mit dem Wolf tanzt")))
                .andExpect(jsonPath("$[1].title.deu", is("Mein Name ist Nobody")));

        verify(this.movieService, times(1)).findAll();
    }

    @Test
    public void shouldFindMovieByUuid() throws Exception {
        when(this.movieService.findByUuid(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"))).thenReturn(
                movieDtos.getFirst());
        this.mockMvc.perform(get("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title.deu", is("Der mit dem Wolf tanzt")));
        verify(this.movieService, times(1)).findByUuid(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"));
    }

    @Test
    public void shouldFailFindMovieDueToNotExistingUuid() throws Exception {
        when(this.movieService.findByUuid(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"))).thenThrow(
                NotFoundException.class);
        this.mockMvc.perform(get("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d"))
                .andExpect(status().isNotFound());
        verify(this.movieService, times(1)).findByUuid(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"));
    }

    @Test
    public void shouldFailFindByUuidDueToInvalidUuid() throws Exception {
        String invalidUuid = "aa7acd67-4052-a63f-90440c683e6d";
        this.mockMvc.perform(get("/api/v1/movies/" + invalidUuid)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldUpdateMovie() throws Exception {

        when(this.movieService.update(any(), any())).thenReturn(this.movieDtos.getFirst());

        this.mockMvc.perform(
                        put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(DummyMovieConstantsJson.WOLF_VALID_MOVIE_DTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is("aa7acd67-4052-421d-a63f-90440c683e6d")))
                .andExpect(jsonPath("$.title.deu", is("Der mit dem Wolf tanzt")))
                .andExpect(jsonPath("$.poster", is("Imadummyfile")));
        verify(this.movieService, times(1)).update(any(), any());
    }

    @Test
    public void shouldFailToUpdateMovieDueToInvalidIsoLang() throws Exception {

        String movieDtoJson = """
                {   "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                    "title":
                        {
                            "en":"Dances with Wolves",
                            "deu":"Der mit dem Wolf tanzt"
                        },
                    "tmdbId":"1051896"
                }
                """;

        this.mockMvc.perform(
                put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(movieDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailToUpdateMovieDueToInvalidImdbWebsiteUrl() throws Exception {

        String movieDtoJson = """
                {   "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                    "title":
                        {
                            "eng":"Dances with Wolves",
                            "deu":"Der mit dem Wolf tanzt"
                        },
                    "tmdbId":"1051896aa-INVALID"
                }
                """;

        this.mockMvc.perform(
                put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(movieDtoJson)).andExpect(status().isBadRequest());
    }


    @Test
    public void shouldFailUpdateMovieDueToNotExisingMovie() throws Exception {
        when(this.movieService.update(any(), any())).thenThrow(NotFoundException.class);
        this.mockMvc.perform(
                put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(DummyMovieConstantsJson.WOLF_VALID_MOVIE_DTO)).andExpect(status().isNotFound());
        verify(this.movieService, times(1)).update(any(), any());
    }

    @Test
    public void shouldFailUpdateMovieDueToInvalidUuid() throws Exception {
        String invalidUuid = "aa7acd67-4052-a63f-90440c683e6d";
        this.mockMvc.perform(put("/api/v1/movies/" + invalidUuid).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(DummyMovieConstantsJson.WOLF_INVALID_MOVIE_DTO_INVALID_UUID)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCreateMovie() throws Exception {

        when(this.movieService.save(any())).thenReturn(this.movieDtos.getFirst());

        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(DummyMovieConstantsJson.WOLF_VALID_MOVIE_DTO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title.eng").value("Dances with Wolves"));
        verify(this.movieService, times(1)).save(any());
    }

    @Test
    public void shouldFailCreateMovieDueToMissingUuid() throws Exception {
        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(DummyMovieConstantsJson.WOLF_INVALID_MOVIE_DTO_MISSING_UUID)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailCreateMovieDueToInvalidUuid() throws Exception {
        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(DummyMovieConstantsJson.WOLF_INVALID_MOVIE_DTO_INVALID_UUID)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailCreateMovieDueToNonIsoLang() throws Exception {
        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(DummyMovieConstantsJson.WOLF_INVALID_MOVIE_DTO_NON_ISO_LANG)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteMovie() throws Exception {
        MovieService movieServiceMock = mock(MovieService.class);
        doNothing().when(movieServiceMock).deleteByUuid(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"));

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
    public void shouldCreateNewMovieScene() throws Exception {
        when(this.movieSceneService.save(this.movieSceneOnlyDto,
                UUID.fromString("b2989ce9-eddc-4772-b32c-5c26cb255a9e"))).thenReturn(this.movieSceneDtos.getFirst());
        this.mockMvc.perform(post("/api/v1/movies/b2989ce9-eddc-4772-b32c-5c26cb255a9e/scenes").contentType(
                                MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.movieSceneOnlyDtoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description.eng").value("Dances with Wolves scene 1"));

        verify(this.movieSceneService, times(1)).save(this.movieSceneOnlyDto,
                UUID.fromString("b2989ce9-eddc-4772-b32c-5c26cb255a9e"));
    }

    @Test
    public void shouldFailToCreateNewMovieSceneDueToMovieNotExist() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(this.movieSceneService.save(this.movieSceneOnlyDto, uuid)).thenThrow(NotFoundException.class);
        this.mockMvc.perform(post("/api/v1/movies/" + uuid + "/scenes").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.movieSceneOnlyDtoJson)).andExpect(status().isNotFound());

        verify(this.movieSceneService, times(1)).save(this.movieSceneOnlyDto, uuid);
    }

    @Test
    public void shouldFailToCreateNewMovieSceneDueToUuidAlreadyExist() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(this.movieSceneService.save(this.movieSceneOnlyDto, uuid)).thenThrow(UuidInUseException.class);
        this.mockMvc.perform(post("/api/v1/movies/" + uuid + "/scenes").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.movieSceneOnlyDtoJson)).andExpect(status().isBadRequest());

        verify(this.movieSceneService, times(1)).save(this.movieSceneOnlyDto, uuid);
    }

    @Test
    public void shouldFailToCreateNewMoveSceneDueToPathVariableMovieUuidInvalid() throws Exception {
        String invalidUuid = "b2989ce9-eddc-b32c-5c26cb255a9e";
        this.mockMvc.perform(post("/api/v1/movies/" + invalidUuid + "/scenes").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.movieSceneOnlyDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteAllMovies() throws Exception {
        doNothing().when(this.movieService).deleteAll();
        this.mockMvc.perform(delete("/api/v1/movies")).andExpect(status().isNoContent());
        verify(this.movieService, times(1)).deleteAll();
    }

    @Test
    public void shouldUpdateMovieScene() throws Exception {
        UUID movieUuid = this.movieDtos.getFirst().uuid();
        UUID sceneUuid = this.movieSceneOnlyDto.uuid();
        when(this.movieSceneService.update(this.movieSceneOnlyDto, movieUuid, sceneUuid)).thenReturn(
                this.movieSceneDtos.getFirst());
        this.mockMvc.perform(
                put("/api/v1/movies/" + movieUuid + "/scenes/" + sceneUuid).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.movieSceneOnlyDtoJson)).andExpect(status().isOk());
        verify(this.movieSceneService, times(1)).update(this.movieSceneOnlyDto, movieUuid, sceneUuid);
    }

    @Test
    public void shouldFailToUpdateMovieSceneDueToMovieOrSceneDoesNotExist() throws Exception {
        UUID movieUuid = this.movieDtos.getFirst().uuid();
        UUID sceneUuid = this.movieSceneOnlyDto.uuid();
        when(this.movieSceneService.update(this.movieSceneOnlyDto, movieUuid, sceneUuid)).thenThrow(
                NotFoundException.class);
        this.mockMvc.perform(
                put("/api/v1/movies/" + movieUuid + "/scenes/" + sceneUuid).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.movieSceneOnlyDtoJson)).andExpect(status().isNotFound());
        verify(this.movieSceneService, times(1)).update(this.movieSceneOnlyDto, movieUuid, sceneUuid);
    }

    @Test
    public void shouldDeleteMovieScene() throws Exception {
        UUID movieSceneUuid = this.movieSceneOnlyDto.uuid();
        UUID movieUuid = this.movieDtos.getFirst().uuid();
        doNothing().when(this.movieSceneService).deleteByUuid(movieSceneUuid);
        this.mockMvc.perform(delete("/api/v1/movies/" + movieUuid + "/scenes/" + movieSceneUuid))
                .andExpect(status().isNoContent());
        verify(this.movieSceneService, times(1)).deleteByUuid(movieSceneUuid);
    }

    @Test
    public void shouldFailDeleteMovieSceneDueToMovieSceneNotFound() throws Exception {
        UUID movieSceneUuid = this.movieSceneOnlyDto.uuid();
        UUID movieUuid = this.movieDtos.getFirst().uuid();
        doThrow(new NotFoundException("Movie scene not found")).when(this.movieSceneService)
                .deleteByUuid(movieSceneUuid);
        this.mockMvc.perform(delete("/api/v1/movies/" + movieUuid + "/scenes/" + movieSceneUuid))
                .andExpect(status().isNotFound());
        verify(this.movieSceneService, times(1)).deleteByUuid(movieSceneUuid);
    }

    @Test
    public void shouldFindMovieSceneByUuid() throws Exception {
        UUID movieSceneUuid = this.movieSceneOnlyDto.uuid();
        UUID movieUuid = this.movieDtos.getFirst().uuid();
        when(this.movieSceneService.findByUuid(movieSceneUuid)).thenReturn(this.movieSceneDtos.getFirst());
        this.mockMvc.perform(get("/api/v1/movies/" + movieUuid + "/scenes/" + movieSceneUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is(movieSceneUuid.toString())));
        verify(this.movieSceneService, times(1)).findByUuid(movieSceneUuid);
    }

    @Test
    public void shouldFailFindMovieSceneByUuidDueToNotExistingUuid() throws Exception {
        UUID movieSceneUuid = this.movieSceneOnlyDto.uuid();
        UUID movieUuid = this.movieDtos.getFirst().uuid();
        when(this.movieSceneService.findByUuid(movieSceneUuid)).thenThrow(NotFoundException.class);
        this.mockMvc.perform(get("/api/v1/movies/" + movieUuid + "/scenes/" + movieSceneUuid))
                .andExpect(status().isNotFound());
        verify(this.movieSceneService, times(1)).findByUuid(movieSceneUuid);
    }

    @Test
    public void shouldFindAllMovieScenesOfMovie() throws Exception {
        UUID movieUuid = this.movieDtos.getFirst().uuid();
        when(this.movieSceneService.findAllScenesOfMovie(movieUuid)).thenReturn(this.movieSceneDtos);
        this.mockMvc.perform(get("/api/v1/movies/" + movieUuid + "/scenes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].uuid", is(this.movieSceneDtos.getFirst().uuid().toString())));
        verify(this.movieSceneService, times(1)).findAllScenesOfMovie(movieUuid);
    }
}