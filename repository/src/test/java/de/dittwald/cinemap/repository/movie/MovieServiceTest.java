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
import de.dittwald.cinemap.repository.scene.Scene;
import de.dittwald.cinemap.repository.scene.SceneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private DummyMovies dummyMovies;

    @BeforeEach
    public void setUp() throws URISyntaxException, MalformedURLException {
        this.dummyMovies = new DummyMovies();
    }

    @Test
    void shouldFindTwoMovies() {
        when(this.movieRepository.findAll()).thenReturn(
                List.of(this.dummyMovies.getNobody(), this.dummyMovies.getWolf()));
        List<MovieDto> movieDtos = this.movieService.findAll();
        assertThat(movieDtos.size()).isEqualTo(2);
        verify(this.movieRepository, times(1)).findAll();
    }

    @Test
    void shouldFindTwoMoviesWhereFirstTitleIsNobody() {
        when(this.movieRepository.findAll()).thenReturn(
                List.of(this.dummyMovies.getNobody(), this.dummyMovies.getWolf()));
        List<MovieDto> movieDtos = this.movieService.findAll();
        assertThat(movieDtos.getFirst().localizedMovies().get("deu").getTitle()).isEqualTo(
                "Mein Name ist Nobody - Title");
        verify(this.movieRepository, times(1)).findAll();
    }

    @Test
    void shouldSaveMovie() {
        MovieDto movieDto = this.dummyMovies.getNobodyDto();

        when(this.movieRepository.save(this.movieDtoMapper.movieDtoToMovie(movieDto))).thenReturn(
                this.movieDtoMapper.movieDtoToMovie(movieDto));
        assertThat(this.movieService.save(movieDto).uuid()).isEqualTo(movieDto.uuid());
        verify(this.movieRepository, times(1)).save(this.movieDtoMapper.movieDtoToMovie(movieDto));
    }

    @Test
    public void shouldUpdateMovie() throws NotFoundException {
        MovieDto movieDto = this.dummyMovies.getNobodyDto();
        Optional<Movie> movieDtos = Optional.of(this.dummyMovies.getNobody());
        Movie movie = this.dummyMovies.getNobody();

        when(this.movieRepository.findByUuid(movieDto.uuid())).thenReturn(movieDtos);
        when(this.movieRepository.save(this.movieDtoMapper.movieDtoToMovie(movieDto))).thenReturn(movie);

        assertThat(this.movieService.update(movieDto, movieDto.uuid()).uuid()).isEqualTo(
                this.movieDtoMapper.movieToMovieDto(movie).uuid());

        verify(this.movieRepository, times(1)).findByUuid(movieDto.uuid());
        verify(this.movieRepository, times(1)).save(movie);
    }

    @Test
    public void shouldFailUpdateMovieDueToMovieDoesNotExist() {
        UUID notExistingMovieUuid = UUID.randomUUID();
        MovieDto movieDto = this.dummyMovies.getNobodyDto();
        when(this.movieRepository.findByUuid(notExistingMovieUuid)).thenReturn(Optional.empty());

        Exception exception =
                assertThrows(NotFoundException.class, () -> this.movieService.update(movieDto, notExistingMovieUuid));
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
        verify(this.movieRepository, times(1)).findByUuid(notExistingMovieUuid);
    }

    @Test
    public void shouldDeleteMovie() throws NotFoundException {
        when(this.movieRepository.existsByUuid(this.dummyMovies.getNobody().getUuid())).thenReturn(true);
        doNothing().when(this.movieRepository).deleteByUuid(this.dummyMovies.getNobody().getUuid());
        this.movieService.deleteByUuid(this.dummyMovies.getNobody().getUuid());
        verify(this.movieRepository, times(1)).existsByUuid(this.dummyMovies.getNobody().getUuid());
        verify(this.movieRepository, times(1)).deleteByUuid(this.dummyMovies.getNobody().getUuid());
    }

    @Test
    public void shouldFailDeleteMoveDueToMovieDoesNotExist() {
        UUID notExistingMovieUuid = UUID.randomUUID();
        when(this.movieRepository.existsByUuid(notExistingMovieUuid)).thenReturn(false);
        Exception exception =
                assertThrows(NotFoundException.class, () -> this.movieService.deleteByUuid(notExistingMovieUuid));
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
        verify(this.movieRepository, times(1)).existsByUuid(notExistingMovieUuid);
    }

    @Test
    public void shouldFindMovieByUuid() throws NotFoundException {
        Optional<Movie> persistedMovie = Optional.of(this.dummyMovies.getWolf());

        when(this.movieRepository.findByUuid(persistedMovie.get().getUuid())).thenReturn(persistedMovie);
        assertThat(this.movieService.findByUuid(persistedMovie.get().getUuid()).uuid()).isEqualTo(
                this.movieDtoMapper.movieToMovieDto(persistedMovie.get()).uuid());
        verify(this.movieRepository, times(1)).findByUuid(persistedMovie.get().getUuid());
    }

    @Test
    public void shouldThrowExceptionWhenTryToFindMovieByUuid() {
        UUID notExistingMovieUuid = UUID.randomUUID();
        when(this.movieRepository.findByUuid(notExistingMovieUuid)).thenReturn(Optional.empty());
        Exception exception =
                assertThrows(NotFoundException.class, () -> this.movieService.findByUuid(notExistingMovieUuid));
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
        verify(this.movieRepository, times(1)).findByUuid(notExistingMovieUuid);
    }

    @Test
    public void shouldFailSaveMovieDueToUuidAlreadyExists() {
        when(this.movieRepository.existsByUuid(this.dummyMovies.getNobody().getUuid())).thenReturn(true);
        Exception exception = assertThrows(DataIntegrityViolationException.class,
                () -> this.movieService.save(this.movieDtoMapper.movieToMovieDto(this.dummyMovies.getNobody())));
        assertThat(exception.getMessage()).isEqualTo("UUID already in use");
        verify(this.movieRepository, times(1)).existsByUuid(this.dummyMovies.getNobody().getUuid());
    }

    @Test
    public void shouldDeleteAllMovies() {
        doNothing().when(this.movieRepository).deleteAll();
        this.movieService.deleteAll();
        verify(this.movieRepository, times(1)).deleteAll();
    }
}
