package de.dittwald.cinemap.repository.movie;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.net.URL;

@Entity
public class LocalizedMovie implements Serializable {

    public LocalizedMovie() {
        this.localizedId = new LocalizedId();
    }

    public LocalizedMovie(LocalizedId localizedId, Movie movie, String title, String overview, String tagline,
                          URL posterUrl) {
        this.localizedId = localizedId;
        this.movie = movie;
        this.title = title;
        this.overview = overview;
        this.tagline = tagline;
        this.posterUrl = posterUrl;
    }

    @EmbeddedId
    private LocalizedId localizedId;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "id")
    private Movie movie;

    @Size(min = 1, max = 255)
    private String title;

    @Lob
    @Size(min = 1, max = 5000)
    private String overview;

    @Size(min = 1, max = 255)
    private String tagline;

    private URL posterUrl;

    public LocalizedId getLocalizedId() {
        return localizedId;
    }

    public void setLocalizedId(LocalizedId localizedId) {
        this.localizedId = localizedId;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public URL getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(URL posterUrl) {
        this.posterUrl = posterUrl;
    }
}
