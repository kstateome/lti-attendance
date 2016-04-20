package edu.ksu.canvas.aviation.form;

import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import edu.ksu.canvas.model.User;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

/**
 * Created by allanjay808
 */
public class RosterForm {
    private HashMap<Section, List<Enrollment>> enrollments = new HashMap<>();

    public void setEnrollments(Section section, List<Enrollment> students) {
        enrollments.put(section, students);
    }

    public HashMap<Section, List<Enrollment>> getEnrollments() {
        return enrollments;
    }

}
