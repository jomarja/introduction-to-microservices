package com.epam.resourceservice.client;

import com.epam.resourceservice.dto.Mp3Tags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class SongServiceClientTest {

    private static final String BASE_URL = "http://song-service";
    private static final Mp3Tags TAGS = new Mp3Tags("Test Song", "Test Artist", "Test Album", "00:07", "2025");

    private MockRestServiceServer server;
    private SongServiceClient songServiceClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        songServiceClient = new SongServiceClient(builder, BASE_URL);
    }

    @Test
    void createSendsResourceIdWithTags() {
        server.expect(requestTo(BASE_URL + "/songs"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Song"))
                .andExpect(jsonPath("$.duration").value("00:07"))
                .andRespond(withSuccess());

        songServiceClient.createSongMetadata(1L, TAGS);

        server.verify();
    }

    @Test
    void deleteSendsIdsAsCsv() {
        server.expect(requestTo(startsWith(BASE_URL + "/songs?id=")))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(queryParam("id", "1,2,3"))
                .andRespond(withSuccess());

        songServiceClient.deleteSongMetadata(List.of(1L, 2L, 3L));

        server.verify();
    }

    @Test
    void songServiceFailureDoesNotBreakResourceOperation() {
        server.expect(requestTo(BASE_URL + "/songs")).andRespond(withServerError());

        assertThatNoException().isThrownBy(() -> songServiceClient.createSongMetadata(1L, TAGS));
    }
}
