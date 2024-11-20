package com.example.aniwhere.application.config;

import com.example.aniwhere.service.user.PrincipalOAuthDetailsService;
import com.example.aniwhere.application.jwt.JwtTokenFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

	private final JwtTokenFilter jwtTokenFilter;
	private final PrincipalOAuthDetailsService principalOAuthDetailsService;
	private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
	private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(AbstractHttpConfigurer::disable)
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.sessionManagement(c ->
						c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
							 new AntPathRequestMatcher("/"),
							 new AntPathRequestMatcher("/favicon.ico"),
							 new AntPathRequestMatcher("/oauth2/authorization/kakao"),
							 new AntPathRequestMatcher("/api/v1/oauth2/**"),
							 new AntPathRequestMatcher("/login/**"),
							 new AntPathRequestMatcher("/api/token"),
							 new AntPathRequestMatcher("/api/auth/**"),
						     new AntPathRequestMatcher("/api/auth/email/**"),
					 		 new AntPathRequestMatcher("/swagger-ui/**"),
							 new AntPathRequestMatcher("/v3/api-docs/**"),
							 new AntPathRequestMatcher("/api/kakaoreissue")
						).permitAll()
						.anyRequest().authenticated()
				)
				.oauth2Login(oauth2 -> oauth2
						.userInfoEndpoint(userInfo -> userInfo
								.userService(principalOAuthDetailsService))
						.successHandler(customOAuth2SuccessHandler)
						.failureHandler(customOAuth2FailureHandler))
				.exceptionHandling(e -> e
						.authenticationEntryPoint(customAuthenticationEntryPoint)
						.accessDeniedHandler(customAccessDeniedHandler))
				.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	public OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository authorizedClientRepository) {

		OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
																.authorizationCode()
																.refreshToken()
																.clientCredentials()
																.password()
																.build();

		DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
		return authorizedClientManager;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
