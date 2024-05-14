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

package de.dittwald.cinemap.repository.scene.entity;

import de.dittwald.cinemap.repository.movie.entity.Movie;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "scenes")
@Getter
@Setter
@NamedQuery(name = "Scene.findAllScenesOfMovie",
        query = "select s from Scene s where s.movie.uuid = :movieUuid")
public class Scene {

    public Scene() {
    }

    public Scene(UUID uuid, double lon, double lat, Movie movie,
                 Map<String, LocalizedScene> localizedScenes) {
        this.uuid = uuid;
        this.lon = lon;
        this.lat = lat;
        this.movie = movie;
        this.localizedScenes = localizedScenes;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotNull
    @Column(unique = true)
    private UUID uuid;

    @NotNull
    private double lon;

    @NotNull
    private double lat;

    @Version
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @OneToMany(mappedBy = "scene",
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
            orphanRemoval = true)
    @MapKey(name = "localizedId.locale")
    private Map<String, LocalizedScene> localizedScenes = new HashMap<>();

    @Override
    public String toString() {
        return "MovieScene{" + "uuid=" + id + ", uuid=" + uuid + ", lon=" + lon + ", lat=" + lat + ", version=" +
                version + ", movie=" + movie + ", localizedMoviesScenes=" + localizedScenes + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Scene that = (Scene) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
