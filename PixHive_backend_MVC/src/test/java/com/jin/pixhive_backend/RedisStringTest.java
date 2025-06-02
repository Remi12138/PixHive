package com.jin.pixhive_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisStringTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void testRedisStringOperations() {
        // operand
        ValueOperations<String, String> valueOps = stringRedisTemplate.opsForValue();

        // Key & Value
        String key = "testKey";
        String value = "testValue";

        // 1. test create
        valueOps.set(key, value);
        String storedValue = valueOps.get(key);
        assertEquals(value, storedValue, "The stored value is not as expected");

        // 2. test update
        String updatedValue = "updatedValue";
        valueOps.set(key, updatedValue);
        storedValue = valueOps.get(key);
        assertEquals(updatedValue, storedValue, "The updated values do not match expectations");

        // 3. test read
        storedValue = valueOps.get(key);
        assertNotNull(storedValue, "The queried value is null");
        assertEquals(updatedValue, storedValue, "The queried value is inconsistent with the expected value");

        // 4. test delete
        stringRedisTemplate.delete(key);
        storedValue = valueOps.get(key);
        assertNull(storedValue, "The deleted value is not empty");
    }
}
