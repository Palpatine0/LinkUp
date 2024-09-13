package com.enchanted.config;

import com.enchanted.interceptor.SysInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfigurer implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public SysInterceptor sysInterceptor() {
        return new SysInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] patterns = new String[]{
                "/error",
                "/**",
        };
        registry.addInterceptor(sysInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(patterns);
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // >dev
        registry.addResourceHandler("/image/qrcode/**").addResourceLocations("file:/Users/sheevpalpatine/Desktop/Work/WBQ/Projects/TasteDash/tastedash-server/img/qrcode/");
        registry.addResourceHandler("/image/dish/**").addResourceLocations("file:/Users/sheevpalpatine/Desktop/Work/WBQ/Projects/TasteDash/tastedash-server/img/dish/");
        // >prd
//        registry.addResourceHandler("/image/qrcode/**").addResourceLocations("file:/opt/projects/TasteDash/tastedash-server/img/qrcode/");
//        registry.addResourceHandler("/image/dish/**").addResourceLocations("file:/opt/projects/TasteDash/tastedash-server/img/dish/");
    }


}
