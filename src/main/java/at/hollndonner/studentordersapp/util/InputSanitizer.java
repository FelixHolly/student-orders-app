package at.hollndonner.studentordersapp.util;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

    /**
     * Escapes HTML entities to prevent XSS attacks.
     * Converts special characters like <, >, &, etc. to their HTML entity equivalents.
     *
     * @param input the input string to escape
     * @return string with HTML entities escaped
     */
    private String escapeHtml(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        return StringEscapeUtils.escapeHtml4(input);
    }

    /**
     * Sanitizes text input by trimming and escaping HTML entities.
     * Use this for general text fields where no HTML is expected.
     *
     * @param input the input string to sanitize
     * @return trimmed and sanitized string
     */
    public String sanitizeText(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.isBlank()) {
            return trimmed;
        }
        return escapeHtml(trimmed);
    }
}
