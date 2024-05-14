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

package de.dittwald.cinemap.repository.movie.util;

import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.movie.dto.MovieFlatDto;
import de.dittwald.cinemap.repository.movie.dto.MovieLocalizationEntryDto;
import de.dittwald.cinemap.repository.movie.entity.LocalizedId;
import de.dittwald.cinemap.repository.movie.entity.LocalizedMovie;
import de.dittwald.cinemap.repository.movie.entity.Movie;

public class LocalizedMovieDtoMapper {

    public static MovieFlatDto entityToDto(Movie entity, String locale) throws LocaleNotFoundException {

        if (entity.getLocalizedMovies().get(locale) == null) {
            throw new LocaleNotFoundException("Locale %s not found for movie %s".formatted(locale, entity));
        }

        return new MovieFlatDto(entity.getUuid(), entity.getTmdbId(), entity.getReleaseYear(), entity.getGenres(),
                entity.getImdbId(), entity.getLocalizedMovies().get(locale).getLocalizedId().getLocale(),
                entity.getLocalizedMovies().get(locale).getTitle(),
                entity.getLocalizedMovies().get(locale).getOverview(),
                entity.getLocalizedMovies().get(locale).getTagline(),
                entity.getLocalizedMovies().get(locale).getPosterUrl());
    }

    public static Movie dtoToEntity(MovieFlatDto dto) {
        Movie entity = new Movie();
        entity.setUuid(dto.uuid());
        entity.setGenres(dto.genres());
        entity.setTmdbId(dto.tmdbId());
        entity.setReleaseYear(dto.releaseYear());
        entity.setImdbId(dto.imdbId());

        LocalizedMovie localizedEntity = new LocalizedMovie();
        LocalizedId localizedId = new LocalizedId(dto.locale());
        localizedEntity.setLocalizedId(localizedId);
        localizedEntity.setTitle(dto.title());
        localizedEntity.setOverview(dto.overview());
        localizedEntity.setTagline(dto.tagline());
        localizedEntity.setPosterUrl(dto.posterUrl());
        localizedEntity.setMovie(entity);

        entity.getLocalizedMovies().put(localizedId.getLocale(), localizedEntity);
        return entity;
    }
}
