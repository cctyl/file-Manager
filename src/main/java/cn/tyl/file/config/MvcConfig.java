package cn.tyl.file.config;


import cn.tyl.file.intercepter.LoginHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Bean
    public LoginHandlerInterceptor loginHandlerInterceptor(){
        return  new LoginHandlerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加自己的登录拦截器,/**表示所有请求
        //当然，要排除登录页面和提交表单的请求
        //静态资源：springboot已经做好了静态资源映射，不用我们处理
        registry.addInterceptor( loginHandlerInterceptor() ).addPathPatterns("/**")
                .excludePathPatterns("/index.html","/","/login","/swagger*",
                        "/actuator/**","/swagger*/**","/webjars/**","/error","/*.html","/**/*.html","/**/*.css","/**/*.js","/**/*.png"
                        ,"/**/*.jpg"
                        ,"/icon/**"
                        );
    }

}
