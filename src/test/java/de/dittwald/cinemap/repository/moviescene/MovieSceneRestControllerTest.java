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
import de.dittwald.cinemap.repository.exceptions.UuidInUseException;
import de.dittwald.cinemap.repository.movie.MovieDto;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
                        "https://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk");

        movieSceneDtos = new ArrayList<>();
        movieSceneDtos.add(new MovieSceneDto(UUID.randomUUID(), 13404954L, 52520008L,
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
    public void shouldCreateNewMovieSceneAndReturn() throws Exception {
        when(this.movieSceneService.save(any())).thenReturn(this.movieSceneDtos.getFirst());
        mockMvc.perform(post("/api/v1/moviescenes").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(this.movieSceneDtoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description.eng").value("Dances with Wolves scene 1"));
    }

    @Test
    public void shouldFailToCreateNewMovieSceneDueToMovieNotExist() throws Exception {
        when(this.movieSceneService.save(any())).thenThrow(NotFoundException.class);
        mockMvc.perform(post("/api/v1/moviescenes").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.movieSceneDtoJson)).andExpect(status().isNotFound());
    }

    @Test
    public void shouldFailToCreateNewMovieSceneDueToUuidAlreadyExist() throws Exception {
        when(this.movieSceneService.save(any())).thenThrow(UuidInUseException.class);
        mockMvc.perform(post("/api/v1/moviescenes").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.movieSceneDtoJson)).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFindAllMovieScenes() throws Exception {
        when(this.movieSceneService.findAll()).thenReturn(movieSceneDtos);
        mockMvc.perform(get("/api/v1/moviescenes")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description.eng").value("Dances with Wolves scene 1"));
    }

    // FindByUuid

    // FindAllByMovieUuid

    // Update

    // DeleteByUuid
}
