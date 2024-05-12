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

package de.dittwald.cinemap.repository.scene;

import de.dittwald.cinemap.repository.movie.LocalizedId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.net.URL;

@Entity
public class LocalizedScene {

    public LocalizedScene() {
        this.localizedId = new LocalizedId();
    }

    public LocalizedScene(LocalizedId localizedId, Scene scene, String description) {
        this.localizedId = localizedId;
        this.scene = scene;
        this.description = description;
    }

    @EmbeddedId
    private LocalizedId localizedId;

    @ManyToOne()
    @MapsId("id")
    @JoinColumn(name = "id")
    private Scene scene;

    @Lob
    @Size(min = 1, max = 5000)
    private String description;

    public LocalizedId getLocalizedId() {
        return localizedId;
    }

    public void setLocalizedId(LocalizedId localizedId) {
        this.localizedId = localizedId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
