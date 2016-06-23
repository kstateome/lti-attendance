package edu.ksu.canvas.attendance.util;

import edu.ksu.canvas.attendance.entity.AttendanceSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class DropDownOrganizer {


    private DropDownOrganizer() {
        // This is a utility class, so it doesn't need a public constructor
    }


    /**
     * Puts the selected section first in the list and sorts the rest alphabetically.
     */
    public static List<AttendanceSection> sortWithSelectedSectionFirst(List<AttendanceSection> sections, String selectedSectionId) {
        Optional<AttendanceSection> selectedSection = sections.stream().filter(s -> String.valueOf(s.getCanvasSectionId()).equals(selectedSectionId)).findFirst();
        List<AttendanceSection> remainingSections = sections.stream()
                .filter(s -> !String.valueOf(s.getCanvasSectionId()).equals(selectedSectionId))
                .sorted((s1, s2) -> s1.getName().compareTo(s2.getName()))
                .collect(Collectors.toList());
        
        List<AttendanceSection> organizedDropDown = new ArrayList<>();
        selectedSection.ifPresent(organizedDropDown::add);
        organizedDropDown.addAll(remainingSections);
        
        return organizedDropDown;
    }

}
