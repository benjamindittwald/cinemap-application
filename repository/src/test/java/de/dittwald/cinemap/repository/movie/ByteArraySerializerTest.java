package de.dittwald.cinemap.repository.movie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;

public class ByteArraySerializerTest {

    @Test
    void shouldSerialize() {
        MovieDto movieDto = new MovieDto(
                UUID.fromString("aa7acd67-4052-421d-a63f-90440c683e6d"), Map.of("deu", "Der mit dem Wolf tanzt", "eng", "Dances with Wolves"),
                1051896, 1970,  Map.of("deu", "Der mit dem Wolf tanzt TAGLINE", "eng", "Dances with Wolves TAGLINE"),
                Map.of("deu", "Der mit dem Wolf tanzt OVERVIEW", "eng", "Dances with Wolves OVERVIEW"),
                Map.of(80, "western", 85, "Thriller"), ArrayUtils.toObject(Base64.getDecoder().decode("Imadummyfile")), "imdbId");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.valueToTree(movieDto);

        Assertions.assertEquals("Imadummyfile", node.get("poster").asText());
    }
}