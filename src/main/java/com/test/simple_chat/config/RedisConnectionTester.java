package com.test.simple_chat.config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisConnectionTester implements CommandLineRunner {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisConnectionTester(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            System.out.println("Testing Redis Connection...");
            redisTemplate.opsForValue().set("test_connection", "Hello Upstash!");
            String value = redisTemplate.opsForValue().get("test_connection");
            System.out.println("✅ SUCCESS! Connected to Redis. Value: " + value);
        } catch (Exception e) {
            System.err.println("❌ FAILED to connect to Redis!");
            System.err.println("REASON: " + e.getMessage());
            // This prints the hidden detail we need:
            e.printStackTrace();
        }
    }
}
