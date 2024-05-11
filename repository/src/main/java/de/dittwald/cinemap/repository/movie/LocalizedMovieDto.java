package de.dittwald.cinemap.repository.movie;

import java.net.URL;

public record LocalizedMovieDto(String locale, String title, String overview, String tagline, URL posterUrl) {
}
