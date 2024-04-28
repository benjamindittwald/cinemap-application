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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    private String movieDtoJson;

    private String movieDtosJson;

    private String movieSceneDtoJson;

    private List<MovieSceneDto> movieSceneDtos;

    private String movieSceneOnlyDtoJson;

    private MovieSceneOnlyDto movieSceneOnlyDto;

    @BeforeEach
    void setUp() {
        this.movieDtos = new ArrayList<>();
        this.movieDtos.add(new MovieDto(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"),
                Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"),
                "https" + "://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"));
        this.movieDtos.add(new MovieDto(UUID.fromString("575c9ec9-fff0-4f04-a115-55fabf4acaee"),
                Map.of("deu", "Mein Name ist Nobody", "eng", "My Name Is Nobody"),
                "https" + "://www.imdb.com/title/tt0070215/?ref_=ext_shr_lnk"));

        this.movieDtoJson = """
                {
                    "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                    "title":
                        {
                            "eng":"Dances with Wolves",
                            "deu":"Der mit dem Wolf tanzt"
                        },
                    "imdbWebsiteUrl":"https://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"
                }
                """;

        this.movieDtosJson = """
                [
                    {
                        "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                        "title":
                            {
                                "eng":"Dances with Wolves",
                                "deu":"Der mit dem Wolf tanzt"
                            },
                        "imdbWebsiteUrl":"https://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"
                    },
                    {
                        "uuid":"575c9ec9-fff0-4f04-a115-55fabf4acaee",
                        "title":
                            {
                                "eng":"My Name Is Nobody",
                                "deu":"Mein Name ist Nobody"
                            },
                        "imdbWebsiteUrl":"https://www.imdb.com/title/tt0070215/?ref_=ext_shr_lnk"
                    }
                ]
                """;

        this.movieSceneDtoJson = """
                {
                    "uuid": "b2989ce9-eddc-4772-b32c-5c26cb255a9e",
                    "lon": "",
                    "lat": "",
                    "description":
                        {
                            "deu":"Der mit dem Wolf tanzt Szene 1",
                            "eng":"Dances with Wolves scene 1"
                        },
                    "movie":
                        {
                        "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                        "title":
                            {
                                "eng":"Dances with Wolves",
                                "deu":"Der mit dem Wolf tanzt"
                            },
                        "imdbWebsiteUrl":"https://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"
                        }
                }
                """;

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
    public void shouldReturnStatus200forMovies() throws Exception {
        this.mockMvc.perform(get("/api/v1/movies")).andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus404ForFake() throws Exception {
        this.mockMvc.perform(get("/api/v1/movie-fake")).andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnStatus200AndMoviesList() throws Exception {

        when(this.movieService.findAll()).thenReturn(movieDtos);

        this.mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title.deu", is("Der mit dem Wolf tanzt")))
                .andExpect(jsonPath("$[1].title.deu", is("Mein Name ist Nobody")));
    }

    @Test
    public void shouldReturnMovieByUuid() throws Exception {
        when(this.movieService.findByUuid(any())).thenReturn(movieDtos.getFirst());
        this.mockMvc.perform(get("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title.deu", is("Der mit dem Wolf tanzt")));
    }

    @Test
    public void shouldReturnStatus404ForFakeId() throws Exception {
        when(this.movieService.findByUuid(any())).thenThrow(NotFoundException.class);
        this.mockMvc.perform(get("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldFailFindByUuidDueToInvalidUuid() throws Exception {
        String invalidUuid = "aa7acd67-4052-a63f-90440c683e6d";
        this.mockMvc.perform(get("/api/v1/movies/"+invalidUuid)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnStatus200AndUpdateMovie() throws Exception {
        when(this.movieService.update(this.movieDtos.getFirst(), this.movieDtos.getFirst().uuid())).thenReturn(this.movieDtos.getFirst());

        this.mockMvc.perform(
                        put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(this.movieDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid", is("aa7acd67-4052-421d-a63f-90440c683e6d")))
                .andExpect(jsonPath("$.title.deu", is("Der mit dem Wolf tanzt")));
    }

    @Test
    public void shouldNotUpdateMovieAndReturnStatus400DueToInvalidLang() throws Exception {

        String movieDtoJson = """
                {   "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                    "title":
                        {
                            "en":"Dances with Wolves",
                            "deu":"Der mit dem Wolf tanzt"
                        },
                    "imdbWebsiteUrl":"http://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"
                }
                """;

        this.mockMvc.perform(put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(movieDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotUpdateMovieAndReturnStatus400DueToInvalidUrl() throws Exception {

        String movieDtoJson = """
                {   "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                    "title":
                        {
                            "eng":"Dances with Wolves",
                            "deu":"Der mit dem Wolf tanzt"
                        },
                    "imdbWebsiteUrl":"http//www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"
                }
                """;

        this.mockMvc.perform(put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(movieDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotUpdateMovieAndReturnStatus400DueToTooLongUrl() throws Exception {

        String movieDtoJson = """
                {   "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                    "title":
                        {
                            "eng":"Dances with Wolves",
                            "deu":"Der mit dem Wolf tanzt"
                        },
                    "imdbWebsiteUrl":"http//www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk/3aspKWmnBr1ZYAUVSbXtaS0jWBkDC41FKtnm3V3mYyD7dnudWKj13pCF9SCTuwTzPsntHEXdJKswp5QToEdkFbo3NuKC2Q9EjK12"
                }
                """;

        this.mockMvc.perform(put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(movieDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnStatus404ForFakeIdUpdate() throws Exception {
        when(this.movieService.update(this.movieDtos.getFirst(), this.movieDtos.getFirst().uuid())).thenThrow(NotFoundException.class);
        this.mockMvc.perform(
                put("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.movieDtoJson)).andExpect(status().isNotFound());
    }

    @Test
    public void shouldFailUpdateMovieDueToInvalidUuid() throws Exception {
        String invalidUuid = "aa7acd67-4052-a63f-90440c683e6d";
        this.mockMvc.perform(
                put("/api/v1/movies/" + invalidUuid).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.movieDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCreateMovie() throws Exception {

        String movieDtoJson = """
                {
                    "uuid": "aa7acd67-4052-421d-a63f-90440c683e6d",
                    "title":
                        {
                            "eng":"Dances with Wolves",
                            "deu":"Der mit dem Wolf tanzt"
                        },
                    "imdbWebsiteUrl":"http://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"
                }
                """;

        when(this.movieService.save(any())).thenReturn(this.movieDtos.getFirst());

        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(movieDtoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title.eng").value("Dances with Wolves"));
    }

    @Test
    public void shouldFailCreateMovieDueToMissingUuid() throws Exception {
        String movieDtoJson = """
                {
                    "uuid":"aa7acd67-4052-421d-a63f-90440c66d",
                    "title":
                        {
                            "eng":"Dances with Wolves",
                            "deu":"Der mit dem Wolf tanzt"
                        },
                    "imdbWebsiteUrl":"http://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"
                }
                """;

        when(this.movieService.save(any())).thenReturn(this.movieDtos.getFirst());

        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(movieDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailCreateMovieDueToInvalidUuid() throws Exception {
        String movieDtoJson = """
                {
                    "title":
                        {
                            "eng":"Dances with Wolves",
                            "deu":"Der mit dem Wolf tanzt"
                        },
                    "imdbWebsiteUrl":"http://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"
                }
                """;

        when(this.movieService.save(any())).thenReturn(this.movieDtos.getFirst());

        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(movieDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnStatus400DueToInvalidUrl() throws Exception {
        String invalidMovieDtoJson = """
                {
                    "title":
                        {
                            "eng":"Dances with Wolves",
                            "deu":"Der mit dem Wolf tanzt"
                        },
                    "imdbWebsiteUrl":"abcd//www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"
                }
                """;
        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(invalidMovieDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnStatus400DueToTooLongUrl() throws Exception {
        String invalidMovieDtoJson = """
                {
                    "title":
                        {
                            "eng":"Dances with Wolves",
                            "deu":"Der mit dem Wolf tanzt"
                        },
                    "imdbWebsiteUrl":"https://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk/3aspKWmnBr1ZYAUVSbXtaS0jWBkDC41FKtnm3V3mYyD7dnudWKj13pCF9SCTuwTzPsntHEXdJKswp5QToEdkFbo3NuKC2Q9EjK12"
                }
                """;
        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(invalidMovieDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnStatus400DueToNonIsoLang() throws Exception {
        String invalidMovieDtoJson = """
                {
                    "title":
                        {
                            "en":"Dances with Wolves",
                            "deu":"Der mit dem Wolf tanzt"
                        },
                    "imdbWebsiteUrl":"https://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"
                }
                """;
        this.mockMvc.perform(post("/api/v1/movies").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(invalidMovieDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteMovie() throws Exception {
        MovieService movieServiceMock = mock(MovieService.class);
        doNothing().when(movieServiceMock).deleteByUuid(any());

        this.mockMvc.perform(delete("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldFailDeleteMovieDueToInvalidUuid() throws Exception {
        String invalidUuid = "aa7acd67-4052-421d-90440c683e6d";
        this.mockMvc.perform(delete("/api/v1/movies/" + invalidUuid))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnStatus404ForFakeIdDelete() throws Exception {
        when(this.movieService.findByUuid(any())).thenThrow(NotFoundException.class);

        this.mockMvc.perform(delete("/api/v1/movies/aa7acd67-4052-421d-a63f-90440c683e6d"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldCreateNewMovieSceneAndReturnStatus201() throws Exception {
        when(this.movieSceneService.save(this.movieSceneOnlyDto,
                UUID.fromString("b2989ce9-eddc-4772-b32c-5c26cb255a9e"))).thenReturn(this.movieSceneDtos.getFirst());
        this.mockMvc.perform(post("/api/v1/movies/b2989ce9-eddc-4772-b32c-5c26cb255a9e/scenes").contentType(
                                MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.movieSceneOnlyDtoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description.eng").value("Dances with Wolves scene 1"));
    }

    @Test
    public void shouldFailToCreateNewMovieSceneDueToMovieNotExist() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(this.movieSceneService.save(this.movieSceneOnlyDto, uuid)).thenThrow(NotFoundException.class);
        this.mockMvc.perform(
                post("/api/v1/movies/" + uuid + "/scenes").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.movieSceneOnlyDtoJson)).andExpect(status().isNotFound());
    }

    @Test
    public void shouldFailToCreateNewMovieSceneDueToUuidAlreadyExist() throws Exception {
        UUID uuid = UUID.randomUUID();
        when(this.movieSceneService.save(this.movieSceneOnlyDto, uuid)).thenThrow(UuidInUseException.class);
        this.mockMvc.perform(post("/api/v1/movies/" + uuid + "/scenes").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.movieSceneOnlyDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailToCreateNewMoveSceneDueToPathVariableMovieUuidInvalid() throws Exception {
        String invalidUuid = "b2989ce9-eddc-b32c-5c26cb255a9e";
        this.mockMvc.perform(post("/api/v1/movies/" + invalidUuid + "/scenes").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.movieSceneOnlyDtoJson)).andExpect(status().isBadRequest());
    }
}