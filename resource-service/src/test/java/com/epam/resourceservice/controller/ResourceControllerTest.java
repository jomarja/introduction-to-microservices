package com.epam.resourceservice.controller;

import com.epam.resourceservice.Mp3;
import com.epam.resourceservice.exception.InvalidIdException;
import com.epam.resourceservice.exception.ResourceNotFoundException;
import com.epam.resourceservice.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
class ResourceControllerTest {

    private static final byte[] AUDIO_DATA = {1, 2, 3};

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceService resourceService;

    @Test
    void uploadReturnsCreatedResourceId() throws Exception {
        when(resourceService.upload(any())).thenReturn(1L);

        mockMvc.perform(post("/resources").contentType(Mp3.MIME_TYPE).content(AUDIO_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void uploadRejectsNonMp3ContentType() throws Exception {
        mockMvc.perform(post("/resources").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Invalid file format: application/json. Only MP3 files are allowed"))
                .andExpect(jsonPath("$.errorCode").value("400"));
    }

    @Test
    void downloadReturnsAudioData() throws Exception {
        when(resourceService.download("1")).thenReturn(AUDIO_DATA);

        mockMvc.perform(get("/resources/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(Mp3.MIME_TYPE))
                .andExpect(content().bytes(AUDIO_DATA));
    }

    @Test
    void downloadReturnsNotFoundForUnknownId() throws Exception {
        when(resourceService.download("99999")).thenThrow(new ResourceNotFoundException(99999L));

        mockMvc.perform(get("/resources/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Resource with ID=99999 not found"))
                .andExpect(jsonPath("$.errorCode").value("404"));
    }

    @Test
    void downloadRejectsInvalidId() throws Exception {
        when(resourceService.download("ABC"))
                .thenThrow(new InvalidIdException("Invalid value 'ABC' for ID. Must be a positive integer"));

        mockMvc.perform(get("/resources/ABC"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("Invalid value 'ABC' for ID. Must be a positive integer"));
    }

    @Test
    void deleteReturnsRemovedIds() throws Exception {
        when(resourceService.deleteByIds("1,2")).thenReturn(List.of(1L, 2L));

        mockMvc.perform(delete("/resources").param("id", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ids", contains(1, 2)));
    }

    @Test
    void deleteRequiresIdParameter() throws Exception {
        mockMvc.perform(delete("/resources"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Required parameter 'id' is missing"));
    }
}
