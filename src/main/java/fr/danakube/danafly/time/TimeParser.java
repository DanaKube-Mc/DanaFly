package fr.danakube.danafly.time;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeParser {

    private static final Pattern TIME_UNIT_PATTERN = Pattern.compile("(?i)(\\d+(?:\\.\\d+)?)\\s*([dhmsj])");

    /**
     * Parses a string representation of time into total seconds.
     * Supports:
     * - "-1", "infinite", "unlimited", "illimité" -> -1.0
     * - "3h", "180m", "10800s", "1d", "1j"
     * - Combinations: "1d 2h 30m 10s"
     * - Raw numbers: "10800"
     *
     * @param input String duration
     * @return Duration in seconds, or -1.0 for infinite, or 0.0 if empty/invalid
     */
    public static double parseToSeconds(String input) {
        if (input == null) {
            return 0.0;
        }
        String trimmed = input.trim().toLowerCase();
        if (trimmed.isEmpty()) {
            return 0.0;
        }

        if (trimmed.equals("-1") || trimmed.equals("infinite") || trimmed.equals("unlimited") || trimmed.equals("illimité") || trimmed.equals("illimite")) {
            return -1.0;
        }

        Matcher matcher = TIME_UNIT_PATTERN.matcher(trimmed);
        double totalSeconds = 0.0;
        boolean matched = false;

        while (matcher.find()) {
            matched = true;
            double value = Double.parseDouble(matcher.group(1));
            char unit = matcher.group(2).toLowerCase().charAt(0);
            switch (unit) {
                case 'd':
                case 'j': // jours
                    totalSeconds += value * 86400.0;
                    break;
                case 'h':
                    totalSeconds += value * 3600.0;
                    break;
                case 'm':
                    totalSeconds += value * 60.0;
                    break;
                case 's':
                    totalSeconds += value;
                    break;
                default:
                    break;
            }
        }

        if (matched) {
            return totalSeconds;
        }

        // Fallback to pure numeric parsing
        try {
            return Double.parseDouble(trimmed);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Formats seconds into a human-readable duration string (e.g. "3h", "2h 15m", "45s", or "Illimité").
     */
    public static String formatDuration(double seconds) {
        if (seconds < 0) {
            return "Illimité";
        }
        long sec = (long) Math.ceil(seconds);
        if (sec == 0) {
            return "0s";
        }

        long days = sec / 86400;
        long hours = (sec % 86400) / 3600;
        long minutes = (sec % 3600) / 60;
        long s = sec % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("j ");
        }
        if (hours > 0) {
            sb.append(hours).append("h ");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m ");
        }
        if (s > 0 || sb.isEmpty()) {
            sb.append(s).append("s");
        }
        return sb.toString().trim();
    }
}
