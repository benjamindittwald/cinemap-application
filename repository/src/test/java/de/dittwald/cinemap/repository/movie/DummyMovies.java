package de.dittwald.cinemap.repository.movie;

import de.dittwald.cinemap.repository.scene.LocalizedScene;
import de.dittwald.cinemap.repository.scene.Scene;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;

public class DummyMovies {

    public DummyMovies() throws URISyntaxException, MalformedURLException {

        // Wolf
        UUID wolfUuid = UUID.randomUUID();

        this.wolf = new Movie();
        this.wolf.setUuid(wolfUuid);
        this.wolf.setGenres(Map.of(80, "western", 85, "Thriller"));
        this.wolf.setReleaseYear(1970);
        this.wolf.setTmdbId(505);
        this.wolf.setImdbId("imdbID");

        LocalizedMovie wolfLmEn = new LocalizedMovie();
        wolfLmEn.setLocalizedId(new LocalizedId("eng"));
        wolfLmEn.setOverview("Dances with Wolves - Overview");
        wolfLmEn.setTagline("Dances with Wolves - Tagline");
        wolfLmEn.setTitle("Dances with Wolves - Title");
        wolfLmEn.setPosterUrl(new URI("https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg").toURL());
        wolfLmEn.setMovie(this.wolf);
        this.wolf.getLocalizedMovies().put("eng", wolfLmEn);

        LocalizedMovie wolfLmDe = new LocalizedMovie();
        wolfLmDe.setLocalizedId(new LocalizedId("deu"));
        wolfLmDe.setOverview("Der mit dem Wolf tanzt - Overview");
        wolfLmDe.setTagline("Der mit dem Wolf tanzt - Tagline");
        wolfLmDe.setTitle("Der mit dem Wolf tanzt - Title");
        wolfLmDe.setPosterUrl(new URI("https://image.tmdb.org/t/p//w300//g09UIYfShY8uWGMGP3HkvWp8L8n.jpg").toURL());
        wolfLmDe.setMovie(this.wolf);
        this.wolf.getLocalizedMovies().put("deu", wolfLmDe);

        this.wolfDto = new MovieDto(wolfUuid, 505, 1970, Map.of(80, "western", 85, "Thriller"), "imdbID",
                Map.of("eng", wolfLmEn, "deu", wolfLmDe));

        // Nobody
        UUID nobodyUuid = UUID.randomUUID();

        this.nobody = new Movie();
        this.nobody.setUuid(nobodyUuid);
        this.nobody.setGenres(Map.of(80, "western", 85, "Thriller"));
        this.nobody.setReleaseYear(1970);
        this.nobody.setTmdbId(505);
        this.nobody.setImdbId("imdbID");

        LocalizedMovie nobodyLmEn = new LocalizedMovie();
        nobodyLmEn.setLocalizedId(new LocalizedId("eng"));
        nobodyLmEn.setOverview("My Name is Nobody - Overview");
        nobodyLmEn.setTagline("My Name is Nobody - Tagline");
        nobodyLmEn.setTitle("My Name is Nobody - Title");
        nobodyLmEn.setMovie(this.nobody);
        this.nobody.getLocalizedMovies().put("eng", nobodyLmEn);

        LocalizedMovie nobodyLmDe = new LocalizedMovie();
        nobodyLmDe.setLocalizedId(new LocalizedId("deu"));
        nobodyLmDe.setOverview("Mein Name ist Nobody - Overview");
        nobodyLmDe.setTagline("Mein Name ist Nobody - Tagline");
        nobodyLmDe.setTitle("Mein Name ist Nobody - Title");
        nobodyLmDe.setMovie(this.nobody);
        this.nobody.getLocalizedMovies().put("deu", nobodyLmDe);

        this.nobodyDto = new MovieDto(nobodyUuid, 505, 1970, Map.of(80, "western", 85, "Thriller"), "imdbID",
                Map.of("eng", nobodyLmEn, "deu", nobodyLmDe));

        // Scene
        this.scene = new Scene();
        this.scene.setUuid(UUID.randomUUID());
        this.scene.setLat(52.51263);
        this.scene.setLon(13.35943);
        this.scene.setMovie(this.wolf);

        LocalizedScene lmsEn = new LocalizedScene();
        lmsEn.setLocalizedId(new LocalizedId("eng"));
        lmsEn.setDescription("Dances with Wolves - Scene Description");
        lmsEn.setScene(this.scene);
        this.scene.getLocalizedMoviesScenes().put("eng", lmsEn);
    }

    private Movie wolf;
    private MovieDto wolfDto;
    private Movie nobody;
    private MovieDto nobodyDto;
    private Scene scene;

    public MovieDto getWolfDto() {
        return wolfDto;
    }

    public void setWolfDto(MovieDto wolfDto) {
        this.wolfDto = wolfDto;
    }

    public Movie getWolf() {
        return wolf;
    }

    public void setWolf(Movie wolf) {
        this.wolf = wolf;
    }

    public Movie getNobody() {
        return nobody;
    }

    public void setNobody(Movie nobody) {
        this.nobody = nobody;
    }

    public MovieDto getNobodyDto() {
        return nobodyDto;
    }

    public void setNobodyDto(MovieDto nobodyDto) {
        this.nobodyDto = nobodyDto;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
