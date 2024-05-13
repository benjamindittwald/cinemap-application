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

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Table(name = "movies")
@Getter
@Setter
public class Movie {

    public Movie() {
        this.localizedMovies = new HashMap<>();
    }

    public Movie(UUID uuid, Integer tmdbId, Integer releaseYear,
                 Map<Integer,String> genres, String imdbId,
                 Map<String, LocalizedMovie> localizedMovies) {
        this.uuid = uuid;
        this.tmdbId = tmdbId;
        this.releaseYear = releaseYear;
        this.genres = genres;
        this.imdbId = imdbId;
        this.localizedMovies = localizedMovies;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Column(unique = true)
    private UUID uuid;

    @Version
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Long version;


    @Min(value = -2147483648) // From TMDB API Reference movie Details
    @Max(value = 2147483647) // https://developer.themoviedb.org/reference/movie-details
    private Integer tmdbId;

    @Column
    @Min(value = 1700)
    private Integer releaseYear;

    @ElementCollection
    @CollectionTable(name = "genres_number_mapping",
            joinColumns = {@JoinColumn(name = "number_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "genres_id")
    @Column(name = "genres")
    private Map<Integer, @Size(min = 1, max = 50) String> genres = new HashMap<>();

    @Column
    @Size(min = 1, max = 50)
    private String imdbId;

    @OneToMany(mappedBy = "movie",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            orphanRemoval = true)
    @MapKey(name = "localizedId.locale")
    private Map<String, LocalizedMovie> localizedMovies;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
