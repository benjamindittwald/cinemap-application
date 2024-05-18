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

package de.dittwald.cinemap.repository.scene.service;

import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.movie.entity.LocalizedId;
import de.dittwald.cinemap.repository.movie.repository.MovieRepository;
import de.dittwald.cinemap.repository.scene.dto.SceneLocalizationDto;
import de.dittwald.cinemap.repository.scene.dto.SceneLocalizationEntryDto;
import de.dittwald.cinemap.repository.scene.entity.LocalizedScene;
import de.dittwald.cinemap.repository.scene.entity.Scene;
import de.dittwald.cinemap.repository.scene.repository.SceneLocalizedRepository;
import de.dittwald.cinemap.repository.scene.repository.SceneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SceneLocalizationService {

    private final SceneRepository sceneRepository;
    private final SceneLocalizedRepository sceneLocalizedRepository;

    public SceneLocalizationService(SceneRepository sceneRepository,
                                    SceneLocalizedRepository sceneLocalizedRepository) {
        this.sceneRepository = sceneRepository;
        this.sceneLocalizedRepository = sceneLocalizedRepository;
    }

    // Fixme: May not work due to duplicate potential UUIDs
    @Transactional
    public void update(SceneLocalizationDto sceneLocalizationDto, UUID sceneUuid, boolean override)
            throws NotFoundException {

        Optional<Scene> sceneOptional = this.sceneRepository.findByUuid(sceneUuid);
        Scene scene = null;
        if (sceneOptional.isPresent()) {
            scene = sceneOptional.get();
        } else {
            throw new NotFoundException("Scene not found");
        }

        if (override) {
            scene.getLocalizedScenes().clear();
        }

        for (SceneLocalizationEntryDto dto : sceneLocalizationDto.localizations()) {
            scene.getLocalizedScenes()
                    .put(dto.locale(), new LocalizedScene(new LocalizedId(dto.locale()), scene, dto.description()));
        }

        this.sceneRepository.save(scene);
    }

    @Transactional
    public SceneLocalizationDto getSceneLocalizationDto(UUID sceneUuid) throws NotFoundException {

        Optional<List<LocalizedScene>> localizedScenes = this.sceneLocalizedRepository.findAllBySceneUuid(sceneUuid);
        if (localizedScenes.isEmpty()) {
            throw new NotFoundException("No localized scenes found");
        }

        List<SceneLocalizationEntryDto> localizedEntries = new ArrayList<>();

        for (LocalizedScene localizedScene : localizedScenes.get()) {
            localizedEntries.add(new SceneLocalizationEntryDto(localizedScene.getLocalizedId().getLocale(),
                    localizedScene.getDescription()));
        }

        return new SceneLocalizationDto(sceneUuid, localizedEntries);
    }
}
