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

package de.dittwald.cinemap.repository.movie;

import de.dittwald.cinemap.repository.movie.dto.MovieFlatDto;
import de.dittwald.cinemap.repository.movie.dto.MovieLocalisationDto;
import de.dittwald.cinemap.repository.movie.dto.MovieLocalizationsDto;
import de.dittwald.cinemap.repository.movie.entity.LocalizedId;
import de.dittwald.cinemap.repository.movie.entity.LocalizedMovie;
import de.dittwald.cinemap.repository.movie.entity.Movie;
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
public class DummyMovies {

    public DummyMovies() throws URISyntaxException, MalformedURLException {

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

        // Scene
        this.scene = new Scene();
        this.scene.setUuid(UUID.randomUUID());
        this.scene.setLat(52.51263);
        this.scene.setLon(13.35943);
        this.scene.setMovie(this.wolf);

        LocalizedScene lmsEn = new LocalizedScene();
        lmsEn.setLocalizedId(new LocalizedId("en"));
        lmsEn.setDescription("Dances with Wolves - Scene Description");
        lmsEn.setScene(this.scene);
        this.scene.getLocalizedMoviesScenes().put("en", lmsEn);

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
        this.wolfLocalizationsDto = new MovieLocalizationsDto(this.wolf.getUuid(),
                List.of(new MovieLocalisationDto(this.wolfFlatEnDto.locale(), this.wolfFlatEnDto.title(),
                                this.wolfFlatEnDto.overview(), this.wolfFlatEnDto.tagline(),
                                this.wolfFlatEnDto.posterUrl()),
                        new MovieLocalisationDto(this.wolfFlatDeDto.locale(), this.wolfFlatDeDto.title(),
                                this.wolfFlatDeDto.overview(), this.wolfFlatDeDto.tagline(),
                                this.wolfFlatDeDto.posterUrl())));
    }

    private Movie wolf;
    private MovieFlatDto wolfFlatEnDto;
    private LocalizedMovie wolfLocalizedMovieEn;
    private LocalizedMovie wolfLocalizedMovieDe;
    private MovieLocalizationsDto wolfLocalizationsDto;
    private MovieFlatDto wolfFlatDeDto;
    private Movie nobody;
    private MovieFlatDto nobodyFlatEnDto;
    private MovieFlatDto nobodyFlatDeDto;
    private LocalizedMovie nobodyLocalizedMovieEn;
    private LocalizedMovie nobodyLocalizedMovieDe;
    private Scene scene;

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
                "movieUuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
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

}
