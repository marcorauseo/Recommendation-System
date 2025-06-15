package com.contentwise.reco.repository;

import com.contentwise.reco.dto.IdAvgDto;
import com.contentwise.reco.model.Movie;
import com.contentwise.reco.model.RatingEvent;
import com.contentwise.reco.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RatingEventRepository   extends JpaRepository<RatingEvent,Long>{
    List<RatingEvent> findByUserId(Long userId,Pageable pageable);

    default List<RatingEvent> findByUserId(Long userId) {
        return findByUserId(userId, Pageable.unpaged());
    }

    Optional<RatingEvent> findByUserIdAndMovieId(Long userId, Long movieId);
    Optional<RatingEvent> findByUserAndMovie(User user, Movie movie);


    @Query("""
           select r from RatingEvent r
           join fetch r.movie
           where r.user.id = :uid
           order by r.ts desc
           """)
    List<RatingEvent> findByUserIdOrderByTsDesc(@Param("uid") Long userId, Pageable page);

    @Modifying
    @Transactional
    @Query("""
        insert into RatingEvent (user, movie, rating, source, ts)
        select u, m, :stars, :src, :ts
        from User u
        join Movie  m on m.id = :mid
        where u.id = :uid
    """)
    void saveExplicit(@Param("uid")   Long    userId,
                      @Param("mid")   Long    movieId,
                      @Param("stars") int     stars,
                      @Param("src") RatingEvent.Source src,
                      @Param("ts") Instant ts);

    @Modifying @Transactional
    @Query("""
        insert into RatingEvent (user, movie, rating, source, ts)
        select u, m,
               case when :perc >= 80 then 5
                    when :perc >= 60 then 4
                    else 0 end,
               :src,
               :ts
        from   User u
        join   Movie   m on m.id = :mid
        where  u.id = :uid
    """)
    void saveImplicit(@Param("uid")  Long    userId,
                      @Param("mid")  Long    movieId,
                      @Param("perc") int     viewPercent,
                      @Param("src") RatingEvent.Source src,
                      @Param("ts")   Instant ts);


    @Query("""
           select new com.contentwise.reco.dto.IdAvgDto(r.movie.id, avg(r.rating))
           from RatingEvent r
           where r.movie.id in :ids
           group by r.movie.id
           """)
    List<IdAvgDto> avgByMovieIds(@Param("ids") List<Long> ids);


}