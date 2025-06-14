package com.contentwise.reco.service;

import com.contentwise.reco.dto.EventRequest;
import com.contentwise.reco.dto.RatingDto;
import com.contentwise.reco.dto.ViewDto;
import com.contentwise.reco.model.Movie;
import com.contentwise.reco.model.RatingEvent;
import com.contentwise.reco.model.User;
import com.contentwise.reco.model.ViewEvent;
import com.contentwise.reco.repository.MovieRepository;
import com.contentwise.reco.repository.RatingEventRepository;
import com.contentwise.reco.repository.UserRepository;
import com.contentwise.reco.repository.ViewEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InteractionService {

    private final UserRepository userRepo;
    private final MovieRepository movieRepo;
    private final RatingEventRepository ratingRepo;
    private final ViewEventRepository viewRepo;

    //TODO forse non servono
//    @Transactional
//    public void addRating(Long userId, Long movieId, int stars) {
//        ratingRepo.saveExplicit(
//                userId,
//                movieId,
//                stars,
//                RatingEvent.Source.EXPLICIT,
//                Instant.now()
//        );
//
//    }
//
//    @Transactional
//    public void addView(Long userId, Long movieId, int percent) {
//        ratingRepo.saveImplicit(
//                userId,
//                movieId,
//                percent,
//                RatingEvent.Source.EXPLICIT,
//                Instant.now()
//        );
//    }

    /* ----------  public query API (used by UserInteractionController) ---------- */
    @Transactional(readOnly = true)
    public List<?> history(Long userId, String type, Pageable page) {
        return switch (type == null ? "all" : type.toLowerCase()) {
            case "rating" -> ratings(userId, page);
            case "view"   -> views(userId, page);
            default       -> {
                var res = new java.util.ArrayList<>();
                res.addAll(ratings(userId, page));
                res.addAll(views(userId, page));
                yield res;
            }
        };
    }


    private List<RatingDto> ratings(Long userId, Pageable p) {
        return ratingRepo.findByUserIdOrderByTsDesc(userId, p).stream()
                .map(r -> new RatingDto(
                        r.getMovie().getId(),
                        r.getMovie().getTitle(),
                        r.getRating(),
                        r.getTs(),
                        r.getSource()
                        ))
                .toList();
    }

    private List<ViewDto> views(Long userId, Pageable p) {
        return viewRepo.findByUserIdOrderByTsDesc(userId, p).stream()
                .map(v -> new ViewDto(
                        v.getMovie().getId(),
                        v.getMovie().getTitle(),
                        v.getViewPercent(),
                        v.getTs()))
                .toList();
    }

    /* ----------  ingestion API (used by EventController / Kafka consumer) ---------- */
    @Transactional
    public void ingestEvent(EventRequest req) {
        var user  = userRepo.getReferenceById(req.getUserId());
        var movie = movieRepo.getReferenceById(req.getMovieId());

        if (req.getViewPercent() != null) {
            var view = new ViewEvent();
            view.setUser(user);
            view.setMovie(movie);
            view.setViewPercent(req.getViewPercent());
            view.setTs(Instant.now());
            viewRepo.save(view);

            if (req.getRating() == null && req.getViewPercent() >= 60) {
                int implicit = req.getViewPercent() > 80 ? 5 : 4;
                upsertRating(user, movie, implicit, RatingEvent.Source.IMPLICIT);
            }
        }
        if (req.getRating() != null) {
            upsertRating(user, movie, req.getRating(), RatingEvent.Source.EXPLICIT);
        }
    }

    @Transactional
    private void upsertRating(User user, Movie movie, int stars, RatingEvent.Source src) {
        ratingRepo.findByUserAndMovie(user, movie).ifPresentOrElse(e -> {
            if (e.getSource() == RatingEvent.Source.EXPLICIT && src == RatingEvent.Source.IMPLICIT) return;
            e.setRating(stars);
            e.setSource(src);
            e.setTs(Instant.now());
        }, () -> {
            var rating = new RatingEvent();
            rating.setUser(user);
            rating.setMovie(movie);
            rating.setRating(stars);
            rating.setSource(src);
            rating.setTs(Instant.now());
            ratingRepo.save(rating);
        });
    }
}
