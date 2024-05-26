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

package de.dittwald.cinemap.repository.movie.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.exceptions.TmdbReadException;
import de.dittwald.cinemap.repository.exceptions.UuidInUseException;
import de.dittwald.cinemap.repository.movie.dto.MovieFlatDto;
import de.dittwald.cinemap.repository.movie.entity.LocalizedId;
import de.dittwald.cinemap.repository.movie.entity.LocalizedMovie;
import de.dittwald.cinemap.repository.movie.entity.Movie;
import de.dittwald.cinemap.repository.movie.repository.MovieRepository;
import de.dittwald.cinemap.repository.scene.entity.Scene;
import de.dittwald.cinemap.repository.scene.repository.SceneRepository;
import de.dittwald.cinemap.repository.tmdb.TmdbClient;
import de.dittwald.cinemap.repository.util.LocaleFallbackHandler;
import de.dittwald.cinemap.repository.movie.util.LocalizedMovieDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;

@Service
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;
    private final SceneRepository sceneRepository;
    private final TmdbClient tmdbClient;

    public MovieService(MovieRepository movieRepository, SceneRepository sceneRepository,
                        TmdbClient tmdbClient) {
        this.movieRepository = movieRepository;
        this.sceneRepository = sceneRepository;
        this.tmdbClient = tmdbClient;
    }

    @Transactional
    public List<MovieFlatDto> findAll(String locale) throws LocaleNotFoundException {
        List<MovieFlatDto> movieFlatDtos = new ArrayList<>();

        for (Movie movie : this.movieRepository.findAll()) {
            movieFlatDtos.add(
                    LocalizedMovieDtoMapper.entityToDto(movie, LocaleFallbackHandler.getMovieLocale(movie, locale)));
        }
        return movieFlatDtos;
    }

    @Transactional
    public MovieFlatDto findByUuid(UUID uuid, String locale) throws NotFoundException, LocaleNotFoundException {
        Movie movie = this.movieRepository.findByUuid(uuid).orElseThrow(() -> new NotFoundException("Movie not found"));
        return LocalizedMovieDtoMapper.entityToDto(movie, LocaleFallbackHandler.getMovieLocale(movie, locale));
    }

    public void save(MovieFlatDto movieFlatDto) throws UuidInUseException {
        if (this.movieRepository.existsByUuid(movieFlatDto.uuid())) {
            throw new UuidInUseException("UUID already in use");
        } else {
            this.movieRepository.save(LocalizedMovieDtoMapper.dtoToEntity(movieFlatDto));
        }
    }

    @Transactional
    public void update(MovieFlatDto movieFlatDto, UUID uuid) throws NotFoundException {

        Movie movie = this.movieRepository.findByUuid(uuid).orElseThrow(() -> new NotFoundException("Movie not found"));

        movie.setGenres(movieFlatDto.genres());
        movie.setReleaseYear(movieFlatDto.releaseYear());
        movie.setTmdbId(movieFlatDto.tmdbId());
        movie.setImdbId(movieFlatDto.imdbId());

        if (movie.getLocalizedMovies().containsKey(movieFlatDto.locale())) {
            LocalizedMovie updatedLocalizedMovie = movie.getLocalizedMovies().get(movieFlatDto.locale());
            updatedLocalizedMovie.setTitle(movieFlatDto.title());
            updatedLocalizedMovie.setTagline(movieFlatDto.tagline());
            updatedLocalizedMovie.setOverview(movieFlatDto.overview());
            updatedLocalizedMovie.setPosterUrl(movieFlatDto.posterUrl());
            updatedLocalizedMovie.setMovie(movie);
            movie.getLocalizedMovies().replace(movieFlatDto.locale(), updatedLocalizedMovie);
        } else {
            movie.getLocalizedMovies()
                    .put(movieFlatDto.locale(),
                            new LocalizedMovie(new LocalizedId(movieFlatDto.locale()), movie, movieFlatDto.title(),
                                    movieFlatDto.overview(), movieFlatDto.tagline(), movieFlatDto.posterUrl()));
        }
        this.movieRepository.save(movie);
    }

    @Transactional
    public void deleteByUuid(UUID uuid) throws NotFoundException {
        if (this.movieRepository.existsByUuid(uuid)) {

            // Deletes all scenes of a movie if present
            Optional<List<Scene>> scenesOptional = this.sceneRepository.findAllScenesOfMovieUuid(uuid);
            if (scenesOptional.isPresent()) {
                List<Long> sceneIds = scenesOptional.get().stream().map(Scene::getId).toList();
                this.sceneRepository.deleteAllById(sceneIds);
            }
            this.movieRepository.deleteByUuid(uuid);
        } else {
            throw new NotFoundException("Movie not found");
        }
    }

    @Transactional
    public void deleteAll() {
        log.info("Deleting all movies");
        this.sceneRepository.deleteAll();
        this.movieRepository.deleteAll();
    }

    @Transactional
    public void createMovieViaTmdbId(int tmdbId) throws TmdbReadException {
        try {
            this.movieRepository.save(this.tmdbClient.getMovieDetails(tmdbId));
        } catch (URISyntaxException | MalformedURLException | JsonProcessingException e) {
            throw new TmdbReadException(e.getMessage());
        }
    }
}
