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

package de.dittwald.cinemap.repository.movie;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.dittwald.cinemap.repository.validation.Iso639Constraint;
import de.dittwald.cinemap.repository.validation.UuidConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public record MovieDto(

        @NotNull @UuidConstraint UUID uuid,

        @NotNull Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> title,

        @Min(value = -2147483648) // From TMDB API Reference movie Details
        @Max(value = 2147483647) // https://developer.themoviedb.org/reference/movie-details
        Integer tmdbId,

        @Min(value = 1700) Integer releaseYear,

        Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> tagline,
        Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> overview,
        Map<Integer, @Size(min = 1, max = 50) String> genres,

        @JsonDeserialize(using = ByteArrayDeserializer.class) @JsonSerialize(using = ByteArraySerializer.class) Byte[] poster,

        @Size(min = 1, max = 50) String imdbId) {
}
