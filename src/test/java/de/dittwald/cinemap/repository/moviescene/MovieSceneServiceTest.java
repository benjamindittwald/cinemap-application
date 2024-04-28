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
import de.dittwald.cinemap.repository.movie.Movie;
import de.dittwald.cinemap.repository.movie.MovieRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebMvcTest({MovieSceneService.class, MovieSceneDtoMapper.class})
@AutoConfigureMockMvc
class MovieSceneServiceTest {

    @Autowired
    private MovieSceneService movieSceneService;

    @MockBean
    private MovieSceneRepository movieSceneRepository;

    @MockBean
    private MovieRepository movieRepository;

    @Autowired
    private MovieSceneDtoMapper movieSceneDtoMapper;

    private List<MovieScene> moviesScenes = new ArrayList<>();

    private MovieSceneOnlyDto movieSceneOnlyDto;

    @BeforeEach
    void setUp() {
        List<Movie> movies = new ArrayList<>();
        movies.add(
                new Movie(UUID.randomUUID(), Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with " + "Wolves"),
                        "https://www" + ".imdb" + ".com/title/tt0099348/?ref_=ext_shr_lnk"));
        movies.add(new Movie(UUID.randomUUID(), Map.of("deu", "Mein Name is Nobody", "eng", "My Name Is Nobody"),
                "https://www" + ".imdb.com/title/tt0070215/?ref_=ext_shr_lnk"));

        moviesScenes = new ArrayList<>();
        moviesScenes.add(new MovieScene(UUID.randomUUID(), 13404954L, 52520008L,
                Map.of("deu", "Der mit dem Wolf " + "tanzt Szene 1", "eng", "Dances with Wolves scene 1"),
                movies.getFirst()));
        moviesScenes.add(new MovieScene(UUID.randomUUID(), 13404954L, 52520008L,
                Map.of("deu", "Der mit dem Wolf " + "tanzt Szene 2", "eng", "Dances with Wolves scene 2"),
                movies.getFirst()));

        movieSceneOnlyDto = new MovieSceneOnlyDto(UUID.randomUUID(), 13404954L, 52520008L,
                Map.of("deu", "Der mit dem Wolf " + "tanzt Szene 1", "eng", "Dances with Wolves scene 1"));
    }

    @Test
    public void shouldFailSaveMovieSceneDueToNotExistingMovie() throws Exception {
        when(this.movieRepository.findByUuid(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            this.movieSceneService.save(this.movieSceneOnlyDto, UUID.randomUUID());
        });

        assertThat(exception.getMessage()).isEqualTo("Movie not found");
    }

    @Test
    public void shouldFindMovieSceneByUuid() {
        when(this.movieSceneRepository.findByUuid(this.moviesScenes.getFirst().getUuid())).thenReturn(
                Optional.of(this.moviesScenes.getFirst()));
        assertThat(this.movieSceneRepository.findByUuid(this.moviesScenes.getFirst().getUuid()).get()).isEqualTo(
                this.moviesScenes.getFirst());
    }

    @Test
    public void shouldFailFindMovieSceneByUuidAndShouldThrowException() {
        when(this.movieSceneRepository.save(any())).thenReturn(Optional.empty());

        Exception exception =
                assertThrows(NotFoundException.class, () -> this.movieSceneService.findByUuid(UUID.randomUUID()));

        assertThat(exception.getMessage()).isEqualTo("Movie scene not found");
    }

    @Test
    public void shouldFindAllMovieScenes() {
        when(this.movieSceneRepository.findAll()).thenReturn(moviesScenes);
        assertThat(this.movieSceneService.findAll().size()).isEqualTo(2);
    }

    @Test
    public void shouldDeleteByUuid() throws NotFoundException {
        MovieSceneRepository movieSceneRepositoryMock = mock(MovieSceneRepository.class);
        when(this.movieSceneRepository.existsByUuid(any())).thenReturn(true);
        doNothing().when(movieSceneRepositoryMock).delete(any());
        this.movieSceneService.deleteByUuid(this.moviesScenes.getFirst().getUuid());
        // Todo: Implement proper assertion!
    }

    @Test
    public void shouldFailDeleteByUuidAndShouldThrowException() {
        when(this.movieSceneRepository.existsByUuid(any())).thenReturn(false);

        Exception exception =
                assertThrows(NotFoundException.class, () -> this.movieSceneService.deleteByUuid(UUID.randomUUID()));

        assertThat(exception.getMessage()).isEqualTo("Movie scene not found");
    }

    @Test
    public void shouldUpdateMovieScene() throws NotFoundException {
        when(this.movieSceneRepository.findByUuid(any())).thenReturn(Optional.of(this.moviesScenes.getFirst()));
        when(this.movieSceneRepository.save(this.moviesScenes.getFirst())).thenReturn(this.moviesScenes.getFirst());
        when(this.movieRepository.findByUuid(this.moviesScenes.getFirst().getMovie().getUuid())).thenReturn(Optional.of(this.moviesScenes.getFirst().getMovie()));

        Assertions.assertThat(this.movieSceneService.update(movieSceneOnlyDto, this.moviesScenes.getFirst().getMovie().getUuid())).isEqualTo(
                this.movieSceneDtoMapper.movieSceneToMovieSceneDto(this.moviesScenes.getFirst()));
    }

    @Test
    public void shouldFailUpdateMovieSceneDueToNotExistingMovieScene() throws NotFoundException {
        when(this.movieSceneRepository.existsByUuid(any())).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class, () -> this.movieSceneService.update(
                this.movieSceneOnlyDto, this.moviesScenes.getFirst().getMovie().getUuid()
        ));

        Assertions.assertThat(exception.getMessage()).isEqualTo("Movie scene not found");
    }

    @Test
    public void shouldSaveNewMovieScene() throws Exception {
        when(this.movieRepository.findByUuid(any())).thenReturn(Optional.of(this.moviesScenes.getFirst().getMovie()));
        when(this.movieSceneRepository.findByUuid(any())).thenReturn(Optional.empty());
        when(this.movieSceneRepository.save(this.moviesScenes.getFirst())).thenReturn(this.moviesScenes.getFirst());
        Assertions.assertThat(this.movieSceneService.save(this.movieSceneOnlyDto, this.moviesScenes.getFirst().getUuid())).isEqualTo(
                this.movieSceneDtoMapper.movieSceneToMovieSceneDto(this.moviesScenes.getFirst()));
    }

    @Test
    public void shouldFailSaveMovieSceneAndShouldThrowUuidInUseException() {
        when(this.movieRepository.findByUuid(any())).thenReturn(Optional.of(this.moviesScenes.getFirst().getMovie()));
        when(this.movieSceneRepository.findByUuid(any())).thenReturn(Optional.of(this.moviesScenes.getFirst()));

        Exception exception = assertThrows(UuidInUseException.class, () -> this.movieSceneService.save(
                this.movieSceneOnlyDto, this.moviesScenes.getFirst().getUuid()
        ));

        Assertions.assertThat(exception.getMessage()).isEqualTo("UUID already in use");
    }

}