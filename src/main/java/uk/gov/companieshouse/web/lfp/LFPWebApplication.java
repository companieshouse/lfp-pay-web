package uk.gov.companieshouse.web.lfp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.web.lfp.interceptor.UserDetailsInterceptor;
import uk.gov.companieshouse.web.lfp.interceptor.LoggingInterceptor;


@SpringBootApplication
public class LFPWebApplication implements WebMvcConfigurer {

    public static final String APPLICATION_NAME_SPACE = "lfp-pay-web";

    private UserDetailsInterceptor userDetailsInterceptor;
    private LoggingInterceptor loggingInterceptor;

    @Autowired
    public LFPWebApplication(UserDetailsInterceptor userDetailsInterceptor,
                                LoggingInterceptor loggingInterceptor) {
        this.userDetailsInterceptor = userDetailsInterceptor;
        this.loggingInterceptor = loggingInterceptor;
    }

    public static void main(String[] args) {
        SpringApplication.run(LFPWebApplication.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
        registry.addInterceptor(userDetailsInterceptor)
                .excludePathPatterns("/late-filing-penalty")
                .excludePathPatterns("/late-filing-penalty/accessibility-statement");
    }
}