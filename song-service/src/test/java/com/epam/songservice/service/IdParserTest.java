package com.epam.songservice.service;

import com.epam.songservice.exception.InvalidIdException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdParserTest {

    private final IdParser idParser = new IdParser();

    @Test
    void parsesPositiveId() {
        assertThat(idParser.parseId("42")).isEqualTo(42L);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC", "1.1", "-1", "0", "", " 1", "99999999999999999999"})
    void rejectsIdThatIsNotAPositiveInteger(String rawId) {
        assertThatThrownBy(() -> idParser.parseId(rawId))
                .isInstanceOf(InvalidIdException.class)
                .hasMessage("Invalid value '%s' for ID. Must be a positive integer".formatted(rawId));
    }

    @Test
    void parsesCsvIgnoringDuplicates() {
        assertThat(idParser.parseCsv("1,2,1,3")).containsExactly(1L, 2L, 3L);
    }

    @Test
    void rejectsCsvWithNonNumericElement() {
        assertThatThrownBy(() -> idParser.parseCsv("1,2,3,4,V"))
                .isInstanceOf(InvalidIdException.class)
                .hasMessage("Invalid ID format: 'V'. Only positive integers are allowed");
    }

    @Test
    void rejectsCsvLongerThanMaxLength() {
        String csv = String.join(",", List.of("2147483647", "2147483646", "2147483645", "2147483644", "2147483643",
                "2147483642", "2147483641", "2147483640", "2147483639", "2147483638", "2147483637", "2147483636",
                "2147483635", "2147483634", "2147483633", "2147483632", "2147483631", "2147483630", "2147483629"));

        assertThatThrownBy(() -> idParser.parseCsv(csv))
                .isInstanceOf(InvalidIdException.class)
                .hasMessage("CSV string is too long: received 208 characters, maximum allowed is 200");
    }
}
