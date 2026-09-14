package com.epam.resourceservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class Mp3DurationTest {

    @ParameterizedTest
    @CsvSource({"7.31, 00:07", "0.4, 00:00", "59.5, 01:00", "179.0, 02:59", "3600, 60:00"})
    void formatsSecondsAsMinutesAndSeconds(String seconds, String expected) {
        assertThat(Mp3Duration.toMinutesAndSeconds(seconds)).isEqualTo(expected);
    }

    @Test
    void returnsNullWhenDurationIsUnknown() {
        assertThat(Mp3Duration.toMinutesAndSeconds(null)).isNull();
        assertThat(Mp3Duration.toMinutesAndSeconds(" ")).isNull();
    }
}
