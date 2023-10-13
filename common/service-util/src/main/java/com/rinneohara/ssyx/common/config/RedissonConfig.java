package com.rinneohara.ssyx.common.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/10/8 15:59
 */
@Data
@Configuration
@ConfigurationProperties("spring.redis")
public class RedissonConfig {
    private String host="192.168.153.100";
    private String password=null;

    private String port="6379";

    private int timeout = 3000;
    private int connectionPoolSize = 64;
    private int connectionMinimumIdleSize=10;
    private int pingConnectionInterval = 60000;
    private static String ADDRESS_PREFIX = "redis://";

    /**
     * 自动装配
     *
     */
    @Bean
    RedissonClient redissonSingle() {
        Config config = new Config();
        //  判断redis 的host是否为空
        if(StringUtils.isEmpty(host)){
            throw new RuntimeException("host is  empty");
        }
        //  配置host，port等参数
        SingleServerConfig serverConfig = config.useSingleServer()
                //redis://127.0.0.1:7181
                .setAddress(ADDRESS_PREFIX + this.host + ":" + port)
                .setTimeout(this.timeout)
                .setPingConnectionInterval(pingConnectionInterval)
                .setConnectionPoolSize(this.connectionPoolSize)
                .setConnectionMinimumIdleSize(this.connectionMinimumIdleSize);
        //  判断进入redis 是否密码
        if(!StringUtils.isEmpty(this.password)) {
            serverConfig.setPassword(this.password);
        }
        // RedissonClient redisson = Redisson.create(config);
        return Redisson.create(config);
    }
}
