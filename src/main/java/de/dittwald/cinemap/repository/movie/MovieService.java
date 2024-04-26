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

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MovieService {

    private final MovieDtoMapper movieDtoMapper;

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository, MovieDtoMapper movieDtoMapper) {
        this.movieRepository = movieRepository;
        this.movieDtoMapper = movieDtoMapper;
    }

    public List<MovieDto> findAll() {
        List<MovieDto> movieDtos = new ArrayList<>();

        this.movieRepository.findAll().forEach(movie -> movieDtos.add(this.movieDtoMapper.movieToMovieDto(movie)));

        return movieDtos;
    }

    public MovieDto findByUuid(UUID uuid) throws NotFoundException {
        return movieDtoMapper.movieToMovieDto(
                this.movieRepository.findByUuid(uuid).orElseThrow(() -> new NotFoundException("Movie not found")));
    }

    public MovieDto save(MovieDto movieDto) throws DataIntegrityViolationException {
        if (this.movieRepository.existsByUuid(movieDto.uuid())) {
            throw new DataIntegrityViolationException("UUID already in use");
        } else {
            return movieDtoMapper.movieToMovieDto(this.movieRepository.save(movieDtoMapper.movieDtoToMovie(movieDto)));
        }
    }

    public MovieDto update(MovieDto movieDto) throws NotFoundException {
        if (this.movieRepository.existsByUuid(movieDto.uuid())) {
            // Fixme: Given Movie.id is probably lost here
            return movieDtoMapper.movieToMovieDto(this.movieRepository.save(movieDtoMapper.movieDtoToMovie(movieDto)));
        } else {
            throw new NotFoundException("Movie not found");
        }
    }

    public void deleteByUuid(UUID uuid) throws NotFoundException {
        if (this.movieRepository.existsByUuid(uuid)) {
            this.movieRepository.deleteByUuid(uuid);
        } else {
            throw new NotFoundException("Movie not found");
        }
    }

}
