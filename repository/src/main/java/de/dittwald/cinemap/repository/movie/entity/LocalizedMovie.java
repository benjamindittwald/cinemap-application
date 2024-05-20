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

package de.dittwald.cinemap.repository.movie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.net.URL;

@Setter
@Getter
@Entity
@NamedQuery(name = "LocalizedMovie.findAllByMovieUuid",
        query = "select lm from LocalizedMovie lm where lm.movie.uuid = :movieUuid")
public class LocalizedMovie implements Serializable {

    public LocalizedMovie() {
        this.localizedId = new LocalizedId();
    }

    public LocalizedMovie(LocalizedId localizedId, Movie movie, String title, String overview, String tagline,
                          URL posterUrl) {
        this.localizedId = localizedId;
        this.movie = movie;
        this.title = title;
        this.overview = overview;
        this.tagline = tagline;
        this.posterUrl = posterUrl;
    }

    @EmbeddedId
    private LocalizedId localizedId;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "id")
    private Movie movie;

    @Size(max = 255)
    private String title;

    @Lob
    @Size(max = 5000)
    private String overview;

    @Size(max = 255)
    private String tagline;

    private URL posterUrl;
}
