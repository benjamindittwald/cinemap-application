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

package de.dittwald.cinemap.repository.scene.service;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest({SceneService.class})
@AutoConfigureMockMvc
class SceneServiceTest {

//    @Autowired
//    private MovieSceneService movieSceneService;
//
//    @MockBean
//    private MovieSceneRepository movieSceneRepository;
//
//    @MockBean
//    private MovieRepository movieRepository;
//
//    @Autowired
//    private MovieSceneDtoMapper movieSceneDtoMapper;
//
//    private List<MovieScene> moviesScenes = new ArrayList<>();
//
//    private MovieSceneOnlyDto movieSceneOnlyDto;
//
//    @BeforeEach
//    void setUp() {
//        List<Movie> movies = new ArrayList<>();
//        movies.add(
//                new Movie(UUID.randomUUID(), Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"),
//                        1051896, 1970,  Map.of("deu", "Der mit dem Wolf tanzt TAGLINE", "eng", "Dances with Wolves TAGLINE"),
//                        Map.of("deu", "Der mit dem Wolf tanzt OVERVIEW", "eng", "Dances with Wolves OVERVIEW"),
//                        Map.of(80, "western", 85, "Thriller"), "https://image.tmdb.org/t/p/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg", "imdbId"));
//        movies.add(new Movie(UUID.randomUUID(), Map.of("deu", "Mein Name ist Nobody", "eng", "My Name is Nobody"),
//                1051896, 1970,  Map.of("deu", "Mein Name ist Nobody TAGLINE", "eng", "DMy Name is Nobody TAGLINE"),
//                Map.of("deu", "Mein Name ist Nobody OVERVIEW", "eng", "My Name is Nobody OVERVIEW"),
//                Map.of(80, "western", 85, "Thriller"), "https://image.tmdb.org/t/p/w300/3JWLA3OYN6olbJXg6dDWLWiCxpn.jpg", "imdbId"));
//
//        this.moviesScenes = new ArrayList<>();
//        this.moviesScenes.add(new MovieScene(UUID.randomUUID(), 13404954L, 52520008L,
//                Map.of("deu", "Der mit dem Wolf " + "tanzt Szene 1", "eng", "Dances with Wolves scene 1"),
//                movies.getFirst()));
//        this.moviesScenes.add(new MovieScene(UUID.randomUUID(), 13404954L, 52520008L,
//                Map.of("deu", "Der mit dem Wolf " + "tanzt Szene 2", "eng", "Dances with Wolves scene 2"),
//                movies.getFirst()));
//
//        this.movieSceneOnlyDto = new MovieSceneOnlyDto(UUID.randomUUID(), 13404954L, 52520008L,
//                Map.of("deu", "Der mit dem Wolf " + "tanzt Szene 1", "eng", "Dances with Wolves scene 1"));
//    }
//
//    @Test
//    public void shouldFailSaveMovieSceneDueToNotExistingMovie() throws Exception {
//        UUID uuid = UUID.randomUUID();
//        when(this.movieRepository.findByUuid(uuid)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(NotFoundException.class, () -> {
//            this.movieSceneService.save(this.movieSceneOnlyDto, uuid);
//        });
//
//        assertThat(exception.getMessage()).isEqualTo("Movie not found");
//        verify(this.movieRepository, times(1)).findByUuid(uuid);
//    }
//
//    @Test
//    public void shouldFindMovieSceneByUuid() {
//        when(this.movieSceneRepository.findByUuid(this.moviesScenes.getFirst().getUuid())).thenReturn(
//                Optional.of(this.moviesScenes.getFirst()));
//        assertThat(this.movieSceneRepository.findByUuid(this.moviesScenes.getFirst().getUuid()).get()).isEqualTo(
//                this.moviesScenes.getFirst());
//        verify(this.movieSceneRepository, times(1)).findByUuid(this.moviesScenes.getFirst().getUuid());
//    }
//
//    @Test
//    public void shouldFailFindMovieSceneByUuidAndShouldThrowException() {
//        UUID uuid = UUID.randomUUID();
//        when(this.movieSceneRepository.findByUuid(uuid)).thenReturn(Optional.empty());
//
//        Exception exception = assertThrows(NotFoundException.class, () -> this.movieSceneService.findByUuid(uuid));
//
//        assertThat(exception.getMessage()).isEqualTo("Movie scene not found");
//        verify(this.movieSceneRepository, times(1)).findByUuid(uuid);
//    }
//
//    @Test
//    public void shouldFindAllMovieScenes() {
//        when(this.movieSceneRepository.findAll()).thenReturn(moviesScenes);
//        assertThat(this.movieSceneService.findAll().size()).isEqualTo(2);
//        verify(this.movieSceneRepository, times(1)).findAll();
//    }
//
//    @Test
//    public void shouldDeleteByUuid() throws NotFoundException {
//        UUID uuid = UUID.randomUUID();
//        when(this.movieSceneRepository.existsByUuid(uuid)).thenReturn(true);
//        when(this.movieSceneRepository.existsByUuid(uuid)).thenReturn(true);
//        this.movieSceneService.deleteByUuid(uuid);
//        verify(this.movieSceneRepository, times(1)).existsByUuid(uuid);
//        verify(this.movieSceneRepository, times(1)).deleteByUuid(uuid);
//    }
//
//    @Test
//    public void shouldFailDeleteByUuidAndShouldThrowException() {
//        UUID uuid = UUID.randomUUID();
//        when(this.movieSceneRepository.existsByUuid(uuid)).thenReturn(false);
//        Exception exception = assertThrows(NotFoundException.class, () -> this.movieSceneService.deleteByUuid(uuid));
//        assertThat(exception.getMessage()).isEqualTo("Movie scene not found");
//        verify(this.movieSceneRepository, times(1)).existsByUuid(uuid);
//    }
//
//    @Test
//    public void shouldUpdateMovieScene() throws NotFoundException {
//        UUID movieSceneUuid = this.movieSceneOnlyDto.uuid();
//        UUID movieUuid = this.moviesScenes.getFirst().getMovie().getUuid();
//
//        when(this.movieSceneRepository.findByUuid(movieSceneUuid)).thenReturn(
//                Optional.of(this.moviesScenes.getFirst()));
//        when(this.movieRepository.findByUuid(movieUuid)).thenReturn(
//                Optional.of(this.moviesScenes.getFirst().getMovie()));
//        when(this.movieSceneRepository.save(this.moviesScenes.getFirst())).thenReturn(this.moviesScenes.getFirst());
//
//        Assertions.assertThat(this.movieSceneService.update(this.movieSceneOnlyDto, movieUuid, movieSceneUuid).uuid())
//                .isEqualTo(this.movieSceneDtoMapper.movieSceneToMovieSceneDto(this.moviesScenes.getFirst()).uuid());
//
//        verify(this.movieSceneRepository, times(1)).findByUuid(movieSceneUuid);
//        verify(this.movieRepository, times(1)).findByUuid(movieUuid);
//        verify(this.movieSceneRepository, times(1)).save(this.moviesScenes.getFirst());
//    }
//
//    @Test
//    public void shouldFailUpdateMovieSceneDueToNotExistingMovieScene() throws NotFoundException {
//        when(this.movieSceneRepository.existsByUuid(any())).thenReturn(false);
//
//        Exception exception = assertThrows(NotFoundException.class,
//                () -> this.movieSceneService.update(this.movieSceneOnlyDto,
//                        this.moviesScenes.getFirst().getMovie().getUuid(), this.movieSceneOnlyDto.uuid()));
//
//        Assertions.assertThat(exception.getMessage()).isEqualTo("Movie scene not found");
//        verify(this.movieSceneRepository, times(1)).findByUuid(this.movieSceneOnlyDto.uuid());
//    }
//
//    @Test
//    public void shouldSaveNewMovieScene() throws Exception {
//        when(this.movieRepository.findByUuid(this.moviesScenes.getFirst().getMovie().getUuid())).thenReturn(
//                Optional.of(this.moviesScenes.getFirst().getMovie()));
//        when(this.movieSceneRepository.findByUuid(any())).thenReturn(Optional.empty());
//        when(this.movieSceneRepository.save(this.moviesScenes.getFirst())).thenReturn(this.moviesScenes.getFirst());
//
//        Assertions.assertThat(
//                        this.movieSceneService.save(this.movieSceneOnlyDto,
//                                this.moviesScenes.getFirst().getMovie().getUuid()).uuid())
//                .isEqualTo(this.movieSceneDtoMapper.movieSceneToMovieSceneDto(this.moviesScenes.getFirst()).uuid());
//
//        verify(this.movieRepository, times(1)).findByUuid(this.moviesScenes.getFirst().getMovie().getUuid());
//        verify(this.movieSceneRepository, times(1)).findByUuid(this.movieSceneOnlyDto.uuid());
//        verify(this.movieSceneRepository, times(1)).save(this.moviesScenes.getFirst());
//    }
//
//    @Test
//    public void shouldFailSaveMovieSceneAndShouldThrowUuidInUseException() {
//        when(this.movieRepository.findByUuid(this.moviesScenes.getFirst().getMovie().getUuid())).thenReturn(Optional.of(this.moviesScenes.getFirst().getMovie()));
//        when(this.movieSceneRepository.findByUuid(this.movieSceneOnlyDto.uuid())).thenReturn(Optional.of(this.moviesScenes.getFirst()));
//
//        Exception exception = assertThrows(UuidInUseException.class,
//                () -> this.movieSceneService.save(this.movieSceneOnlyDto, this.moviesScenes.getFirst().getMovie().getUuid()));
//
//        Assertions.assertThat(exception.getMessage()).isEqualTo("UUID already in use");
//
//        verify(this.movieRepository, times(1)).findByUuid(this.moviesScenes.getFirst().getMovie().getUuid());
//        verify(this.movieSceneRepository, times(1)).findByUuid(this.movieSceneOnlyDto.uuid());
//    }
//
//    @Test
//    public void shouldDeleteAllMovieScenes() {
//        this.movieSceneService.deleteAll();
//        verify(this.movieSceneRepository, times(1)).deleteAll();
//    }
//
//    @Test
//    public void shouldFindAllMovieScenesOfMovie() throws NotFoundException {
//        when(this.movieRepository.existsByUuid(this.moviesScenes.getFirst().getMovie().getUuid())).thenReturn(true);
//        when(this.movieSceneRepository.findAllScenesOfMovie(this.moviesScenes.getFirst().getMovie().getUuid())).thenReturn(Optional.of(this.moviesScenes));
//        this.movieSceneService.findAllScenesOfMovie(this.moviesScenes.getFirst().getMovie().getUuid());
//        verify(this.movieRepository, times(1)).existsByUuid(this.moviesScenes.getFirst().getMovie().getUuid());
//        verify(this.movieSceneRepository, times(1)).findAllScenesOfMovie(this.moviesScenes.getFirst().getMovie().getUuid());
//    }
//
//    @Test
//    public void shouldFailFindAllMovieScenesOfMovieDueToNotExistingMovie() throws NotFoundException {
//        when(this.movieRepository.existsByUuid(this.moviesScenes.getFirst().getMovie().getUuid())).thenReturn(false);
//
//        Exception exception = assertThrows(NotFoundException.class,
//                () -> this.movieSceneService.findAllScenesOfMovie(this.moviesScenes.getFirst().getMovie().getUuid()));
//
//        Assertions.assertThat(exception.getMessage()).isEqualTo("Movie not found");
//
//        verify(this.movieRepository, times(1)).existsByUuid(this.moviesScenes.getFirst().getMovie().getUuid());
//    }
}