
package com.contentwise.reco.repository;

import com.contentwise.reco.model.Movie;
import com.contentwise.reco.model.RatingEvent;
import com.contentwise.reco.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RatingEventRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private RatingEventRepository repo;

    private User user;
    private Movie movie1;
    private Movie movie2;
    private Movie movie3;

    @BeforeEach
    void setup() {
        /* ----- user ----- */
        user = new User();

        user.setUsername("User1");
        em.persist(user);


        movie1 = createMovie( "Movie1");
        movie2 = createMovie( "Movie2");
        movie3 = createMovie( "Movie3");


        persistRating(movie1, 3, Instant.now().minusSeconds(300));
        persistRating(movie2, 4, Instant.now().minusSeconds(200));
        persistRating(movie3, 5, Instant.now().minusSeconds(100));

        em.flush();
    }

    private Movie createMovie( String title) {
        Movie m = new Movie();
        m.setTitle(title);
        em.persist(m);
        return m;
    }

    private void persistRating(Movie movie, int stars, Instant ts) {
        RatingEvent r = new RatingEvent();
        r.setUser(user);
        r.setMovie(movie);
        r.setRating(stars);
        r.setTs(ts);
        r.setSource(RatingEvent.Source.EXPLICIT);
        em.persist(r);
    }

    @Test
    @DisplayName("findByUserId(unpaged) restituisce tutti i rating dell'utente")
    void defaultMethodReturnsAll() {
        List<RatingEvent> all = repo.findByUserId(user.getId());
        assertThat(all).hasSize(3);
    }

    @Test
    @DisplayName("findByUserId(Pageable) limita e ordina per ts desc")
    void pagedMethodReturnsSlice() {
        var pageable = PageRequest.of(0, 2, Sort.Direction.DESC, "ts");
        List<RatingEvent> slice = repo.findByUserId(user.getId(), pageable);

        assertThat(slice)
                .hasSize(2)
                .extracting(RatingEvent::getRating)
                .containsExactly(5,4);
    }

    @Test
    @DisplayName("findByUserIdOrderByTsDesc restituisce elenco ordinato")
    void customQueryOrdered() {
        List<RatingEvent> ordered = repo.findByUserIdOrderByTsDesc(user.getId(), Pageable.unpaged());
        assertThat(ordered).isSortedAccordingTo((a,b) -> b.getTs().compareTo(a.getTs()));
    }

    @Nested
    class InsertQueries {

        @Test
        void saveExplicit_inserisce_nuovo_rating() {
            Movie newMovie = createMovie( "Brand new");
            repo.saveExplicit(user.getId(), newMovie.getId(), 2,
                    RatingEvent.Source.EXPLICIT, Instant.now());
            em.flush();

            Optional<RatingEvent> opt = repo.findByUserIdAndMovieId(user.getId(), newMovie.getId());
            assertThat(opt).isPresent()
                    .get()
                    .returns(2, RatingEvent::getRating)
                    .returns(RatingEvent.Source.EXPLICIT, RatingEvent::getSource);
        }

        @Test
        void saveImplicit_view80plus_crea5stelle() {
            Movie implicitMovie = createMovie( "Implicit 5*");
            repo.saveImplicit(user.getId(), implicitMovie.getId(), 85,
                    RatingEvent.Source.IMPLICIT, Instant.now());
            em.flush();

            int rating = repo.findByUserIdAndMovieId(user.getId(), implicitMovie.getId())
                    .map(RatingEvent::getRating)
                    .orElse(-1);
            assertThat(rating).isEqualTo(5);
        }

        @Test
        void saveImplicit_view60to79_crea4stelle() {
            Movie implicitMovie = createMovie( "Implicit 4*");
            repo.saveImplicit(user.getId(), implicitMovie.getId(), 70,
                    RatingEvent.Source.IMPLICIT, Instant.now());
            em.flush();

            int rating = repo.findByUserIdAndMovieId(user.getId(), implicitMovie.getId())
                    .map(RatingEvent::getRating)
                    .orElse(-1);
            assertThat(rating).isEqualTo(4);
        }

        @Test
        void saveImplicit_viewBelow60_crea0stelle() {
            Movie implicitMovie = createMovie( "Implicit 0*");
            repo.saveImplicit(user.getId(), implicitMovie.getId(), 20,
                    RatingEvent.Source.IMPLICIT, Instant.now());
            em.flush();

            int rating = repo.findByUserIdAndMovieId(user.getId(), implicitMovie.getId())
                    .map(RatingEvent::getRating)
                    .orElse(-1);
            assertThat(rating).isZero();
        }
    }
}
