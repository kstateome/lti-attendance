package edu.ksu.canvas.aviation.util;

import edu.ksu.canvas.aviation.entity.AviationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class DropDownOrganizer {

    /*
     * Puts the selected section first in the list and sorts the rest alphabetically.
     */
    public static List<AviationSection> sortWithSelectedSectionFirst(List<AviationSection> sections, String selectedSectionId) {
        Optional<AviationSection> selectedSection = sections.stream().filter(s -> String.valueOf(s.getCanvasSectionId()).equals(selectedSectionId)).findFirst();
        List<AviationSection> remainingSections = sections.stream()
                .filter(s -> !String.valueOf(s.getCanvasSectionId()).equals(selectedSectionId))
                .sorted((s1, s2) -> s1.getName().compareTo(s2.getName()))
                .collect(Collectors.toList());
        
        List<AviationSection> organizedDropDown = new ArrayList<>();
        selectedSection.ifPresent(organizedDropDown::add);
        organizedDropDown.addAll(remainingSections);
        
        return organizedDropDown;
    }

}
