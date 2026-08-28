package edu.ksu.canvas.attendance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Enable CSRF protection but exclude LTI launch endpoints which cannot include CSRF tokens
		RequestMatcher csrfRequestMatcher = new RequestMatcher() {
			private AntPathRequestMatcher[] requestMatchers = new AntPathRequestMatcher[] {
					new AntPathRequestMatcher("/launch"),
					new AntPathRequestMatcher("/launch/**"),
					new AntPathRequestMatcher("/lti/**")
			};

			@Override
			public boolean matches(HttpServletRequest request) {
				for (AntPathRequestMatcher rm : requestMatchers) {
					if (rm.matches(request)) {
						return false; // do NOT require CSRF for launch paths
					}
				}
				return true; // require CSRF for all other requests
			}
		};

		http
			.csrf()
				.requireCsrfProtectionMatcher(csrfRequestMatcher)
			.and()
			.authorizeRequests()
				.anyRequest().permitAll();
	}

}
