package com.contentwise.reco.repository;

import com.contentwise.reco.model.Movie;
import com.contentwise.reco.dto.MovieDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieRepository extends JpaRepository<Movie, Long> {



    /**
     * Catalogo film con media voti e filtri opzionali.
     *
     * @param genre     filtro sul genere oppure null
     * @param min       rating medio minimo oppure null
     * @param max       rating medio massimo oppure null
     */
    @Query("""
           SELECT new com.contentwise.reco.dto.MovieDto(
                   m.id,
                   m.title,
                   COALESCE(AVG(r.rating), 0)
           )
           FROM Movie m
           LEFT JOIN RatingEvent r ON r.movie = m
           LEFT JOIN m.genres g
           WHERE (:genre IS NULL OR g = :genre)
           GROUP BY m.id, m.title
           HAVING (:min IS NULL OR AVG(r.rating) >= :min)
              AND (:max IS NULL OR AVG(r.rating) <= :max)
           """)
    Page<MovieDto> findCatalog(@Param("genre") String genre,
                               @Param("min")   Integer min,
                               @Param("max")   Integer max,
                               Pageable page);
}