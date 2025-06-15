package com.contentwise.reco;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

public class TestUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String asJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MediaType json() {
        return MediaType.APPLICATION_JSON;
    }
}
