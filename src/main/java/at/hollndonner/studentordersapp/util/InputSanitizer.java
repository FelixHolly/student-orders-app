package at.hollndonner.studentordersapp.util;

import org.apache.commons.text.StringEscapeUtils;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    /**
     * Sanitizes HTML content to prevent XSS attacks.
     * Allows basic formatting and links but removes potentially dangerous tags.
     *
     * @param input the input string to sanitize
     * @return sanitized string with HTML tags escaped or removed
     */
    public String sanitizeHtml(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        return POLICY.sanitize(input);
    }

    /**
     * Escapes HTML entities to prevent XSS attacks.
     * Converts special characters like <, >, &, etc. to their HTML entity equivalents.
     *
     * @param input the input string to escape
     * @return string with HTML entities escaped
     */
    public String escapeHtml(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        return StringEscapeUtils.escapeHtml4(input);
    }

    /**
     * Removes potentially dangerous characters and scripts from input.
     * This is a stricter sanitization that removes all HTML and special characters.
     *
     * @param input the input string to sanitize
     * @return sanitized string with all HTML removed
     */
    public String sanitizeStrict(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        // Remove all HTML tags
        String sanitized = input.replaceAll("<[^>]*>", "");
        // Escape remaining HTML entities
        return StringEscapeUtils.escapeHtml4(sanitized);
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
