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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MovieDtoMapperTest {

    private final List<Movie> movies = new ArrayList<>();
    private final List<MovieDto> movieDtos = new ArrayList<>();
    private final List<MovieInputDto> movieInputDtos = new ArrayList<>();

    @BeforeEach
    void setUp() {
        movies.add(new Movie(Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"), "https://www.imdb" +
                ".com/title/tt0099348/?ref_=ext_shr_lnk"));
        movies.add(new Movie(Map.of("deu", "Mein Name is Nobody", "eng", "My Name Is Nobody"), "https://www.imdb" +
                ".com/title/tt0070215/?ref_=ext_shr_lnk"));

        movieDtos.add(new MovieDto(0L, Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"), "https" +
                "://www.imdb.com/title/tt0099348/?ref_=ext_shr_lnk"));
        movieDtos.add(new MovieDto(1L, Map.of("deu", "Mein Name is Nobody", "eng", "My Name Is Nobody"), "https://www" +
                ".imdb.com/title/tt0070215/?ref_=ext_shr_lnk"));

        movieInputDtos.add(new MovieInputDto(Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"), "https://www.imdb" +
                ".com/title/tt0099348/?ref_=ext_shr_lnk"));
        movieInputDtos.add(new MovieInputDto(Map.of("deu", "Mein Name is Nobody", "eng", "My Name Is Nobody"), "https://www.imdb" +
                ".com/title/tt0070215/?ref_=ext_shr_lnk"));
    }

    @Test
    void shouldConvertMovieToMovieDto() {
        assertThat(MovieDTOMapper.movieToDTO(movies.getFirst()).imdbWebsiteUrl()).isEqualTo(movies.getFirst().getImdbWebsiteUrl());
        assertThat(MovieDTOMapper.movieToDTO(movies.getLast()).imdbWebsiteUrl()).isEqualTo(movies.getLast().getImdbWebsiteUrl());
        assertThat(MovieDTOMapper.movieToDTO(movies.getFirst()).title().get(0)).isEqualTo(movies.getFirst().getTitle().get(0));
    }

    @Test
    void shouldConvertDtoMovieToMovie() {
        assertThat(MovieDTOMapper.dtoToMovie(movieDtos.getFirst()).getImdbWebsiteUrl()).isEqualTo(movieDtos.getFirst().imdbWebsiteUrl());
        assertThat(MovieDTOMapper.dtoToMovie(movieDtos.getLast()).getImdbWebsiteUrl()).isEqualTo(movieDtos.getLast().imdbWebsiteUrl());
        assertThat(MovieDTOMapper.dtoToMovie(movieDtos.getFirst()).getTitle().get(0)).isEqualTo(movieDtos.getFirst().title().get(0));
    }

    @Test
    public void shouldConvertMovieDtoListToMovieList() {
        assertThat(MovieDTOMapper.dtoListToMovieList(movieDtos).size()).isEqualTo(2);
        assertThat(MovieDTOMapper.dtoListToMovieList(movieDtos).getFirst().getTitle().get(0)).isEqualTo(movieDtos.getFirst().title().get(0));
        assertThat(MovieDTOMapper.dtoListToMovieList(movieDtos).getLast().getTitle().get(1)).isEqualTo(movieDtos.getLast().title().get(1));
    }

    @Test
    public void shouldConvertMovieListToMovieDtoList() {
        assertThat(MovieDTOMapper.movieListToDtoList(movies).size()).isEqualTo(2);
        assertThat(MovieDTOMapper.movieListToDtoList(movies).getFirst().title().get(0)).isEqualTo(movies.getFirst().getTitle().get(0));
        assertThat(MovieDTOMapper.movieListToDtoList(movies).getLast().title().get(1)).isEqualTo(movies.getLast().getTitle().get(1));
    }

    @Test
    public void shouldConvertInputDtoToMovie() {
        assertThat(MovieDTOMapper.inputDtoToMovie((this.movieInputDtos.getFirst())).getTitle().get("deu")).isEqualTo("Der mit dem Wolf tanzt");
    }
}