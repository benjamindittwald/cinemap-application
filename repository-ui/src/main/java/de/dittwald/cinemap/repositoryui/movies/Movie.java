package de.dittwald.cinemap.repositoryui.movies;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private UUID uuid;
    private int tmdbId;
    private Map<String, String> title;
    private MovieTmdb dtmdb;
}
