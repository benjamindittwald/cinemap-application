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

import de.dittwald.cinemap.repository.movie.DummyMovies;
import de.dittwald.cinemap.repository.movie.entity.LocalizedId;
import de.dittwald.cinemap.repository.scene.entity.LocalizedScene;
import de.dittwald.cinemap.repository.scene.entity.Scene;
import lombok.Getter;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.UUID;

@Getter
public class DummyScenes {

    public DummyScenes() throws MalformedURLException, URISyntaxException {
        DummyMovies dummyMovies = new DummyMovies();

        // Wolf scene one
        this.wolfSceneOne = new Scene();
        this.wolfSceneOne.setUuid(UUID.randomUUID());
        this.wolfSceneOne.setLat(52.51263);
        this.wolfSceneOne.setLon(13.35943);
        this.wolfSceneOne.setMovie(dummyMovies.getWolf());

        LocalizedScene wolfLsOneEn = new LocalizedScene();
        wolfLsOneEn.setDescription("Dances with Wolves - Scene One Description");
        wolfLsOneEn.setLocalizedId(new LocalizedId("en"));
        wolfLsOneEn.setScene(wolfSceneOne);
        this.wolfSceneOne.getLocalizedScenes().put("en", wolfLsOneEn);

        LocalizedScene wolfLsOneDe = new LocalizedScene();
        wolfLsOneDe.setDescription("Der mit dem Wolf tanzt - Scene One Description");
        wolfLsOneDe.setLocalizedId(new LocalizedId("de"));
        wolfLsOneDe.setScene(wolfSceneOne);
        this.wolfSceneOne.getLocalizedScenes().put("de", wolfLsOneDe);

        // Wolf scene two
        this.wolfSceneTwo = new Scene();
        this.wolfSceneTwo.setUuid(UUID.randomUUID());
        this.wolfSceneOne.setLat(52.51263);
        this.wolfSceneOne.setLon(13.35943);
        this.wolfSceneOne.setMovie(dummyMovies.getWolf());

        LocalizedScene wolfLsTwoEn = new LocalizedScene();
        wolfLsTwoEn.setDescription("Dances with Wolves - Scene Two Description");
        wolfLsTwoEn.setLocalizedId(new LocalizedId("en"));
        wolfLsTwoEn.setScene(wolfSceneOne);
        this.wolfSceneOne.getLocalizedScenes().put("en", wolfLsTwoEn);

        LocalizedScene wolfLsTwoDe = new LocalizedScene();
        wolfLsTwoDe.setDescription("Der mit dem Wolf tanzt - Scene Two Description");
        wolfLsTwoDe.setLocalizedId(new LocalizedId("de"));
        wolfLsTwoDe.setScene(wolfSceneOne);
        this.wolfSceneOne.getLocalizedScenes().put("de", wolfLsTwoDe);

    }

    private Scene wolfSceneOne;
    private Scene wolfSceneTwo;
}
