package uk.gov.companieshouse.web.lfp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HealthcheckControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private HealthcheckController controller;

    private static final String HEALTHCHECK_PATH = "/late-filing-penalty/healthcheck";

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Get DCA Required - success path")
    void getRequestSuccess() throws Exception {

        this.mockMvc.perform(get(HEALTHCHECK_PATH))
                .andExpect(status().isOk());

    }
}
