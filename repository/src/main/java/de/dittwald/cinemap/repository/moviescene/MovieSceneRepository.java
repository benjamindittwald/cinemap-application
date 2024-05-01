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

package de.dittwald.cinemap.repository.moviescene;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieSceneRepository extends ListCrudRepository<MovieScene, Long> {

    Optional<MovieScene> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    boolean existsByUuid(UUID uuid);

    Optional<List<MovieScene>> findAllScenesOfMovie(UUID movieUuid);

    // Fixme: [ERROR: missing FROM-clause entry for table "m1_0" Position: 106]
//    @Modifying
//    @Query("delete from MovieScene ms where ms.movie.uuid = :movieUuid")
//    void deleteAllScenesFromMovie(@Param("movieUuid") UUID movieUuid);
}
