package edu.ksu.canvas.aviation.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import javax.transaction.Transactional;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ksu.canvas.aviation.config.TestDatabaseConfig;
import edu.ksu.canvas.aviation.config.TestSpringMVCConfig;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.lti.LtiLaunch;
import edu.ksu.lti.model.LtiSession;


@Transactional
@ActiveProfiles("test")
@WebAppConfiguration
@ContextConfiguration(classes = {TestDatabaseConfig.class, TestSpringMVCConfig.class})
public class BaseControllerITest {

    
    protected MockMvc mockMvc;
    
    @Autowired
    protected WebApplicationContext webApplicationContext;
    
    @Autowired
    protected LtiLaunch mockLtiLaunch;
    
    protected LtiSession mockLtiSession;
    
    
    @Before
    public void setup() throws NoLtiSessionException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        mockLtiSession = mock(LtiSession.class);
        
        reset(mockLtiLaunch);
        when(mockLtiLaunch.getLtiSession()).thenReturn(mockLtiSession);
        when(mockLtiSession.getEid()).thenReturn("someEid");
    }
    
}
