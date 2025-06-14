package com.contentwise.reco;

import com.contentwise.reco.controller.EventController;
import com.contentwise.reco.controller.RecommendationController;
import com.contentwise.reco.service.EventService;
import com.contentwise.reco.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {RecommendationController.class, EventController.class})
class ApiLayerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @MockBean
    private EventService eventService;

    @Test
    void recommendationsEndpointWorks() throws Exception {
        mockMvc.perform(get("/v1/users/demo/recommendations"))
                .andExpect(status().isOk());
    }

    @Test
    void ratingEndpointWorks() throws Exception {
        String body = "{\"userId\":\"u1\",\"movieId\":\"m1\",\"rating\":5}";
        mockMvc.perform(post("/v1/events/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());
    }

    @Test
    void playbackEndpointWorks() throws Exception {
        String body = "{\"userId\":\"u1\",\"movieId\":\"m1\",\"position\":120}";
        mockMvc.perform(post("/v1/events/playback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());
    }
}
