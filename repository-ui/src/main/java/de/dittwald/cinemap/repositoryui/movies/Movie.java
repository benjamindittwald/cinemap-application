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

package de.dittwald.cinemap.repositoryui.movies;


import de.dittwald.cinemap.repositoryui.validation.Iso639Constraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    private UUID uuid;

    @NotNull
    private Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> title = new HashMap<>();

    @Min(value = -2147483648) // From TMDB API Reference movie Details
    @Max(value = 2147483647) // https://developer.themoviedb.org/reference/movie-details
    private Integer tmdbId;

    @Min(value = 1700)
    private Integer releaseYear;

    private Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> tagline = new HashMap<>();

    private Map<@Iso639Constraint String, @Size(min = 1, max = 5000) String> overview = new HashMap<>();

    private Map<Integer, @Size(min = 1, max = 50) String> genres = new HashMap<>();

    private String poster;

    @Size(min = 1, max = 50)
    private String imdbId;

    @Override
    public String toString() {
        return "Movie{" + "uuid=" + uuid + ", title=" + title + ", tmdbId=" + tmdbId + ", releaseYear=" + releaseYear +
                ", tagline=" + tagline + ", overview=" + overview + ", genres=" + genres + ", poster='" + poster +
                '\'' + ", imdbId='" + imdbId + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Movie movie = (Movie) o;
        return Objects.equals(uuid, movie.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
