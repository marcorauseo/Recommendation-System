package com.contentwise.reco.repository;

import com.contentwise.reco.model.Movie;
import com.contentwise.reco.model.User;
import com.contentwise.reco.model.ViewEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ViewEventRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ViewEventRepository repo;

    private User user;
    private Movie movie;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("tester");
        em.persist(user);

        movie = new Movie();
        movie.setTitle("The Matrix");
        em.persist(movie);


        persistView(10, Instant.now().minusSeconds(300));
        persistView(60, Instant.now().minusSeconds(200));
        persistView(90, Instant.now().minusSeconds(100));

        em.flush();
    }

    private void persistView(int percent, Instant ts) {
        ViewEvent v = new ViewEvent();
        v.setUser(user);
        v.setMovie(movie);
        v.setViewPercent(percent);
        v.setTs(ts);
        em.persist(v);
    }

    @Test
    void findByUserId_withPageable_returnsLimitedResults() {
        List<ViewEvent> firstPage =
                repo.findByUserId(user.getId(), PageRequest.of(0, 2, Sort.Direction.DESC, "ts"));
        assertThat(firstPage).hasSize(2);


        assertThat(firstPage.get(0).getViewPercent()).isEqualTo(90);
    }

    @Test
    void findByUserId_defaultUnpaged_returnsAll() {
        List<ViewEvent> all = repo.findByUserId(user.getId());
        assertThat(all).hasSize(3);
    }

    @Test
    void findByUserIdOrderByTsDesc_respectsOrdering() {
        List<ViewEvent> ordered =
                repo.findByUserIdOrderByTsDesc(user.getId(), Pageable.unpaged());

        assertThat(ordered).hasSize(3);
        assertThat(ordered.get(0).getTs()).isAfter(ordered.get(1).getTs());
        assertThat(ordered.get(1).getTs()).isAfter(ordered.get(2).getTs());
    }
}
