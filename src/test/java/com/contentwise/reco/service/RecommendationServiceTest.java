package com.contentwise.reco.service;

import com.contentwise.reco.dto.MovieDto;
import com.contentwise.reco.model.Recommendation;
import com.contentwise.reco.repository.MovieRepository;
import com.contentwise.reco.repository.RecommendationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recoRepo;
    @Mock
    private MovieRepository movieRepo;

    @InjectMocks
    private RecommendationService service;

    @Test
    void recommendForUserReturnsList() {
        List<Recommendation> list = List.of(new Recommendation());
        when(recoRepo.findByUserId("abc")).thenReturn(list);

        List<Recommendation> result = service.recommendForUser("abc");

        assertThat(result).isEqualTo(list);
        verify(recoRepo).findByUserId("abc");
    }

    @Test
    void recommendCallsMovieRepo() {
        Page<MovieDto> expected = new PageImpl<>(List.of(new MovieDto(1L, "T", 3.0)));
        when(movieRepo.findRecommendations(anyLong(), any(), any(), any(PageRequest.class))).thenReturn(expected);

        Page<MovieDto> result = service.recommend(1L, 5, null, null);

        assertThat(result).isEqualTo(expected);
        verify(movieRepo).findRecommendations(eq(1L), isNull(), isNull(), any(PageRequest.class));
    }
}
