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

import java.util.*;

public class Movie {

    public Movie() {
    }

    public Movie(UUID uuid, Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> title, Integer tmdbId,
                 Integer releaseYear, Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> tagline,
                 Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> overview,
                 Map<Integer, @Size(min = 1, max = 50) String> genres, Byte[] poster, String imdbId) {
        this.uuid = uuid;
        this.title = title;
        this.tmdbId = tmdbId;
        this.releaseYear = releaseYear;
        this.tagline = tagline;
        this.overview = overview;
        this.genres = genres;
        this.poster = poster;
        this.imdbId = imdbId;
    }

    private UUID uuid;

    @NotNull
    private Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> title = new HashMap<>();

    @Min(value = -2147483648) // From TMDB API Reference movie Details
    @Max(value = 2147483647) // https://developer.themoviedb.org/reference/movie-details
    private Integer tmdbId;

    @Min(value = 1700)
    private Integer releaseYear;

    private Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> tagline = new HashMap<>();

    private Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> overview = new HashMap<>();

    private Map<Integer, @Size(min = 1, max = 50) String> genres = new HashMap<>();

    private Byte[] poster;

    @Size(min = 1, max = 50)
    private String imdbId;

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

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Map<String, String> getTitle() {
        return title;
    }

    public void setTitle(Map<String, String> title) {
        this.title = title;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Integer tmdbId) {
        this.tmdbId = tmdbId;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Map<String, String> getTagline() {
        return tagline;
    }

    public void setTagline(Map<String, String> tagline) {
        this.tagline = tagline;
    }

    public Map<String, String> getOverview() {
        return overview;
    }

    public void setOverview(Map<String, String> overview) {
        this.overview = overview;
    }

    public Map<Integer, String> getGenres() {
        return genres;
    }

    public void setGenres(Map<Integer, String> genres) {
        this.genres = genres;
    }

    public Byte[] getPoster() {
        return poster;
    }

    public void setPoster(Byte[] poster) {
        this.poster = poster;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
}
