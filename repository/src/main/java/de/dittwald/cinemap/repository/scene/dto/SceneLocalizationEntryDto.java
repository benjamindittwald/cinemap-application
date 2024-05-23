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

package de.dittwald.cinemap.repository.scene.dto;

import de.dittwald.cinemap.repository.validation.Iso6391Constraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SceneLocalizationEntryDto(@NotNull @Iso6391Constraint String locale,
                                        @Size(min = 1, max = 50) String title,
                                        @Size(min = 1, max = 5000) String description

) {
}
