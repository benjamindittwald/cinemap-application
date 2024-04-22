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
import java.util.Optional;

@Service
public class MovieService {

    final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieDto> findAll() {
        return MovieDTOMapper.movieListToDtoList(this.movieRepository.findAll());
    }

    public MovieDto findById(Long id) throws NotFoundException {
        Optional<Movie> movie = movieRepository.findById(id);

        if (movie.isPresent()) {
            return MovieDTOMapper.movieToDTO(movie.get());
        } else {
            throw new NotFoundException("Movie not found");
        }
    }

    public MovieDto save(MovieInputDto movieDto) {
        return MovieDTOMapper.movieToDTO(this.movieRepository.save(MovieDTOMapper.inputDtoToMovie(movieDto)));
    }

    public MovieDto update(MovieDto movieDto) throws NotFoundException {
        if (this.movieRepository.findById(movieDto.id()).isPresent()) {
            return MovieDTOMapper.movieToDTO(this.movieRepository.save(MovieDTOMapper.dtoToMovie(movieDto)));
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
