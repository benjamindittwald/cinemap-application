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

import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.movie.dto.MovieFlatDto;
import de.dittwald.cinemap.repository.movie.entity.LocalizedId;
import de.dittwald.cinemap.repository.movie.entity.LocalizedMovie;
import de.dittwald.cinemap.repository.movie.entity.Movie;
import de.dittwald.cinemap.repository.movie.repository.MovieRepository;
import de.dittwald.cinemap.repository.movie.util.LocalizedMovieDtoMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieFlatDto> findAll(String locale) {
        List<MovieFlatDto> movieFlatDtos = new ArrayList<>();

        for (Movie movie : this.movieRepository.findAll()) {
            try {
                movieFlatDtos.add(LocalizedMovieDtoMapper.entityToDto(movie, locale));
            } catch (LocaleNotFoundException ex) {
                try {
                    movieFlatDtos.add(LocalizedMovieDtoMapper.entityToDto(movie, "en"));
                } catch (LocaleNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return movieFlatDtos;
    }

    public MovieFlatDto findByUuid(UUID uuid, String locale) throws NotFoundException {

        MovieFlatDto movieFlatDto = null;

        Movie movie = this.movieRepository.findByUuid(uuid).orElseThrow(() -> new NotFoundException("Movie not found"));

        try {
            movieFlatDto = LocalizedMovieDtoMapper.entityToDto(movie, locale);
        } catch (LocaleNotFoundException e) {
            try {
                movieFlatDto = LocalizedMovieDtoMapper.entityToDto(movie, "en");
            } catch (LocaleNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        return movieFlatDto;
    }

    public void save(MovieFlatDto movieFlatDto) throws DataIntegrityViolationException {
        if (this.movieRepository.existsByUuid(movieFlatDto.uuid())) {
            throw new DataIntegrityViolationException("UUID already in use");
        } else {
            this.movieRepository.save(LocalizedMovieDtoMapper.dtoToEntity(movieFlatDto));
        }
    }

//    public void save(CompleteMovieDto completeMovieDto) throws DataIntegrityViolationException {
//        if (this.movieRepository.existsByUuid(completeMovieDto.uuid())) {
//            throw new DataIntegrityViolationException("UUID already in use");
//        } else {
//            this.movieRepository.save(LocalizedMovieDtoMapper.completeMovieDtoToEntity(completeMovieDto));
//        }
//    }

    public void update(MovieFlatDto movieFlatDto, UUID uuid) throws NotFoundException {

        Optional<Movie> movieOptional = this.movieRepository.findByUuid(uuid);

        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            movie.setUuid(uuid);
            movie.setGenres(movieFlatDto.genres());
            movie.setReleaseYear(movieFlatDto.releaseYear());
            movie.setTmdbId(movieFlatDto.tmdbId());
            movie.setImdbId(movieFlatDto.imdbId());
            movie.getLocalizedMovies()
                    .put(movieFlatDto.locale(),
                            new LocalizedMovie(new LocalizedId(movieFlatDto.locale()), movie, movieFlatDto.title(),
                                    movieFlatDto.overview(), movieFlatDto.tagline(), movieFlatDto.posterUrl()));
            this.movieRepository.save(movie);
        } else {
            throw new NotFoundException("Movie not found");
        }
    }

//    public void update(CompleteMovieDto completeMovieDto, UUID uuid) throws NotFoundException {
//        Optional<Movie> movieOptional = this.movieRepository.findByUuid(uuid);
//        if (movieOptional.isPresent()) {
//            Movie movie = movieOptional.get();
//            movie.setUuid(uuid);
//            movie.setGenres(completeMovieDto.genres());
//            movie.setReleaseYear(completeMovieDto.releaseYear());
//            movie.setTmdbId(completeMovieDto.tmdbId());
//            movie.setImdbId(completeMovieDto.imdbId());
//            movie.setLocalizedMovies(completeMovieDto.localizedMovies());
//        } else {
//            throw new NotFoundException("Movie not found");
//        }
//    }

    public void deleteByUuid(UUID uuid) throws NotFoundException {
        if (this.movieRepository.existsByUuid(uuid)) {
            this.movieRepository.deleteByUuid(uuid);
        } else {
            throw new NotFoundException("Movie not found");
        }
    }

    public void deleteAll() {
        this.movieRepository.deleteAll();
    }
}
