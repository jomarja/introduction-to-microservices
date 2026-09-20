package com.epam.resourceservice.client;

import com.epam.resourceservice.dto.Mp3Tags;
import com.epam.resourceservice.dto.SongMetadataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Sends song metadata changes to the Song Service.
 * <p>
 * The base URL is a Eureka service id; the load-balanced {@code RestClient.Builder} resolves it
 * to one of the registered instances per request.
 * <p>
 * Failures are logged and do not break the resource operation: the resource itself is already
 * stored (or removed), and metadata consistency is not guaranteed synchronously in this module.
 */
@Component
public class SongServiceClient {

    private static final Logger log = LoggerFactory.getLogger(SongServiceClient.class);
    private static final String SONGS_PATH = "/songs";

    private final RestClient restClient;

    public SongServiceClient(RestClient.Builder restClientBuilder, @Value("${song-service.url}") String baseUrl) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public void createSongMetadata(Long resourceId, Mp3Tags tags) {
        try {
            restClient.post()
                    .uri(SONGS_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(SongMetadataRequest.from(resourceId, tags))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.warn("Failed to create song metadata for resource ID={}", resourceId, e);
        }
    }

    public void deleteSongMetadata(List<Long> resourceIds) {
        try {
            restClient.delete()
                    .uri(uriBuilder -> uriBuilder.path(SONGS_PATH).queryParam("id", toCsv(resourceIds)).build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.warn("Failed to delete song metadata for resource IDs={}", resourceIds, e);
        }
    }

    private String toCsv(List<Long> resourceIds) {
        return resourceIds.stream().map(String::valueOf).collect(Collectors.joining(","));
    }
}
