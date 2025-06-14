package com.contentwise.reco.model;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "app_user")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id private Long id;
    private String username;
}
