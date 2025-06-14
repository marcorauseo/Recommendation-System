package com.contentwise.reco.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity @Table(
        name = "rating_event",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","movie_id"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RatingEvent {
    @Id @GeneratedValue
    private Long id;

    private Integer rating;

    @Enumerated(EnumType.STRING)
    private Source source;

    private Instant ts;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    public enum Source { EXPLICIT, IMPLICIT }
}

