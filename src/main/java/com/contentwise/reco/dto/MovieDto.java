package com.contentwise.reco.dto;

import lombok.Value;

@Value
public class MovieDto {
    Long   id;
    String title;
    Double avgRating;
}