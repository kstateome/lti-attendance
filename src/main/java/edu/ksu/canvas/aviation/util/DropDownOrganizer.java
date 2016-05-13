package edu.ksu.canvas.aviation.util;

import edu.ksu.canvas.model.Section;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DropDownOrganizer {

    public static List<Section> sortWithSelectedSectionFirst(List<Section> sections, String selectedSectionId) {
        Optional<Section> selectedSection = sections.stream().filter(s -> String.valueOf(s.getId()).equals(selectedSectionId)).findFirst();
        List<Section> remainingSections = sections.stream()
                .filter(s -> !String.valueOf(s.getId()).equals(selectedSectionId))
                .sorted((s1, s2) -> s1.getName().compareTo(s2.getName()))
                .collect(Collectors.toList());
        List<Section> organizedDropDown = new ArrayList<>();
        selectedSection.ifPresent(organizedDropDown::add);
        organizedDropDown.addAll(remainingSections);
        return organizedDropDown;
    }

}
