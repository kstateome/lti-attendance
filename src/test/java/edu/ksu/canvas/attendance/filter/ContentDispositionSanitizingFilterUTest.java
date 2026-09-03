package edu.ksu.canvas.attendance.filter;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ContentDispositionSanitizingFilterUTest {

    @Test
    public void sanitizeContentDispositionRemovesPathTraversalFromFilename() throws Exception {
        assertSanitizedHeader(
                "Content-Disposition",
                "attachment; filename=\"../../evil.csv\"",
                "attachment; filename=\"_._evil.csv\"");
    }

    @Test
    public void sanitizeContentDispositionRemovesCrLfInjection() throws Exception {
        assertSanitizedHeader(
                "Content-Disposition",
                "attachment; filename=\"test\r\nX-Injected: yes\"",
                "attachment; filename=\"test_X-Injected: yes\"");
    }

    @Test
    public void sanitizeContentDispositionRemovesNullBytesFromFilename() throws Exception {
        assertSanitizedHeader(
                "Content-Disposition",
                "attachment; filename=\"test\u0000.csv\"",
                "attachment; filename=\"test_.csv\"");
    }

    @Test
    public void sanitizeContentDispositionHandlesQuoteSemicolonBreakout() throws Exception {
        assertSanitizedHeader(
                "Content-Disposition",
                "attachment; filename=\"test\"; evil=1",
                "attachment; filename=\"test\"; evil=1");
    }

    @Test
    public void sanitizeContentDispositionFallsBackToDownloadWhenFilenameIsEmptyAfterSanitization() throws Exception {
        assertSanitizedHeader(
                "Content-Disposition",
                "attachment; filename=\"////\"",
                "attachment; filename=\"download\"");
    }

    @Test
    public void sanitizeContentDispositionIsCaseInsensitiveForHeaderName() throws Exception {
        assertSanitizedHeader(
                "content-disposition",
                "attachment; filename=\"report.csv\"",
                "attachment; filename=\"report.csv\"");
    }

    @Test
    public void sanitizeContentDispositionPreservesValidFilename() throws Exception {
        assertSanitizedHeader(
                "Content-Disposition",
                "attachment; filename=\"report.csv\"",
                "attachment; filename=\"report.csv\"");
    }

    private void assertSanitizedHeader(String headerName, String originalValue, String expectedValue) throws Exception {
        ContentDispositionSanitizingFilter filter = new ContentDispositionSanitizingFilter();
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletRequest request = mock(ServletRequest.class);
        FilterChain chain = mock(FilterChain.class);

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) {
                HttpServletResponse wrappedResponse = (HttpServletResponse) invocation.getArguments()[1];
                wrappedResponse.setHeader(headerName, originalValue);
                return null;
            }
        }).when(chain).doFilter(any(ServletRequest.class), any(ServletResponse.class));

        filter.doFilter(request, response, chain);

        verify(response).setHeader(headerName, expectedValue);
    }
}
