package de.dittwald.cinemap.repository.movie;

public class DummyMovieConstantsJson {

    public static final String WOLF_VALID_MOVIE_DTO = """
            {
                "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                "title":
                    {
                        "deu":"Der mit dem Wolf tanzt",
                        "eng":"Dances with Wolves"
                     },
                "tmdbId":1051896,
                "releaseYear":1970,
                "tagline":
                    {
                        "deu":"Der mit dem Wolf tanzt TAGLINE",
                        "eng":"Dances with Wolves TAGLINE"
                     },
                 "overview":
                    {
                        "deu":"Der mit dem Wolf tanzt OVERVIEW",
                        "eng":"Dances with Wolves OVERVIEW"
                     },
                 "genres":
                {
                    "80":"western",
                    "85":"thriller"
                 },
                 "poster":"Imadummyfile",
                 "imdbId":"imdbId"
            }
            """;

    public static final String WOLF_INVALID_MOVIE_DTO_MISSING_UUID = """
            {
                "title":
                    {
                        "deu":"Der mit dem Wolf tanzt",
                        "eng":"Dances with Wolves"
                     },
                "tmdbId":1051896,
                "releaseYear":1970,
                "tagline":
                    {
                        "deu":"Der mit dem Wolf tanzt TAGLINE",
                        "eng":"Dances with Wolves TAGLINE"
                     },
                 "overview":
                    {
                        "deu":"Der mit dem Wolf tanzt OVERVIEW",
                        "eng":"Dances with Wolves OVERVIEW"
                     },
                 "genres":
                {
                    "80":"western",
                    "85":"thriller"
                 },
                 "poster":"Imadummyfile",
                 "imdbId":"imdbId"
            }
            """;

    public static final String WOLF_INVALID_MOVIE_DTO_INVALID_UUID = """
            {
                "uuid":"aa7acd67-4052-a63f-90440c683e6d",
                "title":
                    {
                        "deu":"Der mit dem Wolf tanzt",
                        "eng":"Dances with Wolves"
                     },
                "tmdbId":1051896,
                "releaseYear":1970,
                "tagline":
                    {
                        "deu":"Der mit dem Wolf tanzt TAGLINE",
                        "eng":"Dances with Wolves TAGLINE"
                     },
                 "overview":
                    {
                        "deu":"Der mit dem Wolf tanzt OVERVIEW",
                        "eng":"Dances with Wolves OVERVIEW"
                     },
                 "genres":
                {
                    "80":"western",
                    "85":"thriller"
                 },
                 "poster":"Imadummyfile",
                 "imdbId":"imdbId"
            }
            """;

    public static final String WOLF_INVALID_MOVIE_DTO_NON_ISO_LANG = """
            {
                "uuid":"aa7acd67-4052-421d-a63f-90440c683e6d",
                "title":
                    {
                        "de":"Der mit dem Wolf tanzt",
                        "eng":"Dances with Wolves"
                     },
                "tmdbId":1051896,
                "releaseYear":1970,
                "tagline":
                    {
                        "deu":"Der mit dem Wolf tanzt TAGLINE",
                        "en":"Dances with Wolves TAGLINE"
                     },
                 "overview":
                    {
                        "deu":"Der mit dem Wolf tanzt OVERVIEW",
                        "en":"Dances with Wolves OVERVIEW"
                     },
                 "genres":
                {
                    "80":"western",
                    "85":"thriller"
                 },
                 "poster":"Imadummyfile",
                 "imdbId":"imdbId"
            }
            """;
}
