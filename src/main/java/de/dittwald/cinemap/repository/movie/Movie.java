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

import de.dittwald.cinemap.repository.moviescene.MovieScene;
import de.dittwald.cinemap.repository.validation.Iso639Constraint;
import de.dittwald.cinemap.repository.validation.UrlConstraint;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "movies")
public class Movie {

    protected Movie() {
    }

    // Todo: Remove after migration
    public Movie(Long id, Map<String, String> title, String imdbWebsiteUrl) {
        this.id = id;
        this.title = title;
        this.imdbWebsiteUrl = imdbWebsiteUrl;
    }

    // Todo: Remove after migration
    public Movie(Map<String, String> title, String imdbWebsiteUrl) {
        this.title = title;
        this.imdbWebsiteUrl = imdbWebsiteUrl;
    }

    public Movie(Long id, Long version, Map<@Iso639Constraint String, String> title, String imdbWebsiteUrl,
                 List<MovieScene> movieScene) {
        this.id = id;
        this.version = version;
        this.title = title;
        this.imdbWebsiteUrl = imdbWebsiteUrl;
        this.movieScene = movieScene;
    }

    public Movie(Map<@Iso639Constraint String, String> title, String imdbWebsiteUrl, List<MovieScene> movieScene) {
        this.title = title;
        this.imdbWebsiteUrl = imdbWebsiteUrl;
        this.movieScene = movieScene;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Version
    private Long version;

    @ElementCollection
    @CollectionTable(
            name = "title_locale_mapping",
            joinColumns = {@JoinColumn(
                    name = "locale_id", referencedColumnName = "id")}
    )
    @MapKeyColumn(name = "title_locale")
    @Column(name = "title")
    @NotNull
    private Map<@Iso639Constraint String, String> title = new HashMap<>();

    @UrlConstraint
    @NotBlank
    private String imdbWebsiteUrl;

    @OneToMany(
            mappedBy = "movie",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MovieScene> movieScene = new ArrayList<>();

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", version=" + version +
                ", title=" + title +
                ", imdbWebsiteUrl='" + imdbWebsiteUrl + '\'' +
                ", movieScene=" + movieScene +
                '}';
    }

    public void addMovieScene(MovieScene movieScene) {
        this.movieScene.add(movieScene);
        movieScene.setMovie(this);
    }

    public void removeMovieScene(MovieScene movieScene) {
        this.movieScene.remove(movieScene);
        movieScene.setMovie(null);
    }

    public List<MovieScene> getMovieScene() {
        return movieScene;
    }

    public void setMovieScene(List<MovieScene> movieScene) {
        this.movieScene = movieScene;
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

    public String getImdbWebsiteUrl() {
        return imdbWebsiteUrl;
    }

    public void setImdbWebsiteUrl(String imdbWebsiteUrl) {
        this.imdbWebsiteUrl = imdbWebsiteUrl;
    }
}
