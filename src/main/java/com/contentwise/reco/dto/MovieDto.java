package com.contentwise.reco.dto;

import lombok.Getter;
import lombok.Value;

@Value
@Getter
public class MovieDto {
    Long   id;
    String title;
    Double avgRating;
}