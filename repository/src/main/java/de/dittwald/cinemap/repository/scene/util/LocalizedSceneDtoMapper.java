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

package de.dittwald.cinemap.repository.scene.util;

import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.movie.entity.LocalizedId;
import de.dittwald.cinemap.repository.movie.util.LocalizedMovieDtoMapper;
import de.dittwald.cinemap.repository.scene.dto.SceneFlatDto;
import de.dittwald.cinemap.repository.scene.entity.LocalizedScene;
import de.dittwald.cinemap.repository.scene.entity.Scene;

public class LocalizedSceneDtoMapper {

    public static SceneFlatDto entityToDto(Scene entity, String locale) throws LocaleNotFoundException {
        if (entity.getLocalizedScenes().get(locale) == null) {
            throw new LocaleNotFoundException("Locale %s not found for scene %s".formatted(locale, entity));
        }

        return new SceneFlatDto(entity.getUuid(), entity.getLon(), entity.getLat(), locale,
                entity.getLocalizedScenes().get(locale).getDescription(),
                LocalizedMovieDtoMapper.entityToDto(entity.getMovie(), locale));
    }

    public static Scene dtoToEntity(SceneFlatDto dto) {
        Scene entity = new Scene();
        entity.setMovie(LocalizedMovieDtoMapper.dtoToEntity(dto.movie()));
        entity.setLat(dto.lat());
        entity.setLon(dto.lon());

        LocalizedScene localizedEntity = new LocalizedScene();
        LocalizedId localizedId = new LocalizedId(dto.locale());
        localizedEntity.setDescription(dto.description());
        localizedEntity.setLocalizedId(localizedId);
        localizedEntity.setScene(entity);

        entity.getLocalizedScenes().put(localizedId.getLocale(), localizedEntity);

        return entity;
    }
}
