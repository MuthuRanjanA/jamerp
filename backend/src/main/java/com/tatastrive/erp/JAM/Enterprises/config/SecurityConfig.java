package com.tatastrive.erp.JAM.Enterprises.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthFilter;

	@Value("${app.cors.allowed-origins:https://*.vercel.app,https://*.netlify.app,https://jamerp.netlify.app,https://jamerpapplication.netlify.app,http://localhost:5173,http://localhost:3000,http://localhost:5174}")
	private String corsAllowedOrigins;

	@Bean
	public SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.authorizeHttpRequests(req -> req
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(
								"/api/auth/**")
						.permitAll()

						// Users Management
						.requestMatchers("/api/users/**")
						.hasAnyAuthority("ADMIN")

						// Employees
						.requestMatchers(HttpMethod.GET, "/employees/me")
						.hasAnyAuthority("EMPLOYEE", "MANAGER", "HR", "ADMIN")
						.requestMatchers(HttpMethod.GET, "/employees/team/**")
						.hasAnyAuthority("MANAGER", "HR", "ADMIN")
						.requestMatchers("/employees/**", "/employees")
						.hasAnyAuthority("HR", "ADMIN")

						// Departments
						.requestMatchers(HttpMethod.GET, "/departments/**", "/departments")
						.hasAnyAuthority("ADMIN", "HR", "MANAGER", "EMPLOYEE")
						.requestMatchers("/departments/**", "/departments")
						.hasAnyAuthority("ADMIN", "HR")

						// Attendance
						.requestMatchers(HttpMethod.POST, "/attendance/check-in/**", "/attendance/check-out/**")
						.hasAnyAuthority("ADMIN", "HR", "MANAGER", "EMPLOYEE")
						.requestMatchers(HttpMethod.GET, "/attendance/team/**")
						.hasAnyAuthority("MANAGER", "HR", "ADMIN")
						.requestMatchers(HttpMethod.GET, "/attendance", "/attendance/**")
						.hasAnyAuthority("ADMIN", "HR", "MANAGER", "EMPLOYEE")
						.requestMatchers("/attendance", "/attendance/**")
						.hasAnyAuthority("ADMIN", "HR")

						// Leave Requests
						.requestMatchers(HttpMethod.POST, "/leave")
						.hasAnyAuthority("ADMIN", "HR", "MANAGER", "EMPLOYEE")
						.requestMatchers(HttpMethod.GET, "/leave/employee/**")
						.hasAnyAuthority("ADMIN", "HR", "MANAGER", "EMPLOYEE")
						.requestMatchers(HttpMethod.GET, "/leave/team/**")
						.hasAnyAuthority("MANAGER", "HR", "ADMIN")
						.requestMatchers(HttpMethod.PUT, "/leave/approve/**", "/leave/reject/**")
						.hasAnyAuthority("MANAGER", "HR", "ADMIN")
						.requestMatchers(HttpMethod.GET, "/leave", "/leave/**")
						.hasAnyAuthority("ADMIN", "HR", "MANAGER", "EMPLOYEE")

						// Assets
						.requestMatchers(HttpMethod.GET, "/assets/me")
						.hasAnyAuthority("EMPLOYEE", "MANAGER", "HR", "ADMIN")
						.requestMatchers(HttpMethod.GET, "/asset", "/asset/**")
						.hasAnyAuthority("ADMIN", "HR", "MANAGER", "EMPLOYEE")
						.requestMatchers("/asset", "/asset/**")
						.hasAnyAuthority("ADMIN", "HR")

						// Projects
						.requestMatchers(HttpMethod.GET, "/projects", "/projects/**")
						.hasAnyAuthority("ADMIN", "HR", "MANAGER", "EMPLOYEE")
						.requestMatchers("/projects", "/projects/**")
						.hasAnyAuthority("ADMIN", "HR", "MANAGER")

						// Payroll
						.requestMatchers(HttpMethod.GET, "/payroll/employee/**", "/payroll/{id}")
						.hasAnyAuthority("ADMIN", "HR", "MANAGER", "EMPLOYEE")
						.requestMatchers("/payroll", "/payroll/**")
						.hasAnyAuthority("ADMIN", "HR")

						.anyRequest().authenticated())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

				.build();
	}

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager getAuthenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		List<String> origins = new ArrayList<>();
		if (corsAllowedOrigins != null && !corsAllowedOrigins.isBlank()) {
			origins.addAll(
					Arrays.stream(corsAllowedOrigins.split(","))
							.map(String::trim)
							.map(o -> o.endsWith("/") ? o.substring(0, o.length() - 1) : o)
							.filter(s -> !s.isEmpty())
							.collect(Collectors.toList())
			);
		}

		if (origins.isEmpty()) {
			origins.add("https://*.vercel.app");
			origins.add("https://*.netlify.app");
			origins.add("https://jamerp.netlify.app");
			origins.add("https://jamerpapplication.netlify.app");
			origins.add("http://localhost:5173");
			origins.add("http://localhost:5174");
			origins.add("http://localhost:3000");
		}

		config.setAllowedOriginPatterns(origins);
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
		config.setAllowedHeaders(List.of(
				"Authorization",
				"Content-Type",
				"Accept",
				"Origin",
				"X-Requested-With",
				"Access-Control-Request-Method",
				"Access-Control-Request-Headers"
		));
		config.setExposedHeaders(List.of(
				"Authorization",
				"Content-Disposition",
				"Access-Control-Allow-Origin",
				"Access-Control-Allow-Credentials"
		));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
