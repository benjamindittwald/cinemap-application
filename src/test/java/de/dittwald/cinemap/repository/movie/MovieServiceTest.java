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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest(MovieService.class)
@AutoConfigureMockMvc
public class MovieServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MovieService movieService;

    @MockBean
    private MovieRepository movieRepository;

    List<Movie> movies = new ArrayList<>();

    @BeforeEach
    void setUp() {
        movies = new ArrayList<>();
        this.movies.add(new Movie(Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"),
                "https://www" + ".imdb" + ".com/title/tt0099348/?ref_=ext_shr_lnk"));
        this.movies.add(new Movie(Map.of("deu", "Mein Name is Nobody", "eng", "My Name Is Nobody"), "https://www" +
                ".imdb.com/title/tt0070215/?ref_=ext_shr_lnk"));
    }

    @Test
    void shouldRetrieveTwoMovies() {
        when(this.movieRepository.findAll()).thenReturn(movies);
        List<MovieDto> movieDtos = this.movieService.findAll();
        assertThat(movieDtos.size()).isEqualTo(2);
    }

    @Test
    void shouldRetrieveTwoMoviesWithFirstTitleWolve() {
        when(this.movieRepository.findAll()).thenReturn(movies);
        List<MovieDto> movieDtos = this.movieService.findAll();
        assertThat(movieDtos.getFirst().title().get("deu")).isEqualTo("Der mit dem Wolf tanzt");
    }

    @Test
    void shouldSaveMovie() {
        MovieInputDto movieInputDto = new MovieInputDto(Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " +
                "Is Still My Name"), "https://www.imdb.com/title/tt0068154/?ref_=ext_shr_lnk");
        MovieDto persistedMovieDto = new MovieDto(0L, Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " +
                "Is Still My Name"), "https://www.imdb.com/title/tt0068154/?ref_=ext_shr_lnk");
        Movie persistedMovie = new Movie(0L, Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " +
                "Is Still My Name"), "https://www.imdb.com/title/tt0068154/?ref_=ext_shr_lnk");

        when(this.movieRepository.save(any())).thenReturn(persistedMovie);

        assertThat(this.movieService.save(movieInputDto)).isEqualTo(persistedMovieDto);
    }

    @Test
    public void shouldUpdateMovie() throws NotFoundException {
        MovieDto persistedMovieDto = new MovieDto(0L, Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " +
                "Is Still My Name", "fra", "On continue à l'appeler Trinita"), "https://www.imdb" +
                ".com/title/tt0068154/?ref_=ext_shr_lnk");
        Optional<Movie> persistedMovie = Optional.of(new Movie(0L, Map.of("deu", "Der Kleine und der müde Joe", "eng"
                , "Trinity " +
                        "Is Still My Name"), "https://www.imdb.com/title/tt0068154/?ref_=ext_shr_lnk"));
        Movie persistedMovieUpdated = new Movie(0L, Map.of("deu", "Der Kleine und der müde Joe"
                , "eng", "Trinity " +
                        "Is Still My Name", "fra", "On continue à l'appeler Trinita"), "https://www.imdb" +
                ".com/title/tt0068154/?ref_=ext_shr_lnk");

        when(this.movieRepository.findById(0L)).thenReturn(persistedMovie);
        when(this.movieRepository.save(any())).thenReturn(persistedMovieUpdated);

        assertThat(this.movieService.update(persistedMovieDto)).isEqualTo(MovieDTOMapper.movieToDTO(persistedMovieUpdated));
    }

    @Test
    public void shouldThrowNotFoundException() {
        MovieDto persistedMovieDto = new MovieDto(0L, Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " +
                "Is Still My Name", "fra", "On continue à l'appeler Trinita"), "https://www.imdb" +
                ".com/title/tt0068154/?ref_=ext_shr_lnk");
        when(this.movieRepository.findById(0L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> this.movieService.update(persistedMovieDto));
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
    }

    @Test
    public void shouldDeleteMovie() throws NotFoundException {
        Optional<Movie> persistedMovie = Optional.of(new Movie(0L, Map.of("deu", "Der Kleine und der müde Joe", "eng"
                , "Trinity " +
                        "Is Still My Name"), "https://www.imdb.com/title/tt0068154/?ref_=ext_shr_lnk"));
        MovieRepository movieRepositoryMock = mock(MovieRepository.class);
        when(this.movieRepository.findById(0L)).thenReturn(persistedMovie);
        doNothing().when(movieRepositoryMock).deleteById(0L);
    }

    @Test
    public void shouldThroughExceptionWhenTryToDeleteMovie() {
        Optional<Movie> persistedMovie = Optional.of(new Movie(0L, Map.of("deu", "Der Kleine und der müde Joe", "eng"
                , "Trinity " +
                        "Is Still My Name"), "https://www.imdb.com/title/tt0068154/?ref_=ext_shr_lnk"));
        MovieRepository movieRepositoryMock = mock(MovieRepository.class);
        when(this.movieRepository.findById(0L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(NotFoundException.class,
                () -> this.movieService.deleteById(0L));
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
    }

    @Test
    public void shouldFindMovieById() throws NotFoundException {
        Optional<Movie> persistedMovie = Optional.of(new Movie(0L, Map.of("deu", "Der Kleine und der müde Joe", "eng"
                , "Trinity " +
                        "Is Still My Name"), "https://www.imdb.com/title/tt0068154/?ref_=ext_shr_lnk"));

        when(this.movieRepository.findById(0L)).thenReturn(persistedMovie);

        assertThat(this.movieService.findById(0L)).isEqualTo(MovieDTOMapper.movieToDTO(persistedMovie.get()));
    }
}
