package uk.gov.companieshouse.web.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.web.lfp.interceptor.UserDetailsInterceptor;


@SpringBootApplication
public class LFPWebApplication implements WebMvcConfigurer {

    public static final String APPLICATION_NAME_SPACE = "lfp-pay-web";

    private UserDetailsInterceptor userDetailsInterceptor;

    @Autowired
    public LFPWebApplication(UserDetailsInterceptor userDetailsInterceptor) {
        this.userDetailsInterceptor = userDetailsInterceptor;
    }

    public static void main(String[] args) {
        SpringApplication.run(LFPWebApplication.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userDetailsInterceptor);
    }
}