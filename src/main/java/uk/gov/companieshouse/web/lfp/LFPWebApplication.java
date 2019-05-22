package uk.gov.companieshouse.web.lfp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
public class LFPWebApplication implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(LFPWebApplication.class, args);
    }
}
