package com.epam.resourceservice.service;

import java.util.concurrent.TimeUnit;

final class Mp3Duration {

    private Mp3Duration() {
    }

    /**
     * Converts a duration in seconds (as reported by Tika) to the mm:ss format.
     *
     * @return the formatted duration, or {@code null} when the duration is unknown
     */
    static String toMinutesAndSeconds(String durationInSeconds) {
        if (durationInSeconds == null || durationInSeconds.isBlank()) {
            return null;
        }
        long totalSeconds = Math.round(Double.parseDouble(durationInSeconds));
        long minutes = TimeUnit.SECONDS.toMinutes(totalSeconds);
        return "%02d:%02d".formatted(minutes, totalSeconds - TimeUnit.MINUTES.toSeconds(minutes));
    }
}
