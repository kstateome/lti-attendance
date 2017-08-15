package edu.ksu.canvas.attendance.controller;


import edu.ksu.lti.launch.exception.NoLtiSessionException;
import org.apache.commons.validator.routines.LongValidator;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Scope("session")
@RequestMapping("/help")
public class AttendanceHelpController extends AttendanceBaseController{


    @RequestMapping()
    public ModelAndView attendanceHelp() throws NoLtiSessionException {
        return getHelpPage(null);
    }

    @RequestMapping("/{sectionId}")
    public ModelAndView getHelpPage(@PathVariable String sectionId) throws NoLtiSessionException {
        ensureCanvasApiTokenPresent();
        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);

        ModelAndView page = new ModelAndView("help");
        page.addObject("selectedSectionId", validatedSectionId);
        return page;
    }

}
