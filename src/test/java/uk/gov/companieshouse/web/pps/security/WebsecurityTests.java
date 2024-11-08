package uk.gov.companieshouse.web.pps.security;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebsecurityTests {

    @Mock
    private HttpSecurity httpSecurity;

    @InjectMocks
    private WebSecurity webSecurity;

    @Test
    @DisplayName(" apply security filter to /late-filing-penalty")
    void temporaryStartPageSecurityFilterChainTest() throws Exception {
       when(httpSecurity.securityMatcher("/late-filing-penalty")).thenReturn(httpSecurity);
       assertEquals(webSecurity.temporaryStartPageSecurityFilterChain(httpSecurity), httpSecurity.build());
    }
    @Test
    @DisplayName(" apply security filter to /late-filing-penalty/accessibility-statement")
    void accessibilityStatementPageSecurityConfigTest() throws Exception {
        when(httpSecurity.securityMatcher("/late-filing-penalty/accessibility-statement")).thenReturn(httpSecurity);
        assertEquals(webSecurity.accessibilityStatementPageSecurityConfig(httpSecurity), httpSecurity.build());
    }

    @Test
    @DisplayName(" apply security filter to /late-filing-penalty/healthcheck")
    void healthcheckSecurityFilterChainTest() throws Exception {
        when(httpSecurity.securityMatcher("/late-filing-penalty/healthcheck")).thenReturn(httpSecurity);
        assertEquals(webSecurity.healthcheckSecurityFilterChain(httpSecurity), httpSecurity.build());
    }

}
