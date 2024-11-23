package com.example.aniwhere.infrastructure.jwt;

import com.example.aniwhere.application.cache.RedisService;
import com.example.aniwhere.domain.token.dto.JwtToken;
import com.example.aniwhere.domain.token.RefreshToken;
import com.example.aniwhere.domain.user.Role;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.infrastructure.persistence.RefreshTokenRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

	private final JwtProperties jwtProperties;
	private final RedisService redisService;
	private final RefreshTokenRepository refreshTokenRepository;

	public String generateAccessToken(User user) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + jwtProperties.getAccess_token_expiration_time());
		return Jwts.builder()
				.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
				.setIssuer(jwtProperties.getIssuer())
				.setIssuedAt(now)
				.setExpiration(expiry)
				.setSubject(user.getEmail())
				.claim("id", user.getId())
				.signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
				.compact();
	}

	public String generateRefreshToken(User user) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + jwtProperties.getRefresh_token_expiration_time());

		String refreshToken = Jwts.builder()
				.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
				.setIssuer(jwtProperties.getIssuer())
				.setIssuedAt(now)
				.setExpiration(expiry)
				.setSubject(user.getEmail())
				.claim("id", user.getId())
				.signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
				.compact();

		redisService.saveRefreshToken(user.getEmail(), refreshToken);

		RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
				.map(entity -> entity.update(refreshToken))
				.orElse(new RefreshToken(user.getId(), refreshToken));
		refreshTokenRepository.save(refreshTokenEntity);

		return refreshToken;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser()
					.setSigningKey(jwtProperties.getSecretKey())
					.parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.error("잘못된 JWT 서명입니다.", e);
		} catch (ExpiredJwtException e) {
			log.error("만료된 JWT 토큰입니다.", e);
		} catch (UnsupportedJwtException e) {
			log.error("지원되지 않는 JWT 토큰입니다.", e);
		} catch (IllegalArgumentException e) {
			log.error("JWT 토큰이 잘못되었습니다.", e);
		}
		return false;
	}

	public Authentication getAuthentication(String token) {
		Claims claims = getClaims(token);
		Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.getName()));
		return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities), token, authorities);
	}

	public JwtToken resolveToken(HttpServletRequest request) {
		String authorization = request.getHeader("Authorization");
		String refreshToken = request.getHeader("Refresh-Token");

		String accessToken = null;
		if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
			accessToken = authorization.substring(7);
		}

		return JwtToken.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	public Long getUserId(String token) {
		Claims claims = getClaims(token);
		return claims.get("id", Long.class);
	}

	public String getEmail(String token) {
		Claims claims = getClaims(token);
		return claims.getSubject();
	}

	public Claims getClaims(String token) {
		return Jwts.parser()
				.setSigningKey(jwtProperties.getSecretKey())
				.parseClaimsJws(token)
				.getBody();
	}
}