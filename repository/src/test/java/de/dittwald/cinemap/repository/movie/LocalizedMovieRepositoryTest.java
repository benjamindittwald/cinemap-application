package de.dittwald.cinemap.repository.movie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
        classes = {LocalizedMovieRepository.class}))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LocalizedMovieRepositoryTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16.2-alpine").withInitScript("schema.sql");

    @Autowired
    LocalizedMovieRepository localizedMovieRepository;

    @Test
    void shouldStoreLocalizedMovie() {
//        MovieLocalized movieLocalized = new MovieLocalized(new LocalizedId("eng"), "Dances with Wolfs", "Dances with Wolfs - Overview",
//                "Dances with Wolfs - Tagline");


//        this.movieLocalizedRepository.save(movieLocalized);
//        Optional<MovieLocalized> found = this.movieLocalizedRepository.findById("eng");
    }
}