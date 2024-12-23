package com.example.aniwhere.application.auth.jwt.provider;

import com.example.aniwhere.application.auth.jwt.JwtProperties;
import com.example.aniwhere.application.auth.jwt.dto.Claims;
import com.example.aniwhere.application.auth.jwt.dto.CreateTokenCommand;
import com.example.aniwhere.global.error.exception.TokenException;
import com.example.aniwhere.service.redis.RedisService;
import com.example.aniwhere.domain.token.RefreshToken;
import com.example.aniwhere.domain.user.Role;
import com.example.aniwhere.domain.user.User;
import com.example.aniwhere.repository.token.RefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;
import java.util.List;

import static com.example.aniwhere.global.error.ErrorCode.INVALID_TOKEN;

@Slf4j
@Component
public class TokenProvider {

	private static final String USER_ID = "userId";
	private static final String ROLE = "role";

	private final JwtProperties jwtProperties;
	private final RedisService redisService;
	private final RefreshTokenRepository refreshTokenRepository;

	private final Algorithm algorithm;
	private final JWTVerifier jwtVerifier;

	public TokenProvider(JwtProperties jwtProperties, RedisService redisService, RefreshTokenRepository refreshTokenRepository) {
		this.jwtProperties = jwtProperties;
		this.redisService = redisService;
		this.refreshTokenRepository = refreshTokenRepository;
		this.algorithm = Algorithm.HMAC512(jwtProperties.getSecretKey());
		this.jwtVerifier = JWT.require(algorithm)
				.withIssuer(jwtProperties.getIssuer())
				.build();
	}

	public String generateAccessToken(final CreateTokenCommand command) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + jwtProperties.getAccess_token_expiration_time());
		return JWT.create()
				.withIssuer(jwtProperties.getIssuer())
				.withIssuedAt(now)
				.withExpiresAt(expiry)
				.withClaim(USER_ID, command.userId())
				.withClaim(ROLE, command.role().getValue())
				.sign(algorithm);
	}

	public String generateRefreshToken(final CreateTokenCommand command, final User user) {
		Date now = new Date();
		Date expiry = new Date(now.getTime() + jwtProperties.getRefresh_token_expiration_time());

		String refreshToken = JWT.create()
				.withIssuer(jwtProperties.getIssuer())
				.withIssuedAt(now)
				.withExpiresAt(expiry)
				.withClaim(USER_ID, command.userId())
				.withClaim(ROLE, command.role().getValue())
				.sign(algorithm);

		redisService.saveRefreshToken(user.getEmail(), refreshToken);

		RefreshToken refreshTokenEntity = refreshTokenRepository.findByUserId(user.getId())
				.map(entity -> entity.update(refreshToken))
				.orElse(new RefreshToken(user.getId(), refreshToken));
		refreshTokenRepository.save(refreshTokenEntity);

		return refreshToken;
	}

	public Claims validateToken(final String accessToken) {
		try {
			DecodedJWT decodedJWT = jwtVerifier.verify(accessToken);
			Long userId = getUserId(decodedJWT);
			List<String> authorities = getAuthorities(decodedJWT);
			return new Claims(userId, authorities);
		} catch (AlgorithmMismatchException ex) {
			log.info("AlgorithmMismatchException: 토큰의 알고리즘이 유효하지 않습니다.");
		} catch (SignatureVerificationException ex) {
			log.info("SignatureVerificationException: 토큰의 서명이 유효하지 않습니다.");
		} catch (TokenExpiredException ex) {
			log.info("TokenExpiredException: 토큰이 만료되었습니다.");
		} catch (MissingClaimException ex) {
			log.info("MissingClaimException: 유효값이 클레임이 포함되어 있지 않습니다.");
		} catch (JWTVerificationException ex) {
			log.info("JWTVerificationException: 유효하지 않은 토큰입니다.");
		}
		throw new TokenException(INVALID_TOKEN);
	}

	private Long getUserId(final DecodedJWT decodedJWT) {
		Claim claim = decodedJWT.getClaim(USER_ID);
		if (!claim.isNull()) {
			return claim.asLong();
		}
		throw new MissingClaimException(USER_ID);
	}

	private List<String> getAuthorities(final DecodedJWT decodedJWT) {
		Claim claim = decodedJWT.getClaim(ROLE);
		if (!claim.isNull()) {
			String role = claim.asString();
			return Role.valueOf(role).getAuthorities();
		}
		throw new MissingClaimException(ROLE);
	}
}