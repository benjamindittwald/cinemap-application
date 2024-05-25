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

import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.exceptions.NotFoundException;
import de.dittwald.cinemap.repository.exceptions.UuidInUseException;
import de.dittwald.cinemap.repository.movie.entity.LocalizedId;
import de.dittwald.cinemap.repository.movie.entity.Movie;
import de.dittwald.cinemap.repository.movie.repository.MovieRepository;
import de.dittwald.cinemap.repository.scene.dto.SceneCreationDto;
import de.dittwald.cinemap.repository.scene.dto.SceneFlatDto;
import de.dittwald.cinemap.repository.scene.entity.LocalizedScene;
import de.dittwald.cinemap.repository.scene.entity.Scene;
import de.dittwald.cinemap.repository.scene.repository.SceneRepository;
import de.dittwald.cinemap.repository.scene.util.LocalizedSceneDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import de.dittwald.cinemap.repository.util.LocaleFallbackHandler;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class SceneService {

    private final SceneRepository sceneRepository;
    private final MovieRepository movieRepository;

    public SceneService(SceneRepository sceneRepository, MovieRepository movieRepository) {
        this.sceneRepository = sceneRepository;
        this.movieRepository = movieRepository;
    }


    @Transactional
    public SceneFlatDto findByUuid(UUID uuid, String locale) throws NotFoundException, LocaleNotFoundException {
        Scene scene = this.sceneRepository.findByUuid(uuid).orElseThrow(() -> new NotFoundException("Scene not found"));
        return LocalizedSceneDtoMapper.entityToDto(scene, LocaleFallbackHandler.getSceneLocale(scene, locale));
    }

    @Transactional
    public void update(SceneCreationDto sceneCreationDto, UUID movieUuid, UUID sceneUuid) throws NotFoundException {

        Movie movie =
                this.movieRepository.findByUuid(movieUuid).orElseThrow(() -> new NotFoundException("Movie not found"));

        Scene scene =
                this.sceneRepository.findByUuid(sceneUuid).orElseThrow(() -> new NotFoundException("Scene not found"));
        scene.setLat(sceneCreationDto.lat());
        scene.setLon(sceneCreationDto.lon());


        if (scene.getLocalizedScenes().containsKey(sceneCreationDto.locale())) {
            LocalizedScene updatedlocalizedScene = scene.getLocalizedScenes().get(sceneCreationDto.locale());
            updatedlocalizedScene.setScene(scene);
            updatedlocalizedScene.setDescription(sceneCreationDto.description());
            scene.getLocalizedScenes().replace(sceneCreationDto.locale(), updatedlocalizedScene);
        } else {
            scene.getLocalizedScenes()
                    .put(sceneCreationDto.locale(),
                            new LocalizedScene(new LocalizedId(sceneCreationDto.locale()), scene, sceneCreationDto.title(),
                                    sceneCreationDto.description()));
        }

        this.sceneRepository.save(scene);
    }

    public void save(SceneCreationDto sceneCreationDto, UUID movieUuid) throws NotFoundException, UuidInUseException {

        Movie movie =
                this.movieRepository.findByUuid(movieUuid).orElseThrow(() -> new NotFoundException("Movie not found"));

        if (!this.sceneRepository.existsByUuid(sceneCreationDto.uuid())) {
            Scene scene = LocalizedSceneDtoMapper.dtoToEntity(sceneCreationDto);
            scene.setMovie(movie);
            this.sceneRepository.save(scene);
        } else {
            throw new UuidInUseException("UUID already in use");
        }
    }

    @Transactional
    public List<SceneFlatDto> findAll(String locale) throws LocaleNotFoundException {
        List<SceneFlatDto> movieSceneDtos = new ArrayList<>();
        for (Scene scene : this.sceneRepository.findAll()) {
            movieSceneDtos.add(LocalizedSceneDtoMapper.entityToDto(scene, locale));
        }
        return movieSceneDtos;
    }

    @Transactional
    public void deleteByUuid(UUID uuid) throws NotFoundException {
        if (this.sceneRepository.existsByUuid(uuid)) {
            this.sceneRepository.deleteByUuid(uuid);
        } else {
            throw new NotFoundException("Scene not found");
        }
    }

    public void deleteAll() {
        log.warn("Deleting all scenes");
        this.sceneRepository.deleteAll();
    }

    @Transactional
    public List<SceneFlatDto> findAllScenesOfMovie(UUID movieUuid, String locale)
            throws NotFoundException, LocaleNotFoundException {
        List<SceneFlatDto> sceneListDto = new ArrayList<>();

        if (!this.movieRepository.existsByUuid(movieUuid)) {
            throw new NotFoundException("Movie not found");
        }

        Optional<List<Scene>> scenesOptional = this.sceneRepository.findAllScenesOfMovieUuid(movieUuid);
        if (scenesOptional.isPresent()) {
            for (Scene scene : scenesOptional.get()) {
                sceneListDto.add(LocalizedSceneDtoMapper.entityToDto(scene, locale));
            }
        }

        return sceneListDto;
    }
}
