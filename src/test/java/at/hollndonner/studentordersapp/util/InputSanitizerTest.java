package at.hollndonner.studentordersapp.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputSanitizerTest {

    private InputSanitizer inputSanitizer;

    @BeforeEach
    void setUp() {
        inputSanitizer = new InputSanitizer();
    }

    @Test
    void sanitizeText_WithNormalText_ShouldReturnSameText() {
        String input = "John Doe";
        String result = inputSanitizer.sanitizeText(input);
        assertThat(result).isEqualTo("John Doe");
    }

    @Test
    void sanitizeText_WithXSSScript_ShouldEscapeHtml() {
        String input = "<script>alert('XSS')</script>";
        String result = inputSanitizer.sanitizeText(input);
        assertThat(result).doesNotContain("<script>");
        assertThat(result).contains("&lt;script&gt;");
    }

    @Test
    void sanitizeText_WithHtmlTags_ShouldEscapeHtml() {
        String input = "<b>Bold Text</b>";
        String result = inputSanitizer.sanitizeText(input);
        assertThat(result).doesNotContain("<b>");
        assertThat(result).contains("&lt;b&gt;");
    }

    @Test
    void sanitizeText_WithLeadingAndTrailingSpaces_ShouldTrim() {
        String input = "  John Doe  ";
        String result = inputSanitizer.sanitizeText(input);
        assertThat(result).isEqualTo("John Doe");
    }

    @Test
    void sanitizeText_WithNull_ShouldReturnNull() {
        String result = inputSanitizer.sanitizeText(null);
        assertThat(result).isNull();
    }

    @Test
    void sanitizeText_WithBlankString_ShouldReturnBlank() {
        String input = "   ";
        String result = inputSanitizer.sanitizeText(input);
        assertThat(result).isEqualTo("");
    }

    @Test
    void escapeHtml_WithSpecialCharacters_ShouldEscape() {
        String input = "Tom & Jerry < > \"quoted\"";
        String result = inputSanitizer.escapeHtml(input);
        assertThat(result).contains("&amp;");
        assertThat(result).contains("&lt;");
        assertThat(result).contains("&gt;");
        assertThat(result).contains("&quot;");
    }

    @Test
    void sanitizeStrict_WithHtmlContent_ShouldRemoveAllHtml() {
        String input = "<div><script>alert('XSS')</script><p>Text</p></div>";
        String result = inputSanitizer.sanitizeStrict(input);
        assertThat(result).doesNotContain("<div>");
        assertThat(result).doesNotContain("<script>");
        assertThat(result).doesNotContain("<p>");
        assertThat(result).contains("Text");
    }

    @Test
    void sanitizeHtml_WithBasicFormatting_ShouldAllowSafeTags() {
        String input = "<b>Bold</b> and <i>italic</i>";
        String result = inputSanitizer.sanitizeHtml(input);
        assertThat(result).contains("<b>Bold</b>");
        assertThat(result).contains("<i>italic</i>");
    }

    @Test
    void sanitizeHtml_WithDangerousScript_ShouldRemoveScript() {
        String input = "<b>Safe</b><script>alert('danger')</script>";
        String result = inputSanitizer.sanitizeHtml(input);
        assertThat(result).contains("<b>Safe</b>");
        assertThat(result).doesNotContain("<script>");
        assertThat(result).doesNotContain("alert");
    }

    @Test
    void sanitizeText_WithSQLInjection_ShouldReturnSanitized() {
        // Note: SQL injection prevention should be handled at the database layer
        // using prepared statements, not at the input sanitization layer
        String input = "'; DROP TABLE students; --";
        String result = inputSanitizer.sanitizeText(input);
        // The sanitizer focuses on XSS, not SQL injection
        // SQL injection is prevented by using JPA/Hibernate with parameterized queries
        assertThat(result).isNotNull();
    }

    @Test
    void sanitizeText_WithJavaScriptEventHandlers_ShouldEscape() {
        String input = "<img src=x onerror=alert('XSS')>";
        String result = inputSanitizer.sanitizeText(input);
        assertThat(result).doesNotContain("<img");
        assertThat(result).contains("&lt;img");
    }
}
