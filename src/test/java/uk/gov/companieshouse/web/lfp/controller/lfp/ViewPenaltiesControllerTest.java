package uk.gov.companieshouse.web.lfp.controller.lfp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import uk.gov.companieshouse.api.model.latefilingpenalty.LFPItems;
import uk.gov.companieshouse.api.model.latefilingpenalty.LateFilingPenalty;
import uk.gov.companieshouse.web.lfp.exception.ServiceException;
import uk.gov.companieshouse.web.lfp.service.lfp.ViewPenaltiesService;
import uk.gov.companieshouse.web.lfp.service.navigation.NavigatorService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ViewPenaltiesControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ViewPenaltiesService mockViewPenaltiesService;

    @Mock
    private NavigatorService mockNavigatorService;

    @Mock
    private LateFilingPenalty lateFilingPenalty;

    @InjectMocks
    private ViewPenaltiesController controller;

    @BeforeEach
    private void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private static final String COMPANY_NUMBER = "12345678";

    private static final String VIEW_PENALTIES_PATH = "/lfp/view-penalties/" + COMPANY_NUMBER;

    private static final String ENTER_LFP_DETAILS_VIEW = "lfp/viewPenalties";
    private static final String ERROR_VIEW = "error";

    private static final String OUTSTANDING_MODEL_ATTR = "outstanding";
    private static final String MADE_UP_DATE_MODEL_ATTR = "madeUpDate";
    private static final String DUE_DATE_MODEL_ATTR = "dueDate";
    private static final String COMPANY_NAME_MODEL_ATTR = "companyName";

    private static final String MOCK_CONTROLLER_PATH = UrlBasedViewResolver.REDIRECT_URL_PREFIX + "mockControllerPath";

    @Test
    @DisplayName("Get View Penalties - success path")
    void getRequestSuccess() throws Exception {

        when(mockNavigatorService.getPreviousControllerPath(any(), any())).thenReturn(MOCK_CONTROLLER_PATH);
        when(mockViewPenaltiesService.getLateFilingPenalty(COMPANY_NUMBER)).thenReturn(lateFilingPenalty);
        when(lateFilingPenalty.getTotalResults()).thenReturn(1);
        when(lateFilingPenalty.getItems()).thenReturn(returnValidLfpItems());

        this.mockMvc.perform(get(VIEW_PENALTIES_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(ENTER_LFP_DETAILS_VIEW))
                .andExpect(model().attributeExists(OUTSTANDING_MODEL_ATTR))
                .andExpect(model().attributeExists(MADE_UP_DATE_MODEL_ATTR))
                .andExpect(model().attributeExists(DUE_DATE_MODEL_ATTR))
                .andExpect(model().attributeExists(COMPANY_NAME_MODEL_ATTR));
    }

    @Test
    @DisplayName("Get View Penalties - Throws Exception")
    void getRequestThrowsException() throws Exception {

        when(mockNavigatorService.getPreviousControllerPath(any(), any())).thenReturn(MOCK_CONTROLLER_PATH);
        when(mockViewPenaltiesService.getLateFilingPenalty(COMPANY_NUMBER)).thenThrow(ServiceException.class);

        this.mockMvc.perform(get(VIEW_PENALTIES_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(ERROR_VIEW));
    }

    //TODO: - Add additional unit tests when filter is applied to show error screens.

    List<LFPItems> returnValidLfpItems() {
        LFPItems lfpItems = new LFPItems();
        lfpItems.setMadeUpDate("2018-12-12");
        lfpItems.setDueDate("2020-01-01");
        lfpItems.setOutstanding(1500);
        return Arrays.asList(lfpItems);
    }
}
