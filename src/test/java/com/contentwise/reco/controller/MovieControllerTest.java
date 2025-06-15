package com.contentwise.reco.controller;

import com.contentwise.reco.dto.MovieDto;
import com.contentwise.reco.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean MovieService movieService;

    @Test
    void listReturnsOk() throws Exception {
        when(movieService.list(any(), any(), any(), any())).thenReturn(
                new PageImpl<>(List.of(new MovieDto(1L, "T", 4.0))));

        mockMvc.perform(get("/v1/movies?genre=action")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(movieService).list(eq("action"), isNull(), isNull(), any());
    }
}
