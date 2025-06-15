package com.contentwise.reco.service;

import com.contentwise.reco.dto.EventRequest;
import com.contentwise.reco.dto.EventType;
import com.contentwise.reco.model.Movie;
import com.contentwise.reco.model.RatingEvent;
import com.contentwise.reco.model.User;
import com.contentwise.reco.model.ViewEvent;
import com.contentwise.reco.repository.MovieRepository;
import com.contentwise.reco.repository.RatingEventRepository;
import com.contentwise.reco.repository.UserRepository;
import com.contentwise.reco.repository.ViewEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InteractionServiceTest {

    @Mock RatingEventRepository ratingRepo;
    @Mock MovieRepository movieRepo;
    @Mock ViewEventRepository viewRepo;
    @Mock UserRepository userRepo;

    @InjectMocks InteractionService service;

    private User user;
    private Movie movie;

    @BeforeEach
    void init() {
        user = User.builder().id(1L).username("u").build();
        movie = Movie.builder().id(2L).title("m").build();
    }

    @Test
    void addRatingCallsSaveExplicit() {
        service.addRating(1L, 2L, 4);

        verify(ratingRepo).saveExplicit(eq(1L), eq(2L), eq(4),
                eq(RatingEvent.Source.EXPLICIT), any(Instant.class));
    }

    @Test
    void addViewCallsSaveImplicit() {
        service.addView(1L, 2L, 80);

        verify(ratingRepo).saveImplicit(eq(1L), eq(2L), eq(80),
                eq(RatingEvent.Source.EXPLICIT), any(Instant.class));
    }

    @Test
    void ingestEventSavesViewAndImplicitRatingWhenPercentHigh() {
        when(userRepo.getReferenceById(1L)).thenReturn(user);
        when(movieRepo.getReferenceById(2L)).thenReturn(movie);
        when(ratingRepo.findByUserAndMovie(user, movie)).thenReturn(Optional.empty());

        EventRequest req = EventRequest.builder()
                .userId(1L)
                .movieId(2L)
                .type(EventType.VIEW)
                .viewPercent(90)
                .build();

        service.ingestEvent(req);

        verify(viewRepo).save(any(ViewEvent.class));
        verify(ratingRepo).save(any(RatingEvent.class));
    }




    @Test
    void history_rating_only() {
        RatingEvent re = new RatingEvent();
        re.setMovie(movie);
        re.setRating(4);
        re.setTs(Instant.now());
        re.setSource(RatingEvent.Source.EXPLICIT);

        when(ratingRepo.findByUserIdOrderByTsDesc(eq(1L), any(Pageable.class)))
                .thenReturn(List.of(re));

        var res = service.history(1L, "rating", PageRequest.of(0, 10));
        assertThat(res).hasSize(1)
                .first().isInstanceOf(com.contentwise.reco.dto.RatingDto.class);
    }

    @Test
    void history_view_only() {
        ViewEvent ve = new ViewEvent();
        ve.setMovie(movie);
        ve.setViewPercent(75);
        ve.setTs(Instant.now());

        when(viewRepo.findByUserIdOrderByTsDesc(eq(1L), any(Pageable.class)))
                .thenReturn(List.of(ve));

        var res = service.history(1L, "view", PageRequest.of(0, 10));
        assertThat(res).hasSize(1)
                .first().isInstanceOf(com.contentwise.reco.dto.ViewDto.class);
    }

    @Test
    void history_all_merges_ratings_and_views() {
        RatingEvent re = new RatingEvent();
        re.setMovie(movie);
        re.setRating(4);
        re.setTs(Instant.now());
        re.setSource(RatingEvent.Source.EXPLICIT);

        ViewEvent ve = new ViewEvent();
        ve.setMovie(movie);
        ve.setViewPercent(90);
        ve.setTs(Instant.now());

        when(ratingRepo.findByUserIdOrderByTsDesc(eq(1L), any(Pageable.class)))
                .thenReturn(List.of(re));
        when(viewRepo.findByUserIdOrderByTsDesc(eq(1L), any(Pageable.class)))
                .thenReturn(List.of(ve));

        var res = service.history(1L, null, Pageable.unpaged());
        assertThat(res).hasSize(2);
    }
}

