package de.dittwald.cinemap.repository.scene;

import de.dittwald.cinemap.repository.movie.LocalizedId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.net.URL;

@Entity
public class LocalizedScene {

    public LocalizedScene() {
        this.localizedId = new LocalizedId();
    }

    public LocalizedScene(LocalizedId localizedId, Scene scene, String description, URL posterUrl) {
        this.localizedId = localizedId;
        this.scene = scene;
        this.description = description;
        this.posterUrl = posterUrl;
    }

    @EmbeddedId
    private LocalizedId localizedId;

    @ManyToOne()
    @MapsId("id")
    @JoinColumn(name = "id")
    private Scene scene;

    @Lob
    @Size(min = 1, max = 5000)
    private String description;

    @Column
    private URL posterUrl;

    public LocalizedId getLocalizedId() {
        return localizedId;
    }

    public void setLocalizedId(LocalizedId localizedId) {
        this.localizedId = localizedId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public URL getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(URL posterUrl) {
        this.posterUrl = posterUrl;
    }
}
