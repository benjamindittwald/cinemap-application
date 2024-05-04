package de.dittwald.cinemap.repositoryui.movies;

import java.net.URL;

public record MovieTmdb(URL posterUrl, String overview, String tagline, URL imdbUrl) {
}
