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
import de.dittwald.cinemap.repository.moviescene.MovieScene;
import de.dittwald.cinemap.repository.moviescene.MovieSceneRepository;
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

    @MockBean
    private MovieSceneRepository movieSceneRepository;

    List<Movie> movies = new ArrayList<>();

    List<MovieScene> moviesScenes;

    @BeforeEach
    void setUp() {
        this.movies = new ArrayList<>();
        this.movies.add(
                new Movie(UUID.randomUUID(), Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"),
                        1051896));
        this.movies.add(new Movie(UUID.randomUUID(), Map.of("deu", "Mein Name is Nobody", "eng", "My Name Is Nobody"),
                1051896));

        this.moviesScenes = new ArrayList<>();
        this.moviesScenes.add(new MovieScene(UUID.randomUUID(), 13404954L, 52520008L,
                Map.of("deu", "Der mit dem Wolf " + "tanzt Szene 1", "eng", "Dances with Wolves scene 1"),
                this.movies.getFirst()));
        this.moviesScenes.add(new MovieScene(UUID.randomUUID(), 13404954L, 52520008L,
                Map.of("deu", "Der mit dem Wolf " + "tanzt Szene 2", "eng", "Dances with Wolves scene 2"),
                this.movies.getFirst()));
    }

    @Test
    void shouldFindTwoMovies() {
        when(this.movieRepository.findAll()).thenReturn(movies);
        List<MovieDto> movieDtos = this.movieService.findAll();
        assertThat(movieDtos.size()).isEqualTo(2);
        verify(this.movieRepository, times(1)).findAll();
    }

    @Test
    void shouldFindTwoMoviesWhereFirstTitleIsWolf() {
        when(this.movieRepository.findAll()).thenReturn(movies);
        List<MovieDto> movieDtos = this.movieService.findAll();
        assertThat(movieDtos.getFirst().title().get("deu")).isEqualTo("Der mit dem Wolf tanzt");
        verify(this.movieRepository, times(1)).findAll();
    }

    @Test
    void shouldSaveMovie() {
        UUID uuid = UUID.randomUUID();
        MovieDto notYetPersistedMovie =
                new MovieDto(uuid, Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name"),
                        1051896);
        MovieDto persistedMovieDto =
                new MovieDto(uuid, Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name"),
                        1051896);
        Movie persistedMovie =
                new Movie(uuid, Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name"),
                        1051896);

        when(this.movieRepository.save(this.movieDtoMapper.movieDtoToMovie(notYetPersistedMovie))).thenReturn(
                persistedMovie);
        assertThat(this.movieService.save(notYetPersistedMovie)).isEqualTo(persistedMovieDto);
        verify(this.movieRepository, times(1)).save(this.movieDtoMapper.movieDtoToMovie(notYetPersistedMovie));
    }

    @Test
    public void shouldUpdateMovie() throws NotFoundException {
        UUID uuid = UUID.randomUUID();
        MovieDto persistedMovieDto = new MovieDto(uuid,
                Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name", "fra",
                        "On continue à l'appeler Trinita"),
                1051896);
        Optional<Movie> persistedMovie = Optional.of(
                new Movie(uuid, Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name"),
                        1051896));
        Movie persistedMovieUpdated = new Movie(uuid,
                Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name", "fra",
                        "On continue à l'appeler Trinita"),
                1051896);

        when(this.movieRepository.findByUuid(uuid)).thenReturn(persistedMovie);
        when(this.movieRepository.save(this.movieDtoMapper.movieDtoToMovie(persistedMovieDto))).thenReturn(
                persistedMovieUpdated);

        assertThat(this.movieService.update(persistedMovieDto, uuid)).isEqualTo(
                this.movieDtoMapper.movieToMovieDto(persistedMovieUpdated));

        verify(this.movieRepository, times(1)).findByUuid(uuid);
        verify(this.movieRepository, times(1)).save(persistedMovieUpdated);
    }

    @Test
    public void shouldFailUpdateMoiveDueToMovieDoesNotExist() {
        UUID notExistingMovieUuid = UUID.randomUUID();
        MovieDto persistedMovieDto = new MovieDto(UUID.randomUUID(),
                Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name", "fra",
                        "On continue à l'appeler Trinita"),
                1051896);
        when(this.movieRepository.findByUuid(notExistingMovieUuid)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> this.movieService.update(persistedMovieDto, notExistingMovieUuid));
        assertThat(exception.getMessage()).isEqualTo("Movie not found");
        verify(this.movieRepository, times(1)).findByUuid(notExistingMovieUuid);
    }

    @Test
    public void shouldDeleteMovie() throws NotFoundException {
        when(this.movieRepository.existsByUuid(this.movies.getFirst().getUuid())).thenReturn(true);
        when(this.movieSceneRepository.findAllScenesOfMovie(this.movies.getFirst().getUuid())).thenReturn(
                Optional.of(this.moviesScenes));
        doNothing().when(this.movieSceneRepository).deleteAll(this.moviesScenes);
        doNothing().when(this.movieRepository).deleteByUuid(this.movies.getFirst().getUuid());
        this.movieService.deleteByUuid(this.movies.getFirst().getUuid());
        verify(this.movieRepository, times(1)).existsByUuid(this.movies.getFirst().getUuid());
        verify(this.movieSceneRepository, times(1)).findAllScenesOfMovie(this.movies.getFirst().getUuid());
        verify(this.movieSceneRepository, times(1)).deleteAll(this.moviesScenes);
        verify(this.movieRepository, times(1)).deleteByUuid(this.movies.getFirst().getUuid());
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
        Optional<Movie> persistedMovie = Optional.of(new Movie(UUID.randomUUID(),
                Map.of("deu", "Der Kleine und der müde Joe", "eng", "Trinity " + "Is Still My Name"),
                1051896));

        when(this.movieRepository.findByUuid(persistedMovie.get().getUuid())).thenReturn(persistedMovie);
        assertThat(this.movieService.findByUuid(persistedMovie.get().getUuid())).isEqualTo(
                this.movieDtoMapper.movieToMovieDto(persistedMovie.get()));
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
        UUID existingMovieUuid = movies.getFirst().getUuid();
        when(this.movieRepository.existsByUuid(existingMovieUuid)).thenReturn(true);
        Exception exception = assertThrows(DataIntegrityViolationException.class,
                () -> this.movieService.save(this.movieDtoMapper.movieToMovieDto(this.movies.getFirst())));
        assertThat(exception.getMessage()).isEqualTo("UUID already in use");
        verify(this.movieRepository, times(1)).existsByUuid(existingMovieUuid);
    }

    @Test
    public void shouldDeleteAllMovies() {
        doNothing().when(this.movieRepository).deleteAll();
        doNothing().when(this.movieSceneRepository).deleteAll();
        this.movieService.deleteAll();
        verify(this.movieRepository, times(1)).deleteAll();
        verify(this.movieSceneRepository, times(1)).deleteAll();
    }
}
