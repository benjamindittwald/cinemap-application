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
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @BeforeEach
    void setUp() {
        movieDtos = new ArrayList<>();
        movieDtos.add(new MovieDto(0L, Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"), "https://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"));
        movieDtos.add(new MovieDto(0L, Map.of("deu", "Mein Name ist Nobody", "eng", "My Name Is Nobody"), "https://www.imdb.com/title/tt0070215/?ref_=ext_shr_lnk"));
    }

    @Test
    public void shouldReturnStatus200() throws Exception {
        this.mockMvc.perform(get("/api/v1/movies")).andExpect(status().isOk());
    }

    @Test
    public void shouldReturnStatus404() throws Exception {
        this.mockMvc.perform(get("/api/v1/movie")).andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnStatus200AndMoviesList() throws Exception {

        when(this.movieService.findAll()).thenReturn(movieDtos);

        String jsonResponse = """
                [
                    {
                        "id":0,
                        "title":
                            {
                                "eng":"Dances with Wolves",
                                "deu":"Der mit dem Wolf tanzt"
                            }
                        "imdbWebsite":"https://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"
                    },
                    {
                        "id":1,
                            {
                                "eng":"My Name Is Nobody",
                                "deu":"Mein Name ist Nobody"
                            }
                        "movieImdbWebsite":"https://www.imdb.com/title/tt0070215/?ref_=ext_shr_lnk"
                    }
                ]
                """;

        this.mockMvc.perform(get("/api/v1/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title.deu", is("Der mit dem Wolf tanzt")))
                .andExpect(jsonPath("$[1].title.deu", is("Mein Name ist Nobody")));
    }
}
