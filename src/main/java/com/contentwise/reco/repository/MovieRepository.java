package com.contentwise.reco.repository;

import com.contentwise.reco.model.Movie;
import com.contentwise.reco.dto.MovieDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {



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


    @Query("""
        SELECT new com.contentwise.reco.dto.MovieDto(
                   m.id,
                   m.title,
                   COALESCE(AVG(r.rating), 0)
           )
        FROM Movie m
        LEFT JOIN RatingEvent r     ON r.movie = m
        WHERE m.id NOT IN (SELECT re.movie.id  FROM RatingEvent re WHERE re.user.id = :uid)
          AND m.id NOT IN (SELECT ve.movie.id  FROM ViewEvent  ve WHERE ve.user.id = :uid)
          AND (:genre IS NULL OR :genre IN elements(m.genres))
        GROUP BY m.id, m.title
        HAVING (:minRating IS NULL OR AVG(r.rating) >= :minRating)
        ORDER BY COALESCE(AVG(r.rating), 0) DESC
        """)
    Page<MovieDto> findRecommendations(@Param("uid")        Long    uid,
                             @Param("genre")      String  genre,
                             @Param("minRating")  Integer minRating,
                             Pageable pageable);





}