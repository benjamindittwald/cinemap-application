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

import de.dittwald.cinemap.repository.movie.dto.MovieFlatDto;
import de.dittwald.cinemap.repository.movie.dto.MovieLocalizationEntryDto;
import de.dittwald.cinemap.repository.movie.dto.MovieLocalizationDto;
import de.dittwald.cinemap.repository.movie.entity.LocalizedId;
import de.dittwald.cinemap.repository.movie.entity.LocalizedMovie;
import de.dittwald.cinemap.repository.movie.entity.Movie;
import de.dittwald.cinemap.repository.scene.dto.SceneCreationDto;
import de.dittwald.cinemap.repository.scene.dto.SceneFlatDto;
import de.dittwald.cinemap.repository.scene.dto.SceneLocalizationDto;
import de.dittwald.cinemap.repository.scene.dto.SceneLocalizationEntryDto;
import de.dittwald.cinemap.repository.scene.entity.LocalizedScene;
import de.dittwald.cinemap.repository.scene.entity.Scene;
import lombok.Getter;
import lombok.Setter;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class DummyData {

    public DummyData() throws URISyntaxException, MalformedURLException {

        // Wolf
        this.wolf = new Movie();
        this.wolf.setUuid(UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"));
        this.wolf.setGenres(Map.of(85, "thriller", 80, "western"));
        this.wolf.setReleaseYear(1970);
        this.wolf.setTmdbId(1051896);
        this.wolf.setImdbId("imdbId");

        LocalizedMovie wolfLmEn = new LocalizedMovie();
        wolfLmEn.setLocalizedId(new LocalizedId("en"));
        wolfLmEn.setOverview("Dances with Wolves - Overview");
        wolfLmEn.setTagline("Dances with Wolves - Tagline");
        wolfLmEn.setTitle("Dances with Wolves - Title");
        wolfLmEn.setPosterUrl(new URI("https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg").toURL());
        wolfLmEn.setMovie(this.wolf);
        this.wolf.getLocalizedMovies().put("en", wolfLmEn);

        LocalizedMovie wolfLmDe = new LocalizedMovie();
        wolfLmDe.setLocalizedId(new LocalizedId("de"));
        wolfLmDe.setOverview("Der mit dem Wolf tanzt - Overview");
        wolfLmDe.setTagline("Der mit dem Wolf tanzt - Tagline");
        wolfLmDe.setTitle("Der mit dem Wolf tanzt - Title");
        wolfLmDe.setPosterUrl(new URI("https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg").toURL());
        wolfLmDe.setMovie(this.wolf);
        this.wolf.getLocalizedMovies().put("de", wolfLmDe);

        // Wolf scene one
        this.wolfSceneOne = new Scene();
        this.wolfSceneOne.setUuid(UUID.fromString("a9f669e3-3f7e-4cb3-899d-2e03e28ab2e8"));
        this.wolfSceneOne.setLat(52.51263);
        this.wolfSceneOne.setLon(13.35943);
        this.wolfSceneOne.setMovie(this.getWolf());

        this.wolfLsOneEn = new LocalizedScene();
        this.wolfLsOneEn.setTitle("Dances with Wolves - Scene One Title");
        this.wolfLsOneEn.setDescription("Dances with Wolves - Scene One Description");
        this.wolfLsOneEn.setLocalizedId(new LocalizedId("en"));
        this.wolfLsOneEn.setScene(wolfSceneOne);
        this.wolfSceneOne.getLocalizedScenes().put("en", wolfLsOneEn);

        this.wolfLsOneDe = new LocalizedScene();
        this.wolfLsOneDe.setTitle("Dances with Wolves - Scene One Title");
        this.wolfLsOneDe.setDescription("Der mit dem Wolf tanzt - Scene One Description");
        this.wolfLsOneDe.setLocalizedId(new LocalizedId("de"));
        this.wolfLsOneDe.setScene(wolfSceneOne);
        this.wolfSceneOne.getLocalizedScenes().put("de", wolfLsOneDe);

        // Wolf scene two
        this.wolfSceneTwo = new Scene();
        this.wolfSceneTwo.setUuid(UUID.randomUUID());
        this.wolfSceneTwo.setLat(52.51263);
        this.wolfSceneTwo.setLon(13.35943);
        this.wolfSceneTwo.setMovie(this.getWolf());

        this.wolfLsTwoEn = new LocalizedScene();
        this.wolfLsTwoEn.setTitle("Dances with Wolves - Scene Two Title");
        this.wolfLsTwoEn.setDescription("Dances with Wolves - Scene Two Description");
        this.wolfLsTwoEn.setLocalizedId(new LocalizedId("en"));
        this.wolfLsTwoEn.setScene(wolfSceneTwo);
        this.wolfSceneTwo.getLocalizedScenes().put("en", wolfLsTwoEn);

        this.wolfLsTwoDe = new LocalizedScene();
        this.wolfLsTwoDe.setTitle("Dances with Wolves - Scene Two Title");
        this.wolfLsTwoDe.setDescription("Der mit dem Wolf tanzt - Scene Two Description");
        this.wolfLsTwoDe.setLocalizedId(new LocalizedId("de"));
        this.wolfLsTwoDe.setScene(wolfSceneTwo);
        this.wolfSceneTwo.getLocalizedScenes().put("de", wolfLsTwoDe);

        // Nobody
        this.nobody = new Movie();
        this.nobody.setUuid(UUID.randomUUID());
        this.nobody.setGenres(Map.of(80, "western", 85, "thriller"));
        this.nobody.setReleaseYear(1970);
        this.nobody.setTmdbId(505);
        this.nobody.setImdbId("imdbId");

        LocalizedMovie nobodyLmEn = new LocalizedMovie();
        nobodyLmEn.setLocalizedId(new LocalizedId("en"));
        nobodyLmEn.setOverview("My Name is Nobody - Overview");
        nobodyLmEn.setTagline("My Name is Nobody - Tagline");
        nobodyLmEn.setTitle("My Name is Nobody - Title");
        nobodyLmEn.setPosterUrl(new URI("https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg").toURL());
        nobodyLmEn.setMovie(this.nobody);
        this.nobody.getLocalizedMovies().put("en", nobodyLmEn);

        LocalizedMovie nobodyLmDe = new LocalizedMovie();
        nobodyLmDe.setLocalizedId(new LocalizedId("de"));
        nobodyLmDe.setOverview("Mein Name ist Nobody - Overview");
        nobodyLmDe.setTagline("Mein Name ist Nobody - Tagline");
        nobodyLmDe.setTitle("Mein Name ist Nobody - Title");
        nobodyLmDe.setPosterUrl(new URI("https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg").toURL());
        nobodyLmDe.setMovie(this.nobody);
        this.nobody.getLocalizedMovies().put("de", nobodyLmDe);

        // Localized wolfDto
        this.wolfFlatEnDto = new MovieFlatDto(wolf.getUuid(), wolf.getTmdbId(), wolf.getReleaseYear(), wolf.getGenres(),
                wolf.getImdbId(), "en", wolf.getLocalizedMovies().get("en").getTitle(),
                wolf.getLocalizedMovies().get("en").getOverview(), wolf.getLocalizedMovies().get("en").getTagline(),
                wolf.getLocalizedMovies().get("en").getPosterUrl());

        this.wolfFlatDeDto = new MovieFlatDto(wolf.getUuid(), wolf.getTmdbId(), wolf.getReleaseYear(), wolf.getGenres(),
                wolf.getImdbId(), "de", wolf.getLocalizedMovies().get("de").getTitle(),
                wolf.getLocalizedMovies().get("de").getOverview(), wolf.getLocalizedMovies().get("de").getTagline(),
                wolf.getLocalizedMovies().get("de").getPosterUrl());

        // Localized wolf
        this.wolfLocalizedMovieEn = this.wolf.getLocalizedMovies().get("en");
        this.wolfLocalizedMovieDe = this.wolf.getLocalizedMovies().get("de");


        // Localized nobodyDto
        this.nobodyFlatEnDto =
                new MovieFlatDto(nobody.getUuid(), nobody.getTmdbId(), nobody.getReleaseYear(), nobody.getGenres(),
                        nobody.getImdbId(), "en", nobody.getLocalizedMovies().get("en").getTitle(),
                        nobody.getLocalizedMovies().get("en").getOverview(),
                        nobody.getLocalizedMovies().get("en").getTagline(),
                        nobody.getLocalizedMovies().get("en").getPosterUrl());

        this.nobodyFlatDeDto =
                new MovieFlatDto(nobody.getUuid(), nobody.getTmdbId(), nobody.getReleaseYear(), nobody.getGenres(),
                        nobody.getImdbId(), "de", nobody.getLocalizedMovies().get("de").getTitle(),
                        nobody.getLocalizedMovies().get("de").getOverview(),
                        nobody.getLocalizedMovies().get("de").getTagline(),
                        nobody.getLocalizedMovies().get("de").getPosterUrl());

        // Localized nobody
        this.nobodyLocalizedMovieEn = this.nobody.getLocalizedMovies().get("en");
        this.nobodyLocalizedMovieEn = this.nobody.getLocalizedMovies().get("de");

        // Wolf MovieLocalizationsDto
        this.wolfLocalizationDto = new MovieLocalizationDto(this.wolf.getUuid(),
                List.of(new MovieLocalizationEntryDto(this.wolfFlatEnDto.locale(), this.wolfFlatEnDto.title(),
                                this.wolfFlatEnDto.overview(), this.wolfFlatEnDto.tagline(),
                                this.wolfFlatEnDto.posterUrl()),
                        new MovieLocalizationEntryDto(this.wolfFlatDeDto.locale(), this.wolfFlatDeDto.title(),
                                this.wolfFlatDeDto.overview(), this.wolfFlatDeDto.tagline(),
                                this.wolfFlatDeDto.posterUrl())));


        // Wold scene flat DTO
        this.wolfSceneOneFlatEnDto = new SceneFlatDto(this.getWolfSceneOne().getUuid(), this.wolfSceneOne.getLon(),
                this.wolfSceneOne.getLat(), "en", this.getWolfLsOneEn().getTitle(),
                this.wolfSceneOne.getLocalizedScenes().get("en").getDescription(), this.wolfFlatEnDto);

        // Wolf Scene Localization DTO
        this.wolfSceneOneLocalizationDto = new SceneLocalizationDto(this.wolfSceneOne.getUuid(),
                List.of(new SceneLocalizationEntryDto(wolfLsOneEn.getLocalizedId().getLocale(),
                                this.getWolfLsOneEn().getTitle(), wolfLsOneEn.getDescription()),
                        new SceneLocalizationEntryDto(wolfLsOneDe.getLocalizedId().getLocale(),
                                this.getWolfLsOneEn().getTitle(), wolfLsOneDe.getDescription())));

        // Wold scene creation DTO
        this.wolfSceneOneCreationEnDto =
                new SceneCreationDto(this.getWolfSceneOne().getUuid(), this.wolfSceneOne.getLon(),
                        this.wolfSceneOne.getLat(), "en", this.getWolfLsOneEn().getTitle(),
                        this.wolfSceneOne.getLocalizedScenes().get("en").getDescription());
    }

    private Movie wolf;
    private MovieFlatDto wolfFlatEnDto;
    private MovieFlatDto wolfFlatDeDto;
    private LocalizedMovie wolfLocalizedMovieEn;
    private LocalizedMovie wolfLocalizedMovieDe;
    private MovieLocalizationDto wolfLocalizationDto;

    private Scene wolfSceneOne;
    private Scene wolfSceneTwo;
    private SceneLocalizationDto wolfSceneOneLocalizationDto;
    private LocalizedScene wolfLsOneEn;
    private LocalizedScene wolfLsOneDe;
    private LocalizedScene wolfLsTwoEn;
    private LocalizedScene wolfLsTwoDe;
    private SceneFlatDto wolfSceneOneFlatEnDto;
    private SceneCreationDto wolfSceneOneCreationEnDto;

    private Movie nobody;
    private MovieFlatDto nobodyFlatEnDto;
    private MovieFlatDto nobodyFlatDeDto;
    private LocalizedMovie nobodyLocalizedMovieEn;
    private LocalizedMovie nobodyLocalizedMovieDe;

    public String getValidWolfEnDtoJson = """
            {
                "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                "title":"Dances with Wolves - Title",
                "tmdbId":1051896,
                "releaseYear":1970,
                "tagline":"Dances with Wolves - Tagline",
                "overview":"Dances with Wolves - Overview",
                "genres":
                    {
                        "80":"western",
                        "85":"thriller"
                     },
                 "posterUrl":"https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg",
                 "imdbId":"imdbId",
                 "locale":"en"
            }
            """;

    public String getInvalidLocaleWolfEnDtoJson = """
            {
                "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                "title":"Dances with Wolves - Title",
                "tmdbId":1051896,
                "releaseYear":1970,
                "tagline":"Dances with Wolves - Tagline",
                "overview":"Dances with Wolves - Overview",
                "genres":
                    {
                        "80":"western",
                        "85":"thriller"
                     },
                 "posterUrl":"https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg",
                 "imdbId":"imdbId",
                 "locale":"eng"
            }
            """;

    public String getInvalidPosterUrlWolfEnDtoJson = """
            {
                "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                "title":"Dances with Wolves - Title",
                "tmdbId":1051896,
                "releaseYear":1970,
                "tagline":"Dances with Wolves - Tagline",
                "overview":"Dances with Wolves - Overview",
                "genres":
                    {
                        "80":"western",
                        "85":"thriller"
                     },
                 "posterUrl":"https/image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg",
                 "imdbId":"imdbId",
                 "locale":"en"
            }
            """;

    public String getInvalidUuiWolfEnDtoJson = """
            {
                "uuid":"aa7acd67-4052-421d-a6-90440c683e6d",
                "title":"Dances with Wolves - Title",
                "tmdbId":1051896,
                "releaseYear":1970,
                "tagline":"Dances with Wolves - Tagline",
                "overview":"Dances with Wolves - Overview",
                "genres":
                    {
                        "80":"western",
                        "85":"thriller"
                     },
                 "posterUrl":"https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg",
                 "imdbId":"imdbId",
                 "locale":"en"
            }
            """;

    public String getInValidMissingUuidWolfEnDtoJson = """
            {
                "title":"Dances with Wolves - Title",
                "tmdbId":1051896,
                "releaseYear":1970,
                "tagline":"Dances with Wolves - Tagline",
                "overview":"Dances with Wolves - Overview",
                "genres":
                    {
                        "80":"western",
                        "85":"thriller"
                     },
                 "posterUrl":"https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg",
                 "imdbId":"imdbId",
                 "locale":"en"
            }
            """;

    public String getValidWolfMovieLocalizationsJson = """
            {
                "movieUuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                "localizations":
                    [
                        {
                            "locale":"en",
                            "title":"Dances with Wolves - Title",
                            "overview":"Dances with Wolves - Overview",
                            "tagline":"Dances with Wolves - Tagline",
                            "posterUrl":"https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg"
                        },
                        {
                            "locale":"de",
                            "title":"Der mit dem Wolf tanzt - Title",
                            "overview":"Der mit dem Wolf tanzt - Overview",
                            "tagline":"Der mit dem Wolf tanzt - Tagline",
                            "posterUrl":"https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg"
                        }
                    ]
            }
            """;

    public String getInvalidRequestBodyWolfMovieLocalizationsJson = """
            {
                "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                "localizations":
                    [
                        {
                            "locale":"eng",
                            "title":"Dances with Wolves - Title",
                            "overview":"Dances with Wolves - Overview",
                            "tagline":"Dances with Wolves - Tagline",
                            "posterUrl":"https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg"
                        },
                        {
                            "locale":"de",
                            "title":"Der mit dem Wolf tanzt - Title",
                            "overview":"Der mit dem Wolf tanzt - Overview",
                            "tagline":"Der mit dem Wolf tanzt - Tagline",
                            "posterUrl":"https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg"
                        }
                    ]
            }
            """;

    public String getValidRequestBodyWolfSceneOneEnJson = """
            {
                "uuid":"a9f669e3-3f7e-4cb3-899d-2e03e28ab2e8",
                "lon":"13.35943",
                "lat":"52.51263",
                "locale":"en",
                "title":"Dances with Wolves - Scene One Title",
                "description":"Dances with Wolves - Scene One Description"
            }
            """;

    public String getInvalidSceneLocaleRequestBodyWolfSceneOneEnJson = """
            {
                "uuid":"a9f669e3-3f7e-4cb3-899d-2e03e28ab2e8",
                "lon":"13.35943",
                "lat":"52.51263",
                "locale":"eng",
                "title":"Dances with Wolves - Scene One Title",
                "description":"Dances with Wolves - Scene One Description",
                "movie":
                    {
                        "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                        "title":"Dances with Wolves - Title",
                        "tmdbId":1051896,
                        "releaseYear":1970,
                        "tagline":"Dances with Wolves - Tagline",
                        "overview":"Dances with Wolves - Overview",
                        "genres":
                            {
                                "80":"western",
                                "85":"thriller"
                             },
                         "posterUrl":"https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg",
                         "imdbId":"imdbId",
                         "locale":"en"
                    }
            }
            """;

    public String getValidWolfSceneLocalizationsJson = """
                {
                    "sceneUuid":"a9f669e3-3f7e-4cb3-899d-2e03e28ab2e8",
                    "localizations":
                        [
                            {
                                "locale":"en",
                                "title":"Dances with Wolves - Scene One Title",
                                "description":"Dances with Wolves - Scene One Description"
                            },
                            {
                                "locale":"de",
                                "title":"Dances with Wolves - Scene One Title",
                                "description":"Der mit dem Wolf tanzt - Scene One Description"
                            }
                        ]
                }
            """;
}
