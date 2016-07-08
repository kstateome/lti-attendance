package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.config.TestDatabaseConfig;
import edu.ksu.canvas.attendance.config.TestSpringMVCConfig;
import edu.ksu.canvas.entity.lti.OauthToken;
import edu.ksu.canvas.error.NoLtiSessionException;
import edu.ksu.lti.LtiLaunch;
import edu.ksu.lti.model.LtiSession;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static org.mockito.Mockito.*;


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
        when(mockLtiSession.getCanvasOauthToken()).thenReturn(mock(OauthToken.class));
    }
    
}
