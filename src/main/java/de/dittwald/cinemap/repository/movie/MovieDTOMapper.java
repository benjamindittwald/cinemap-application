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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MovieDTOMapper {

    public static MovieDto movieToDTO(Movie movie) {
        return new MovieDto(
                movie.getId(),
                movie.getTitle().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue())),
                movie.getImdbWebsiteUrl()
        );
    }

    public static Movie dtoToMovie(MovieDto movie) {
        return new Movie(
                movie.title().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> (String) e.getValue())),
                movie.imdbWebsiteUrl()
        );
    }

    public static List<Movie> dtoListToMovieList(List<MovieDto> movies) {
        return movies.stream().map(MovieDTOMapper::dtoToMovie).collect(Collectors.toList());
    }

    public static List<MovieDto> movieListToDtoList(List<Movie> movies) {
        return movies.stream().map(MovieDTOMapper::movieToDTO).collect(Collectors.toList());
    }
}
