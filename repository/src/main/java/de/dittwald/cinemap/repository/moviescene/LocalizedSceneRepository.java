package de.dittwald.cinemap.repository.moviescene;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalizedSceneRepository extends ListCrudRepository<LocalizedScene, Long> {
}
