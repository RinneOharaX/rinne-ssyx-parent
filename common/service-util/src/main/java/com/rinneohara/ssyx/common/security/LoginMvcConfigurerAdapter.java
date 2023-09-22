package com.rinneohara.ssyx.common.security;

import com.rinneohara.ssyx.common.auth.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;
/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/7 15:26
 */
@Configuration
public class LoginMvcConfigurerAdapter extends WebMvcConfigurationSupport {

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加自定义拦截器，设置路径
        registry.addInterceptor(
                        new UserInterceptor(redisTemplate))
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/**/inner/**")
                .excludePathPatterns("/api/user/weixin/wxLogin/*");
        super.addInterceptors(registry);
    }
}
