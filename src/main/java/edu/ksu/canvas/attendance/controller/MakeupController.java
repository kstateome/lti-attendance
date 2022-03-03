package edu.ksu.canvas.attendance.controller;

import edu.ksu.canvas.attendance.entity.AttendanceSection;
import edu.ksu.canvas.attendance.entity.AttendanceStudent;
import edu.ksu.canvas.attendance.form.MakeupForm;
import edu.ksu.canvas.attendance.form.MakeupValidator;
import edu.ksu.canvas.attendance.services.AttendanceStudentService;
import edu.ksu.canvas.attendance.services.MakeupService;
import edu.ksu.lti.launch.exception.NoLtiSessionException;
import org.apache.commons.validator.routines.LongValidator;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
@Scope("session")
@RequestMapping("/studentMakeup")
public class MakeupController extends AttendanceBaseController {

    private static final Logger LOG = LogManager.getLogger(MakeupController.class);

    @Autowired
    private MakeupService makeupService;

    @Autowired
    private AttendanceStudentService studentService;

    @Autowired
    private MakeupValidator validator;


    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }


    @RequestMapping("/{sectionId}/{studentId}")
    public ModelAndView studentMakeup(@PathVariable String sectionId, @PathVariable String studentId) throws NoLtiSessionException {
        LOG.info("eid: " + canvasService.getEid() + " is viewing makeup data");

        return studentMakeup(sectionId, studentId, false);
    }

    private ModelAndView studentMakeup(String sectionId, String studentId, boolean addEmptyEntry) throws NoLtiSessionException {
        Long validatedSectionId = LongValidator.getInstance().validate(sectionId);
        AttendanceSection selectedSection = validatedSectionId == null ? null : getSelectedSection(validatedSectionId);
        if(validatedSectionId == null || selectedSection == null) {
            return new ModelAndView("forward:roster");
        }

        Long validatedStudentId = LongValidator.getInstance().validate(studentId);
        AttendanceStudent selectedStudent = validatedStudentId == null ? null : studentService.getStudent(validatedStudentId);
        if(validatedStudentId == null || selectedStudent == null) {
            return new ModelAndView("forward:roster/"+validatedSectionId);
        }

        AttendanceStudent student = studentService.getStudent(new Long(studentId));
        MakeupForm makeupForm = makeupService.createMakeupForm(Long.valueOf(studentId), Long.valueOf(sectionId), addEmptyEntry);

        ModelAndView page = new ModelAndView("studentMakeup");
        page.addObject("sectionId", sectionId);
        page.addObject("student", student);
        page.addObject("makeupForm", makeupForm);

        return page;
    }

    @RequestMapping(value = "/save", params = "saveMakeup", method = RequestMethod.POST)
    public ModelAndView saveMakeup(@ModelAttribute MakeupForm makeupForm, BindingResult bindingResult) throws NoLtiSessionException {
        LOG.info("eid: " + canvasService.getEid() + " is saving makeup data.");
        validator.validate(makeupForm, bindingResult);

        boolean allUnsavedAndToBeDeleted = false;
        if (makeupForm.getEntries() != null) {
            long count = makeupForm.getEntries().stream()
                    .filter(entry -> entry.getMakeupId() == null && entry.isToBeDeletedFlag())
                    .count();
            allUnsavedAndToBeDeleted = makeupForm.getEntries().size() == count;
        }

        if (bindingResult.hasErrors()) {
            LOG.debug("There were user input errors saving the Makeup form" + bindingResult.getAllErrors());
            String errorMessage = "Please correct user input and try saving again.";

            ModelAndView page = new ModelAndView("studentMakeup");
            AttendanceStudent student = studentService.getStudent(makeupForm.getStudentId());
            page.addObject("sectionId", String.valueOf(makeupForm.getSectionId()));
            page.addObject("student", student);
            page.addObject("makeupForm", makeupForm);
            page.addObject("error", errorMessage);

            return page;
        } else {
            makeupService.save(makeupForm);
        }

        ModelAndView page = studentMakeup(String.valueOf(makeupForm.getSectionId()), String.valueOf(makeupForm.getStudentId()), false);
        if (makeupForm.getEntries() == null || allUnsavedAndToBeDeleted) {
            page.addObject("nullEntry", true);
        } else {
            page.addObject("updateSuccessful", true);
        }
        return page;

    }

}
