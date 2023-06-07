package com.example.demo.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class RatingsRepository {

    public static class MaxRatingsAddedException extends RuntimeException {
        public MaxRatingsAddedException() {
            super("Max ratings added");
        }
    }

    final StringRedisTemplate redisTemplate;

    public RatingsRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Map<Integer, Integer> findAll(String talkId) {
        return redisTemplate.opsForHash()
                .entries(toKey(talkId))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        it -> Integer.valueOf((String) it.getKey()),
                        it -> Integer.valueOf((String) it.getValue())
                ));
    }

    public void add(String talkId, int value) {
        String currentValue = (String) redisTemplate.opsForHash().get(toKey(talkId), value + "");
        if (currentValue != null && Long.valueOf(currentValue) >= Long.MAX_VALUE) {
            throw new MaxRatingsAddedException();
        }

        redisTemplate.opsForHash()
                .increment(toKey(talkId), value + "", 1);
    }

    public String toKey(String talkId) {
        return "ratings/" + talkId;
    }
}
