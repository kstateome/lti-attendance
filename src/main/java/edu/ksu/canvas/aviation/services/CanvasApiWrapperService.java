package edu.ksu.canvas.aviation.services;

import edu.ksu.canvas.CanvasApiFactory;
import edu.ksu.canvas.interfaces.CourseReader;
import edu.ksu.canvas.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class CanvasApiWrapperService {

    @Autowired
    private CanvasApiFactory canvasApiFactory;



    public String getCourseName(String canvasCourseId, String oAuthToken) throws IOException {
        Optional<Course> courseOptional = getCourseOptional(canvasCourseId, oAuthToken);
        return courseOptional.isPresent() ? courseOptional.get().getName() : null;
    }


    private Optional<Course> getCourseOptional(String canvasCourseId, String oAuthToken) throws IOException {
        CourseReader reader = canvasApiFactory.getReader(CourseReader.class, oAuthToken);
        return reader.getSingleCourse(canvasCourseId, Collections.emptyList());
    }
}
