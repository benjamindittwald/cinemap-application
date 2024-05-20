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

import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.movie.dto.MovieLocalizationEntryDto;
import de.dittwald.cinemap.repository.movie.dto.MovieLocalizationDto;
import de.dittwald.cinemap.repository.movie.entity.LocalizedId;
import de.dittwald.cinemap.repository.movie.entity.LocalizedMovie;
import de.dittwald.cinemap.repository.movie.entity.Movie;
import de.dittwald.cinemap.repository.movie.repository.MovieLocalizedRepository;
import de.dittwald.cinemap.repository.movie.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MovieLocalizationService {

    private final MovieRepository movieRepository;
    private final MovieLocalizedRepository movieLocalizedRepository;

    public MovieLocalizationService(MovieRepository movieRepository,
                                    MovieLocalizedRepository movieLocalizedRepository) {
        this.movieRepository = movieRepository;
        this.movieLocalizedRepository = movieLocalizedRepository;
    }

    @Transactional
    public void update(MovieLocalizationDto movieLocalizationDto, UUID movieUuid, boolean override)
            throws NotFoundException {
        Optional<Movie> movieOptional = this.movieRepository.findByUuid(movieUuid);
        Movie movie;

        if (movieOptional.isPresent()) {
            movie = movieOptional.get();
        } else {
            throw new NotFoundException("Movie not found");
        }

        if (override) {
            movie.getLocalizedMovies().clear();
            this.movieLocalizedRepository.deleteAll(this.movieLocalizedRepository.findAllByMovieUuid(movie.getUuid()).get());
            this.movieRepository.save(movie);
        }

        for (MovieLocalizationEntryDto dto : movieLocalizationDto.localizations()) {
            if (movie.getLocalizedMovies().containsKey(dto.locale())) {
                LocalizedMovie updatedLocalizedMovie = movie.getLocalizedMovies().get(dto.locale());
                updatedLocalizedMovie.setTitle(dto.title());
                updatedLocalizedMovie.setTagline(dto.tagline());
                updatedLocalizedMovie.setOverview(dto.overview());
                updatedLocalizedMovie.setPosterUrl(dto.posterUrl());
                updatedLocalizedMovie.setMovie(movie);
                movie.getLocalizedMovies().replace(dto.locale(), updatedLocalizedMovie);
            } else {
                movie.getLocalizedMovies()
                        .put(dto.locale(),
                                new LocalizedMovie(new LocalizedId(dto.locale()), movie, dto.title(), dto.overview(),
                                        dto.tagline(), dto.posterUrl()));
            }
        }

        this.movieRepository.save(movie);
    }

    @Transactional
    public MovieLocalizationDto getMovieLocalizations(UUID movieUuid) throws NotFoundException {
        Optional<List<LocalizedMovie>> localizedMovies = this.movieLocalizedRepository.findAllByMovieUuid(movieUuid);

        if (localizedMovies.isEmpty()) {
            throw new NotFoundException("No localized movies found");
        }

        List<MovieLocalizationEntryDto> localizedMoviesDto = new ArrayList<>();

        for (LocalizedMovie localizedMovie : localizedMovies.get()) {
            localizedMoviesDto.add(new MovieLocalizationEntryDto(localizedMovie.getLocalizedId().getLocale(),
                    localizedMovie.getTitle(), localizedMovie.getOverview(), localizedMovie.getTagline(),
                    localizedMovie.getPosterUrl()));
        }

        return new MovieLocalizationDto(movieUuid, localizedMoviesDto);
    }
}
