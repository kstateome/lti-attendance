package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.exception.MissingSisIdException;
import edu.ksu.canvas.exception.InvalidOauthTokenException;
import edu.ksu.lti.launch.exception.OauthTokenRequiredException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

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

    @ExceptionHandler(InvalidOauthTokenException.class)
    public String initiateOauthToken(InvalidOauthTokenException e) {
        return "redirect:/beginOauth";
    }

    @ExceptionHandler({MissingSisIdException.class})
    public ModelAndView handleMissingSisIdException(MissingSisIdException exception) {
        ModelAndView page;
        if (exception.isAuthority()) {
            page = new ModelAndView("instructorSyncFailed");
        }
        else {
            page = new ModelAndView("studentSyncFailed");
        }
        page.addObject("exception", exception);
        return page;
    }
}
