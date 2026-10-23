package fr.danakube.danafly.time;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeParserTest {

    @Test
    @DisplayName("Should parse single unit durations correctly")
    void shouldParseSingleUnitDurations() {
        assertThat(TimeParser.parseToSeconds("3h")).isEqualTo(10800.0);
        assertThat(TimeParser.parseToSeconds("180m")).isEqualTo(10800.0);
        assertThat(TimeParser.parseToSeconds("10800s")).isEqualTo(10800.0);
        assertThat(TimeParser.parseToSeconds("1d")).isEqualTo(86400.0);
        assertThat(TimeParser.parseToSeconds("1j")).isEqualTo(86400.0);
    }

    @Test
    @DisplayName("Should parse combined duration strings")
    void shouldParseCombinedDurations() {
        assertThat(TimeParser.parseToSeconds("1d 2h 30m 15s")).isEqualTo(86400.0 + 7200.0 + 1800.0 + 15.0);
        assertThat(TimeParser.parseToSeconds("2h30m")).isEqualTo(7200.0 + 1800.0);
    }

    @Test
    @DisplayName("Should parse unlimited keywords and -1 as -1.0")
    void shouldParseUnlimited() {
        assertThat(TimeParser.parseToSeconds("-1")).isEqualTo(-1.0);
        assertThat(TimeParser.parseToSeconds("infinite")).isEqualTo(-1.0);
        assertThat(TimeParser.parseToSeconds("unlimited")).isEqualTo(-1.0);
        assertThat(TimeParser.parseToSeconds("illimité")).isEqualTo(-1.0);
        assertThat(TimeParser.parseToSeconds("illimite")).isEqualTo(-1.0);
    }

    @Test
    @DisplayName("Should parse raw numeric strings as seconds")
    void shouldParseRawNumbers() {
        assertThat(TimeParser.parseToSeconds("3600")).isEqualTo(3600.0);
        assertThat(TimeParser.parseToSeconds("123.5")).isEqualTo(123.5);
    }

    @Test
    @DisplayName("Should handle empty or null gracefully")
    void shouldHandleEmptyOrNull() {
        assertThat(TimeParser.parseToSeconds(null)).isEqualTo(0.0);
        assertThat(TimeParser.parseToSeconds("")).isEqualTo(0.0);
        assertThat(TimeParser.parseToSeconds("   ")).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should format seconds into readable durations")
    void shouldFormatDurations() {
        assertThat(TimeParser.formatDuration(-1)).isEqualTo("Illimité");
        assertThat(TimeParser.formatDuration(10800)).isEqualTo("3h");
        assertThat(TimeParser.formatDuration(90)).isEqualTo("1m 30s");
        assertThat(TimeParser.formatDuration(0)).isEqualTo("0s");
    }
}
