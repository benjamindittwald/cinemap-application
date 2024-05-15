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
import de.dittwald.cinemap.repository.movie.DummyMovies;
import de.dittwald.cinemap.repository.movie.entity.Movie;
import de.dittwald.cinemap.repository.scene.entity.Scene;
import de.dittwald.cinemap.repository.util.LocaleFallbackHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LocaleFallbackHandlerTest {

    private DummyMovies dummyMovies;

    @BeforeEach
    void setUp() throws MalformedURLException, URISyntaxException {
        this.dummyMovies = new DummyMovies();
    }

    @Test
    void shouldGetExpectedMovieLocale() throws LocaleNotFoundException {
        assertEquals("de", LocaleFallbackHandler.getMovieLocale(this.dummyMovies.getWolf(), "de"));
    }

    @Test
    void shouldGetDefaultMovieLocale() throws LocaleNotFoundException {
        assertEquals("en", LocaleFallbackHandler.getMovieLocale(this.dummyMovies.getWolf(), "nl"));
    }

    @Test
    void shouldGetFirstMovieLocale() throws LocaleNotFoundException {
        Movie movie = dummyMovies.getWolf();
        movie.getLocalizedMovies().remove("en");
        assertEquals("de", LocaleFallbackHandler.getMovieLocale(movie, "nl"));
    }

    @Test
    void shouldFailGetLocaleDueToEmptyMovieLocales() {
        Movie movie = dummyMovies.getWolf();
        movie.getLocalizedMovies().clear();

        Exception exception = assertThrows(LocaleNotFoundException.class,
                () -> LocaleFallbackHandler.getMovieLocale(this.dummyMovies.getWolf(), "de"));
        assertThat(exception.getMessage()).isEqualTo("No locales found");
    }

    @Test
    void shouldGetExpectedSceneLocale() throws LocaleNotFoundException {
        assertEquals("de", LocaleFallbackHandler.getSceneLocale(this.dummyMovies.getWolfSceneOne(), "de"));
    }

    @Test
    void shouldGetDefaultSceneLocale() throws LocaleNotFoundException {
        assertEquals("en", LocaleFallbackHandler.getSceneLocale(this.dummyMovies.getWolfSceneOne(), "nl"));
    }

    @Test
    void shouldGetFirstSceneLocale() throws LocaleNotFoundException {
        Scene scene = dummyMovies.getWolfSceneOne();
        scene.getLocalizedScenes().remove("en");
        assertEquals("de", LocaleFallbackHandler.getSceneLocale(scene, "nl"));
    }

    @Test
    void shouldFailGetLocaleDueToEmptySceneLocales() {
        Scene scene = dummyMovies.getWolfSceneOne();
        scene.getLocalizedScenes().clear();

        Exception exception = assertThrows(LocaleNotFoundException.class,
                () -> LocaleFallbackHandler.getSceneLocale(this.dummyMovies.getWolfSceneOne(), "de"));
        assertThat(exception.getMessage()).isEqualTo("No locales found");
    }
}