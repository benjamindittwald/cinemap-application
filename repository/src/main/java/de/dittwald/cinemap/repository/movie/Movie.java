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

import de.dittwald.cinemap.repository.validation.Iso639Constraint;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "movies")
public class Movie {

    public Movie() {
    }

    public Movie(UUID uuid, Map<String, String> title, int tmdbId) {
        this.uuid = uuid;
        this.title = title;
        this.tmdbId = tmdbId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private UUID uuid;

    @Version
    private Long version;

    @ElementCollection
    @CollectionTable(name = "title_locale_mapping",
            joinColumns = {@JoinColumn(name = "locale_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "title_locale")
    @Column(name = "title")
    @NotNull
    private Map<@Iso639Constraint String, String> title = new HashMap<>();

    @Min(value = -2147483648) // From TMDB API Reference movie Details
    @Max(value = 2147483647) // https://developer.themoviedb.org/reference/movie-details
    private int tmdbId;

    @Override
    public String toString() {
        return "Movie{" + "id=" + id + ", version=" + version + ", title=" + title + ", tmdbId='" + tmdbId + '\'' + '}';
    }

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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Long getId() {
        return id;
    }

    public Long getVersion() {
        return version;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }
}
