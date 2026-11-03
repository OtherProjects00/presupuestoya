package com.presupuestoya.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginAttemptService {

    private final int maxAttempts;
    private final Duration blockDuration;
    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public LoginAttemptService(
            @Value("${app.security.login-max-attempts:5}") int maxAttempts,
            @Value("${app.security.login-block-minutes:15}") long blockMinutes) {
        this.maxAttempts = Math.max(3, maxAttempts);
        this.blockDuration = Duration.ofMinutes(Math.max(1, blockMinutes));
    }

    public boolean isBlocked(String key) {
        Attempt attempt = attempts.get(key);
        if (attempt == null) {
            return false;
        }
        if (attempt.blockedUntil != null && Instant.now().isBefore(attempt.blockedUntil)) {
            return true;
        }
        if (attempt.blockedUntil != null) {
            attempts.remove(key);
        }
        return false;
    }

    public void loginFailed(String key) {
        Instant now = Instant.now();
        attempts.compute(key, (ignored, current) -> {
            if (current == null || current.firstAttempt.isBefore(now.minus(blockDuration))) {
                return new Attempt(now, 1, null);
            }
            current.count++;
            if (current.count >= maxAttempts) {
                current.blockedUntil = now.plus(blockDuration);
            }
            return current;
        });
        cleanup();
    }

    public void loginSucceeded(String key) {
        attempts.remove(key);
    }

    private void cleanup() {
        if (attempts.size() < 5_000) {
            return;
        }
        Instant cutoff = Instant.now().minus(blockDuration.multipliedBy(2));
        attempts.entrySet().removeIf(entry -> entry.getValue().firstAttempt.isBefore(cutoff));
    }

    private static final class Attempt {
        private final Instant firstAttempt;
        private int count;
        private Instant blockedUntil;

        private Attempt(Instant firstAttempt, int count, Instant blockedUntil) {
            this.firstAttempt = firstAttempt;
            this.count = count;
            this.blockedUntil = blockedUntil;
        }
    }
}
