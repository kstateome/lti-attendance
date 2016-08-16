package edu.ksu.canvas.attendance.controller;

import edu.ksu.lti.launch.exception.OauthTokenRequiredException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Created by david on 8/11/16.
 */
@Controller
@ControllerAdvice("edu.ksu.canvas.attendance")
public class ExceptionController {

    @ExceptionHandler(OauthTokenRequiredException.class)
    public String initiateOauthToken(OauthTokenRequiredException e) {
        return "redirect:/beginOauth";
    }
}
