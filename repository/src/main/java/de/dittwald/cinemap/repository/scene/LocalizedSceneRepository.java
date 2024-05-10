package de.dittwald.cinemap.repository.scene;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalizedSceneRepository extends ListCrudRepository<LocalizedScene, Long> {
}
