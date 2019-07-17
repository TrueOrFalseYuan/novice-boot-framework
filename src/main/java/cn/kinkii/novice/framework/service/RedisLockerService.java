package cn.kinkii.novice.framework.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class RedisLockerService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final static DefaultRedisScript<Long> LOCK_LUA_SCRIPT =
            new DefaultRedisScript<>(
                    "if redis.call('exists', KEYS[1]) == 0 then redis.call('set', KEYS[1], KEYS[2]) redis.call('pexpire', KEYS[1], KEYS[3]) return 1" +
                            " else return 0 end", Long.class);

    private final static DefaultRedisScript<Long> UNLOCK_LUA_SCRIPT =
            new DefaultRedisScript<>("if redis.call('exists', KEYS[1]) == 1 then redis.call('del', KEYS[1]) return 1 end", Long.class);

    private final static DefaultRedisScript<Long> ISLOCKED_LUA_SCRIPT =
            new DefaultRedisScript<>("if redis.call('exists', KEYS[1]) == 1 then return 1  else return 0 end", Long.class);

    public String getRedisKeyName(String... val) {
        return StringUtils.join(Arrays.asList(val), ":");
    }

    public Boolean lock(String key, String value, long timeout) {
        List<String> keys = Arrays.asList(key, value, String.valueOf(timeout));
        Long result = redisTemplate.execute(LOCK_LUA_SCRIPT, keys);
        return result == 1l;
    }

    public void unlock(String key) {
        List<String> keys = Arrays.asList(key);
        redisTemplate.execute(UNLOCK_LUA_SCRIPT, keys);
    }

    public Boolean isLocked(String key) {
        List<String> keys = Lists.newArrayList(key);
        Long result = redisTemplate.execute(ISLOCKED_LUA_SCRIPT, keys);
        return result == 1L;
    }
}
