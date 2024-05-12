/*
 * Copyright 2024 Benjamin Dittwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dittwald.cinemap.repository.movie;

import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@WebMvcTest({MovieService.class})
@AutoConfigureMockMvc
public class MovieServiceTest {

    @Autowired
    private MovieService movieService;

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
        List<MovieDto> movieDtos = this.movieService.findAll("en");
        assertThat(movieDtos.size()).isEqualTo(2);
        verify(this.movieRepository, times(1)).findAll();
    }

    @Test
    void shouldFindTwoMoviesWhereFirstTitleIsNobodyEn() {
        when(this.movieRepository.findAll()).thenReturn(
                List.of(this.dummyMovies.getNobody(), this.dummyMovies.getWolf()));
        List<MovieDto> movieDtos = this.movieService.findAll("en");
        assertThat(movieDtos.getFirst().title()).isEqualTo("My Name is Nobody - Title");
        assertThat(movieDtos.getLast().title()).isEqualTo("Dances with Wolves - Title");

        verify(this.movieRepository, times(1)).findAll();
    }

    @Test
    void shouldSaveMovie() throws LocaleNotFoundException, MalformedURLException, URISyntaxException {
        when(this.movieRepository.save(this.dummyMovies.getWolf())).thenReturn(this.dummyMovies.getWolf());
        this.movieService.save(this.dummyMovies.getWolfEnDto());
        verify(this.movieRepository, times(1)).save(this.dummyMovies.getWolf());
    }

        @Test
        public void shouldUpdateMovie() throws NotFoundException, MalformedURLException, URISyntaxException {
            MovieDto movieDto = this.dummyMovies.getWolfEnDto();
            Optional<Movie> movieOptional = Optional.of(this.dummyMovies.getWolf());
            Movie movie = this.dummyMovies.getWolf();

            when(this.movieRepository.findByUuid(movieDto.uuid())).thenReturn(movieOptional);
            when(this.movieRepository.save(LocalizedMovieDtoMapper.dtoToEntity(movieDto))).thenReturn(movie);

            this.movieService.update(movieDto, movieDto.uuid());

            verify(this.movieRepository, times(1)).findByUuid(movieDto.uuid());
            verify(this.movieRepository, times(1)).save(movie);
        }

        @Test
        public void shouldFailUpdateMovieDueToMovieDoesNotExist() {
            UUID notExistingMovieUuid = UUID.randomUUID();
            when(this.movieRepository.findByUuid(notExistingMovieUuid)).thenReturn(Optional.empty());

            Exception exception =
                    assertThrows(NotFoundException.class, () -> this.movieService.update(this.dummyMovies.getWolfEnDto(),
                    notExistingMovieUuid));
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
        assertThat(this.movieService.findByUuid(persistedMovie.get().getUuid(), "en").uuid()).isEqualTo(this.dummyMovies.getWolfEnDto().uuid());
        verify(this.movieRepository, times(1)).findByUuid(persistedMovie.get().getUuid());
    }

    @Test
    public void shouldFailFindMovieByUuidDueToMovieDoesNotExist() {
        UUID notExistingMovieUuid = UUID.randomUUID();
        when(this.movieRepository.findByUuid(notExistingMovieUuid)).thenReturn(Optional.empty());
        Exception exception =
                assertThrows(NotFoundException.class, () -> this.movieService.findByUuid(notExistingMovieUuid, "en"));
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
        verify(this.movieRepository, times(1)).findByUuid(notExistingMovieUuid);
    }

    @Test
    public void shouldFailSaveMovieDueToUuidAlreadyExists() {
        when(this.movieRepository.existsByUuid(this.dummyMovies.getNobody().getUuid())).thenReturn(true);
        Exception exception = assertThrows(DataIntegrityViolationException.class,
                () -> this.movieService.save(LocalizedMovieDtoMapper.entityToDto(this.dummyMovies.getNobody(), "en")));
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
