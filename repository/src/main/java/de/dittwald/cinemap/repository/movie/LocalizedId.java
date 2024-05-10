package de.dittwald.cinemap.repository.movie;

import de.dittwald.cinemap.repository.validation.Iso639Constraint;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LocalizedId implements Serializable {

    public LocalizedId(String locale) {
        this.locale = locale;
    }

    public LocalizedId() {
    }

    private Long id;

    // Todo: Switch to ISO-639-1
    @Iso639Constraint
    private String locale;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LocalizedId that = (LocalizedId) o;
        return Objects.equals(id, that.id) && Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(locale);
        return result;
    }
}
