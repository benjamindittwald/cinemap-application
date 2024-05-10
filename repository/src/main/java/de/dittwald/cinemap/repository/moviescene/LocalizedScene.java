package de.dittwald.cinemap.repository.moviescene;

import de.dittwald.cinemap.repository.movie.LocalizedId;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
public class LocalizedScene {

    public LocalizedScene() {
        this.localizedId = new LocalizedId();
    }

    public LocalizedScene(LocalizedId localizedId, Scene scene, String description) {
        this.localizedId = localizedId;
        this.scene = scene;
        this.description = description;
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

    public LocalizedId getLocalizedId() {
        return localizedId;
    }

    public void setLocalizedId(LocalizedId localizedId) {
        this.localizedId = localizedId;
    }

    public Scene getMovieScene() {
        return scene;
    }

    public void setMovieScene(Scene scene) {
        this.scene = scene;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
