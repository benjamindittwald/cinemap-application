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

import de.dittwald.cinemap.repository.movie.entity.LocalizedId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NamedQuery(name = "LocalizedScene.findAllBySceneUuid",
        query = "select ls from LocalizedScene ls where ls.scene.uuid = :sceneUuid")
public class LocalizedScene implements Serializable {

    public LocalizedScene() {
        this.localizedId = new LocalizedId();
    }

    public LocalizedScene(LocalizedId localizedId, Scene scene, String title, String description) {
        this.localizedId = localizedId;
        this.scene = scene;
        this.title = title;
        this.description = description;
    }

    @EmbeddedId
    private LocalizedId localizedId;

    @ManyToOne()
    @MapsId("id")
    @JoinColumn(name = "id")
    private Scene scene;

    @Size(min = 1, max = 50)
    private String title;

    @Lob
    @Size(min = 1, max = 5000)
    private String description;
}
