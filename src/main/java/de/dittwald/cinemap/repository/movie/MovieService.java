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

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieDto> findAll() {
        return MovieDTOMapperLegacy.movieListToDtoList(this.movieRepository.findAll());
    }

    public MovieDto findById(Long id) throws NotFoundException {
        return MovieDTOMapperLegacy.movieToDTO(this.movieRepository.findById(id).orElseThrow(() -> new NotFoundException(
                "Movie not found")));
    }

    // Todo: Implement check in order to avoid duplicates
    public MovieDto save(MovieDto movieDto) {
        return MovieDTOMapperLegacy.movieToDTO(this.movieRepository.save(MovieDTOMapperLegacy.dtoToMovie(movieDto)));
    }

    public MovieDto update(MovieDto movieDto) throws NotFoundException {
        if (this.movieRepository.findById(movieDto.id()).isPresent()) {
            return MovieDTOMapperLegacy.movieToDTO(this.movieRepository.save(MovieDTOMapperLegacy.dtoToMovie(movieDto)));
        } else {
            throw new NotFoundException("Movie not found");
        }
    }

    public void deleteById(Long id) throws NotFoundException {
        if (this.movieRepository.findById(id).isPresent()) {
            this.movieRepository.deleteById(id);
        } else {
            throw new NotFoundException("Movie not found");
        }
    }

}
