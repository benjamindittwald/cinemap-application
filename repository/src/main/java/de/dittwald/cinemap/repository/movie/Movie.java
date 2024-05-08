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

import java.util.*;

@Entity
@Table(name = "movies")
public class Movie {

    public Movie() {
    }

    public Movie(UUID uuid, Map<String, String> title, int tmdbId, int releaseYear, Map<String, String> tagline,
                 Map<String, String> overview, Map<Integer, String> genres, Byte[] poster, String imdbId) {
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
    private Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> title = new HashMap<>();

    @Min(value = -2147483648) // From TMDB API Reference movie Details
    @Max(value = 2147483647) // https://developer.themoviedb.org/reference/movie-details
    private Integer tmdbId;

    @Column
    @Min(value = 1700)
    private Integer releaseYear;

    @ElementCollection
    @CollectionTable(name = "tagline_locale_mapping",
            joinColumns = {@JoinColumn(name = "locale_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "tagline_locale")
    @Column(name = "tagline")
    private Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> tagline = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "overview_locale_mapping",
            joinColumns = {@JoinColumn(name = "locale_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "overview_locale")
    @Column(name = "overview")
    private Map<@Iso639Constraint String, @Size(min = 1, max = 500) String> overview = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "genres_number_mapping",
            joinColumns = {@JoinColumn(name = "number_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "genres_id")
    @Column(name = "genres")
    private Map<Integer, @Size(min = 1, max = 50) String> genres = new HashMap<>();

    @Lob
    private Byte[] poster;

    @Column
    @Size(min = 1, max = 50)
    private String imdbId;

    @Override
    public String toString() {
        return "Movie{" + "id=" + id + ", uuid=" + uuid + ", version=" + version + ", title=" + title + ", tmdbId=" +
                tmdbId + ", releaseYear=" + releaseYear + ", tagline=" + tagline + ", overview=" + overview +
                ", genres=" + genres + ", poster=" + Arrays.toString(poster) + ", imdbId='" + imdbId + '\'' + '}';
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

    public Long getId() {
        return id;
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

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
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
