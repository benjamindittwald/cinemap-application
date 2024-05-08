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

package de.dittwald.cinemap.repository.moviescene;

import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.movie.MovieDto;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MovieSceneRestController.class)
@AutoConfigureMockMvc
public class MovieSceneRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieSceneService movieSceneService;

    private List<MovieSceneDto> movieSceneDtos;

    private String movieSceneDtoJson;

    @BeforeEach
    void setUp() {
        MovieDto wolf =
                new MovieDto(UUID.randomUUID(), Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"),
                        1051896, 1970,  Map.of("deu", "Der mit dem Wolf tanzt TAGLINE", "eng", "Dances with Wolves TAGLINE"),
                        Map.of("deu", "Der mit dem Wolf tanzt OVERVIEW", "eng", "Dances with Wolves OVERVIEW"),
                        Map.of(80, "western", 85, "Thriller"), ArrayUtils.toObject("Test".getBytes()), "imdbId");

        movieSceneDtos = new ArrayList<>();
        movieSceneDtos.add(
                new MovieSceneDto(UUID.fromString("b2989ce9-eddc-4772-b32c-5c26cb255a9e"), 13404954L, 52520008L,
                        Map.of("deu", "Der mit dem Wolf tanzt Szene 1", "eng", "Dances with Wolves scene 1"), wolf));
        movieSceneDtos.add(new MovieSceneDto(UUID.randomUUID(), 13404954L, 52520008L,
                Map.of("deu", "Der mit dem Wolf tanzt Szene 2", "eng", "Dances with Wolves scene 2"), wolf));

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
    }

    @Test
    public void shouldFindAllMovieScenes() throws Exception {
        when(this.movieSceneService.findAll()).thenReturn(movieSceneDtos);
        this.mockMvc.perform(get("/api/v1/scenes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description.eng").value("Dances with Wolves scene 1"));
    }

    @Test
    public void shouldFindMovieSceneByUuid() throws Exception {
        when(this.movieSceneService.findByUuid(this.movieSceneDtos.getFirst().uuid())).thenReturn(
                this.movieSceneDtos.getFirst());
        this.mockMvc.perform(get("/api/v1/scenes/" + this.movieSceneDtos.getFirst().uuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value("b2989ce9-eddc-4772-b32c-5c26cb255a9e"));
    }

    @Test
    public void shouldNotFindMovieSceneByUuid() throws Exception {
        when(this.movieSceneService.findByUuid(any())).thenThrow(NotFoundException.class);
        this.mockMvc.perform(get("/api/v1/scenes/" + UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteMovieSceneByUuid() throws Exception {
        doNothing().when(this.movieSceneService).deleteByUuid(any());
        this.mockMvc.perform(delete("/api/v1/scenes/" + movieSceneDtos.getFirst().uuid()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldFailDeleteMovieSceneByUuidDueToMovieSceneNotFound() throws Exception {
        Mockito.doThrow(NotFoundException.class)
                .when(this.movieSceneService)
                .deleteByUuid(this.movieSceneDtos.getFirst().uuid());
        this.mockMvc.perform(delete("/api/v1/scenes/" + movieSceneDtos.getFirst().uuid()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldFailDeleteMovieSceneByUuidDueToInvalidMovieSceneUuid() throws Exception {
        String invalidUuid = "b2989ce9-eddc-4772-5c26cb255a9e";
        this.mockMvc.perform(delete("/api/v1/scenes/"+ invalidUuid))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteAllMovieScenes() throws Exception {
        doNothing().when(this.movieSceneService).deleteAll();
        this.mockMvc.perform(delete("/api/v1/scenes"))
                .andExpect(status().isNoContent());

        verify(this.movieSceneService, times(1)).deleteAll();
    }
}
