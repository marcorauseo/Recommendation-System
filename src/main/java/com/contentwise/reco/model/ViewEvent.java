package com.contentwise.reco.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity @Table(name = "view_event")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ViewEvent {
    @Id @GeneratedValue private Long id;
    private Integer viewPercent;
    @Column(name="ts") private Instant ts;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="movie_id")
    private Movie movie;
}
