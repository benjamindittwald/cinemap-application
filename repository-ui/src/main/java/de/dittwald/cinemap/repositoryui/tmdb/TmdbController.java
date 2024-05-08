package de.dittwald.cinemap.repositoryui.tmdb;

import de.dittwald.cinemap.repositoryui.movies.Movie;
import de.dittwald.cinemap.repositoryui.movies.RepositoryClient;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class TmdbController {

    private final TmdbClient tmdbClient;
    private final RepositoryClient repositoryClient;

    public TmdbController(TmdbClient tmdbClient, RepositoryClient repositoryClient) {
        this.tmdbClient = tmdbClient;
        this.repositoryClient = repositoryClient;
    }

    @PostMapping("/tmdb")
    public String createMovieByTmdbId(@Valid @ModelAttribute TmdbId tmdbId) {

        Movie movie = this.tmdbClient.getMovieTmdb(tmdbId.getId());
        movie.setUuid(UUID.randomUUID());
        this.repositoryClient.createMovieByTmdbId(movie);

        return "index";
    }
}
