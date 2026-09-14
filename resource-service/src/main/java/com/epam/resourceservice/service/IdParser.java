package com.epam.resourceservice.service;

import com.epam.resourceservice.exception.InvalidIdException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Parses IDs coming from the API: a single path variable or a comma-separated list.
 */
@Component
public class IdParser {

    private static final int MAX_CSV_LENGTH = 200;
    private static final Pattern DIGITS = Pattern.compile("\\d{1,18}");

    public long parseId(String value) {
        return toPositiveLong(value)
                .orElseThrow(() -> new InvalidIdException(
                        "Invalid value '%s' for ID. Must be a positive integer".formatted(value)));
    }

    public List<Long> parseCsv(String csv) {
        if (csv.length() > MAX_CSV_LENGTH) {
            throw new InvalidIdException("CSV string is too long: received %d characters, maximum allowed is %d"
                    .formatted(csv.length(), MAX_CSV_LENGTH));
        }
        return Arrays.stream(csv.split(",", -1))
                .map(this::parseCsvElement)
                .distinct()
                .toList();
    }

    private long parseCsvElement(String value) {
        return toPositiveLong(value)
                .orElseThrow(() -> new InvalidIdException(
                        "Invalid ID format: '%s'. Only positive integers are allowed".formatted(value)));
    }

    private Optional<Long> toPositiveLong(String value) {
        if (value == null || !DIGITS.matcher(value).matches()) {
            return Optional.empty();
        }
        long id = Long.parseLong(value);
        return id > 0 ? Optional.of(id) : Optional.empty();
    }
}
