package com.contentwise.reco.repository;

import com.contentwise.reco.model.ViewEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ViewEventRepository     extends JpaRepository<ViewEvent,Long> {
    List<ViewEvent> findByUserId(Long userId,Pageable pageable);
    default List<ViewEvent> findByUserId(Long userId) {
        return findByUserId(userId, Pageable.unpaged());
    }

    @Query("""
           select v
           from ViewEvent v
           join fetch v.movie
           where v.user.id = :uid
           order by v.ts desc
           """)
    List<ViewEvent> findByUserIdOrderByTsDesc(@Param("uid") Long userId, Pageable pageable);

}
