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

package de.dittwald.cinemap.repository.tmdb;

import de.dittwald.cinemap.repository.exceptions.TmdbReadException;
import de.dittwald.cinemap.repository.movie.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tmdb")
@Tag(name = "TMDB")
@Validated
public class TmdbRestController {

    private final MovieService movieService;

    public TmdbRestController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PutMapping("/{tmdbId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Create movie via TMDB ID",
            description = "You can  provide a TMDB ID and the repository gathers all required movie detail from TMDB " +
                    "(https://www.themoviedb.org). The repository also collects all available localisations for the " +
                    "movie.")
    @ApiResponse(responseCode = "204", description = "The movie is created")
    void createMovieViaTmdbId(@PathVariable(value = "tmdbId") int tmdbId) throws TmdbReadException {
        this.movieService.createMovieViaTmdbId(tmdbId);
    }
}
