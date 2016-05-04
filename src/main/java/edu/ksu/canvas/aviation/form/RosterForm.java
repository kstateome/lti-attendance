package edu.ksu.canvas.aviation.form;

import edu.ksu.canvas.aviation.model.Attendance;
import edu.ksu.canvas.aviation.model.SectionInfo;
import edu.ksu.canvas.model.Enrollment;
import edu.ksu.canvas.model.Section;
import edu.ksu.canvas.model.User;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

public class RosterForm {

    private List<SectionInfo> sectionInfoList;

    private long sectionId;

    public List<SectionInfo> getSectionInfoList() {
        return sectionInfoList;
    }

    public void setSectionInfoList(List<SectionInfo> sectionInfoList) {
        this.sectionInfoList = sectionInfoList;
    }

    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(long sectionId) {
        this.sectionId = sectionId;
    }
}
