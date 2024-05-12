/*
 * Copyright 2024 Benjamin Dittwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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