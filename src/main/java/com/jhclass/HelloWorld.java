package com.jhclass;

import redis.clients.jedis.Jedis;

public class HelloWorld {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost");
        String name = jedis.get("who");
        System.out.println("name:"+name);
    }
}
