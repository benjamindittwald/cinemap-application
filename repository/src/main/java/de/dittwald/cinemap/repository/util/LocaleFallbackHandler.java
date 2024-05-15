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

package de.dittwald.cinemap.repository.util;

import de.dittwald.cinemap.repository.exceptions.LocaleNotFoundException;
import de.dittwald.cinemap.repository.movie.entity.Movie;
import de.dittwald.cinemap.repository.scene.entity.Scene;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocaleFallbackHandler {

    public static String getMovieLocale(Movie movie, String locale) throws LocaleNotFoundException {
        if (movie.getLocalizedMovies().isEmpty()) {
            throw new LocaleNotFoundException("No locales found");
        } else if (movie.getLocalizedMovies().containsKey(locale)) {
            return locale;
        } else if (movie.getLocalizedMovies().containsKey("en")) {
            logChangedLocale(locale, "en");
            return "en";
        } else {
            String fallbackLocale = movie.getLocalizedMovies().keySet().stream().findFirst().get();
            logChangedLocale(locale, fallbackLocale);
            return fallbackLocale;
        }
    }

    public static String getSceneLocale(Scene scene, String locale) throws LocaleNotFoundException {
        if (scene.getLocalizedScenes().isEmpty()) {
            throw new LocaleNotFoundException("No locales found");
        } else if (scene.getLocalizedScenes().containsKey(locale)) {
            return locale;
        } else if (scene.getLocalizedScenes().containsKey("en")) {
            return "en";
        } else {
            return scene.getLocalizedScenes().keySet().stream().findFirst().get();
        }
    }

    private static void logChangedLocale(String expectedLocale, String actualLocale) {
        log.warn("Locale {} not found. Using {} instead.", expectedLocale, actualLocale);
    }


}
