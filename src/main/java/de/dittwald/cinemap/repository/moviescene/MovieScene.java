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

package de.dittwald.cinemap.repository.moviescene;

import de.dittwald.cinemap.repository.movie.Movie;
import de.dittwald.cinemap.repository.validation.Iso639Constraint;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "movie_scenes")
public class MovieScene {

    protected MovieScene() {
    }

    public MovieScene(Long lon, Long lat, Map<@Iso639Constraint String, String> description, Movie movie) {
        this.lon = lon;
        this.lat = lat;
        this.description = description;
        this.movie = movie;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    private Long version;

    @NotNull
    private Long lon;

    @NotNull
    private Long lat;

    @ElementCollection
    @CollectionTable(
            name = "description_locale_mapping",
            joinColumns = {@JoinColumn(
                    name = "locale_id", referencedColumnName = "id")}
    )
    @MapKeyColumn(name = "description_locale")
    @Column(name = "description")
    @NotNull
    private Map<@Iso639Constraint String, String> description = new HashMap<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Movie movie;

    @Override
    public String toString() {
        return "MovieScene{" +
                "id=" + id +
                ", version=" + version +
                ", lon=" + lon +
                ", lat=" + lat +
                ", description=" + description +
                ", movie=" + movie +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieScene that = (MovieScene) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public Long getLon() {
        return lon;
    }

    public void setLon(Long lon) {
        this.lon = lon;
    }

    public Long getLat() {
        return lat;
    }

    public void setLat(Long lat) {
        this.lat = lat;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
