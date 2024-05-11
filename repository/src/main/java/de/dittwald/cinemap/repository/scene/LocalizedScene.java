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
}
