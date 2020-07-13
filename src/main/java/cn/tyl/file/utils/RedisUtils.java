package cn.tyl.file.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtils {

    @Autowired
    private StringRedisTemplate redisTemplate;


    public  void set(String key,String value){
        redisTemplate.opsForValue().set(key,value);

    }

    public  String get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public  void hset(String key,String field,Object value){
        redisTemplate.opsForHash().put(key,field,value);
    }

    public  Object hget(String key,String field){
        return redisTemplate.opsForHash().get(key,field);
    }
}
