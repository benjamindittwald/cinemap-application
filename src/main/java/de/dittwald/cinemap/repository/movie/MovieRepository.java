/*
 * Copyright 2024 Benjamin Dittwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.dittwald.cinemap.repository.movie;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class MovieRepository {

    @PersistenceContext
    private EntityManager em;

    Logger log = LoggerFactory.getLogger(MovieRepository.class);

    @Transactional(readOnly = true)
    public List<Movie> findAll() {
        List<Movie> movies = em.createQuery("Select a From Movie a", Movie.class).getResultList();
        movies.forEach(movie -> log.debug("Found movie : {}", movie.toString()));
        return movies;
    }

    @Transactional(readOnly = true)
    public Movie findById(Long id) {
        Movie movie = em.find(Movie.class, id);
        log.debug("Found movie : {}", movie.toString());
        return movie;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<Movie> saveAll(List<Movie> movies) {
        movies.forEach(this::save);
        return movies;
    }

    @Transactional(rollbackFor = Exception.class)
    public Movie save(Movie movie) {
        em.persist(movie);
        log.debug("Persisted Movie : {}", movie.toString());
        return movie;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Movie movie) {
        em.remove(movie);
        log.debug("Removed Movie : {}", movie.toString());
    }

    @Transactional(rollbackFor = Exception.class)
    public Movie update(Movie movie) {
        Movie mergedMovie = em.merge(movie);
        log.debug("Updated Movie : {}, Old movie : {}", mergedMovie.toString(), movie.toString());
        return mergedMovie;
    }
}
