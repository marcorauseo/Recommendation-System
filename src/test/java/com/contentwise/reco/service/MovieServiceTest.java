package com.contentwise.reco.service;

import com.contentwise.reco.dto.MovieDto;
import com.contentwise.reco.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository repo;

    @InjectMocks
    private MovieService service;

    @Test
    void listDelegatesToRepository() {
        Page<MovieDto> expected = new PageImpl<>(List.of(new MovieDto(1L, "Test", 4.5)));
        when(repo.findCatalog(any(), any(), any(), any(Pageable.class))).thenReturn(expected);

        Page<MovieDto> result = service.list(null, null, null, Pageable.unpaged());

        assertThat(result).isEqualTo(expected);
        verify(repo).findCatalog(isNull(), isNull(), isNull(), any(Pageable.class));
    }
}
