package de.dittwald.cinemap.repository.movie;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;

public class ByteArrayDeserializerTest {

    @Test
    void shouldDeserialize() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        MovieDto movieDto = mapper.readValue(DummyMovieConstantsJson.WOLF_VALID_MOVIE_DTO, MovieDto.class);

        Assertions.assertEquals("Imadummyfile",
                new String(Base64.getEncoder().encode(ArrayUtils.toPrimitive(movieDto.poster()))));
    }
}