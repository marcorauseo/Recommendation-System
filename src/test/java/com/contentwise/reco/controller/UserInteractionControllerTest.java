package com.contentwise.reco.controller;

import com.contentwise.reco.service.InteractionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@WebMvcTest(UserInteractionController.class)
class UserInteractionControllerTest {

    @Autowired MockMvc mockMvc;

    @MockBean InteractionService interactionService;

    @Test
    void historyReturnsOk() throws Exception {
        when(interactionService.history(anyLong(), any(), any())).thenReturn(List.of());

        mockMvc.perform(get("/v1/users/1/events"))
                .andExpect(status().isOk());

        verify(interactionService).history(eq(1L), isNull(), any());
    }
}
