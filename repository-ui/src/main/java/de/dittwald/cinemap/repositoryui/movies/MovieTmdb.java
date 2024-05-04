package de.dittwald.cinemap.repositoryui.movies;

import java.net.URL;
import java.util.Date;

public record MovieTmdb(URL posterUrl, String overview, String tagline, URL imdbUrl, String releaseYear) {
}
