package com.backend.server.config.security;

import com.backend.server.model.entity.PostgresqlRefreshToken;
import com.backend.server.model.repository.user.PostgresqlRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
@Profile("postgre")
@Component
@RequiredArgsConstructor
public class PostgresqlKeyValueTemplate implements RedisPostgresTemplate{
    private final PostgresqlRefreshTokenRepository refreshTokenRepository;
    @Override
    public void set(String key, String value, Duration timeToLive) {
        long expiresAt = System.currentTimeMillis() + timeToLive.toMillis();
        PostgresqlRefreshToken refreshToken = refreshTokenRepository.findByKey(key).orElse(new PostgresqlRefreshToken());
        refreshToken.setKey(key);
        refreshToken.setValue(value);
        refreshToken.setExpiresAt(expiresAt);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public String get(String key) {
        return refreshTokenRepository.findByKey(key)
                .filter(e -> e.getExpiresAt() == null || e.getExpiresAt() > System.currentTimeMillis())
                .map(PostgresqlRefreshToken::getValue)
                .orElse(null);
    }

    @Override
    public void delete(String key) {
        refreshTokenRepository.deleteByKey(key);
    }
}
