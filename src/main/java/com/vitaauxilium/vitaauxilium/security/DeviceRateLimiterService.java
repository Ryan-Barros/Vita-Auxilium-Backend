package com.vitaauxilium.vitaauxilium.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeviceRateLimiterService {

    private final Map<UUID, Bucket> buckets = new ConcurrentHashMap<>();

    public boolean tryConsume(UUID deviceId) {
        Bucket bucket = buckets.computeIfAbsent(deviceId, id -> newBucket());
        return bucket.tryConsume(1);
    }

    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(20)
                .refillGreedy(15, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}
