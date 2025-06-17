package com.czw.question.config;

import com.czw.question.interceptor.JwtTokenStudentInterceptor;
import com.czw.question.interceptor.JwtTokenTeacherInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                //是否发送Cookie
                .allowCredentials(true)
                //放行哪些原始域
                .allowedOriginPatterns("*")
                .allowedMethods(new String[]{"GET", "POST", "PUT", "DELETE"})
                .allowedHeaders("*")
                .exposedHeaders("*");
    }
    @Autowired
    private JwtTokenStudentInterceptor jwtTokenStudentInterceptor;

    @Autowired
    private JwtTokenTeacherInterceptor jwtTokenTeacherInterceptor;


    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenStudentInterceptor)
                .addPathPatterns("/student/**")
                .excludePathPatterns("/login")
                .excludePathPatterns("/register");
        registry.addInterceptor(jwtTokenTeacherInterceptor)
                .addPathPatterns("/teacher/**")
                .excludePathPatterns("/login")
                .excludePathPatterns("/register");
    }
}
