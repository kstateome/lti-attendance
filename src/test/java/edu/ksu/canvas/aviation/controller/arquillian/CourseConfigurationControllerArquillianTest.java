package edu.ksu.canvas.aviation.controller.arquillian;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.warp.WarpTest;
import org.junit.Test;
import org.junit.runner.RunWith;


@WarpTest
@RunAsClient
@RunWith(Arquillian.class)
public class CourseConfigurationControllerArquillianTest extends BaseArquillianTest{

    @Test
    public void shouldBeAbleToSaveConfiguration() throws Exception {
        //TODO: actually write a test for this 
    }
    
}
