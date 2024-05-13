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

public class LocalizedMovieDtoMapper {

    public static MovieDto entityToDto(Movie entity, String locale) throws LocaleNotFoundException {

        if (entity.getLocalizedMovies().get(locale) == null) {
            throw new LocaleNotFoundException("Locale %s not found for movie %s".formatted(locale, entity));
        }

        return new MovieDto(entity.getUuid(), entity.getTmdbId(), entity.getReleaseYear(), entity.getGenres(),
                entity.getImdbId(), entity.getLocalizedMovies().get(locale).getLocalizedId().getLocale(),
                entity.getLocalizedMovies().get(locale).getTitle(),
                entity.getLocalizedMovies().get(locale).getOverview(),
                entity.getLocalizedMovies().get(locale).getTagline(),
                entity.getLocalizedMovies().get(locale).getPosterUrl());
    }

    public static Movie dtoToEntity(MovieDto dto) {
        Movie movie = new Movie();
        movie.setUuid(dto.uuid());
        movie.setGenres(dto.genres());
        movie.setTmdbId(dto.tmdbId());
        movie.setReleaseYear(dto.releaseYear());
        movie.setImdbId(dto.imdbId());

        LocalizedMovie localizedMovie = new LocalizedMovie();
        LocalizedId localizedId = new LocalizedId(dto.locale());
        localizedMovie.setLocalizedId(localizedId);
        localizedMovie.setTitle(dto.title());
        localizedMovie.setOverview(dto.overview());
        localizedMovie.setTagline(dto.tagline());
        localizedMovie.setPosterUrl(dto.posterUrl());
        localizedMovie.setMovie(movie);

        movie.getLocalizedMovies().put(localizedId.getLocale(), localizedMovie);
        return movie;
    }

    public static CompleteMovieDto entityToCompleteDto(Movie entity) {
        return new CompleteMovieDto(entity.getUuid(), entity.getTmdbId(), entity.getReleaseYear(), entity.getGenres(),
                entity.getImdbId(), entity.getLocalizedMovies());
    }

    public static Movie completeMovieDtoToEntity(CompleteMovieDto dto) {
        return new Movie(dto.uuid(), dto.tmdbId(), dto.releaseYear(), dto.genres(), dto.imdbId(),
                dto.localizedMovies());
    }
}
