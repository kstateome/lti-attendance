package edu.ksu.canvas.attendance.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * Mitigate CVE-2022-22965 (Spring4Shell) by blocking dangerous data-binding field patterns.
 */
@ControllerAdvice
public class Spring4ShellMitigationController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(
                "class.*",
                "*.class.*",
                "classLoader.*",
                "*.classLoader.*",
                "*.request.*",
                "*.session.*",
                "*.application.*",
                "*[*#*]*", 
                "*[T(*]*"
        );
    }
}
