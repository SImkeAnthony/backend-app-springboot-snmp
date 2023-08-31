package be.heh.backendappspringbootsnmp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityWebConfiguration{

    @Value("${react.origin}")
    private String origin;
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/").allowedOrigins("http://"+origin);
                registry.addMapping("/scan").allowedOrigins("http://"+origin);
                registry.addMapping("/").allowedOrigins("https://"+origin);
                registry.addMapping("/scan").allowedOrigins("https://"+origin);
            }
        };
    }
}
