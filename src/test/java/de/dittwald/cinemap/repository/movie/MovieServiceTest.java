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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest({MovieService.class, MovieDtoMapper.class})
@AutoConfigureMockMvc
public class MovieServiceTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MovieDtoMapper movieDtoMapper;

    @MockBean
    private MovieRepository movieRepository;


    List<Movie> movies = new ArrayList<>();

    @BeforeEach
    void setUp() {
        movies = new ArrayList<>();
        this.movies.add(
                new Movie(UUID.randomUUID(), Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"),
                        "https://www" + ".imdb" + ".com/title/tt0099348/?ref_=ext_shr_lnk"));
        this.movies.add(new Movie(UUID.randomUUID(), Map.of("deu", "Mein Name is Nobody", "eng", "My Name Is Nobody"),
                "https://www" + ".imdb.com/title/tt0070215/?ref_=ext_shr_lnk"));
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
        UUID uuid = UUID.randomUUID();
        MovieDto notYetPersistedMovie =
                new MovieDto(uuid, Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name"),
                        "https://www.imdb.com/title/tt0068154/?ref_=ext_shr_lnk");
        MovieDto persistedMovieDto =
                new MovieDto(uuid, Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name"),
                        "https://www.imdb.com/title/tt0068154/?ref_=ext_shr_lnk");
        Movie persistedMovie =
                new Movie(uuid, Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name"),
                        "https://www.imdb.com/title/tt0068154/?ref_=ext_shr_lnk");

        when(this.movieRepository.save(any())).thenReturn(persistedMovie);

        assertThat(this.movieService.save(notYetPersistedMovie)).isEqualTo(persistedMovieDto);
    }

    @Test
    public void shouldUpdateMovie() throws NotFoundException {
        UUID uuid = UUID.randomUUID();
        MovieDto persistedMovieDto = new MovieDto(uuid,
                Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name", "fra",
                        "On continue à l'appeler Trinita"),
                "https://www.imdb" + ".com/title/tt0068154/?ref_=ext_shr_lnk");
        Optional<Movie> persistedMovie = Optional.of(
                new Movie(uuid, Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name"),
                        "https://www.imdb.com/title/tt0068154/?ref_=ext_shr_lnk"));
        Movie persistedMovieUpdated = new Movie(uuid,
                Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name", "fra",
                        "On continue à l'appeler Trinita"),
                "https://www.imdb" + ".com/title/tt0068154/?ref_=ext_shr_lnk");

        when(this.movieRepository.findByUuid(uuid)).thenReturn(persistedMovie);
        when(this.movieRepository.save(any())).thenReturn(persistedMovieUpdated);

        assertThat(this.movieService.update(persistedMovieDto, uuid)).isEqualTo(
                this.movieDtoMapper.movieToMovieDto(persistedMovieUpdated));
    }

    @Test
    public void shouldThrowNotFoundException() {
        MovieDto persistedMovieDto = new MovieDto(UUID.randomUUID(),
                Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name", "fra",
                        "On continue à l'appeler Trinita"),
                "https://www.imdb" + ".com/title/tt0068154/?ref_=ext_shr_lnk");
        when(this.movieRepository.findById(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> this.movieService.update(persistedMovieDto, persistedMovieDto.uuid()));
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
    }

    @Test
    public void shouldDeleteMovie() {
        Optional<Movie> persistedMovie = Optional.of(new Movie(UUID.randomUUID(),
                Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name"),
                "https://www.imdb.com/title/tt0068154/?ref_=ext_shr_lnk"));
        MovieRepository movieRepositoryMock = mock(MovieRepository.class);
        when(this.movieRepository.findById(0L)).thenReturn(persistedMovie);
        doNothing().when(movieRepositoryMock).deleteById(0L);
        // Todo: Implement proper assertion!
    }

    @Test
    public void shouldThroughExceptionWhenTryToDeleteMovie() {
        when(this.movieRepository.findByUuid(any())).thenReturn(Optional.empty());
        Exception exception =
                assertThrows(NotFoundException.class, () -> this.movieService.deleteByUuid(UUID.randomUUID()));
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
    }

    @Test
    public void shouldFindMovieById() throws NotFoundException {
        Optional<Movie> persistedMovie = Optional.of(new Movie(UUID.randomUUID(),
                Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name"),
                "https://www.imdb.com/title/tt0068154/?ref_=ext_shr_lnk"));

        when(this.movieRepository.findByUuid(any())).thenReturn(persistedMovie);
        assertThat(this.movieService.findByUuid(UUID.randomUUID())).isEqualTo(
                this.movieDtoMapper.movieToMovieDto(persistedMovie.get()));
    }

    @Test
    public void shouldThrowExceptionWhenTryToFindMovieById() {
        when(this.movieRepository.findById(any())).thenReturn(Optional.empty());
        Exception exception =
                assertThrows(NotFoundException.class, () -> this.movieService.findByUuid(UUID.randomUUID()));
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
    }

    @Test
    public void shouldFailSaveAndThrowDataIntegrityViolationException() {

        when(this.movieRepository.existsByUuid(any())).thenReturn(true);

        Exception exception = assertThrows(DataIntegrityViolationException.class,
                () -> this.movieService.save(this.movieDtoMapper.movieToMovieDto(this.movies.getFirst())));

        assertThat(exception.getMessage()).isEqualTo("UUID already in use");
    }
}
