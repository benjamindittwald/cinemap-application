package de.dittwald.cinemap.repositoryui.tmdb;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TmdbId {

    @Min(value = -2147483648) // From TMDB API Reference movie Details
    @Max(value = 2147483647) // https://developer.themoviedb.org/reference/movie-details
    Integer id;
}
