package edu.ksu.canvas.attendance.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sanitizes Content-Disposition headers to reduce the attack surface for CVE-2020-5421.
 */
@Component
@Order(1)
public class ContentDispositionSanitizingFilter implements Filter {

    private static final Pattern FILENAME_PATTERN = Pattern.compile("(?i)(filename\\*?=)(\\\"?)([^\\\";]*)(\\\"?)");

    @Override
    public void init(FilterConfig filterConfig) {
        // No initialization needed.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        chain.doFilter(request, new HeaderSanitizingResponse(httpResponse));
    }

    @Override
    public void destroy() {
        // No cleanup required.
    }

    private static class HeaderSanitizingResponse extends HttpServletResponseWrapper {

        HeaderSanitizingResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setHeader(String name, String value) {
            super.setHeader(name, sanitizeHeader(name, value));
        }

        @Override
        public void addHeader(String name, String value) {
            super.addHeader(name, sanitizeHeader(name, value));
        }

        private String sanitizeHeader(String name, String value) {
            if (value == null || !"Content-Disposition".equalsIgnoreCase(name)) {
                return value;
            }
            return sanitizeContentDisposition(value);
        }

        private String sanitizeContentDisposition(String contentDisposition) {
            Matcher matcher = FILENAME_PATTERN.matcher(contentDisposition);
            StringBuffer replaced = new StringBuffer();
            while (matcher.find()) {
                String prefix = matcher.group(1);
                String quote = matcher.group(2);
                String filename = matcher.group(3);
                String sanitizedFilename = sanitizeFilename(filename);
                matcher.appendReplacement(replaced, prefix + quote + sanitizedFilename + quote);
            }
            matcher.appendTail(replaced);
            return replaced.toString();
        }

        private String sanitizeFilename(String filename) {
            if (filename == null) {
                return filename;
            }
            String sanitized = filename.replaceAll("[\\\\/\\r\\n\\t\\u0000\"<>|;]+", "_");
            sanitized = sanitized.replaceAll("\\.{2,}", ".");
            if (sanitized.startsWith(".")) {
                sanitized = sanitized.substring(1);
            }
            if (sanitized.isEmpty() || sanitized.matches("_+")) {
                sanitized = "download";
            }
            return sanitized;
        }
    }
}
