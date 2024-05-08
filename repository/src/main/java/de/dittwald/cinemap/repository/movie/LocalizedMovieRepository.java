package de.dittwald.cinemap.repository.movie;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalizedMovieRepository extends ListCrudRepository<LocalizedMovie, String> {
}
