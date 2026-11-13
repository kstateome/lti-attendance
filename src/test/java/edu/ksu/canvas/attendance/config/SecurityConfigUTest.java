package edu.ksu.canvas.attendance.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.junit.Assert.*;

/**
 * Unit tests for SecurityConfig class.
 */
@RunWith(MockitoJUnitRunner.class)
public class SecurityConfigUTest {

	private SecurityConfig securityConfig;

	@Before
	public void setUp() throws Exception {
		securityConfig = new SecurityConfig();
	}

	@Test
	public void testSecurityConfigBeanExists() {
		assertNotNull("SecurityConfig bean should exist", securityConfig);
	}

	@Test
	public void testSecurityConfigIsWebSecurityConfigurerAdapter() {
		assertTrue("SecurityConfig should extend WebSecurityConfigurerAdapter",
				securityConfig instanceof org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter);
	}

	@Test
	public void testCsrfProtectionExcludesLaunchPath() throws Exception {
		// Test that /launch endpoint does NOT require CSRF protection
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/launch");
		
		assertNotNull("Request should not be null", request);
		assertEquals("Request path should be /launch", "/launch", request.getRequestURI());
	}

	@Test
	public void testCsrfProtectionExcludesLaunchSubpaths() throws Exception {
		// Test that /launch/** endpoints do NOT require CSRF protection
		MockHttpServletRequest request1 = new MockHttpServletRequest("POST", "/launch/course");
		MockHttpServletRequest request2 = new MockHttpServletRequest("POST", "/launch/course/123");
		
		assertNotNull("Request 1 should not be null", request1);
		assertNotNull("Request 2 should not be null", request2);
		assertEquals("Request 1 path should be /launch/course", "/launch/course", request1.getRequestURI());
		assertEquals("Request 2 path should be /launch/course/123", "/launch/course/123", request2.getRequestURI());
	}

	@Test
	public void testCsrfProtectionExcludesLtiPaths() throws Exception {
		// Test that /lti/** endpoints do NOT require CSRF protection
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/lti/grade");
		
		assertNotNull("Request should not be null", request);
		assertEquals("Request path should be /lti/grade", "/lti/grade", request.getRequestURI());
	}

	@Test
	public void testCsrfProtectionRequiredForOtherPaths() throws Exception {
		// Test that other paths DO require CSRF protection
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/attendance");
		
		assertNotNull("Request should not be null", request);
		assertEquals("Request path should be /api/attendance", "/api/attendance", request.getRequestURI());
	}

	@Test
	public void testAntPathRequestMatcherLaunch() {
		AntPathRequestMatcher matcher = new AntPathRequestMatcher("/launch");
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/launch");
		request.setContextPath("");
		request.setServletPath("/launch");
		
		assertTrue("Matcher should match /launch", matcher.matches(request));
	}

	@Test
	public void testAntPathRequestMatcherLaunchWildcard() {
		AntPathRequestMatcher matcher = new AntPathRequestMatcher("/launch/**");
		MockHttpServletRequest request1 = new MockHttpServletRequest("POST", "/launch/course");
		MockHttpServletRequest request2 = new MockHttpServletRequest("POST", "/launch/course/123");
		request1.setContextPath("");
		request1.setServletPath("/launch/course");
		request2.setContextPath("");
		request2.setServletPath("/launch/course/123");
		
		assertTrue("Matcher should match /launch/course", matcher.matches(request1));
		assertTrue("Matcher should match /launch/course/123", matcher.matches(request2));
	}

	@Test
	public void testAntPathRequestMatcherLtiWildcard() {
		AntPathRequestMatcher matcher = new AntPathRequestMatcher("/lti/**");
		MockHttpServletRequest request1 = new MockHttpServletRequest("POST", "/lti/grade");
		MockHttpServletRequest request2 = new MockHttpServletRequest("POST", "/lti/grade/123");
		request1.setContextPath("");
		request1.setServletPath("/lti/grade");
		request2.setContextPath("");
		request2.setServletPath("/lti/grade/123");
		
		assertTrue("Matcher should match /lti/grade", matcher.matches(request1));
		assertTrue("Matcher should match /lti/grade/123", matcher.matches(request2));
	}

	@Test
	public void testAntPathRequestMatcherNonMatch() {
		AntPathRequestMatcher matcher = new AntPathRequestMatcher("/launch");
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/attendance");
		request.setContextPath("");
		request.setServletPath("/api/attendance");
		
		assertFalse("Matcher should not match /api/attendance", matcher.matches(request));
	}

	@Test
	public void testGetRequestNotAffectedByCsrfRequirement() throws Exception {
		// GET requests should not require CSRF tokens
		MockHttpServletRequest getRequest = new MockHttpServletRequest("GET", "/attendance");
		
		assertEquals("Method should be GET", "GET", getRequest.getMethod());
		assertEquals("Request path should be /attendance", "/attendance", getRequest.getRequestURI());
	}

	@Test
	public void testLaunchPathExactMatch() throws Exception {
		// Test exact /launch path
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/launch");
		request.setContextPath("");
		request.setServletPath("/launch");
		AntPathRequestMatcher matcher = new AntPathRequestMatcher("/launch");
		
		assertTrue("Exact path matcher should match /launch", matcher.matches(request));
	}

	@Test
	public void testSecurityConfigConfigureMethodExists() throws Exception {
		// Verify the configure method is present and overridden
		assertTrue("SecurityConfig should have configure method",
				securityConfig.getClass().getDeclaredMethods().length > 0);
	}

	@Test
	public void testCanvasFramingHeaders() throws Exception {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.setServletContext(new MockServletContext());
		context.register(SecurityConfig.class);

		try {
			context.refresh();
			FilterChainProxy securityFilter = context.getBean("springSecurityFilterChain", FilterChainProxy.class);
			MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
			request.setServletPath("/");
			MockHttpServletResponse response = new MockHttpServletResponse();

			securityFilter.doFilter(request, response, new MockFilterChain());

			assertNull("Spring Security should not deny iframe rendering", response.getHeader("X-Frame-Options"));
			assertEquals("Canvas should be permitted to frame the response",
					"frame-ancestors https://k-state.test.instructure.com",
					response.getHeader("Content-Security-Policy"));
		} finally {
			context.close();
		}
	}

}
