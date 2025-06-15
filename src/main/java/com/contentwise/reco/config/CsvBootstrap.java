package com.contentwise.reco.config;

import com.contentwise.reco.model.Movie;
import com.contentwise.reco.model.RatingEvent;
import com.contentwise.reco.model.ViewEvent;
import com.contentwise.reco.repository.MovieRepository;
import com.contentwise.reco.repository.RatingEventRepository;
import com.contentwise.reco.repository.UserRepository;
import com.contentwise.reco.repository.ViewEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Set;

import com.contentwise.reco.model.User;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ClassPathResource;


import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


/**
 * Bootstrap that loads movies, users and ratings/view events from
 * CSV files located in classpath:data/*.csv . Runs once at startup.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class CsvBootstrap implements CommandLineRunner {

    private final MovieRepository movieRepo;
    private final UserRepository userRepo;
    private final RatingEventRepository ratingRepo;
    private final ViewEventRepository viewRepo;


    @Transactional
    @Override
    public void run(String... args) throws Exception {
        loadMovies();
        loadUsers();
        loadRatingsAndViews();
    }



    private void loadMovies() throws Exception {
        try (var in = new ClassPathResource("data/movies.csv").getInputStream();
             var reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             var parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            parser.forEach(r -> movieRepo.save(Movie.builder()
                    .id(Long.parseLong(r.get("movie_id")))
                    .title(r.get("title"))
                    .genres(Set.of(r.get("genres").split("\\|")))
                    .build()));
            log.info("Loaded {} movies", movieRepo.count());
        }
    }

    private void loadUsers() throws Exception {
        try (var in = new ClassPathResource("data/users.csv").getInputStream();
             var reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             var parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            parser.forEach(r -> userRepo.save(User.builder()
                    .id(Long.parseLong(r.get("user_id")))
                    .username(r.get("username"))
                    .build()));
            log.info("Loaded {} users", userRepo.count());
        }
    }

    @Transactional
    private void loadRatingsAndViews() throws Exception {

        try (var in = new ClassPathResource("data/ratings.csv").getInputStream();
             var reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             var csv = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            csv.forEach(r -> {

                long userId = Long.parseLong(r.get("user_id"));
                long movieId = Long.parseLong(r.get("movie_id"));

                User user = userRepo.findById(userId).orElseThrow();
                Movie movie = movieRepo.findById(movieId).orElseThrow();

                Instant now = Instant.now();


                if (!r.get("rating").isBlank()) {
                    ratingRepo.save(RatingEvent.builder()
                            .user(user)
                            .movie(movie)
                            .rating(Integer.parseInt(r.get("rating")))
                            .source(RatingEvent.Source.EXPLICIT)
                            .ts(now)
                            .build());
                }

                else if (!r.get("view_percentage").isBlank()) {
                    int percent = Integer.parseInt(r.get("view_percentage"));

                    viewRepo.save(ViewEvent.builder()
                            .user(user)
                            .movie(movie)
                            .viewPercent(percent)
                            .ts(now)
                            .build());

                    int implicit = percent > 80 ? 5 : (percent >= 60 ? 4 : 3);
                    ratingRepo.save(RatingEvent.builder()
                            .user(user)
                            .movie(movie)
                            .rating(implicit)
                            .source(RatingEvent.Source.IMPLICIT)
                            .ts(now)
                            .build());
                }
            });
        }
    }
}


