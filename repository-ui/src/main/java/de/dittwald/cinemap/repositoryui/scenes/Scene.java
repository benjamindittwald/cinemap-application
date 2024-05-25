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

package de.dittwald.cinemap.repositoryui.scenes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.dittwald.cinemap.repositoryui.movies.MovieFlat;
import de.dittwald.cinemap.repositoryui.validation.Iso6391Constraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Scene {

    @NotNull
    private UUID uuid;

    @NotNull
    private Double lon;

    @NotNull
    private Double lat;

    @NotNull
    @Iso6391Constraint
    private String locale;

    @Size(min = 1, max = 50)
    private String title;

    @Size(min = 1, max = 5000)
    private String description;

    @JsonIgnore
    private MovieFlat movie;
}
