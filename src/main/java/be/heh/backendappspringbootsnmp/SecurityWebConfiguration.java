package be.heh.backendappspringbootsnmp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNullApi;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.core.env.Environment;

@Configuration
public class SecurityWebConfiguration {

    @Autowired
    private Environment env;
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String origin = env.getProperty("react.origin");
                registry.addMapping("/**")
                        .allowedOrigins("http://172.168.8.252:3000")
                        .allowedOrigins("http://snmp-nms:3000")
                        .allowedOrigins("http://"+origin)
                        .allowedOrigins("https://"+origin)
                        .allowedHeaders("Access-Control-Allow-Origin")
                        .exposedHeaders("Access-Control-Allow-origin")
                        .allowedMethods("GET","POST")
                        .allowCredentials(true);
            }
        };
    }
}
