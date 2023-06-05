package com.example.nonreldataproject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NonRelDataProjectApplication.class)
public class NonRelDataProjectApplicationTests {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void run(){
        redisTemplate.opsForValue().set("JZTSP000000034","12.0");
        redisTemplate.opsForValue().set("JZTSP000000033","88.0");
        redisTemplate.opsForValue().set("JZTSP000000032","12.0");
        redisTemplate.opsForValue().set("JZTSP000000031","365.0");
        redisTemplate.opsForValue().set("JZTSP000000030","269.0");
        redisTemplate.opsForValue().set("JZTSP000000029","529.0");
        redisTemplate.opsForValue().set("JZTSP000000028","72.0");
        redisTemplate.opsForValue().set("JZTSP000000027","36.0");
        redisTemplate.opsForValue().set("JZTSP000000026","343.0");
        redisTemplate.opsForValue().set("JZTSP000000025","18.0");
        redisTemplate.opsForValue().set("JZTSP000000024","1433.0");
        redisTemplate.opsForValue().set("JZTSP000000023","76.0");
        redisTemplate.opsForValue().set("JZTSP000000022","12.0");
        redisTemplate.opsForValue().set("JZTSP000000021","497.0");
        redisTemplate.opsForValue().set("JZTSP000000020","84.0");
        redisTemplate.opsForValue().set("JZTSP000000019","255.0");
        redisTemplate.opsForValue().set("JZTSP000000018","51.0");
        redisTemplate.opsForValue().set("JZTSP000000017","312.0");
        redisTemplate.opsForValue().set("JZTSP000000016","439.0");
        redisTemplate.opsForValue().set("JZTSP000000015","36.0");
        redisTemplate.opsForValue().set("JZTSP000000014","462.0");
        redisTemplate.opsForValue().set("JZTSP000000013","90.0");
        redisTemplate.opsForValue().set("JZTSP000000012","24.0");
        redisTemplate.opsForValue().set("JZTSP000000011","231.0");
        redisTemplate.opsForValue().set("JZTSP000000010","94.0");
        redisTemplate.opsForValue().set("JZTSP000000009","125.0");
        redisTemplate.opsForValue().set("JZTSP000000008","26.0");
        redisTemplate.opsForValue().set("JZTSP000000007","7.0");
        redisTemplate.opsForValue().set("JZTSP000000006","21.0");
        redisTemplate.opsForValue().set("JZTSP000000005","173.0");
        redisTemplate.opsForValue().set("JZTSP000000004","409.0");
        redisTemplate.opsForValue().set("JZTSP000000003","538.0");
        redisTemplate.opsForValue().set("JZTSP000000002","277.0");
        redisTemplate.opsForValue().set("JZTSP000000001","185.0");
        Object aaa = redisTemplate.opsForValue().get("JZTSP000000016");
//        redisTemplate.opsForValue().set("aaa","222");
//        Object aaa = redisTemplate.opsForValue().get("aaa");
        System.out.println(aaa);
    }


}
