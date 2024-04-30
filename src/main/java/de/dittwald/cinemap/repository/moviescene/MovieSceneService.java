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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MovieSceneService {

    private final MovieSceneRepository movieSceneRepository;
    private final MovieSceneDtoMapper movieSceneDtoMapper;
    private final MovieRepository movieRepository;

    public MovieSceneService(MovieSceneRepository MovieSceneRepository, MovieSceneDtoMapper movieSceneDtoMapper,
                             MovieRepository movieRepository) {
        this.movieSceneRepository = MovieSceneRepository;
        this.movieSceneDtoMapper = movieSceneDtoMapper;
        this.movieRepository = movieRepository;
    }

    public MovieSceneDto findByUuid(UUID uuid) throws NotFoundException {
        Optional<MovieScene> movieScene = movieSceneRepository.findByUuid(uuid);
        if (movieScene.isPresent()) {
            return this.movieSceneDtoMapper.movieSceneToMovieSceneDto(movieScene.get());
        } else {
            throw new NotFoundException("Movie scene not found");
        }
    }

    public MovieSceneDto update(MovieSceneOnlyDto movieSceneOnlyDto, UUID movieUuid) throws NotFoundException {
        Optional<MovieScene> movieSceneOptional = movieSceneRepository.findByUuid(movieSceneOnlyDto.uuid());
        MovieScene movieScene;

        if (movieSceneOptional.isPresent()) {
            movieScene = movieSceneOptional.get();

            Optional<Movie> movieOptional = movieRepository.findByUuid(movieUuid);

            if (movieOptional.isPresent()) {
                if (movieOptional.get().getUuid() != movieSceneOnlyDto.uuid()) {
                    movieScene.setMovie(movieOptional.get());
                }
            } else {
                throw new NotFoundException("Movie not found");
            }
            return this.movieSceneDtoMapper.movieSceneToMovieSceneDto(this.movieSceneRepository.save(movieScene));
        } else {
            throw new NotFoundException("Movie scene not found");
        }
    }

    public void deleteByUuid(UUID uuid) throws NotFoundException {
        if (this.movieSceneRepository.existsByUuid(uuid)) {
            this.movieSceneRepository.deleteByUuid(uuid);
        } else {
            throw new NotFoundException("Movie scene not found");
        }
    }

    public MovieSceneDto save(MovieSceneOnlyDto movieSceneOnlyDto, UUID movieUuid)
            throws NotFoundException, UuidInUseException {
        Movie movie;
        Optional<Movie> movieOptional = movieRepository.findByUuid(movieUuid);
        Optional<MovieScene> movieSceneOptional;

        if (movieOptional.isPresent()) {
            movieSceneOptional = movieSceneRepository.findByUuid(movieSceneOnlyDto.uuid());
            if (movieSceneOptional.isEmpty()) {
                movie = movieOptional.get();
                MovieScene movieScene = this.movieSceneDtoMapper.movieSceneOnlyDtoToMovieScene(movieSceneOnlyDto);
                movieScene.setMovie(movie);
                return this.movieSceneDtoMapper.movieSceneToMovieSceneDto(this.movieSceneRepository.save(movieScene));
            } else {
                throw new UuidInUseException("UUID already in use");
            }
        } else {
            throw new NotFoundException("Movie not found");
        }
    }

    public List<MovieSceneDto> findAll() {
        List<MovieSceneDto> movieSceneDtos = new ArrayList<>();
        this.movieSceneRepository.findAll().forEach(movie -> {
            movieSceneDtos.add(this.movieSceneDtoMapper.movieSceneToMovieSceneDto(movie));
        });

        return movieSceneDtos;
    }

    public void deleteAll() {
        this.movieSceneRepository.deleteAll();
    }

    public List<MovieSceneDto> findAllScenesOfMovie(UUID movieUuid) throws NotFoundException {
        List<MovieSceneDto> movieSceneListDto = new ArrayList<>();
        if (this.movieRepository.existsByUuid(movieUuid)) {
            Optional<List<MovieScene>> movieScenesOptional = this.movieSceneRepository.findAllScenesOfMovie(movieUuid);
            movieScenesOptional.ifPresent(movieScenes -> movieScenes.forEach(movieScene -> movieSceneListDto.add(
                    this.movieSceneDtoMapper.movieSceneToMovieSceneDto(movieScene))));
        } else {
            throw new NotFoundException("Movie not found");
        }

        return movieSceneListDto;
    }
}
