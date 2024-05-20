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

package de.dittwald.cinemap.repositoryui.movies;

import de.dittwald.cinemap.repositoryui.validation.Iso6391Constraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieFlat implements Comparable<MovieFlat> {

        private @NotNull UUID uuid;

        @Min(value = -2147483648) // From TMDB API Reference movie Details
        @Max(value = 2147483647) // https://developer.themoviedb.org/reference/movie-details
        private Integer tmdbId;

        @Min(value = 1700)
        private Integer releaseYear;

        private Map<Integer, @Size(max = 50) String> genres;

        @Size(max = 50)
        private String imdbId;

        @Iso6391Constraint
        private String locale;

        @Size(max = 255)
        private String title;

        @Size(max = 5000)
        private String overview;

        @Size(max = 255)
        private String tagline;

        private URL posterUrl;

        @Override
        public int compareTo(MovieFlat o) {
                return StringUtils.compare(getTitle(), o.getTitle());
        }
}
