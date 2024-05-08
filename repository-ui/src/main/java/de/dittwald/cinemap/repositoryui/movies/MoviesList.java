package de.dittwald.cinemap.repositoryui.movies;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dittwald.cinemap.repositoryui.tmdb.TmdbId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MoviesList {

    private final RepositoryClient repositoryClient;

    public MoviesList(RepositoryClient repositoryClient) {
        this.repositoryClient = repositoryClient;
    }

    @GetMapping("/")
    public String index(Model model) throws JsonProcessingException {

        List<Movie> movies = new ArrayList<>(repositoryClient.getAllMovies());
        model.addAttribute("movies", movies);
        model.addAttribute("tmdbId", new TmdbId());

        return "index";
    }
}
