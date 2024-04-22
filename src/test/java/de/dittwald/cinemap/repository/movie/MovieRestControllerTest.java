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

    private List<MovieDto> movieDtos;

    private String movieDtoJson;

    private String movieDtosJson;

    @BeforeEach
    void setUp() {
        movieDtos = new ArrayList<>();
        movieDtos.add(new MovieDto(0L, Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"),
                "https" + "://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"));
        movieDtos.add(new MovieDto(1L, Map.of("deu", "Mein Name ist Nobody", "eng", "My Name Is Nobody"), "https" +
                "://www.imdb.com/title/tt0070215/?ref_=ext_shr_lnk"));

        this.movieDtoJson = """
                {
                    "id":0,
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
                        "id":0,
                        "title":
                            {
                                "eng":"Dances with Wolves",
                                "deu":"Der mit dem Wolf tanzt"
                            },
                        "imdbWebsiteUrl":"https://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"
                    },
                    {
                        "id":1,
                        "title":
                            {
                                "eng":"My Name Is Nobody",
                                "deu":"Mein Name ist Nobody"
                            },
                        "imdbWebsiteUrl":"https://www.imdb.com/title/tt0070215/?ref_=ext_shr_lnk"
                    }
                ]
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

        this.mockMvc.perform(get("/api/v1/movies")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].title.deu", is("Der mit dem Wolf tanzt"))).andExpect(jsonPath("$[1].title.deu", is("Mein Name ist Nobody")));
    }

    @Test
    public void shouldReturnMovieByID() throws Exception {
        when(this.movieService.findById(0L)).thenReturn(movieDtos.getFirst());
        this.mockMvc.perform(get("/api/v1/movies/0")).andExpect(status().isOk()).andExpect(jsonPath("$.title.deu",
                is("Der mit dem Wolf tanzt")));
    }

    @Test
    public void shouldReturnStatus404ForFakeId() throws Exception {
        when(this.movieService.findById(anyLong())).thenThrow(NotFoundException.class);
        this.mockMvc.perform(get("/api/v1/movies/42")).andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnStatus200AndUpdateMovie() throws Exception {
        when(this.movieService.update(this.movieDtos.getFirst())).thenReturn(this.movieDtos.getFirst());

        this.mockMvc.perform(put("/api/v1/movies/0")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.movieDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(0)))
                .andExpect(jsonPath("$.title.deu", is("Der mit dem Wolf tanzt")));
    }

    @Test
    public void shouldReturnStatus404ForFakeIdUpdate() throws Exception {
        when(this.movieService.update(any())).thenThrow(NotFoundException.class);
        this.mockMvc.perform(put("/api/v1/movies/0")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.movieDtoJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldCreateMovie() throws Exception {
        when(this.movieService.save(any())).thenReturn(this.movieDtos.getFirst());

        this.mockMvc.perform(post("/api/v1/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(this.movieDtoJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldDeleteMovie() throws Exception {
        MovieService movieServiceMock = mock(MovieService.class);
        doNothing().when(movieServiceMock).deleteById(anyLong());

        this.mockMvc.perform(delete("/api/v1/movies/0"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnStatus404ForFakeIdDelete() throws Exception {
        when(this.movieService.findById(anyLong())).thenThrow(NotFoundException.class);

        this.mockMvc.perform(delete("/api/v1/movies/0"))
                .andExpect(status().isNoContent());
    }
}