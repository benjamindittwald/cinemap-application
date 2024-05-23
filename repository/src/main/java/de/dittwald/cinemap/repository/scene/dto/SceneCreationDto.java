package de.dittwald.cinemap.repository.scene.dto;

import de.dittwald.cinemap.repository.validation.Iso6391Constraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SceneCreationDto(@NotNull UUID uuid,

                               @NotNull Double lon,

                               @NotNull Double lat,

                               @NotNull @Iso6391Constraint String locale, @Size(min = 1, max = 50) String title,

                               @Size(min = 1, max = 5000) String description) {
}
