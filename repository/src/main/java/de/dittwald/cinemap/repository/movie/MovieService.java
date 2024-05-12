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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieDto> findAll(String locale) {
        List<MovieDto> movieDtos = new ArrayList<>();

        for (Movie movie : this.movieRepository.findAll()) {
            try {
                movieDtos.add(LocalizedMovieDtoMapper.entityToDto(movie, locale));
            } catch (LocaleNotFoundException ex) {
                try {
                    movieDtos.add(LocalizedMovieDtoMapper.entityToDto(movie, "en"));
                } catch (LocaleNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return movieDtos;
    }

    public MovieDto findByUuid(UUID uuid, String locale) throws NotFoundException {

        MovieDto movieDto = null;

        Movie movie = this.movieRepository.findByUuid(uuid).orElseThrow(() -> new NotFoundException("Movie not found"));

        try {
            movieDto = LocalizedMovieDtoMapper.entityToDto(movie, locale);
        } catch (LocaleNotFoundException e) {
            try {
                movieDto = LocalizedMovieDtoMapper.entityToDto(movie, "en");
            } catch (LocaleNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }

        return movieDto;
    }

    public void save(MovieDto movieDto) throws DataIntegrityViolationException {
        if (this.movieRepository.existsByUuid(movieDto.uuid())) {
            throw new DataIntegrityViolationException("UUID already in use");
        } else if (movieDto.uuid() == null) {
            throw new DataIntegrityViolationException("UUID is mandatory");
        } else {
            this.movieRepository.save(LocalizedMovieDtoMapper.dtoToEntity(movieDto));
        }
    }

    public void update(MovieDto movieDto, UUID uuid) throws NotFoundException {

        Optional<Movie> movieOptional = this.movieRepository.findByUuid(uuid);

        if (movieOptional.isPresent()) {
            Movie movie = movieOptional.get();
            movie.setUuid(uuid);
            movie.setGenres(movieDto.genres());
            movie.setReleaseYear(movieDto.releaseYear());
            movie.setTmdbId(movieDto.tmdbId());
            movie.setImdbId(movieDto.imdbId());
            movie.getLocalizedMovies()
                    .put(movieDto.locale(),
                            new LocalizedMovie(new LocalizedId(movieDto.locale()), movie, movieDto.title(),
                                    movieDto.overview(), movieDto.tagline(), movieDto.posterUrl()));
            this.movieRepository.save(movie);
        } else {
            throw new NotFoundException("Movie not found");
        }
    }

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
