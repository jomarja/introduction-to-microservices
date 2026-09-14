package com.epam.songservice.controller;

import com.epam.songservice.dto.SongDto;
import com.epam.songservice.dto.SongRequest;
import com.epam.songservice.exception.InvalidIdException;
import com.epam.songservice.exception.SongMetadataAlreadyExistsException;
import com.epam.songservice.exception.SongMetadataNotFoundException;
import com.epam.songservice.service.SongService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SongController.class)
class SongControllerTest {

    private static final SongRequest VALID_REQUEST =
            new SongRequest(1L, "Test Song", "Test Artist", "Test Album", "03:45", "2024");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SongService songService;

    @Test
    void createReturnsStoredMetadataId() throws Exception {
        when(songService.create(any(SongRequest.class))).thenReturn(1L);

        mockMvc.perform(postSong(VALID_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createReturnsConflictWhenMetadataAlreadyExists() throws Exception {
        when(songService.create(any(SongRequest.class)))
                .thenThrow(new SongMetadataAlreadyExistsException(1L));

        mockMvc.perform(postSong(VALID_REQUEST))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value("Metadata for resource ID=1 already exists"))
                .andExpect(jsonPath("$.errorCode").value("409"));
    }

    @Test
    void createRejectsMalformedDurationAndYear() throws Exception {
        SongRequest request = new SongRequest(1L, "Test Song", "Test Artist", "Test Album", "02:77", "01977");

        mockMvc.perform(postSong(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Validation error"))
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.details.duration")
                        .value("Duration must be in mm:ss format with leading zeros"))
                .andExpect(jsonPath("$.details.year").value("Year must be between 1900 and 2099"));
    }

    @Test
    void createRejectsMissingFields() throws Exception {
        mockMvc.perform(post("/songs").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.id").value("Song ID is required"))
                .andExpect(jsonPath("$.details.name").value("Song name is required"))
                .andExpect(jsonPath("$.details.artist").value("Artist name is required"))
                .andExpect(jsonPath("$.details.album").value("Album name is required"))
                .andExpect(jsonPath("$.details.duration").value("Duration is required"))
                .andExpect(jsonPath("$.details.year").value("Year is required"));
    }

    @Test
    void createRejectsEmptyTextFields() throws Exception {
        SongRequest request = new SongRequest(1L, "", "", "", "", "");

        mockMvc.perform(postSong(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.name").value("Song name must be between 1 and 100 characters"))
                .andExpect(jsonPath("$.details.artist").value("Artist name must be between 1 and 100 characters"))
                .andExpect(jsonPath("$.details.album").value("Album name must be between 1 and 100 characters"))
                .andExpect(jsonPath("$.details.duration")
                        .value("Duration must be in mm:ss format with leading zeros"))
                .andExpect(jsonPath("$.details.year").value("Year must be between 1900 and 2099"));
    }

    @Test
    void getByIdReturnsMetadata() throws Exception {
        when(songService.findById("1"))
                .thenReturn(new SongDto(1L, "Test Song", "Test Artist", "Test Album", "03:45", "2024"));

        mockMvc.perform(get("/songs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Song"))
                .andExpect(jsonPath("$.artist").value("Test Artist"))
                .andExpect(jsonPath("$.album").value("Test Album"))
                .andExpect(jsonPath("$.duration").value("03:45"))
                .andExpect(jsonPath("$.year").value("2024"));
    }

    @Test
    void getByIdReturnsNotFoundForUnknownId() throws Exception {
        when(songService.findById("99999")).thenThrow(new SongMetadataNotFoundException(99999L));

        mockMvc.perform(get("/songs/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Song metadata for ID=99999 not found"))
                .andExpect(jsonPath("$.errorCode").value("404"));
    }

    @Test
    void getByIdRejectsInvalidId() throws Exception {
        when(songService.findById("ABC"))
                .thenThrow(new InvalidIdException("Invalid value 'ABC' for ID. Must be a positive integer"));

        mockMvc.perform(get("/songs/ABC"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Invalid value 'ABC' for ID. Must be a positive integer"));
    }

    @Test
    void deleteReturnsRemovedIds() throws Exception {
        when(songService.deleteByIds("1,2")).thenReturn(List.of(1L, 2L));

        mockMvc.perform(delete("/songs").param("id", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", contains(1, 2)));
    }

    @Test
    void deleteRequiresIdParameter() throws Exception {
        mockMvc.perform(delete("/songs"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Required parameter 'id' is missing"));
    }

    private MockHttpServletRequestBuilder postSong(SongRequest request) throws JsonProcessingException {
        return post("/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
    }
}
