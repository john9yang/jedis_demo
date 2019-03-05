package com.jhclass;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.concurrent.TimeUnit;

public class SimpleRateLimiter {
    private Jedis jedis;

    public SimpleRateLimiter(Jedis jedis){
          this.jedis = jedis;
    }

    public boolean isActionAllowed(String userId,String actionKey,int period,int maxCount){
        String key = String.format("hist:%s:%s",userId,actionKey);
        long nowTs = System.currentTimeMillis();
        System.out.println("now:"+nowTs);
        Pipeline pipe = jedis.pipelined();
        pipe.multi();
        pipe.zadd(key,nowTs,""+nowTs);
        pipe.zremrangeByScore(key,0,nowTs-period*1000);
        Response<Long> count = pipe.zcard(key);
        pipe.expire(key,period+1);
        pipe.exec();
        pipe.close();
        return count.get() <= maxCount;
    }

    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = new Jedis("localhost");
        SimpleRateLimiter limiter = new SimpleRateLimiter(jedis);
        for(int i=0;i<20;i++){
            TimeUnit.SECONDS.sleep(1l);
            System.out.println(limiter.isActionAllowed("laoqian","reply",60,5));
        }
    }
}
