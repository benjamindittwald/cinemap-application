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
import de.dittwald.cinemap.repository.movie.dto.MovieLocalisationDto;
import de.dittwald.cinemap.repository.movie.dto.MovieLocalizationsDto;
import de.dittwald.cinemap.repository.movie.entity.LocalizedId;
import de.dittwald.cinemap.repository.movie.entity.LocalizedMovie;
import de.dittwald.cinemap.repository.movie.entity.Movie;
import de.dittwald.cinemap.repository.movie.repository.LocalizedMovieRepository;
import de.dittwald.cinemap.repository.movie.repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MovieLocalizationService {

    private final MovieRepository movieRepository;
    private final LocalizedMovieRepository localizedMovieRepository;

    public MovieLocalizationService(MovieRepository movieRepository,
                                    LocalizedMovieRepository localizedMovieRepository) {
        this.movieRepository = movieRepository;
        this.localizedMovieRepository = localizedMovieRepository;
    }

    public void update(MovieLocalizationsDto movieLocalizationsDto, UUID movieUuid, boolean override)
            throws NotFoundException {
        Optional<Movie> movieOptional = this.movieRepository.findByUuid(movieUuid);
        Movie movie = null;

        if (movieOptional.isPresent()) {
            movie = movieOptional.get();
        } else {
            throw new NotFoundException("Movie not found");
        }

        if (override) {
            movie.setLocalizedMovies(new HashMap<>());
        }

        for (MovieLocalisationDto dto : movieLocalizationsDto.localizations()) {
            movie.getLocalizedMovies()
                    .put(dto.locale(),
                            new LocalizedMovie(new LocalizedId(dto.locale()), movie, dto.title(), dto.overview(),
                                    dto.tagline(), dto.posterUrl()));
        }

        this.movieRepository.save(movie);
    }

    public MovieLocalizationsDto getMovieLocalizations(UUID movieUuid) throws NotFoundException {
        Optional<List<LocalizedMovie>> localizedMovies = this.localizedMovieRepository.findAllByMovieUuid(movieUuid);

        if (localizedMovies.isEmpty()) {
            throw new NotFoundException("No localized movies found");
        }

        List<MovieLocalisationDto> localizedMoviesDto = new ArrayList<>();

        for (LocalizedMovie localizedMovie : localizedMovies.get()) {
            localizedMoviesDto.add(
                    new MovieLocalisationDto(localizedMovie.getLocalizedId().getLocale(), localizedMovie.getTitle(),
                            localizedMovie.getOverview(), localizedMovie.getTagline(), localizedMovie.getPosterUrl()));
        }

        return new MovieLocalizationsDto(movieUuid, localizedMoviesDto);
    }
}
