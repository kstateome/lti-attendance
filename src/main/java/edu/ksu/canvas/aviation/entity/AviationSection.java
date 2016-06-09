package edu.ksu.canvas.aviation.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "aviation_section")
public class AviationSection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "section_id")
    private Long sectionId; //aviation project's local section Id

    // Canvas has the authoritative data.
    @Column(name = "canvas_course_id", nullable = false)
    private Long canvasCourseId;

    @Column(name = "canvas_section_id", nullable = false)
    private Long canvasSectionId;

    // Canvas has the authoritative data.
    @Column(name = "section_name")
    private String name;


    public AviationSection() {

    }


    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    public Long getCanvasCourseId() {
        return canvasCourseId;
    }

    public void setCanvasCourseId(Long canvasCourseId) {
        this.canvasCourseId = canvasCourseId;
    }

    public Long getCanvasSectionId() {
        return canvasSectionId;
    }

    public void setCanvasSectionId(Long canvasSectionId) {
        this.canvasSectionId = canvasSectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "AviationSection [sectionId=" + sectionId + ", canvasCourseId=" + canvasCourseId + ", canvasSectionId="
                + canvasSectionId + ", name=" + name + "]";
    }

}
